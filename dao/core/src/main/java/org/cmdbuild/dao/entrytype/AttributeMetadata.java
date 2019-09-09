/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.entrytype.attributetype.UnitOfMeasureLocation;

public interface AttributeMetadata extends AbstractMetadata {

    static final String BASEDSP = "cm_basedsp";
    static final String CLASSORDER = "cm_classorder";
    static final String DEFAULT = "cm_default";
    static final String EDITOR_TYPE = "cm_text_editor_type";
    static final String DOMAINKEY = "cm_domain_key";
    static final String FILTER = "cm_filter";
    static final String GROUP = "cm_group";
    static final String INDEX = "cm_index";
    static final String INHERITED = "cm_inherited";
    static final String IP_TYPE = "cm_ip_type";
    static final String LOOKUP_TYPE = "cm_lookup_type";
    static final String MANDATORY = "cm_mandatory";
    static final String REFERENCE_DIRECTION = "cm_reference_direction";
    static final String REFERENCE_DOMAIN = "cm_reference_domain";
    static final String UNIQUE = "cm_unique";
    static final String FK_TARGET_CLASS = "cm_fk_target_class";
    static final String SHOW_IN_REDUCED_GRID = "cm_show_in_reduced_grid",
            FORMAT_PATTERN = "cm_format_pattern",
            PRESELECT_IF_UNIQUE = "cm_preselect_if_unique",
            UNIT_OF_MEASURE = "cm_unit_of_measure",
            UNIT_OF_MEASURE_LOCATION = "cm_unit_of_measure_location",
            VISIBLE_DECIMALS = "cm_visible_decimals",
            SHOW_SEPARATORS = "cm_show_separators",
            HELP_MESSAGE = "cm_help",
            SHOW_IF_EXPR = "cm_showIf",
            VALIDATION_RULES_EXPR = "cm_validationRules",
            AUTO_VALUE_EXPR = "cm_autoValue",
            SHOW_THOUSANDS_SEPARATOR = "cm_show_thousands_separator",
            SHOW_SECONDS = "cm_show_seconds",
            IS_MASTER_DETAIL = "cm_is_master_detail",
            MASTER_DETAIL_DESCRIPTION = "cm_master_detail_description";

    boolean showInGrid();

    boolean showInReducedGrid();

    boolean isMandatory();

    boolean isUnique();

    boolean isInherited();

    boolean isDomainKey();

    boolean showThousandsSeparator();

    boolean showSeconds();

    int getIndex();

    int getClassOrder();

    @Nullable
    String getHelpMessage();

    @Nullable
    String getShowIfExpr();

    @Nullable
    String getValidationRulesExpr();

    @Nullable
    String getAutoValueExpr();

    @Nullable
    String getDefaultValue();

    @Nullable
    String getGroup();

    @Nullable
    String getEditorType();

    @Nullable
    String getFilter();

    @Nullable
    String getLookupType();

    @Nullable
    String getDomain();

    @Nullable
    RelationDirection getDirection();

    @Nullable
    String getForeignKeyDestinationClassName();

    @Nullable
    String getUnitOfMeasure();

    UnitOfMeasureLocation getUnitOfMeasureLocation();

    @Nullable
    Integer getVisibleDecimals();

    boolean preselectIfUnique();

    boolean showSeparators();

    @Nullable
    String getFormatPattern();

    @Nullable
    String getMasterDetailDescription();

    @Nullable
    Boolean isMasterDetail();

    AttributePermissionMode getMode();

    Map<PermissionScope, Set<AttributePermission>> getPermissionMap();

    default boolean isLookup() {
        return isNotBlank(getLookupType());
    }

    default boolean isReference() {
        return isNotBlank(getDomain());
    }

    default boolean isForeignKey() {
        return isNotBlank(getForeignKeyDestinationClassName());
    }

}
