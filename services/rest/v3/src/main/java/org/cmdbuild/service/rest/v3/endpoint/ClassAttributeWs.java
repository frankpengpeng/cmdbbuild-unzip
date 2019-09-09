package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
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
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.common.utils.PagedElements.paged;

import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.springframework.security.access.prepost.PreAuthorize;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.parseAttributePermissionMode;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.entrytype.attributetype.AttributeUtils;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.UnitOfMeasureLocation;
import static org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService.TYPE_NAME_MAP;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Path("{a:classes|processes}/{" + CLASS_ID + "}/attributes/")
@Produces(APPLICATION_JSON)

public class ClassAttributeWs {

    private final UserClassService classService;
    private final AttributeTypeConversionService conversionService;

    public ClassAttributeWs(UserClassService classService, AttributeTypeConversionService conversionService) {
        this.classService = checkNotNull(classService);
        this.conversionService = checkNotNull(conversionService);
    }

    @GET
    @Path("{attrId}/")
    public Object read(@PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId) {
        Attribute attribute = classService.getUserAttribute(classId, attrId);
        return buildAttrResponse(attribute);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@PathParam(CLASS_ID) String classId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        List list = classService.getUserAttributes(classId).stream().map(conversionService::serializeAttributeType).collect(toList());
        return response(paged(list, offset, limit));
    }

    @POST
    @Path(EMPTY)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(@PathParam(CLASS_ID) String classId, WsAttributeData data) {
        Classe classe = classService.getUserClass(classId);
        Attribute attribute = classService.createAttribute(data.toAttrDefinition(classe));//TODO check metadata persistence , check authorization
        return buildAttrResponse(attribute);
    }

    @PUT
    @Path("{attrId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId, WsAttributeData data) {
        Classe classe = classService.getUserClass(classId);
        Attribute attribute = classService.updateAttribute(data.toAttrDefinition(classe));//TODO check metadata persistence
        return buildAttrResponse(attribute);
    }

    @DELETE
    @Path("{attrId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId) {
        classService.deleteAttribute(classId, attrId);
        return success();
    }

    @POST
    @Path("order")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object reorder(@PathParam(CLASS_ID) String classId, List<String> attrOrder) {
        checkNotNull(attrOrder);
        Classe classe = classService.getUserClass(classId);
        classService.updateAttributes(prepareAttributesToUpdateForOrder(classe::getAttribute, attrOrder));
        classe = classService.getUserClass(classId);
        return response(attrOrder.stream().map(classe::getAttribute).map(conversionService::serializeAttributeType).collect(toList()), attrOrder.size());
    }

