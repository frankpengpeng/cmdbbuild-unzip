package org.cmdbuild.dao.entrytype.attributetype;

import static java.lang.String.format;

public interface CMAttributeTypeVisitor {

	default void visit(BooleanAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(CharAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(DateAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(DateTimeAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(DecimalAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(DoubleAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(RegclassAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(ForeignKeyAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(IntegerAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(LongAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(IpAddressAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(LookupAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(ReferenceAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(StringArrayAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(JsonAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(StringAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(TextAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(TimeAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(ByteArrayAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

	default void visit(ReferenceArrayAttributeType attributeType) {
		throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType));
	}

}
