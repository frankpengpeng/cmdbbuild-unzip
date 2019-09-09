/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import static com.google.common.base.Objects.equal;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toSet;
import static org.cmdbuild.classe.access.userCardAccessUtils.buildFilterMarkName;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.data.filter.CmdbFilter;

public interface UserCardAccess {

    CmdbFilter getWholeClassFilter();

    Map<String, UserCardAccessWithFilter> getSubsetFiltersByName();

    Classe getUserClass(Set<String> activeFilters);

    default boolean hasWholeClassFilter() {
        return !getWholeClassFilter().isNoop();
    }

    Consumer<QueryBuilder> addSubsetFilterMarkersToQueryVisitor();

    default Card addCardAccessPermissionsFromSubfilterMark(Card card) {
        Set<String> activeFilters = getSubsetFiltersByName().keySet().stream().filter((k) -> {
            return card.getNotNull(buildFilterMarkName(k), Boolean.class) == true;
        }).collect(toSet());
        Classe userClass = getUserClass(activeFilters);
        if (!equal(userClass.getName(), card.getType().getName())) {
            userClass = ClasseImpl.copyOf(card.getType())
                    .withAttributes(userClass.getAllAttributes())
                    .withPermissions(userClass)
                    .build();
        }
        return CardImpl.copyOf(card).withType(userClass).build();
    }
}
