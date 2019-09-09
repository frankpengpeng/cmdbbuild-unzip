/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static java.lang.Long.parseLong;
import java.util.List;
import javax.activation.DataHandler;
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
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.gis.GeoserverLayer;
import org.cmdbuild.gis.GeoserverLayerImpl;
import org.cmdbuild.gis.GeoserverLayerImpl.GeoserverLayerImplBuilder;
import org.cmdbuild.gis.GisService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ZOOM_DEF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ZOOM_MAX;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ZOOM_MIN;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.security.access.prepost.PreAuthorize;

@Path("{a:processes|classes}/{" + CLASS_ID + "}/{b:cards|instances}/{" + CARD_ID + "}/geolayers/")
@Produces(APPLICATION_JSON)
public class GeoserverLayerWs {

    private final GisService service;
    private final DaoService dao;

    public GeoserverLayerWs(GisService service, DaoService dao) {
        this.service = checkNotNull(service);
        this.dao = checkNotNull(dao);
    }

    @GET
    @Path("")
    public Object getAllForCard(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) String cardId, @QueryParam("visible") @DefaultValue(FALSE) Boolean isVisible) {
        List<GeoserverLayer> list;
        if (equal(classId, "_ANY")) {
            list = service.getGeoServerLayers();
        } else if (equal(cardId, "_ANY")) {
            if (isVisible == true) {
                list = service.getGeoLayersVisibleFromClass(classId);
            } else {
                list = service.getGeoLayersOwnedByClass(classId);
            }
        } else {
            list = service.getGeoServerLayersForCard(classId, parseLong(cardId));
        }
        return response(list.stream().map((l) -> serializeLayer(l)));
    }

    @POST
    @Path("")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @Multipart("data") WsGeoLayer attributeData, @Multipart(FILE) DataHandler dataHandler) {
        GeoserverLayer layer = service.createGeoServerLayer(attributeData.toGisAttribute()
                .withOwnerClassId(classId)
                .withOwnerCardId(cardId)
                .build(), dataHandler);
        return response(serializeLayer(layer));
    }

    @PUT
    @Path("{layerId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam("layerId") Long layerId, WsGeoLayer attributeData) {
        GeoserverLayer layer = service.updateGeoserverLayer(attributeData.toGisAttribute()
                .withOwnerClassId(classId)
                .withOwnerCardId(cardId)
                .withId(layerId).build());
        return response(serializeLayer(layer));
    }

    @DELETE
    @Path("{layerId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam("layerId") Long layerId) {
        service.deleteGeoServerLayer(layerId);
        return success();
    }

    @POST
    @Path("order")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object reorder(@PathParam(CLASS_ID) String classId, List<Long> layerOrder) {
        checkArgument(equal(classId, "_ANY"), "service available only for _ANY classes");
        List<GeoserverLayer> layers = service.updateGeoserverLayersOrder(layerOrder);
        return response(layers.stream().map(this::serializeLayer));
    }

    private FluentMap serializeLayer(GeoserverLayer l) {
        return map(
                "_id", l.getId(),
                "name", l.getLayerName(),
                "active", l.getActive(),
                "type", l.getType(),
                "index", l.getIndex(),
                "geoserver_name", l.getGeoserverName(),
                "description", l.getDescription(),
                ZOOM_MIN, l.getMinimumZoom(),
                ZOOM_DEF, l.getDefaultZoom(),
                ZOOM_MAX, l.getMaximumZoom(),
                "visibility", l.getVisibility(),
                "owner_type", l.getOwnerClassId(),
                "owner_id", l.getOwnerCardId());
    }

    public static class WsGeoLayer {

        private final String name, type, geoserverName, description;
        private final Integer index, zoomMin, zoomDef, zoomMax;
        private final List<String> visibility;
        private final boolean active;

        public WsGeoLayer(
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("active") Boolean active,
                @JsonProperty("type") String type,
                @JsonProperty("index") Integer index,
                @JsonProperty("geoserver_name") String geoserverName,
                @JsonProperty("zoomMin") Integer zoomMin,
                @JsonProperty("zoomDef") Integer zoomDef,
                @JsonProperty("zoomMax") Integer zoomMax,
                @JsonProperty("visibility") List<String> visibility) {
            this.name = name;
            this.description = description;
            this.active = active;
            this.type = type;
            this.geoserverName = geoserverName;
            this.index = index;
            this.zoomMin = zoomMin;
            this.zoomDef = zoomDef;
            this.zoomMax = zoomMax;
            this.visibility = ImmutableList.copyOf(visibility);
        }

        public GeoserverLayerImplBuilder toGisAttribute() {
            return GeoserverLayerImpl.builder()
                    .withLayerName(name)
                    .withDescription(description)
                    .withActive(active)
                    .withType(type)
                    .withIndex(index)
                    .withMinimumZoom(zoomMin)
                    .withDefaultZoom(zoomDef)
                    .withMaximumZoom(zoomMax)
                    .withGeoserverName(geoserverName)
                    .withVisibility(set(visibility));
        }

    }
}
