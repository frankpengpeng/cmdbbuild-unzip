package org.cmdbuild.logic.data.access.filter.model;

public interface ElementVisitor {

	void visit(All element);

	void visit(Attr element);

	void visit(Not element);

	void visit(OneOf element);

}
