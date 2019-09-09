/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget.model;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceArrayAttributeType;
import org.cmdbuild.exception.WidgetException;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_CLASS_NAME;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_TYPE_LINK_CARDS;
import static org.cmdbuild.widget.utils.WidgetValueUtils.parseWidgetStringValueOrRawValue;

public interface Widget extends WidgetData {

    Map<String, Object> getContext();

    default boolean has(String key) {
        return isNotBlank(getStringOrNull(key));
    }

    default String getNotBlank(String key) {
        return checkNotBlank(getStringOrNull(key));
    }

    @Nullable
    default String getStringOrNull(String key) {
        return toStringOrNull(getData().get(key));
    }

    @Nullable
    default String getOutputKeyOrNull() {
        return getOutputParameterOrNull();
    }

    default String getOutputKey() {
        return checkNotBlank(getOutputKeyOrNull());
    }

    default boolean hasOutputKey() {
        return isNotBlank(getOutputParameterOrNull());
    }

    default boolean hasOutputType() {
        return getOutputTypeOrNull() != null;
    }

    default CardAttributeType getOutputType() {
        return checkNotNull(getOutputTypeOrNull());
    }

    default @Nullable
    CardAttributeType getOutputTypeOrNull() {
        try {
            switch (getType()) {
                case WIDGET_TYPE_LINK_CARDS:
                    String className = checkNotBlank(parseWidgetStringValueOrRawValue(toStringOrNull(getData().get(WIDGET_CLASS_NAME))), "missing ClassName widget attr"); //TODO fix bugs, use parseWidgetStringValue()
                    return new ReferenceArrayAttributeType(className);
                default:
                    return null;
            }
        } catch (Exception ex) {
            throw new WidgetException(ex, "error processing output type for widget = %s", this);
        }
    }

}
