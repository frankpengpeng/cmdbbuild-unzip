/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.collect.Streams;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.activation.DataSource;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.data2.impl.ProcessEntryFiller;
import org.cmdbuild.logic.data.access.resolver.AbstractSerializer;
import org.cmdbuild.logic.data.access.resolver.ForeignReferenceResolver;
import org.cmdbuild.workflow.WorkflowService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.cmdbuild.workflow.inner.WorkflowServiceDelegate;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.inner.FlowCardRepository;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.services.MinionStatus;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.workflow.core.inner.WorkflowServiceDelegates;
import org.cmdbuild.workflow.core.xpdl.XpdlTemplateService;
import org.cmdbuild.workflow.model.XpdlInfoImpl;
import org.cmdbuild.workflow.model.XpdlInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.widget.model.WidgetData;
import org.cmdbuild.workflow.inner.FlowMigrationService;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.inner.ProcessRepository;
import org.cmdbuild.services.MinionComponent;
import org.cmdbuild.services.MinionConfig;
import static org.cmdbuild.services.MinionConfig.MC_DISABLED;
import static org.cmdbuild.services.MinionConfig.MC_ENABLED;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import org.cmdbuild.workflow.FlowUpdatedEvent;
import org.cmdbuild.workflow.WorkflowEventService;
import org.cmdbuild.workflow.core.utils.WorkflowUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Component
@MinionComponent(name = "Workflow Engine", configBean = WorkflowConfiguration.class)
public class WorkflowServiceFacadeImpl implements WorkflowService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WorkflowConfiguration configuration;
    private final WorkflowServiceDelegates services;
    private final FlowCardRepository cardRepository;
    private final ProcessRepository classeRepository;
    private final WidgetService widgetService;
    private final XpdlTemplateService templateService;
    private final FlowMigrationService flowMigrationService;
    private final WorkflowEventService eventService;
    private final OperationUserSupplier userStore;

    public WorkflowServiceFacadeImpl(OperationUserSupplier userStore, WorkflowEventService eventService, WorkflowConfiguration configuration, WorkflowServiceDelegates services, FlowCardRepository cardRepository, ProcessRepository classeRepository, WidgetService widgetService, XpdlTemplateService templateService, FlowMigrationService flowMigrationService) {
        this.configuration = checkNotNull(configuration);
        this.services = checkNotNull(services);
        this.cardRepository = checkNotNull(cardRepository);
        this.classeRepository = checkNotNull(classeRepository);
        this.widgetService = checkNotNull(widgetService);
        this.templateService = checkNotNull(templateService);
        this.flowMigrationService = checkNotNull(flowMigrationService);
        this.eventService = checkNotNull(eventService);
        this.userStore = checkNotNull(userStore);
    }

    public MinionStatus getServiceStatus() {
        if (isWorkflowEnabled()) {
            return MS_READY;
        } else {
            return MS_DISABLED;
        }
    }

    public MinionConfig getServiceConfig() {
        return isWorkflowEnabled() ? MC_ENABLED : MC_DISABLED;
    }

    private void checkWorkflowEnabled() {
        checkArgument(isWorkflowEnabled(), "CM: operation not allowed: workflow service is not enabled");
    }

    private WorkflowServiceDelegate getDefault() {
        checkWorkflowEnabled();
        return services.getDefault();
    }

    private WorkflowServiceDelegate getService(String key) {
        checkWorkflowEnabled();
        return services.getService(key);
    }

    private WorkflowServiceDelegate getService(Process classe) {
        return getService(classe.getProviderOrDefault(configuration.getDefaultWorkflowProvider()));
    }

    private WorkflowServiceDelegate getService(Flow card) {
        return getService(card.getType());
    }

    private List<WorkflowServiceDelegate> getServicesDefaultFirst() {
        return services.getServicesDefaultFirst();
    }

    @Override
    public Map<String, Object> getAllFlowData(String classId, long cardId) {
        Flow flow = getFlowCard(classId, cardId);
        return getService(flow).getAllFlowData(classId, cardId);
    }

    @Override
    public TaskDefinition getEntryTaskForCurrentUser(String processId) {
        Process process = getProcess(processId);
        return WorkflowUtils.getEntryTaskForCurrentUser(process, userStore.getUser());
    }

    @Override
    public Process getProcess(String processClasseName) {
        return classeRepository.getProcessClassByName(processClasseName);
    }

    @Override
    public Collection<Process> getActiveProcessClasses() {
        return classeRepository.getAllPlanClasses().stream().filter(Process::isActive).collect(toList());//TODO check isActive filter
    }

    @Override
    public Collection<Process> getAllProcessClasses() {
        return classeRepository.getAllPlanClasses();
    }

    @Override
    public List<Task> getTaskListForCurrentUserByClassIdAndCardId(String classId, Long cardId) {
        Flow flowCard = getFlowCard(classId, cardId);
        return getService(flowCard).getTaskListForCurrentUserByClassIdAndCardId(classId, cardId);
    }

    @Override
    public PagedElements<Task> getTaskListForCurrentUserByClassIdSkipFlowData(String processId, DaoQueryOptions queryOptions) {
        return getService(getProcess(processId)).getTaskListForCurrentUserByClassIdSkipFlowData(processId, queryOptions);
    }

