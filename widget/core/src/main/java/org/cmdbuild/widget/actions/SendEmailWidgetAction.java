/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget.actions;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.beans.EmailImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import org.cmdbuild.widget.WidgetAction;
import org.cmdbuild.widget.model.Widget;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_ACTION_ADVANCE;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_OUTPUT_KEY;
import org.springframework.stereotype.Component;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_TYPE_MANAGE_EMAIL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SendEmailWidgetAction implements WidgetAction {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final EmailService emailService;

	public SendEmailWidgetAction(EmailService emailService) {
		this.emailService = checkNotNull(emailService);
	}

	@Override
	public String getType() {
		return WIDGET_TYPE_MANAGE_EMAIL;
	}

	@Override
	public String getActionId() {
		return WIDGET_ACTION_ADVANCE;
	}

	@Override
	public Map executeAction(Widget widget) { //TODO check this
		Long instanceId = checkNotNull(convert(widget.getContext().get(widget.getNotBlank(WIDGET_OUTPUT_KEY)), Long.class), "reference not found for email widget output = %s", widget);
		emailService.getAllForCard(instanceId).stream().filter((e) -> ES_DRAFT.equals(e.getStatus())).forEach((e) -> {
			emailService.update(EmailImpl.copyOf(e).withStatus(ES_OUTGOING).build());
		});
		return emptyMap();
	}

}
