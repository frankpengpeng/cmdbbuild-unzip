/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.migration;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Ordering;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import javax.inject.Provider;
import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NEXT_EXECUTOR;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PLAN_INFO;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_DEFINITION_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_INSTANCE_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.FLOW_ATTR_DATA;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverFlow.FlowStatus;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlUtils.buildStepIdFromParentActivityIdAndActivityId;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlUtils.buildUserTaskId;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.workflow.inner.FlowMigrationService;
import static org.cmdbuild.workflow.WorkflowCommonConst.RIVER;
import org.cmdbuild.workflow.WorkflowTypeConverter;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.river.dao.ExtendedRiverPlanRepository;
import static org.cmdbuild.workflow.river.utils.FlowDataSerializerUtils.serializeRiverFlowData;
import static org.cmdbuild.workflow.river.utils.WfRiverXpdlUtils.riverPlanIdToLegacyUniqueProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.workflow.WorkflowCommonConst.SHARK;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.core.LookupHelper;
import org.cmdbuild.workflow.migration.SharkDbUtils.SharkHelper;
import org.cmdbuild.workflow.migration.SharkDbUtils.XpdlData;
import org.cmdbuild.workflow.model.PlanInfo;
import org.cmdbuild.workflow.model.PlanInfoImpl;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.isCompleted;

