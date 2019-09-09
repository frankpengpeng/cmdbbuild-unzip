package org.cmdbuild.task.reademail.mapper;

public interface MapperEngineVisitor {

	void visit(KeyValueMapperEngine mapper);

	void visit(NullMapperEngine mapper);

}
