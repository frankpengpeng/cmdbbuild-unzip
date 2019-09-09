/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.data.filter.CmdbFilter;

public interface EmailTemplateService extends EmailSysTemplateRepository {

    List<EmailTemplate> getAll();

    List<EmailTemplate> getMany(CmdbFilter filter, @Nullable Long offset, @Nullable Long limit);

    EmailTemplate getOne(long id);

    EmailTemplate getByName(String name);

    EmailTemplate createEmailTemplate(EmailTemplate emailTemplate);

    EmailTemplate updateEmailTemplate(EmailTemplate emailTemplate);

    void deleteEmailTemplate(long id);

    EmailTemplateBindings getEmailTemplateBindings(EmailTemplate emailTemplate);

}
