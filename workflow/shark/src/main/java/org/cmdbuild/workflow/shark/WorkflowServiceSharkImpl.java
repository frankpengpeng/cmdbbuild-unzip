package org.cmdbuild.workflow.shark;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static org.cmdbuild.exception.ConsistencyException.ConsistencyExceptionType.OUT_OF_DATE_PROCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.DataSource;

import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.entrytype.attributetype.CMAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForwardingAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import org.joda.time.DateTime;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;

import net.jcip.annotations.NotThreadSafe;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.lock.LockService;
import org.cmdbuild.workflow.model.SimpleTaskInfo;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.TaskInfo;
import org.cmdbuild.workflow.model.FlowStatus;
import org.cmdbuild.workflow.inner.WorkflowTypesConverter;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.workflow.model.FlowStatus.ABORTED;
import static org.cmdbuild.workflow.model.FlowStatus.OPEN;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_GROUP_NAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_PERFORMER_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_USER_USERNAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_USER_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.PROCESS_CARD_ID_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.PROCESS_CLASSNAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.PROCESS_INSTANCE_ID_VARIABLE;
import org.cmdbuild.workflow.model.SimpleFlowData;
import org.cmdbuild.workflow.shark.engine.WorkflowActionExecutor;
import org.cmdbuild.workflow.shark.engine.WorkflowActionExecutorCallback;
import org.cmdbuild.workflow.shark.engine.WorkflowRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.workflow.model.FlowInfo;
import org.cmdbuild.workflow.inner.WorkflowServiceDelegate;
import org.cmdbuild.workflow.inner.FlowCardRepository;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.emptyOptions;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.core.q3.WhereOperator;
import org.cmdbuild.utils.date.CmDateUtils;
import org.cmdbuild.workflow.model.AdvancedFlowStatus;
import static org.cmdbuild.workflow.WorkflowCommonConst.SHARK;
import org.cmdbuild.workflow.model.SimpleFlowAdvanceResponse;
import org.cmdbuild.workflow.utils.PlanIdUtils;
import org.cmdbuild.workflow.shark.xpdl.SharkPlanService;
import static org.cmdbuild.workflow.core.utils.WorkflowUtils.getEntryTaskForCurrentUserOrNull;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.workflow.core.model.TaskImpl;
import org.cmdbuild.workflow.core.utils.WorkflowUtils;
import org.cmdbuild.workflow.model.XpdlInfoImpl;
import org.cmdbuild.workflow.model.XpdlInfo;
import org.cmdbuild.workflow.inner.WfReference;
import org.cmdbuild.workflow.shark.engine.WorkflowRemoteRepository;
import org.cmdbuild.widget.model.WidgetData;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_ACTION_ADVANCE;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_ACTION_SUBMIT;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.lookup.LookupService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.workflow.shark.data.SharkFlowTasklistLoaderServiceImpl;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.inner.ProcessRepository;
import org.cmdbuild.auth.role.Role;
import static com.google.common.collect.FluentIterable.from;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NEXT_EXECUTOR;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_DEFINITION_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_INSTANCE_ID;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.auth.login.AuthenticationService;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.workflow.WorkflowService.WorkflowVariableProcessingStrategy;

@Component
public class WorkflowServiceSharkImpl implements WorkflowServiceDelegate {

	private static final String BEGIN_DATE_ATTRIBUTE = "beginDate";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final WorkflowConfiguration configuration;
	private final LockService lockLogic;
	private final OperationUserSupplier operationUserSupplier;
	private final FlowCardRepository repository;
	private final WorkflowRemoteService remoteService;
	private final WorkflowTypesConverter typesConverter;
	private final AuthenticationService authenticationService;
	private final SharkFlowVariablesSynchronizer processSynchronizer;
	private final ProcessRepository classeRepository;
	private final SharkPlanService planService;
	private final WorkflowRemoteRepository remoteRepository;
	private final OperationUserSupplier userSupplier;
	private final WidgetService widgetService;
	private final DaoService dao;
	private final LookupService lookupService;
	private final SharkFlowTasklistLoaderServiceImpl cardToFlowCard;

