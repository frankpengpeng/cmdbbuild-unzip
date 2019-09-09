package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.WILDCARD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.springframework.security.access.prepost.PreAuthorize;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_SYSTEM_ACCESS_AUTHORITY;
import org.cmdbuild.services.Minion;
import org.cmdbuild.services.MinionService;
import static org.cmdbuild.services.SystemServiceStatusUtils.serializeMinionStatus;

@Path("system_services/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@PreAuthorize(HAS_SYSTEM_ACCESS_AUTHORITY)
public class MinionsWs {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final MinionService minionService;

	public MinionsWs(MinionService servicesStatusService) {
		this.minionService = checkNotNull(servicesStatusService);
	}

	@GET
	@Path("")
	public Object getServicesStatus() {
		return response(minionService.getMinions().stream().sorted(Ordering.natural().onResultOf(Minion::getName)).map(this::serializeServiceStatus));
	}

	@GET
	@Path("{serviceId}")
	public Object getServiceStatus(@PathParam("serviceId") String serviceId) {
		return response(serializeServiceStatus(minionService.getMinion(serviceId)));
	}

	@POST
	@Path("{serviceId}/start")
	@Consumes(WILDCARD)
	public Object startService(@PathParam("serviceId") String serviceId) {
		minionService.startMinion(serviceId);
		return response(serializeServiceStatus(minionService.getMinion(serviceId)));
	}

	@POST
	@Path("{serviceId}/stop")
	@Consumes(WILDCARD)
	public Object stopService(@PathParam("serviceId") String serviceId) {
		minionService.stopMinion(serviceId);
		return response(serializeServiceStatus(minionService.getMinion(serviceId)));
	}

	private Object serializeServiceStatus(Minion minion) {
		return map("_id", minion.getId(),
				"name", minion.getName(),
				"status", serializeMinionStatus(minion.getStatus()),
				"_is_enabled", minion.isEnabled(),
				"_can_start", minion.canStart(),
				"_can_stop", minion.canStop()
		);
	}

}
