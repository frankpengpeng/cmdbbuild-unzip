/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v2.serializationhelpers;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.widget.model.WidgetData;
import org.springframework.stereotype.Component;

@Component
public class WsSerializationUtilsv2 {

    private final EmailTemplateService emailTemplateService;
    private final EmailAccountService emailAccountService;

    public WsSerializationUtilsv2(EmailTemplateService emailTemplateService, EmailAccountService emailAccountService) {
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.emailAccountService = checkNotNull(emailAccountService);
    }

    public Object serializeWidget(WidgetData widgetData) {
        return map("_id", widgetData.getId(),
                "label", widgetData.getLabel(),
                "type", "." + capitalize(widgetData.getType()),
                "active", widgetData.isActive(),
                "alwaysenabled", widgetData.isAlwaysEnabled(),
                "required", widgetData.isRequired())
                .with(widgetData.getExtendedData())
                .accept((b) -> {
                    switch (widgetData.getType()) {
                        case "manageEmail": {
                            String templateCode = toStringOrNull(widgetData.getExtendedData().get("Template"));
                            List templates = list();
                            if (isNotBlank(templateCode)) {
                                EmailTemplate template = emailTemplateService.getByName(templateCode);
                                templates.add(map(
                                        "account", template.getAccount() == null ? null : emailAccountService.getAccount(template.getAccount()).getName(),
                                        "bccAddresses", template.getBcc(),
                                        "ccAddresses", template.getCc(),
                                        "condition", widgetData.getExtendedData().get("Condition"),
                                        "content", template.getBody(),
                                        "delay", template.getDelay(),
                                        "fromAddress", template.getFrom(),
                                        "keepSynchronization", template.getKeepSynchronization(),
                                        "promptSynchronization", template.getPromptSynchronization(),
                                        "notifyWith", widgetData.getExtendedData().get("NotifyWith"),
                                        "toAddresses", template.getTo(),
                                        "subject", template.getSubject(),
                                        "noSubjectPrefix", false, //TODO find out what this is
                                        "variables", template.getData(),
                                        "key", "implicitTemplateName" //TODO find out what this is
                                ));
                            }
                            b.put("data", map(
                                    "templates", templates,
                                    "id", widgetData.getId(),
                                    "label", widgetData.getLabel(),
                                    "type", "." + capitalize(widgetData.getType()),
                                    "active", widgetData.isActive(),
                                    "readOnly", false,
                                    "alwaysenabled", widgetData.isAlwaysEnabled(),
                                    "noSubjectPrefix", false, //TODO find out what this is
                                    "required", widgetData.isRequired()
                            )
                            );
                            break;
                        }
                        default: {
                            b.put("data", map(
                                    "id", widgetData.getId(),
                                    "label", widgetData.getLabel(),
                                    "type", "." + capitalize(widgetData.getType()),
                                    "active", widgetData.isActive(),
                                    "readOnly", false,
                                    "alwaysenabled", widgetData.isAlwaysEnabled(),
                                    "required", widgetData.isRequired()
                            ).with(widgetData.getExtendedData()));
                            break;
                        }
                    }
                });
    }
}
