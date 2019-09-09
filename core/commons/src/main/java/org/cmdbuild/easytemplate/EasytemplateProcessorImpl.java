package org.cmdbuild.easytemplate;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableMap;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.regex.Matcher.quoteReplacement;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringEscapeUtils.escapeJson;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasytemplateProcessorImpl implements EasytemplateProcessor {

    private final static String OPEN_GRAPH_MARK = randomId(), CLOSE_GRAPH_MAKR = randomId();

    private final static Map<String, Function<String, Object>> DEFAULT_RESOLVERS = ImmutableMap.of("symbol", (Function<String, Object>) (x) -> {
        switch (x) {
            case "open":
                return OPEN_GRAPH_MARK;
            case "close":
                return CLOSE_GRAPH_MAKR;
            default:
                throw runtime("unsupported value for `symbol` resolver =< %s >", x);
        }
    });

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Function<String, Object>> templateResolvers;
    private final Pattern pattern;
    private final boolean hasResolverForDefaultExpr;
    private final int resGroup, exprGroup;

    private EasytemplateProcessorImpl(EasytemplateProcessorImplBuilder builder) {
        this.templateResolvers = builder.templateResolvers;
        this.hasResolverForDefaultExpr = templateResolvers.containsKey("");
        String patternStr;
        if (hasResolverForDefaultExpr) {
            patternStr = format("[{]((%s):)?([^{}]+)[}]", Joiner.on("|").join(templateResolvers.keySet()));
            resGroup = 2;
            exprGroup = 3;
        } else {
            patternStr = format("[{](%s):([^{}]+)[}]", Joiner.on("|").join(templateResolvers.keySet()));
            resGroup = 1;
            exprGroup = 2;
        }
        logger.trace("start template processor, using pattern = {}", patternStr);
        pattern = Pattern.compile(patternStr);
    }

    @Override
    @Nullable
    public String processExpression(@Nullable String expression, ExprProcessingMode mode) {
        return isBlank(expression) ? expression : doResolve(expression, checkNotNull(mode));
    }

    @Override
    public Map<String, Function<String, Object>> getResolvers() {
        return templateResolvers;
    }

    private String doResolve(String expression, ExprProcessingMode mode) {
        logger.trace("trying to resolve expr = {}", expression);
        if (Pattern.compile(Pattern.quote("{easytemplate:disable}")).matcher(expression).find()) {
            logger.trace("easytemplate disabled for this expr, skipping");
            return expression.replaceFirst(Pattern.quote("{easytemplate:disable}"), "");
        } else {
            String resolved = expression;
            Matcher matcher = pattern.matcher(resolved);
            while (matcher.find()) {
                logger.trace("found match = {}", matcher.group(0));
                String resolver = nullToEmpty(matcher.group(resGroup));
                String expr = matcher.group(exprGroup);
                Object value = resolveExpression(resolver, expr);
                String replacement = valueToString(value, mode);
                logger.trace("replacing match with string = {}", replacement);
                resolved = matcher.replaceFirst(quoteReplacement(replacement));
                matcher = pattern.matcher(resolved);
            }
            return resolved
                    .replace(OPEN_GRAPH_MARK, "{")
                    .replace(CLOSE_GRAPH_MAKR, "}");
        }
    }

    private String valueToString(@Nullable Object value, ExprProcessingMode mode) {
        switch (mode) {
            case EPM_JAVASCRIPT:
                if (value == null) {
                    return "null";
                } else {
                    String strValue = toStringOrEmpty(value);
                    if (isNumber(strValue)) {
                        return strValue;
                    } else {
                        return format("\"%s\"", escapeJson(strValue));
                    }
                }
            default:
                return toStringOrEmpty(value);
        }
    }

    @Nullable
    private Object resolveExpression(String resolver, String expression) {
        logger.trace("evaluating expr = {} with resolver = {}", expression, resolver);
        Function<String, Object> engine = templateResolvers.get(resolver);
        if (engine != null) {
            Object value = engine.apply(expression);
            if (value == null) {
                logger.warn("expr =< {} > resolved to null with resolver =< {} >", expression, resolver);
            } else {
                logger.trace("expr =< {} > resolved to value =< {} > ({}) with resolver =< {} >", expression, value, getClassOfNullable(value).getName(), resolver);
            }
            return value;
        } else {
            logger.warn(marker(), "expr resolver =< {} > not found (return null)", resolver);
            return null;
        }
    }

    public static EasytemplateProcessorImplBuilder builder() {
        return new EasytemplateProcessorImplBuilder();
    }

    public static EasytemplateProcessorImplBuilder copyOf(EasytemplateProcessor processor) {
        return builder().withResolvers(processor.getResolvers());
    }

    public static class EasytemplateProcessorImplBuilder implements Builder<EasytemplateProcessorImpl, EasytemplateProcessorImplBuilder> {

        protected final Map<String, Function<String, Object>> templateResolvers = map(DEFAULT_RESOLVERS);

        private EasytemplateProcessorImplBuilder() {
        }

        public EasytemplateProcessorImplBuilder withResolver(Function<String, Object> engine, String... prefixes) {
            return EasytemplateProcessorImplBuilder.this.withResolver(engine, asList(prefixes));
        }

        public EasytemplateProcessorImplBuilder withResolver(Function<String, Object> engine, Iterable<String> prefixes) {
            for (String p : prefixes) {
                templateResolvers.put(p, engine);
            }
            return this;
        }

        public EasytemplateProcessorImplBuilder withResolver(String prefix, Function<String, Object> engine) {
            templateResolvers.put(prefix, engine);
            return this;
        }

        public EasytemplateProcessorImplBuilder withResolvers(Map<String, Function<String, Object>> resolvers) {
            templateResolvers.putAll(resolvers);
            return this;
        }

        @Override
        public EasytemplateProcessorImpl build() {
            return new EasytemplateProcessorImpl(this);
        }

    }

}
