/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import java.util.Collection;

public interface DomainMetadata extends EntryTypeMetadata {

    static final String DOMAIN_TYPE = "cm_type";
    static final String CARDINALITY = "cm_cardinality";
    static final String CLASS_1 = "cm_class1";
    static final String CLASS_2 = "cm_class2";
    static final String DESCRIPTION_1 = "cm_description1";
    static final String DESCRIPTION_2 = "cm_description2";
    static final String MASTERDETAIL = "cm_masterdetail";
    static final String MASTERDETAIL_DESCRIPTION = "cm_masterdetail_label";
    static final String MASTERDETAIL_FILTER = "cm_masterdetail_filter";
    static final String DISABLED_1 = "cm_disabled_1";
    static final String DISABLED_2 = "cm_disabled_2";
    static final String INDEX_1 = "cm_index1";
    static final String INDEX_2 = "cm_index2",
            INLINE = "cm_show_inline",
            DEFAULT_CLOSED = "cm_show_inline_default_closed";

    String getDirectDescription();

    String getInverseDescription();

    String getCardinality();

    boolean isMasterDetail();

    String getMasterDetailDescription();

    String getMasterDetailFilter();

    Collection<String> getDisabledSourceDescendants();

    Collection<String> getDisabledTargetDescendants();

    int getIndexForSource();

    int getIndexForTarget();

    boolean isInline();

    boolean isDefaultClosed();

}
