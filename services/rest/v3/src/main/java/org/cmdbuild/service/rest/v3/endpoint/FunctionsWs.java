package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static java.util.Collections.emptyMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Ordering;
import static com.google.common.collect.Streams.stream;
import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;

import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.AttributeFilterCondition.ConditionOperator;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FUNCTION_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.NAME;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PARAMETERS;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.function.StoredFunctionParameter;
import org.cmdbuild.dao.function.StoredFunctionOutputParameter;
import org.cmdbuild.dao.function.StoredFunctionOutputParameterImpl;
import org.cmdbuild.data.filter.AttributeFilterProcessor;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelper;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

@Path("functions/")
@Produces(APPLICATION_JSON)
public class FunctionsWs {

    private final DaoService dao;
    private final CardWsSerializationHelper helper;

    public FunctionsWs(DaoService dao, CardWsSerializationHelper helper) {
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(FILTER) String filterStr, @QueryParam(DETAILED) boolean detailed) {
        CmdbFilter filter = CmdbFilterUtils.parseFilter(filterStr);

        List<StoredFunction> list = dao.getAllFunctions().stream().sorted(Ordering.natural().onResultOf(StoredFunction::getId)).collect(toList());
        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (filter.hasAttributeFilter()) {

            list = AttributeFilterProcessor.<StoredFunction>builder()
                    .withKeyToValueFunction((key, fun) -> {
                        switch (key) {
                            case NAME:
                                return fun.getName();
                            case "tags":
                                return fun.getTags();
                            case "source":
                                return fun.hasSourceClassName() ? dao.getClasse(fun.getSourceClassName()) : null;
                            default:
                                throw new IllegalArgumentException("unsupported filter key = " + key);
                        }
                    })
                    .withConditionEvaluatorFunction((AttributeFilterProcessor.ConditionEvaluatorFunction) (AttributeFilterCondition condition, Object value) -> {
                        switch (condition.getOperator()) {
                            case EQUAL:
                                return equal(toStringOrNull(value), condition.getSingleValue());
                            case IN:
                                return condition.getValues().contains(toStringOrNull(value));
                            case CONTAIN:
                                if (value instanceof Collection) {
                                    return ((Collection) value).contains(condition.getSingleValue());//TODO improve this
                                } else {
                                    return value != null && ((Classe) value).equalToOrAncestorOf(dao.getClasse(condition.getSingleValue())); //TODO filter also
                                }
                            default:
                                throw new IllegalArgumentException("unsupported operator = " + condition.getOperator());
                        }
                    })
                    .withFilter(filter.getAttributeFilter())
                    .filter(list);
        }

        PagedElements<StoredFunction> paged = PagedElements.paged(list, offset, limit);

        return response(paged.stream().map(detailed ? this::toDetailedResponse : this::toResponse).collect(toList()), paged.totalSize());
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/")
    public Object read(@PathParam(FUNCTION_ID) String functionId) {
        StoredFunction function = getFunction(functionId);
        return response(toDetailedResponse(function));
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/parameters/")
    public Object readInputParameters(@PathParam(FUNCTION_ID) String functionId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        StoredFunction function = getFunction(functionId);
        Collection<StoredFunctionParameter> parameters = function.getInputParameters();
        return serializeResponse(parameters, limit, offset);
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/attributes/")
    public Object readOutputParameters(@PathParam(FUNCTION_ID) String functionId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        StoredFunction function = getFunction(functionId);
        Collection<StoredFunctionOutputParameter> oParameters = function.getOutputParameters();
        return serializeResponse(oParameters, limit, offset);
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/outputs/")
    public Object call(@PathParam(FUNCTION_ID) String functionId, @QueryParam(PARAMETERS) String inputs, @QueryParam("model") String model) {
        StoredFunction function = getFunction(functionId);

        Map<String, Object> params = isBlank(inputs) ? emptyMap() : fromJson(inputs, MAP_OF_OBJECTS);
        List<Object> inputParams = function.getInputParameters().stream().map(StoredFunctionParameter::getName).map(params::get).collect(toList());

        List<StoredFunctionOutputParameter> outputParams = isBlank(model)
                ? function.getOutputParameters()
                : parseOutputParameters(function, model);

        List rows = dao.selectFunction(function, inputParams, outputParams).run().stream().map((r) -> r.asMap())
                .map((source) -> map().accept((map) -> outputParams.forEach((p) -> helper.addCardValuesAndDescriptionsAndExtras(p.getName(), p.getType(), source::get, map::put)))).collect(toList());

        return response(rows);
    }

    private Object serializeResponse(Collection<? extends StoredFunctionParameter> parameters, @Nullable Integer limit, @Nullable Integer offset) {
        return response(paged(parameters.stream().map(this::serializeParam).collect(toList()), offset, limit));
    }

    private Object serializeParam(StoredFunctionParameter param) {
        return map("_id", param.getName(),
                "name", param.getName(),
                "type", AttributeTypeConversionService.serializeAttributeType(param.getType().getName()));
    }

    private StoredFunction getFunction(String idOrName) {
        checkNotBlank(idOrName);
        if (isNumber(idOrName)) {
            return dao.getFunctionById(Long.valueOf(idOrName));
        } else {
            return dao.getFunctionByName(idOrName);
        }
    }

    private FluentMap<String, Object> toResponse(StoredFunction input) {
        return map("_id", input.getId(),
                "name", input.getName(),
                "description", input.getName());
    }

    private List serializeParams(Iterable<? extends StoredFunctionParameter> parameters) {
        return stream(parameters).map(new com.google.common.base.Function<StoredFunctionParameter, Object>() {

            private int index = 0;

            @Override
            public Object apply(StoredFunctionParameter attribute) {
                return map(
                        "_id", attribute.getName(),
                        "type", AttributeTypeConversionService.serializeAttributeType(attribute.getType().getName()),
                        "name", attribute.getName(),
                        "description", attribute.getName(),
                        "active", true,
                        "index", index++);
            }

        }).collect(toList());
    }

    private Object toDetailedResponse(StoredFunction input) {
        return toResponse(input).with(
                "tags", input.getTags(),
                "source", input.getSourceClassName(),
                "metadata", input.getMetadataExt(),
                "parameters", serializeParams(input.getInputParameters()));
    }

    private List<StoredFunctionOutputParameter> parseOutputParameters(StoredFunction function, String modelStr) {
        WsFunctionOutputModel model = CmJsonUtils.fromJson(modelStr, WsFunctionOutputModel.class);
        Map<String, WsFunctionOutputParameter> customParams = uniqueIndex(model.output, (p) -> p.name);
        return function.getOutputParameters().stream().map((param) -> {
            WsFunctionOutputParameter customParam = customParams.get(param.getName());
            if (customParam != null) {
                param = new StoredFunctionOutputParameterImpl(param.getName(), customParam.type, param.getBasedsp());
            }
            return param;

        }).collect(toList());
    }

    private static class WsFunctionOutputModel {

        private final List<WsFunctionOutputParameter> output;

        public WsFunctionOutputModel(@JsonProperty("output") List<WsFunctionOutputParameter> output) {
            this.output = checkNotNull(output);
        }

        @Override
        public String toString() {
            return "WsFunctionOutputModel{" + "output=" + output + '}';
        }

    }

    private static class WsFunctionOutputParameter {

        private final String name;
        private final CardAttributeType type;

        public WsFunctionOutputParameter(@JsonProperty("name") String name, @JsonProperty("type") String typeStr, @JsonProperty("fkTarget") String fkTarget, @JsonProperty("lookupType") String lookupType) {
            this.name = checkNotBlank(name);
            AttributeTypeName typeName = AttributeTypeName.valueOf(typeStr.toUpperCase());
            switch (typeName) {
                case FOREIGNKEY:
                    type = new ForeignKeyAttributeType(fkTarget);
                    break;
                case LOOKUP:
                    type = new LookupAttributeType(lookupType);
                    break;
                default:
                    throw unsupported("unsupported param type = %s", typeStr);
            }
        }

        @Override
        public String toString() {
            return "WsFunctionOutputParameter{" + "name=" + name + ", type=" + type + '}';
        }

    }
}
