/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import java.util.List;
import java.util.Map;

public interface CmdbSorter {

    List<SorterElement> getElements();

    CmdbSorter mapAttributeNames(Map<String, String> mapping);

    default int count() {
        return getElements().size();
    }

    default boolean isNoop() {
        return count() == 0;
    }
}