	public WorkflowServiceSharkImpl(WorkflowConfiguration configuration, LockService lockLogic, OperationUserSupplier operationUserSupplier, FlowCardRepository repository, WorkflowRemoteService remoteService, WorkflowTypesConverter typesConverter, AuthenticationService authenticationService, SharkFlowVariablesSynchronizer processSynchronizer, ProcessRepository classeRepository, SharkPlanService planService, WorkflowRemoteRepository remoteRepository, OperationUserSupplier userSupplier, WidgetService widgetService, DaoService dao, LookupService lookupService, SharkFlowTasklistLoaderServiceImpl cardToFlowCard) {
		this.configuration = checkNotNull(configuration);
		this.lockLogic = checkNotNull(lockLogic);
		this.operationUserSupplier = checkNotNull(operationUserSupplier);
		this.repository = checkNotNull(repository);
		this.remoteService = checkNotNull(remoteService);
		this.typesConverter = checkNotNull(typesConverter);
		this.authenticationService = checkNotNull(authenticationService);
		this.processSynchronizer = checkNotNull(processSynchronizer);
		this.classeRepository = checkNotNull(classeRepository);
		this.planService = checkNotNull(planService);
		this.remoteRepository = checkNotNull(remoteRepository);
		this.userSupplier = checkNotNull(userSupplier);
		this.widgetService = checkNotNull(widgetService);
		this.dao = checkNotNull(dao);
		this.lookupService = checkNotNull(lookupService);
		this.cardToFlowCard = checkNotNull(cardToFlowCard);
	}

	@Override
	public String getName() {
		return SHARK;
	}

	@Override
	public List<Task> getTaskListForCurrentUserByClassIdAndCardId(String processId, Long cardId) {
		return getTaskListForCurrentUserByClassIdAndOptionalCardId(processId, checkNotNull(cardId), emptyOptions()).elements();
	}

	@Override
	public PagedElements<Task> getTaskListForCurrentUserByClassIdSkipFlowData(String classId, DaoQueryOptions queryOptions) {
		return getTaskListForCurrentUserByClassIdAndOptionalCardId(classId, null, queryOptions);
	}

	private PagedElements<Task> getTaskListForCurrentUserByClassIdAndOptionalCardId(String processId, @Nullable Long cardId, DaoQueryOptions queryOptions) {
		logger.debug("getTaskListForCurrentUser with processId = {} and cardId = {}", processId, cardId);
		//TODO handle positionOf
		QueryBuilder query = dao.selectAll()
				.selectExpr("_unnested_taskid", format("unnest(%s)", quoteSqlIdentifier(ATTR_TASK_INSTANCE_ID)))
				.from(processId)
				//				.where("FlowStatus", WhereOperator.EQ, lookupService.getLookupByTypeAndCode("FlowStatus", "open.running").getId())
				.withOptions(queryOptions);
		if (cardId != null) {
			query.where(ATTR_ID, WhereOperator.EQ, cardId);
		}
		List<Task> tasks = query.run().stream().map((r) -> {
			Card card = r.toCard();
			Flow flowCard = cardToFlowCard.cardToFlowCard(card);
			String taskId = checkNotBlank(toStringOrNull(r.asMap().get("_unnested_taskid")));
			return getTask(flowCard, taskId);
		}).collect(toList());
		logger.debug("returning {} shark tasks", tasks.size());
		int total;
		if (queryOptions.isPaged()) {
			total = dao.select(ATTR_ID)
					.selectExpr("_unnested_taskid", format("unnest(%s)", quoteSqlIdentifier(ATTR_TASK_INSTANCE_ID)))
					.selectCount()
					.from(processId)
					.where(query)
					.getCount();
			return new PagedElements<>(tasks, total);
		} else {
			return new PagedElements<>(tasks);
		}
	}

	@Override
	public Task getUserTask(Flow card, String taskId) {
		logger.debug("getting activity instance {} for card = {}", taskId, card);
		return getTask(card, taskId);
	}

	private boolean isProcessUpdated(Flow processInstance, DateTime givenBeginDate) {
		DateTime currentBeginDate = processInstance.getBeginDate();
		return givenBeginDate.equals(currentBeginDate);
	}

