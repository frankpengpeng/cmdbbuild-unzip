package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.cmdbuild.common.utils.PagedElements;

import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DOMAIN_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.v3.endpoint.ClassAttributeWs.prepareAttributesToUpdateForOrder;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.springframework.security.access.prepost.PreAuthorize;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.service.rest.v3.endpoint.ClassAttributeWs.WsAttributeData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Path("domains/{" + DOMAIN_ID + "}/attributes/")
@Produces(APPLICATION_JSON)
public class DomainAttributeWs {

    private final DaoService dao;
    private final AttributeTypeConversionService conversionService;

    public DomainAttributeWs(DaoService dao, AttributeTypeConversionService conversionService) {
        this.dao = checkNotNull(dao);
        this.conversionService = checkNotNull(conversionService);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@PathParam(DOMAIN_ID) String domainId, @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        Domain domain = dao.getDomain(domainId);
        List<Attribute> list = domain.getServiceAttributes();
        if (!isAdminViewMode(viewMode)) {
            list = list(list).without(a -> !a.isActive());
        }
        //TODO attributes filtered by user (?)
        PagedElements<Attribute> attributes = PagedElements.paged(list, offset, limit);
        return response(attributes.stream().map(conversionService::serializeAttributeType).collect(toList()), attributes.totalSize());
    }

    @GET
    @Path("{attrId}/")
    public Object read(@PathParam(DOMAIN_ID) String domainId, @PathParam("attrId") String attrId) {
        Domain domain = dao.getDomain(domainId);
        Attribute attribute = domain.getAttribute(attrId);
        return response(conversionService.serializeAttributeType(attribute));
    }

    @POST
    @Path(EMPTY)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(@PathParam(DOMAIN_ID) String domainId, WsAttributeData data) {
        checkNotNull(data);
        Domain domain = dao.getDomain(domainId);
        checkArgument(domain.getAttributeOrNull(data.getName()) == null, "attribute already present in domain = %s for name = %s", domainId, data.getName());
        Attribute attribute = dao.createAttribute(data.toAttrDefinition(domain));
        return response(conversionService.serializeAttributeType(attribute));
    }

    @PUT
    @Path("{attrId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam(DOMAIN_ID) String domainId, @PathParam("attrId") String attrId, WsAttributeData data) {
        checkNotNull(data);
        Domain domain = dao.getDomain(domainId);
        domain.getAttribute(attrId);
        checkArgument(equal(attrId, data.getName()), "data attr name = %s does not match with path attr id = %s", data.getName(), attrId);
        Attribute attribute = dao.updateAttribute(data.toAttrDefinition(domain));//TODO check metadata persistence
        return response(conversionService.serializeAttributeType(attribute));
    }

    @DELETE
    @Path("{attrId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object delete(@PathParam(DOMAIN_ID) String domainId, @PathParam("attrId") String attrId) {
        Domain domain = dao.getDomain(domainId);
        Attribute attribute = domain.getAttribute(attrId);
        dao.deleteAttribute(attribute);
        return success();
    }

    @POST
    @Path("order")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object reorder(@PathParam(DOMAIN_ID) String domainId, List<String> attrOrder) {
        checkNotNull(attrOrder);

        Domain domain = dao.getDomain(domainId);

        dao.updateAttributes(prepareAttributesToUpdateForOrder(domain::getAttribute, attrOrder));

        domain = dao.getDomain(domainId);

        return response(attrOrder.stream().map(domain::getAttribute).map(conversionService::serializeAttributeType).collect(toList()), attrOrder.size());
    }

}
