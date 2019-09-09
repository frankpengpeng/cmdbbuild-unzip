package org.cmdbuild.workflow.shark;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.lookup.Lookup;
import org.enhydra.shark.api.common.SharkConstants;

import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.List;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.toList;
import org.cmdbuild.workflow.core.LookupHelper;
import org.cmdbuild.workflow.model.FlowStatus;
import org.springframework.stereotype.Component;
import org.cmdbuild.lookup.LookupRepository;

@Component
public class LookupHelperImpl implements LookupHelper {

	private static final String FLOW_STATUS_LOOKUP = "FlowStatus";

//	private static final LookupType FLOW_STATUS = LookupType.newInstance().withName(FLOW_STATUS_LOOKUP).build();
	private static final BiMap<String, FlowStatus> stateByFlowStatusCode;

	static {
		stateByFlowStatusCode = HashBiMap.create();
		stateByFlowStatusCode.put(SharkConstants.STATE_OPEN_RUNNING, FlowStatus.OPEN);
		stateByFlowStatusCode.put(SharkConstants.STATE_OPEN_NOT_RUNNING_SUSPENDED, FlowStatus.SUSPENDED);
		stateByFlowStatusCode.put(SharkConstants.STATE_CLOSED_COMPLETED, FlowStatus.COMPLETED);
		stateByFlowStatusCode.put(SharkConstants.STATE_CLOSED_TERMINATED, FlowStatus.TERMINATED);
		stateByFlowStatusCode.put(SharkConstants.STATE_CLOSED_ABORTED, FlowStatus.ABORTED);
	}

	private final LookupRepository lookupStore;

	public LookupHelperImpl(LookupRepository lookupStore) {
		this.lookupStore = checkNotNull(lookupStore);
	}

	@Override
	public FlowStatus stateForLookupCode(String code) {
		if (code == null) {
			// TODO why not UNSUPPORTED?
			return null;
		}
		return firstNonNull(stateByFlowStatusCode.get(code), FlowStatus.UNSUPPORTED);
	}

	@Override
	public Optional<Lookup> lookupForState(FlowStatus state) {
		String code = stateByFlowStatusCode.inverse().get(state);
		return flowStatusWithCode(code);
	}

	@Override
	public Optional<Lookup> flowStatusWithCode(String code) {
		return Optional.fromNullable(allLookups().stream().filter((lookup) -> equal(code, lookup.getCode())).findAny().orElse(null));
	}

	@Override
	public FlowStatus stateForLookupId(Long id) {
		return allLookups().stream().filter((l) -> equal(l.getId(), id)).collect(toOptional()).map((l) -> firstNonNull(stateForLookupCode(l.getCode()), FlowStatus.UNSUPPORTED)).orElse(FlowStatus.UNSUPPORTED);
	}

	@Override
	public List<Lookup> allLookups() {
		return toList(lookupStore.getAllByType(FLOW_STATUS_LOOKUP));
	}

}