	@Override
	public FlowAdvanceResponse startProcess(Process process, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
		Flow flowCard = startProcess(process, vars);
		List<Task> activities = getTaskList(flowCard);
		if (activities.size() != 1) {
			throw new UnsupportedOperationException(format("Not just one activity to advance! (%d activities)", activities.size()));
		}
		Task task = activities.get(0);
		Map<String, Object> mergedVars = mergeVars(
				from(flowCard.getAttributeValues()).filter(new ValuesFilter(process)),
				vars);
		updateActivity(task, mergedVars);
		if (advance) {
			flowCard = advanceActivity(task);
		} else {
			flowCard = task.getProcessInstance();
		}
		return buildFlowAdvanceResponse(flowCard);
	}

	@Override
	public TaskDefinition getTaskDefinition(Flow flowCard, String taskId) {
		return planService.getTaskDefinition(flowCard, taskId);
	}

	@Override
	public Map<String, Object> getFlowData(Flow flowCard) {
		return remoteService.getProcessInstanceVariables(flowCard.getFlowId());
	}

	/**
	 * Only non-null attributes are accepted with the following exceptions:
	 * date, datetime and time attributes are always set even if null (needed
	 * for initialize correctly workflow instance variables).
	 */
	@NotThreadSafe
	private static class ValuesFilter extends ForwardingAttributeTypeVisitor implements Predicate<Entry<String, Object>> {

		private final CMAttributeTypeVisitor DELEGATE = NullAttributeTypeVisitor.getInstance();

		@Override
		protected CMAttributeTypeVisitor delegate() {
			return DELEGATE;
		}

		private final Process process;
		private String name;
		private Object value;
		private boolean applies;

		public ValuesFilter(Process process) {
			this.process = process;
		}

		@Override
		public boolean apply(Entry<String, Object> input) {
			name = input.getKey();
			value = input.getValue();
			applies = (value != null);
			Attribute attribute = process.getAttributeOrNull(name);
			if (attribute != null) {
				attribute.getType().accept(this);
			} else {
				applies = false;
			}
			return applies;
		}

		@Override
		public void visit(DateAttributeType attributeType) {
			applies = true;
		}

		@Override
		public void visit(DateTimeAttributeType attributeType) {
			applies = true;
		}

		@Override
		public void visit(LookupAttributeType attributeType) {
			if (value instanceof IdAndDescriptionImpl) {
				applies = IdAndDescriptionImpl.class.cast(value).getId() != null;
			}
		}

		@Override
		public void visit(ReferenceAttributeType attributeType) {
			if (value instanceof IdAndDescriptionImpl) {
				applies = IdAndDescriptionImpl.class.cast(value).getId() != null;
			}
		}

		@Override
		public void visit(TimeAttributeType attributeType) {
			applies = true;
		}

	}

	/**
	 * This awful hack is needed because SOMEONE decided that it was a good idea
	 * to specify default attributes in the database, so old clients did it and
	 * now we have to deal with it.
	 *
	 * @param databaseValues values as they are in the newly created database
	 * row
	 * @param entrySet values submitted in the form
	 *
	 * @return database values overridden by the submitted ones
	 */
	private Map<String, Object> mergeVars(Iterable<Entry<String, Object>> databaseValues, Map<String, ?> submittedValues) {
		Map<String, Object> mergedValues = new HashMap<>();
		for (Entry<String, ?> e : databaseValues) {
			mergedValues.put(e.getKey(), e.getValue());
		}
		for (Entry<String, ?> e : submittedValues.entrySet()) {
			mergedValues.put(e.getKey(), e.getValue());
		}
		return mergedValues;
	}

