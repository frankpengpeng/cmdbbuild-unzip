package org.cmdbuild.workflow.core;

import org.cmdbuild.workflow.model.FlowStatus;
import org.cmdbuild.lookup.Lookup;

import com.google.common.base.Optional;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_STATUS;

public interface LookupHelper {

	FlowStatus stateForLookupCode(String code);

	Optional<Lookup> lookupForState(FlowStatus state);

	Optional<Lookup> flowStatusWithCode(String code);

	FlowStatus stateForLookupId(Long id);

	Iterable<Lookup> allLookups();

	default FlowStatus getFlowStatus(Card card) {
		return Optional.fromNullable(card.get(ATTR_FLOW_STATUS, IdAndDescriptionImpl.class)).transform((l) -> stateForLookupId(l.getId())).or(FlowStatus.UNDEFINED);
	}

	default Optional<Lookup> getFlowStatusLookup(Card card) {
		return lookupForState(getFlowStatus(card));
	}
}
