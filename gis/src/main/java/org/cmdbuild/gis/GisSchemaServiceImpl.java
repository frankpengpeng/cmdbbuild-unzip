package org.cmdbuild.gis;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.cmdbuild.dao.ConfigurableDataSource;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GisSchemaServiceImpl implements GisSchemaService {//TODO merge this in gis service/gis repo

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final JdbcTemplate jdbcTemplate;
	private final ConfigurableDataSource dataSource;

	private final Supplier<String> postgisVersion = Suppliers.memoize(this::doGetPostgisVersion);

	public GisSchemaServiceImpl(DaoService dao, ConfigurableDataSource dataSource) {
		this.jdbcTemplate = dao.getJdbcTemplate();
		this.dataSource = checkNotNull(dataSource);
	}

	@Override
	public String getPostgisVersion() {
		return postgisVersion.get();
	}

	@Override
	public void checkGisSchemaAndCreateIfMissing() {
		if (!gisSchemaExists()) {
			logger.info("gis schema not found; create gis schema");
			createGisSchema();
		}
		String version = getPostgisVersion();
		logger.info("postgis ready with version = {}", version);
	}

	@Override
	public boolean isGisSchemaOk() {
		try {
			doGetPostgisVersion();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private void createGisSchema() {
		try {
			String avaliablePostgisVersion = jdbcTemplate.queryForObject("select default_version from pg_available_extensions where name = 'postgis'", String.class);
			checkNotBlank(avaliablePostgisVersion, "CM: postgis not available; jou should install the POSTGis postgres database extension before enabling GIS");
			//TODO check postgis supported version 
			logger.info("create gis schema and enable postgis version {}", avaliablePostgisVersion);
			dataSource.doAsSuperuser(() -> {
				jdbcTemplate.execute("CREATE SCHEMA gis");
				jdbcTemplate.execute("CREATE EXTENSION postgis SCHEMA gis");
			});
		} catch (Exception ex) {
			throw new GisException(ex, "unable to prepare the 'gis' schema");
		}
	}

	private boolean gisSchemaExists() {
		return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT schema_name FROM information_schema.schemata WHERE schema_name = 'gis')", Boolean.class);
	}

	private String doGetPostgisVersion() {
		try {
			String pgExtensionVersion = jdbcTemplate.queryForObject("SELECT extversion FROM pg_extension WHERE extname = 'postgis'", String.class);
			String pgFunctionstgisVersion = jdbcTemplate.queryForObject("SELECT gis.postgis_lib_version()", String.class);
			checkNotBlank(pgFunctionstgisVersion, "postgis functions not found on schema gis");
			checkNotBlank(pgExtensionVersion, "postgis extension not found on this database");
			checkArgument(equal(pgFunctionstgisVersion, pgExtensionVersion), "postgis version mismatch: extension version = %s does not match function version = %s", pgExtensionVersion, pgFunctionstgisVersion);
			//TODO check postgis supported version 
			return pgExtensionVersion;
		} catch (Exception ex) {
			throw new GisException(ex, "error processing gis schema: invalid gis schema content");
		}
	}

}
