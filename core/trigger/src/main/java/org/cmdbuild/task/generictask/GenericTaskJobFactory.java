package org.cmdbuild.task.generictask;

import static com.google.common.base.Preconditions.checkNotNull;
//import static org.cmdbuild.scheduler.command.Commands.conditional;
//import org.cmdbuild.task.scheduler.AbstractJobFactory;
//import org.cmdbuild.scheduler.command.Command;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.report.ReportService;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_TWO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.view.DataView;

@Component
//public class GenericTaskJobFactory extends AbstractJobFactory<GenericTask> {
public class GenericTaskJobFactory  {

//	private final EmailAccountService emailAccountFacade;
//	private final EmailTemplateLogic emailTemplateLogic;
	private final ReportService reportLogic;
	private final DataView dataView;
	private final EasytemplateProcessor databaseEngine;
//	private final EmailTemplateSenderFactory emailTemplateSenderFactory;

	public GenericTaskJobFactory(ReportService reportLogic, @Qualifier(SYSTEM_LEVEL_TWO) DataView dataView, EasytemplateProcessor databaseEngine) {
//		this.emailAccountFacade = checkNotNull(emailAccountFacade);
//		this.emailTemplateLogic = checkNotNull(emailTemplateLogic);
		this.reportLogic = checkNotNull(reportLogic);
		this.dataView = checkNotNull(dataView);
		this.databaseEngine = checkNotNull(databaseEngine);
//		this.emailTemplateSenderFactory = checkNotNull(emailTemplateSenderFactory);
	}

//	@Override
//	protected Class<GenericTask> getType() {
//		return GenericTask.class;
//	}
//
//	@Override
//	protected Command command(final GenericTask task) {
//		return conditional(sendEmail(task), (x) -> task.isEmailActive());
//	}

//	private Command sendEmail(final GenericTask task) {
//		throw new UnsupportedOperationException("TODO");
//		return new Command() {
//
//			@Override
//			public void execute() {
//				final EasytemplateProcessorImpl templateResolver = templateResolverOf(task);
//
//				final Supplier<Template> template = memoize(() -> {
//					logger.debug("getting email template for '{}'", task);
//					final String value = task.getEmailTemplate();
//					logger.debug("template name is '{}'", value);
//					return emailTemplateLogic.read(value);
//				});
//				final Supplier<EmailAccount> account = () -> {
//					logger.debug("getting email account for '{}'", task);
//					final Iterable<String> eligibleAccounts = from(
//							asList(template.get().getAccount(), task.getEmailAccount())).filter(String.class);
//					logger.debug("eligible accounts are '{}'", eligibleAccounts);
//					return emailAccountFacade.firstOfOrDefault(eligibleAccounts).get();
//				};
//				final Collection<Supplier<? extends DataHandler>> attachments = new ArrayList<>();
//				if (task.isReportActive()) {
//					attachments.add(new Supplier<DataHandler>() {
//
//						@Override
//						public DataHandler get() {
//							final ReportInfo report = stream(reportLogic.getAll().spliterator(), false) //
//									.filter(input -> input.getCode().equals(task.getReportName())) //
//									.findFirst() //
//									.get();
//							return reportLogic.executeReportAndDownload(report.getId(), reportExtFromString(task.getReportExtension()),
//									resolve((Map) task.getReportParameters()));
//						}
//
//						private Map<String, ? extends Object> resolve(final Map<String, String> input) {
//							return Maps.transformValues(input, (final String input1) -> templateResolver.resolve(input1));
//						}
//
//					});
//				}
//
//				emailTemplateSenderFactory.queued() //
//						.withAccount(account) //
//						.withTemplate(template) //
//						.withAttachments(attachments) //
//						.withReference(task.getId()) //
//						.withTemplateResolver(templateResolver) //
//						.build() //
//						.execute();
//			}
//
//			private EasytemplateProcessorImpl templateResolverOf(final GenericTask task) {
//				final EasytemplateProcessorImpl.EasytemplateProcessorImplBuilder templateResolverBuilder = EasytemplateProcessorImpl
//						.builder() //
//						.withResolver(emptyStringOnNull(nullOnError(EasytemplateCqlResolver.newInstance() //
//								.withDataView(dataView) //
//								.build())), //
//								CQL_PREFIX) //
//						.withResolver(
//								emptyStringOnNull(nullOnError( //
//										(input) -> databaseEngine.resolve(input))), //
//								DB_TEMPLATE);
//				for (final Entry<String, Map<String, String>> element : task.getContext().entrySet()) {
//					templateResolverBuilder.withResolver(emptyStringOnNull(nullOnError(forMap(element.getValue(), null))), //
//							element.getKey());
//				}
//				final EasytemplateProcessorImpl templateResolver = templateResolverBuilder //
//						.build();
//				return templateResolver;
//			}
//
//		};
//	}

//	private Predicate<Void> emailActive(final GenericTask task) {
//		return (final Void input) -> task.isEmailActive();
//	}
}
