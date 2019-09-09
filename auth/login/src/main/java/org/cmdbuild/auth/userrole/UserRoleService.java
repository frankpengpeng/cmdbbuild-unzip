/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.userrole;

import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.UserRepository;

public interface UserRoleService extends UserRoleRepository, UserRepository, RoleRepository {

}
