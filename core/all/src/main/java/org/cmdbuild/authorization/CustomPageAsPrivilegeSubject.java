package org.cmdbuild.authorization;

import static java.lang.String.format;

import org.cmdbuild.extcomponents.custompage.CustomPageData;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.extcomponents.commons.ExtComponentInfo;

public class CustomPageAsPrivilegeSubject implements PrivilegeSubjectWithInfo {

	private final long id;
	private final String name, description;

	public CustomPageAsPrivilegeSubject(ExtComponentInfo delegate) {
		this.id = delegate.getId();
		this.name = delegate.getName();
		this.description = delegate.getDescription();
	}

	public CustomPageAsPrivilegeSubject(CustomPageData delegate) {
		this.id = delegate.getId();
		this.name = delegate.getName();
		this.description = delegate.getDescription();
	}

	@Override
	public String getPrivilegeId() {
		return format("CustomPage:%d", getId());
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
