package org.cmdbuild.dao.query.clause;

import org.cmdbuild.dao.beans.CMRelation;

public class QueryRelation {

	final CMRelation relation;
	final String querySource;

	public QueryRelation(CMRelation relation, String querySource) {
		this.relation = relation;
		this.querySource = querySource;
	}

	public CMRelation getRelation() {
		return relation;
	}

	public QueryDomain getQueryDomain() {
		return new QueryDomain(relation.getType(), querySource);
	}

	public static QueryRelation newInstance(CMRelation relation, String querySource) {
		return new QueryRelation(relation, querySource);
	}

}
