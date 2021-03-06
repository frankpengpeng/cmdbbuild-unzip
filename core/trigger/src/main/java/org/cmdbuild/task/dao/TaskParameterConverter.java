package org.cmdbuild.task.dao;

import java.util.Map;

import org.cmdbuild.data.store.dao.BaseStorableConverter;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.beans.Card;

@Component
public class TaskParameterConverter extends BaseStorableConverter<TaskParameter> {

	private static final String CLASSNAME = "_TaskParameter";

	public static final String OWNER = "Owner";
	private static final String KEY = "Key";
	private static final String VALUE = "Value";

	@Override
	public String getClassName() {
		return CLASSNAME;
	}

	@Override
	public TaskParameter convert(final Card card) {
		return TaskParameter.newInstance() //
				.withId(card.getId()) //
				.withOwner(card.get(OWNER, Integer.class).longValue()) //
				.withKey(card.get(KEY, String.class)) //
				.withValue(card.get(VALUE, String.class)) //
				.build();
	}

	@Override
	public Map<String, Object> getValues(final TaskParameter storable) {
		final Map<String, Object> values = Maps.newHashMap();
		values.put(OWNER, storable.getOwner());
		values.put(KEY, storable.getKey());
		values.put(VALUE, storable.getValue());
		return values;
	}

}
