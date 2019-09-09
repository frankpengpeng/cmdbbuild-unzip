package org.cmdbuild.dao.postgres.legacy.relationquery;

import static org.cmdbuild.dao.query.clause.DomainHistory.history;

import java.util.List;
import org.cmdbuild.dao.postgres.legacy.relationquery.GetRelationHistoryResponse;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationHistoryService;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationInfo;
import static java.util.stream.Collectors.toList;

import org.cmdbuild.dao.query.clause.QueryRelation;
import org.cmdbuild.dao.query.QueryResult;
import org.cmdbuild.dao.entrytype.Domain;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.view.DataView;

@Component
public class RelationHistoryServiceImpl extends AbstractGetRelation implements RelationHistoryService {

	public RelationHistoryServiceImpl(DataView view) {
		super(view);
	}

	@Override
	public GetRelationHistoryResponse getRelationHistory(String classId, Long cardId) {
		throw new UnsupportedOperationException("BROKEN - TODO");
//		return getRelationHistory(classId, cardId, anyDomain());
	}

	@Override
	public GetRelationHistoryResponse getRelationHistory(String classId, Long cardId, Domain domain) {
//		Validate.notNull(source);
		final QueryResult relationList = getRelationQuerySpecsBuilder(classId, cardId, history(domain), null).run();
		return createResponse(relationList);
	}

	private GetRelationHistoryResponse createResponse(QueryResult relationList) {
//		final GetRelationHistoryResponse out = new GetRelationHistoryResponseImpl(this);
		List<RelationInfo> list = relationList.stream().map((row) -> {

			Card src = row.getCard(SRC_ALIAS);
			QueryRelation rel = row.getRelation(DOM_ALIAS);
			Card dst = row.getCard(DST_ALIAS);
			return new RelationInfoImpl(rel, src, dst);
		}).collect(toList());
		return new GetRelationHistoryResponseImpl(list);
//		for ( CMQueryRow row : relationList) {
//			out.addRelation(rel, src, dst);
//		}
//		return out;
	}

}