    public static List<Attribute> prepareAttributesToUpdateForOrder(Function<String, Attribute> attributeFun, List<String> attributes) {
        checkArgument(set(attributes).size() == attributes.size());
        List<Attribute> list = list();
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = checkNotNull(attributeFun.apply(attributes.get(i)));
            int newIndex = i + 1;
            if (attribute.getIndex() != newIndex) {
                list.add(AttributeImpl.copyOf(attribute).withIndex(newIndex).build());
            } else {
                list.add(attribute);
            }
        }
        return list;
    }

    private Object buildAttrResponse(Attribute attribute) {
        return response(conversionService.serializeAttributeType(attribute));
    }

    public static class WsAttributeData {

        private final AttributeTypeName typeName;
        private final String name, formatPattern, unitOfMeasure;
        private final UnitOfMeasureLocation unitOfMeasureLocation;
        private final String description, masterDetailDescription;
        private final boolean showInGrid, showInReducedGrid;
        private final Boolean preselectIfUnique, showSeparators, domainKey, showSeconds, showThousandsSeparator, isMasterDetail;
        private final String domain;
        private final boolean unique;
        private final boolean mandatory;
        private final boolean active;
        private final Integer index, visibleDecimals;
        private final String defaultValue;
        private final String group;
        private final Integer precision;
        private final Integer scale;
        private final String targetClass;
        private final Integer maxLength;
        private final String editorType;
        private final String lookupType;
        private final String filter;
        private final String mode;
        private final Map<String, String> metadata;
        private final Integer classOrder;
        private final String ipType, help, showIf, validationRules, autoValue;
        private final RelationDirection direction;

        public WsAttributeData(
                @JsonProperty("formatPattern") String formatPattern,
                @JsonProperty("unitOfMeasure") String unitOfMeasure,
                @JsonProperty("unitOfMeasureLocation") String unitOfMeasureLocation,
                @JsonProperty("visibleDecimals") Integer visibleDecimals,
                @JsonProperty("preselectIfUnique") Boolean preselectIfUnique,
                @JsonProperty("showThousandsSeparator") Boolean showThousandsSeparator,
                @JsonProperty("showSeconds") Boolean showSeconds,
                @JsonProperty("showSeparators") Boolean showSeparators,
                @JsonProperty("type") String type,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("showInGrid") Boolean showInGrid,
                @JsonProperty("showInReducedGrid") Boolean showInReducedGrid,
                @JsonProperty("domainKey") Boolean domainKey,
                @JsonProperty("domain") String domain,
                @JsonProperty("direction") String direction,
                @JsonProperty("unique") Boolean unique,
                @JsonProperty("mandatory") Boolean mandatory,
                @JsonProperty("active") Boolean active,
                @JsonProperty("index") Integer index,
                @JsonProperty("defaultValue") String defaultValue,
                @JsonProperty("group") String group,
                @JsonProperty("precision") Integer precision,
                @JsonProperty("scale") Integer scale,
                @JsonProperty("targetClass") String targetClass,
                @JsonProperty("maxLength") Integer maxLength,
                @JsonProperty("editorType") String editorType,
                @JsonProperty("lookupType") String lookupType,
                @JsonProperty("filter") String filter,
                @JsonProperty("help") String help,
                @JsonProperty("showIf") String showIf,
                @JsonProperty("validationRules") String validationRules,
                @JsonProperty("autoValue") String autoValue,
                @JsonProperty("mode") String mode,
                @JsonProperty("metadata") Map<String, String> metadata,
                @JsonProperty("classOrder") Integer classOrder,
                @JsonProperty("isMasterDetail") Boolean isMasterDetail,
                @JsonProperty("masterDetailDescription") String masterDetailDescription,
                @JsonProperty("ipType") String ipType) {
            this.typeName = checkNotNull(TYPE_NAME_MAP.inverse().get(checkNotBlank(type, "attr type cannote be null")), "unknown attr type = %s", type);
            this.name = checkNotBlank(name, "attr name cannot be null");
            this.description = checkNotNull(description, "attr description cannot be null");
            this.showInGrid = checkNotNull(showInGrid, "attr showInGrid cannot be null");
            this.showInReducedGrid = checkNotNull(showInReducedGrid, "attr showInReducedGrid cannot be null");
            this.unique = checkNotNull(unique, "attr unique cannot be null");
            this.mandatory = checkNotNull(mandatory, "attr mandatory cannot be null");
            this.active = checkNotNull(active, "attr active cannot be null");
            this.index = index;
            this.defaultValue = defaultValue;
            this.domainKey = domainKey;
            this.group = trimToNull(group);
            this.precision = precision;
            this.scale = scale;
            this.maxLength = maxLength;
            this.editorType = editorType;
            this.lookupType = lookupType;
            this.filter = filter;
            this.showSeconds = showSeconds;
            this.showThousandsSeparator = showThousandsSeparator;
            this.mode = checkNotBlank(mode, "attr mode cannot be null");
            this.metadata = metadata == null ? emptyMap() : map(metadata).immutable();
            this.classOrder = classOrder;
            this.ipType = ipType;
            this.help = help;
            this.showIf = showIf;
            this.validationRules = validationRules;
            this.autoValue = autoValue;
            this.isMasterDetail = isMasterDetail;
            this.masterDetailDescription = masterDetailDescription;

            this.formatPattern = formatPattern;
            this.preselectIfUnique = preselectIfUnique;
            this.showSeparators = showSeparators;
            this.unitOfMeasure = unitOfMeasure;
            this.unitOfMeasureLocation = parseEnumOrNull(unitOfMeasureLocation, UnitOfMeasureLocation.class);
            this.visibleDecimals = visibleDecimals;

            switch (typeName) {
                case REFERENCE:
                case REFERENCEARRAY:
                    this.direction = parseEnum(direction, RelationDirection.class);
                    this.domain = checkNotBlank(domain, "domain cannot be null");
                    this.targetClass = null;
                    break;
                case FOREIGNKEY:
                    this.direction = null;
                    this.domain = null;
                    this.targetClass = checkNotBlank(targetClass, "target class cannot be null");
                    break;
                default:
                    this.direction = null;
                    this.domain = null;
                    this.targetClass = null;
            }
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getDomain() {
            return domain;
        }

        public boolean isUnique() {
            return unique;
        }

        public boolean isMandatory() {
            return mandatory;
        }

        public String getMode() {
            return mode;
        }

        public boolean isActive() {
            return active;
        }

        public Integer getIndex() {
            return index;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getGroup() {
            return group;
        }

        public Integer getPrecision() {
            return precision;
        }

        public Integer getScale() {
            return scale;
        }

        public Integer getMaxLength() {
            return maxLength;
        }

        public String getEditorType() {
            return editorType;
        }

        public String getLookupType() {
            return lookupType;
        }

        public String getFilter() {
            return filter;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public Integer getClassOrder() {
            return classOrder;
        }

        public String getIpType() {
            return ipType;
        }

        public Attribute toAttrDefinition(EntryType owner) {
            return AttributeImpl.builder()
                    .withName(getName())
                    .withType(getAttrType())
                    .withMeta(AttributeMetadataImpl.builder()
                            .withMetadata(getMetadata())
                            .withActive(isActive())
                            .withClassOrder(getClassOrder())
                            .withDefaultValue(getDefaultValue())
                            .withDescription(getDescription())
                            .withEditorType(getEditorType())
                            .withFilter(getFilter())//TODO check this
                            .withGroup(group)
                            .withIndex(getIndex() == null ? null : (getIndex()))
                            .withMode(parseAttributePermissionMode(getMode()))
                            .withRequired(isMandatory())
                            .withShowInGrid(showInGrid)
                            .withShowInReducedGrid(showInReducedGrid)
                            .withTargetClass(targetClass)
                            .withUnique(isUnique())
                            .withFormatPattern(formatPattern)
                            .withPreselectIfUnique(preselectIfUnique)
                            .withUnitOfMeasure(unitOfMeasure)
                            .withUnitOfMeasureLocation(unitOfMeasureLocation)
                            .withVisibleDecimal(visibleDecimals)
                            .withShowSeparators(showSeparators)
                            .withValidationRulesExpr(validationRules)
                            .withAutoValueExpr(autoValue)
                            .withShowIfExpr(showIf)
                            .withHelpMessage(help)
                            .withDomainKey(domainKey)
                            .withShowThousandsSeparators(showThousandsSeparator)
                            .withShowSeconds(showSeconds)
                            .withMasterDetail(isMasterDetail)
                            .withMasterDetailDescription(masterDetailDescription)
                            .build())
                    .withOwner(owner)
                    .build();
        }

        private CardAttributeType getAttrType() {
            switch (typeName) {
                case DECIMAL:
                    return new DecimalAttributeType(getPrecision(), getScale());
                case FOREIGNKEY:
                    return new ForeignKeyAttributeType(targetClass);
                case INET:
                    return new IpAddressAttributeType(isBlank(getIpType()) ? IpType.IPV4 : IpType.valueOf(getIpType().toUpperCase()));
                case LOOKUP:
                    return new LookupAttributeType(getLookupType());
                case REFERENCE:
                    return new ReferenceAttributeType(domain, direction);
                case STRING:
                    return new StringAttributeType(getMaxLength());
                default:
                    return AttributeUtils.newAttributeTypeByName(typeName);
            }
        }
    }
}
