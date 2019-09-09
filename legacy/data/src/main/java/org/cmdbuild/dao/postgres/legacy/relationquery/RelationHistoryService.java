package org.cmdbuild.dao.postgres.legacy.relationquery;

import org.cmdbuild.dao.entrytype.Domain;

public interface RelationHistoryService {

	GetRelationHistoryResponse getRelationHistory(String classId, Long cardId);

	GetRelationHistoryResponse getRelationHistory(String classId, Long cardId, Domain domain);

}
