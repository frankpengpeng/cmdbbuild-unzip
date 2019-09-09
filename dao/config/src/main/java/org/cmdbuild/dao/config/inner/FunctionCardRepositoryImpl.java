/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import org.cmdbuild.dao.sql.utils.SqlFunction;
import org.cmdbuild.dao.sql.utils.SqlFunctionImpl;
import org.cmdbuild.debuginfo.BuildInfoService;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FunctionCardRepositoryImpl implements FunctionCardRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final BuildInfoService buildInfoService;
	private final JdbcTemplate jdbcTemplate;

	public FunctionCardRepositoryImpl(@Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate, BuildInfoService buildInfoService) {
		this.jdbcTemplate = checkNotNull(jdbcTemplate);
		this.buildInfoService = checkNotNull(buildInfoService);
	}

	@Override
	public Map<String, SqlFunction> getFunctions() {
		return jdbcTemplate.query("SELECT * FROM \"_Function\"", (r, i) -> SqlFunctionImpl.builder()
				.withHash(r.getString("Hash"))
				.withFunctionDefinition(r.getString("Content"))
				.withRequiredPatchVersion("unknown")
				.withSignature(r.getString("Code"))
				.build()).stream().collect(toMap(SqlFunction::getSignature, identity()));
	}

	@Override
	public void update(SqlFunction function) {
		if (getFunctions().containsKey(function.getSignature())) {
			checkArgument(1 == jdbcTemplate.update("UPDATE \"_Function\" SET \"Hash\" = ?, \"Revision\" = ?, \"Content\" = ?, \"BeginDate\" = now() WHERE \"Code\" = ?",
					function.getHash(), buildInfoService.getCommitInfoOrUnknownIfNotAvailable(), function.getFunctionDefinition(), function.getSignature()), "error updating function registry: no record modified");
		} else {
			jdbcTemplate.update("INSERT INTO \"_Function\" (\"Code\",\"Hash\",\"Revision\",\"Content\") VALUES (?,?,?,?)", function.getSignature(), function.getHash(), buildInfoService.getCommitInfoOrUnknownIfNotAvailable(), function.getFunctionDefinition());
		}

	}
}
