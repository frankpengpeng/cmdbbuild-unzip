package org.cmdbuild.widget.model;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_ALWAYS_ENABLED_KEY;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_OUTPUT_KEY;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_REQUIRED_KEY;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_BUTTON_LABEL_KEY;

public interface WidgetData extends WidgetInfo {

	Map<String, Object> getData();

	default Map<String, Object> getExtendedData() {
		return Maps.filterKeys(getData(), not(in(set(WIDGET_REQUIRED_KEY, WIDGET_OUTPUT_KEY, WIDGET_BUTTON_LABEL_KEY, WIDGET_ALWAYS_ENABLED_KEY))));
	}

	default boolean isRequired() {
		return toBooleanOrDefault(getData().get(WIDGET_REQUIRED_KEY), false);
	}

	default boolean isAlwaysEnabled() {
		return toBooleanOrDefault(getData().get(WIDGET_ALWAYS_ENABLED_KEY), false);
	}

	@Nullable
	default String getOutputParameterOrNull() {
		return trimToNull(toStringOrNull(getData().get(WIDGET_OUTPUT_KEY)));
	}

	default String getOutputParameter() {
		return checkNotBlank(getOutputParameterOrNull());
	}

}
