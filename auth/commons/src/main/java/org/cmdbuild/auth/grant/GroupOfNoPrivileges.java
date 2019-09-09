/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;

public enum GroupOfNoPrivileges implements GroupOfPrivileges {

    INSTANCE;

    @Override
    public Set<GrantPrivilege> getPrivileges() {
        return emptySet();
    }

    @Override
    @Nullable
    public Map<String, Set<GrantAttributePrivilege>> getAttributePrivileges() {
        return emptyMap();
    }

    @Override
    public String getSource() {
        return "dummy";
    }

    @Override
    public CmdbFilter getFilter() {
        return CmdbFilterImpl.noopFilter();
    }

}
