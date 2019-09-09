/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.userrole;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.springframework.stereotype.Component;

@Component
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.userRoleRepository = checkNotNull(userRoleRepository);
        this.userRepository = checkNotNull(userRepository);
        this.roleRepository = checkNotNull(roleRepository);
    }

    @Override
    public void addRoleToUser(long userId, long roleId) {
        userRoleRepository.addRoleToUser(userId, roleId);
    }

    @Override
    public void removeRoleFromUser(long userId, long roleId) {
        userRoleRepository.removeRoleFromUser(userId, roleId);
    }

    @Override
    public LoginUser getUserOrNull(LoginUserIdentity login) {
        return userRepository.getUserOrNull(login);
    }

    @Override
    public LoginUser getUserByIdOrNull(Long userId) {
        return userRepository.getUserByIdOrNull(userId);
    }

    @Override
    public LoginUser getUser(LoginUserIdentity identity) {
        return userRepository.getUser(identity);
    }

    @Override
    public LoginUser getUserByEmailOrNull(String email) {
        return userRepository.getUserByEmailOrNull(email);
    }

    @Override
    public LoginUser getUserByUsernameOrNull(String username) {
        return userRepository.getUserByUsernameOrNull(username);
    }

    @Override
    public LoginUser getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    @Override
    public PagedElements<UserData> getMany(CmdbFilter filter, CmdbSorter sorter, Long offset, Long limit) {
        return userRepository.getMany(filter, sorter, offset, limit);
    }

    @Override
    public PagedElements<UserData> getAllWithRole(long roleId, CmdbFilter filter, CmdbSorter sorter, Long offset, Long limit) {
        return userRepository.getAllWithRole(roleId, filter, sorter, offset, limit);
    }

    @Override
    public List<UserData> getAllWithRole(long roleId) {
        return userRepository.getAllWithRole(roleId);
    }

    @Override
    public PagedElements<UserData> getAllWithoutRole(long roleId, CmdbFilter filter, CmdbSorter sorter, Long offset, Long limit) {
        return userRepository.getAllWithoutRole(roleId, filter, sorter, offset, limit);
    }

    @Override
    public UserData get(long id) {
        return userRepository.get(id);
    }

    @Override
    public UserData create(UserData user) {
        return userRepository.create(user);
    }

    @Override
    public UserData update(UserData user) {
        return userRepository.update(user);
    }

    @Override
    public List<Role> getAllGroups() {
        return roleRepository.getAllGroups();
    }

    @Override
    public Role getByIdOrNull(long groupId) {
        return roleRepository.getByIdOrNull(groupId);
    }

    @Override
    public Role getById(long groupId) {
        return roleRepository.getById(groupId);
    }

    @Override
    public Role getGroupWithNameOrNull(String groupName) {
        return roleRepository.getGroupWithNameOrNull(groupName);
    }

    @Override
    public Role getGroupWithName(String groupName) {
        return roleRepository.getGroupWithName(groupName);
    }

    @Override
    public Map<String, Role> getGroupsByName(Iterable<String> groupNames) {
        return roleRepository.getGroupsByName(groupNames);
    }

    @Override
    public Role update(Role group) {
        return roleRepository.update(group);
    }

    @Override
    public Role create(Role group) {
        return roleRepository.create(group);
    }

    @Override
    public List<UserRole> getUserGroups(long userId) {
        return roleRepository.getUserGroups(userId);
    }

    @Override
    public void setUserGroups(long userId, Collection<Long> userGroupIds, Long defaultGroupId) {
        roleRepository.setUserGroups(userId, userGroupIds, defaultGroupId);
    }

    @Override
    public void setUserGroupsByName(long userId, Collection<String> userGroups, String defaultGroup) {
        roleRepository.setUserGroupsByName(userId, userGroups, defaultGroup);
    }

    @Override
    public Role getByNameOrId(String roleId) {
        return roleRepository.getByNameOrId(roleId);
    }
}
