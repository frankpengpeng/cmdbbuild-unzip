package org.cmdbuild.dao.postgres.legacy.query;

import static com.google.common.collect.Iterables.transform;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteAttribute;

import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cmdbuild.dao.query.clause.alias.Alias;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.aliasToQuotedSql;

public class SelectAttributesExpressions implements SelectAttributesHolder {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static class Element {

		private final Alias typeAlias;
		private final String name;
		private final String cast;
		private final Alias alias;
		private final transient String toString;

		public Element(final Alias typeAlias, final String name, final String cast, final Alias alias) {
			this.typeAlias = typeAlias;
			this.name = name;
			this.cast = cast;
			this.alias = alias;
			this.toString = ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
		}

		@Override
		public String toString() {
			return toString;
		}

	}

	private final Collection<Element> elements;

	public SelectAttributesExpressions() {
		elements = Lists.newArrayList();
	}

	@Override
	public void add(final Alias typeAlias, final String name, final String cast, final Alias alias) {
		final Element element = new Element(typeAlias, name, cast, alias);
		logger.trace("adding element '{}'", element);
		elements.add(element);
	}

	@Override
	public void add(final String cast, final Alias alias) {
		add(null, null, cast, alias);
	}

	/**
	 * Returns the expressions that must be used within <code>SELECT</code>
	 * statement.
	 * 
	 * @return an iterable collection of expressions.
	 */
	public Iterable<String> getExpressions() {
		return transform(elements, (Element input) -> {
			logger.trace("transforming element '{}'", input);
			final StringBuffer sb = new StringBuffer();

			if (input.typeAlias == null && input.name == null) {
				logger.trace("appending alias '{}'", input.alias);
				sb.append(aliasToQuotedSql(input.alias));
				if (input.cast != null) {
					logger.trace("appending cast '{}'", input.cast);
					sb.append("::").append(input.cast);
				}
			} else {
				sb.append(quoteAttribute(input.typeAlias, input.name));
				if (input.cast != null) {
					logger.trace("appending cast '{}'", input.cast);
					sb.append("::").append(input.cast);
				}
				if (input.alias != null) {
					logger.trace("appending alias '{}'", input.alias);
					sb.append(" AS ").append(aliasToQuotedSql(input.alias));
				}
			}

			final String expression = sb.toString();
			logger.trace("appending expression '{}'", expression);
			return expression;
		});
	}

}
