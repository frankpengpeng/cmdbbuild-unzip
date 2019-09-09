/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.legacy.relationquery;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import org.cmdbuild.dao.postgres.legacy.relationquery.GetRelationHistoryResponse;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationInfo;

public class GetRelationHistoryResponseImpl implements GetRelationHistoryResponse {

	private final List<RelationInfo> relations;

	public GetRelationHistoryResponseImpl(List<RelationInfo> relations) {
		this.relations = ImmutableList.copyOf(relations);
	}

//	
//
//	@Override
//	protected void doAddRelation(final RelationInfo relationInfo) {
//		relations.add(relationInfo);
//	}
	@Override
	public Iterator<RelationInfo> iterator() {
		return relations.iterator();
	}

}
