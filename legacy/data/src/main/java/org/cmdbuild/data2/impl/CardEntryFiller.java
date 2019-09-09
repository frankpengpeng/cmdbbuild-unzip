package org.cmdbuild.data2.impl;

import static com.google.common.collect.FluentIterable.from;

import java.util.Map;
import java.util.Map.Entry;

import org.cmdbuild.dao.beans.ForwardingCard;
import org.cmdbuild.logic.data.access.resolver.ForeignReferenceResolver.EntryFiller;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.beans.Card;

public class CardEntryFiller extends EntryFiller<Card> {

	private static class StaticForwarder extends ForwardingCard {

		private final Card delegate;
		private final Map<String, Object> values;
		private final boolean includeSystemAttributes;

		public StaticForwarder(final Card delegate, final Map<String, Object> currentValues,
				final boolean includeSystemAttributes) {
			this.delegate = delegate;
			this.values = Maps.newHashMap(currentValues);
			this.includeSystemAttributes = includeSystemAttributes;
		}

		@Override
		protected Card delegate() {
			return delegate;
		}

		@Override
		public Iterable<Entry<String, Object>> getRawValues() {
			return values.entrySet();
		}

		@Override
		public Iterable<Entry<String, Object>> getAttributeValues() {
			return from(getRawValues()) //
					.filter(new Predicate<Map.Entry<String, Object>>() {

						@Override
						public boolean apply(final Entry<String, Object> input) {
							final String name = input.getKey();
							final Attribute attribute = getType().getAttributeOrNull(name);
							return (attribute != null) && (includeSystemAttributes || !attribute.hasNotServiceListPermission());
						}

					});
		}

	}

	public static class Builder implements org.apache.commons.lang3.builder.Builder<CardEntryFiller> {

		private boolean includeSystemAttributes;

		/**
		 * use factory method
		 */
		private Builder() {
		}

		@Override
		public CardEntryFiller build() {
			return new CardEntryFiller(this);
		}

		public Builder includeSystemAttributes(final boolean includeSystemAttributes) {
			this.includeSystemAttributes = includeSystemAttributes;
			return this;
		}

	}

	public static Builder newInstance() {
		return new Builder();
	}

	private final boolean includeSystemAttributes;

	private CardEntryFiller(final Builder builder) {
		this.includeSystemAttributes = builder.includeSystemAttributes;
	}

	@Override
	public Card getOutput() {
		return new StaticForwarder(input, values, includeSystemAttributes);
	}

}
