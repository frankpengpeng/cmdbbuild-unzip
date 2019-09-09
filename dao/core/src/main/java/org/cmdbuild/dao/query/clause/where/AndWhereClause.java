package org.cmdbuild.dao.query.clause.where;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static java.util.Arrays.asList;

import java.util.List;

import com.google.common.collect.Streams;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.utils.lang.CmCollectionUtils;

public class AndWhereClause extends CompositeWhereClause {

	private AndWhereClause(List<? extends WhereClause> clauses) {
		super(clauses);
		checkArgument(clauses.size() > 1);
	}

	@Override
	public void accept(WhereClauseVisitor visitor) {
		visitor.visit(this);

	}

	public static WhereClause and(WhereClause... clauses) {
		return and(asList(clauses));
	}

	/**
	 * Creates a new {@link AndWhereClause} from the specified
	 * {@link WhereClause}s.<br>
	 * Clause
	 *
	 * The following considerations are performed:<br>
	 * <ul>
	 * <li>0 where clauses - throws exception</li>
	 * <li>1 where clause - clause</li>
	 * <li>2 or more where clauses - (clause1 AND clause2 AND ...)</li>
	 * </ul>
	 *
	 * @param whereClauses
	 *
	 * @return a newly created {@link AndWhereClause}.
	 *
	 * @throws IllegalArgumentException if there are no where clauses.
	 */
	public static WhereClause and(Iterable<? extends WhereClause> whereClauses) {
		whereClauses = Streams.stream(whereClauses).filter(not(TrueWhereClause.class::isInstance)).collect(toList());
		if (isEmpty(whereClauses)) {
			return TrueWhereClause.trueWhereClause();
		}
		if (Streams.stream(whereClauses).anyMatch(FalseWhereClause.class::isInstance)) {
			return FalseWhereClause.falseWhereClause();
		}
		if (size(whereClauses) == 1) {
			return getOnlyElement(whereClauses);
		} else {
			return new AndWhereClause(CmCollectionUtils.toList(whereClauses));
		}
	}

}
