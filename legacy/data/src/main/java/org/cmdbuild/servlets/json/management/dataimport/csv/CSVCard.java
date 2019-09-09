/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.servlets.json.management.dataimport.csv;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.cmdbuild.common.Constants;
import org.cmdbuild.dao.beans.CardDefinition;
import org.cmdbuild.dao.beans.ForwardingCardDefinition;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.joda.time.DateTime;
import org.cmdbuild.dao.beans.Card;

public class CsvCard extends ForwardingCardDefinition implements Card {

	final Classe type;
	String user;
	final Map<String, Object> values;

	public CsvCard(final Classe type, final CardDefinition delegate) {
		super(delegate);
		this.type = type;
		this.values = Maps.newHashMap();
	}

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public CardDefinition setUser(final String user) {
		this.user = user;
		return super.setUser(user);
	}

	@Override
	public DateTime getBeginDate() {
		return null;
	}

	@Override
	public DateTime getEndDate() {
		return null;
	}

	@Override
	public Iterable<Map.Entry<String, Object>> getRawValues() {
		return values.entrySet();
	}

	@Override
	public CardDefinition set(final Iterable<? extends Map.Entry<String, ? extends Object>> keysAndValues) {
		for (final Map.Entry<String, ? extends Object> element : keysAndValues) {
			values.put(element.getKey(), element.getValue());
		}
		return super.set(keysAndValues);
	}

	@Override
	public Object get(final String key) {
		return values.get(key);
	}

	@Override
	public CardDefinition set(final String key, final Object value) {
		values.put(key, value);
		return super.set(key, value);
	}

	@Override
	public <T> T get(final String key, final Class<? extends T> requiredType) {
		return requiredType.cast(get(key));
	}

	@Override
	public <T> T get(final String key, final Class<? extends T> requiredType, final T defaultValue) {
		return ObjectUtils.defaultIfNull(get(key, requiredType), defaultValue);
	}

	@Override
	public Iterable<Map.Entry<String, Object>> getAttributeValues() {
		return //
		FluentIterable.from(getRawValues()) //
		.filter(new Predicate<Map.Entry<String, Object>>() {
			@Override
			public boolean apply(final Map.Entry<String, Object> input) {
				final String name = input.getKey();
				final Attribute attribute = type.getAttributeOrNull(name);
				return !attribute.hasNotServiceListPermission();
			}
		});
	}

	@Override
	public Classe getType() {
		return type;
	}

	@Override
	public String getCode() {
		return (String) values.get(Constants.CODE_ATTRIBUTE);
	}

	@Override
	public CardDefinition setCode(final Object value) {
		values.put(Constants.CODE_ATTRIBUTE, value);
		return super.setCode(value);
	}

	@Override
	public String getDescription() {
		return (String) values.get(Constants.DESCRIPTION_ATTRIBUTE);
	}

	@Override
	public CardDefinition setDescription(final Object value) {
		values.put(Constants.DESCRIPTION_ATTRIBUTE, value);
		return super.setDescription(value);
	}

	@Override
	public Long getCurrentId() {
		return null;
	}

}
