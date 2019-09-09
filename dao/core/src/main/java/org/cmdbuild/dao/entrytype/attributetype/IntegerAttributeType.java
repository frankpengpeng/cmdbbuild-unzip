package org.cmdbuild.dao.entrytype.attributetype;

public class IntegerAttributeType implements CardAttributeType<Integer> {

	@Override
	public void accept(final CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.INTEGER;
	}

}
