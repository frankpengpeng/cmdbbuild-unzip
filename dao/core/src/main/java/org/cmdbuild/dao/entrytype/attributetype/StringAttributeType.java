package org.cmdbuild.dao.entrytype.attributetype;

import static com.google.common.base.Preconditions.checkArgument;

public class StringAttributeType implements CardAttributeType<String> {

	public final int length;

	private StringAttributeType(int length) {
		this.length = length;
	}

	public StringAttributeType() {
		this.length = Integer.MAX_VALUE;
	}

	public int getLength() {
		return length;
	}

	public StringAttributeType(Integer length) {
		checkArgument(length != null && length > 0, "invalid length value = %s", length);
		this.length = length;
	}

	public boolean hasLength() {
		return length != Integer.MAX_VALUE;
	}

	@Override
	public void accept(CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "StringAttributeType{" + "length=" + length + '}';
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.STRING;
	}
}
