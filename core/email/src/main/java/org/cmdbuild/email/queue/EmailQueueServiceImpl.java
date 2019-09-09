/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.queue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import static java.lang.Integer.max;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.List;
import javax.annotation.Nullable;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.EmailService.NewOutgoingEmailEvent;
import static org.cmdbuild.email.EmailStatus.ES_ERROR;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.mta.EmailMtaService;
import org.cmdbuild.jobs.JobExecutorService;
import static org.cmdbuild.jobs.JobExecutorService.JOBUSER_SYSTEM;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.lock.LockService;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.scheduler.JobClusterMode.CM_RUN_ON_SINGLE_NODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.EmailQueueConfiguration;
import org.cmdbuild.config.api.ConfigListener;
import static org.cmdbuild.email.EmailStatus.ES_SKIPPED;
import org.cmdbuild.lock.LockScope;
import org.cmdbuild.lock.LockService.LockResponse;
import org.cmdbuild.services.MinionStatus;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import org.cmdbuild.services.MinionComponent;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import org.cmdbuild.syscommand.SysCommand;
import org.cmdbuild.syscommand.SysCommandBus;
import static org.cmdbuild.utils.date.CmDateUtils.now;

@Component
@MinionComponent(name = "Email Queue", config = "org.cmdbuild.email.queue")//TODO improve namespace const
public class EmailQueueServiceImpl implements EmailQueueService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LockService lockService;
    private final JobExecutorService jobExecutorService;
    private final EmailMtaService mtaService;
    private final EmailService emailService;
    private final EmailQueueConfiguration config;

    public EmailQueueServiceImpl(EmailMtaService mtaService, EmailService emailService, LockService lockService, JobExecutorService jobExecutorService, EmailQueueConfiguration emailConfiguration, SysCommandBus sysCommandBus) {
        this.mtaService = checkNotNull(mtaService);
        this.emailService = checkNotNull(emailService);
        this.lockService = checkNotNull(lockService);
        this.jobExecutorService = checkNotNull(jobExecutorService);
        this.config = checkNotNull(emailConfiguration);
        reloadConfig();
        emailService.getEventBus().register(new Object() {
            @Subscribe
            public void handleNewOutgoingEmailEvent(NewOutgoingEmailEvent event) {
                if (isRunning()) {
                    triggerEmailQueue();//TODO do not hard fail if queue lock is not available
                }
            }
        });
        sysCommandBus.getEventBus().register(new Object() {
            @Subscribe
            public void handleSysCommand(SysCommand command) {
                switch (command.getAction()) {
                    case "email_queue_trigger":
                        logger.info("trigger email queue for sys command");
                        triggerEmailQueue();
                        break;
                    case "email_queue_send_single":
                        long emailId = command.get("_email_id", Long.class);
                        logger.info("trigger email send for sys command, email = {}", emailId);
                        sendSingleEmail(emailId);
                        break;
                }
            }
        });
    }

    @ConfigListener(EmailQueueConfiguration.class)
    public final void reloadConfig() {
        if (isRunning()) {
            triggerEmailQueue();
        }
    }

    public MinionStatus getServiceStatus() {
        if (isRunning()) {
            return MS_READY;
        } else {
            return MS_DISABLED;//TODO handle notrunning
        }
    }

    private boolean isRunning() {
        return config.isQueueProcessingEnabled();
    }

    @Override
    public void triggerEmailQueue() {
        jobExecutorService.executeJobAs(() -> doProcessEmailQueue(), JOBUSER_SYSTEM);
    }

    @Override
    public void sendSingleEmail(long emailId) {
        Email email = emailService.getOne(emailId);
        checkArgument(email.isOutgoing(), "invalid email status");
        jobExecutorService.executeJobAs(() -> doProcessEmailQueue(emailId), JOBUSER_SYSTEM);
    }

    @ScheduledJob(value = "0 */10 * * * ?", clusterMode = CM_RUN_ON_SINGLE_NODE, user = JOBUSER_SYSTEM)//run every 10 minutes
    public void processEmailQueue() {
        if (isRunning()) {
            doProcessEmailQueue();
        } else {
            logger.debug("email queue processing is disabled - skipping");
        }
    }

    private void doProcessEmailQueue() {
        doProcessEmailQueue(null);
    }

    private void doProcessEmailQueue(@Nullable Long singleEmailId) {
        try {
            logger.debug("processing email queue");
            LockResponse lockResponse = aquireEmailQueueLock();
            if (lockResponse.isAquired()) {
                ItemLock lock = lockResponse.aquired();
                try {
                    List<Email> outgoing;
                    if (singleEmailId == null) {
                        outgoing = emailService.getAllForOutgoingProcessing();
                    } else {
                        Email email = emailService.getOneOrNull(singleEmailId);
                        if (email == null || !email.isOutgoing()) {
                            logger.debug("no outgoing email found for id = {}", singleEmailId);
                            outgoing = emptyList();
                        } else {
                            outgoing = singletonList(email);

                        }
                    }
                    if (!outgoing.isEmpty()) {
                        logger.info("processing {} outgoing email", outgoing.size());
                        outgoing.forEach(this::doSendEmailAndHandleErrors);
                    }
                } finally {
                    lockService.releaseLock(lock);
                }
            } else {
                logger.warn("unable to aquire queue lock, skip email queue processing");
            }
        } catch (Exception ex) {
            logger.error("error processing email queue", ex);
        }
    }

    private LockResponse aquireEmailQueueLock() {
        return lockService.aquireLockWaitALittle("org.cmdbuild.email.QUEUE", LockScope.LS_SESSION);//TODO expire this lock eventually
    }

    private void doSendEmailAndHandleErrors(Email email) {
        try {
            if (email.hasDestinationAddress()) {
                doSendEmail(email);
            } else {
                skipEmail(email);
            }
        } catch (Exception ex) {
            logger.error(marker(), "error sending email = {}", email, ex);
            int errors = email.getErrorCount() + 1;
            if (errors >= config.getMaxErrors()) {
                logger.info(marker(), "email = {} failed, setting email status to ERROR", email);
                emailService.update(EmailImpl.copyOf(email).withErrorCount(errors).withSentOrReceivedDate(now()).withStatus(ES_ERROR).build());
            } else {
                long delaySeconds = max(0, config.getMinRetryDelaySeconds());
                for (int i = 1; i < errors; i++) {
                    delaySeconds = (long) delaySeconds * 2;
                }
                delaySeconds = Long.min(delaySeconds, max(0, config.getMaxRetryDelaySeconds()));
                logger.info(marker(), "retrying email = {} after delay = {}", email, toUserDuration(delaySeconds * 1000));
                emailService.update(EmailImpl.copyOf(email).withErrorCount(errors).withDelay(delaySeconds).build());
            }
        }
    }

    private Email doSendEmail(Email email) {
        logger.debug("sending email = {}", email);
        email = mtaService.send(emailService.loadAttachments(email));
        email = emailService.update(email);
        logger.info("sent email = {}", email);
        return email;
    }

    private Email skipEmail(Email email) {
        logger.info("skip email = {}", email);
        return emailService.update(EmailImpl.copyOf(email).withSentOrReceivedDate(now()).withStatus(ES_SKIPPED).build());
    }

}
