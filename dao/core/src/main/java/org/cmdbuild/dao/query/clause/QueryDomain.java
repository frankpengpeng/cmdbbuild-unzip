package org.cmdbuild.dao.query.clause;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import static org.cmdbuild.dao.entrytype.Domain.DEFAULT_INDEX_VALUE;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;

/**
 * Adds to the CMDomain the information about the attribute used as a source in
 * the query.
 */
public class QueryDomain {

	final Domain domain;
	final Source querySource;

	public QueryDomain(Domain domain, String querySource) {
		this(domain, parseEnum(querySource, Source.class));
	}

	public QueryDomain(Domain domain, Source querySource) {
		this.domain = domain;
		this.querySource = querySource;
	}

	public Domain getDomain() {
		return domain;
	}

	public String getQuerySource() {
		return querySource.name();
	}

	public Classe getSourceClass() {
		return querySource.getSourceClass(domain);
	}

	public Classe getTargetClass() {
		return querySource.getTargetClass(domain);
	}

	/**
	 * @return @deprecated Use {@link getQuerySource()} instead
	 */
	@Deprecated
	public boolean getDirection() {
		return querySource.getDirection();
	}

	public String getDescription() {
		return querySource.getDomainDescription(domain);
	}

	/**
	 * return index to order target instances. Convert 'default' to MAX_INT
	 *
	 * @return
	 */
	public int getIndex() {
		int index = querySource.getIndex(domain);
		return index == DEFAULT_INDEX_VALUE ? Integer.MAX_VALUE : index;
	}

	/*
	 * Object overrides
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.domain).append(this.querySource).hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof QueryDomain == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		final QueryDomain other = (QueryDomain) obj;
		return new EqualsBuilder().append(this.domain, other.domain).append(this.querySource, other.querySource)
				.isEquals();
	}

	/**
	 * Domains are between two classes only, but we want to design it for
	 * domains between more than two classes
	 */
	public enum Source {
		_1 {
			@Override
			public boolean getDirection() {
				return true;
			}

			@Override
			public String getDomainDescription(Domain domain) {
				return domain.getDirectDescription();
			}

			@Override
			public Classe getSourceClass(Domain domain) {
				return domain.getSourceClass();
			}

			@Override
			public Classe getTargetClass(Domain domain) {
				return domain.getTargetClass();
			}

			@Override
			public int getIndex(Domain domain) {
				return domain.getIndexForTarget();
			}
		},
		_2 {
			@Override
			public boolean getDirection() {
				return false;
			}

			@Override
			public String getDomainDescription(Domain domain) {
				return domain.getInverseDescription();
			}

			@Override
			public Classe getSourceClass(Domain domain) {
				return domain.getTargetClass();
			}

			@Override
			public Classe getTargetClass(Domain domain) {
				return domain.getSourceClass();
			}

			@Override
			public int getIndex(Domain domain) {
				return domain.getIndexForSource();
			}
		};

		public abstract boolean getDirection();

		public abstract String getDomainDescription(Domain domain);

		public abstract Classe getSourceClass(Domain domain);

		public abstract Classe getTargetClass(Domain domain);

		public abstract int getIndex(Domain domain);
	}
}
