/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.postgres;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import static java.lang.String.format;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeBashScript;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmNetUtils.isPortAvailable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.postgres.PostgresUtils.POSTGRES_SERVER_VERSION_DEFAULT;
import static org.cmdbuild.utils.postgres.PostgresUtils.getPostgresServerAvailablePort;
import static org.cmdbuild.utils.postgres.PostgresUtils.getPostgresServerBinaries;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.io.CmIoUtils.cmTmpDir;

public class PostgresServerHelper {

    private final static String PG_INIT_SCRIPT_CONTENT = readToString(PostgresServerHelper.class.getResourceAsStream("/org/cmdbuild/utils/postgres/pg_init.sh"));

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String version = POSTGRES_SERVER_VERSION_DEFAULT;
    private int port = getPostgresServerAvailablePort();
    private String adminPassword = randomId();
    private File installDirectory = new File(cmTmpDir(), format("postgres_%s", randomId(8)));

    public String getVersion() {
        return version;
    }

    public int getPort() {
        return port;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public File getInstallDirectory() {
        return installDirectory;
    }

    public PostgresServerHelper withPostgresVersion(String version) {
        this.version = checkNotBlank(version);
        return this;
    }

    public PostgresServerHelper withAdminPassword(String adminPassword) {
        this.adminPassword = checkNotBlank(adminPassword);
        return this;
    }

    public PostgresServerHelper withServerPort(int port) {
        this.port = port;
        return this;
    }

    public PostgresServerHelper withInstallDirectory(File dir) {
        this.installDirectory = checkNotNull(dir);
        return this;
    }

    public PostgresServerHelper installAndStartPostgres() {
        logger.info("install postgres version = {} to path = {} with port = {}", version, installDirectory.getAbsolutePath(), port);
        checkArgument(isPortAvailable(port), "invalid port = %s : port is not available", port);
        File binaries = getPostgresServerBinaries(version);
        logger.info("got binaries = {} {}", binaries.getAbsolutePath(), byteCountToDisplaySize(binaries.length()));
        installDirectory.mkdirs();
        checkArgument(installDirectory.isDirectory(), "invalid install directory = %s", installDirectory.getAbsolutePath());
        checkArgument(installDirectory.listFiles().length == 0, "install directory is not empty = %s", installDirectory.getAbsolutePath());
        executeBashScript(PG_INIT_SCRIPT_CONTENT, installDirectory.getAbsolutePath(), binaries.getAbsolutePath(), port, adminPassword);
        startPostgres();
        logger.debug("set admin password");
        executeBashScript(format("#!/bin/bash\n\nexport PGPASSFILE=\nexport PATH=\"%s\"/pgsql/bin:$PATH\npsql -p %s -U postgres -w -c \"ALTER USER postgres WITH PASSWORD '%s'\"", installDirectory.getAbsolutePath(), port, adminPassword));
        return this;
    }

    public PostgresServerHelper startPostgres() {
        logger.info("start postgres = {}", installDirectory.getAbsolutePath());
        executePgCtl("-l \"%s\"/log -w start", installDirectory.getAbsolutePath());
        return this;
    }

    public boolean isRunning() {
        return executePgCtl("status; true").matches("(?s).*server is running.*");
    }

    public PostgresServerHelper stopPostgres() {
        logger.info("stop postgres = {}", installDirectory.getAbsolutePath());
        executePgCtl("stop");
        return this;
    }

    private String executePgCtl(String other, Object... args) {
        return executeBashScript(format("#!/bin/bash\n\nexport PATH=\"%s\"/pgsql/bin:$PATH\npg_ctl -D \"%s\"/data %s", installDirectory.getAbsolutePath(), installDirectory.getAbsolutePath(), format(other, args)));
    }

    public PostgresServerHelper uninstallPostgres() {
        if (isRunning()) {
            stopPostgres();
        }
        logger.info("remove postgres server from path = {}", installDirectory.getAbsolutePath());
        deleteQuietly(installDirectory);
        checkArgument(!installDirectory.exists(), "unable to delete postgres from dir = %s", installDirectory.getAbsolutePath());
        return this;
    }

}
