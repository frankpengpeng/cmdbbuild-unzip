/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget.utils;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class WidgetConst {

    public static final String WIDGET_TABLE = "_Widget",
            ATTR_OWNER = "Owner",
            ATTR_IS_ACTIVE = "Active",
            ATTR_TYPE = "Type",
            ATTR_DATA = "Data";

    public static final String WIDGET_BUTTON_LABEL_KEY = "ButtonLabel",
            WIDGET_OUTPUT_KEY = "Output",
            WIDGET_REQUIRED_KEY = "Required",
            WIDGET_ALWAYS_ENABLED_KEY = "AlwaysEnabled",
            WIDGET_FILTER_KEY = "Filter",
            WIDGET_DEFAULT_SELECTION_KEY = "DefaultSelection",
            WIDGET_CLASS_NAME = "ClassName";

    public static final String WIDGET_ACTION_SUBMIT = "submit",
            WIDGET_ACTION_ADVANCE = "advance";

    public static final String WIDGET_TYPE_CREATE_MODIFY_CARD = "createModifyCard",
            WIDGET_TYPE_CALENDAR = "calendar",
            WIDGET_TYPE_MANAGE_EMAIL = "manageEmail",
            WIDGET_TYPE_LINK_CARDS = "linkCards",
            WIDGET_TYPE_CUSTOM_FORM = "customForm",
            WIDGET_TYPE_OPEN_ATTACHMENT = "openAttachment",
            WIDGET_TYPE_CREATE_REPORT = "createReport",
            WIDGET_TYPE_PRESET_FROM_CARD = "presetFromCard";

    public static final Set<String> WIDGETS_FOR_WORKFLOW = ImmutableSet.of(
            WIDGET_TYPE_CREATE_MODIFY_CARD,
            WIDGET_TYPE_CALENDAR,
            WIDGET_TYPE_MANAGE_EMAIL,
            WIDGET_TYPE_LINK_CARDS,
            WIDGET_TYPE_CUSTOM_FORM,
            WIDGET_TYPE_OPEN_ATTACHMENT,
            WIDGET_TYPE_CREATE_REPORT,
            WIDGET_TYPE_PRESET_FROM_CARD);

    public static final Set<String> WIDGET_ATTR_KEYS_FOR_CQL_PROCESSING = ImmutableSet.of(WIDGET_FILTER_KEY, WIDGET_DEFAULT_SELECTION_KEY);
}
