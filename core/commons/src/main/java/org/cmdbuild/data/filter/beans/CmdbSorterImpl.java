/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.SorterElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CmdbSorterImpl implements CmdbSorter {

    private final List<SorterElement> elements;

    public CmdbSorterImpl(SorterElement... elements) {
        this(list(elements));
    }

    public CmdbSorterImpl(List<SorterElement> elements) {
        this.elements = ImmutableList.copyOf(checkNotNull(elements));
    }

    @Override
    public List<SorterElement> getElements() {
        return elements;
    }

    @Override
    public CmdbSorter mapAttributeNames(Map<String, String> mapping) {
        return new CmdbSorterImpl(elements.stream().map((e) -> new SorterElementImpl(firstNonNull(mapping.get(e.getProperty()), e.getProperty()), e.getDirection())).collect(toImmutableList()));
    }
    private final static CmdbSorter NOOP = new CmdbSorterImpl();

    public static CmdbSorter noopSorter() {
        return NOOP;
    }

    @Override
    public String toString() {
        return "CmdbSorterImpl{" + elements + '}';
    }

}
