package org.cmdbuild.dao.postgres.legacy.query;

import java.util.ArrayList;
import java.util.List;

import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.query.clause.where.Native;
import org.cmdbuild.dao.entrytype.EntryType;

public abstract class PartCreator {

	protected final StringBuilder sb;
	private final List<Object> params;

	/**
	 * Usable by subclasses only.
	 */
	protected PartCreator() {
		sb = new StringBuilder();
		params = new ArrayList<Object>();
	}

	public final String getPart() {
		return sb.toString();
	}

	protected final String param(final Object o) {
		return param(o, null);
	}

	// TODO Handle CMDBuild and Geographic types conversion
	protected final String param(final Object o, final String cast) {
		final String output;
		if (o instanceof List) {
			final List<Object> l = (List<Object>) o;
			if (l.size() == 1 && l.get(0) instanceof Native) {
				output = Native.class.cast(l.get(0)).expression;
			} else {
				final StringBuilder sb = new StringBuilder("(");
				int i = 1;
				for (final Object value : l) {
					sb.append("?");
					if (i < l.size()) {
						sb.append(",");
						i++;
					}
					Object effectiveValue = value;
					if (value instanceof IdAndDescriptionImpl) {
						effectiveValue = IdAndDescriptionImpl.class.cast(value).getId();
					}
					params.add(effectiveValue);
				}
				sb.append(")");
				output = sb.toString();
			}
		} else {
			params.add(o);
			output = "?" + (cast != null ? "::" + cast : "");
		}
		return output;
	}

	public final List<Object> getParams() {
		return params;
	}

	protected final String quoteType(final EntryType type) {
		throw new UnsupportedOperationException("BROKEN - TODO");
//		if (type instanceof ClassHistory) {
//			return entryTypeHistoryToQuotedSql(type);
//		}
//		return entryTypeToQuotedSql(type, (Object value) -> params.add(value));
	}
}
