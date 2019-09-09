package org.cmdbuild.task.cardeventprocessing;

import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.isEmpty;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.easytemplate.EasytemplateResolverNames.CURRENT_CARD_PREFIX;
import static org.cmdbuild.easytemplate.EasytemplateResolverNames.NEXT_CARD_PREFIX;
import static org.cmdbuild.easytemplate.EasytemplateResolverNames.PREVIOUS_CARD_PREFIX;

import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.logic.mapping.json.JsonFilterHelper;
import org.cmdbuild.task.util.CardIdFilterElementGetter;
import org.cmdbuild.task.startworkflow.StartProcessAction;
import org.cmdbuild.easytemplate.EasytemplateCardResolver;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.function.Consumer;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.dao.query.QueryResult;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data2.impl.QuerySpecsBuilderService;
import org.cmdbuild.logic.taskmanager.SynchronousEventTask;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.view.DataView;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.EmailTemplateService;
import org.cmdbuild.event.AfterCardCreateEvent;
import org.cmdbuild.event.AfterCardUpdateEvent;
import org.cmdbuild.event.BeforeCardDeleteEvent;
import org.cmdbuild.event.BeforeCardUpdateEvent;
import org.cmdbuild.event.CardEvent;
import org.cmdbuild.logic.data.QueryOptionsImpl;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class SynchronousEventTaskServiceImpl implements SynchronousEventTaskService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final OperationUserSupplier userStore;
	private final ScriptCommandService scriptService;
	private final WorkflowService workflowLogic;
	private final EmailAccountService emailAccountFacade;
	private final EmailTemplateService emailTemplateLogic;
	private final DataView dataView;
	private final EmailService emailTemplateSenderFactory;
	private final QuerySpecsBuilderService querySpecsBuilderService;

	public SynchronousEventTaskServiceImpl(QuerySpecsBuilderService querySpecsBuilderService, OperationUserSupplier userStore, ScriptCommandService fluentApi, WorkflowService workflowLogic, EmailAccountService emailAccountFacade, EmailTemplateService emailTemplateLogic, DataView dataView, EmailService emailTemplateSenderFactory) {
		this.userStore = userStore;
		this.scriptService = fluentApi;
		this.workflowLogic = workflowLogic;
		this.emailAccountFacade = emailAccountFacade;
		this.emailTemplateLogic = emailTemplateLogic;
		this.dataView = dataView;
		this.emailTemplateSenderFactory = emailTemplateSenderFactory;
		this.querySpecsBuilderService = querySpecsBuilderService;
	}

	@Override
	public Object createCardEventListenerForTask(SynchronousEventTask task) {
		List<Consumer<CardEvent>> list = list();
		if (task.isWorkflowEnabled()) {
			list.add(buildStartWorkflowEventProcessor(task));
		}
		if (task.isEmailEnabled()) {
			list.add(buildSendEmailEventProcessor(task));
		}
		if (task.isScriptingEnabled()) {
			list.add(scriptingOf(task));
		}
		Consumer<CardEvent> processor = list.size() == 1 ? getOnlyElement(list) : (event) -> list.forEach((c) -> c.accept(event));

		Predicate<Card> eventFilter = new SynchronousEventTaskPredicate(task);

		return cardEventListenerForPhase(task.getPhase(), (event) -> {
			try {
				if (eventFilter.apply(event.getCurrentCard())) {
					logger.debug("activate sync event task = {} for event = {}", task, event);
					processor.accept(event);
				}
			} catch (Exception ex) {
				throw runtime(ex, "error processing sync event task = %s for event = %s", task, event);
			}
		});
	}

	private Object cardEventListenerForPhase(SynchronousEventTask.Phase phase, Consumer<CardEvent> processor) {
		switch (phase) {
			case AFTER_CREATE:
				return new Object() {
					@Subscribe
					public void handleAfterCardCreateEvent(AfterCardCreateEvent event) {
						processor.accept(event);
					}
				};
			case BEFORE_UPDATE:
				return new Object() {
					@Subscribe
					public void handleBeforeCardUpdateEvent(BeforeCardUpdateEvent event) {
						processor.accept(event);
					}
				};
			case AFTER_UPDATE:
				return new Object() {
					@Subscribe
					public void handleAfterCardUpdateEvent(AfterCardUpdateEvent event) {
						processor.accept(event);
					}
				};
			case BEFORE_DELETE:
				return new Object() {
					@Subscribe
					public void handleBeforeCardDeleteEvent(BeforeCardDeleteEvent event) {
						processor.accept(event);
					}
				};
			default:
				throw unsupported("unsupported phase = %s", phase);
		}
	}

	private Consumer<CardEvent> buildStartWorkflowEventProcessor(SynchronousEventTask task) {
		return (context) -> StartProcessAction.newInstance()
				.withWorkflowLogic(workflowLogic)
				.withClassName(task.getWorkflowClassName())
				.withAttributes(task.getWorkflowAttributes())
				.withAdvanceStatus(task.isWorkflowAdvanceable())
				.withTemplateResolver(contextBasedTemplateResolver(context).build())
				.build()
				.execute();
	}

	private Consumer<CardEvent> buildSendEmailEventProcessor(SynchronousEventTask task) {
		throw new UnsupportedOperationException("TODO");
//		return (context) -> {
//			Supplier<Template> emailTemplateSupplier = memoize(() -> {
//				String name = defaultString(task.getEmailTemplate());
//				return emailTemplateLogic.read(name);
//			});
//			Optional<EmailAccount> account = emailAccountFacade.firstOfOrDefault(asList(emailTemplateSupplier.get().getAccount(), task.getEmailAccount()));
//			Supplier<EmailAccount> emailAccountSupplier = account.isPresent() ? ofInstance(account.get()) : null;
//			emailTemplateSenderFactory.queued()
//					.withAccount(emailAccountSupplier)
//					.withTemplate(emailTemplateSupplier)
//					.withTemplateResolver(templateResolverForEmail(context))
//					.withReference(task.getId())
//					.build().execute();
//		};
	}
