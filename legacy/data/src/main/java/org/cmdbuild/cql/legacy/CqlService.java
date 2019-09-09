package org.cmdbuild.cql.legacy;

import java.util.Map;

public interface CqlService {

	void compileAndAnalyze(String query, Map<String, Object> context, CqlProcessingCallback callback);

}
