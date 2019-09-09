package org.cmdbuild.auth.grant;

import java.util.Collection;
import java.util.List;

public interface GrantDataRepository {

    List<GrantData> getGrantsForTypeAndRole(PrivilegedObjectType type, long groupId);

    List<GrantData> getGrantsForRole(long roleId);

    List<GrantData> setGrantsForRole(long roleId, Collection<GrantData> grants);

}
