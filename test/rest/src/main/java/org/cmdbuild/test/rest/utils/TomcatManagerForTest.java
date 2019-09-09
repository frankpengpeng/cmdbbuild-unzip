/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.rest.utils;

import com.google.common.base.Joiner;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import javax.sql.DataSource;
import static java.lang.String.format;
import java.util.List;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.lang.StringUtils.trimToNull;
import org.cmdbuild.client.rest.RestClientImpl;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.SHARK_PASSWORD;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.SHARK_USERNAME;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import static org.cmdbuild.test.framework.TestContextUtils.createTestDatabase;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.tomcatmanager.LogFollow;
import org.cmdbuild.utils.tomcatmanager.TomcatConfig;
import org.cmdbuild.utils.tomcatmanager.TomcatManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class TomcatManagerForTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TomcatManager tomcatManager;
    private LogFollow sharkLog;
    private DatabaseCreator databaseCreator;

    public void initTomcatAndDb(String dbType) throws Exception {
        initTomcatAndDb(dbType, false);
    }

    public void initTomcatAndDb(String dbType, boolean includeShark) {
        try {
            doInitTomcatAndDb(dbType, includeShark);
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    private void doInitTomcatAndDb(String dbType, boolean includeShark) throws Exception {
        logger.info("\n\n\ninitTomcatAndDb BEGIN\n\n\n");
        databaseCreator = createTestDatabase((c) -> c.withSource(dbType).withUseSharkSchema(includeShark));

        String projectVersion = firstNonNull(trimToNull(System.getProperty("project.version")), "3-SNAPSHOT");//TODO workaround for null property, fix this

        List<String> wars = list(format("org.cmdbuild:cmdbuild:%s:war", projectVersion));
        if (includeShark) {
            wars.add(format("org.cmdbuild:cmdbuild-shark-server:%s:war AS shark", projectVersion));
        }

        TomcatConfig config = TomcatConfig.builder()
                .withProperties(getClass().getResourceAsStream("/tomcat-manager-cmdbuild-config.properties"))
                .withProperty("tomcat_deploy_artifacts", Joiner.on(",").join(wars))
                .withOverlay("database", databaseCreator.getConfig().getConfig())
                .withOverlay("logback", IOUtils.toString(getClass().getResourceAsStream("/tomcat_logback.xml")))
                .build();

        if (includeShark) {
            config = TomcatConfig.copyOf(config)
                    .withOverlay("sharkdb", buildSharkContextXml(databaseCreator.getDatabaseUrl(), SHARK_USERNAME, SHARK_PASSWORD))
                    .withOverlay("sharkconf", buildSharkConf(format("http://%s:%s/cmdbuild/", getHostname(), config.getHttpPort())))
                    .build();
        }

        tomcatManager = new TomcatManager(config);
        if (includeShark) {
            sharkLog = new LogFollow(new File(tomcatManager.getConfig().getInstallDir(), "logs/shark.log"));
            sharkLog.startFollowingLog();
        }
        tomcatManager.buildAndStart();
        logger.info("\n\n\ninitTomcatAndDb END\n\n\n");
    }

    public DatabaseCreator getDatabaseCreator() {
        return databaseCreator;
    }

    public void cleanupTomcatAndDb() {
        logger.info("\n\n\ncleanupClass BEGIN\n\n\n");
        tomcatManager.stopAndCleanup();
        tomcatManager = null;
        if (databaseCreator != null) {
            databaseCreator.dropDatabase();
            databaseCreator = null;
        }
        if (sharkLog != null) {
            sharkLog.stopFollowingLog();
            sharkLog.flushLogs();
            sharkLog = null;
        }
        logger.info("\n\n\ncleanupClass END\n\n\n");
    }

    public TomcatManager getTomcatManager() {
        return checkNotNull(tomcatManager, "tomcat manager is null (already shutdown)");
    }

    public String getHostname() {
        return firstNonNull(trimToNull(System.getProperty("cmdbuild.test.my.hostname")), "localhost");
    }

    public int getPort() {
        return getTomcatManager().getConfig().getHttpPort();
    }

    public String getBaseUrl() {
        return format("http://%s:%s/cmdbuild/", getHostname(), getPort());
    }

    public String getSharkUrl() {
        return format("http://%s:%s/shark", getHostname(), getPort());
    }

    private String buildSharkContextXml(String url, String user, String psw) {
        return readToString(getClass().getResourceAsStream("/shark_context_template.xml"))
                .replaceFirst("DBURL", url)
                .replaceFirst("DBUSERNAME", user)
                .replaceFirst("DBPASSWORD", psw);
    }

    private String buildSharkConf(String cmdbuildUrl) {
        return readToString(getClass().getResourceAsStream("/Shark_conf_template.conf"))
                .replaceFirst("CMDBUILDURL", cmdbuildUrl);

    }

    public DataSource getDataSource() {
        return databaseCreator.getCmdbuildDataSource();
    }

    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    public void reconfigureDatabase(DatabaseCreatorConfig dbConfig) {
        logger.info("reconfigure database to = {} {}/{}", dbConfig.getDatabaseUrl(), dbConfig.getCmdbuildUser(), dbConfig.getCmdbuildPassword());
        RestClientImpl.build(getBaseUrl()).doLoginWithAnyGroup("admin", "admin")//TODO admin account
                .system().reconfigureDatabase(dbConfig.getConfig());
    }

}
