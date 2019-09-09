package org.cmdbuild.widget;

import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Maps.filterKeys;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Optional;
import static java.util.function.Function.identity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import static org.cmdbuild.cql.utils.CqlUtils.getFrom;
import org.cmdbuild.easytemplate.EasytemplateResolverNames;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.utils.EcqlUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.widget.model.WidgetImpl;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_ATTR_KEYS_FOR_CQL_PROCESSING;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_BUTTON_LABEL_KEY;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_CLASS_NAME;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_FILTER_KEY;
import static org.cmdbuild.widget.utils.WidgetValueUtils.buildWidgetStringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public final class WidgetFactoryServiceImpl implements WidgetFactoryService {

	private static final String WIDGET_ECQL_FROM = "from";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final EasytemplateRepository templateRespository;

	public WidgetFactoryServiceImpl(EasytemplateRepository templateRespository) {
		this.templateRespository = checkNotNull(templateRespository);
	}

	@Override
	public Widget createWidget(WidgetData widgetData, Map<String, Object> context) {
		return new WidgetDataProcessor(widgetData, context).processData();
	}

	private class WidgetDataProcessor {

		private static final String CLIENT_PREFIX = EasytemplateResolverNames.CLIENT + ":";
		private static final String DB_TEMPLATE_PREFIX = EasytemplateResolverNames.DB_TEMPLATE + ":";

		private final WidgetData widgetData;
		private final Map<String, Object> contextData;
		private final Map<String, Object> sourceData;
		private final Map<String, Object> processedData = map();

		public WidgetDataProcessor(WidgetData widgetData, Map<String, Object> context) {
			this.widgetData = checkNotNull(widgetData);
			this.sourceData = map(widgetData.getData())
					.with(WIDGET_BUTTON_LABEL_KEY, widgetData.getLabel());
			this.contextData = checkNotNull(context);
		}

		public Widget processData() {
			sourceData.forEach(this::processKeyValue);
			map(filterKeys(processedData, this::shouldProcessCqlValue)).forEach(this::processWidgetCqlValue);

			Optional.ofNullable(processedData.get(ecqlProcessedKey(WIDGET_FILTER_KEY))).ifPresent((m) -> {
				Optional.ofNullable(emptyToNull((String) ((Map) m).get(WIDGET_ECQL_FROM))).ifPresent((classId) -> processedData.put(WIDGET_CLASS_NAME, buildWidgetStringValue(classId)));
			});

			Widget widget = WidgetImpl.builder()
					.withContext(contextData)
					.withData(processedData)
					.withId(widgetData.getId())
					.withActive(widgetData.isActive())
					.withLabel((String) processedData.get(WIDGET_BUTTON_LABEL_KEY))
					.withType(widgetData.getType())
					.build();

			return widget;
		}

		private void processKeyValue(String key, Object rawValue) {
			logger.trace("process widget keyValue {} = {}", key, rawValue);
			String value = checkNotNull(toStringOrNull(rawValue));
			Object processedValue;
			if (isBetweenQuotes(value)) {
				processedValue = removeQuotes(value);
			} else if (WIDGET_FILTER_KEY.equals(key)) {
				if (value.startsWith(DB_TEMPLATE_PREFIX)) {
					String templateName = value.substring(DB_TEMPLATE_PREFIX.length());
					processedValue = templateRespository.getTemplate(templateName);
				} else {
					processedValue = value;
				}
			} else if (isNumber(value)) {
				processedValue = Integer.valueOf(value);
			} else if (value.startsWith(CLIENT_PREFIX)) {
				processedValue = String.format("{%s}", value);
			} else if (value.startsWith(DB_TEMPLATE_PREFIX)) {
				String templateName = value.substring(DB_TEMPLATE_PREFIX.length());
				processedValue = templateRespository.getTemplate(templateName);
			} else {
				processedValue = contextData.get(value);
			}

			logger.trace("set processed widget keyValue {} = {}", key, processedValue);
			processedData.put(key, processedValue);
		}

		private void processWidgetCqlValue(String key, Object value) {
			if (isNotBlank(value)) {
				value = cqlToEcqlData((String) value);
				key = ecqlProcessedKey(key);
				logger.trace("set processed widget cql value {} = {}", key, value);
				processedData.put(key, value);
			}
		}

		private Object cqlToEcqlData(String cql) {
			cql = EcqlUtils.resolveEcqlXa(cql, processedData);
			EcqlBindingInfo ecqlBindingInfo = EcqlUtils.getEcqlBindingInfoForExpr(cql, emptyMap());//TODO check context
//			Map<String, Object> xaContext = ecqlBindingInfo.getXaBindings().stream().collect(toMap(identity(), processedData::get));
			String ecqlId = EcqlUtils.buildEmbeddedEcqlId(cql);//TODO avoid embedded
			String classId = getFrom(cql);
			return map("id", ecqlId,
					"bindings", map("server", list(ecqlBindingInfo.getServerBindings()), "client", list(ecqlBindingInfo.getClientBindings())),
					WIDGET_ECQL_FROM, classId);
		}

		private String ecqlProcessedKey(String key) {
			return format("_%s_ecql", key);
		}

		private boolean isBetweenQuotes(String value) {
			return value.matches("^(['].*[']|[\"].*[\"])$");
		}

		private String removeQuotes(String value) {
			Matcher matcher = Pattern.compile("^['\"](.*)['\"]$").matcher(value);
			checkArgument(matcher.find());
			return matcher.group(1);
		}

		private boolean shouldProcessCqlValue(String key) {
			return WIDGET_ATTR_KEYS_FOR_CQL_PROCESSING.contains(key);
		}

	}

}
