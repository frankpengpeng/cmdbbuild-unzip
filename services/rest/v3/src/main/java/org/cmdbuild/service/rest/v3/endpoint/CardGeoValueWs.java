package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.gis.GisValue;
import org.cmdbuild.gis.GisValueType;
import org.cmdbuild.gis.model.GisValueImpl;
import org.cmdbuild.gis.model.LinestringImpl;
import org.cmdbuild.gis.model.PointImpl;
import org.cmdbuild.gis.model.PolygonImpl;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;

import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import org.cmdbuild.service.rest.common.utils.WsSerializationUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("{a:processes|classes}/{" + CLASS_ID + "}/{b:cards|instances}/{" + CARD_ID + "}/geovalues")
@Produces(APPLICATION_JSON)
public class CardGeoValueWs {

	private final GisService service;

	public CardGeoValueWs(GisService logic) {
		this.service = checkNotNull(logic);
	}

	@GET
	@Path("")
	public Object getAllForCard(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId) {
		return response(service.getValues(classId, cardId).stream().map(WsSerializationUtils::serializeGeometry).collect(toList()));
	}

	@GET
	@Path("/{attributeId}")
	public Object get(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam("attributeId") String attributeId) {
		return response(WsSerializationUtils.serializeGeometry(service.getValue(classId, cardId, attributeId)));
	}

	@PUT
	@Path("/{attributeId}")
	public Object set(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam("attributeId") String attributeId, WsGisValue data) {
		GisValue value = GisValueImpl.builder()
				.withOwnerClassId(classId)
				.withOwnerCardId(cardId)
				.withLayerName(attributeId)
				.accept((b) -> {
					switch (data.type) {
						case POINT:
							b.withGeometry(new PointImpl(((WsPoint) data.geometry).x, ((WsPoint) data.geometry).y));
							break;
						case LINESTRING:
							b.withGeometry(new LinestringImpl(((WsPoints) data.geometry).points.stream().map((p) -> new PointImpl(p.x, p.y)).collect(toList())));
							break;
						case POLYGON:
							b.withGeometry(new PolygonImpl(((WsPoints) data.geometry).points.stream().map((p) -> new PointImpl(p.x, p.y)).collect(toList())));
							break;
						default:
							throw unsupported("unsupported geometry type = %s", data.type);
					}
				}).build();
		value = service.setValue(value);
		return response(WsSerializationUtils.serializeGeometry(value));
	}

	@DELETE
	@Path("/{attributeId}")
	public Object delete(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @PathParam("attributeId") String attributeId) {
		service.deleteValue(classId, cardId, attributeId);
		return success();
	}

	public static class WsGisValue {

		public final GisValueType type;
		public final Object geometry;

		public WsGisValue(
				@JsonProperty("_type") String type,
				@JsonProperty("x") Double x,
				@JsonProperty("y") Double y,
				@JsonProperty("points") List<WsPoint> points) {
			this.type = GisValueType.valueOf(checkNotBlank(type).toUpperCase());
			switch (this.type) {
				case POINT:
					geometry = new WsPoint(x, y);
					break;
				case LINESTRING:
				case POLYGON:
					geometry = new WsPoints(points);
					break;
				default:
					throw unsupported("unsupported geometry type = %s", this.type);
			}
		}

	}

	public static class WsPoint {

		public final double x, y;

		public WsPoint(@JsonProperty("x") Double x, @JsonProperty("y") Double y) {
			this.x = x;
			this.y = y;
		}

	}

	public static class WsPoints {

		public final List<WsPoint> points;

		public WsPoints(List<WsPoint> points) {
			this.points = checkNotNull(points);
		}

	}

}
