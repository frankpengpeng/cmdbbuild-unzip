/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.collect.Maps;
import static java.lang.String.format;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverFlowService;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.core.CompletedTaskImpl;
import org.cmdbuild.workflow.river.engine.core.RiverFlowImpl;
import org.cmdbuild.workflow.river.engine.lock.AquiredLock;
import org.springframework.stereotype.Component;
import org.cmdbuild.workflow.inner.WorkflowServiceDelegate;
import org.cmdbuild.workflow.inner.FlowCardRepository;
import org.cmdbuild.workflow.river.dao.ExtendedRiverPlanRepository;
import org.cmdbuild.workflow.river.engine.lock.RiverLockService;
import org.cmdbuild.workflow.model.AdvancedFlowStatus;
import static org.cmdbuild.workflow.WorkflowCommonConst.RIVER;
import org.cmdbuild.workflow.model.SimpleFlowAdvanceResponse;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.river.dao.FlowPersistenceService;
import org.cmdbuild.workflow.river.dao.converters.TaskConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.classNameOrVoid;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.workflow.core.utils.WorkflowUtils;
import static org.cmdbuild.workflow.core.utils.WorkflowUtils.getEntryTaskForCurrentUser;
import static org.cmdbuild.workflow.core.utils.XpdlUtils.xpdlToDatasource;
import org.cmdbuild.workflow.model.XpdlInfoImpl;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_GROUP_NAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_PERFORMER_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_USER_USERNAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_USER_VARIABLE;
import org.cmdbuild.workflow.model.XpdlInfo;
import org.cmdbuild.workflow.river.dao.ExtendedRiverPlanRepository.RiverPlanVersionInfo;
import static org.cmdbuild.workflow.river.dao.RiverPlanRepositoryImpl.ATTR_BIND_TO_CLASS;
import static org.cmdbuild.workflow.river.utils.WfRiverXpdlUtils.parseXpdlForCmdb;
import org.cmdbuild.workflow.model.WfReferenceImpl;
import org.cmdbuild.workflow.type.ReferenceType;
import org.cmdbuild.workflow.WorkflowTypeConverter;
import org.cmdbuild.widget.model.WidgetData;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_ACTION_SUBMIT;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.river.dao.RiverFlowConversionService;
import org.cmdbuild.workflow.river.dao.converters.TaskDefinitionConversionService;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import org.cmdbuild.auth.login.AuthenticationService;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_READ;
import org.cmdbuild.classe.access.UserCardAccess;
import org.cmdbuild.classe.access.UserCardService;
import static org.cmdbuild.common.Constants.USER_CLASS_NAME;
import org.cmdbuild.common.utils.PositionOf;
import org.cmdbuild.common.utils.PositionOfImpl;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NEXT_EXECUTOR;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_INSTANCE_ID;
import static org.cmdbuild.dao.core.q3.CompositeWhereOperator.OR;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import static org.cmdbuild.dao.core.q3.WhereOperator.INTERSECTS;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.workflow.core.dao.data.CardToFlowCardWrapperService;
import static org.cmdbuild.workflow.river.dao.converters.FlowConversionMode.CM_LEAN;
import static org.cmdbuild.workflow.river.utils.ClosedFlowUtils.buildTaskForClosedFlow;
import org.cmdbuild.workflow.model.TaskAttribute;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.workflow.WorkflowService.WorkflowVariableProcessingStrategy;
import static org.cmdbuild.workflow.river.dao.converters.FlowConversionMode.CM_FULL;

@Component
public class WorkflowServiceRiverImpl implements WorkflowServiceDelegate {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final WorkflowConfiguration workflowConfiguration;
    private final ExtendedRiverPlanRepository planRepository;
    private final RiverFlowService flowService;
    private final RiverFlowConversionService flowRepository;
    private final FlowCardRepository cardRepository;
    private final RiverLockService lockService;
    private final TaskConversionService taskConversionService;
    private final TaskDefinitionConversionService taskDefinitionConversionService;
    private final FlowPersistenceService persistenceService;
    private final WorkflowTypeConverter typeConverter;
    private final AuthenticationService authenticationService;
    private final OperationUserSupplier user;
    private final WidgetService widgetService;
    private final CardToFlowCardWrapperService wrapperService;
    private final UserCardService cardService;

