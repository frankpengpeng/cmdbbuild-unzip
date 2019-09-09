package org.cmdbuild.shark.toolagent;

import static java.lang.String.format;

import java.util.Arrays;

import org.enhydra.shark.api.client.wfmc.wapi.WMSessionHandle;
import org.enhydra.shark.api.internal.toolagent.AppParameter;
import org.enhydra.shark.api.internal.working.CallbackUtilities;

public class ParametersLogger {

	public final static String LOGGER_NAME = "CMDBuild";

	public static ParametersLogger from(CallbackUtilities cus, WMSessionHandle sessionHandle) {
		return new ParametersLogger(cus, sessionHandle);
	}

	private final CallbackUtilities callbackUtilities;
	private final WMSessionHandle sessionHandle;

	public ParametersLogger(CallbackUtilities callbackUtilities, WMSessionHandle sessionHandle) {
		this.callbackUtilities = callbackUtilities;
		this.sessionHandle = sessionHandle;
	}

	public void beforeInvocation(AppParameter[] parameters) {
		callbackUtilities.debug(sessionHandle, LOGGER_NAME, "parameters before invocation...");
		dumpParameters(parameters);
	}

	public void afterInvocation(AppParameter[] parameters) {
		callbackUtilities.debug(sessionHandle, LOGGER_NAME, "parameters after invocation...");
		dumpParameters(parameters);
	}

	private void dumpParameters(AppParameter[] parameters) {
		for (AppParameter parameter : parameters) {
			callbackUtilities.debug(sessionHandle, LOGGER_NAME, formatParameter(parameter));
		}
	}

	private String formatParameter(AppParameter parameter) {
		Object value;
		if (parameter.the_class.isArray()) {
			value = Arrays.toString((Object[]) parameter.the_value);
		} else {
			value = parameter.the_value;
		}
		return format("parameter '%s' (%s) = '%s'", parameter.the_formal_name, parameter.the_class, value);
	}

}
