/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.q3;

import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;

public interface DaoQueryOptions {

    List<String> getAttrs();

    long getOffset();

    @Nullable
    Long getLimit();

    CmdbSorter getSorter();

    CmdbFilter getFilter();

    long getPositionOf();

    boolean getGoToPage();

    boolean hasPositionOf();

    default boolean hasAttrs() {
        return !getAttrs().isEmpty();
    }

    default boolean isPaged() {
        return PagedElements.isPaged(getOffset(), getLimit());
    }

    default boolean hasOffset() {
        return PagedElements.hasOffset(getOffset());
    }

    default boolean hasLimit() {
        return PagedElements.hasLimit(getLimit());
    }

}
