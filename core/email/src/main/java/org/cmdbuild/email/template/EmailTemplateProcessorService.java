/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.template;

import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateBindings;
import org.cmdbuild.email.job.MapperConfig;

public interface EmailTemplateProcessorService {

    Email applyEmailTemplate(Email email, EmailTemplate template, Card data);

    Email applyEmailTemplate(Email email, EmailTemplate template);

    String applyEmailTemplateExpr(String expr, EmailTemplate template, Card cardData);

    Email applyEmailTemplate(Email email, EmailTemplate template, Card data, Email receivedEmail);

    Map<String, Object> applyEmailTemplate(Map<String, String> expressions, Email receivedEmail, @Nullable MapperConfig mapperConfig);

    EmailTemplateBindings getEmailTemplateBindings(EmailTemplate template);

    Email createEmailFromTemplate(EmailTemplate template, Map<String, Object> map);

    Email createEmailFromTemplate(EmailTemplate template, Card card);

}
