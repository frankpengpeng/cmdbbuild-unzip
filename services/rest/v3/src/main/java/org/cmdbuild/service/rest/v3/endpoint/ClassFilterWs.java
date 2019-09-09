package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClass;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClassImpl;
import org.cmdbuild.cardfilter.StoredFilterImpl;
import org.cmdbuild.cardfilter.StoredFilterImpl.StoredFilterImplBuilder;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.cardfilter.StoredFilter;
import org.cmdbuild.dao.core.q3.DaoService;

@Path("{a:classes|processes}/{" + CLASS_ID + "}/filters/")
@Produces(APPLICATION_JSON)
public class ClassFilterWs {

    private final CardFilterService filterService;
    private final DaoService dao;
    private final OperationUserSupplier userStore;

    public ClassFilterWs(CardFilterService filterService, DaoService dao, OperationUserSupplier userStore) {
        this.filterService = checkNotNull(filterService);
        this.dao = checkNotNull(dao);
        this.userStore = checkNotNull(userStore);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@PathParam(CLASS_ID) String classId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam("shared") boolean sharedOnly) {
        List<StoredFilter> list;
        if (equal(classId, "_ANY")) {
            list = filterService.readAllSharedFilters();
        } else if (sharedOnly) {
            list = filterService.readSharedForCurrentUser(classId);
        } else {
            list = filterService.readAllForCurrentUser(classId);
        }
        PagedElements<StoredFilter> res = paged(list, offset, limit);
        return response(res.stream().map(ClassFilterWs::serializeFilter).collect(toList()), res.totalSize());
    }

    @GET
    @Path("{" + FILTER_ID + "}/")
    public Object read(@PathParam(CLASS_ID) String classId, @PathParam(FILTER_ID) Long filterId) {
        return response(serializeFilter(filterService.getById(filterId)));
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam(CLASS_ID) String classId, WsFilterData element) {
        StoredFilter filter = filterService.create(element.toCardFilter().accept(setCurrentUserForNonSharedFiltersVisitor(element)).build());
        return response(serializeFilter(filter));
    }

    @PUT
    @Path("{" + FILTER_ID + "}/")
    public Object update(@PathParam(CLASS_ID) String classId, @PathParam(FILTER_ID) Long filterId, WsFilterData element) {
        StoredFilter filter = filterService.update(element.toCardFilter().withId(filterId).accept(setCurrentUserForNonSharedFiltersVisitor(element)).build());
        return response(serializeFilter(filter));
    }

    @DELETE
    @Path("{" + FILTER_ID + "}/")
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam(FILTER_ID) Long filterId) {
        filterService.delete(filterId);
        return success();
    }

    @GET
    @Path("{filterId}/defaultFor/")
    public Object getDefaultForRoles(@PathParam(CLASS_ID) String classId, @PathParam(FILTER_ID) Long filterId) {
        return response(filterService.getDefaultFiltersForFilter(filterId).stream().filter(f -> equal(f.getDefaultForClass().getName(), f.getFilter().getClassName())).map(f -> map(
                "_id", f.getDefaultForRole()
//                "defaultFor", f.getDefaultForClass()
        )));
    }

    @POST
    @Path("{filterId}/defaultFor/")
    public Object updateWithPost(@PathParam(CLASS_ID) String classId, @PathParam("filterId") Long filterId, List<WsDefaultStoredFilter> roles) {
        StoredFilter filter = filterService.getById(filterId);
        List<CardFilterAsDefaultForClass> filters = roles.stream().map(r -> new CardFilterAsDefaultForClassImpl(filter, dao.getClasse(filter.getClassName()), r.getId())).collect(toList());
        filterService.setDefaultFiltersForFilterWithMatchingClass(filter.getId(), filters);
        return getDefaultForRoles(classId, filterId);
    }
 
    public static FluentMap<String, Object> serializeFilter(StoredFilter filter) {
        return map(
                "_id", filter.getId(),
                "name", filter.getName(),
                "description", filter.getDescription(),
                "target", filter.getClassName(),
                "configuration", filter.getConfiguration(),
                "shared", filter.isShared()
        );
    }

    private Consumer<StoredFilterImplBuilder> setCurrentUserForNonSharedFiltersVisitor(WsFilterData data) {
        return (b) -> {
            if (!data.shared) {
                b.withUserId(userStore.getUser().getLoginUser().getId());
            }
        };
    }

    public static class WsFilterData {

        private final String name;
        private final String description;
        private final String target;
        private final String configuration;
        private final boolean shared;

        public WsFilterData(@JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("target") String target,
                @JsonProperty("configuration") String configuration,
                @JsonProperty("shared") Boolean shared) {
            this.name = checkNotBlank(name, "missing required param 'name'");
            this.description = description;
            this.target = checkNotBlank(target, "missing required param 'target'");
            this.configuration = configuration;
            this.shared = shared;
        }

        public StoredFilterImplBuilder toCardFilter() {
            return StoredFilterImpl.builder()
                    .withClassName(target)
                    .withConfiguration(configuration)
                    .withDescription(description)
                    .withName(name)
                    .withShared(shared);
        }
    }

    public static class WsDefaultStoredFilter {

        private final long id;

        public WsDefaultStoredFilter(@JsonProperty("_id") Long roleId) {
            this.id = roleId;
        }

        public long getId() {
            return id;
        }

    }
}
