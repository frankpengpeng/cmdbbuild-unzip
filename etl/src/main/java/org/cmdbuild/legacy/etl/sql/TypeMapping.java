package org.cmdbuild.legacy.etl.sql;

import org.cmdbuild.legacy.etl.ClassType;

public interface TypeMapping {

	ClassType getType();

	Iterable<AttributeMapping> getAttributeMappings();

}
