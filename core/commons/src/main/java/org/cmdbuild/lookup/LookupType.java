package org.cmdbuild.lookup;

import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface LookupType {

	String getName();

	@Nullable
	String getParentOrNull();

	default boolean hasParent() {
		return isNotBlank(getParentOrNull());
	}
}
