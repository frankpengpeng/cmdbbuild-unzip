/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import java.io.IOException;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.bim.BimProject;
import org.cmdbuild.bim.BimProjectExt;
import org.cmdbuild.bim.BimProjectExtImpl;
import org.cmdbuild.bim.BimProjectImpl;
import org.cmdbuild.bim.BimProjectImpl.BimProjectImplBuilder;
import org.cmdbuild.bim.BimService;
import static org.cmdbuild.bim.utils.BimConstants.IFC_CONTENT_TYPE;
import org.cmdbuild.dao.beans.CardIdAndClassName;
import static org.cmdbuild.dao.beans.CardIdAndClassNameImpl.card;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("bim/projects/")
@Produces(APPLICATION_JSON)
public class BimProjectWs {

    private final BimService bim;

    public BimProjectWs(BimService bim) {
        this.bim = checkNotNull(bim);
    }

    @GET
    @Path("")
    public Object getAll() {
        return response(bim.getAllProjectsAndObjects().stream().sorted(Ordering.natural().onResultOf(BimProjectExt::getName)).map(this::serializeProjectAndObject));
    }

    @GET
    @Path("{id}")
    public Object getOne(@PathParam("id") Long id) {
        BimProjectExt projectExt = bim.getProjectExt(id);
        return response(serializeProjectAndObject(projectExt));
    }

    @POST
    @Path("")
    public Object createProject(WsProjectData data) {
        return response(serializeProject(bim.createProjectExt(data.toBimProject().build(), data.toOwnerOrNull())));
    }

    @POST
    @Path("")
    @Consumes(MULTIPART_FORM_DATA)
    public Object createProjectWithFile(@Multipart(FILE) DataHandler dataHandler, @Multipart("data") WsProjectData data, @QueryParam("ifcFormat") @Nullable String ifcFormat) {
        BimProjectExt project = bim.createProjectExt(new BimProjectExtImpl(data.toBimProject().build(), data.toOwnerOrNull()));
        bim.uploadIfcFile(project.getId(), dataHandler, ifcFormat);
        project = bim.getProjectExt(project.getId());
        return response(serializeProject(project));
    }

    @PUT
    @Path("{id}")
    public Object update(@PathParam("id") Long id, WsProjectData data) {
        return response(serializeProject(bim.updateProjectExt(data.toBimProject().withId(id).build(), data.toOwnerOrNull())));
    }

    @GET
    @Path("{id}/file")
    @Produces(IFC_CONTENT_TYPE)
    public DataHandler downloadIfcFile(@PathParam("id") Long id, @QueryParam("ifcFormat") @Nullable String ifcFormat) {
        return bim.downloadIfcFile(id, ifcFormat);
    }

    @POST
    @Path("{id}/file")
    @Consumes(MULTIPART_FORM_DATA)
    public Object uploadIfcFile(@PathParam("id") Long id, @Multipart(FILE) DataHandler dataHandler, @QueryParam("ifcFormat") @Nullable String ifcFormat) throws IOException {
        bim.uploadIfcFile(id, dataHandler, ifcFormat);
        return success();
    }

    @DELETE
    @Path("{id}")
    public Object delete(@PathParam("id") Long id) {
        bim.deleteProjectById(id);
        return success();
    }

    private FluentMap<String, Object> serializeProject(BimProject p) {
        return map(
                "_id", p.getId(),
                "parentId", p.getParentId(),
                "name", p.getName(),
                "description", p.getDescription(),
                "lastCheckin", toIsoDateTime(p.getLastCheckin()),
                "projectId", p.getProjectId(),
                "active", p.isActive()
        );
    }

    private Object serializeProjectAndObject(BimProjectExt projectAndObject) {
        return serializeProject(projectAndObject).accept(m -> {
            if (projectAndObject.hasOwner()) {
                m.put(
                        "ownerClass", projectAndObject.getOwner().getClassName(),
                        "ownerCard", projectAndObject.getOwner().getId());
            }
        });
    }

    public static class WsProjectData {

        private final String name, description, importMapping, projectId, ownerClass;
        private final Boolean active;
        private final Long parentId, ownerCard;

        public WsProjectData(
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("importMapping") String importMapping,
                @JsonProperty("ownerClass") String ownerClass,
                @JsonProperty("parentId") Long parentId,
                @JsonProperty("ownerCard") Long ownerCard,
                @JsonProperty("active") Boolean active,
                @JsonProperty("projectId") String projectId) {
            this.projectId = projectId;
            this.name = checkNotBlank(name);
            this.description = description;
            this.ownerClass = ownerClass;
            this.importMapping = importMapping;
            this.active = firstNonNull(active, true);
            this.parentId = parentId;
            this.ownerCard = ownerCard;
        }

        public BimProjectImplBuilder toBimProject() {
            return BimProjectImpl.builder()
                    .withName(name)
                    .withDescription(description)
                    .withActive(active)
                    .withParentId(parentId)
                    .withImportMapping(importMapping)
                    .withProjectId(projectId);
        }

        @Nullable
        public CardIdAndClassName toOwnerOrNull() {
            if (isNotBlank(ownerClass) && isNotNullAndGtZero(ownerCard)) {
                return card(ownerClass, ownerCard);
            } else {
                return null;
            }

        }

    }
}
