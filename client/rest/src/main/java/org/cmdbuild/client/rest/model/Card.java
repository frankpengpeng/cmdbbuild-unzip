/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

public interface Card {

	@Nullable
	String getClassId();

	@Nullable
	String getCardId();

	default boolean hasCardId() {
		return !isBlank(getCardId());
	}

	default boolean hasClassId() {
		return !isBlank(getClassId());
	}

	Map<String, Object> getAttributes();

	default String getDescription() {
		return nullToEmpty((String) getAttributes().get("Description"));
	}
}
