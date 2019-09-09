package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import java.util.Collection;

import java.util.Map;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public abstract class EntryTypeImpl implements EntryType {

	private final String name;
	private final long id;
	private final Map<String, Attribute> attributes;

	protected EntryTypeImpl(String name, Long oid, Iterable<? extends AttributeWithoutOwner> attributes) {
		this.name = checkNotBlank(name);
		this.id = oid;
		this.attributes = map(transformValues(checkNotNull(uniqueIndex(attributes, AttributeWithoutOwner::getName)), (AttributeWithoutOwner attr) -> (Attribute) AttributeImpl.copyOf(attr).withOwner(EntryTypeImpl.this).build())).immutable();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Collection<Attribute> getAllAttributes() {
		return attributes.values();
	}

	@Override
	public Map<String, Attribute> getAllAttributesAsMap() {
		return attributes;
	}

	@Override
	public boolean hasAttribute(String key) {
		return attributes.containsKey(key);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + (int) (this.id ^ (this.id >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EntryType)) {
			return false;
		}
		EntryType other = (EntryType) obj;
		if ((long) getId() != (long) other.getId()) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "EntryType{" + "name=" + name + ", id=" + id + '}';
	}

}
