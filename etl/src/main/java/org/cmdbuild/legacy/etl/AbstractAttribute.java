package org.cmdbuild.legacy.etl;

abstract class AbstractAttribute implements Attribute {

	@Override
	public final boolean equals(final Object obj) {
		return doEquals(obj);
	}

	protected abstract boolean doEquals(final Object obj);

	@Override
	public final int hashCode() {
		return doHashCode();
	}

	protected abstract int doHashCode();

	@Override
	public String toString() {
		return doToString();
	}

	protected abstract String doToString();

}
