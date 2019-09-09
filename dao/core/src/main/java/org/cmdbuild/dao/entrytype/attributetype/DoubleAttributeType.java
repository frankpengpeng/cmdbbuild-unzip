package org.cmdbuild.dao.entrytype.attributetype;

public class DoubleAttributeType implements CardAttributeType<Double> {

	@Override
	public void accept(final CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

//	@Override
//	protected Double convertNotNullValue(final Object value) {
//	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.DOUBLE;
	}


}
