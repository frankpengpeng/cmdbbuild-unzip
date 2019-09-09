/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import java.util.Map;
import org.cmdbuild.email.EmailSysTemplateRepository;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class EmailSysTemplateRepositoryImpl implements EmailSysTemplateRepository {

    private final Map<String, EmailTemplate> sysTemplates;

    public EmailSysTemplateRepositoryImpl() {
        sysTemplates = uniqueIndex(list(
                EmailTemplateImpl.builder().withContentType("application/octet-stream").withName("cm_send_to_current_user").withTo("[#ftl]${cmdb.currentUser.email}").build()
        ), EmailTemplate::getName);
    }

    @Override
    public EmailTemplate getSystemTemplate(String sysTemplateId) {
        return checkNotNull(sysTemplates.get(sysTemplateId), "system template not found for id =< %s >", sysTemplateId);
    }

}
