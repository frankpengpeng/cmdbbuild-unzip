package org.cmdbuild.service.rest.v3.endpoint;

import static java.util.Collections.emptyList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

@Path("dashboards/")
@Produces(APPLICATION_JSON)
public class DashboardWs {

	private final Logger logger = LoggerFactory.getLogger(getClass());

//	private final DashboardService dashboardLogic;
//
//	public DashboardWs(DashboardService dashboardLogic) {
//		this.dashboardLogic = checkNotNull(dashboardLogic);
//	}
	@GET
	@Path(EMPTY)
	public Object list() {
//		logger.debug("list all dashboards");
//		Map<Integer, DashboardDefinition> dashboards = dashboardLogic.listDashboards();
//		logger.debug("processing dashboards = {}", dashboards);
//		List<DashboardJsonBean> elements = dashboards.entrySet().stream().map((entry) -> DashboardJsonBean.builder().withDashboardId(entry.getKey()).withDashboardDefinition(entry.getValue()).build()).collect(toList());
		return response(emptyList());//no dashboards yet
	}
}
