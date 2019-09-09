package org.cmdbuild.workflow.core;

import org.cmdbuild.workflow.model.GroupQueryAdapter;
import static com.google.common.collect.FluentIterable.from;


import com.google.common.base.Function;
import org.springframework.stereotype.Component;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.login.AuthenticationService;

@Component
public class GroupQueryAdapterImpl implements GroupQueryAdapter {

	private static final Function<Role, String> TO_GROUP_NAME = (Role input) -> input.getName();

	private final AuthenticationService authenticationService;

	public GroupQueryAdapterImpl(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public String[] getAllGroupNames() {
		return from(authenticationService.getAllGroups())
				.transform(TO_GROUP_NAME)
				.toArray(String.class);
	}

}
