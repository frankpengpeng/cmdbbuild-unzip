/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import java.util.List;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.mail.util.ByteArrayDataSource;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.io.IOUtils.copyLarge;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.postgres.DumpService;
import org.cmdbuild.report.ReportService;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmNetUtils.getHostname;
import static org.cmdbuild.utils.io.CmPropertyUtils.serializeMapAsProperties;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.service.ConfigRepositoryFacade;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.common.log.LoggersConfigService;
import org.cmdbuild.config.BugreportConfiguration;
import org.cmdbuild.log.LogbackConfigServiceHelper;
import static org.cmdbuild.utils.date.CmDateUtils.dateTimeFileSuffix;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowSupplier;

@Component
public class BugReportServiceImpl implements BugReportService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BugreportConfiguration config;
    private final LoggersConfigService loggerService;
    private final ReportService reportService;
    private final DumpService dumpService;
    private final GlobalConfigService configService;

    public BugReportServiceImpl(BugreportConfiguration config, LoggersConfigService loggerService, ReportService reportService, DumpService dumpService, GlobalConfigService configService) {
        this.config = checkNotNull(config);
        this.loggerService = checkNotNull(loggerService);
        this.reportService = checkNotNull(reportService);
        this.dumpService = checkNotNull(dumpService);
        this.configService = checkNotNull(configService);
    }

    @Override
    public DataSource generateBugReport() {
        File zip = generateBugReportFile();
        byte[] zipData = toByteArray(zip);
        deleteQuietly(zip);
        String zipName = generateZipName();
        logger.info("return debug info zip {} {}", zipName, FileUtils.byteCountToDisplaySize(zipData.length));
        return new ByteArrayDataSource(zipData, ContentType.APPLICATION_OCTET_STREAM.getMimeType()) {
            {
                setName(zipName);
            }
        };
    }

    @Override
    public BugReportInfo sendBugReport(@Nullable String message) {
        File zip = generateBugReportFile(message);
        String zipName = generateZipName();
        try {
            logger.info("begin upload of bug report zip file {} {}", zipName, FileUtils.byteCountToDisplaySize(toIntExact(zip.length())));
            uploadBugreport(zip, zipName);//TODO get report server via config
            logger.info("completed upload of bug report zip file {} {}", zipName, FileUtils.byteCountToDisplaySize(toIntExact(zip.length())));
            return new DebugInfoImpl(zipName);
        } finally {
            deleteQuietly(zip);
        }
    }

    private File generateBugReportFile() {
        return generateBugReportFile(null);
    }

    private File generateBugReportFile(@Nullable String message) {
        try {

            logger.info("building debug info zip file");
            List<Pair<String, Supplier<InputStream>>> list = list();

            LogbackConfigServiceHelper.getInstance().getLogFiles().forEach(rethrowConsumer((file) -> {
                logger.debug("load log file = {}", file.getAbsolutePath());
                list.add(Pair.of(file.getName(), rethrowSupplier(() -> new FileInputStream(file))));
            }));

//        list.add(toPair(reportService.executeReportFromFile("system_status_log_report", ReportFormat.PDF))); TODO
//        list.add(toPair(reportService.executeReportFromFile("system_status_log_report", ReportFormat.CSV))); TODO
            list.add(Pair.of("system.conf", () -> new ByteArrayInputStream(serializeMapAsProperties(configService.getConfigAsMap()).getBytes())));//TODO improve config export format (see cli editconfig)

            if (isNotBlank(message)) {
                list.add(Pair.of("message.txt", () -> new ByteArrayInputStream(message.getBytes())));
            }

            File dump = tempFile();
            dumpService.dumpDatabaseToFile(dump);
            list.add(Pair.of("database.backup", rethrowSupplier(() -> new FileInputStream(dump))));

            File zip = tempFile();

            try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip))) {
                for (Pair<String, Supplier<InputStream>> record : list) {
                    logger.debug("add zip entry = {}", record.getLeft());
                    ZipEntry zipEntry = new ZipEntry(checkNotBlank(record.getLeft()));
                    out.putNextEntry(zipEntry);
                    try (InputStream in = record.getValue().get()) {
                        copyLarge(in, out);
                    }
                    out.closeEntry();
                }
            } finally {
                deleteQuietly(dump);
            }

            return zip;
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    private String generateZipName() {
        return format("cmdbuild_bugreport_%s_%s.zip", dateTimeFileSuffix(), getHostname().toLowerCase().replaceAll("[^a-z0-9]", ""));
    }

    private Pair<String, byte[]> toPair(DataHandler dataHandler) {
        return Pair.of(dataHandler.getName(), CmIoUtils.toByteArray(dataHandler));
    }

    private void uploadBugreport(File reportfile, String filename) {
        logger.debug("send bug report to url = {}", config.getBugreportEndpoint());
        checkNotNull(reportfile);
        checkArgument(reportfile.exists() && reportfile.length() > 0);
        CloseableHttpClient client = HttpClientBuilder.create().build();
        try {
            HttpEntity payload = MultipartEntityBuilder.create()
                    .addBinaryBody("file", reportfile, ContentType.APPLICATION_OCTET_STREAM, filename)
                    .build();
            HttpPost request = new HttpPost(config.getBugreportEndpoint());
            request.setEntity(payload);
            String response = IOUtils.toString(client.execute(request).getEntity().getContent());
            checkArgument(fromJson(response, JsonNode.class).get("success").asBoolean() == true, "bug report upload error");//TODO improve this
        } catch (IOException ex) {
            throw runtime(ex);
        } finally {
            try {
                client.close();
            } catch (IOException ex) {
            }
        }
    }
}