	@Override
	public FlowAdvanceResponse updateProcess(Flow processInstance, String taskId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
		Process processClass = processInstance.getType();
		lockLogic.requireLockedByCurrent(LockService.itemIdFromCardIdAndActivityId(processInstance.getCardId(), taskId));

		/*
		 * check if the given begin date is the same of the stored process, to
		 * be sure to deny the update of old versions
		 */
		if (vars.containsKey(BEGIN_DATE_ATTRIBUTE)) {
			Long givenBeginDateAsLong = (Long) vars.get(BEGIN_DATE_ATTRIBUTE);
			DateTime givenBeginDate = new DateTime(givenBeginDateAsLong);
			if (!isProcessUpdated(processInstance, givenBeginDate)) {
				throw OUT_OF_DATE_PROCESS.createException();
			}

			/*
			 * must be removed to not use it as a custom attribute
			 */
			vars.remove(BEGIN_DATE_ATTRIBUTE);
		}

		updateProcess(getTask(processInstance, taskId), vars, advance);

		lockLogic.releaseLock(LockService.itemIdFromCardIdAndActivityId(processInstance.getCardId(), taskId));

		/*
		 * retrieve again the processInstance because the updateProcess return
		 * the old processInstance, not the updated
		 */
		processInstance = repository.getFlowCardByPlanAndCardId(processClass, processInstance.getCardId());
		return buildFlowAdvanceResponse(processInstance);
	}

	private Flow updateProcess(Task activityInstance, Map<String, ?> vars, boolean advance) {
		updateActivity(activityInstance, vars);
		Flow output;
		if (advance) {
			output = advanceActivity(activityInstance);
		} else {
			output = activityInstance.getProcessInstance();
		}
		return output;
	}

	private FlowAdvanceResponse buildFlowAdvanceResponse(Flow flowCard) {
		return SimpleFlowAdvanceResponse.builder()
				.withFlowCard(flowCard)
				.withTasklist(emptyList())
				.withAdvancedFlowStatus(flowStatusToAdvancedFlowStatus(flowCard.getStatus()))
				.build();
	}

	private AdvancedFlowStatus flowStatusToAdvancedFlowStatus(FlowStatus flowStatus) {
		switch (flowStatus) {
			case OPEN:
			case SUSPENDED:
			case UNDEFINED:
				return AdvancedFlowStatus.PROCESSING_SCRIPT;
			case TERMINATED:
			case ABORTED:
			case COMPLETED:
			case UNSUPPORTED:
			default:
				return AdvancedFlowStatus.COMPLETED;
		}
	}

	private Flow startProcess(Process plan, Map<String, ?> vars) {
		logger.info("starting walk for plan = {} with variables = {}", plan, vars);

		TaskDefinition startTaskDefinition = getEntryTaskForCurrentUserOrNull(plan, userSupplier.getUser());
		if (startTaskDefinition == null) {
			logger.warn("start activity is null for plan = {}, skip start", plan);
			return null;
		}
		PlanIdUtils.PackageIdAndVersionAndDefinitionId packageIdAndProcessId = PlanIdUtils.readPlanId(plan.getPlanIdOrNull());
		FlowInfo flowInfo = actionExecutor().startProcess(packageIdAndProcessId.getPackageId(), packageIdAndProcessId.getDefinitionId());
		TaskInfo startTaskInfo = keepOnlyStartingActivityInstance(startTaskDefinition.getId(), flowInfo.getFlowId());

		logger.debug("create walk on local repository for walkInfo = {}", flowInfo);
		Flow walk = repository.createFlowCard(plan, flowInfo, SimpleFlowData.builder()
				.withStatus(OPEN)
				.withInfo(flowInfo)
				.withValues(defaultIfNull(vars, emptyMap()).isEmpty() ? null : vars)
				.build(), () -> remoteService.getProcessInstanceVariables(flowInfo.getFlowId()));
		logger.debug("current walk = {}", walk);
		logger.debug("update walk on local repository (add start task with current group)");
		walk = repository.updateFlowCard(walk, SimpleFlowData.builder()
				.withTasksToAdd(asList(activityWithSpecificParticipant(startTaskInfo, operationUserSupplier.getUser().getDefaultGroupOrNull().getName())))
				.build(), () -> remoteService.getProcessInstanceVariables(flowInfo.getFlowId()));
		fillCardInfoAndProcessInstanceIdOnProcessInstance(walk);
		logger.debug("current walk = {}", walk);
		logger.debug("refresh walk instance");
		walk = repository.getFlowCard(walk);
		logger.debug("return walk = {}", walk);
		return walk;
	}

