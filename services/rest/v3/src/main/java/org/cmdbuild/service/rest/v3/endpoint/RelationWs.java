package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.classe.access.UserDomainService;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.RelationImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CREATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_SERVICE;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelper;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import org.cmdbuild.service.rest.v3.endpoint.CardRelationWs.WsRelationData;

@Path("domains/{domainId}/relations")
@Produces(APPLICATION_JSON)
public class RelationWs {

    private final UserDomainService domainService;
    private final DaoService dao;
    private final CardWsSerializationHelper helper;

    public RelationWs(UserDomainService domainService, DaoService dao, CardWsSerializationHelper helper) {
        this.domainService = checkNotNull(domainService);
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@PathParam("domainId") String domainId, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed) {
        return response(domainService.getUserRelations(domainId, DaoQueryOptionsImpl.builder().withPaging(offset, limit).build()).map(detailed ? helper::serializeDetailedRelation : helper::serializeMinimalRelation));
    }

    @GET
    @Path("{relationId}/")
    public Object read(@PathParam("domainId") String domainId, @PathParam("relationId") Long relationId) {
        return response(helper.serializeDetailedRelation(domainService.getUserRelation(domainId, relationId)));
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("domainId") String domainId, WsRelationData relationData) {
        Domain domain = domainService.getUserDomain(domainId);
        domain.checkPermission(PS_SERVICE, CP_CREATE);
        relationData = relationData.getDataDirect();
        CMRelation relation = RelationImpl.builder()
                .withType(domain)
                .withSourceCard(dao.getCard(relationData.getSourceClassId(), relationData.getSourceCardId()))
                .withTargetCard(dao.getCard(relationData.getDestinationClassId(), relationData.getDestinationCardId()))
                .addAttributes(relationData.getValues())
                .build();
        relation = dao.create(relation);
        return response(helper.serializeDetailedRelation(relation));
    }

    @PUT
    @Path("{relationId}/")
    public Object update(@PathParam("domainId") String domainId, @PathParam("relationId") Long relationId, WsRelationData relationData) {
        CMRelation relation = domainService.getUserRelation(domainId, relationId);
        relation.getType().checkPermission(PS_SERVICE, CP_UPDATE);
        relationData = relationData.getDataDirect();
        relation = RelationImpl.copyOf(relation)
                .withSourceCard(dao.getCard(relationData.getSourceClassId(), relationData.getSourceCardId()))
                .withTargetCard(dao.getCard(relationData.getDestinationClassId(), relationData.getDestinationCardId()))
                .addAttributes(relationData.getValues())
                .build();
        relation = dao.update(relation);
        return response(helper.serializeDetailedRelation(relation));
    }

    @DELETE
    @Path("{relationId}/")
    public Object delete(@PathParam("domainId") String domainId, @PathParam("relationId") Long relationId) {
        CMRelation relation = domainService.getUserRelation(domainId, relationId);
        relation.getType().checkPermission(PS_SERVICE, CP_DELETE);
        dao.delete(relation);
        return success();
    }

}
