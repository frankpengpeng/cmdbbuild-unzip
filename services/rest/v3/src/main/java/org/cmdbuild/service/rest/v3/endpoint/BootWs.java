package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.WILDCARD;
import org.cmdbuild.dao.config.inner.Patch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.service.rest.common.utils.WsSerializationUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.services.SystemService;
import static org.cmdbuild.services.SystemStatusUtils.serializeSystemStatus;
import org.cmdbuild.dao.config.inner.PatchService;

@Path("boot/")
@Produces(APPLICATION_JSON)
public class BootWs {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final SystemService systemService;
	private final PatchService patchManager;

	public BootWs(SystemService bootService, PatchService patchManager) {
		this.systemService = checkNotNull(bootService);
		this.patchManager = checkNotNull(patchManager);
	}

	@GET
	@Path("status")
	public Object status() {
		Map map = map(success());
		map.put("status", serializeSystemStatus(systemService.getSystemStatus()));
		if (systemService.isWaitingForUser() && !patchManager.isUpdated()) {
			map.put("operationRequired", "applyPatch");
		}
		return map;
	}

	@GET
	@Path("patches")
	public Object getPendingPatches() {
		List<Patch> patches = patchManager.getAvailableCorePatches();
		List list = patches.stream().map(WsSerializationUtils::serializePatchInfo).collect(toList());
		return response(list);
	}

	@POST
	@Path("patches/apply")
	@Consumes(WILDCARD)
	public Object applyPendingPatches() {
		logger.info("applyPendingPatches");
		patchManager.applyPendingPatchesAndFunctions();
		return success();
	}

}
