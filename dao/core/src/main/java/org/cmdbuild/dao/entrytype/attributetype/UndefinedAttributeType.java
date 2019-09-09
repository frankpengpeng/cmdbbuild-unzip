package org.cmdbuild.dao.entrytype.attributetype;

public class UndefinedAttributeType implements CardAttributeType<Object> {

	private static final UndefinedAttributeType INSTANCE = new UndefinedAttributeType();

	public static UndefinedAttributeType undefined() {
		return INSTANCE;
	}

	private UndefinedAttributeType() {
	}

	@Override
	public void accept(CMAttributeTypeVisitor visitor) {
		throw new UnsupportedOperationException("undefined attribute type");
	}

	public static boolean isUndefined(CardAttributeType type) {
		return type == INSTANCE;
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.UNKNOWN;
	}
}
