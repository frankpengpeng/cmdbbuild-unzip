package org.cmdbuild.dao.query.clause;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.dao.query.clause.alias.EntryTypeAlias.canonicalAlias;

public class QueryAliasAttribute implements QueryAttribute {

	private final Alias alias;
	private final String name;

	private QueryAliasAttribute(Alias alias, String name) {
		this.alias = checkNotNull(alias);
		this.name = checkNotBlank(name);
	}

	@Override
	public void accept(QueryAttributeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Alias getAlias() {
		return alias;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof QueryAliasAttribute)) {
			return false;
		}
		QueryAliasAttribute other = QueryAliasAttribute.class.cast(obj);
		return new EqualsBuilder()
				.append(alias, other.alias)
				.append(name, other.name)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(alias)
				.append(name)
				.toHashCode();
	}

	@Override
	public String toString() {
		return reflectionToString(this, SHORT_PREFIX_STYLE);
	}

	public static QueryAliasAttribute attribute(EntryType type, Attribute attribute) {
		return attribute(type, attribute.getName());
	}

	public static QueryAliasAttribute attribute(EntryType type, String name) {
		return attribute(canonicalAlias(type), name);
	}

	public static QueryAliasAttribute attribute(Alias entryTypeAlias, Attribute attribute) {
		return attribute(entryTypeAlias, attribute.getName());
	}

	public static QueryAliasAttribute attribute(Alias entryTypeAlias, String name) {
		return new QueryAliasAttribute(entryTypeAlias, name);
	}
}
