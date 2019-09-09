package org.cmdbuild.workflow.shark;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.google.common.collect.Maps;
import java.util.List;
import org.cmdbuild.workflow.model.TaskInfo;
import org.cmdbuild.workflow.model.FlowStatus;
import org.cmdbuild.workflow.inner.WorkflowTypesConverter;
import org.cmdbuild.workflow.model.SimpleFlowData;
import org.cmdbuild.workflow.shark.engine.WorkflowRemoteRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.workflow.model.FlowInfo;
import org.cmdbuild.workflow.inner.FlowCardRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.workflow.model.Flow;

@Component
public class SharkFlowVariablesSynchronizerImpl implements SharkFlowVariablesSynchronizer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final WorkflowRemoteRepository remoteRepository;
	private final FlowCardRepository persistence;
	private final WorkflowTypesConverter typesConverter;

	public SharkFlowVariablesSynchronizerImpl(WorkflowRemoteRepository remoteRepository, FlowCardRepository persistence, WorkflowTypesConverter typesConverter) {
		this.remoteRepository = checkNotNull(remoteRepository);
		this.persistence = checkNotNull(persistence);
		this.typesConverter = checkNotNull(typesConverter);
	}

	@Override
	public Flow syncProcessStateActivitiesAndVariables(Flow processInstance, FlowInfo processInstanceInfo) {
		return syncProcessStateActivitiesAndMaybeVariables(processInstance, processInstanceInfo, true);
	}

	@Override
	public Flow syncProcessStateAndActivities(Flow processInstance, FlowInfo processInstanceInfo) {
		return syncProcessStateActivitiesAndMaybeVariables(processInstance, processInstanceInfo, false);
	}

	@Override
	public Map<String, Object> getRawSharkVariables(Flow flow) {
		return remoteRepository.getProcessInstanceVariables(flow.getFlowId());
	}

	@Override
	public Map<String, Object> getSharkVariablesForCard(Flow flow) {
		return workflowVariablesToCard(getRawSharkVariables(flow));
	}

	private Flow syncProcessStateActivitiesAndMaybeVariables(Flow processInstance, FlowInfo walkInfo, boolean syncVariables) {
		logger.debug("synchronizing process state, activities and (maybe) variables");

		Map<String, Object> values = Maps.newHashMap();
		if (syncVariables) {
			logger.debug("synchronizing process variables");
			Map<String, Object> convertedWorkflowValues = getSharkVariablesForCard(processInstance);
			for (Attribute cardAttribute : processInstance.getType().getServiceAttributes()) {
				String attributeName = cardAttribute.getName();
				Object newValue = convertedWorkflowValues.get(attributeName);
				logger.trace("set card attribute from process variable, {} = '{}'", attributeName, newValue);
				values.put(attributeName, newValue);
			}
		} else {
			logger.debug("variables syncronization not required, skipping");
		}

		FlowStatus walkStatus;
		List<TaskInfo> activities;

		if (walkInfo == null) {
			logger.warn("process instance info is null, setting process as completed (this should not happen)");
			activities = null;
			walkStatus = FlowStatus.COMPLETED;
		} else {
			activities = remoteRepository.findOpenActivitiesForProcessInstance(processInstance.getFlowId());
			walkStatus = walkInfo.getStatus();
			if (walkStatus == FlowStatus.COMPLETED) {
				logger.debug("process state = COMPLETED, delete if from workflow service");
				remoteRepository.deleteProcessInstance(walkInfo.getFlowId());
			} else {
				logger.debug("process state = {}, cleanup is not required", walkStatus);
			}
		}

		return persistence.updateFlowCard(processInstance, SimpleFlowData.builder()
				.withValues(values)
				.withStatus(walkStatus)
				.withTasks(activities)
				.withInfo(walkInfo)
				.build(), () -> remoteRepository.getProcessInstanceVariables(processInstance.getFlowId()));
	}

	private Map<String, Object> workflowVariablesToCard(Map<String, Object> workflowValues) {
		logger.debug("convert workflow instance values");
		return fromWorkflowValues(workflowValues, typesConverter);
	}

	/*
	 * FIXME AWFUL pre-release hack
	 */
	@Deprecated
	public static Map<String, Object> fromWorkflowValues(Map<String, Object> workflowValues, WorkflowTypesConverter workflowVariableConverter) {
		Map<String, Object> nativeValues = new HashMap<>();
		for (Map.Entry<String, Object> record : workflowValues.entrySet()) {
			nativeValues.put(record.getKey(), workflowVariableConverter.fromWorkflowType(record.getValue()));
		}
		return nativeValues;
	}

}
