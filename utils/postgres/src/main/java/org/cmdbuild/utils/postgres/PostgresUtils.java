/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.postgres;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.transform;
import java.io.File;
import java.util.List;
import java.util.Set;
import static org.cmdbuild.utils.io.CmIoUtils.fetchFileWithCache;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmNetUtils.scanPortOffset;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.postgres.PostgresHelperConfigImpl.PostgresHelperBuilder;

public class PostgresUtils {

    private final static PgVersionsData PG_UTILS_CONFIG = fromJson(readToString(PostgresUtils.class.getResourceAsStream("/org/cmdbuild/utils/postgres/pg_versions.json")), PgVersionsData.class);

    public final static Set<String> POSTGRES_VERSIONS = set(PG_UTILS_CONFIG.pgVersions).immutable();
    public final static String POSTGRES_VERSION_AUTO = "auto",
            POSTGRES_VERSION_DEFAULT = getLast(POSTGRES_VERSIONS);

    public final static Set<String> POSTGRES_SERVER_VERSIONS = set(transform(PG_UTILS_CONFIG.pgServerVersions, s -> s.version)).immutable();

    public final static String POSTGRES_SERVER_VERSION_DEFAULT = getLast(POSTGRES_SERVER_VERSIONS);

    public static void checkBinaries() {
        POSTGRES_VERSIONS.stream().map((version) -> PostgresUtils.newHelper().withPostgresVersion(version).buildHelper()).forEach((helper) -> {
            helper.runCommand("pg_dump", "--version");
            helper.runCommand("pg_restore", "--version");
            helper.runCommand("psql", "--version");
        });
    }

    public static void checkDumpFile(File file) {
        newHelper().buildHelper().checkDumpFile(file);
    }

    public static boolean dumpContainsSchema(File dumpFile, String schema) {
        return newHelper().withSchema(schema).buildHelper().dumpContainsSchema(dumpFile);
    }

    public static List<String> getTablesInDump(File dumpFile, String schema) {
        return newHelper().withSchema(schema).buildHelper().getTablesInDump(dumpFile);
    }

    public static PostgresHelperBuilder newHelper() {
        return PostgresHelperConfigImpl.builder();
    }

    public static PostgresHelperBuilder newHelper(String host, int port, String username, String password) {
        return newHelper()
                .withUsername(username)
                .withPassword(password)
                .withHost(host)
                .withPort(port);
    }

    public static PostgresServerHelper serverHelper() {
        return new PostgresServerHelper();
    }

    public static File getPostgresServerBinaries(String version) {
        PgServerVersionData info = PG_UTILS_CONFIG.pgServerVersions.stream().filter(p -> equal(p.version, version)).collect(onlyElement("pg version not found = %s", version));
        return fetchFileWithCache(info.checksum, info.url);
    }

    public static int getPostgresServerAvailablePort() {
        return scanPortOffset(0, 5432) + 5432;
    }

    private static class PgVersionsData {

        private final List<String> pgVersions;
        private final List<PgServerVersionData> pgServerVersions;

        public PgVersionsData(
                @JsonProperty("pg_client_versions") List<String> pgVersions,
                @JsonProperty("pg_server_versions") List<PgServerVersionData> pgServerVersions) {
            this.pgVersions = ImmutableList.copyOf(pgVersions);
            this.pgServerVersions = ImmutableList.copyOf(pgServerVersions);
        }

    }

    private static class PgServerVersionData {

        private final String version, url, checksum;

        public PgServerVersionData(
                @JsonProperty("v") String version,
                @JsonProperty("url") String url,
                @JsonProperty("sha1hash") String checksum) {
            this.version = checkNotBlank(version);
            this.url = checkNotBlank(url);
            this.checksum = checkNotBlank(checksum);
        }

    }

}
