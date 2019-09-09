package org.cmdbuild.task.asyncevent;

import static com.google.common.collect.FluentIterable.from;
import static org.cmdbuild.easytemplate.EasytemplateUtils.emptyStringOnNull;
import static org.cmdbuild.easytemplate.EasytemplateUtils.nullOnError;
import static org.cmdbuild.easytemplate.EasytemplateResolverNames.CARD_PREFIX;
import static org.cmdbuild.easytemplate.EasytemplateResolverNames.CQL_PREFIX;
import static org.cmdbuild.easytemplate.EasytemplateResolverNames.DB_TEMPLATE;
import static org.cmdbuild.easytemplate.EasytemplateResolverNames.FUNCTION_PREFIX;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.activation.DataHandler;

import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.task.dao.TaskStore;
//import org.cmdbuild.task.scheduler.AbstractJobFactory;
import org.cmdbuild.task.dao.LogicAndStoreConverter;
//import org.cmdbuild.scheduler.command.Command;
import org.cmdbuild.easytemplate.EasytemplateCardResolver;
import org.cmdbuild.easytemplate.EasytemplateCqlResolver;
import org.cmdbuild.easytemplate.EasytemplateFunctionResolver;
import org.joda.time.DateTime;

import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import java.util.List;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.report.ReportService;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import org.springframework.stereotype.Component;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CURRENTID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.LT;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.SorterElement.SorterElementDirection;
import static org.cmdbuild.data.filter.utils.CmdbFilterUtils.parseFilter;

@Component
public class AsynchronousEventTaskJobFactory   {

	private final DaoService dao;
	private final TaskStore taskStore;
	private final LogicAndStoreConverter logicAndStoreConverter;
	private final EasytemplateProcessor databaseEngine;
	private final ReportService reportLogic;
	private final EasytemplateFunctionResolver functionResolver;
	private final EasytemplateCqlResolver cqlResolver;

	public AsynchronousEventTaskJobFactory(DaoService dao, TaskStore taskStore, LogicAndStoreConverter logicAndStoreConverter, EasytemplateProcessor databaseEngine, ReportService reportLogic, EasytemplateFunctionResolver functionResolver, EasytemplateCqlResolver cqlResolver) {
		this.dao = checkNotNull(dao);
		this.taskStore = checkNotNull(taskStore);
		this.logicAndStoreConverter = checkNotNull(logicAndStoreConverter);
		this.databaseEngine = checkNotNull(databaseEngine);
		this.reportLogic = checkNotNull(reportLogic);
		this.functionResolver = checkNotNull(functionResolver);
		this.cqlResolver = checkNotNull(cqlResolver);
	}

//	@Override
	protected Class<AsynchronousEventTask> getType() {
		return AsynchronousEventTask.class;
	}

//	@Override
//	protected Command command(AsynchronousEventTask task) {
//		return new Command() {
//
//			@Override
//			public void execute() {
//				String classname = task.getTargetClassname();
//				String filterStr = task.getFilter();
//				DateTime lastExecution = taskStore.read(logicAndStoreConverter.taskToTaskData(task)).getLastExecution();
//
//				logger.debug("checking class '{}' with filter '{}'", classname, filterStr);
//
//				filterStr = EasytemplateProcessorImpl.builder().withResolver(functionResolver, FUNCTION_PREFIX).build().processExpression(filterStr);
//				CmdbFilter filter = parseFilter(filterStr);
//				List<Card> cards = dao.selectAll().from(classname).where(filter).getCards();
//				for (Card card : cards) {
//					Classe cardType = card.getType();
//					if (createdAfterLastExecution(card, lastExecution)) {
//						logger.debug("history card not found");
//						sendEmail(task, card);
//					} else {
//						Optional<Card> lastHistoryCardWithNoFilter = historyCardBeforeLastExecutionWithNoFilter(cardType, card.getId(), lastExecution);
//						if (lastHistoryCardWithNoFilter.isPresent()) {
//							Card historyCardWithNoFilter = lastHistoryCardWithNoFilter.get();
//							logger.debug("found history card with id '{}'", historyCardWithNoFilter.getId());
//							Optional<Card> historyCardWithFilter = historyCardBeforeLastExecutionWithFilter(cardType, historyCardWithNoFilter, filter);
//							if (!historyCardWithFilter.isPresent()) {
//								logger.debug("filtered history card not found");
//								sendEmail(task, card);
//							}
//						}
//					}
//				}
//			}
//
//			private boolean createdAfterLastExecution(Card card, DateTime lastExecution) {
//				logger.debug("checking if card has been created after last execution");
//				return (lastExecution == null) ? true : card.getBeginDate().compareTo(lastExecution) > 0;
//			}
//
//			private Optional<Card> historyCardBeforeLastExecutionWithNoFilter(Classe type, Long id, DateTime lastExecution) {
//				logger.debug("getting last history for card of type '{}' and with id '{}'", type.getName(), id);
//				return Optional.fromNullable(dao.selectAll().from(type).includeHistory()
//						.where(ATTR_CURRENTID, EQ, id)
//						.where(ATTR_STATUS, EQ, "U")//TODO improve this
//						.where(ATTR_BEGINDATE, LT, lastExecution)
//						.orderBy(ATTR_BEGINDATE, SorterElementDirection.DESC)
//						.limit(1)
//						.getCardOrNull());
//			}
//
//			private Optional<Card> historyCardBeforeLastExecutionWithFilter(Classe type, Card historyCard, CmdbFilter jsonFilter) {
//				logger.debug("getting last history for card of type '{}' and id '{}', with filter '{}'", type.getName(), historyCard.getId(), jsonFilter);
//				return Optional.fromNullable(dao.selectAll().from(type).includeHistory()
//						.where(ATTR_ID, EQ, historyCard.getId())
//						.where(ATTR_STATUS, EQ, "U")//TODO improve this						
//						.getCardOrNull());
//			}
//
//		};
//	}

