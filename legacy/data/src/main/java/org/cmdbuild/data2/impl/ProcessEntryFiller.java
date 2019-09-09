package org.cmdbuild.data2.impl;

import static com.google.common.collect.FluentIterable.from;

import java.util.Map;
import java.util.Map.Entry;

import org.cmdbuild.logic.data.access.resolver.ForeignReferenceResolver.EntryFiller;
import org.cmdbuild.workflow.inner.ForwardingFlow;

import com.google.common.collect.Maps;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.workflow.model.Flow;

public class ProcessEntryFiller extends EntryFiller<Flow> {

	private static class StaticForwarder extends ForwardingFlow {

		private final Flow delegate;
		private final Map<String, Object> values;

		public StaticForwarder(final Flow delegate, final Map<String, Object> currentValues) {
			this.delegate = delegate;
			this.values = Maps.newHashMap(currentValues);
		}

		@Override
		protected Flow delegate() {
			return delegate;
		}

		@Override
		public Object get(final String key) {
			return values.get(key);
		}

		@Override
		public Iterable<Entry<String, Object>> getRawValues() {
			return values.entrySet();
		}

		@Override
		public Iterable<Entry<String, Object>> getAttributeValues() {
			return from(getRawValues()) //
					.filter((Entry<String, Object> input1) -> {
						final String name = input1.getKey();
						final Attribute attribute = getType().getAttributeOrNull(name);
						return !attribute.hasNotServiceListPermission();
					});
		}

	}

	@Override
	public Flow getOutput() {
		return new StaticForwarder(input, values);
	}

}
