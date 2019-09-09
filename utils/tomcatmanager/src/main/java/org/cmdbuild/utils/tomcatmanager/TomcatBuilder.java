/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.tomcatmanager;

import com.google.common.base.Charsets;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import static java.lang.String.format;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.copyDirectory;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import static org.cmdbuild.utils.io.CmIoUtils.fetchFileWithCache;
import static org.cmdbuild.utils.io.CmZipUtils.unzipToDir;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import static org.cmdbuild.utils.maven.MavenUtils.getFileByArtifactId;
import static org.cmdbuild.utils.tomcatmanager.TomcatManagerUtils.execSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.maven.MavenUtils.cleanupFileObtainedByArtifactId;

public class TomcatBuilder {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TomcatConfig tomcatConfig;

    public TomcatBuilder(TomcatConfig tomcatConfig) {
        checkNotNull(tomcatConfig);
        this.tomcatConfig = tomcatConfig;
    }

    public void buildTomcat() {
        try {
            checkArgument(!tomcatConfig.getInstallDir().exists() || tomcatConfig.getInstallDir().list().length == 0, "install dir must be empty");
            logger.info("buildTomcat BEGIN");
            unpackToDir();
            unpackPostgres();
            prepareTomcatConfig();
            addExtLibs();
            deployWars();
            applyConfigOverlay();
            runCustomScript();
            logger.info("buildTomcat END");
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public void cleanup() {
        if (containsTomcat(tomcatConfig.getInstallDir())) {
            FileUtils.deleteQuietly(tomcatConfig.getInstallDir());
        }
        if (tomcatConfig.getInstallDir().exists()) {
            logger.warn("unable to clean properly tomcat install dir = {}", tomcatConfig.getInstallDir().getAbsolutePath());
        }
    }

    private static boolean containsTomcat(File dir) {
        return new File(dir, "bin/startup.sh").exists();
    }

    private void unpackToDir() throws IOException, ZipException, InterruptedException {
        String tomcatArtifact = tomcatConfig.getProperty("tomcat_bin_artifact_id");
        logger.info("unpacking tomcat binary distribution = {} to dir = {}", tomcatArtifact, tomcatConfig.getInstallDir().getAbsolutePath());
        File tempZipFile = getFileByArtifactId(tomcatArtifact);
        try {
            ZipFile zipFile = new ZipFile(tempZipFile);
            FileHeader fileHeader = Iterables.find(zipFile.getFileHeaders(), (FileHeader f) -> f.getFileName().matches("apache-tomcat-[0-9.]+/?"));
            File tempUnzipDir = new File(tomcatConfig.getInstallDir().getParentFile(), UUID.randomUUID().toString());
            tempUnzipDir.deleteOnExit();
            zipFile.extractAll(tempUnzipDir.getAbsolutePath());
            tomcatConfig.getInstallDir().delete();
            FileUtils.moveDirectory(new File(tempUnzipDir, fileHeader.getFileName()), tomcatConfig.getInstallDir());
            FileUtils.deleteQuietly(tempUnzipDir);
        } finally {
            cleanupFileObtainedByArtifactId(tempZipFile);
        }
        checkArgument(containsTomcat(tomcatConfig.getInstallDir()));

        String startupDebugScript = IOUtils.toString(getClass().getResourceAsStream("/startup_debug.sh"));
        startupDebugScript = startupDebugScript.replaceAll("CMDBUILD_DEBUG_PORT", Integer.toString(tomcatConfig.getDebugPort()));
        FileUtils.writeStringToFile(new File(tomcatConfig.getInstallDir(), "bin/startup_debug.sh"), startupDebugScript);

        checkArgument(execSafe(tomcatConfig.getInstallDir(), "/bin/bash", "-c", "chmod +x bin/*.sh") == 0);
        logger.info("successfully unpacked tomcat binary distribution to dir = {}", tomcatConfig.getInstallDir().getAbsolutePath());
    }

    private void unpackPostgres() throws IOException, ZipException, InterruptedException, URISyntaxException, ArchiveException {
        if (toBooleanOrDefault(tomcatConfig.getProperty("include_embedded_postgres"), false)) {
            String pgVersion = checkNotBlank(tomcatConfig.getProperty("postgres.version")),
                    pgUrl = checkNotBlank(tomcatConfig.getProperty(format("postgres.version.%s.url", pgVersion))),
                    pgChecksum = trim(checkNotBlank(tomcatConfig.getProperty(format("postgres.version.%s.checksum", pgVersion))));
            logger.info("include postgres version = {}", pgVersion);
            File file = fetchFileWithCache(pgChecksum, pgUrl);
            File targetDir = new File(tomcatConfig.getInstallDir(), "postgres");
            executeProcess("/bin/tar", "-C", targetDir.getAbsolutePath(), "-xf", file.getAbsolutePath());
            //TODO init postgres db, user, config
        }
    }

    private void prepareTomcatConfig() throws IOException, InterruptedException {
        logger.info("configure tomcat ports, http port = {}, shutdown port = {}", tomcatConfig.getHttpPort(), tomcatConfig.getShutodownPort());
        checkArgument(execSafe(tomcatConfig.getInstallDir(), "/bin/bash", "-c",
                "sed -i -e 's/port=\"8005\"/port=\"" + tomcatConfig.getShutodownPort() + "\"/g' -e 's/port=\"8080\"/port=\"" + tomcatConfig.getHttpPort() + "\"/g' -e 's/[<]Connector[^>]*AJP[^>]*[>]/<!-- & -->/' ./conf/server.xml") == 0);
        try (Writer writer = new FileWriter(new File(tomcatConfig.getInstallDir(), "bin/setenv.sh"), true)) {
            logger.info("set catalina pid = {}", tomcatConfig.getCatalinaPidFile().getAbsolutePath());
            writer.write("\n\nCATALINA_PID=\"" + tomcatConfig.getCatalinaPidFile().getAbsolutePath() + "\"\n\n");
        }
    }

    public void deployWar(String warArtifactAndName) {
        try {
            logger.info("deploy war artifact = {} to tomcat = {}", warArtifactAndName, tomcatConfig.getInstallDir().getAbsolutePath());
            String warArtifact, webappName;
            Matcher matcher = Pattern.compile("^(.*) +AS +(.*)$").matcher(warArtifactAndName);
            if (matcher.find()) {
                warArtifact = trimAndCheckNotBlank(matcher.group(1));
                webappName = trimAndCheckNotBlank(matcher.group(2));
            } else {
                warArtifact = trimAndCheckNotBlank(warArtifactAndName);
                webappName = null;//set from file later
            }
            boolean shouldCleanup;
            File file;
            if (warArtifact.matches(".+[.].ar$") || new File(warArtifact).exists()) {
                file = new File(warArtifact);
                shouldCleanup = false;
            } else {
                file = getFileByArtifactId(warArtifact);
                shouldCleanup = true;
            }
            if (webappName == null) {
                webappName = trimAndCheckNotBlank(file.getName().replaceFirst("(-[0-9].*)?(.war)?$", ""));
            }
            try {
                File targetDir = new File(tomcatConfig.getInstallDir(), "webapps/" + webappName);
                if (file.isDirectory()) {
                    copyDirectory(file, targetDir);
                } else {
//				checkArgument(file.getName().endsWith(".war"), "invalid war file = %s", file); TODO validate war file
                    unzipToDir(file, targetDir);
                }
                checkArgument(new File(targetDir, "WEB-INF").exists());
                logger.info("successfully deployed war artifact = {} to dir = {}", warArtifactAndName, targetDir);
            } finally {
                if (shouldCleanup) {
                    cleanupFileObtainedByArtifactId(file);
                }
            }
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    private void deployWars() {
        for (String warArtifact : tomcatConfig.getPropertyAsList("tomcat_deploy_artifacts")) {
            deployWar(warArtifact);
        }
    }

    private void addExtLibs() throws IOException {
        for (String libArtifact : tomcatConfig.getPropertyAsList("tomcat_ext_libs")) {
            File dir = new File(tomcatConfig.getInstallDir(), "lib/");
            logger.info("add ext lib = {}", libArtifact);
            File file = getFileByArtifactId(libArtifact);
            try {
                logger.info("add ext lib file = {}", new File(dir, file.getName()));
                FileUtils.copyFileToDirectory(file, dir, false);
            } finally {
                cleanupFileObtainedByArtifactId(file);
            }
        }
    }

    private void applyConfigOverlay() throws IOException {
        for (String key : tomcatConfig.getPropertyAsList("tomcat_config_overlay")) {
            String fileName = tomcatConfig.getProperty("tomcat_config_overlay." + key + ".file"),
                    fileContent = tomcatConfig.getProperty("tomcat_config_overlay." + key + ".content");
            File file = new File(tomcatConfig.getInstallDir(), fileName);
            logger.info("adding config overlay for key = {} file = {} content = \n{}", key, file.getAbsolutePath(), fileContent);
            file.getParentFile().mkdirs();
            FileUtils.writeStringToFile(file, fileContent, Charsets.UTF_8);
        }
    }

    private void runCustomScript() {
        String customScript = tomcatConfig.getProperty("tomcat_install_final_custom_script");
        if (!isBlank(customScript)) {
            execSafe(tomcatConfig.getInstallDir(), "/bin/bash", "-c", customScript);
        }
    }

}
