/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.services;

import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class SystemServiceStatusUtils {

	public static String serializeMinionStatus(MinionStatus status) {
		return status.name().replaceFirst("MS_", "").toLowerCase();
	}

	public static MinionStatus parseMinionStatus(String value) {
		return parseEnum("MS_" + checkNotBlank(value).toUpperCase().replaceFirst("MS_", ""), MinionStatus.class);
	}

}
