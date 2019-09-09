package org.cmdbuild.services.soap.operation;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.lookup.LookupImpl;
import org.cmdbuild.lookup.LookupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LookupLogicHelper {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final LookupService lookupService;

	public LookupLogicHelper(LookupService lookupService) {
		this.lookupService = checkNotNull(lookupService);
	}

	public long createLookup(org.cmdbuild.services.soap.types.Lookup lookup) {
		LookupImpl lookupDto = transform(lookup);
		return lookupService.createOrUpdateLookup(lookupDto).getId();
	}

	public boolean updateLookup(org.cmdbuild.services.soap.types.Lookup lookup) {
		LookupImpl lookupDto = transform(lookup);
		lookupService.createOrUpdateLookup(lookupDto);
		return true;
	}

	public boolean disableLookup(long id) {
//		logic.disableLookup(Long.valueOf(id));
		throw new UnsupportedOperationException("TODO");
//		return true;
	}

	public org.cmdbuild.services.soap.types.Lookup getLookupById(long id) {
		Lookup lookup = lookupService.getLookup(id);
		return transform(lookup, true);
	}

	public org.cmdbuild.services.soap.types.Lookup[] getLookupListByCode(String type, String code, boolean parentList) {
		return getLookupListByAttribute(type, (Lookup input) -> code.equals(input.getCode()), parentList);
	}

	public org.cmdbuild.services.soap.types.Lookup[] getLookupListByDescription(String type,
			String description, boolean parentList) {
		return getLookupListByAttribute(type, (Lookup input) -> (description == null) || description.equals(input.getDescription()), parentList);
	}

	private org.cmdbuild.services.soap.types.Lookup[] getLookupListByAttribute(String type,
			AttributeChecker attributeChecker, boolean parentList) {
//		LookupType lookupType = LookupType.newInstance() //
//				.withName(type) //
//				.build();
		Iterable<Lookup> lookupList = lookupService.getAllLookup(type);
		return from(lookupList) //
				.filter((Lookup input) -> attributeChecker.check(input)) //
				.transform((Lookup input) -> transform(input, parentList)) //
				.toArray(org.cmdbuild.services.soap.types.Lookup.class);
	}

	private LookupImpl transform(org.cmdbuild.services.soap.types.Lookup from) {
		return LookupImpl.builder() //
				.withTypeName(from.getType()) //
				.withCode(defaultIfEmpty(from.getCode(), EMPTY)) //
				.withId(from.getId()) //
				.withDescription(from.getDescription()) //
				.withNotes(from.getNotes()) //
				.withParentId(from.getParentId()) //
				.withIndex(from.getPosition()) //
				//				.withActiveStatus(true) //
				.build();
	}

	private org.cmdbuild.services.soap.types.Lookup transform(Lookup from, boolean parentList) {
		logger.debug("serializing lookup '{}'", from);
		org.cmdbuild.services.soap.types.Lookup to = new org.cmdbuild.services.soap.types.Lookup();
		to.setId(from.getId());
		to.setCode(from.getCode());
		to.setDescription(from.getDescription());
		to.setNotes(from.getNotes());
		to.setType(from.getType().getName());
		to.setPosition(from.getIndex());
		if (from.getParent() != null) {
			to.setParentId(from.getParentId());
		}
		if (parentList && from.getParent() != null) {
			to.setParent(transform(from.getParent(), true));
		}
		return to;
	}

	private static interface AttributeChecker {

		boolean check(Lookup input);

	}
}
