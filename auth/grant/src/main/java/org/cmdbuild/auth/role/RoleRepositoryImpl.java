package org.cmdbuild.auth.role;

import org.cmdbuild.auth.userrole.UserRole;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.collect.Ordering;

import java.util.Map;

import com.google.common.collect.Streams;
import com.google.common.eventbus.Subscribe;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Optional;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.annotation.Nullable;
import org.cmdbuild.auth.grant.Grant;
import org.cmdbuild.auth.grant.GrantEventBusService;
import org.cmdbuild.auth.grant.GrantService;
import static org.cmdbuild.auth.role.RoleImpl.copyOf;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.dao.core.q3.ResultRow;
import static org.codehaus.plexus.util.StringUtils.isBlank;
import org.springframework.context.annotation.Primary;

@Component
@Primary
public class RoleRepositoryImpl implements RoleRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final GrantService privilegeService;

    private final Holder<List<RoleInfo>> allGroups;
    private final CmCache<Optional<Role>> groupsByName;
    private final CmCache<Optional<Role>> groupsById;

    public RoleRepositoryImpl(DaoService dao, GrantService privilegeService, CacheService cacheService, GrantEventBusService grantEventService) {
        this.dao = checkNotNull(dao);
        this.privilegeService = checkNotNull(privilegeService);
        this.groupsByName = cacheService.newCache("groups_by_name");
        this.groupsById = cacheService.newCache("groups_by_id");
        this.allGroups = cacheService.newHolder("all_group_names");
        grantEventService.getEventBus().register(new Object() {
            @Subscribe
            public void handleGrantDataUpdatedEvent(GrantEventBusService.GrantDataUpdatedEvent event) {
                invalidateCache();
            }
        });
    }

    private void invalidateCache() {
        groupsByName.invalidateAll();
        groupsById.invalidateAll();
        allGroups.invalidate();
    }

    @Override
    public List<Role> getAllGroups() {
        return getAllGroupInfos().stream().map((g) -> getById(g.getId())).collect(toList());
    }

    @Override
    @Nullable
    public Role getByIdOrNull(long groupId) {
        return groupsById.get(groupId, () -> doGetGroupWithId(groupId)).orElse(null);
    }

    private Optional<Role> doGetGroupWithId(long groupId) {
        ResultRow record = dao.getByIdOrNull(Role.class, groupId);
        if (record == null) {
            return Optional.empty();
        } else {
            Role role = record.toModel();
            Collection<Grant> allPrivileges = privilegeService.getAllPrivilegesByGroupId(role.getId());
            role = copyOf(role).withPrivileges(allPrivileges).build();
            return Optional.of(role);
        }
    }

    @Override
    public Map<String, Role> getGroupsByName(Iterable<String> groupNames) {
        if (isEmpty(groupNames)) {
            return emptyMap();
        } else {
            Map<String, Role> map = map();
            Streams.stream(groupNames).forEach((groupName) -> {
                map.put(groupName, getGroupWithName(groupName));//leverage caching
            });
            return map;
        }
    }

    @Override
    @Nullable
    public Role getGroupWithNameOrNull(String groupName) {
        checkNotBlank(groupName);
        return groupsByName.get(groupName, () -> getAllGroupInfos().stream().filter((g) -> equal(g.getName(), groupName)).map((g) -> getById(g.getId())).collect(toOptional())).orElse(null);
    }

    @Override
    public Role create(Role group) {
        group = dao.create(group);
        invalidateCache();
        return group;
    }

    @Override
    public Role update(Role group) {
        group = dao.update(group);
        invalidateCache();
        return group;
    }

    @Override
    public List<UserRole> getUserGroups(long userId) {
        return dao.getJdbcTemplate().query("SELECT g.\"Id\" group_id,COALESCE(m.\"DefaultGroup\",FALSE) is_default FROM \"Role\" g JOIN \"Map_UserRole\" m ON g.\"Id\" = m.\"IdObj2\" WHERE g.\"Status\" = 'A' AND m.\"Status\" = 'A' AND m.\"IdObj1\" = ?", (r, i) -> {
            return new UserRoleImpl(getByIdOrNull(r.getLong("group_id")), r.getBoolean("is_default"));
        }, userId).stream().sorted(Ordering.natural().onResultOf(UserRole::getDescription)).collect(toList());
    }

    @Override
    public void setUserGroups(long userId, Collection<Long> userGroupIds, @Nullable Long defaultGroupId) {
        checkArgument(defaultGroupId == null || userGroupIds.contains(defaultGroupId));

        Map<Long, UserRole> curUserGroups = getUserGroups(userId).stream().collect(toMap(UserRole::getId, identity()));
        Long curDefaultGroup = curUserGroups.values().stream().filter(UserRole::isDefault).collect(toOptional()).map(UserRole::getId).orElse(null);

        Collection<Long> toRemove = curUserGroups.keySet().stream().filter(not(userGroupIds::contains)).collect(toSet());
        Collection<Long> toAdd = userGroupIds.stream().filter(not(curUserGroups::containsKey)).collect(toSet());

        if (curDefaultGroup != null && !equal(curDefaultGroup, defaultGroupId)) {
            dao.getJdbcTemplate().update("UPDATE \"Map_UserRole\" SET \"DefaultGroup\" = FALSE WHERE \"IdObj1\" = ? AND \"IdObj2\" = ? AND \"Status\" = 'A'", userId, curDefaultGroup);
        }
        toAdd.forEach((roleToAdd) -> {
            dao.getJdbcTemplate().update("INSERT INTO \"Map_UserRole\" (\"IdClass1\",\"IdClass2\",\"IdObj1\",\"IdObj2\") VALUES ('\"User\"','\"Role\"',?,?)", userId, roleToAdd);
        });
        toRemove.forEach((roleToRemove) -> {
            dao.getJdbcTemplate().update("UPDATE \"Map_UserRole\" SET \"Status\" = 'N' WHERE \"IdObj1\" = ? AND \"IdObj2\" = ? AND \"Status\" = 'A'", userId, roleToRemove);
        });
        if (defaultGroupId != null && !equal(curDefaultGroup, defaultGroupId)) {
            dao.getJdbcTemplate().update("UPDATE \"Map_UserRole\" SET \"DefaultGroup\" = TRUE WHERE \"IdObj1\" = ? AND \"IdObj2\" = ? AND \"Status\" = 'A'", userId, defaultGroupId);
        }
    }

    @Override
    public void setUserGroupsByName(long userId, Collection<String> userGroups, @Nullable String defaultGroup) {
        setUserGroups(userId, userGroups.stream().map(g -> getGroupWithName(g).getId()).collect(toList()), isBlank(defaultGroup) ? null : getGroupWithName(defaultGroup).getId());
    }

    public List<RoleInfo> getAllGroupInfos() {
        return allGroups.get(this::doGetAllGroups);
    }

    private List<RoleInfo> doGetAllGroups() {
        return dao.selectAll().from(Role.class).asList();
    }

    private static class UserRoleImpl implements UserRole {

        private final Role group;
        private final boolean isDefault;

        public UserRoleImpl(Role group, boolean isDefault) {
            this.group = checkNotNull(group);
            this.isDefault = isDefault;
        }

        @Override
        public Role getRole() {
            return group;
        }

        @Override
        public boolean isDefault() {
            return isDefault;
        }

    }
}
