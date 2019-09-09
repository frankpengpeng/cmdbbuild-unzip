/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.dao.data;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.cache.CacheService;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.core.model.ProcessImpl;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.TaskPerformer;
import org.cmdbuild.workflow.core.utils.BshTaskPerformerExpressionProcessor;
import org.cmdbuild.workflow.core.utils.TaskPerformerExpressionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.workflow.inner.PlanUpdatedEvent;
import org.cmdbuild.workflow.inner.PlanServiceDelegate;
import org.cmdbuild.dao.driver.repository.ClassStructureChangedEvent;
import org.cmdbuild.dao.driver.repository.DaoEventService;
import static org.cmdbuild.workflow.model.Process.ADMIN_PERFORMER_AS_GROUP;
import static org.cmdbuild.workflow.model.Process.UNKNOWN_PERFORMER_AS_GROUP;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.config.api.ConfigListener;

@Component
public class PlanAndClasseMapperServiceImpl implements PlanAndClasseMapperService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final PlanServiceDelegateSupplier delegates;
	private final EasytemplateProcessor easytemplateProcessor;
	private final WorkflowConfiguration configuration;

	private final CmCache<Process> classesByClasseIdAndPlanId;

	public PlanAndClasseMapperServiceImpl(PlanServiceDelegateSupplier delegates, EasytemplateProcessor easytemplateProcessor, WorkflowConfiguration configuration, CacheService cacheService, DaoEventService daoEventService) {
		this.delegates = checkNotNull(delegates);
		this.easytemplateProcessor = checkNotNull(easytemplateProcessor);
		this.configuration = checkNotNull(configuration);
		classesByClasseIdAndPlanId = cacheService.newCache("workflow_planClasse_by_classe_id_and_plan_id");
		daoEventService.getEventBus().register(new Object() {

			@Subscribe
			public void handleClassStructureChangedEvent(ClassStructureChangedEvent event) {
				classesByClasseIdAndPlanId.invalidateAll();
			}
		});
		delegates.getAllDelegates().forEach((d) -> {
			d.getEventBus().register(new Object() {
				@Subscribe
				public void handlePlanUpdatedEvent(PlanUpdatedEvent event) {
					classesByClasseIdAndPlanId.invalidate(key(event.getClassId(), event.getPlanId()));
					classesByClasseIdAndPlanId.invalidate(key(event.getClassId(), DEFAULT_PLAN_ID));
				}
			});
		});
	}

	@ConfigListener(WorkflowConfiguration.class)
	public void handleConfigUpdateEvent() {
		classesByClasseIdAndPlanId.invalidateAll();
	}

	@Override
	public String getClasseIdByProviderAndPlanId(String provider, String planId) {
		return checkNotNull(delegates.get(provider).getClassNameOrNull(planId), "class name not found for provider = %s planId = %s", provider, planId);
	}

	@Override
	public Process classeAndPlanIdToPlanClasse(Classe classe, String planId) {
		checkNotNull(classe);
		checkNotBlank(planId);
		return classesByClasseIdAndPlanId.get(key(classe.getName(), planId), () -> doClasseAndPlanIdToPlanClasse(classe, planId));
	}

	private Process doClasseAndPlanIdToPlanClasse(Classe classe, String suggestedPlanId) {
		logger.debug("build plan classe for classe = {}", classe);
		try {
			String planId = null;
			Map<String, TaskDefinition> entryTaskMap = null, tasksById = null;
			try {
				if (configuration.isEnabled()) {
					String provider = firstNonNull(classe.getMetadata().getFlowProviderOrNull(), configuration.getDefaultWorkflowProvider());
					PlanServiceDelegate planService = delegates.get(provider);
					if (equal(suggestedPlanId, DEFAULT_PLAN_ID)) {
						planId = planService.getPlanIdOrNull(classe);
					} else {
						checkArgument(planService.hasPlanId(suggestedPlanId), "unable to find provider for plan id = %s with provider = %s", suggestedPlanId, provider);
						planId = suggestedPlanId;
					}
					if (planId != null) {
						entryTaskMap = buildEntryTaskMap(planService.getEntryTasks(planId));
						tasksById = uniqueIndex(planService.getAllTasks(planId), TaskDefinition::getId);
					}
				}
			} catch (Exception ex) {
				if (configuration.returnNullPlanOnPlanError()) {
					logger.warn(marker(), "error retrieving plan for class = {} planId = {}", classe, planId, ex);
					planId = null;
					entryTaskMap = null;
					tasksById = null;
				} else {
					throw ex;
				}
			}
			return ProcessImpl.builder()
					.withInner(classe)
					.withPlanId(planId)
					.withEntryTasks(entryTaskMap)
					.withTasksById(tasksById)
					.build();
		} catch (Exception ex) {
			throw new WorkflowException(ex, "error processing plan classe = %s with plan id = %s", classe, suggestedPlanId);
		}
	}

	private Map<String, TaskDefinition> buildEntryTaskMap(List<TaskDefinition> entryTasks) {
		Map<String, TaskDefinition> map = map();
		entryTasks.forEach((TaskDefinition task) -> {
			for (TaskPerformer performer : task.getPerformers()) {
				switch (performer.getType()) {
					case ADMIN:
						map.put(ADMIN_PERFORMER_AS_GROUP, task);
						break;
					case ROLE:
						map.put(performer.getValue(), task);
						break;
					case EXPRESSION:
						String expression = performer.getValue();
						String resolvedExpression = easytemplateProcessor.processExpression(expression);
						TaskPerformerExpressionProcessor evaluator = new BshTaskPerformerExpressionProcessor(resolvedExpression);
						Set<String> names = evaluator.getNames();
						names.forEach((group) -> map.put(group, task));
						break;
					case UNKNOWN:
					default:
						logger.warn("unsupported performer type = {}", performer);
						map.put(UNKNOWN_PERFORMER_AS_GROUP, task);
				}
			}

		});
		return map;
	}

}
