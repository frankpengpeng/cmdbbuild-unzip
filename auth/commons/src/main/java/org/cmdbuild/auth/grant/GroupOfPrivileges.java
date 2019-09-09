/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.cmdbuild.data.filter.CmdbFilter;

public interface GroupOfPrivileges {

    String getSource();

    Set<GrantPrivilege> getPrivileges();

    @Nullable
    Map<String, Set<GrantAttributePrivilege>> getAttributePrivileges();

    CmdbFilter getFilter();

    default boolean hasFilter() {
        return !getFilter().isNoop();
    }

}
