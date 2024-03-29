package org.cmdbuild.dao.entrytype.attributetype;

public class TextAttributeType implements CardAttributeType<String> {

	@Override
	public void accept(CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.TEXT;
	}
}
