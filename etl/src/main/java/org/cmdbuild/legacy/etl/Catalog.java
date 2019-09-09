package org.cmdbuild.legacy.etl;

import org.cmdbuild.legacy.etl.Type;

public interface Catalog {

	Iterable<Type> getTypes();

	<T extends Type> T getType(String name, Class<T> type);

}