@Component
public class FlowMigrationServiceImpl implements FlowMigrationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GlobalConfigService configService;
    private final ExtendedRiverPlanRepository riverPlanRepository;
    private final DaoService dao;
    private final WorkflowTypeConverter typeConverter;
    private final LookupHelper lookupHelper;
    private final Provider<WorkflowService> workflowService;

    public FlowMigrationServiceImpl(LookupHelper lookupHelper, Provider<WorkflowService> workflowService, GlobalConfigService configService, ExtendedRiverPlanRepository riverPlanRepository, DaoService dao, WorkflowTypeConverter typeConverter) {
        this.configService = checkNotNull(configService);
        this.riverPlanRepository = checkNotNull(riverPlanRepository);
        this.dao = checkNotNull(dao);
        this.typeConverter = checkNotNull(typeConverter);
        this.workflowService = checkNotNull(workflowService);
        this.lookupHelper = checkNotNull(lookupHelper);
    }

    @Override
    public void migrateFlowInstancesToNewProvider(String classId) {
        migrateFlowInstancesToNewProvider(classId, false);
    }

    @Override
    public void migrateFlowInstancesToNewProviderWithExistingXpdl(String classId) {
        migrateFlowInstancesToNewProvider(classId, true);
    }

    private void migrateFlowInstancesToNewProvider(String classId, boolean copyExistingXpdl) {
        Classe classe = dao.getClasse(classId);
        if (equal(classe.getMetadata().getFlowProviderOrNull(), RIVER)) {
            logger.warn(marker(), "process already migrated to river, nothing to do");
        } else {
            checkArgument(equal(classe.getMetadata().getFlowProviderOrNull(), SHARK), "cannot migrate process = %s with provider =< %s >: unsupported source provider", classe, classe.getMetadata().getFlowProviderOrNull());
            SharkToRiverMigrationProcess helper = new SharkToRiverMigrationProcess(classe);
            if (copyExistingXpdl) {
                helper.copyExistingXpdlToRiver();
            }
            if (dao.selectCount().from(classe).getCount() == 0) {
                updateProcessProvider(classe);
            } else {
                helper.migrateFlowInstancesFromSharkToRiver();
            }
        }
    }

    private void updateProcessProvider(Classe process) {
        logger.info("update process = {}, set default provider = river", process);
        dao.updateClass(ClassDefinitionImpl.copyOf(process).withMetadata(ClassMetadataImpl.copyOf(process.getMetadata()).withFlowProvider(RIVER).build()).build());
    }

    private class SharkToRiverMigrationProcess {

        private final Classe process;
        private RiverPlan riverPlan;
        private final SharkHelper sharkHelper;

        private final Set<Card> flowsWithError = set();
        private final List<Card> preparedCards = list();

        public SharkToRiverMigrationProcess(Classe process) {
            logger.info("preparing shark to river migration process");
            this.process = checkNotNull(process);
            sharkHelper = SharkDbUtils.sharkHelper(configService::getString);
        }

        public void copyExistingXpdlToRiver() {
            PlanInfo planInfo = getFlowList().stream().map(f -> f.get(ATTR_PLAN_INFO, String.class)).filter(StringUtils::isNotBlank).distinct().map(PlanInfoImpl::deserialize).sorted(Ordering.natural().onResultOf(PlanInfo::getVersionInt).reversed()).findFirst().orElse(null);
            if (planInfo == null) {
                logger.warn("unable to copy existing xpdl: no process active from which to get plan info");
            } else {
                logger.info("copying existing xpdl from shark for id =< {} >", planInfo.serialize());
                XpdlData xpdlData = sharkHelper.getXpdlDataByPackageAndVersion(planInfo.getPackageId(), planInfo.getVersion());
                workflowService.get().addXpdl(process.getName(), RIVER, newDataSource(xpdlData.getXpdlData()));
            }
        }

        public void migrateFlowInstancesFromSharkToRiver() {
            logger.info("running shark to river migration process, for process = {}", process);

            riverPlan = riverPlanRepository.getPlanByClasseId(process.getName());
            logger.info("target plan is = {}", riverPlan);

            logger.debug("loading current flow list");
            List<Card> flowList = getFlowList();

            logger.info("execute migration of {} instances", flowList.size());
            if (flowList.stream().map(f -> f.get(ATTR_PLAN_INFO, String.class)).filter(StringUtils::isNotBlank).distinct().count() > 1) {
                logger.warn("more than one xpdl found with active instances!! old instances may not be compatible with new xpdl supplied for migration");//TODO: throw exception??
            }

            if (!flowList.isEmpty()) {
                flowList.forEach(this::prepareFlowInstance);
                logger.info("processed {} instances", flowList.size());
                checkArgument(flowsWithError.isEmpty(), "migration failed for %s / %s instances (note: db was not modified)", flowsWithError.size(), flowList.size());
                logger.info("no errors, finalizing migration");
                preparedCards.stream().forEach(this::completeMigration);
                logger.info("upgrade completed for {} instances", preparedCards.size());
            }

            updateProcessProvider(process);

            logger.info("migration completed for process = {}", process);
        }

        private List<Card> getFlowList() {
            return dao.selectAll().from(process).getCards();
        }

        private void prepareFlowInstance(Card flow) {
            try {
                Card card = doPrepareFlowInstance(flow);
                preparedCards.add(card);
            } catch (Exception ex) {
                logger.error("check error", ex);
                flowsWithError.add(flow);
            }
        }

        private void completeMigration(Card card) {
            logger.info("finalize migration for flow = {} (update card with processed data)", card);
            dao.update(card);
        }

        private Card doPrepareFlowInstance(Card flow) {
            logger.info("prepare migration for flow = {} (load data from shark, migrate activities)", flow);
            try {
                if (isCompleted(lookupHelper.getFlowStatus(flow))) {
                    String serializedData = serializeRiverFlowData(emptyMap(), process, riverPlan, RiverFlow.FlowStatus.COMPLETE);
                    return CardImpl.copyOf(flow)
                            .addAttribute(ATTR_TASK_INSTANCE_ID, emptyList())
                            .addAttribute(ATTR_TASK_DEFINITION_ID, emptyList())
                            .addAttribute(ATTR_NEXT_EXECUTOR, emptyList())
                            .addAttribute(ATTR_PLAN_INFO, riverPlanIdToLegacyUniqueProcessDefinition(riverPlan.getId()))
                            .addAttribute(ATTR_FLOW_ID, randomId())//TODO use newRiverFlowId() function
                            .addAttribute(FLOW_ATTR_DATA, serializedData)
                            .build();
                } else {

                    logger.debug("get flow variables from shark for flow = {}", flow);
                    Map<String, Object> data;
                    List<String> riverTaskIds;
                    List<String> activityPerformers;

                    FlowStatus riverFlowStatus;

                    data = sharkHelper.getFlowDataForProcess(flow.getString(ATTR_FLOW_ID));
                    logger.trace("shark flow variables = \n\n{}\n", mapToLoggableStringLazy(data));

                    data = data.entrySet().stream().map(e -> Pair.of(e.getKey(), rawToRiverFlow(e.getKey(), e.getValue(), riverPlan))).collect(toMap(Pair::getKey, Pair::getValue));

                    riverTaskIds = (List) flow.getNotNull(ATTR_TASK_INSTANCE_ID, List.class).stream().map((sharkActivityId) -> {
                        logger.debug("process shark activity = {}", sharkActivityId);
                        Pair<String, String> sharkActivityDefinitionIds = sharkHelper.getSharkActivityDefinitionIdWithParent((String) sharkActivityId);
                        logger.debug("shark activity id = {} has activity definition id = {} and parent activity definition id = {}", sharkActivityId, sharkActivityDefinitionIds.getLeft(), sharkActivityDefinitionIds.getRight());
                        String riverStepId;
                        if (isBlank(sharkActivityDefinitionIds.getRight())) {
                            riverStepId = sharkActivityDefinitionIds.getLeft();
                        } else {
                            riverStepId = buildStepIdFromParentActivityIdAndActivityId(sharkActivityDefinitionIds.getRight(), sharkActivityDefinitionIds.getLeft());
                        }
                        checkNotNull(riverPlan.getStepById(riverStepId));
                        String riverTaskId = buildUserTaskId(riverStepId);
                        logger.debug("converting shark activity id = {} to river task id = {}", sharkActivityId, riverTaskId);
                        checkNotNull(riverPlan.getTask(riverTaskId));
                        return riverTaskId;
                    }).collect(toImmutableList());
                    activityPerformers = flow.getNotNull(ATTR_NEXT_EXECUTOR, List.class);

                    riverFlowStatus = FlowStatus.RUNNING;//TODO check state (suspended? ) 

                    String serializedData = serializeRiverFlowData(data, process, riverPlan, riverFlowStatus);

                    return CardImpl.copyOf(flow)
                            .addAttribute(ATTR_TASK_INSTANCE_ID, riverTaskIds.stream().map((x) -> randomId()).collect(toList())) //TODO replace randomId with specific newTaskId function
                            .addAttribute(ATTR_TASK_DEFINITION_ID, riverTaskIds)
                            .addAttribute(ATTR_NEXT_EXECUTOR, activityPerformers)
                            .addAttribute(ATTR_PLAN_INFO, riverPlanIdToLegacyUniqueProcessDefinition(riverPlan.getId()))
                            .addAttribute(ATTR_FLOW_ID, randomId())//TODO use newRiverFlowId() function
                            .addAttribute(FLOW_ATTR_DATA, serializedData)
                            .build();
                }
            } catch (Exception ex) {
                throw new WorkflowException(ex, "error preparing flow instance migration for flow = %s", flow);
            }
        }

    }

    @Nullable
    private Object rawToRiverFlow(String key, @Nullable Object value, RiverPlan plan) {
        RiverVariableInfo<?> info = plan.getGlobalVariables().get(key);
        if (info == null) {
            return value;
        } else {
            try {
                Object converted = typeConverter.rawValueToFlowValue(value, info.getJavaType());
                logger.trace("converted value =< {} > ({}) to value =< {} > ({})", value, getClassOfNullable(value).getName(), converted, getClassOfNullable(converted).getName());
                return converted;
            } catch (Exception ex) {
                throw new WorkflowException(ex, "error converting flow value %s = %s (%s) to type = %s", key, value, getClassOfNullable(value).getName(), info.getJavaType().getName());
            }
        }
    }
}
