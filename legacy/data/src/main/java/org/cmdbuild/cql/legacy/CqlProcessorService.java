package org.cmdbuild.cql.legacy;

import java.util.Map;

import org.cmdbuild.cql.compiler.impl.CqlQueryImpl;

public interface CqlProcessorService {

	void analyze(CqlQueryImpl q, Map<String, Object> vars, CqlProcessingCallback callback);

}
