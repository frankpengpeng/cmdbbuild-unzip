package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.UnsupportedEncodingException;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupTypeImpl;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LOOKUP_TYPE_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.security.access.prepost.PreAuthorize;

import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;

@Path("lookup_types/")
@Produces(APPLICATION_JSON)
public class LookupTypeWs {

	private final LookupService lookupService;

	public LookupTypeWs(LookupService lookupLogic) {
		this.lookupService = checkNotNull(lookupLogic);
	}

	@GET
	@Path("{" + LOOKUP_TYPE_ID + "}/")
	public Object read(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId) {
		return response(toResponse(lookupService.getLookupType(decodeIfHex(lookupTypeId))));
	}

	@GET
	@Path(EMPTY)
	public Object readAll(@QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(FILTER) String filter) {
		PagedElements<LookupType> lookupTypes = lookupService.getAllTypes(offset, limit, filter);
		return response(lookupTypes.stream().map(this::toResponse).collect(toList()), lookupTypes.totalSize());
	}

	@POST
	@Path("")
	@PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
	public Object createLookupType(WsLookupType wsLookupType) {
		return response(toResponse(lookupService.createLookupType(wsLookupType.toLookupType())));
	}

	@PUT
	@Path("{" + LOOKUP_TYPE_ID + "}/")
	@PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
	public Object updateLookupType(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId, WsLookupType wsLookupType) {
		return response(toResponse(lookupService.updateLookupType(decodeIfHex(lookupTypeId), wsLookupType.toLookupType())));
	}

	@DELETE
	@Path("{" + LOOKUP_TYPE_ID + "}/")
	@PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
	public Object deleteLookupType(@PathParam(LOOKUP_TYPE_ID) String lookupTypeId) {
		lookupService.deleteLookupType(decodeIfHex(lookupTypeId));
		return success();
	}

	private Object toResponse(LookupType lookupType) {
		return map(
				"_id", lookupType.getName(),
				"name", lookupType.getName(),
				"parent", lookupType.getParentOrNull());
	}

	public static String decodeIfHex(String value) {
		if (nullToEmpty(value).matches("(0x|HEX)[0-9a-fA-F]*")) {
			try {
				return new String(Hex.decodeHex(value.replaceFirst("^(0x|HEX)", "")), "UTF8");
			} catch (DecoderException | UnsupportedEncodingException ex) {
				throw runtime(ex, "error decoding hex value = %s", value);
			}
		} else {
			return value;
		}
	}

	public static class WsLookupType {

		private final String name, parent;

		public WsLookupType(@JsonProperty("name") String name, @JsonProperty("parent") @Nullable String parent) {
			this.name = checkNotBlank(name);
			this.parent = emptyToNull(parent);
		}

		public LookupType toLookupType() {
			return LookupTypeImpl.builder().withName(name).withParent(parent).build();
		}

	}
}
