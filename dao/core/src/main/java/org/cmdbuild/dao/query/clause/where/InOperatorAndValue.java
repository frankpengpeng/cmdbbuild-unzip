package org.cmdbuild.dao.query.clause.where;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.copyOf;
import java.util.List;

import com.google.common.collect.Lists;
import java.util.Collection;

public class InOperatorAndValue implements OperatorAndValue {

	private final List<Object> values;

	private InOperatorAndValue(final Object... objects) {
		this.values = Lists.newArrayList(objects);
		checkArgument(!values.isEmpty(), "cannot execute query with IN and an empty set of values");
	}

	public List<Object> getValue() {
		return values;
	}

	@Override
	public void accept(final OperatorAndValueVisitor visitor) {
		visitor.visit(this);
	}

	public static OperatorAndValue in(Object... objects) {
		return new InOperatorAndValue(objects);
	}

	public static OperatorAndValue in(Iterable objects) {
		return in((objects instanceof Collection ? ((Collection) objects) : copyOf(objects)).toArray());
	}

}
