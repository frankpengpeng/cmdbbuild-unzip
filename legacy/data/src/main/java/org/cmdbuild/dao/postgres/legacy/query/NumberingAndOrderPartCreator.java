package org.cmdbuild.dao.postgres.legacy.query;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;
import static org.cmdbuild.dao.postgres.Const.SystemAttributes.Id;
import static org.cmdbuild.dao.postgres.Const.SystemAttributes.RowNumber;
import static org.cmdbuild.dao.postgres.Const.SystemAttributes.RowsCount;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.nameForSystemAttribute;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.nameForUserAttribute;
import static org.cmdbuild.dao.postgres.legacy.query.SelectPartCreator.ATTRIBUTES_SEPARATOR;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cmdbuild.dao.entrytype.CMEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.CMFunctionCall;
import org.cmdbuild.dao.entrytype.ForwardingEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.NullEntryTypeVisitor;
import org.cmdbuild.dao.query.QuerySpecs;
import org.cmdbuild.dao.query.clause.OrderByClause;
import org.cmdbuild.dao.query.clause.OrderByClause.Direction;
import org.cmdbuild.dao.query.clause.QueryAttribute;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.aliasToQuotedSql;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.dao.query.clause.alias.NameAlias.nameAlias;

public class NumberingAndOrderPartCreator extends PartCreator {

	private static class SpecialFeaturesChecker extends ForwardingEntryTypeVisitor {

		private final CMEntryTypeVisitor delegate = NullEntryTypeVisitor.getInstance();

		private boolean addDefaultOrderings;

		@Override
		protected CMEntryTypeVisitor delegate() {
			return delegate;
		}

		/**
		 * @return {@code true} if default orderings must be added,
		 *         {@code false} otherwise ({@code true} is default).
		 */
		public boolean addDefaultOrderings(final EntryType type) {
			addDefaultOrderings = true;
			type.accept(this);
			return addDefaultOrderings;
		}

		@Override
		public void visit(final CMFunctionCall type) {
			addDefaultOrderings = false;
		}

	}

	private static final String SPACE = " ";
	private static final String ORDER_BY = "ORDER BY";
	private static final String ORDER_BY_ATTRIBUTE_EXPRESSION = "%s %s";

	private static final SpecialFeaturesChecker specialFeaturesChecker = new SpecialFeaturesChecker();

	public NumberingAndOrderPartCreator(final QuerySpecs querySpecs, final StringBuilder main) {
		final String actual = main.toString();
		main.setLength(0);

		final String orderByAttributesExpression = join(expressionsForOrdering(querySpecs), ATTRIBUTES_SEPARATOR);

		if (querySpecs.count() || querySpecs.numbered()) {
			final List<String> selectAttributes = newArrayList();

			// any attribute of default query
			selectAttributes.add("*");

			// count
			if (querySpecs.count()) {
				selectAttributes.add(format("count(*) over() AS %s", //
						nameForSystemAttribute(querySpecs.getFromClause().getAlias(), RowsCount)));
			}

			/*
			 * row number (if possible) uses row_number feature for ordering
			 */
			if (querySpecs.numbered() && !orderByAttributesExpression.isEmpty()) {
				selectAttributes.add(format("row_number() OVER (%s %s) AS %s", //
						ORDER_BY, //
						orderByAttributesExpression, //
						nameForSystemAttribute(querySpecs.getFromClause().getAlias(), RowNumber)));
			}

			sb.append(format("SELECT %s FROM (%s) AS main", //
					join(selectAttributes, ATTRIBUTES_SEPARATOR), //
					actual));

			/*
			 * does not uses row_number feature for ordering
			 */
			if (!querySpecs.numbered() && !orderByAttributesExpression.isEmpty()) {
				sb.append(LINE_SEPARATOR).append(ORDER_BY).append(SPACE).append(orderByAttributesExpression);
			}

			if (querySpecs.numbered()) {
				sb.append(new ConditionOnNumberedQueryPartCreator(querySpecs, sb).getPart());
			}
		} else {
			sb.append(actual);
			if (!orderByAttributesExpression.isEmpty()) {
				sb.append(LINE_SEPARATOR).append(ORDER_BY).append(SPACE).append(orderByAttributesExpression);
			}
		}
	}

	/**
	 * Returns a list of all ordering expression in the format where each
	 * expression has the format: <br>
	 * <br>
	 * {@code "attribute [ASC|DESC]}" <br>
	 * <br>
	 * If no attributes are specified then {@code Id} is added (if possible).
	 * 
	 * @return a list of ordering expressions (if any).
	 */
	private static List<String> expressionsForOrdering(final QuerySpecs querySpecs) {
		return quotedOrderClauses(querySpecs).entrySet().stream() //
				.map(input -> {
					final String key = input.getKey();
					final Direction value = input.getValue();
					return (value == null) ? key : format(ORDER_BY_ATTRIBUTE_EXPRESSION, key, value);
				}) //
				.collect(toList());
	}

	static Map<String, Direction> quotedOrderClauses(final QuerySpecs querySpecs) {
		final Map<String, Direction> output = new LinkedHashMap<>();
		for (final OrderByClause clause : querySpecs.getOrderByClauses()) {
			final QueryAttribute attribute = clause.getAttribute();
			output.put(aliasToQuotedSql(nameAlias(nameForUserAttribute(attribute.getAlias(), attribute.getName()))),
					clause.getDirection());
		}
		if ((!output.isEmpty() || !querySpecs.skipDefaultOrdering())
				&& specialFeaturesChecker.addDefaultOrderings(querySpecs.getFromClause().getType())) {
			output.put(aliasToQuotedSql(nameAlias(nameForSystemAttribute(querySpecs.getFromClause().getAlias(), Id))), null);
		}
		return output;
	}

}
