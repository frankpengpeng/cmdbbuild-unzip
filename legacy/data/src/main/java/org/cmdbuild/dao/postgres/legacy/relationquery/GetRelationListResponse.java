/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.dao.postgres.legacy.relationquery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cmdbuild.dao.query.clause.QueryDomain;

public interface GetRelationListResponse  extends Iterable<DomainInfo> {
//
//	private final List<DomainInfo> domainInfos;
//	int totalNumberOfRelations;
//
//	private GetRelationListResponse(AbstractGetRelation outer) {
//		super(outer);
//		domainInfos = new ArrayList<>();
//	}
//
//	@Override
//	protected void doAddRelation(RelationInfo relationInfo) {
//		getOrCreateDomainInfo(relationInfo.getQueryDomain()).addRelationInfo(relationInfo);
//	}
//
//	private DomainInfo getOrCreateDomainInfo(QueryDomain qd) {
//		for (DomainInfo di : domainInfos) {
//			if (di.getQueryDomain().equals(qd)) {
//				return di;
//			}
//		}
//		return addDomainInfo(qd);
//	}

//	private DomainInfo addDomainInfo(QueryDomain qd) {
//		DomainInfo di = new DomainInfo(qd);
//		domainInfos.add(di);
//		return di;
//	}

//	void setTotalNumberOfRelations(int totalNumberOfRelations) {
//		this.totalNumberOfRelations = totalNumberOfRelations;
//	}

//	@Override
//	public Iterator<DomainInfo> iterator() {
//		return domainInfos.iterator();
//	}

	 int getTotalNumberOfRelations();

}
