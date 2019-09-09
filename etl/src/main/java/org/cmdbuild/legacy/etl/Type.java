package org.cmdbuild.legacy.etl;

public interface Type {

	void accept(TypeVisitor visitor);

	String getName();

	Iterable<Attribute> getAttributes();

	Attribute getAttribute(String name);

}