	private void sendEmail(AsynchronousEventTask task, Card card) {
		if (task.isNotificationActive()) {
//			Supplier<EmailTemplate> emailTemplateSupplier = memoize(() -> {
//				String name = defaultString(task.getNotificationTemplate());
//				return emailTemplateLogic.getByName(name);
//			});
//			EmailAccount emailAccount = emailAccountFacade.getAccountOrDefaultOrNull(toStringOrNull(emailTemplateSupplier.get().getAccount()), task.getNotificationAccount());//TODO check themplate.getAccount, should return valid account name/id
//			Optional<EmailAccount> account = emailAccountFacade.
//					.firstOfOrDefault(asList(emailTemplateSupplier.get().getAccount(), task.getNotificationAccount()));
//			Supplier<EmailAccount> emailAccountSupplier = account.isPresent() ? ofInstance(account.get()) : null;
			EasytemplateProcessorImpl templateResolver = EasytemplateProcessorImpl.builder()
					.withResolver(emptyStringOnNull(nullOnError(EasytemplateCardResolver.forCard(card))), CARD_PREFIX)
					.withResolver(emptyStringOnNull(nullOnError(cqlResolver)), CQL_PREFIX)
					.withResolver(emptyStringOnNull(nullOnError((input) -> databaseEngine.processExpression(input))), DB_TEMPLATE)
					.withResolver(functionResolver, FUNCTION_PREFIX)
					.build();
			Collection<Supplier<? extends DataHandler>> attachments = new ArrayList<>();
			if (task.isReportActive()) {
				attachments.add(new Supplier<DataHandler>() {

					@Override
					public DataHandler get() {
						ReportInfo report = from(reportLogic.getAll()) //
								.filter(input -> input.getCode().equals(task.getReportName())) //
								.limit(1) //
								.first() //
								.get();
						return reportLogic.executeReportAndDownload(report.getId(), reportExtFromString(task.getReportExtension()),
								resolve((Map) task.getReportParameters()));
					}

					private Map<String, ? extends Object> resolve(Map<String, String> input) {
						return Maps.transformValues(input, (String input1) -> templateResolver.processExpression(input1));
					}

				});
			}
			throw new UnsupportedOperationException("TODO");
//			emailTemplateSenderFactory.queued() //
//					.withAccount(emailAccount) //
//					.withTemplate(emailTemplateSupplier) //
//					.withTemplateResolver(templateResolver) //
//					.withReference(task.getId()) //
//					.withAttachments(attachments) //
//					.build() //
//					.execute();
		}
	}

}
