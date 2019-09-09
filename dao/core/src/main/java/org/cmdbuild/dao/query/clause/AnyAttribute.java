package org.cmdbuild.dao.query.clause;

import static com.google.common.reflect.Reflection.newProxy;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;
import static org.cmdbuild.common.utils.Reflection.unsupported;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.dao.query.clause.alias.EntryTypeAlias.canonicalAlias;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.function.StoredFunctionParameter;

public class AnyAttribute extends ForwardingQueryAttribute {

	public static AnyAttribute anyAttribute(final EntryType entryType) {
		return anyAttribute(canonicalAlias(entryType));
	}

	public static AnyAttribute anyAttribute(final Alias entryTypeAlias) {
		return new AnyAttribute(entryTypeAlias);
	}

	/*
	 * TODO: Should be replaced by anyAttribute(f) when it works
	 */
	public static QueryAttribute[] anyAttribute(final StoredFunction function, final Alias f) {
		final List<QueryAttribute> attributes = new ArrayList<>();
		for (final StoredFunctionParameter p : function.getOutputParameters()) {
			attributes.add(attribute(f, p.getName()));
		}

		return attributes.toArray(new QueryAttribute[attributes.size()]);
	}

	private static final QueryAttribute UNSUPPORTED = newProxy(QueryAttribute.class, unsupported("should not be used"));

	private final Alias alias;

	AnyAttribute(final Alias alias) {
		this.alias = alias;
	}

	@Override
	protected QueryAttribute delegate() {
		return UNSUPPORTED;
	}

	@Override
	public void accept(final QueryAttributeVisitor visitor) {
		visitor.accept(this);
	}

	@Override
	public Alias getAlias() {
		return alias;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof AnyAttribute)) {
			return false;
		}
		final AnyAttribute other = AnyAttribute.class.cast(obj);
		return new EqualsBuilder() //
				.append(alias, other.alias).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder() //
				.append(alias) //
				.toHashCode();
	}

	@Override
	public String toString() {
		return reflectionToString(this, SHORT_PREFIX_STYLE);
	}

}
