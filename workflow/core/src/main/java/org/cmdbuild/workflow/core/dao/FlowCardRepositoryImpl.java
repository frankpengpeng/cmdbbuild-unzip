package org.cmdbuild.workflow.core.dao;

import org.cmdbuild.workflow.core.dao.data.CardToFlowCardWrapperService;
import static org.cmdbuild.workflow.model.FlowStatus.OPEN;
import static org.cmdbuild.workflow.model.FlowStatus.SUSPENDED;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.query.CMQueryRow;
import org.cmdbuild.lookup.Lookup;
import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import org.apache.commons.lang3.Validate;
import org.cmdbuild.common.utils.PositionOf;
import org.cmdbuild.common.utils.PositionOfImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.core.TaskEasytemplateProcessorFactory;
import org.cmdbuild.workflow.core.LookupHelper;
import org.springframework.stereotype.Component;
import org.cmdbuild.workflow.model.FlowInfo;
import org.cmdbuild.workflow.model.FlowData;
import org.cmdbuild.workflow.inner.FlowCardRepository;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.TaskInfo;
import org.cmdbuild.workflow.model.TaskPerformer;
import static org.cmdbuild.workflow.core.utils.TaskPerformerExpressionProcessorUtils.getPerformersFromExpression;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NEXT_EXECUTOR;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PLAN_INFO;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PREV_EXECUTORS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_DEFINITION_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_INSTANCE_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.FLOW_ATTR_DATA;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.inner.ProcessRepository;
import org.cmdbuild.workflow.model.FlowStatus;

