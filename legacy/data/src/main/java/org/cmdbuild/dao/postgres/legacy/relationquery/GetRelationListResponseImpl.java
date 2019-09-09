/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.legacy.relationquery;

import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Multimaps.index;
import java.util.Iterator;
import java.util.List;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;

public class GetRelationListResponseImpl implements GetRelationListResponse {

	private final List<DomainInfo> domainInfos;
	private final int totalNumberOfRelations;

	public GetRelationListResponseImpl(List<RelationInfo> relationInfos, int totalNumberOfRelations) {
		this.domainInfos = aggregateRelationInfos(relationInfos);
		this.totalNumberOfRelations = totalNumberOfRelations;
	}

	private static List<DomainInfo> aggregateRelationInfos(List<RelationInfo> relationInfos) {
		return index(relationInfos, r -> key(r.getQueryDomain().getDomain().getName(), r.getQueryDomain().getQuerySource())).asMap().values().stream().map((list)
				-> new DomainInfoImpl(list.iterator().next().getQueryDomain(), ImmutableList.copyOf(list))
		).collect(toImmutableList());
	}

	@Override
	public Iterator<DomainInfo> iterator() {
		return domainInfos.iterator();
	}

	@Override
	public int getTotalNumberOfRelations() {
		return totalNumberOfRelations;
	}

}
