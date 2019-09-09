package org.cmdbuild.auth.user;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import static org.cmdbuild.auth.login.NullPrivilegeContext.nullPrivilegeContext;
import static org.cmdbuild.auth.grant.SystemPrivilegeContext.systemPrivilegeContext;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import static org.cmdbuild.auth.multitenant.UserTenantContextImpl.fullAccessUser;
import static org.cmdbuild.auth.multitenant.UserTenantContextImpl.minimalAccessUser;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RolePrivilege;
import static org.cmdbuild.auth.user.LoginUserImpl.ANONYMOUS_LOGIN_USER;
import org.cmdbuild.utils.lang.Builder;

public class OperationUserImpl implements OperationUser {

	private final static OperationUser ANONYMOUS = builder().withAuthenticatedUser(ANONYMOUS_LOGIN_USER).build(); //default user is anonymous with minimal access

	private final UserPrivileges privilegeContext;
	private final LoginUser authUser;
	private final UserTenantContext userTenantContext;
	private final Role defaultGroup;

	private OperationUserImpl(LoginUser authUser, UserPrivileges privilegeCtx, @Nullable Role defaultGroup, UserTenantContext userTenantContext) {
		this.privilegeContext = checkNotNull(privilegeCtx);
		this.authUser = checkNotNull(authUser);
		this.userTenantContext = checkNotNull(userTenantContext);
		this.defaultGroup = defaultGroup;
	}

	@Override
	public Map<String, UserPrivilegesForObject> getAllPrivileges() {
		return privilegeContext.getAllPrivileges();
	}

	@Override
	public Set<RolePrivilege> getRolePrivileges() {
		return privilegeContext.getRolePrivileges();
	}

	@Override
	public UserTenantContext getUserTenantContext() {
		return userTenantContext;
	}

	@Override
	public LoginUser getLoginUser() {
		return authUser;
	}

	@Override
	@Nullable
	public Role getDefaultGroupOrNull() {
		return defaultGroup;
	}

	@Override
	public UserPrivileges getPrivilegeContext() {
		return privilegeContext;
	}

	@Override
	public String toString() {
		return "OperationUserImpl{" + "privilegeContext=" + privilegeContext + ", authUser=" + authUser + ", userTenantContext=" + userTenantContext + ", defaultGroup=" + defaultGroup + '}';
	}

	public static OperationUserBuilder builder() {
		return new OperationUserBuilder();
	}

	public static OperationUserBuilder copyOf(OperationUser operationUser) {
		return builder()
				.withAuthenticatedUser(operationUser.getLoginUser())
				.withDefaultGroup(operationUser.getDefaultGroupOrNull())
				.withPrivilegeContext(operationUser.getPrivilegeContext())
				.withUserTenantContext(operationUser.getUserTenantContext());
	}

	public static OperationUser anonymousOperationUser() {
		return ANONYMOUS;
	}

	public static OperationUser sysAdminOperationUser() {
		return builder()
				.withPrivilegeContext(systemPrivilegeContext())
				.withUserTenantContext(fullAccessUser())
				.build();
	}

	public static class OperationUserBuilder implements Builder<OperationUser, OperationUserBuilder> {

		private UserPrivileges privilegeContext = nullPrivilegeContext();
		private LoginUser authenticatedUser;
		private UserTenantContext userTenantContext = minimalAccessUser();
		private Role defaultGroup;

		@Override
		public OperationUser build() {
			return new OperationUserImpl(authenticatedUser, privilegeContext, defaultGroup, userTenantContext);
		}

		public OperationUserBuilder withPrivilegeContext(UserPrivileges privilegeContext) {
			this.privilegeContext = privilegeContext;
			return this;
		}

		public OperationUserBuilder withAuthenticatedUser(LoginUser authenticatedUser) {
			this.authenticatedUser = authenticatedUser;
			return this;
		}

		public OperationUserBuilder withUserTenantContext(UserTenantContext userTenantContext) {
			this.userTenantContext = userTenantContext;
			return this;
		}

		public OperationUserBuilder withDefaultGroup(@Nullable Role defaultGroup) {
			this.defaultGroup = defaultGroup;
			return this;
		}

	}

}
