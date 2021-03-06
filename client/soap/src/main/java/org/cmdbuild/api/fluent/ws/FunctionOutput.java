package org.cmdbuild.api.fluent.ws;

public final class FunctionOutput extends EntryTypeAttributeImpl {

	protected FunctionOutput(final String functionName, final String attributeName) {
		super(functionName, attributeName);
	}

	@Override
	public void accept(final AttrTypeVisitor visitor) {
		visitor.visit(this);
	}

	public String getFunctionName() {
		return entryTypeName;
	}

	public static FunctionOutput functionOutput(final String functionName, final String attributeName) {
		return new FunctionOutput(functionName, attributeName);
	}

}
