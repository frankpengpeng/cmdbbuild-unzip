/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.workflow.core.fluentapi.WorkflowApiService;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class MailApiIT {

    private final WorkflowApiService apiService;
    private final EmailService emailService;

    public MailApiIT(WorkflowApiService apiService, EmailService emailService) {
        this.apiService = checkNotNull(apiService);
        this.emailService = checkNotNull(emailService);
    }

    @Test
    public void testEmailApi() {
        apiService.getWorkflowApi().newMail().withFrom("cmdbuild@test.it").withTo("cmdbuild@test.it").withSubject("test mail").withContent("This is just a test mail").send();

        int index = emailService.getAllForOutgoingProcessing().size() - 1;
        assertEquals("test mail", emailService.getAllForOutgoingProcessing().get(index).getSubject());
        assertEquals("cmdbuild@test.it", emailService.getAllForOutgoingProcessing().get(index).getFromAddress());
        assertEquals("This is just a test mail", emailService.getAllForOutgoingProcessing().get(index).getContentPlaintext());
        assertEquals(ES_OUTGOING, emailService.getAllForOutgoingProcessing().get(index).getStatus());
    }
}
