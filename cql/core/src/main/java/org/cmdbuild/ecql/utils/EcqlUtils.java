/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import static com.google.common.collect.ImmutableList.copyOf;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.cql.EcqlException;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.EcqlExpression;
import org.cmdbuild.ecql.EcqlId;
import org.cmdbuild.ecql.EcqlSource;
import org.cmdbuild.ecql.inner.EcqlBindingInfoImpl;
import org.cmdbuild.ecql.inner.EcqlExpressionImpl;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class EcqlUtils {

    private final static BiMap<EcqlSource, String> MINIFIED_ECQL_SOURCE_KEY_MAP = HashBiMap.create(map(
            EcqlSource.CLASS_ATTRIBUTE, "C",
            EcqlSource.EMBEDDED, "E"
    ));

    public static String buildEcqlId(EcqlSource source, Object... sourceId) {
        List<String> keys = asList(checkNotNull(sourceId)).stream().map(CmStringUtils::toStringOrNull).collect(toList());
        return buildEcqlId(source, keys.toArray(new String[]{}));
    }

    public static String buildClassAttrEcqlId(Attribute attribute) {
        return buildEcqlId(EcqlSource.CLASS_ATTRIBUTE, attribute.getOwner().getName(), attribute.getName());
    }

    public static String buildEmbeddedEcqlId(String cqlExpr) {
        return buildEcqlId(EcqlSource.EMBEDDED, checkNotBlank(cqlExpr));
    }

    public static String buildEcqlId(EcqlSource source, String... sourceId) {
        checkArgument(asList(checkNotNull(sourceId)).stream().allMatch(notNull()));
        String ecqlRawId = format("%s%s",
                checkNotNull(MINIFIED_ECQL_SOURCE_KEY_MAP.get(source)),
                toJson(sourceId));
        String encodedId = encodeString(ecqlRawId);
        return encodedId;
    }

    public static EcqlId parseEcqlId(String encodedId) {
        try {
            String decodedId = decodeString(encodedId);
            checkArgument(decodedId.length() >= 2);
            String key = decodedId.substring(0, 1),
                    value = checkNotBlank(decodedId.substring(1));
            EcqlSource source = checkNotNull(MINIFIED_ECQL_SOURCE_KEY_MAP.inverse().get(key), "ecql source not found for key = '%s'", key);
            List<String> idList = fromJson(value, LIST_OF_STRINGS);
            return new EcqlIdImpl(source, idList);
        } catch (Exception ex) {
            throw new EcqlException(ex, "error parsing ecqlId = '%s'", encodedId);
        }
    }

    public static EcqlBindingInfo getEcqlBindingInfoForExpr(EcqlExpression expression) {
        return getEcqlBindingInfoForExpr(expression.getEcql(), expression.getContext());
    }

    public static String resolveEcqlXa(String ecql, Map<String, Object> xaContext) {
        return EasytemplateProcessorImpl.builder()
                .withResolver("xa", xaContext::get)
                .build().processExpression(ecql);
    }

    public static EcqlBindingInfo getEcqlBindingInfoForExpr(String ecql, Map<String, Object> context) {
        checkNotBlank(ecql, "ecql expression is null or blank");

        List<String> clientBindings = list(),
                serverBindings = list(),
                xaBindings = list();

        AtomicReference<EasytemplateProcessor> processor = new AtomicReference<>();

        processor.set(EasytemplateProcessorImpl.builder()
                .withResolver("client", (key) -> clientBindings.add(key))
                .withResolver("server", (key) -> serverBindings.add(key))
                .withResolver("xa", (key) -> xaBindings.add(key))
                .withResolver("js", (key) -> processor.get().processExpression(getJsExprFromContext(key, context)))
                .build());

        processor.get().processExpression(ecql);

        return EcqlBindingInfoImpl.builder()
                .withClientBindings(clientBindings)
                .withServerBindings(serverBindings)
                .withXaBindings(xaBindings)
                .build();
    }

    public static String getJsExprFromContext(String key, Map<String, Object> context) {
        return checkNotBlank((String) context.get(key), "value not found in context for js key = '%s'", key);
    }

    public static EcqlExpression getEcqlExpressionFromClassAttributeFilter(Attribute attribute) {
        String filter = attribute.getFilter();
        checkNotBlank(filter);
        //TODO check syntax??

        Map<String, Object> context = map(attribute.getMetadata().getAll());
        attribute.getMetadata().getAll().entrySet().stream()
                .filter((e) -> e.getKey().startsWith("system.template."))
                .forEach((e) -> context.put(e.getKey().replaceFirst(quote("system.template."), ""), e.getValue()));

        return new EcqlExpressionImpl(filter, context);
    }

    private final static class EcqlIdImpl implements EcqlId<List<String>> {

        private final EcqlSource source;
        private final List<String> id;

        public EcqlIdImpl(EcqlSource source, List<String> id) {
            this.source = checkNotNull(source);
            this.id = copyOf(checkNotNull(id));
        }

        @Override
        public EcqlSource getSource() {
            return source;
        }

        @Override
        public List<String> getId() {
            return id;
        }

        @Override
        public String toString() {
            return "EcqlIdImpl{" + "source=" + source + ", id=" + id + '}';
        }

    }

}
