package org.cmdbuild.task.wizardconnector;

//import static org.cmdbuild.scheduler.command.Commands.composeOnExeption;
//import static org.cmdbuild.scheduler.command.Commands.conditional;
//import static org.cmdbuild.scheduler.command.Commands.nullCommand;

import org.cmdbuild.common.java.sql.DataSourceHelper;
//import org.cmdbuild.task.scheduler.AbstractJobFactory;
//import org.cmdbuild.scheduler.command.Command;
import org.cmdbuild.legacy.etl.AttributeValueAdapter;

import org.springframework.stereotype.Component;
import org.cmdbuild.dao.view.DataView;

//@Component
//public class ConnectorTaskJobFactory extends AbstractJobFactory<ConnectorTask> {
@Component
public class ConnectorTaskJobFactory  {

	private final DataView dataView;
	private final DataSourceHelper jdbcService;
	private final AttributeValueAdapter attributeValueAdapter;
//	private final EmailAccountFacade emailAccountFacade;
//	private final EmailTemplateLogic emailTemplateLogic;
//	private final EmailTemplateSenderFactory emailTemplateSenderFactory;

	public ConnectorTaskJobFactory(DataView dataView, DataSourceHelper jdbcService, AttributeValueAdapter attributeValueAdapter) {
		this.dataView = dataView;
		this.jdbcService = jdbcService;
		this.attributeValueAdapter = attributeValueAdapter;
//		this.emailAccountFacade = emailAccountFacade;
//		this.emailTemplateLogic = emailTemplateLogic;
//		this.emailTemplateSenderFactory = emailTemplateSenderFactory;
	}

//	@Override
//	protected Class<ConnectorTask> getType() {
//		return ConnectorTask.class;
//	}
//
//	@Override
//	protected Command command(ConnectorTask task) {
//		return composeOnExeption(connector(task), sendEmail(task));
//
//	}
//
//	private ConnectorTaskCommandWrapper connector(ConnectorTask task) {
//		return new ConnectorTaskCommandWrapper(dataView, jdbcService, attributeValueAdapter, task);
//	}
//
//	private Command sendEmail(ConnectorTask task) {
//		Command command;
//		// TODO do it in a better way
//		if (task.isNotificationActive()) {
//			throw new UnsupportedOperationException("TODO");
//			
////			Supplier<Template> emailTemplateSupplier = memoize(() -> {
////				String name = defaultString(task.getNotificationErrorTemplate());
////				return emailTemplateLogic.read(name);
////			});
////			Optional<EmailAccount> account = emailAccountFacade.firstOfOrDefault(asList(emailTemplateSupplier.get()
////					.getAccount(), task.getNotificationAccount()));
////			Supplier<EmailAccount> emailAccountSupplier = account.isPresent() ? ofInstance(account.get()) : null;
////			EmailTemplateSenderFactory.EmailTemplateSender sender = emailTemplateSenderFactory.queued().withAccount(emailAccountSupplier).withTemplate(emailTemplateSupplier).withReference(task.getId()).build();
////			command = sender::execute;
//		} else {
//			command = nullCommand();
//		}
//		return conditional(command, new NotificationEnabled(task));
//	}

}
