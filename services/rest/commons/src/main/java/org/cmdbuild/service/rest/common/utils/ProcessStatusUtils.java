package org.cmdbuild.service.rest.common.utils;

import static org.cmdbuild.service.rest.common.utils.ProcessStatus.ABORTED;
import static org.cmdbuild.service.rest.common.utils.ProcessStatus.COMPLETED;
import static org.cmdbuild.service.rest.common.utils.ProcessStatus.OPEN;
import static org.cmdbuild.service.rest.common.utils.ProcessStatus.SUSPENDED;

import java.util.Map;
import javax.annotation.Nullable;

import org.cmdbuild.lookup.Lookup;

import org.cmdbuild.utils.lang.CmMapUtils;

public class ProcessStatusUtils {

	public static final String STATE_CLOSED_ABORTED = "closed.aborted";
	public static final String STATE_CLOSED_COMPLETED = "closed.completed";
	public static final String STATE_OPEN_NOT_RUNNING_SUSPENDED = "open.not_running.suspended";
	public static final String STATE_OPEN_RUNNING = "open.running";
	public static final String STATE_CLOSED_TERMINATED = "closed.terminated";

	private static final Map<String, String> PROCESS_STATUS_CODE_MAP = CmMapUtils.<String, String>map(
			STATE_OPEN_RUNNING, OPEN,
			STATE_OPEN_NOT_RUNNING_SUSPENDED, SUSPENDED,
			STATE_CLOSED_COMPLETED, COMPLETED,
			STATE_CLOSED_ABORTED, ABORTED
	).immutable();

	public static ProcessStatus toProcessStatus(Lookup input) {
		return new ProcessStatus(input.getId(),
				// FIXME do it in a better way
				PROCESS_STATUS_CODE_MAP.get(input.getCode()),
				input.getDescription());
	}

	public static @Nullable
	ProcessStatus toProcessStatusOrNull(@Nullable Lookup input) {
		return input == null ? null : toProcessStatus(input);
	}

}