    public WorkflowServiceRiverImpl(UserCardService cardService, CardToFlowCardWrapperService wrapperService, DaoService dao, WorkflowConfiguration workflowConfiguration, ExtendedRiverPlanRepository planRepository, RiverFlowService flowService, RiverFlowConversionService flowRepository, FlowCardRepository cardRepository, RiverLockService lockService, TaskConversionService taskConversionService, TaskDefinitionConversionService taskDefinitionConversionService, FlowPersistenceService persistenceService, WorkflowTypeConverter typeConverter, AuthenticationService authenticationService, OperationUserSupplier userSupplier, WidgetService widgetService) {
        this.workflowConfiguration = checkNotNull(workflowConfiguration);
        this.planRepository = checkNotNull(planRepository);
        this.flowService = checkNotNull(flowService);
        this.flowRepository = checkNotNull(flowRepository);
        this.cardRepository = checkNotNull(cardRepository);
        this.lockService = checkNotNull(lockService);
        this.taskConversionService = checkNotNull(taskConversionService);
        this.taskDefinitionConversionService = checkNotNull(taskDefinitionConversionService);
        this.persistenceService = checkNotNull(persistenceService);
        this.typeConverter = checkNotNull(typeConverter);
        this.authenticationService = checkNotNull(authenticationService);
        this.user = checkNotNull(userSupplier);
        this.widgetService = checkNotNull(widgetService);
        this.wrapperService = checkNotNull(wrapperService);
        this.dao = checkNotNull(dao);
        this.cardService = checkNotNull(cardService);
    }

    @Override
    public String getName() {
        return RIVER;
    }

    @Override
    public void abortProcessInstance(Flow flow) {
        RiverFlow riverFlow = flowRepository.cardToRiverFlow(flow);
        try (AquiredLock lock = lockService.aquireLock(riverFlow).aquired()) {
            riverFlow = flowService.terminateFlow(riverFlow);
            persistenceService.updateFlowCard(flow, riverFlow);
        }
    }

    @Override
    public void suspendProcessInstance(Flow flow) {
        RiverFlow riverFlow = flowRepository.cardToRiverFlow(flow);
        try (AquiredLock lock = lockService.aquireLock(riverFlow).aquired()) {
            riverFlow = flowService.suspendFlow(riverFlow);
            persistenceService.updateFlowCard(flow, riverFlow);
        }
    }

    @Override
    public void resumeProcessInstance(Flow flow) {
        RiverFlow riverFlow = flowRepository.cardToRiverFlow(flow);
        try (AquiredLock lock = lockService.aquireLock(riverFlow).aquired()) {
            riverFlow = flowService.resumeFlow(riverFlow);
            persistenceService.updateFlowCard(flow, riverFlow);
        }
    }

    @Override
    public List<Task> getTaskListForCurrentUserByClassIdAndCardId(String classId, Long cardId) {
        logger.debug("getTaskListForCurrentUser with classId = {} cardId = {}", classId, cardId);
        Flow card = cardRepository.getFlowCardByClasseIdAndCardId(classId, cardId);
        return taskConversionService.getTaskList(card);//TODO filter for user
    }

