package org.cmdbuild.legacy.etl;

import java.util.Map;

import org.cmdbuild.legacy.etl.ClassType;

public interface AttributeValueAdapter {

	Iterable<Map.Entry<String, Object>> toInternal(ClassType type,
			Iterable<? extends Map.Entry<String, ? extends Object>> values);

	Iterable<Map.Entry<String, Object>> toSynchronizer(ClassType type,
			Iterable<? extends Map.Entry<String, ? extends Object>> values);

}
