package org.cmdbuild.workflow.shark.xpdl;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.io.IOException;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import java.util.List;

import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.io.IOUtils;

import org.cmdbuild.workflow.model.GroupQueryAdapter;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.utils.PlanIdUtils;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.workflow.core.utils.XpdlUtils.xpdlToDatasource;
import org.cmdbuild.workflow.core.xpdl.XpdlTemplateService;
import org.cmdbuild.workflow.model.PlanPackageDefinitionInfo;
import org.cmdbuild.workflow.model.SimplePlanUpdatedEvent;
import static org.cmdbuild.workflow.WorkflowCommonConst.SHARK;
import org.cmdbuild.workflow.shark.xpdl.XpdlPlanRepository.ProcessInfo;
import org.cmdbuild.workflow.inner.PlanServiceDelegate;
import static org.cmdbuild.workflow.shark.xpdl.utils.SharkXpdlUtils.getStandardPackageId;
import static org.cmdbuild.workflow.shark.xpdl.utils.SharkXpdlUtils.getStandardProcessDefinitionId;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;

@Component
public class SharkPlanServiceImpl implements SharkPlanService, XpdlTemplateService, PlanServiceDelegate {

	private final XpdlPlanRepository xpdlPlanRepository;
	private final GroupQueryAdapter groupQueryAdapter;

	private final EventBus eventBus = new EventBus();

	public SharkPlanServiceImpl(GroupQueryAdapter groupQueryAdapter, XpdlPlanRepositoryImpl xpdlProcessDefinitionStore) {
		this.xpdlPlanRepository = checkNotNull(xpdlProcessDefinitionStore);
		this.groupQueryAdapter = checkNotNull(groupQueryAdapter);
	}

	@Override
	public String getName() {
		return SHARK;
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

//	@Override
//	public String getClassNameOrNull(String planId) {
//		ProcessInfo processInfo = xpdlPlanRepository.getProcessInfoByPlanIdOrNull(planId);
//		if (processInfo == null) {
//			return null;
//		} else {
//			return processInfo.getClassName();
//		}
//	}
	@Override
	public List<TaskDefinition> getAllTasks(String planId) {
		ProcessInfo processInfo = xpdlPlanRepository.getProcessInfoByPlanId(planId);
		return processInfo.getAllTasks();
	}

	@Override
	public boolean hasPlanId(String planId) {
		ProcessInfo processInfo = xpdlPlanRepository.getProcessInfoByPlanIdOrNull(planId);
		return processInfo != null;
	}

	@Override
	public String[] getVersions(Process process) {
		return xpdlPlanRepository.getPackageVersions(process.getName());
	}

	@Override
	public DataSource getDefinition(String classId, String version) {
		byte[] pkgDef = xpdlPlanRepository.downloadPackage(classId, version);
		return xpdlToDatasource(pkgDef, format("%s_%s", classId, version));
	}

	@Override
	public void updateDefinition(Process process, DataSource dataSource) {
		try {
			byte[] data = IOUtils.toByteArray(dataSource.getInputStream());
			XpdlDocumentHelper xpdl = new XpdlDocumentHelper(XpdlPackageFactory.readXpdl(data));
			checkArgument(equal(getStandardPackageId(process), xpdl.getPackageId()), "xpdl package id = %s does not match standard package id = %s", xpdl.getPackageId(), getStandardPackageId(process));
			XpdlProcess proc = xpdl.findProcess(getStandardProcessDefinitionId(process));
			if (proc == null) {
				throw new XpdlException("The process id does not match");
			}
			String processId = proc.getId();
			String bindedClass = proc.getBindToClass();
			if (!process.getName().equals(bindedClass)) {
				throw new XpdlException("The process is not bound to this class");
			}
			PlanPackageDefinitionInfo packageInfo;
			synchronized (this) {
				packageInfo = xpdlPlanRepository.uploadPackage(process.getName(), data);
			}
			eventBus.post(SimplePlanUpdatedEvent.builder()
					.withClassId(process.getName())
					.withPlanId(PlanIdUtils.buildPlanId(packageInfo.getPackageId(), packageInfo.getVersion(), processId))
					.build());
		} catch (IOException e) {
			throw new WorkflowException(e);
		}
	}

	@Override
	public List<TaskDefinition> getEntryTasks(String planId) {
		return xpdlPlanRepository.getEntryTasks(PlanIdUtils.readPlanId(planId).getDefinitionId());
	}

	@Override
	public TaskDefinition getTaskDefinition(Flow processInstance, String activityDefinitionId) {
		return xpdlPlanRepository.getTask(processInstance.getPlanInfoOrNull(), activityDefinitionId);
	}

	@Override
	public String getPackageId(Process process) {
		return xpdlPlanRepository.getPackageId(process.getName());
	}

	@Override
	public @Nullable
	String getPlanIdOrNull(Classe classe) {
		ProcessInfo processInfo = xpdlPlanRepository.getProcessInfoByClassIdOrNull(classe.getName());
		if (processInfo == null) {
			return null;
		} else {
			return processInfo.getPlanId();
		}
	}

	@Override
	public String getProcessDefinitionId(Process process) {
		return xpdlPlanRepository.getProcessDefinitionId(process.getName());
	}

	@Override
	@Nullable
	public String getClassNameOrNull(String planId) {
		return xpdlPlanRepository.getProcessClassName(planId);
	}

	@Override
	public DataSource getTemplate(final Process process) throws XpdlException { //TODO move this out of shark module, into its own service
		XpdlDocumentHelper xpdlDocumentHelper = XpdlDocumentHelper.createXpdlTemplateForProcess(process, asList(groupQueryAdapter.getAllGroupNames()));
		return createDataSource(process, xpdlDocumentHelper);
	}

	private DataSource createDataSource(final Process process, final XpdlDocumentHelper doc) throws XpdlException {
		final byte[] xpdl = XpdlPackageFactory.xpdlByteArray(doc.getPkg());
		final ByteArrayDataSource ds = new ByteArrayDataSource(xpdl, getMimeType());
		ds.setName(String.format("%s.%s", process.getName(), getFileExtension()));
		return ds;
	}

	protected String getMimeType() {
		return "application/x-xpdl";
	}

	protected String getFileExtension() {
		return "xpdl";
	}

}
