/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.legacy.relationquery;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import org.cmdbuild.dao.postgres.legacy.relationquery.DomainInfo;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationInfo;
import org.cmdbuild.dao.query.clause.QueryDomain;

public class DomainInfoImpl implements DomainInfo {

	private final QueryDomain querydomain;
	private final List<RelationInfo> relations;

	public DomainInfoImpl(QueryDomain queryDomain, List<RelationInfo> relations) {
		this.querydomain = queryDomain;
		this.relations = ImmutableList.copyOf(relations);
	}

	@Override
	public QueryDomain getQueryDomain() {
		return querydomain;
	}

	@Override
	public String getDescription() {
		return querydomain.getDescription();
	}

	@Override
	public Iterator<RelationInfo> iterator() {
		return relations.iterator();
	}

}
