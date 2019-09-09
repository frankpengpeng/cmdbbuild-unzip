package org.cmdbuild.dao.query.clause.join;

import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.query.clause.alias.EntryTypeAlias;
import org.cmdbuild.dao.entrytype.Domain;

public class Over {

	private final Domain domain;
	private final Alias alias;

	public static Over over(final Domain domain) {
		return over(domain, EntryTypeAlias.canonicalAlias(domain));
	}

	public static Over over(final Domain domain, final Alias alias) {
		return new Over(domain, alias);
	}

	private Over(final Domain domain, final Alias alias) {
		this.domain = domain;
		this.alias = alias;
	}

	public Domain getDomain() {
		return domain;
	}

	public Alias getAlias() {
		return alias;
	}

}