//
//	private EasytemplateProcessor templateResolverForEmail(CardEvent context) {
//		return contextBasedTemplateResolver(context)
//				.withResolver(emptyStringOnNull(nullOnError(EasytemplateUserEmailResolver.newInstance().withDataView(dataView).build())), USER_PREFIX)
//				.withResolver(emptyStringOnNull(nullOnError(EasytemplateGroupEmailResolver.newInstance().withDataView(dataView).build())), GROUP_PREFIX)
//				.withResolver(emptyStringOnNull(nullOnError(EasytemplateGroupUsersEmailResolver.newInstance().withDataView(dataView).withSeparator(EmailConstants.ADDRESSES_SEPARATOR).build())), GROUP_USERS_PREFIX)
//				.withResolver(emptyStringOnNull(nullOnError(EasytemplateCqlResolver.newInstance().withDataView(dataView).build())), CQL_PREFIX).build();
//	}

	private Consumer<CardEvent> scriptingOf(SynchronousEventTask task) {
		ScriptCommand script = ScriptCommandImpl.builder()
				.withEngine(task.getScriptingEngine())
				.withScript(task.getScriptingScript())
				.withId(format("task:%s", task.getId()))
				.build();
		if (task.isScriptingSafe()) {
			return (event) -> {
				try {
					scriptService.executeScript(script, event);
				} catch (Exception ex) {
					logger.error("error executing script = {} for task = {} event = {}", script, task, event, ex);
				}
			};
		} else {
			return (event) -> scriptService.executeScript(script, event);
		}
	}

	private EasytemplateProcessorImpl.EasytemplateProcessorImplBuilder contextBasedTemplateResolver(CardEvent event) {
		return EasytemplateProcessorImpl.builder().accept((builder) -> {

			if (event instanceof AfterCardCreateEvent) {
				builder
						.withResolver(EasytemplateCardResolver.forCard(event.getCurrentCard()), CURRENT_CARD_PREFIX);
			} else if (event instanceof BeforeCardUpdateEvent) {
				builder
						.withResolver(EasytemplateCardResolver.forCard(event.getCurrentCard()), CURRENT_CARD_PREFIX)
						.withResolver(EasytemplateCardResolver.forCard(((BeforeCardUpdateEvent) event).getNextCard()), NEXT_CARD_PREFIX);
			} else if (event instanceof AfterCardUpdateEvent) {
				builder
						.withResolver(EasytemplateCardResolver.forCard(event.getCurrentCard()), CURRENT_CARD_PREFIX)
						.withResolver(EasytemplateCardResolver.forCard(((AfterCardUpdateEvent) event).getPreviousCard()), PREVIOUS_CARD_PREFIX);
			} else if (event instanceof BeforeCardDeleteEvent) {
				builder
						.withResolver(EasytemplateCardResolver.forCard(event.getCurrentCard()), CURRENT_CARD_PREFIX);
			}

		});
	}

	private class SynchronousEventTaskPredicate implements Predicate<Card> {

		private final SynchronousEventTask task;

		private SynchronousEventTaskPredicate(SynchronousEventTask task) {
			this.task = checkNotNull(task);
		}

		@Override
		public boolean apply(Card input) {
			return matchesGroup() && matchesClass(input) && matchesCards(input);
		}

		private boolean matchesGroup() {
			return isEmpty(task.getGroups()) || contains(task.getGroups(), userStore.getUser().getDefaultGroupNameOrNull());
		}

		private boolean matchesClass(Card input) {
			Classe type = input.getType();
			String targetClassname = task.getTargetClassname();
			return isBlank(targetClassname) || type.getName().equals(targetClassname)
					|| type.getAncestorsAndSelf().stream().anyMatch(equalTo(targetClassname));
		}

		private boolean matchesCards(Card input) {
			return (isBlank(task.getTargetClassname()) && isBlank(task.getFilter())) || matchesFilter(input);
		}

		private boolean matchesFilter(Card input) { //TODO use new dao; if possible evaluate in memory
			String classname = task.getTargetClassname();
			String filter = task.getFilter();
			try {
				JSONObject jsonFilter = (filter == null) ? new JSONObject() : new JSONObject(filter);
				QueryOptionsImpl queryOptions = QueryOptionsImpl.builder() //
						.filter(new JsonFilterHelper(jsonFilter) //
								.merge(CardIdFilterElementGetter.of(input))) //
						.build();
				QueryResult result = querySpecsBuilderService.withQueryOptions(queryOptions).withNullableEntryType(dataView
						.findClasse(classname)).builder()
						.run();
				return !isEmpty(result);
			} catch (JSONException e) {
				final String message = format("malformed filter: '%s'", filter);
				logger.error(message, e);
				return false;
			}
		}
	}
}
