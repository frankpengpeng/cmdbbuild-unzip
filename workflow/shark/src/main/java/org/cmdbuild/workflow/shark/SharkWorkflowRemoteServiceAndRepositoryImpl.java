package org.cmdbuild.workflow.shark;

import org.cmdbuild.workflow.shark.model.TaskInfoImpl;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.workflow.model.FlowStatus;
import com.google.common.collect.Ordering;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.cmdbuild.workflow.model.WorkflowException;
import org.enhydra.shark.api.client.wfmc.wapi.WAPI;
import org.enhydra.shark.api.client.wfmc.wapi.WMActivityInstance;
import org.enhydra.shark.api.client.wfmc.wapi.WMActivityInstanceState;
import org.enhydra.shark.api.client.wfmc.wapi.WMAttribute;
import org.enhydra.shark.api.client.wfmc.wapi.WMAttributeIterator;
import org.enhydra.shark.api.client.wfmc.wapi.WMFilter;
import org.enhydra.shark.api.client.wfmc.wapi.WMProcessInstance;
import org.enhydra.shark.api.client.wfmc.wapi.WMProcessInstanceState;
import org.enhydra.shark.api.client.wfmc.wapi.WMSessionHandle;
import org.enhydra.shark.api.client.wfservice.PackageAdministration;
import org.enhydra.shark.api.client.wfservice.SharkInterface;
import org.enhydra.shark.api.client.wfservice.WMEntity;
import org.enhydra.shark.api.common.ActivityFilterBuilder;
import org.enhydra.shark.api.common.ProcessFilterBuilder;
import org.enhydra.shark.api.common.SharkConstants;
import org.enhydra.shark.utilities.MiscUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.model.PlanPackageDefinitionInfo;
import org.cmdbuild.workflow.model.TaskInfo;
import org.cmdbuild.workflow.model.PlanPackageDefinitionInfoAndData;
import org.cmdbuild.workflow.model.SimpleFlowInfo;
import org.cmdbuild.workflow.shark.engine.WorkflowActionExecutor;
import org.cmdbuild.workflow.shark.engine.WorkflowActionExecutorCallback;
import org.cmdbuild.workflow.shark.engine.WorkflowRemoteService;
import org.cmdbuild.workflow.shark.engine.WorkflowRemoteRepository;
import org.cmdbuild.workflow.shark.model.FlowInfoImpl;
import static org.cmdbuild.workflow.shark.SharkTransactionContextHolder.handle;
import org.springframework.stereotype.Component;
import static org.cmdbuild.workflow.shark.SharkTransactionContextHolder.getAndRemoveCollectedEvents;
import org.cmdbuild.workflow.model.FlowInfo;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class SharkWorkflowRemoteServiceAndRepositoryImpl implements WorkflowRemoteService, WorkflowRemoteRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final SharkWebserviceClient webserviceClient;
	private final TransactedSharkMethodAspectjConfig aspectjConfig;

	public SharkWorkflowRemoteServiceAndRepositoryImpl(SharkWebserviceClient sharkWebserviceClient, TransactedSharkMethodAspectjConfig aspectjConfig) {
		this.webserviceClient = checkNotNull(sharkWebserviceClient);
		this.aspectjConfig = checkNotNull(aspectjConfig);
	}

	private WAPI wapi() {
		return webserviceClient.getWapi();
	}

	private SharkInterface shark() {
		return webserviceClient.getSharkInterface();
	}

	@Override
	public String getName() {
		return "shark";
	}

	@Override
	public WorkflowActionExecutor executor() {
		WorkflowActionExecutor workflowActionExecutor = new WorkflowActionExecutorImpl();
		workflowActionExecutor = aspectjConfig.createAspectjProxy(workflowActionExecutor);
		return workflowActionExecutor;
	}

	@Override
	@TransactedSharkMethod
	public String[] getPackageVersions(String pkgId) {
		try {
			return shark().getPackageAdministration().getPackageVersions(handle(), pkgId);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	@Override
	@TransactedSharkMethod
	public PlanPackageDefinitionInfo uploadPackage(String pkgId, byte[] pkgDefData) {
		checkNotNull(pkgDefData, "package data cannot be null");
		WMSessionHandle handle = handle();
		try {
			PackageAdministration pa = shark().getPackageAdministration();
			WMEntity uploadedPackage;
			if (pkgId == null || pa.getPackageVersions(handle, pkgId).length == 0) {
				uploadedPackage = pa.uploadPackage(handle, pkgDefData);
			} else {
				uploadedPackage = pa.updatePackage(handle, pkgId, pkgDefData);
			}
			return newWSPackageDefInfo(uploadedPackage.getPkgId(), uploadedPackage.getPkgVer());
		} catch (Exception e) {
			logger.error("error uploading xpld data = {}", new String(pkgDefData));
			throw new WorkflowException(e);
		}
	}

	private PlanPackageDefinitionInfo newWSPackageDefInfo(String id, String version) {
		return new PlanPackageDefinitionInfo() {

			@Override
			public String getPackageId() {
				return id;
			}

			@Override
			public String getVersion() {
				return version;
			}

		};
	}

	@Override
	@TransactedSharkMethod
	public byte[] downloadPackage(String pkgId, String pkgVer) {
		try {
			return shark().getPackageAdministration().getPackageContent(handle(), pkgId, pkgVer);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	@Override
	@TransactedSharkMethod
	public List<PlanPackageDefinitionInfoAndData> downloadAllPackages() {
		WMSessionHandle handle = handle();
		try {
			PackageAdministration pa = shark().getPackageAdministration();
			String[] pkgIds = pa.getOpenedPackageIds(handle);
			List<PlanPackageDefinitionInfoAndData> out = new ArrayList<>();
			for (int i = 0; i < pkgIds.length; ++i) {
				String id = pkgIds[i];

				for (String version : pa.getPackageVersions(handle, id)) {//TODO retrieve packages lazy with cache
					byte[] data = pa.getPackageContent(handle, id, version);
					out.add(newWSPackageDef(id, version, data));
				}

//				String version = pa.getCurrentPackageVersion(handle, id);
//				byte[] data = pa.getPackageContent(handle, id, version);
//				out.add(newWSPackageDef(id, version, data));
			}
			return out;
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	private PlanPackageDefinitionInfoAndData newWSPackageDef(String id, String version, byte[] data) {
		return new PlanPackageDefinitionInfoAndData() {

			@Override
			public String getPackageId() {
				return id;
			}

			@Override
			public String getVersion() {
				return version;
			}

			@Override
			public byte[] getData() {
				return data;
			}

		};
	}

	//TODO move this to common lib
	public static @Nullable
	Object abbreviateForDebug(Logger logger, @Nullable Object value) {
//		if (value == null || logger.isTraceEnabled() || !logger.isDebugEnabled()) {
		if (value == null) {
			return value;
		} else {
			return StringUtils.abbreviate(value.toString(), 100);
		}
	}

	@Override
	@TransactedSharkMethod
	public void setProcessInstanceVariables(String walkId, Map<String, ?> variables) {
		logger.info("setProcessInstanceVariables for walkId = {} variables = {}", walkId, variables);
		try {
			setProcessInstanceVariables(handle(), walkId, variables);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	private void setProcessInstanceVariables(WMSessionHandle handle, String procInstId, Map<String, ?> variables) throws Exception {
		for (String name : variables.keySet()) {
			Object sharkValue = variables.get(name);
			try {
				wapi().assignProcessInstanceAttribute(handle, procInstId, name, sharkValue);
			} catch (Exception e) {
				throw new WorkflowException(e, "error setting workflow variable %s = %s (%s) for flow instance = %s", name, sharkValue, getClassOfNullable(sharkValue).getName(), procInstId);
			}
		}
	}

	@Override
	@TransactedSharkMethod
	public FlowInfo[] listOpenProcessInstances(String procDefId) {
		WMSessionHandle handle = handle();
		try {
			WMProcessInstance[] pis = wapi().listProcessInstances(handle, openProcessInstances(handle, procDefId), false).getArray();
			FlowInfo[] out = new FlowInfo[pis.length];
			for (int i = 0; i < pis.length; ++i) {
				out[i] = FlowInfoImpl.newInstance(pis[i]);
			}
			return out;
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	private WMFilter openProcessInstances(WMSessionHandle handle, String procDefId) throws Exception {
		ProcessFilterBuilder fb = shark().getProcessFilterBuilder();
		return fb.and(handle, fb.addProcessDefIdEquals(handle, procDefId),
				fb.addStateStartsWith(handle, SharkConstants.STATEPREFIX_OPEN));
	}

	@Override
	@TransactedSharkMethod
	public FlowInfo getWalkInfo(String procInstId) {
		try {
			WMProcessInstance pi = wapi().getProcessInstance(handle(), procInstId);
			return FlowInfoImpl.newInstance(pi);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	@Override
	@TransactedSharkMethod
	public Map<String, Object> getProcessInstanceVariables(String procInstId) {
		logger.debug("get process instance variables for instanceId = {}", procInstId);
		try {
			WMAttributeIterator iterator = wapi().listProcessInstanceAttributes(handle(), procInstId, null, false);
			Map<String, Object> variables = list(iterator.getArray()).stream().sorted(Ordering.natural().onResultOf(WMAttribute::getName)).collect(toMap(WMAttribute::getName, WMAttribute::getValue));
			logger.trace("raw shark variables = \n\n{}\n", mapToLoggableStringLazy(variables));
			return variables;
		} catch (Exception e) {
			throw new WorkflowException(e, "error retrieving flow variables for instance = %s", procInstId);
		}
	}

	@Override
	@TransactedSharkMethod
	public List<TaskInfo> findOpenActivitiesForProcessInstance(String walkId) {
		WMSessionHandle handle = handle();
		logger.debug("findOpenActivitiesForProcessInstance with walkId = {} and handle = {}", walkId, handle);
		try {
			WMFilter filter = createFilterForOpenActivitiesForProcessInstance(handle, walkId);
			WMActivityInstance[] ais = wapi().listActivityInstances(handle, filter, false).getArray();
			List<TaskInfo> list = asList(ais).stream().map((input) -> TaskInfoImpl.newInstance(input)).collect(toList());
			logger.debug("found open activities = {}", list);
			return list;
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	private WMFilter createFilterForOpenActivitiesForProcessInstance(WMSessionHandle handle, String procInstId) throws Exception {
		ActivityFilterBuilder fb = shark().getActivityFilterBuilder();
		return fb.and(handle, fb.addProcessIdEquals(handle, procInstId),
				fb.addStateStartsWith(handle, SharkConstants.STATEPREFIX_OPEN));
	}

	@Override
	@TransactedSharkMethod
	public List<TaskInfo> findOpenActivitiesForProcess(String procDefId) {
		WMSessionHandle handle = handle();
		try {
			WMFilter filter = openActivitiesForProcess(handle, procDefId);
			WMActivityInstance[] ais = wapi().listActivityInstances(handle, filter, false).getArray();
			return asList(ais).stream().map((value) -> TaskInfoImpl.newInstance(value)).collect(toList());
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	private WMFilter openActivitiesForProcess(WMSessionHandle handle, String procDefId) throws Exception {
		ActivityFilterBuilder fb = shark().getActivityFilterBuilder();
		return fb.and(handle, fb.addProcessDefIdEquals(handle, procDefId),
				fb.addStateStartsWith(handle, SharkConstants.STATEPREFIX_OPEN));
	}

	@Override
	@TransactedSharkMethod
	public void deleteProcessInstance(String procInstId) {
		WMSessionHandle handle = handle();
		try {
			ProcessFilterBuilder fb = shark().getProcessFilterBuilder();
			WMFilter filter = fb.addIdEquals(handle, procInstId);
			shark().getExecutionAdministration().deleteProcessesWithFiltering(handle, filter);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
	}

	private class WorkflowActionExecutorImpl implements WorkflowActionExecutor {

		private WorkflowActionExecutorCallback callback;

		@Override
		public WorkflowActionExecutor withCallback(WorkflowActionExecutorCallback callback) {
			this.callback = checkNotNull(callback);
			return this;
		}

		protected void processCollectedEvents() {
			List<SharkEvent> collectedEvents = getAndRemoveCollectedEvents();
			logger.debug("processCollectedEvents, events = {}", collectedEvents);
			for (SharkEvent event : collectedEvents) {
				logger.debug("processing event = {}", event);
				FlowInfo walkInfo = getWalkInfo(event.getWalkId());
				if (walkInfo == null) {
					walkInfo = new SimpleFlowInfo(event.getWalkId(), FlowStatus.COMPLETED, null, null, event.getPlanId());
				}
				switch (event.getType()) {
					case START:
						callback.startFlow(walkInfo);
						break;
					case UPDATE:
						callback.updateFlow(walkInfo);
						break;
					default:
						throw new IllegalArgumentException("Invalid event type");
				}
			}
		}

		@Override
		@TransactedSharkMethod
		public FlowInfo startProcess(String pkgId, String procDefId) {
			return startProcess(pkgId, procDefId, Collections.<String, Object>emptyMap());
		}

		@Override
		@TransactedSharkMethod
		public FlowInfo startProcess(String pkgId, String procDefId, Map<String, ?> variables) {
			logger.info("start process pkgId = {} procDefId = {} variables = {}", pkgId, procDefId, variables);
			WMSessionHandle handle = handle();
			try {
				String uniqueProcDefId = shark().getXPDLBrowser().getUniqueProcessDefinitionName(handle, pkgId, "", procDefId);
				String procInstId = wapi().createProcessInstance(handle, uniqueProcDefId, null);
				setProcessInstanceVariables(handle, procInstId, variables);
				String newProcInstId = wapi().startProcess(handle, procInstId);
				FlowInfo walkInfo = newWSProcessInstInfo(uniqueProcDefId, newProcInstId);
				logger.info("started walk = {}", walkInfo);
				return walkInfo;
			} catch (Exception e) {
				throw new WorkflowException(e);
			}
		}

		private FlowInfo newWSProcessInstInfo(String uniqueProcDefId, String procInstId) {
			return new SimpleFlowInfo(procInstId, FlowStatus.OPEN, MiscUtilities.getProcessMgrPkgId(uniqueProcDefId), MiscUtilities.getProcessMgrVersion(uniqueProcDefId), MiscUtilities.getProcessMgrProcDefId(uniqueProcDefId));//TODO create builder
		}

		@Override
		@TransactedSharkMethod
		public void abortTask(String walkId, String taskId) {
			logger.debug("abort task for walkId = {} taskId = {}", walkId, taskId);
			WMSessionHandle handle = handle();
			try {
				// From Shark's FAQ,
				// "terminate [...] tries to follow the next activity(s), [...] abort [...] doesn't."
				wapi().changeActivityInstanceState(handle, walkId, taskId, WMActivityInstanceState.CLOSED_ABORTED);
			} catch (Exception e) {
				throw new WorkflowException(e);
			}
		}

		@Override
		@TransactedSharkMethod
		public void completeTask(String procInstId, String actInstId) {
			WMSessionHandle handle = handle();
			try {
				wapi().changeActivityInstanceState(handle, procInstId, actInstId, WMActivityInstanceState.OPEN_RUNNING);
			} catch (Exception e) {
				logger.warn("warning: ", e);
				// Ignore: it might be open-running already...
			}
			try {
				wapi().changeActivityInstanceState(handle, procInstId, actInstId, WMActivityInstanceState.CLOSED_COMPLETED);
				processCollectedEvents();
			} catch (Exception e) {
				throw new WorkflowException(e);
			}
		}

		@Override
		@TransactedSharkMethod
		public void abortFlow(String procInstId) {
			WMSessionHandle handle = handle();
			try {
				wapi().changeProcessInstanceState(handle, procInstId, WMProcessInstanceState.CLOSED_ABORTED);
				processCollectedEvents();
			} catch (Exception e) {
				throw new WorkflowException(e);
			}
		}

		@Override
		@TransactedSharkMethod
		public void suspendFlow(String procInstId) {
			WMSessionHandle handle = handle();
			try {
				wapi().changeProcessInstanceState(handle, procInstId, WMProcessInstanceState.OPEN_NOTRUNNING_SUSPENDED);
				processCollectedEvents();
			} catch (Exception e) {
				throw new WorkflowException(e);
			}
		}

		@Override
		@TransactedSharkMethod
		public void resumeFlow(String procInstId) {
			WMSessionHandle handle = handle();
			try {
				wapi().changeProcessInstanceState(handle, procInstId, WMProcessInstanceState.OPEN_RUNNING);
				processCollectedEvents();
			} catch (Exception e) {
				throw new WorkflowException(e);
			}
		}

	}

}
