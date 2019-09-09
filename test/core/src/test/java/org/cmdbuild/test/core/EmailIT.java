/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import static org.cmdbuild.email.EmailStatus.ES_RECEIVED;
import static org.cmdbuild.email.EmailStatus.ES_SENT;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.workflow.core.fluentapi.WorkflowApiService;
import org.cmdbuild.workflow.type.ReferenceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class EmailIT {

    private final EmailService emailService;
    private final WorkflowApiService workflowApiService;
    private final DaoService dao;

    public EmailIT(EmailService emailService, WorkflowApiService workflowApiService, DaoService dao) {
        this.emailService = checkNotNull(emailService);
        this.workflowApiService = checkNotNull(workflowApiService);
        this.dao = checkNotNull(dao);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testReplyToEmailFuzzyMatching() {
        String baseSubject = format("My Subject %s :)", tuid());
        String toAddress = format("my.address.%s@some.host", tuid());
        Email oldMatchingEmail = emailService.create(EmailImpl.builder().withToAddresses(toAddress).withSubject(baseSubject).withStatus(ES_SENT).withSentOrReceivedDate(now()).build()),
                otherEmail1 = emailService.create(EmailImpl.builder().withToAddresses("other.address@some.host").withSubject(baseSubject).withStatus(ES_SENT).withSentOrReceivedDate(now()).build()),
                otherEmail2 = emailService.create(EmailImpl.builder().withToAddresses(toAddress).withSubject("other subject").withStatus(ES_SENT).withSentOrReceivedDate(now()).build());

        Email receivedEmail = EmailImpl.builder().withFromAddress(format("My Address <%s>", toAddress)).withSubject(format("Re: Fw: %s", baseSubject)).withStatus(ES_RECEIVED).withSentOrReceivedDate(now()).build();

        Email found = emailService.getLastWithReferenceBySenderAndSubjectFuzzyMatchingOrNull(receivedEmail.getSingleFromAddress(), receivedEmail.getSubject());

        assertNotNull(found);
        assertEquals(oldMatchingEmail.getId(), found.getId());
    }

    @Test
    public void testEmailWfApi() {
        Classe classe = dao.createClass(ClassDefinitionImpl.build(tuid("MyClass")));
        Card card = dao.create(CardImpl.buildCard(classe, ATTR_CODE, "myCard"));

        workflowApiService.getWorkflowApi().newMail()
                .withTo("to@host.com")
                .withFrom("from@host.com")
                .withCc("cc@host.com")
                .withBcc("bcc@host.com")
                .withContent("my email content")
                .withSubject("hello")
                .withCard(new ReferenceType(card.getClassName(), card.getId()))
                .send();

        Email email = getOnlyElement(emailService.getAllForCard(card.getId()));

        assertEquals("to@host.com", email.getToAddresses());
        assertEquals("from@host.com", email.getFromAddress());
        assertEquals("cc@host.com", email.getCcAddresses());
        assertEquals("bcc@host.com", email.getBccAddresses());
        assertEquals("my email content", email.getContentPlaintext());
        assertEquals("hello", email.getSubject());
        assertEquals(ES_OUTGOING, email.getStatus());
    }

}
