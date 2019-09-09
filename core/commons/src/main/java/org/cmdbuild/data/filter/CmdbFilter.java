/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.data.filter.beans.CompositeFilterImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface CmdbFilter {

    AttributeFilter getAttributeFilter();

    RelationFilter getRelationFilter();

    FulltextFilter getFulltextFilter();

    CqlFilter getCqlFilter();

    EcqlFilter getEcqlFilter();

    FunctionFilter getFunctionFilter();

    CompositeFilter getCompositeFilter();

    boolean hasAttributeFilter();

    boolean hasRelationFilter();

    boolean hasFulltextFilter();

    boolean hasCqlFilter();

    boolean hasEcqlFilter();

    boolean hasFunctionFilter();

    boolean hasCompositeFilter();

    CmdbFilter mapNames(Map<String, String> map);

    default CmdbFilter mapNames(String... map) {
        return mapNames((Map<String, String>) (Map) map(map));
    }

    default boolean isNoop() {
        return getFilterTypes().isEmpty();
    }

    default boolean hasFilter() {
        return !isNoop();
    }

    default CmdbFilter and(CmdbFilter otherFilter) {
        return CompositeFilterImpl.and(this, otherFilter);
    }

    default CmdbFilter or(CmdbFilter otherFilter) {
        return CompositeFilterImpl.or(this, otherFilter);
    }

    default CmdbFilter not() {
        return CompositeFilterImpl.not(this);
    }

    default void checkHasOnlySupportedFilterTypes(FilterType... supportedFilterTypes) {
        Set<FilterType> unsupportedFilterTypes = set(getFilterTypes()).without(supportedFilterTypes);
        checkArgument(unsupportedFilterTypes.isEmpty(), "error: found unsupported filter types = %s (only these filter types are supported = %s)", unsupportedFilterTypes, supportedFilterTypes);
    }

    default boolean hasFilterOfType(FilterType type) {
        return getFilterTypes().contains(type);
    }

    default Set<FilterType> getFilterTypes() {
        return (Set) set().accept((s) -> {
            if (hasAttributeFilter()) {
                s.add(FilterType.ATTRIBUTE);
            }
            if (hasCqlFilter()) {
                s.add(FilterType.CQL);
            }
            if (hasEcqlFilter()) {
                s.add(FilterType.ECQL);
            }
            if (hasFulltextFilter()) {
                s.add(FilterType.FULLTEXT);
            }
            if (hasFunctionFilter()) {
                s.add(FilterType.FUNCTION);
            }
            if (hasRelationFilter()) {
                s.add(FilterType.RELATION);
            }
            if (hasCompositeFilter()) {
                s.add(FilterType.COMPOSITE);
            }
        });
    }

}
