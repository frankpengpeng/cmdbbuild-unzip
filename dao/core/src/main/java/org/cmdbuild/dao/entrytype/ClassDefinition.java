package org.cmdbuild.dao.entrytype;

import javax.annotation.Nullable;

public interface ClassDefinition {

	@Nullable
	Long getOid();

	String getName();

	@Nullable
	String getParentOrNull();

	ClassMetadata getMetadata();
}