@Component
public class FlowCardRepositoryImpl implements FlowCardRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final LookupHelper lookupHelper;
    private final TaskEasytemplateProcessorFactory templateResolverFactory;
    private final CardToFlowCardWrapperService wrapperService;
    private final ProcessRepository planClasseRepository;

    public FlowCardRepositoryImpl(DaoService dao, LookupHelper lookupHelper, TaskEasytemplateProcessorFactory templateResolverFactory, CardToFlowCardWrapperService wrapperService, ProcessRepository planClasseRepository) {
        this.dao = checkNotNull(dao);
        this.lookupHelper = checkNotNull(lookupHelper);
        this.templateResolverFactory = checkNotNull(templateResolverFactory);
        this.wrapperService = checkNotNull(wrapperService);
        this.planClasseRepository = checkNotNull(planClasseRepository);
    }

    @Override
    public Flow create(Flow flow) {
        Card card = flowToCard(flow);
        card = dao.create(card);
        return cardToFlowCard(card);
    }

    @Override
    public Flow update(Flow flow) {
        Card card = flowToCard(flow);
        card = dao.update(card);
        return cardToFlowCard(card);
    }

    @Override
    public List<Flow> getCardsByFlowId(String classId, Collection<String> flowIds) {
        checkArgument(!isNullOrEmpty(flowIds));
        List<Card> cards = dao.selectAll().from(classId).where(ATTR_FLOW_ID, IN, flowIds).getCards();
        checkArgument(cards.size() == flowIds.size(), "result mismatch, expected %s cards but found %s cards, for ids = %s", flowIds.size(), cards.size(), Joiner.on(",").join(flowIds));
        return cards.stream().map(this::cardToFlowCard).collect(toList());
    }

    @Override
    public Flow createFlowCard(Classe classe, FlowData flowData, Supplier<Map<String, Object>> processInstanceVariablesSupplier) {
        checkNotNull(flowData, "flow data cannot be null");
        FlowInfo flowInfo = flowData.getFlowInfo();
        logger.info("creating process instance of '{}' '{}'", flowInfo.getPackageId(), flowInfo.getDefinitionId());
        Process process = planClasseRepository.classToPlanClasse(classe);
        return createFlowCard(process, flowInfo, flowData, processInstanceVariablesSupplier);
    }

    @Override
    public Flow createFlowCard(Process processClass, FlowInfo flowInfo, FlowData flowData, Supplier<Map<String, Object>> processInstanceVariablesSupplier) {
        logger.info("creating process instance for class '{}'", processClass);
        Card card = new WorkflowUpdateHelper(processClass, processInstanceVariablesSupplier)
                .withFlowInfo(flowInfo)
                .withFlowData(flowData)
                .build();
        return cardToFlowCard(dao.create(card));
    }

    @Override
    public Flow updateFlowCard(Flow processInstance, FlowData processData, Supplier<Map<String, Object>> processInstanceVariablesSupplier) {
        logger.info("updating process instance for class '{}' and id '{}'", processInstance.getType().getName(), processInstance.getCardId());
        Card card = getProcessCard(processInstance);
        Process plan = planClasseRepository.classToPlanClasse(card.getType());
        card = new WorkflowUpdateHelper(plan, processInstanceVariablesSupplier)
                .withCard(card)
                .withFlowData(processData)
                .build();
        return cardToFlowCard(dao.update(card));
    }

    @Override
    public Flow updateThisFlowCard(Flow card, FlowData flowData) {
        return cardToFlowCard(dao.update(new WorkflowUpdateHelper(card.getType(), () -> emptyMap())
                .withCard(card)
                .withFlowData(flowData)
                .build()));
    }

    @Override
    public Flow getFlowCard(Flow processInstance) {
        logger.debug("getting process instance for class '{}' and card id '{}'", processInstance.getType(), processInstance.getCardId());
        return cardToFlowCard(getProcessCard(processInstance));
    }

    @Override
    public Flow getFlowCardByPlanAndCardId(Process classe, Long cardId) {
        logger.debug("getting process instance for class '{}' and card id '{}'", classe, cardId);
        return cardToFlowCard(getProcessCard(classe, cardId));
    }

    @Override
    public Flow getFlowCardByPlanIdAndFlowId(String provider, String planId, String flowId) {
        Process plan = planClasseRepository.getPlanClasseByProviderAndPlanId(provider, planId);
        return cardToFlowCard(getProcessCard(plan, flowId));
    }

    @Override
    public Flow getFlowCardByClasseIdAndCardId(String classeName, Long cardId) {
        Process plan = classToPlan(dao.getClasse(classeName));
        return cardToFlowCard(getProcessCard(plan, cardId));
    }

    @Override
    public Iterable<? extends Flow> queryOpenAndSuspended(Process processClass) {
        logger.debug("getting all opened and suspended process instances for class '{}'", processClass);
        Optional<Lookup> open = lookupHelper.lookupForState(OPEN);
        Optional<Lookup> suspended = lookupHelper.lookupForState(SUSPENDED);
        Object[] ids = new Long[]{open.isPresent() ? open.get().getId() : null,
            suspended.isPresent() ? suspended.get().getId() : null};
        logger.debug("lookup ids are '{}'", ids);
        return dao.selectAll().from(processClass).where(ATTR_FLOW_STATUS, IN, ids).getCards().stream().map(toProcessInstanceOf(processClass)).collect(toList());
    }

    private Function<Card, Flow> toProcessInstanceOf(Process processClass) {
        logger.debug("transforming from '{}' to '{}'", CMQueryRow.class, Flow.class);
        return this::cardToFlowCard;
    }

    @Override
    public PagedElements<Flow> getUserCardsByClassIdAndQueryOptions(String className, DaoQueryOptions queryOptions) {
        //TODO add user filter (?)
        List<Flow> list = dao.selectAll().from(className).withOptions(queryOptions).getCards().stream().map(this::cardToFlowCard).collect(toList());
        PositionOf positionOf = null;
        if (queryOptions.hasPositionOf()) {
            long offset = queryOptions.getOffset();
            Long rowNumber = dao.selectRowNumber().where(ATTR_ID, EQ, queryOptions.getPositionOf()).then()
                    .from(className)
                    .orderBy(queryOptions.getSorter())
                    .where(queryOptions.getFilter())
                    .build().getRowNumberOrNull();
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
        int count;
        if (queryOptions.isPaged()) {
            count = dao.selectCount()
                    .from(className)
                    .where(queryOptions.getFilter())
                    .getCount();
        } else {
            count = list.size();
        }
        return new PagedElements<>(list, count, positionOf);
    }

    private Process classToPlan(Classe classe) {
        return planClasseRepository.classToPlanClasse(classe);
    }

    private Flow cardToFlowCard(Card card) {
        return wrapperService.cardToFlowCard(card);
    }

    private Card flowToCard(Flow flow) {
        FlowStatus flowStatus = flow.getStatus();
        Lookup flowStatusLookup = lookupHelper.lookupForState(flowStatus).get();
        return CardImpl.copyOf(flow)
                .addAttribute(ATTR_FLOW_STATUS, flowStatusLookup.getId())
                .build();
    }

    private Card getProcessCard(Flow processInstance) {
        return getProcessCard(processInstance.getType(), processInstance.getCardId());
    }

    private Card getProcessCard(Process classe, long cardId) {
        logger.debug("getting process card for class = {} and card id = {}", classe, cardId);
        return dao.selectAll().from(classe).where(ATTR_ID, EQ, cardId).getCard();
    }

    private Card getProcessCard(Process processClass, String flowId) {
        logger.debug("getting process card for class '{}' and process instance id '{}'", processClass, flowId);
        return dao.selectAll().from(processClass).where(ATTR_FLOW_ID, EQ, flowId).getCard();
    }

    private class WorkflowUpdateHelper {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private static final String UNRESOLVABLE_PARTICIPANT_GROUP = EMPTY;

        private final Process process;
        private final Map<String, Object> cardDefinition = map();
        private final Supplier<Map<String, Object>> processInstanceVariablesSupplier;

        private Card card;
        private FlowInfo flowInfo;
        private FlowData flowData;

        private String processInstanceId;
        private String code;
        private String uniqueProcessDefinition;
        private final List<String> activityInstanceIds = newArrayList();
        private final List<String> activityDefinitionIds = newArrayList();
        private final List<String> currentActivityPerformers = newArrayList();
        private List<String> allActivityPerformers = newArrayList();

        private WorkflowUpdateHelper(Process processClass, Supplier<Map<String, Object>> processInstanceVariablesSupplier) {
            this.process = checkNotNull(processClass);
//			this.cardDefinition.putAll(cardDefinition);
            this.processInstanceVariablesSupplier = checkNotNull(processInstanceVariablesSupplier);

        }

        public WorkflowUpdateHelper withCard(Card card) {
            this.card = card;
            return this;
        }

        public WorkflowUpdateHelper withFlowInfo(FlowInfo flowInfo) {
            this.flowInfo = flowInfo;
            return this;
        }

        public WorkflowUpdateHelper withFlowData(FlowData flowData) {
            this.flowData = flowData;
            return this;
        }

        public Card build() {
            if (card != null) {
                logger.debug("copy values from old card");
                this.code = String.class.cast(card.getCode());
                this.uniqueProcessDefinition = card.get(ATTR_PLAN_INFO, String.class);
                this.processInstanceId = card.get(ATTR_FLOW_ID, String.class);
                this.activityInstanceIds.addAll(asList(card.get(ATTR_TASK_INSTANCE_ID, String[].class)));
                this.activityDefinitionIds.addAll(asList(card.get(ATTR_TASK_DEFINITION_ID, String[].class)));
                this.currentActivityPerformers.addAll(asList(card.get(ATTR_NEXT_EXECUTOR, String[].class)));
                this.allActivityPerformers.addAll(asList(card.get(ATTR_PREV_EXECUTORS, String[].class)));
                card = CardImpl.copyOf(card).build();
            } else {
                logger.debug("old card not set, setting default values");
                this.processInstanceId = flowInfo.getFlowId();
                card = CardImpl.buildCard(process, emptyMap());
            }

            logger.debug("got old activity instance ids = {}", (Object) activityInstanceIds);
            logger.debug("got old activity definition ids = {}", (Object) activityDefinitionIds);
            logger.debug("got old current activity performers = {}", (Object) currentActivityPerformers);
            logger.debug("got old all activity performers = {}", (Object) allActivityPerformers);

            copyValuesAndTasksFromFlowData();

            logger.debug("save card");
            // FIXME operation user must be always valid
//			OperationUser operationUser = userSupplier.getUser();
//			if (operationUser.hasDefaultGroup()) {
//				cardDefinition.setUser(operationUser.getAuthenticatedUser().getUsername());
//			} else if (operationUser.getPrivilegeContext() instanceof SystemPrivilegeContext) {
//				cardDefinition.setUser("system");
//			}
            cardDefinition.put(ATTR_CODE, code);
            cardDefinition.put(ATTR_PLAN_INFO, uniqueProcessDefinition);
            cardDefinition.put(ATTR_FLOW_ID, processInstanceId);
            cardDefinition.put(ATTR_TASK_INSTANCE_ID, activityInstanceIds.toArray(new String[]{}));
            cardDefinition.put(ATTR_TASK_DEFINITION_ID, activityDefinitionIds.toArray(new String[]{}));
            cardDefinition.put(ATTR_NEXT_EXECUTOR, currentActivityPerformers.toArray(new String[]{}));
            cardDefinition.put(ATTR_PREV_EXECUTORS, allActivityPerformers.toArray(new String[]{}));

            logger.debug("save new task list = {}", activityInstanceIds);
            logger.debug("save new task definitions = {}", activityDefinitionIds);
            logger.debug("save new task performers = {}", currentActivityPerformers);
            logger.debug("save new all activity performers = {}", (Object) allActivityPerformers);

            return CardImpl.copyOf(card).addAttributes(cardDefinition).build();
        }

        private void copyValuesAndTasksFromFlowData() {
            if (flowData != null) {
                logger.info("filling process card");
                if (flowData.getStatus() != null) {
                    logger.debug("updating state");
                    Optional<Lookup> lookup = lookupHelper.lookupForState(flowData.getStatus());
                    Object id;
                    if (lookup.isPresent()) {
                        id = lookup.get().getId();
                    } else {
                        logger.warn("flow status lookup not found for status = {}", flowData.getStatus());
                        id = null;
                    }
                    cardDefinition.put(ATTR_FLOW_STATUS, id);
                }
                if (flowData.getFlowInfo() != null) {
                    logger.debug("updating process instance info");
                    FlowInfo info = flowData.getFlowInfo();
                    String planId = info.getPlanId();
                    uniqueProcessDefinition = planId;
                }
                if (flowData.values() != null) {
                    logger.debug("updating values from flow to card");

                    Map<String, Object> attributesNotInCard = map(), systemAttributes = map(), attributesToUpdate = map();

                    flowData.values().entrySet().stream().sorted(Ordering.natural().onResultOf(Map.Entry::getKey)).forEach((entry) -> {
                        String key = entry.getKey();
                        Attribute attribute = process.getAttributeOrNull(key);
                        if (attribute == null) {
                            attributesNotInCard.put(key, entry.getValue());
                        } else if (isSystemAttrAndShouldBeSkipped(attribute)) {
                            systemAttributes.put(key, entry.getValue());
                        } else {
                            attributesToUpdate.put(key, entry.getValue());
                        }
                    });

                    if (logger.isTraceEnabled()) {
                        logger.trace("values updated in card:\n\n{}\n", mapToLoggableString(attributesToUpdate));
                        logger.trace("flow values that are system attributes in card, not updated:\n\n{}\n", mapToLoggableString(systemAttributes));
                        logger.trace("flow values not present in card, not updated:\n\n{}\n", mapToLoggableString(attributesNotInCard));
                    }

                    attributesToUpdate.forEach((key, value) -> cardDefinition.put(key, value));

                }
                if (flowData.hasTasksToSet()) {
                    logger.debug("update task list");
                    List<TaskInfo> taskList = flowData.getTasksToSet();
                    setTasks(taskList);
                    logger.debug("current task list = {}", activityInstanceIds);
                }
                if (flowData.hasTasksToAdd()) {
                    flowData.getTasksToAdd().forEach((taskInfo) -> {
                        logger.debug("adding task = {}", taskInfo);
                        addTask(taskInfo);
                    });
                }
            }
        }

        private void setTasks(List<TaskInfo> taskList) {
            Map<String, TaskInfo> newTasksById = Maps.uniqueIndex(taskList, TaskInfo::getTaskId);

            Set<String> newTaskIds = newTasksById.keySet();

            Set<String> oldTaskIds = newLinkedHashSet(activityInstanceIds);

            logger.debug("update task list, current tasks = {}, new tasks = {}", oldTaskIds, newTaskIds);

            Sets.difference(oldTaskIds, newTaskIds).forEach((taskToRemove) -> {
                removeTask(taskToRemove);
            });

            Sets.difference(newTaskIds, oldTaskIds).stream().map((taskToAdd) -> newTasksById.get(taskToAdd)).forEach((taskInfo) -> {
                addTask(taskInfo);
            });
        }

        private void addTask(TaskInfo taskInfo) {
            logger.debug("add task = {}", taskInfo);
            Validate.notNull(taskInfo);
            Validate.notNull(taskInfo.getTaskId());
            String participantGroup = getActivityParticipantGroup(taskInfo);
            if (!equal(participantGroup, UNRESOLVABLE_PARTICIPANT_GROUP)) {
                activityInstanceIds.add(taskInfo.getTaskId());
                activityDefinitionIds.add(taskInfo.getTaskDefinitionId());

                currentActivityPerformers.add(participantGroup);
                allActivityPerformers = newArrayList(newTreeSet(concat(allActivityPerformers, singletonList(participantGroup))));
                updateCodeFromFirstTaskInfo();
            }
        }

        private void removeTask(String taskId) {
            logger.debug("remove task = {}", taskId);
            int index = activityInstanceIds.indexOf(taskId);
            activityInstanceIds.remove(index);
            activityDefinitionIds.remove(index);
            currentActivityPerformers.remove(index);
            //TODO update allActivityPerformers ???

            updateCodeFromFirstTaskInfo();
        }

        private String getActivityParticipantGroup(TaskInfo activityInfo) {
            TaskDefinition activity = process.getTaskById(activityInfo.getTaskDefinitionId());
//			TaskDefinition activity = processDefinitionManager.getTaskDefinition(processInstance, activityInfo.getTaskDefinitionId());
            TaskPerformer performer = activity.getFirstNonAdminPerformer();
            String group;
            switch (performer.getType()) {
                case ROLE:
                    group = performer.getValue();
                    break;
                case EXPRESSION:
                    String expression = performer.getValue();
                    Set<String> names = getPerformersFromExpression(templateResolverFactory.templateResolver(), processInstanceVariablesSupplier.get(), expression);

                    if (activityInfo.getParticipantList().isEmpty()) {
                        /*
				 * an arbitrary expression in a non-starting activity, so should
				 * be a single name
                         */
                        Iterator<String> namesItr = names.iterator();
                        group = namesItr.hasNext() ? namesItr.next() : UNRESOLVABLE_PARTICIPANT_GROUP;
                    } else {
                        String maybeParticipantGroup = activityInfo.getParticipantList().iterator().next();
                        group = names.contains(maybeParticipantGroup) ? maybeParticipantGroup : UNRESOLVABLE_PARTICIPANT_GROUP;
                    }
                    break;
                default:
                    group = UNRESOLVABLE_PARTICIPANT_GROUP;
            }
            return group;
        }

        private void updateCodeFromFirstTaskInfo() {
            code = buildFlowCardCode(activityDefinitionIds, process);
        }
    }

    public static boolean isSystemAttrAndShouldBeSkipped(Attribute attribute) {
        return attribute.hasNotServiceListPermission() || set(FLOW_ATTR_DATA, ATTR_FLOW_STATUS).contains(attribute.getName());
    }

    @Nullable
    public static String buildFlowCardCode(List<String> activityDefinitionIds, Process process) {
        List<String> activities = activityDefinitionIds;
        if (activities.isEmpty()) {
            return null;
        } else {
            String taskId = activities.get(0);
            TaskDefinition taskDefinition = process.getTaskById(taskId);
            String label = defaultString(taskDefinition.getDescription());
            if (size(activities) > 1) {
                return format("%s, ...", label);
            } else {
                return label;
            }
        }
    }

}
