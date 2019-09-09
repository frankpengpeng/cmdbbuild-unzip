package org.cmdbuild.dao.entrytype;

import javax.annotation.Nullable;

public interface DomainDefinition {

	@Nullable
	Long getOid();

	String getName();

	DomainMetadata getMetadata();

	Classe getSourceClass();

	Classe getTargetClass();
}
