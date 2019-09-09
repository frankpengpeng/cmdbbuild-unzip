package org.cmdbuild.core.api.fluent;

import org.cmdbuild.api.fluent.Lookup;

public class LookupWrapper implements Lookup {

	private final org.cmdbuild.lookup.Lookup inner;

	public LookupWrapper(final org.cmdbuild.lookup.Lookup inner) {
		this.inner = inner;
	}

	@Override
	public String getType() {
		return inner.getType().getName();
	}

	@Override
	public String getCode() {
		return inner.getCode();
	}

	@Override
	public String getDescription() {
		return inner.getDescription();
	}

	@Override
	public Long getId() {
		return  inner.getId();
	}

}
