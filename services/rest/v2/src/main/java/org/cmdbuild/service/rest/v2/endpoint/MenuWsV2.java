package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.menu.MenuService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.security.access.prepost.PreAuthorize;

@Path("menu/")
@Produces(APPLICATION_JSON)
public class MenuWsV2 {

    private final MenuService menuService;

    public MenuWsV2(MenuService menuService) {
        this.menuService = checkNotNull(menuService);
    }

    @GET
    @Path(EMPTY)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object read() {
        return map("data", menuService.getAllMenuInfos().stream().map((m) -> map("_id", m.getId(), "group", m.getGroup())).collect(toList()));

    }

}
