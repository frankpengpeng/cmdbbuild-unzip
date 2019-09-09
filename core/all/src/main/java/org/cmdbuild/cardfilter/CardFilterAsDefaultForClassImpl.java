/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cardfilter;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.entrytype.Classe;

public class CardFilterAsDefaultForClassImpl implements CardFilterAsDefaultForClass {

    private final Classe forClass;
    private final StoredFilter filter;
    private final long roleId;

    public CardFilterAsDefaultForClassImpl(StoredFilter filter, Classe forClass, Long forRole) {
        this.forClass = checkNotNull(forClass);
        this.filter = checkNotNull(filter);
        this.roleId = forRole;
    }

    @Override
    public StoredFilter getFilter() {
        return filter;
    }

    @Override
    public Classe getDefaultForClass() {
        return forClass;
    }

    @Override
    public long getDefaultForRole() {
        return roleId;
    }

}
