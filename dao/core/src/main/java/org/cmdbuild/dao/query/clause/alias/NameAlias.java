package org.cmdbuild.dao.query.clause.alias;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

public class NameAlias extends AbstractAlias {

	private final String name;

	private NameAlias(String name) {
		this.name = checkNotBlank(name);
	}

	@Override
	public void accept(AliasVisitor visitor) {
		visitor.visit(this);
	}

	public String getName() {
		return name;
	}

	public static NameAlias nameAlias(String name) {
		return new NameAlias(name);
	}

	public static NameAlias randomAlias() {
		return new NameAlias(randomId().replaceAll("[0-9]", "").substring(0, 8));
	}

	@Override
	public String asString() {
		return name;
	}

}