	private TaskInfo activityWithSpecificParticipant(TaskInfo taskInfo, String participant) {
		checkNotNull(taskInfo);
		return SimpleTaskInfo.copyOf(taskInfo)
				.withParticipants(singleton(participant))
				.build();
	}

	private TaskInfo keepOnlyStartingActivityInstance(String taskId, String flowId) {
		logger.debug("keepOnlyStartingActivityInstance for startTaskId = {} walkId = {}", taskId, flowId);
		TaskInfo startActivityInstanceInfo = null;
		Iterable<TaskInfo> activityInstanceInfos = remoteService.findOpenActivitiesForProcessInstance(flowId);
		for (TaskInfo activityInstanceInfo : activityInstanceInfos) {
			String activityDefinitionId = activityInstanceInfo.getTaskDefinitionId();
			if (taskId.equals(activityDefinitionId)) {
				startActivityInstanceInfo = activityInstanceInfo;
			} else {
				String taskIdToTerminate = activityInstanceInfo.getTaskId();
				logger.debug("terminate task {}", taskIdToTerminate);
				actionExecutor().abortTask(flowId, taskIdToTerminate);
			}
		}
		checkNotNull(startActivityInstanceInfo, "start task not found for taskId = %s flowId = %s", taskId, flowId);
		return startActivityInstanceInfo;
	}

	private void fillCardInfoAndProcessInstanceIdOnProcessInstance(Flow walk) {
		logger.debug("fillCardInfoAndProcessInstanceIdOnProcessInstance for walk = {}", walk);
		String walkId = walk.getFlowId();
		Map<String, Object> extraVars = Maps.newHashMap();
		extraVars.put(PROCESS_CARD_ID_VARIABLE, walk.getCardId());
		extraVars.put(PROCESS_CLASSNAME_VARIABLE, walk.getType().getName());
		extraVars.put(PROCESS_INSTANCE_ID_VARIABLE, walkId);
		remoteService.setProcessInstanceVariables(walkId, toWorkflowValues(walk.getType(), extraVars));
	}

	private Map<String, Object> toWorkflowValues(Process processClass, Map<String, Object> nativeValues) {
		Map<String, Object> workflowValues = Maps.newHashMap();
		for (Map.Entry<String, Object> entry : nativeValues.entrySet()) {
			String attributeName = entry.getKey();
			CardAttributeType<?> attributeType;
			Attribute attribute = processClass.getAttributeOrNull(attributeName);
			if (attribute == null) {
				logger.warn("unable to get attribute type for attribute name = {}", attributeName);
				attributeType = null;
			} else {
				attributeType = attribute.getType();
			}
			Object systemValue = entry.getValue();
			Object workflowValue = typesConverter.toWorkflowType(attributeType, systemValue);
			logger.debug("converted value for key = {} from value = {} to workflow value = {}", attributeName, systemValue, workflowValue);
			workflowValues.put(attributeName, workflowValue);
		}
		return workflowValues;
	}

	@Override
	public void abortProcessInstance(Flow processInstance) {
		logger.info("aborting process instance for class '{}' and id '{}'", processInstance.getType().getName(), processInstance.getCardId());
		actionExecutor().abortFlow(processInstance.getFlowId());
		repository.updateFlowCard(processInstance, SimpleFlowData.builder().withStatus(ABORTED).build(), () -> remoteService.getProcessInstanceVariables(processInstance.getFlowId()));
	}

	@Override
	public void suspendProcessInstance(Flow processInstance) {
		logger.info("suspending process instance for class '{}' and id '{}'", processInstance.getType().getName(), processInstance.getCardId());
		actionExecutor().suspendFlow(processInstance.getFlowId());
	}

	@Override
	public void resumeProcessInstance(Flow processInstance) {
		logger.info("resuming process instance for class '{}' and id '{}'", processInstance.getType().getName(), processInstance.getCardId());
		actionExecutor().resumeFlow(processInstance.getFlowId());
	}

