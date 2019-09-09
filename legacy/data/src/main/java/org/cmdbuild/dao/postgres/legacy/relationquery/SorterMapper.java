package org.cmdbuild.dao.postgres.legacy.relationquery;

import java.util.List;

import org.cmdbuild.dao.query.clause.OrderByClause;

public interface SorterMapper {

	/**
	 * Method that returns a list of OrderByClause starting from a given
	 * "configuration" (e.g. JSON, XML, etc).
	 */
	List<OrderByClause> deserialize();

}
