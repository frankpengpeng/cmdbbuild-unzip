package org.cmdbuild.service.rest.v3.helpers;

import org.cmdbuild.service.rest.common.helpers.ProcessStatusHelper;
import static com.google.common.collect.FluentIterable.from;

import org.cmdbuild.service.rest.common.utils.ProcessStatusUtils;
import org.cmdbuild.service.rest.common.utils.ProcessStatus;
import org.cmdbuild.workflow.core.LookupHelper;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import org.springframework.stereotype.Component;

@Component
public class ProcessStatusHelperImpl implements ProcessStatusHelper {

	private static final Predicate<ProcessStatus> VALID_STATUSES = (ProcessStatus input) -> (input.getValue() != null);

	private final LookupHelper lookupHelper;

	public ProcessStatusHelperImpl(LookupHelper lookupHelper) {
		this.lookupHelper = checkNotNull(lookupHelper);
	}

	@Override
	public Iterable<ProcessStatus> allValues() {
		return from(lookupHelper.allLookups()).transform(ProcessStatusUtils::toProcessStatus).filter(VALID_STATUSES);
	}

}