	private void updateActivity(Task activityInstance, Map<String, ?> inputValues) {
		logger.info("updating activity instance '{}' for process '{}'", activityInstance.getId(), activityInstance.getProcessInstance().getType().getName());

		Flow walk = activityInstance.getProcessInstance();
		walk = repository.updateFlowCard(walk, SimpleFlowData.builder().withValues(defaultIfNull(inputValues, emptyMap()).isEmpty() ? null : inputValues).build(), () -> remoteService.getProcessInstanceVariables(activityInstance.getProcessInstance().getFlowId()));

		Map<String, Object> nativeValues = map(inputValues);
		nativeValues.put(CURRENT_USER_USERNAME_VARIABLE, currentUserUsername());
		nativeValues.put(CURRENT_GROUP_NAME_VARIABLE, currentGroupName());
		nativeValues.put(CURRENT_USER_VARIABLE, currentUserReference());
		nativeValues.put(CURRENT_PERFORMER_VARIABLE, currentGroupReference(activityInstance));

		/**
		 * Synchronizes missing variables
		 */
		if (!configuration.isSynchronizationOfMissingVariablesDisabled()) {
			Map<String, Object> workflowServiceVariables = remoteService.getProcessInstanceVariables(walk.getFlowId());
			for (Attribute attribute : walk.getType().getServiceAttributes()) {
				if (!attribute.hasNotServiceListPermission() && !workflowServiceVariables.containsKey(attribute.getName())) {
					logger.debug("'{}' is missing, initializing it", attribute.getName());
					nativeValues.put(attribute.getName(), null);
				}
			}
		}

		saveWidgets(activityInstance, nativeValues);
		remoteService.setProcessInstanceVariables(walk.getFlowId(), toWorkflowValues(walk.getType(), nativeValues));
	}

	private WfReference currentUserReference() {
		LoginUser authenticatedUser = operationUserSupplier.getUser().getLoginUser();
		return new WfReference() {

			@Override
			public Long getId() {
				return authenticatedUser.getId();
			}

			@Override
			public String getClassName() {
				return "User";
			}

		};
	}

	private String currentUserUsername() {
		return operationUserSupplier.getUser().getLoginUser().getUsername();
	}

	private String currentGroupName() {
		return operationUserSupplier.getUser().getDefaultGroupOrNull().getName();
	}

	private WfReference currentGroupReference(Task activityInstance) {
		Role group = authenticationService.getGroupWithNameOrNull(activityInstance.getPerformerName());
		WfReference output = WorkflowUtils.workflowReferenceFromCmGroup(group);
		return output;
	}

	private void saveWidgets(Task activityInstance, Map<String, Object> nativeValues) {
		for (WidgetData widgetData : activityInstance.getWidgets()) {
			if (widgetService.hasWidgetAction(widgetData.getType(), WIDGET_ACTION_SUBMIT)) {
//				Map<String, Object> params = checkNotNull((Map<String, Object>) allWidgetSubmission.get(widget.getId()),"missing submitted params for widget = %s",widget); TODO enable check once widget ui code is ready
//				Map<String, Object> widgetActionParams = firstNonNull((Map<String, Object>) allWidgetSubmission.get(widgetData.getId()), emptyMap());
				Widget widget = widgetService.widgetDataToWidget(widgetData, (Map<String, Object>) nativeValues); //TODO check context
				logger.debug("save data for widget = {}", widget);
				Map<String, Object> res = widgetService.executeWidgetAction(widget, WIDGET_ACTION_SUBMIT);
				nativeValues.putAll(res);
			}
		}
	}

	private Flow advanceActivity(Task activityInstance) {
		logger.info("advancing activity instance '{}' for process '{}'", activityInstance.getId(), activityInstance.getProcessInstance().getType().getName());

		Flow procInst = activityInstance.getProcessInstance();
		String processInstanceId = procInst.getFlowId();
		for (WidgetData activityWidget : activityInstance.getWidgets()) {
			if (widgetService.hasWidgetAction(activityWidget.getType(), WIDGET_ACTION_ADVANCE)) {
				Map<String, Object> context = SharkFlowVariablesSynchronizerImpl.fromWorkflowValues(remoteRepository.getProcessInstanceVariables(processInstanceId), typesConverter);
				Widget widget = widgetService.widgetDataToWidget(activityWidget, context);
				widgetService.executeWidgetAction(widget, WIDGET_ACTION_ADVANCE);
			}
		}
		actionExecutor().completeTask(processInstanceId, activityInstance.getId());
		return repository.getFlowCard(procInst);
	}