    @Override
    public PagedElements<Task> getTaskListForCurrentUserByClassIdSkipFlowData(String classId, DaoQueryOptions queryOptions) {
        Collection<String> groups = user.getActiveGroupNames();
        UserCardAccess cardAccess = cardService.getUserCardAccess(classId);
        CmdbFilter cardAccessFilter = cardAccess.getWholeClassFilter();
        queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).withFilter(queryOptions.getFilter().and(cardAccessFilter)).build();
        logger.debug("getTaskListForCurrentUserByClassIdSkipFlowData with classId = {} and groups = {}", classId, groups);
        PositionOf positionOf = null;
        if (queryOptions.hasPositionOf()) {
            long offset = firstNonNull(queryOptions.getOffset(), 0l);
            Long rowNumber = dao.selectRowNumber().where(ATTR_ID, EQ, queryOptions.getPositionOf()).then()
                    .selectExpr("_unnested_taskid", buildUnnestQuery(ATTR_TASK_INSTANCE_ID))
                    .selectExpr("_unnested_groupname", buildUnnestQuery(ATTR_NEXT_EXECUTOR))
                    .from(classId)
                    .orderBy(queryOptions.getSorter())
                    .where(queryOptions.getFilter())
                    .accept((q) -> {
                        if (!user.hasPrivileges(p -> p.hasPrivileges(RP_DATA_ALL_READ))) {
                            q.where(OR, (b) -> b.where("_unnested_groupname", IN, groups).where("PrevExecutors", INTERSECTS, groups));//TODO handle dummy task for closed processes
                        }
                    })
                    .build().getFirstRowNumberOrNull();
            if (rowNumber == null) {
                positionOf = PositionOfImpl.builder().withFoundCard(false).build();
            } else {
                long positionInPage = rowNumber % queryOptions.getLimit();
                long pageOffset = rowNumber - positionInPage;
                if (queryOptions.getGoToPage()) {
                    offset = pageOffset;
                }
                positionOf = PositionOfImpl.builder().withFoundCard(true)
                        .withPositionInPage(positionInPage)
                        .withPositionInTable(rowNumber)
                        .withPageOffset(pageOffset)
                        .withActualOffset(offset)
                        .build();
                queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).withOffset(offset).build();
            }
        }
        QueryBuilder query = dao.selectAll()
                .selectExpr("_unnested_taskid", buildUnnestQuery(ATTR_TASK_INSTANCE_ID))
                .selectExpr("_unnested_groupname", buildUnnestQuery(ATTR_NEXT_EXECUTOR))
                .from(classId)
                .accept((q) -> {
                    if (!user.hasPrivileges(p -> p.hasPrivileges(RP_DATA_ALL_READ))) {
                        q.where(OR, (b) -> b.where("_unnested_groupname", IN, groups).where("PrevExecutors", INTERSECTS, groups));//TODO handle dummy task for closed processes
                    }
                })
                .accept(cardAccess.addSubsetFilterMarkersToQueryVisitor())
                .withOptions(queryOptions);
        List<Task> tasks = query.run().stream().map((r) -> {
            Card card = r.toCard();
            card = cardAccess.addCardAccessPermissionsFromSubfilterMark(card);
            Flow flowCard = wrapperService.cardToFlowCard(card);
            String taskId = checkNotBlank(toStringOrNull(r.asMap().get("_unnested_taskid")));
            if (equal(taskId, UNNEST_EMPTY_VAL)) {
                return buildTaskForClosedFlow(flowCard);
            } else {
                return taskConversionService.getTask(flowCard, taskId, CM_LEAN);
            }
        }).collect(toList());
        int total;
        if (queryOptions.isPaged()) {
            total = dao.select(ATTR_ID)
                    .selectExpr("_unnested_taskid", buildUnnestQuery(ATTR_TASK_INSTANCE_ID))
                    .selectExpr("_unnested_groupname", buildUnnestQuery(ATTR_NEXT_EXECUTOR))
                    .select("PrevExecutors")
                    .selectCount()
                    .from(classId)
                    .where(query)
                    .getCount();
            return new PagedElements<>(tasks, total, positionOf);
        } else {
            return new PagedElements<>(tasks);
        }
    }

    public static final String UNNEST_EMPTY_VAL = "__empty__";

    private static String buildUnnestQuery(String attr) {
        attr = quoteSqlIdentifier(attr);
        return format("unnest(CASE cardinality(%s) WHEN 0 THEN ARRAY['%s'::varchar] ELSE %s END)", attr, UNNEST_EMPTY_VAL, attr);
    }

    @Override
    public Task getUserTask(Flow card, String userTaskId) {
        return taskConversionService.getTask(card, userTaskId);
    }

    @Override
    public FlowAdvanceResponse startProcess(Process process, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
        return new StartProcessOperation(process, (Map) vars, variableProcessingStrategy, advance).startProcess();
    }

    @Override
    public FlowAdvanceResponse updateProcess(Flow flowCard, String taskId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
        return new UpdateProcessOperation(flowCard, taskId, (Map) vars, variableProcessingStrategy, advance).updateProcess();
    }

    @Override
    public List<XpdlInfo> getXpdlInfosOrderByVersionDesc(String classId) {
        List<RiverPlanVersionInfo> list = planRepository.getPlanVersionsByClassIdOrderByCreationDesc(classId);
        List<XpdlInfo> res = list();
        for (int i = 0; i < list.size(); i++) {
            RiverPlanVersionInfo riverPlan = list.get(i);
            int ver = list.size() - i;
            XpdlInfo xpdlInfo = XpdlInfoImpl.builder()
                    .withDefault(i == 0)
                    .withLastUpdate(riverPlan.getLastUpdate())
                    .withVersion(Integer.toString(ver))
                    .withProvider(getName())
                    .withPlanId(riverPlan.getPlanId())
                    .build();
            res.add(xpdlInfo);
        }
        return res;
    }

    @Override
    public DataSource getXpdlForClasse(Process classe) {
        RiverPlan plan = planRepository.getPlanById(classe.getPlanId());
        String xpdl = plan.toXpdl();
        return xpdlToDatasource(xpdl, format("%s_%s", classe.getName(), plan.getId()));
    }

    @Override
    public XpdlInfo addXpdl(String expectedClassId, DataSource wrapAsDataSource) {
        String xpdlContent = readToString(wrapAsDataSource);
        RiverPlan riverPlan = parseXpdlForCmdb(xpdlContent);
        String xpdlClassId = riverPlan.getAttr(ATTR_BIND_TO_CLASS);
        checkArgument(equal(xpdlClassId, expectedClassId), "xpdl binding mismatch, xpdl bind to %s = %s", ATTR_BIND_TO_CLASS, xpdlClassId);
        riverPlan = planRepository.storePlan(riverPlan);
        String planId = riverPlan.getId();
        XpdlInfo xpdlInfo = getXpdlInfosOrderByVersionDesc(xpdlClassId).stream().filter((i) -> equal(i.getPlanId(), planId)).findFirst().get();
        logger.info("uploaded new xpdl version = {}", xpdlInfo);
        return xpdlInfo;
    }

    @Override
    public TaskDefinition getTaskDefinition(Flow flowCard, String taskId) {
        RiverPlan plan = planRepository.getPlanById(flowCard.getPlanId());
        RiverTask task = plan.getTask(taskId);
        return taskDefinitionConversionService.toTaskDefinition(task);
    }

    @Override
    public Map<String, Object> getFlowData(Flow flowCard) {
        RiverFlow flow = flowRepository.cardToRiverFlow(flowCard);
        return flow.getData();//TODO ensure that flow.getData contains all flow data
    }

    @Override
    public Task getTask(Flow flowCard, String taskId) {
        return getUserTask(flowCard, taskId);
    }

    @Override
    public List<Task> getTaskList(Flow flowCard) {
        return taskConversionService.getTaskList(flowCard);
    }

    @Override
    public Map<String, Object> getAllFlowData(String classId, long cardId) {
        Flow card = cardRepository.getFlowCardByClasseIdAndCardId(classId, cardId);
        return flowRepository.cardToRiverFlow(card, CM_FULL).getData();
    }

    private RiverFlow addCurrentPerformerDataInFlow(RiverFlow riverFlow, Task userTask) {
        OperationUser operationUser = user.getUser();//TODO the following lines are duplicated in shark impl; refactor and remove duplicate code
        Role performer = getTaskPerformer(userTask);
        ReferenceType groupAsReferenceType = typeConverter.rawValueToFlowValue(WorkflowUtils.workflowReferenceFromCmGroup(performer), ReferenceType.class);

        Map<String, Object> map = map(
                CURRENT_USER_USERNAME_VARIABLE, operationUser.getUsername(),
                CURRENT_GROUP_NAME_VARIABLE, performer.getName(),
                CURRENT_USER_VARIABLE, toReference(operationUser),
                CURRENT_PERFORMER_VARIABLE, groupAsReferenceType);
        logger.trace("add current performer data to flow, from current op user; data = \n\n{}\n", mapToLoggableStringLazy(map));
        return RiverFlowImpl.copyOf(riverFlow)
                .withData(map(riverFlow.getData()).with(map))
                .build();
    }

    private Role getTaskPerformer(Task userTask) {
        String taskPerformer = userTask.getPerformerName();
        return checkNotNull(authenticationService.getGroupWithNameOrNull(taskPerformer), "group not found for name =< %s > (task performer for task = %s)", taskPerformer, userTask);
    }

    @Nullable
    private ReferenceType toReference(OperationUser operationUser) {
        return typeConverter.rawValueToFlowValue(operationUser.getLoginUser().getId() == null ? null : new WfReferenceImpl(operationUser.getLoginUser().getIdNotNull(), USER_CLASS_NAME), ReferenceType.class);
    }

    private class StartProcessOperation extends ProcessOperation {

        private final Process process;
        private final RiverPlan riverPlan;
        private TaskDefinition entryTask;
        private String entryTaskId;

        public StartProcessOperation(Process process, Map<String, Object> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
            super(vars, variableProcessingStrategy, advance);
            this.process = checkNotNull(process);
            riverPlan = planRepository.getPlanById(process.getPlanId());
        }

        public FlowAdvanceResponse startProcess() {
            logger.info("start process for classe = {}", process);
            prepareEntryTask();
            createFlowInstance();
            try (AquiredLock lock = lockService.aquireLock(riverFlow).aquired()) {
                createCardAndUpdateFlowWithCardStuff();
                if (advance) {
                    advanceProcess();
                }
                updateFlowCard();
                return buildResponse(flow);
            }
        }

        private void prepareEntryTask() {
            entryTask = getEntryTaskForCurrentUser(process, user.getUser());
            entryTaskId = riverPlan.getEntryPointIdByTaskId(entryTask.getId());
        }

        private void createFlowInstance() {
            riverFlow = flowService.createFlow(riverPlan);
            riverFlow = setInitialDataInFlow(riverFlow, process);
        }

        private void createCardAndUpdateFlowWithCardStuff() {
            TaskDefinition taskDefinition = WorkflowUtils.getEntryTaskForCurrentUser(process, user.getUser());
            addTaskDataFromFormInFlow(vars, taskDefinition, widgetService.widgetDataToWidget(taskDefinition.getWidgets(), vars));
            createFlowCard();
            riverFlow = flowService.startFlow(riverFlow, entryTaskId);//note: we create flow card and then start flow to allow triggers on card creation, before flow start
            updateFlowCard();

            List<Task> taskList = taskConversionService.getTaskList(flow);
            checkArgument(taskList.size() == 1, "we expected exactly one task for flow = %s at this time, found = %s", riverFlow.getId(), taskList);
            task = getOnlyElement(taskList);

            addTaskDataFromFormInFlow(vars, task.getDefinition(), task.getWidgets());
            task = getOnlyElement(taskConversionService.getTaskList(flow));//TODO avoid this refresh, retrieve only task definition before 

            Map<String, Object> varsAndWidgetData = saveWidgets(task, riverFlow.getData());//TODO get data from flow, filtered for form; is this correct?
            addDataFromFormInFlow(varsAndWidgetData);
            updateFlowCard();
        }

        private void createFlowCard() {
            checkArgument(riverFlow != null && flow == null);
            flow = persistenceService.createFlowCard(riverFlow);
            riverFlow = flowRepository.cardToRiverFlow(flow);
        }

        private RiverFlow setInitialDataInFlow(RiverFlow riverFlow, org.cmdbuild.workflow.model.Process classe) {
            logger.debug("set initial data in flow = {}", riverFlow);
            CmMapUtils.FluentMap<String, Object> data = map();
            RiverPlan plan = riverFlow.getPlan();

            plan.getGlobalVariables().forEach((String key, RiverVariableInfo var) -> {
                Object value = var.getDefaultValue().orElse(typeConverter.defaultValueForFlowInitialization(var.getJavaType()));
                value = typeConverter.rawValueToFlowValue(value, var.getJavaType());
                data.put(key, value);
            });

            if (logger.isTraceEnabled()) {
                logger.trace("initial flow data = \n\n{}\n", mapToLoggableString(data));
            }

            return RiverFlowImpl.copyOf(riverFlow)
                    .withData(data)
                    .build();
        }

    }

    private class UpdateProcessOperation extends ProcessOperation {

        private final String taskId, flowId;

        public UpdateProcessOperation(Flow flowCard, String taskId, Map<String, Object> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
            super(vars, variableProcessingStrategy, advance);
            this.flow = checkNotNull(flowCard);
            this.taskId = checkNotBlank(taskId);
            flowId = flowCard.getFlowId();
        }

        public FlowAdvanceResponse updateProcess() {
            logger.info("update process for classeId = {} cardId = {}", flow.getType().getName(), flow.getCardId());
            try (AquiredLock lock = lockService.aquireLock(flowId).aquired()) {
                doUpdate();
                if (advance) {
                    advanceProcess();
                }
                return buildResponse(flow);
            }
        }

        private void doUpdate() {//TODO fix this method
            riverFlow = flowRepository.cardToRiverFlow(flow);
            task = taskConversionService.getTask(flow, taskId);
            addTaskDataFromFormInFlow(vars, task.getDefinition(), task.getWidgets());
            //TODO update flow card with river flow data
            task = taskConversionService.getTask(flow, taskId);//TODO avoid this refresh, retrieve only task definition before 
            Map<String, Object> varsAndWidgetData = saveWidgets(task, riverFlow.getData());//TODO get data from flow, filtered for form; is this correct?
            addDataFromFormInFlow(varsAndWidgetData);
            updateFlowCard();
        }

    }

    private abstract class ProcessOperation {

        protected RiverFlow riverFlow;
        protected Flow flow;
        protected Task task;
        protected final Map<String, Object> vars;
        protected final boolean advance;
        protected final WorkflowVariableProcessingStrategy variableProcessingStrategy;

        public ProcessOperation(Map<String, Object> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
            this.vars = checkNotNull(vars);
            this.variableProcessingStrategy = checkNotNull(variableProcessingStrategy);
            this.advance = advance;
        }

        protected void validateTaksParameters(Map<String, ?> vars, Task task) {
            if (workflowConfiguration.isUserTaskParametersValidationEnabled()) {
                task.getDefinition().getVariables().stream().filter(TaskAttribute::isMandatory).forEach((var) -> {
                    Object formValue = vars.get(var.getName()), flowValue = riverFlow.getData().get(var.getName());
                    checkArgument(!isBlank(toStringOrNull(formValue)) || !isBlank(toStringOrNull(flowValue)), "missing mandatory value for var = %s task = %s", var.getName(), task.getDefinition());
                });
            }
        }

        protected void addCurrentPerformerDataInFlowAndCompleteTask(Task userTask) {
            String taskId = userTask.getDefinition().getId();
            RiverTask riverTask = riverFlow.getPlan().getTask(taskId);
            riverFlow = addCurrentPerformerDataInFlow(riverFlow, userTask);
            riverFlow = flowService.completedTask(riverFlow, CompletedTaskImpl.of(riverFlow, riverTask));
            checkArgument(riverFlow.getBatchTasks().isEmpty(), "batch tasks are currently not supported");//TODO fix batch tasks
//            flowCard = cardRepository.getFlowCard(flowCard);
        }

        protected void addTaskDataFromFormInFlow(Map<String, Object> data, TaskDefinition taskDefinition, List<Widget> widgets) {
            logger.trace("adding data to flow, for task = {} raw (unfiltered) data = \n\n{}\n", taskDefinition.getId(), mapToLoggableStringLazy(data));
            Set<String> formVars = taskDefinition.getVariables().stream().map(TaskAttribute::getName).sorted().collect(toSet());
            logger.trace("variable processing strategy = {}, task form vars = {}", variableProcessingStrategy, formVars);
            Map dataToAdd = map().accept(m -> {
                switch (variableProcessingStrategy) {
                    case SET_ONLY_TASK_VARIABLES:
                        m.putAll(Maps.filterKeys(data, formVars::contains));
                        break;
                    case SET_ALL_CLASS_VARIABLES:
                        m.putAll(data);
                        break;
                    default:
                        throw unsupported("unsupported variable processing strategy = %s", variableProcessingStrategy);
                }
            }).accept((m) -> {
                widgets.stream().filter(Widget::hasOutputKey).forEach((w) -> {
                    Object value = data.get(w.getOutputKey());
                    if (w.hasOutputType()) {
                        value = typeConverter.cardValueToFlowValue(value, w.getOutputType());
                    }
                    m.put(w.getOutputKey(), value);
                });
            });
            logger.trace("this data is not included in task or widgets, so it will be discarded = \n\n{}\n", lazyString(() -> mapToLoggableString(map(data).withoutKeys(dataToAdd.keySet()))));
            addDataFromFormInFlow(dataToAdd);
        }

        protected void addDataFromFormInFlow(Map data) {
            logger.trace("adding data to flow, raw data = \n\n{}\n", mapToLoggableStringLazy(data));
            data = convertFormDataToFlow(riverFlow.getPlan(), data);
            logger.trace("adding data to flow, processed data = \n\n{}\n", mapToLoggableStringLazy(data));
            riverFlow = RiverFlowImpl.copyOf(riverFlow)
                    .withData(map(riverFlow.getData()).with(data))
                    .build();
        }

        protected void updateFlowCard() {
            checkNotNull(flow);
            checkNotNull(riverFlow);
            flow = persistenceService.updateFlowCard(flow, riverFlow);
            riverFlow = flowRepository.cardToRiverFlow(flow);
        }

        protected void advanceProcess() {
            try {
                doAdvanceProcess();
            } catch (Exception ex) {
                throw new WorkflowException(ex, "error advancing process = %s for task = %s", flow, task.getDefinition());
            }
        }

        protected void doAdvanceProcess() {
            logger.debug("advance flow, complete user task = {}", task);
            validateTaksParameters(vars, task);
            addCurrentPerformerDataInFlowAndCompleteTask(task);
            updateFlowCard();
        }

    }

    private Map<String, Object> convertFormDataToFlow(RiverPlan plan, Map<String, Object> data) {
        Map<String, Object> converted = map();
        data.forEach((key, value) -> {
            RiverVariableInfo varInfo = plan.getGlobalVariables().get(key);
            if (varInfo != null) {
                try {
                    value = typeConverter.rawValueToFlowValue(value, varInfo.getJavaType());
                } catch (Exception ex) {
                    throw new WorkflowException(ex, "error converting form value = %s to flow var = %s", value, varInfo);
                }
            } else {
                logger.warn("received form data {} = {} ({}) not defined in flow plan global variables; skipping conversion", key, abbreviate(value), classNameOrVoid(value));
            }
            converted.put(key, value);
        });
        return converted;
    }

    private Map<String, Object> saveWidgets(Task activityInstance, Map<String, ?> vars) {
        logger.debug("save data for widgets");
        Map<String, Object> varsAndWidgetData = map(vars);
        for (WidgetData widgetData : activityInstance.getWidgets()) {
            if (widgetService.hasWidgetAction(widgetData.getType(), WIDGET_ACTION_SUBMIT)) {
//				Map<String, Object> params = checkNotNull((Map<String, Object>) allWidgetSubmission.get(widget.getId()),"missing submitted params for widget = %s",widget); TODO enable check once widget ui code is ready
//				Map<String, Object> widgetActionParams = firstNonNull((Map<String, Object>) allWidgetSubmission.get(widgetData.getId()), emptyMap());
                Widget widget = widgetService.widgetDataToWidget(widgetData, (Map<String, Object>) vars); //TODO check context
                logger.debug("save data for widget = {}", widget);
                Map<String, Object> res = widgetService.executeWidgetAction(widget, WIDGET_ACTION_SUBMIT);
                varsAndWidgetData.putAll(res);
            }
        }
        return typeConverter.widgetValuesToFlowValues(varsAndWidgetData);
    }

    //TODO refactor cose, use command bean
    private FlowAdvanceResponse buildResponse(Flow flow) {
        logger.debug("build flow response for flow = {}", flow);
        boolean isCompleted = flow.isCompleted();
        List<Task> taskList = taskConversionService.getTaskList(flow);
        AdvancedFlowStatus status;
        if (isCompleted) {
            status = AdvancedFlowStatus.COMPLETED;
        } else if (taskList.isEmpty()) {
            status = AdvancedFlowStatus.PROCESSING_SCRIPT;
        } else {
            status = AdvancedFlowStatus.WAITING_FOR_USER_TASK;
        }
        return SimpleFlowAdvanceResponse.builder()
                .withFlowCard(flow)
                .withAdvancedFlowStatus(status)
                .withTasklist(taskList)
                .build();
    }
}
