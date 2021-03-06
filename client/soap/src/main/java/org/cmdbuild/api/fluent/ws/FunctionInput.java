package org.cmdbuild.api.fluent.ws;

public final class FunctionInput extends EntryTypeAttributeImpl {

	protected FunctionInput(final String functionName, final String attributeName) {
		super(functionName, attributeName);
	}

	@Override
	public void accept(final AttrTypeVisitor visitor) {
		visitor.visit(this);
	}

	public String getFunctionName() {
		return entryTypeName;
	}

	public static FunctionInput functionInput(final String functionName, final String attributeName) {
		return new FunctionInput(functionName, attributeName);
	}

}
