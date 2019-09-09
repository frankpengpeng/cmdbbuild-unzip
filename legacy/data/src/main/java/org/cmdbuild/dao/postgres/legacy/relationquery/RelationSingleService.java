package org.cmdbuild.dao.postgres.legacy.relationquery;

import com.google.common.base.Optional;
import org.cmdbuild.dao.entrytype.Domain;

public interface RelationSingleService {

	Optional<RelationInfo> getRelationInfo(Domain domain, Long id);

}
