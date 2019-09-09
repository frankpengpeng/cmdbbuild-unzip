package org.cmdbuild.cql.legacy;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cmdbuild.cql.EcqlException;

import static org.cmdbuild.cql.CqlUtils.compileAndCheck;
import org.cmdbuild.cql.compiler.impl.CqlQueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;

@Component
public class CqlServiceImpl implements CqlService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final CqlProcessorService analyzerService;

	public CqlServiceImpl(CqlProcessorService analyzerService) {
		this.analyzerService = checkNotNull(analyzerService);
	}


	@Override
	public void compileAndAnalyze(String query, Map<String, Object> context, CqlProcessingCallback callback) {
		CqlQueryImpl compiled = null;
		try {
			compiled = compileAndCheck(query);
		} catch (Exception e) {
			throw new EcqlException(e, "eCQL compilation failed for expression = '%s'", abbreviate(query));
		}
		try {
			analyzerService.analyze(compiled, context, callback);
		} catch (Exception e) {
			throw new EcqlException(e, "eCQL processing failed for expression = '%s'", abbreviate(query));
		}
	}

}
