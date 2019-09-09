/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

public class AttachmentDescriptionModeUtils {

	public static String serializeAttachmentDescriptionMode(AttachmentDescriptionMode mode) {
		return mode.name().replaceFirst("ADM_", "").toLowerCase();
	}

	public static @Nullable
	AttachmentDescriptionMode parseAttachmentDescriptionMode(@Nullable String value) {
		return parseEnumOrNull(value, AttachmentDescriptionMode.class);
	}
}
