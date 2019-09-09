package org.cmdbuild.legacy.etl;

import java.util.Map;

public interface Entry {

	Type getType();

	Iterable<Map.Entry<String, Object>> getValues();

	Object getValue(String name);

	Key getKey();

}
