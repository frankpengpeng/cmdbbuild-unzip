package org.cmdbuild.auth.grant;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_ROLE_ID;
import org.cmdbuild.auth.grant.GrantEventBusService.GrantDataUpdatedEvent;
import org.cmdbuild.cache.CacheService;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class GrantDataRepositoryImpl implements GrantDataRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GrantEventBusService grantEventService;

    private final DaoService dao;
    private final CmCache<List<GrantData>> grantDataByRoleId;

    public GrantDataRepositoryImpl(DaoService dao, CacheService cacheService, GrantEventBusService grantEventBus) {
        this.dao = checkNotNull(dao);
        this.grantEventService = checkNotNull(grantEventBus);
        this.grantDataByRoleId = cacheService.newCache("grant_data_by_role_id");
    }

    private void invalidateCache() {
        grantDataByRoleId.invalidateAll();
        grantEventService.getEventBus().post(GrantDataUpdatedEvent.INSTANCE);
    }

    @Override
    public List<GrantData> getGrantsForRole(long roleId) {
        return grantDataByRoleId.get(Long.toString(roleId), () -> doGetGrantsForRole(roleId));
    }

    private List<GrantData> doGetGrantsForRole(long roleId) {
        return Ordering.natural().onResultOf(GrantDataRepositoryImpl::keyForGrant).sortedCopy(dao.selectAll().from(GrantData.class)
                .where(GRANT_ATTR_ROLE_ID, EQ, roleId)
                .asList());
    }

    @Override
    public List<GrantData> getGrantsForTypeAndRole(PrivilegedObjectType type, long groupId) {
        checkNotNull(type);
        return getGrantsForRole(groupId).stream().filter((g) -> equal(g.getType(), type)).collect(toList());
    }

    @Override
    public List<GrantData> setGrantsForRole(long roleId, Collection<GrantData> grants) {
        grants.forEach((g) -> checkArgument(g.getRoleId() == roleId, "role id mismatch: update grant for role id = %s, but supplied grant data role id = %s for record = %s", roleId, g.getRoleId(), g));

        Map<String, GrantData> currentGrants = uniqueIndex(getGrantsForRole(roleId), GrantDataRepositoryImpl::keyForGrant);
        Map<String, GrantData> newGrants = uniqueIndex(grants, GrantDataRepositoryImpl::keyForGrant);

        Collection<GrantData> toDelete = filterKeys(currentGrants, not(in(newGrants.keySet()))).values();
        Collection<GrantData> toCreate = filterKeys(newGrants, not(in(currentGrants.keySet()))).values();

        List<GrantData> toUpdate = Sets.intersection(currentGrants.keySet(), newGrants.keySet()).stream().map((key) -> {
            GrantData currentGrant = checkNotNull(currentGrants.get(key));
            GrantData newGrant = checkNotNull(newGrants.get(key));

            return GrantDataImpl.copyOf(newGrant).withId(currentGrant.getId()).build();
        }).collect(toList());

        toDelete.forEach(dao::delete);
        List<GrantData> res = list(toCreate.stream().map(dao::create).collect(toList()))
                .with(toUpdate.stream().map(dao::update).collect(toList()));

        invalidateCache();

        return Ordering.natural().onResultOf(GrantDataRepositoryImpl::keyForGrant).sortedCopy(res);
    }

    private static String keyForGrant(GrantData grantData) {
        return key(grantData.getType(), grantData.getObjectIdOrClassName());
    }

}