//    @Override
//    public PagedElements<UserFlowWithPosition> queryWithPosition(String className, DaoQueryOptions queryOptions, Iterable<Long> cardId) {
//        return cardRepository.queryWithPosition(className, queryOptions, cardId);
//    }
    @Override
    public Flow getFlowCardOrNull(Process classe, Long cardId) {
        return cardRepository.getFlowCardByPlanAndCardId(classe, cardId);
    }

    @Override
    public PagedElements<Flow> getUserFlowCardsByClasseIdAndQueryOptions(String className, DaoQueryOptions queryOptions) {
        return cardRepository.getUserCardsByClassIdAndQueryOptions(className, queryOptions);
    }

    @Override
    public PagedElements<Flow> getFlowCardsByClasseAndQueryOptions(Classe processClass, DaoQueryOptions queryOptions) {
        PagedElements<Flow> fetchedProcesses = getUserFlowCardsByClasseIdAndQueryOptions(processClass.getName(), queryOptions);
        Iterable<Flow> processes = resolve(fetchedProcesses); //TODO WHY WHY WHY this method behave differently than getFlowCardsByClasseIdAndQueryOptions ???
        return new PagedElements<>(processes, fetchedProcesses.totalSize());
    }

    private Iterable<Flow> resolve(Iterable<? extends Flow> fetchedProcesses) {
        return ForeignReferenceResolver.<Flow>newInstance() //
                .withEntries(fetchedProcesses) //
                .withEntryFiller(new ProcessEntryFiller()) //
                .withSerializer(new AbstractSerializer<Flow>() {
                }) //
                .build() //
                .resolve();
    }

    @Override
    public void sync() {
        getServicesDefaultFirst().forEach((d) -> d.sync());
    }

    @Override
    public boolean isWorkflowEnabledAndProcessRunnable(String className) {
        return configuration.isEnabled() && getProcess(className).isRunnable();
    }

    @Override
    public boolean isWorkflowEnabled() {
        return configuration.isEnabled();
    }

    @Override
    public String getDefaultProvider() {
        return getDefault().getName();
    }

    @Override
    public Task getUserTask(Flow card, String userTaskId) {
        return getService(card).getUserTask(card, userTaskId);
    }

    @Override
    public FlowAdvanceResponse startProcess(String processClassName, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
        Process classe = classeRepository.getProcessClassByName(processClassName);
        FlowAdvanceResponse response = getService(classe).startProcess(classe, vars, variableProcessingStrategy, advance);
        eventService.getEventBus().post(new FlowUpdatedEventImpl(response, advance));
        return response;
    }

    @Override
    public FlowAdvanceResponse updateProcess(String planClasseId, Long flowCardId, String taskId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
        Flow card = cardRepository.getFlowCardByClasseIdAndCardId(planClasseId, flowCardId);
        FlowAdvanceResponse response = getService(card).updateProcess(card, taskId, vars, variableProcessingStrategy, advance);
        eventService.getEventBus().post(new FlowUpdatedEventImpl(response, advance));
        return response;
    }

    @Override
    public FlowAdvanceResponse updateProcessWithOnlyTask(String classId, Long cardId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
        Flow card = cardRepository.getFlowCardByClasseIdAndCardId(classId, cardId);
        Task task = getOnlyElement(getTaskList(card));
        FlowAdvanceResponse response = getService(card).updateProcess(card, task.getId(), vars, variableProcessingStrategy, advance);
        eventService.getEventBus().post(new FlowUpdatedEventImpl(response, advance));
        return response;
    }

    @Override
    public void abortProcessInstance(Flow card) {
        getService(card).abortProcessInstance(card);
    }

    @Override
    public void suspendProcessInstance(Flow card) {
        getService(card).suspendProcessInstance(card);
    }

    @Override
    public void resumeProcessInstance(Flow card) {
        getService(card).resumeProcessInstance(card);
    }

    @Override
    public void suspendProcess(String classId, Long cardId) {
        suspendProcessInstance(getFlowCard(classId, cardId));
    }

    @Override
    public void resumeProcess(String classId, Long cardId) {
        resumeProcessInstance(getFlowCard(classId, cardId));
    }

    @Override
    public void abortProcess(String classId, Long cardId) {
        abortProcessInstance(getFlowCard(classId, cardId));
    }

    @Override
    public DataSource getXpdlTemplate(String planClasseId) {
        Process classe = classeRepository.getProcessClassByName(planClasseId);
        return templateService.getTemplate(classe);
    }

    @Override
    public List<XpdlInfo> getXpdlInfosOrderByVersionDesc(String planClasseId) {
        boolean[] foundDefault = {false};
        List<XpdlInfo> infos = getServicesDefaultFirst().stream()
                .map((d) -> (Iterable<XpdlInfo>) d.getXpdlInfosOrderByVersionDesc(planClasseId))
                .reduce((l1, l2) -> Iterables.concat(l1, l2)).map(Streams::stream).orElse(Stream.empty())
                .map((info) -> {
                    if (info.isDefault()) {
                        if (foundDefault[0]) {
                            return XpdlInfoImpl.copyOf(info).withDefault(false).build();
                        } else {
                            foundDefault[0] = true;
                        }
                    }
                    return info;
                })
                .collect(toList());
        return infos;
    }

    @Override
    public DataSource getXpdlByClasseIdAndPlanId(String classId, String planId) {
        Process classe = classeRepository.getPlanClasseByClassAndPlanId(classId, planId);
        checkArgument(equal(classId, classe.getName()), "planId = %s does not bind to class = %s", planId, classId);
        return getService(classe).getXpdlForClasse(classe);
    }

    @Override
    public XpdlInfo addXpdl(String classId, String provider, DataSource dataSource) {
        return getService(provider).addXpdl(classId, dataSource);
    }

    @Override
    public XpdlInfo addXpdl(String classId, DataSource dataSource) {
        return getDefault().addXpdl(classId, dataSource);
    }

    @Override
    public List<WidgetData> getWidgetsForUserTask(String classeId, Long cardId, String taskId) {
        Flow flowCard = getFlowCard(classeId, cardId);
        TaskDefinition taskDefinition = getTaskDefinition(flowCard, taskId);
        Map<String, Object> flowData = getFlowData(flowCard);
//		return widgetService.createWidgets(taskDefinition.getWidgets(), flowData); TODO
        return emptyList();
    }

    private TaskDefinition getTaskDefinition(Flow flowCard, String taskId) {
        return getService(flowCard).getTaskDefinition(flowCard, taskId);
    }

    private Map<String, Object> getFlowData(Flow flowCard) {
        return getService(flowCard).getFlowData(flowCard);
    }

    @Override
    public Task getTask(Flow flowCard, String taskId) {
        return getService(flowCard).getTask(flowCard, taskId);
    }

    @Override
    public Collection<Task> getTaskList(Flow flowCard) {
        return getService(flowCard).getTaskList(flowCard);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)//TODO check this
    public void migrateFlowInstancesToNewProvider(String classId, DataSource newXpdlDataSource) {
        addXpdl(classId, newXpdlDataSource);
        flowMigrationService.migrateFlowInstancesToNewProvider(classId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW) //TODO check this
    public void migrateFlowInstancesToNewProviderWithExistingXpdl(String classId) {
        flowMigrationService.migrateFlowInstancesToNewProviderWithExistingXpdl(classId);
    }

    private static class FlowUpdatedEventImpl implements FlowUpdatedEvent {

        private final boolean isAdvanced;
        private final FlowAdvanceResponse response;

        public FlowUpdatedEventImpl(FlowAdvanceResponse response, boolean isAdvanced) {
            this.isAdvanced = isAdvanced;
            this.response = checkNotNull(response);
        }

        @Override
        public boolean isAdvanced() {
            return isAdvanced;
        }

        @Override
        public FlowAdvanceResponse getAdvanceResponse() {
            return response;
        }

    }
}