	/**
	 * It should extract CMProcessClass with findAllProcessClasses() but the new
	 * DAO is not here yet. If it wasn't for SQL, we would breathe hacks.
	 *
	 * @
	 */
	@Override
	public void sync() {
		logger.info("sync all processes");
		for (Process plan : classeRepository.getAllPlanClasses()) {
			if (plan.isSuperclass()) {
				logger.debug("plan class {} is superclass, no need to sync", plan);
			} else {
				syncProcess(plan);
			}
		}
	}

	private void syncProcess(Process processClass) {
		logger.info("sync plans for class = {}", processClass);
		Map<String, FlowInfo> activeWalkInfosById = queryWSOpenAndSuspended(processClass);
		Iterable<? extends Flow> activeWalks = repository.queryOpenAndSuspended(processClass);
		for (Flow walk : activeWalks) {
			logger.info("sync walk = {}", activeWalks);
			String walkId = walk.getFlowId();
			FlowInfo walkInfo = activeWalkInfosById.get(walkId);
			if (walkInfo == null) {
				logger.info("walk {} is missing walk info; this means it is out of sync, and will be removed", walk);
				removeOutOfSyncProcess(walk);
			} else {
				processSynchronizer.syncProcessStateAndActivities(walk, walkInfo);
			}
		}
	}

	private Map<String, FlowInfo> queryWSOpenAndSuspended(Process processClass) {
		Map<String, FlowInfo> wsInfo = Maps.newHashMap();
		String processDefinitionId = processClass.getPlanIdOrNull();
		if (processDefinitionId != null) {
			for (FlowInfo pis : remoteService.listOpenProcessInstances(processDefinitionId)) {
				wsInfo.put(pis.getFlowId(), pis);
			}
		}
		return wsInfo;
	}

	private void removeOutOfSyncProcess(Flow processInstance) {
		repository.updateFlowCard(processInstance, SimpleFlowData.builder().withStatus(ABORTED).build(), () -> remoteService.getProcessInstanceVariables(processInstance.getFlowId()));
	}

	private WorkflowActionExecutor actionExecutor() {
		return remoteService.executor().withCallback(new WorkflowActionExecutorCallback() {
			@Override
			public void startFlow(FlowInfo flowInfo) {
				logger.info("actionExecutor start flow = {}", flowInfo);
//				classeRepository.
//				x
				Classe classe = dao.getClasse(planService.getClassNameOrNull(flowInfo.getDefinitionId()));
				Flow flow = repository.createFlowCard(classe, SimpleFlowData.builder().withStatus(FlowStatus.OPEN).withInfo(flowInfo).build(), () -> remoteService.getProcessInstanceVariables(flowInfo.getFlowId()));
				processSynchronizer.syncProcessStateActivitiesAndVariables(flow, flowInfo);
			}

			@Override
			public void updateFlow(FlowInfo flowInfo) {
				logger.info("actionExecutor update flow = {}", flowInfo);
				Flow flow = repository.getFlowCardByPlanIdAndFlowId(SHARK, flowInfo.getPlanId(), flowInfo.getFlowId());
				if (flow != null) {
					processSynchronizer.syncProcessStateActivitiesAndVariables(flow, flowInfo);
				}
			}

		});
	}

