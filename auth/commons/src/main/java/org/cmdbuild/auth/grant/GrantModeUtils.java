/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.auth.grant.GrantMode.GM_NONE;
import static org.cmdbuild.auth.grant.GrantMode.GM_READ;
import static org.cmdbuild.auth.grant.GrantMode.GM_WRITE;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class GrantModeUtils {

	public static GrantMode parseGrantMode(String value) {
		switch (checkNotBlank(value).toLowerCase()) {
			case "r":
				return GM_READ;
			case "w":
				return GM_WRITE;
			case "-":
				return GM_NONE;
			default:
				throw runtime("unable to parse grant mode value = %s", value);
		}
	}

	public static String serializeGrantMode(GrantMode value) {
		switch (checkNotNull(value)) {
			case GM_WRITE:
				return "w";
			case GM_READ:
				return "r";
			case GM_NONE:
				return "-";
			default:
				throw unsupported("unsupported grant mode = %s", value);
		}
	}

}
