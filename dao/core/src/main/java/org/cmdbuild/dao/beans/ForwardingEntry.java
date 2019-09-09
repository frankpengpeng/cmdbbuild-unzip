package org.cmdbuild.dao.beans;

import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.cmdbuild.dao.entrytype.EntryType;

public abstract class ForwardingEntry extends ForwardingValueSet implements DatabaseRecord {

	/**
	 * Usable by subclasses only.
	 */
	protected ForwardingEntry() {
	}

	@Override
	protected abstract DatabaseRecord delegate();

	@Override
	public EntryType getType() {
		return delegate().getType();
	}

	@Override
	public Long getId() {
		return delegate().getId();
	}

	@Override
	public String getUser() {
		return delegate().getUser();
	}

	@Override
	public DateTime getBeginDate() {
		return delegate().getBeginDate();
	}

	@Override
	public DateTime getEndDate() {
		return delegate().getEndDate();
	}

	@Override
	public Iterable<Entry<String, Object>> getRawValues() {
		return delegate().getRawValues();
	}

}
