package org.cmdbuild.legacy.etl.sql;

public interface TableOrViewMapping {

	String getName();

	Iterable<TypeMapping> getTypeMappings();

}
