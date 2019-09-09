package org.cmdbuild.scheduler.quartz;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import static java.util.function.Function.identity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.sql.DataSource;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.cluster.ClusterConfiguration;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.config.SchedulerConfiguration;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.requestcontext.RequestContextService;
import org.cmdbuild.scheduler.beans.JobDetail;
import org.cmdbuild.scheduler.utils.JobKeyUtils;
import org.cmdbuild.scheduler.JobSource;
import org.cmdbuild.scheduler.JobUpdatedEvent;

import org.cmdbuild.scheduler.SchedulerService;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Qualifier;
import org.cmdbuild.scheduler.beans.JobTrigger;
import org.quartz.Job;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.cmdbuild.scheduler.beans.ScheduledJobInfo;
import org.cmdbuild.services.PostStartup;
import org.cmdbuild.scheduler.beans.OneTimeTriggerImpl;
import org.cmdbuild.scheduler.beans.CronTrigger;
import org.cmdbuild.scheduler.beans.SchedulerException;
import org.cmdbuild.services.MinionStatus;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.RAW_DATA_SOURCE;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.TriggerBuilder;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.services.PreShutdown;
import org.quartz.JobKey;
import org.cmdbuild.services.MinionComponent;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import static org.cmdbuild.services.MinionStatus.MS_NOTRUNNING;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobBuilder;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleTrigger;
import org.slf4j.MDC;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
@MinionComponent(name = "Quartz Scheduler (core)", configBean = SchedulerConfiguration.class)
public class SchedulerServiceImpl implements SchedulerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClusterConfiguration clusterConfiguration;
    private final SchedulerConfiguration schedulerConfiguration;
    private final RequestContextService requestContextService;
    private final DataSource datasource;
    private final Map<String, JobSource> jobSources;

    private Scheduler scheduler;

    public SchedulerServiceImpl(RequestContextService requestContextService, List<JobSource> jobSources, ClusterConfiguration clusterConfiguration, SchedulerConfiguration schedulerConfiguration, @Qualifier(RAW_DATA_SOURCE) DataSource datasource) {
        this.schedulerConfiguration = checkNotNull(schedulerConfiguration);
        this.datasource = checkNotNull(datasource);
        this.requestContextService = checkNotNull(requestContextService);
        this.clusterConfiguration = checkNotNull(clusterConfiguration);
        this.jobSources = uniqueIndex(jobSources, JobSource::getJobSourceName);
        jobSources.forEach((s) -> s.register(new Object() {
            @Subscribe
            public void handleJobUpdateEvent(JobUpdatedEvent event) {
                try {
                    reconfigureJobs(event);
                } catch (Exception ex) {
                    logger.error("error processing job update event = {}", event, ex);
                }
            }

        }));
    }

    public MinionStatus getServiceStatus() {
        if (isRunning()) {
            return MS_READY;
        } else if (!schedulerConfiguration.isEnabled()) {
            return MS_DISABLED;
        } else {
            return MS_NOTRUNNING;
        }
    }

    @PostStartup
    public void startup() {
        if (schedulerConfiguration.isEnabled()) {
            doStartService();
        } else {
            logger.info("quartz scheduler not enabled");
        }
    }

    @PreShutdown
    public void stopService() {
        destroySchedulerSafe();
    }

    @Override
    public List<ScheduledJobInfo> getConfiguredJobs() {
        List<ScheduledJobInfo> list = list();
        if (isRunning()) {
            try {
                scheduler.getJobKeys(GroupMatcher.anyJobGroup()).forEach((jobKey) -> {
                    try {
                        String name = jobKey.getName(),
                                source = quartzGroupToJobSourceName(jobKey.getGroup());
                        String triggers = Joiner.on(",").join(scheduler.getTriggersOfJob(jobKey).stream().map((trigger) -> {
                            if (trigger instanceof org.quartz.CronTrigger) {
                                return ((org.quartz.CronTrigger) trigger).getCronExpression();
                            } else {
                                return trigger.toString();
                            }
                        }).collect(toList()));
                        list.add(new ScheduledJobInfoImpl(source, name, triggers));
                    } catch (Exception e) {
                        throw new SchedulerException(e);
                    }

                });
            } catch (Exception e) {
                throw new SchedulerException(e);
            }
        }
        return list;
    }

    @Override
    public void triggerJobImmediately(String group, String name) {
        checkArgument(isRunning(), "scheduler is not running: cannot trigger job");
        try {
            JobKey jobKey = new JobKey(name, jobSourceNameToQuartzGroup(group));
            scheduler.triggerJob(jobKey);
        } catch (Exception ex) {
            throw new SchedulerException(ex, "error triggering job = %s", JobKeyUtils.serializeJobKey(group, name));
        }
    }

    @ConfigListener(SchedulerConfiguration.class)
    public synchronized void reloadScheduler() {
        logger.info("reloading quartz scheduler");
        try {
            destroySchedulerSafe();
            if (schedulerConfiguration.isEnabled()) {
                doStartService();
            }
        } catch (Exception ex) {
            throw new SchedulerException(ex);
        }
    }

    private synchronized void doStartService() {
        if (isRunning()) {
            logger.warn("already running - ignore start request");
        } else {
            logger.info("start quartz service");
            try {
                startScheduler();
            } catch (Exception e) {
                throw new SchedulerException(e);
            }
        }
    }

    private boolean isRunning() {
        try {
            return scheduler != null && scheduler.isStarted() && !scheduler.isShutdown();
        } catch (Exception ex) {
            logger.warn("error checking scheduler status", ex);
            return false;
        }
    }

    private synchronized void destroySchedulerSafe() {
        if (scheduler != null) {
            logger.info("stop quartz scheduler service");
            try {
                if (!scheduler.isShutdown()) {
                    scheduler.shutdown(true);
                }
            } catch (Exception ex) {
                logger.warn(marker(), "error stopping scheduler", ex);
            }
            scheduler = null;
        }
    }

    private synchronized void reconfigureJobs(JobUpdatedEvent event) throws Exception {
        if (isRunning()) {//TODO test this method
            scheduler.standby();
            doReconfigureJobs();
            scheduler.start();
        }
    }

    private synchronized void startScheduler() throws Exception {
        logger.info("preparing quartz scheduler with job sources = {}", Joiner.on(",").join(jobSources.keySet()));
        checkArgument(scheduler == null);
        QuartzConnectionProvider.setDataSource(datasource);

        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();

        Properties quartzProperties = map(schedulerConfiguration.getQuartzProperties()).with(
                "org.quartz.dataSource.CMDBuildDatasource.connectionProvider.class", QuartzConnectionProvider.class.getName(), //TODO please springify this
                "org.quartz.scheduler.instanceId", clusterConfiguration.getClusterNodeId()
        ).toProperties();
        schedulerFactory.initialize(quartzProperties);

        scheduler = schedulerFactory.getScheduler();
        scheduler.setJobFactory(new MyJobFactory());

        doReconfigureJobs();

        logger.info("start scheduler");
        scheduler.start();
    }

    private synchronized void doReconfigureJobs() throws Exception {
        Map<String, Pair<org.quartz.JobDetail, Trigger>> jobsToLoad = jobSources.values().stream().flatMap((js) -> js.getJobs().stream().map(j -> {

            try {
                JobTrigger trigger = j.getTrigger();
                JobDetail job = j.getJob();
                return Pair.of(toQuartzJobDetail(js, job), toQuartzTrigger(trigger));
            } catch (Exception ex) {
                logger.error("error preparing job = {}", j, ex);
                return null;
            }

        })).filter(not(isNull())).collect(toMap(p -> serializeJobKey(p.getLeft().getKey()), identity()));
        logger.info("check status of {} configured jobs", jobsToLoad.size());
        Set<String> currentJobs = scheduler.getJobKeys(GroupMatcher.anyJobGroup()).stream().map(SchedulerServiceImpl::serializeJobKey).collect(toSet());
        Set<String> jobToAdd = set(jobsToLoad.keySet()).without(currentJobs),
                jobsToRemove = set(currentJobs).without(jobsToLoad.keySet()),
                jobsToCheck = Sets.intersection(currentJobs, jobsToLoad.keySet());
        jobsToCheck.forEach(rethrowConsumer(j -> {
            Trigger currentTrigger = getOnlyElement(scheduler.getTriggersOfJob(deserializeJobKey(j)));
            Trigger newTrigger = jobsToLoad.get(j).getRight();
            if (!equal(quartzTriggerToString(newTrigger), quartzTriggerToString(currentTrigger))) {
                jobsToRemove.add(j);
                jobToAdd.add(j);
            }
        }));
        jobsToRemove.stream().map(SchedulerServiceImpl::deserializeJobKey).forEach(this::deleteJobSafe);
        jobToAdd.stream().map(jobsToLoad::get).forEach((job) -> {
            try {
                createQuartzJob(job.getLeft(), job.getRight());
            } catch (Exception ex) {
                logger.error("error creating job = {}", job, ex);
            }
        });
    }

    private void deleteJobSafe(JobKey jobKey) {
        logger.info("delete job = {}", jobKey);
        try {
            scheduler.deleteJob(jobKey);
        } catch (Exception ex) {
            logger.error("error deleting job = {}", jobKey, ex);
        }
    }

    private void createQuartzJob(org.quartz.JobDetail jobDetail, Trigger trigger) {
        logger.info("create job = {}", jobDetail.getKey());
        try {
            scheduler.deleteJob(jobDetail.getKey());
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            throw new SchedulerException(e);
        }
    }

    private org.quartz.JobDetail toQuartzJobDetail(JobSource jobSource, JobDetail jobDetail) {
        return JobBuilder.newJob(MyQuartzJob.class).withIdentity(jobDetail.getKey(), jobSourceNameToQuartzGroup(jobSource.getJobSourceName())).build();
    }

    private static String jobSourceNameToQuartzGroup(String name) {
        return "cm_" + name;
    }

    private static String quartzGroupToJobSourceName(String group) {
        Matcher matcher = Pattern.compile("cm_(.+)").matcher(group);
        checkArgument(matcher.matches(), "invalid quartz group syntax = %s", group);
        return checkNotBlank(matcher.group(1));
    }

    private static String serializeJobKey(JobKey jobKey) {
        checkArgument(!jobKey.getGroup().contains("."), "unable to serialize job key = %s (invalid group name)", jobKey);
        return format("%s.%s", jobKey.getGroup(), jobKey.getName());
    }

    private static JobKey deserializeJobKey(String string) {
        try {
            Matcher matcher = Pattern.compile("([^.]+)[.](.+)").matcher(string);
            checkArgument(matcher.matches());
            return new JobKey(checkNotBlank(matcher.group(2)), checkNotBlank(matcher.group(1)));
        } catch (Exception ex) {
            throw new SchedulerException(ex, "invalid syntax for job key = %s", string);
        }
    }

    private org.quartz.Trigger toQuartzTrigger(JobTrigger trigger) {
        try {
            checkNotNull(trigger);
            if (trigger instanceof OneTimeTriggerImpl) {
                return TriggerBuilder.newTrigger().withIdentity(format("onetimetrigger_%s", randomId())).withSchedule(simpleSchedule().withRepeatCount(0)).startAt(((OneTimeTriggerImpl) trigger).getDate()).build();
            } else if (trigger instanceof CronTrigger) {
                return TriggerBuilder.newTrigger().withIdentity(format("crontrigger_%s", randomId())).withSchedule(cronSchedule(((CronTrigger) trigger).getCronExpression())).build();
            } else {
                throw new IllegalArgumentException(format("unsupported job trigger = %s (%s)", trigger, trigger.getClass()));
            }
        } catch (Exception ex) {
            throw new SchedulerException(ex, "error creating quartz trigger from jobTrigger = %s", trigger);
        }
    }

    private String quartzTriggerToString(org.quartz.Trigger trigger) {
        if (trigger instanceof org.quartz.CronTrigger) {
            return format("cron:<%s>", ((org.quartz.CronTrigger) trigger).getCronExpression());
        } else if (trigger instanceof SimpleTrigger) {
            return format("simple:%s", ((SimpleTrigger) trigger).getStartTime().getTime());//TODO check this
        } else {
            throw new IllegalArgumentException(format("unsupported trigger = %s (%s)", trigger, getClassOfNullable(trigger).getName()));
        }
    }

    private Consumer<JobExecutionContext> buildJobInner(TriggerFiredBundle bundle) {
        requestContextService.initCurrentRequestContext("job loader");
        try {
            org.quartz.JobDetail jobDetail = bundle.getJobDetail();
            String jobSourceName = quartzGroupToJobSourceName(jobDetail.getKey().getGroup());
            JobSource jobSource = checkNotNull(jobSources.get(jobSourceName), "job source not found for name = %s", jobSourceName);
            String key = jobDetail.getKey().getName();
            if (jobSource.isEnabled()) {
                logger.debug("prepare job run for source = {} job = {}", jobSource, key);
                return (x) -> {

                    MDC.put("cm_type", "job");
                    MDC.put("cm_id", format("job:%s", key));
                    requestContextService.initCurrentRequestContext("job runner");
                    try {
                        jobSource.runJob(key);
                    } catch (Exception ex) {
                        logger.error("error running scheduled job = {}", jobDetail.getKey(), ex);
                    } finally {
                        requestContextService.destroyCurrentRequestContext();
                        MDC.clear();
                    }
                };
            } else {
                logger.debug("skip job run for source = {} job = {} (source is not enabled)", jobSource, key);
                return (x) -> {
                };
            }
        } finally {
            requestContextService.destroyCurrentRequestContext();
        }
    }

    private class MyJobFactory extends SimpleJobFactory implements JobFactory {

        @Override
        public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws org.quartz.SchedulerException {
            MDC.put("cm_type", "job");
            MDC.put("cm_id", "sched");
            try {
                logger.debug("prepare job for firing = {}", bundle);
                Job job = super.newJob(bundle, scheduler);
                checkArgument(job instanceof MyQuartzJob, "found invalid job = %s (unsupported class)", job);
                MyQuartzJob myJob = (MyQuartzJob) job;
                myJob.setInner(buildJobInner(bundle));
                return job;
            } catch (Exception ex) {
                logger.error("error preparing job = {}", bundle, ex);
                throw new org.quartz.SchedulerException(ex);
            } finally {
                MDC.clear();
            }
        }
    }

    @DisallowConcurrentExecution
    public static class MyQuartzJob implements org.quartz.Job {

        private Consumer<JobExecutionContext> inner;

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            checkNotNull(inner, "error: this job was not initialized correctly: inner is null").accept(context);
        }

        public void setInner(Consumer<JobExecutionContext> inner) {
            this.inner = checkNotNull(inner);
        }

    }

}
