package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClass;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClassImpl;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.security.access.prepost.PreAuthorize;

@Path("roles/{roleId}/filters")
@Produces(APPLICATION_JSON)
@PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
public class RoleClassFilterWs {

    private final RoleRepository roleRepository;
    private final CardFilterService filterService;
    private final DaoService dao;

    public RoleClassFilterWs(RoleRepository roleRepository, CardFilterService filterService, DaoService dao) {
        this.roleRepository = checkNotNull(roleRepository);
        this.filterService = checkNotNull(filterService);
        this.dao = checkNotNull(dao);
    }

    @GET
    @Path("")
    public Object read(@PathParam("roleId") String roleId) {
        Role role = roleRepository.getByNameOrId(roleId);
        List<CardFilterAsDefaultForClass> filters = filterService.getDefaultFiltersForRole(role.getId());
        return response(filters.stream().map(f -> map(
                "_id", f.getFilter().getId(),
                "_defaultFor", f.getDefaultForClass().getName()
        )));
    }

    @POST
    @Path("")
    public Object updateWithPost(@PathParam("roleId") String roleId, List<WsDefaultStoredFilterForClass> filters) {
        Role role = roleRepository.getByNameOrId(roleId);
        List<CardFilterAsDefaultForClass> filtersUpdate = filters.stream().map(f -> new CardFilterAsDefaultForClassImpl(filterService.getById(f.getId()), dao.getClasse(f.getForClass()), role.getId())).collect(toList());
        filterService.setDefaultFiltersForRole(role.getId(), filtersUpdate);
        return read(roleId);
    }

    public static class WsDefaultStoredFilterForClass {

        private final long id;
        private final String forClass;

        public WsDefaultStoredFilterForClass(@JsonProperty("_id") Long id, @JsonProperty("_defaultFor") String forClass) {
            this.id = id;
            this.forClass = checkNotNull(forClass);
        }

        public long getId() {
            return id;
        }

        public String getForClass() {
            return forClass;
        }

    }
}
