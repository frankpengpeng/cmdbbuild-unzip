package org.cmdbuild.dao.postgres;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;

/**
 * A simple DTO that contains the value and type
 */
public class AttributeValueType {

	/**
	 * unquoted attribute name
	 */
	private final String name;
	private final Object value;
	private final CardAttributeType<?> type;

	AttributeValueType(String name, @Nullable Object value, CardAttributeType<?> type) {
		this.name = checkNotBlank(name);
		this.value = value;
		this.type = checkNotNull(type);
	}

	public String getName() {
		return name;
	}

	@Nullable
	public Object getValue() {
		return value;
	}

	public CardAttributeType<?> getType() {
		return type;
	}

	@Override
	public String toString() {
		return "AttributeValueType{" + "name=" + name + ", value=" + value + ", type=" + type + '}';
	}

}
