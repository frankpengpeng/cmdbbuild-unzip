package org.cmdbuild.task.reademail.mapper;

public class NullMapperEngine implements MapperEngine {

	private static final NullMapperEngine instance = new NullMapperEngine();

	public static NullMapperEngine getInstance() {
		return instance;
	}

	private NullMapperEngine() {
		// prevents instantiation
	}

	@Override
	public void accept(final MapperEngineVisitor visitor) {
		visitor.visit(this);
	}

}
