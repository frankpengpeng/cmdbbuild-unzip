package org.cmdbuild.workflow.shark.xpdl;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.widget.WidgetFactoryService;

import org.cmdbuild.workflow.model.PlanPackageDefinitionInfo;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.PlanInfo;
import org.cmdbuild.workflow.shark.engine.WorkflowRemoteRepository;
import org.cmdbuild.workflow.utils.PlanIdUtils;
import org.enhydra.jxpdl.elements.Package;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class XpdlPlanRepositoryImpl implements XpdlPlanRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final XpdlExtendedAttributeVariableFactory xpdlVariableFactory;
	private final XpdlExtendedAttributeMetadataFactory xpdlMetadataFactory;
	private final WidgetFactoryService xpdlWidgetFactory;

	private final WorkflowRemoteRepository remoteRepository;
	private final Holder<List<PackageInfo>> allPackages;

	public XpdlPlanRepositoryImpl(WorkflowRemoteRepository repository, XpdlExtendedAttributeVariableFactory xpdlvariablefactory, XpdlExtendedAttributeMetadataFactory xpdlMetadataFactory, WidgetFactoryService xpdlwidgetfactory, CacheService cacheService) {
		this.remoteRepository = checkNotNull(repository);
		this.xpdlVariableFactory = checkNotNull(xpdlvariablefactory);
		this.xpdlMetadataFactory = checkNotNull(xpdlMetadataFactory);
		this.xpdlWidgetFactory = checkNotNull(xpdlwidgetfactory);
		this.allPackages = cacheService.newHolder("shark_all_process_packages");
	}

	@Override
	public List<TaskDefinition> getEntryTasks(String processDefinitionId) {
		ProcessInfo pi = getProcessInfoByDefinition(processDefinitionId);
		if (pi == null) {
			// it is null when the process was created but the XPDL is not yet
			// uploaded
			return Collections.<TaskDefinition>emptyList();
		} else {
			return pi.getEntryTasks();
		}
	}

	@Override
	public TaskDefinition getTask(PlanInfo procDefInfo, String activityDefinitionId) {
		return getProcessInfo(procDefInfo).getTaskById(activityDefinitionId);
	}

	/**
	 * Gets cached process info or downloads and caches it.
	 */
	public ProcessInfo getProcessInfo(PlanInfo procDefInfo) {
		PackageInfo pkgInfo = getPackageInfoById(procDefInfo.getPackageId());
		PackageVersionInfo pkgVerInfo = pkgInfo.getVersionInfo(procDefInfo.getVersion());
		if (pkgVerInfo == null) {
			pkgVerInfo = downloadAndCachePackageVersion(procDefInfo.getVersion(), procDefInfo.getPackageId());
		}
		return pkgVerInfo.getProcess(procDefInfo.getDefinitionId());
	}

	@Override
	public String[] getPackageVersions(String className) {
		return remoteRepository.getPackageVersions(getPackageId(className));
	}

	@Override
	public String getPackageId(String className) {
		PackageInfo pi = getPackageInfoByClass(className);
		if (pi == null) {
			// it is null when the process was created but the XPDL is not yet
			// uploaded
			return null;
		} else {
			return pi.getId();
		}
	}

	@Override
	public String getProcessDefinitionId(String className) {
		ProcessInfo pi = getProcessInfoByClass(className);
		if (pi == null) {
			// it is null when the process was created but the XPDL is not yet
			// uploaded
			return null;
		} else {
			return pi.getDefinitionId();
		}
	}

	@Override
	@Nullable
	public ProcessInfo getProcessInfoByPlanIdOrNull(String planId) {
		logger.debug("get info for plan = {}", planId);
//		getProcessInfoStream().forEach(p -> logger.info("available plan = {}", p.getPlanId()));

		return getProcessInfoStream().filter((p) -> equal(p.getPlanId(), planId)).findAny().orElse(null);
	}

	@Override
	@Nullable
	public ProcessInfo getProcessInfoByClassIdOrNull(String classId) {
		return getProcessInfoByClass(classId);
	}

	@Override
	@Nullable
	public String getProcessClassName(String processDefinitionId) {
		ProcessInfo pi = getProcessInfoByDefinition(processDefinitionId);
		if (pi == null) {
			// it is null when the process was created but the XPDL is not yet
			// uploaded
			return null;
		} else {
			return pi.getClassName();
		}
	}

	@Override
	public byte[] downloadPackage(String className, String pkgVer) {
		PackageVersionInfo pvi = getPackageInfoByClass(className).getVersionInfo(pkgVer);
		if (pvi == null) {
			String pkgId = getPackageId(className);
			return downloadAndCachePackageVersion(pkgVer, pkgId).getRawDefinition();
		} else {
			return pvi.getRawDefinition();
		}

	}

	private PackageVersionInfo downloadAndCachePackageVersion(String pkgVer, String pkgId) {
		byte[] pkgData = remoteRepository.downloadPackage(pkgId, pkgVer);
//		return cache.addPackageVersion(pkgId, pkgVer, pkgData);
		return createPackageVersionInfo(pkgData, pkgVer);
	}

	@Override
	public synchronized PlanPackageDefinitionInfo uploadPackage(String className, byte[] pkgData) {
		String pkgId = getPackageId(className);
		PlanPackageDefinitionInfo pkgInfo = remoteRepository.uploadPackage(pkgId, pkgData);
//		cache.addCurrentPackageVersion(pkgInfo.getPackageId(), pkgInfo.getVersion(), pkgData);
		allPackages.invalidate();
		return pkgInfo;
	}

	private final class PackageInfo {

		private final String id;
		private String currentVersion;
		private final Map<String, PackageVersionInfo> packageVersions = new HashMap<>();

		public PackageInfo(String id, String currentVersion, PackageVersionInfo currentVersionInfo) {
			this.id = id;
			setCurrentVersion(currentVersion, currentVersionInfo);
		}

		public String getId() {
			return id;
		}

		PackageVersionInfo getCurrentVersionInfo() {
			return packageVersions.get(currentVersion);
		}

		PackageVersionInfo getVersionInfo(String version) {
			return packageVersions.get(version);
		}

		public Map<String, PackageVersionInfo> getPackageVersions() {
			return packageVersions;
		}

		public void setCurrentVersion(String currentVersion, PackageVersionInfo currentVersionInfo) {
			this.currentVersion = currentVersion;
			this.packageVersions.put(currentVersion, currentVersionInfo);
		}

		public void addVersion(String version, PackageVersionInfo versionInfo) {
			this.packageVersions.put(version, versionInfo);
		}
	}

	protected interface PackageVersionInfo {

		byte[] getRawDefinition();

		ProcessInfo getProcess(String procDefId);

		Collection<ProcessInfo> getProcesses();
	}

	private Stream<ProcessInfo> getProcessInfoStream() {
//		return getAllPackages().stream().flatMap((p) -> p.getCurrentVersionInfo().getProcesses().stream());
		return getVersionInfoStream().flatMap(i -> i.getProcesses().stream()); //TODO check this
	}

	private Stream<PackageVersionInfo> getVersionInfoStream() {
		return getAllPackages().stream().flatMap((p) -> list(p.getCurrentVersionInfo()).with(p.getPackageVersions().values()).stream()); //TODO check this
	}

	private @Nullable
	ProcessInfo getProcessInfoByDefinition(String processDefinitionId) {
		return getProcessInfoStream().filter((p) -> equal(p.getDefinitionId(), processDefinitionId)).findAny().orElse(null);
	}

	private @Nullable
	ProcessInfo getProcessInfoByClass(String className) {
		return getProcessInfoStream().filter((p) -> equal(p.getClassName(), className)).findAny().orElse(null);
	}

	private @Nullable
	PackageInfo getPackageInfoByClass(String className) {
//		return getAllPackages().stream().filter((p) -> p.getCurrentVersionInfo().getProcesses().stream().anyMatch((pv) -> equal(pv.getClassName(), className))).findAny().orElse(null);
		return getAllPackages().stream().filter((p) -> p.getCurrentVersionInfo().getProcesses().stream().anyMatch((pv) -> equal(pv.getClassName(), className))).findAny().orElse(null);//TODO check this
	}

	private @Nullable
	PackageInfo getPackageInfoById(String pkgId) {
		return getAllPackages().stream().filter((p) -> equal(p.getId(), pkgId)).findAny().orElse(null);
	}

	private List<PackageInfo> getAllPackages() {
		return allPackages.get(this::doGetAllPackages);
	}

	private List<PackageInfo> doGetAllPackages() {
		Map<String, PackageInfo> packageInfoById = map();
		return remoteRepository.downloadAllPackages().stream().map((p) -> {
			PackageVersionInfo currentVersionInfo = createPackageVersionInfo(p.getData(), p.getVersion());
			PackageInfo packageInfo = packageInfoById.get(p.getPackageId());
			if (packageInfo == null) {
				packageInfo = new PackageInfo(p.getPackageId(), p.getVersion(), currentVersionInfo);
				packageInfoById.put(p.getPackageId(), packageInfo);
			} else {
				packageInfo.setCurrentVersion(p.getVersion(), currentVersionInfo);
			}
			return packageInfo;
		}).collect(toList());
	}

	private PackageVersionInfo createPackageVersionInfo(byte[] pkgDef, String version) {
		return new XpdlPackageVersionInfo(pkgDef, version);
	}

	private class XpdlPackageVersionInfo implements PackageVersionInfo {

		private final Map<String, ProcessInfo> processInfoById;
		private final byte[] raw;
		private final String version;
		private final String packageId;

		public XpdlPackageVersionInfo(byte[] pkgDef, String version) {
			this.version = checkNotBlank(version);
			Package xpdlPackage = XpdlPackageFactory.readXpdl(pkgDef);
			packageId = checkNotBlank(xpdlPackage.getId());
			XpdlDocumentHelper xpdl = new XpdlDocumentHelper(xpdlPackage);
			this.processInfoById = createProcessInfoMap(xpdl);
			this.raw = pkgDef;
		}

		private Map<String, ProcessInfo> createProcessInfoMap(XpdlDocumentHelper xpdl) {
			Map<String, ProcessInfo> map = map();
			xpdl.findAllProcesses().forEach((xproc) -> {
				String className = xproc.getBindToClass();
				if (isNotBlank(className)) {
					ProcessInfo pi = new XpdlProcessInfo(xproc);
					map.put(pi.getDefinitionId(), pi);
				}
			});
			return map;
		}

		@Override
		public byte[] getRawDefinition() {
			return raw;
		}

		@Override
		public Collection<ProcessInfo> getProcesses() {
			return processInfoById.values();
		}

		@Override
		public ProcessInfo getProcess(String procDefId) {
			return processInfoById.get(procDefId);
		}

		private class XpdlProcessInfo implements ProcessInfo {

			private final XpdlProcess xproc;
			private final String planId;

			public XpdlProcessInfo(XpdlProcess xproc) {
				this.xproc = xproc;
				this.planId = PlanIdUtils.buildPlanId(packageId, version, xproc.getId());
			}

			@Override
			public String getClassName() {
				return xproc.getBindToClass();
			}

			@Override
			public String getDefinitionId() {
				return xproc.getId();
			}

			@Override
			public List<TaskDefinition> getEntryTasks() {
				return xproc.getManualStartActivitiesRecursive().stream().map(this::toTaskDefinition).collect(toList());
			}

			@Override
			public TaskDefinition getTaskById(String activityDefinitionId) {
				XpdlActivity xact = xproc.getActivity(activityDefinitionId);
				return toTaskDefinition(xact);
			}

			@Override
			public List<TaskDefinition> getAllTasks() {
				return xproc.getAllActivities().stream().map(this::toTaskDefinition).collect(toList());
			}

			private TaskDefinition toTaskDefinition(XpdlActivity xpdlActivity) {
				return new XpdlActivityWrapper(xpdlActivity, xpdlVariableFactory, xpdlMetadataFactory, xpdlWidgetFactory);
			}

			@Override
			public String getPlanId() {
				return planId;
			}
		}
	}
}