	@Override
	public List<XpdlInfo> getXpdlInfosOrderByVersionDesc(String planClasseId) {
		Process classe = classeRepository.getProcessClassByName(planClasseId);
		String packageId = planService.getPackageId(classe);
		String processId = planService.getProcessDefinitionId(classe);
		if (isBlank(processId)) {
			return emptyList();
		} else {
			String[] versions = planService.getVersions(classe);
			List<XpdlInfo> list = asList(versions).stream().sorted(Ordering.natural().reverse().onResultOf(Integer::valueOf)).map((version) -> {//TODO check sorting
				String planId = PlanIdUtils.buildPlanId(packageId, version, processId);
				return XpdlInfoImpl.builder()
						.withDefault(false)
						.withLastUpdate(ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC))//TODO						
						.withPlanId(planId)
						.withVersion(version)
						.withProvider(getName())
						.build();
			}).sorted(Ordering.natural().reverse().onResultOf((i) -> Integer.valueOf(i.getVersion()))).collect(toList());
			if (!list.isEmpty()) {
				list.set(0, XpdlInfoImpl.copyOf(list.get(0)).withDefault(true).build());//TODO verify that first element is also default
			}
			return list;
		}
	}

	@Override
	public DataSource getXpdlForClasse(Process classe) {
		String version = PlanIdUtils.readPlanId(classe.getPlanId()).getVersion();
		return planService.getDefinition(classe.getName(), version);
	}

	@Override
	public XpdlInfo addXpdl(String processId, DataSource dataSource) {
		Process classe = classeRepository.getProcessClassByName(processId);
		planService.updateDefinition(classe, dataSource);
		return XpdlInfoImpl.builder()
				.withDefault(true)
				.withLastUpdate(CmDateUtils.now())//TODO			
				.withPlanId("_unsupported_")//TODO
				.withVersion("_unsupported_")//TODO
				.withProvider(getName())
				.build();
	}

	@Override
	public List<Task> getTaskList(Flow card) {
		List<String> activityInstanceIds = asList(card.get(ATTR_TASK_INSTANCE_ID, String[].class));
		return activityInstanceIds.stream().map((t) -> getTask(card, t)).collect(toList());
	}

	@Override
	public Task getTask(Flow card, String taskId) {
		List<String> activityInstanceIds = asList(card.get(ATTR_TASK_INSTANCE_ID, String[].class));
		List<String> activityDefinitionIds = asList(card.get(ATTR_TASK_DEFINITION_ID, String[].class));
		List<String> performers = asList(card.get(ATTR_NEXT_EXECUTOR, String[].class));

		checkArgument(activityInstanceIds.size() == activityDefinitionIds.size());
		checkArgument(activityInstanceIds.size() == performers.size());

		int index = activityInstanceIds.indexOf(taskId);
		checkArgument(index >= 0, "task not found for id = %s in card = %s", taskId, card);
		String taskDefinitionId = activityDefinitionIds.get(index);
		String taskPerformer = performers.get(index);

		TaskDefinition taskDefinition = card.getType().getTaskById(taskDefinitionId);
		Supplier<List<Widget>> taskWidgetSupplier = () -> getTaskWidgetForTaskAndWalkId(taskDefinition, card.getFlowId());

		return TaskImpl.builder()
				.withCard(card)
				.withTaskDefinition(taskDefinition)
				.withTaskId(taskId)
				.withFlowId(card.getFlowId())
				.withTaskPerformer(taskPerformer)
				.isAdvanceable(isAdvanceableByCurrentUser(taskPerformer))
				.withTaskWidgetSupplier(taskWidgetSupplier)
				.build();

	}

	private List<Widget> getTaskWidgetForTaskAndWalkId(TaskDefinition taskDefinition, String walkId) {
		Map<String, Object> workflowRawTypes = remoteRepository.getProcessInstanceVariables(walkId);
		Map<String, Object> convertedValues = SharkFlowVariablesSynchronizerImpl.fromWorkflowValues(workflowRawTypes, typesConverter);
//		List<WidgetData> widgets = widgetFactoryService.createWidgets(taskDefinition.getWidgets(), convertedValues); TODO
//		return widgets;
		return emptyList();
	}

	private boolean isAdvanceableByCurrentUser(String groupName) {//TODO duplicate code
		OperationUser operationUser = userSupplier.getUser();
		if (operationUser.hasAdminAccess()) {
			return true;
		} else {
			return operationUser.getGroupNames().contains(groupName);
		}
	}

}
