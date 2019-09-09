package org.cmdbuild.dao.query;

import org.cmdbuild.dao.query.clause.QueryRelation;
import org.cmdbuild.dao.query.clause.alias.Alias;

import com.google.common.collect.ForwardingObject;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecordValues;

public abstract class ForwardingQueryRow extends ForwardingObject implements CMQueryRow {

	/**
	 * Usable by subclasses only.
	 */
	protected ForwardingQueryRow() {
	}

	@Override
	public Long getNumber() {
		return delegate().getNumber();
	}

	@Override
	public DatabaseRecordValues getValueSet(final Alias alias) {
		return delegate().getValueSet(alias);
	}

	@Override
	public boolean hasCard(Alias alias) {
		return delegate().hasCard(alias);
	}

	@Override
	public Card getCard(final Alias alias) {
		return delegate().getCard(alias);
	}

	@Override
	public boolean hasCard(Classe type) {
		return delegate().hasCard(type);
	}

	@Override
	public Card getCard(final Classe type) {
		return delegate().getCard(type);
	}

	@Override
	public QueryRelation getRelation(final Alias alias) {
		return delegate().getRelation(alias);
	}

	@Override
	public QueryRelation getRelation(final Domain type) {
		return delegate().getRelation(type);
	}

	@Override
	protected abstract CMQueryRow delegate();

}
