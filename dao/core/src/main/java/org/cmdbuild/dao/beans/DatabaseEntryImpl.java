package org.cmdbuild.dao.beans;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.FluentIterable.from;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.driver.PostgresService;

public abstract class DatabaseEntryImpl implements DatabaseRecord, DatabaseEntryDefinition {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	final PostgresService driver;

	final EntryType type;
	final Map<String, Object> values;

	Long id;
	String user;
	DateTime beginDate;
	DateTime endDate;

	protected DatabaseEntryImpl(PostgresService driver, EntryType type, Long id) {
		this.driver = driver;
		this.type = type;
		this.values = Maps.newHashMap();
		this.id = id;
	}

	@Override
	public EntryType getType() {
		return type;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public DatabaseEntryImpl setUser(String user) {
		this.user = user;
		return this;
	}

	@Override
	public String getUser() {
		return user;
	}

	public void setBeginDate(DateTime beginDate) {
		this.beginDate = beginDate;
	}

	@Override
	public DateTime getBeginDate() {
		return beginDate;
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}

	@Override
	public DateTime getEndDate() {
		return endDate;
	}

	@Override
	public Object get(String key) {
		if (!values.containsKey(key)) {
			checkArgument(isNew() || type.hasAttribute(key), "attribute for key = %s does not exist in card = %s", key, this);
			return null;
		}
		return values.get(key);
	}

	private boolean isNew() {
		return (id == null);
	}

	@Override
	public Iterable<Map.Entry<String, Object>> getAttributeValues() {
		return from(getRawValues()) //
				.filter((Entry<String, Object> input) -> {
					String name = input.getKey();
					Attribute attribute = type.getAttribute(name);
					return !attribute.hasNotServiceListPermission();
				});
	}

	@Override
	public Iterable<Map.Entry<String, Object>> getRawValues() {
		return values.entrySet();
	}

	public void setOnly(String key, Object value) {
		if (type.hasAttribute(key)) {
			values.put(key, toNative(key, value));
		} else {
			logger.warn("attribute not found for key = {} in card type = {}, skipping attribute", key, type);
		}
	}

	private Object toNative(String key, Object value) {
		try {
			return rawToSystem(type.getAttribute(key).getType(), value);
		} catch (Exception ex) {
			throw runtime(ex, "error setting db entry attr for key = %s value = %s entry = %s", key, abbreviate(value), this);
		}
	}

	protected void saveOnly() {
		id = driver.create(this);
	}

	protected void delete() {
		driver.delete(this);
	}

	protected void updateOnly() {
		driver.update(this);
	}

}
