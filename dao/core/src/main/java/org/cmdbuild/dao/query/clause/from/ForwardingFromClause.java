package org.cmdbuild.dao.query.clause.from;

import org.cmdbuild.dao.query.clause.alias.Alias;

import com.google.common.collect.ForwardingObject;
import org.cmdbuild.dao.entrytype.EntryType;

public abstract class ForwardingFromClause extends ForwardingObject implements FromClause {

	/**
	 * Usable by subclasses only.
	 */
	protected ForwardingFromClause() {
	}

	@Override
	protected abstract FromClause delegate();

	@Override
	public EntryType getType() {
		return delegate().getType();
	}

	@Override
	public Alias getAlias() {
		return delegate().getAlias();
	}

	@Override
	public boolean isHistory() {
		return delegate().isHistory();
	}

	@Override
	public EntryTypeStatus getStatus(final EntryType entryType) {
		return delegate().getStatus(entryType);
	}

}
