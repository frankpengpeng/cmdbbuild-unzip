/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.utils.lang.CmCollectionUtils;

public class SorterProcessor<T> {

    public static <T> List<T> sorted(Iterable<T> source, CmdbSorter sorter, KeyToValueFunction<T> keyToValueFunction) {
        if (sorter.isNoop()) {
            return CmCollectionUtils.toList(source);
        } else {
            return Streams.stream(source)
                    .sorted((T a, T b) -> {

                        for (SorterElement element : sorter.getElements()) {
                            Comparable va  = keyToValueFunction.apply(element.getProperty(), a);
                            Comparable vb = keyToValueFunction.apply(element.getProperty(), b);
                            int dir = element.getDirection().equals(SorterElement.SorterElementDirection.ASC) ? 1 : -1;
                            if (va  != null || vb != null) {
                                int res;
                                if (va  == null) {
                                    res = 1;
                                } else if (vb == null) {
                                    res = -1;
                                } else {
                                    res = va.compareTo(vb);
                                }
                                if (res != 0) {
                                    return res * dir;
                                }
                            }
                        }
                        return 0;

                    })
                    .collect(toList());
        }
    }

    public static <T extends Map> List<T> sorted(Iterable<T> source, CmdbSorter sorter) {
        return sorted(source, sorter, (k, m) -> (Comparable) ((Map) m).get(k));
    }

    public static interface KeyToValueFunction<T> {

        Comparable apply(String key, T object);
    }

}
