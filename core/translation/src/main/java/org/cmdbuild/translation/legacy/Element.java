package org.cmdbuild.translation.legacy;

import static org.cmdbuild.data.store.Groupables.nameAndValue;
import static org.cmdbuild.translation.legacy.Constants.ELEMENT;

import org.cmdbuild.data.store.ForwardingGroupable;
import org.cmdbuild.data.store.Groupable;

public class Element extends ForwardingGroupable {

	public static Element of(final String value) {
		return new Element(nameAndValue(ELEMENT, value));
	}

	private final Groupable delegate;

	private Element(final Groupable delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Groupable delegate() {
		return delegate;
	}

}
