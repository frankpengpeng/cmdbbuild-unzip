/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.io.IOException;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.data.filter.AttributeFilter;
import org.cmdbuild.data.filter.AttributeFilter.AttributeFilterMode;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.AttributeFilterCondition.ConditionOperator;
import org.cmdbuild.data.filter.beans.AttributeFilterImpl;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl.CmdbFilterBuilder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.json.JSONObject;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.EcqlFilter;
import org.cmdbuild.data.filter.FunctionFilter;
import org.cmdbuild.data.filter.FunctionFilterEntry;
import org.cmdbuild.data.filter.RelationFilter;
import org.cmdbuild.data.filter.RelationFilterCardInfo;
import org.cmdbuild.data.filter.RelationFilterRule;
import org.cmdbuild.data.filter.beans.CqlFilterImpl;
import org.cmdbuild.data.filter.beans.EcqlFilterImpl;
import org.cmdbuild.data.filter.beans.FulltextFilterImpl;
import org.cmdbuild.data.filter.beans.FunctionFilterEntryImpl;
import org.cmdbuild.data.filter.beans.FunctionFilterImpl;
import org.cmdbuild.data.filter.beans.RelationFilterCardInfoImpl;
import org.cmdbuild.data.filter.beans.RelationFilterImpl;
import org.cmdbuild.data.filter.beans.RelationFilterRuleImpl;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.AND_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ATTRIBUTE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.CLASSNAME_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.CQL_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ECQL_CONTEXT_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ECQL_ID_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.ECQL_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.FULL_TEXT_QUERY_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.FUNCTION_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.FUNCTION_NAME_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.NOT_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.OPERATOR_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.OR_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_CARDS_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_CARD_CLASSNAME_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_CARD_ID_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_DESTINATION_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_DOMAIN_DIRECTION;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_DOMAIN_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_SOURCE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.RELATION_TYPE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.SIMPLE_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.Filters.VALUE_KEY;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CmdbFilterUtils {

    protected final static ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        MAPPER.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        MAPPER.registerModule(new JsonOrgModule());
    }

    private final static CmdbFilter NOOP_FILTER = CmdbFilterImpl.builder().build();

    public static CmdbFilter noopFilter() {
        return NOOP_FILTER;
    }

    public static CmdbFilter merge(CmdbFilter one, CmdbFilter two) {
        try {
            CmdbFilterBuilder builder = CmdbFilterImpl.builder()
                    .withAttributeFilter(mergeAttributeFilter(one.hasAttributeFilter() ? one.getAttributeFilter() : null, two.hasAttributeFilter() ? two.getAttributeFilter() : null));
            checkArgument(!(one.hasCqlFilter() && two.hasCqlFilter()));
            if (one.hasCqlFilter()) {
                builder.withCqlFilter(one.getCqlFilter());
            }
            if (two.hasCqlFilter()) {
                builder.withCqlFilter(two.getCqlFilter());
            }
            checkArgument(!(one.hasFulltextFilter() && two.hasFulltextFilter()));
            if (one.hasFulltextFilter()) {
                builder.withFulltextFilter(one.getFulltextFilter());
            }
            if (two.hasFulltextFilter()) {
                builder.withFulltextFilter(two.getFulltextFilter());
            }
            checkArgument(!(one.hasEcqlFilter() && two.hasEcqlFilter()));
            if (one.hasEcqlFilter()) {
                builder.withEcqlFilter(one.getEcqlFilter());
            }
            if (two.hasEcqlFilter()) {
                builder.withEcqlFilter(two.getEcqlFilter());
            }
            checkArgument(!(one.hasFunctionFilter() && two.hasFunctionFilter()));
            if (one.hasFunctionFilter()) {
                builder.withFunctionFilter(one.getFunctionFilter());
            }
            if (two.hasFunctionFilter()) {
                builder.withFunctionFilter(two.getFunctionFilter());
            }
            checkArgument(!(one.hasRelationFilter() && two.hasRelationFilter()));
            if (one.hasRelationFilter()) {
                builder.withRelationFilter(one.getRelationFilter());
            }
            if (two.hasRelationFilter()) {
                builder.withRelationFilter(two.getRelationFilter());
            }
            return builder.build();
        } catch (Exception ex) {
            throw runtime(ex, "unable to merge card filters");
        }
    }

    public static CmdbFilter mapNamesInFilter(CmdbFilter source, Map<String, String> mapping) {
        if (!source.hasAttributeFilter()) {
            return source;
        } else {
            return CmdbFilterImpl.copyOf(source).withAttributeFilter(mapNamesInFilter(source.getAttributeFilter(), mapping)).build();
        }
    }

    private static AttributeFilter mapNamesInFilter(AttributeFilter source, Map<String, String> mapping) {
        if (source.isSimple()) {
            AttributeFilterCondition condition = source.getCondition();
            return AttributeFilterImpl.simple(AttributeFilterConditionImpl.copyOf(condition).withKey(mapping.getOrDefault(condition.getKey(), condition.getKey())).build());
        } else {
            return AttributeFilterImpl.build(source.getMode(), source.getElements().stream().map(e -> mapNamesInFilter(e, mapping)).collect(toImmutableList()));
        }
    }

    private static @Nullable
    AttributeFilter mergeAttributeFilter(@Nullable AttributeFilter one, @Nullable AttributeFilter two) {
        if (one == null && two == null) {
            return null;
        } else if (one != null ^ two != null) {
            return firstNonNull(one, two);
        } else {
            return AttributeFilterImpl.and(list(one, two));
        }
    }

    public static String serializeFilter(CmdbFilter filter) {
        try {
            Map map = toJsonMap(filter);
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException ex) {
            throw runtime(ex);
        }
    }

    @Deprecated()//"for legacy support only"
    public static JSONObject toJsonObject(@Nullable CmdbFilter filter) {
        try {
            if (filter == null) {
                return new JSONObject();
            } else {
                return MAPPER.readValue(serializeFilter(filter), JSONObject.class);
            }
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    private static Map toJsonMap(CmdbFilter filter) {
        Map map = map();
        if (filter.hasAttributeFilter()) {
            map.put(ATTRIBUTE_KEY, toJsonMap(filter.getAttributeFilter()));
        }
        if (filter.hasCqlFilter()) {
            map.put(CQL_KEY, filter.getCqlFilter().getCqlExpression());
        }
        if (filter.hasFulltextFilter()) {
            map.put(FULL_TEXT_QUERY_KEY, filter.getFulltextFilter().getQuery());
        }
        if (filter.hasRelationFilter()) {
            map.put(RELATION_KEY, toJsonMap(filter.getRelationFilter()));
        }
        if (filter.hasFunctionFilter()) {
            map.put(FUNCTION_KEY, filter.getFunctionFilter().getFunctions().stream().map((f) -> map(FUNCTION_NAME_KEY, f.getName())).collect(toList()));
        }
        if (filter.hasEcqlFilter()) {
            map.put(ECQL_KEY, map(ECQL_ID_KEY, filter.getEcqlFilter().getEcqlId(), ECQL_CONTEXT_KEY, filter.getEcqlFilter().getJsContext()));
        }
        return map;
    }

    private static Map toJsonMap(AttributeFilter attributeFilter) {
        Map attribute = map();
        if (attributeFilter.isSimple()) {
            AttributeFilterCondition condition = attributeFilter.getCondition();
            attribute.put(SIMPLE_KEY, map(
                    ATTRIBUTE_KEY, condition.getKey(),
                    OPERATOR_KEY, condition.getOperator().name().toLowerCase(),
                    VALUE_KEY, list(condition.getValues())));
        } else {
            attribute.put(attributeFilter.getMode().name().toLowerCase(), attributeFilter.getElements().stream().map(CmdbFilterUtils::toJsonMap).collect(toList()));
        }
        return attribute;
    }

    private static List toJsonMap(RelationFilter relationFilter) {
        return relationFilter.getRelationFilterRules().stream().map((r) -> toJsonMap(r)).collect(toList());
    }

    private static Map toJsonMap(RelationFilterRule rule) {
        Map map = map(
                RELATION_DOMAIN_DIRECTION, rule.getDirection().name(),
                RELATION_SOURCE_KEY, rule.getSource(),
                RELATION_DESTINATION_KEY, rule.getDestination(),
                RELATION_DOMAIN_KEY, rule.getDomain(),
                RELATION_TYPE_KEY, rule.getType().name().toLowerCase()
        );
        if (rule.isOneOf()) {
            map.put(RELATION_CARDS_KEY, rule.getCardInfos().stream().map((c) -> map(RELATION_CARD_ID_KEY, c.getId(), RELATION_CARD_CLASSNAME_KEY, c.getClassName())).collect(toList()));
        }
        return map;
    }

    @Deprecated()//"for legacy support only"
    public static CmdbFilter fromJson(JSONObject jsonFilter) {
        try {
            String json = MAPPER.writeValueAsString(jsonFilter);
            return parseFilter(json);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static CmdbFilter parseFilter(@Nullable String jsonFilter) {
        if (isBlank(jsonFilter)) {
            return NOOP_FILTER;
        } else {
            try {
                JsonNode jsonNode = MAPPER.readTree(jsonFilter);
                CmdbFilterBuilder builder = CmdbFilterImpl.builder();
                jsonNode.fieldNames().forEachRemaining(f -> {
                    switch (f.toLowerCase()) {
                        case ATTRIBUTE_KEY:
                            builder.withAttributeFilter(parseAttributeFilter(jsonNode.get(f)));
                            break;
//                        case CQL_KEY:
                        case "cql"://note: CQL_KEY.toLowercase()
                            builder.withCqlFilter(new CqlFilterImpl(jsonNode.get(f).asText()));
                            break;
                        case FULL_TEXT_QUERY_KEY:
                            if (isNotBlank(jsonNode.get(f).asText())) {
                                builder.withFulltextFilter(new FulltextFilterImpl(jsonNode.get(f).asText()));
                            }
                            break;
                        case RELATION_KEY:
                            builder.withRelationFilter(parseRelationFilter(jsonNode.get(f)));
                            break;
                        case FUNCTION_KEY:
                            builder.withFunctionFilter(parseFunctionFilter(jsonNode.get(f)));
                            break;
                        case ECQL_KEY:
                            builder.withEcqlFilter(parseEcqlFilter(jsonNode.get(f)));
                            break;
                        default:
                            throw runtime("invalid json filter key =< %s >", f);
                    }
                });
                return builder.build();
            } catch (Exception ex) {
                throw runtime(ex, "error deserializing json filter =< %s >", jsonFilter);
            }
        }
    }

    private static AttributeFilter parseAttributeFilter(JsonNode jsonNode) {
        if (jsonNode.has(SIMPLE_KEY)) {
            JsonNode simpleConditionNode = jsonNode.get(SIMPLE_KEY);
            String key = simpleConditionNode.get(ATTRIBUTE_KEY).asText();
            String className = simpleConditionNode.has(CLASSNAME_KEY) ? simpleConditionNode.get(CLASSNAME_KEY).asText() : null;
            ConditionOperator operator = parseEnum(simpleConditionNode.get(OPERATOR_KEY).asText(), ConditionOperator.class);
            List<String> values = list();
            JsonNode valuesNode = simpleConditionNode.get(VALUE_KEY);
            checkArgument(valuesNode != null && !valuesNode.isNull(), "missing attr '%s'", VALUE_KEY);
            if (valuesNode.isArray()) {
                for (int i = 0; i < valuesNode.size(); i++) {
                    values.add(valuesNode.get(i).asText());
                }
            } else {
                checkArgument(!valuesNode.isObject());
                values.add(valuesNode.asText());
            }
            return AttributeFilterConditionImpl.builder()
                    .withOperator(operator)
                    .withValues(values)
                    .withKey(key)
                    .withClassName(className)
                    .build().toAttributeFilter();
        } else if (jsonNode.has(NOT_KEY)) {
            JsonNode notElement = jsonNode.get(NOT_KEY);
            return AttributeFilterImpl.not(parseAttributeFilter(notElement));
        } else {
            JsonNode elementNodes;
            AttributeFilterMode mode;
            if (jsonNode.has(AND_KEY)) {
                elementNodes = jsonNode.get(AND_KEY);
                mode = AttributeFilterMode.AND;
            } else if (jsonNode.has(OR_KEY)) {
                elementNodes = jsonNode.get(OR_KEY);
                mode = AttributeFilterMode.OR;
            } else {
                throw new IllegalArgumentException(format("unable to parse illegal attribute filter = %s (invalid top-level key, must be one of 'and','or','simple','not')", abbreviate(toStringOrNull(jsonNode))));
            }
            List<AttributeFilter> elements = list();
            checkArgument(elementNodes.isArray());
            for (int i = 0; i < elementNodes.size(); i++) {
                elements.add(parseAttributeFilter(elementNodes.get(i)));
            }
            return AttributeFilterImpl.andOr(mode, elements);
        }
    }

    private static RelationFilter parseRelationFilter(JsonNode jsonNode) {
        checkArgument(jsonNode.isArray());
        List<RelationFilterRule> rules = list();
        for (int i = 0; i < jsonNode.size(); i++) {
            rules.add(parseRelationFilterRule(jsonNode.get(i)));
        }
        return new RelationFilterImpl(rules);
    }

    private static RelationFilterRule parseRelationFilterRule(JsonNode jsonNode) {
        try {
            List<RelationFilterCardInfo> cardInfos;
            if (jsonNode.has(RELATION_CARDS_KEY)) {
                cardInfos = list();
                JsonNode cards = jsonNode.get(RELATION_CARDS_KEY);
                for (int i = 0; i < cards.size(); i++) {
                    JsonNode card = cards.get(i);
                    String className = checkNotNull(card.get(RELATION_CARD_CLASSNAME_KEY), "must set 'className' field for 'cards' filter entry").asText();
                    long cardId = checkNotNull(card.get(RELATION_CARD_ID_KEY), "must set 'id' field for 'cards' filter entry").asLong();
                    cardInfos.add(new RelationFilterCardInfoImpl(className, cardId));
                }
            } else {
                cardInfos = null;
            }
            return RelationFilterRuleImpl.builder()
                    .withDestination(jsonNode.get(RELATION_DESTINATION_KEY).textValue())
                    .withDirection(jsonNode.has(RELATION_DOMAIN_DIRECTION) ? RelationFilterRule.RelationFilterDirection.valueOf(jsonNode.get(RELATION_DOMAIN_DIRECTION).textValue()) : null)
                    .withDomain(jsonNode.get(RELATION_DOMAIN_KEY).textValue())
                    .withSource(jsonNode.get(RELATION_SOURCE_KEY).textValue())
                    .withType(RelationFilterRule.RelationFilterRuleType.valueOf(jsonNode.get(RELATION_TYPE_KEY).asText().toUpperCase()))
                    .withCardInfos(cardInfos)
                    .build();
        } catch (Exception ex) {
            throw runtime(ex, "error parsing relation filter rule from node = %s", abbreviate(jsonNode));
        }
    }

    private static FunctionFilter parseFunctionFilter(JsonNode jsonNode) {
        checkArgument(jsonNode.isArray());
        List<FunctionFilterEntry> filters = list();
        for (int i = 0; i < jsonNode.size(); i++) {
            JsonNode entryNode = jsonNode.get(i);
            filters.add(new FunctionFilterEntryImpl(entryNode.get(FUNCTION_NAME_KEY).asText()));
        }
        return new FunctionFilterImpl(filters);
    }

    private static EcqlFilter parseEcqlFilter(JsonNode jsonNode) {
        return new EcqlFilterImpl(jsonNode.get(ECQL_ID_KEY).asText(), jsContextAsString(jsonNode));
    }

    private static String jsContextAsString(JsonNode jsonNode) {
        JsonNode context = jsonNode.get(ECQL_CONTEXT_KEY);
        if (context.isObject()) {
            try {
                return MAPPER.writeValueAsString(context);
            } catch (JsonProcessingException ex) {
                throw runtime(ex);
            }
        } else {
            return context.asText();
        }
    }

    public static void checkFilterSyntax(String filter) {
        try {
            parseFilter(filter);
        } catch (Exception ex) {
            throw runtime(ex, "invalid filter syntax for filter = '%s'", abbreviate(filter));
        }
    }

}
