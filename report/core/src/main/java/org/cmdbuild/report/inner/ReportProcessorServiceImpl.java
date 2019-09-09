/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import static java.lang.String.format;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import static java.util.stream.Collectors.toList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.activation.DataHandler;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import org.apache.commons.io.FilenameUtils;
import org.cmdbuild.common.localization.LanguageService;
import static org.cmdbuild.report.ReportConst.PARAM_IMAGE;
import static org.cmdbuild.report.ReportConst.PARAM_SUBREPORT;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.ReportException;
import org.cmdbuild.report.ReportFormat;
import static org.cmdbuild.report.inner.utils.ReportUtils.getImages;
import static org.cmdbuild.report.inner.utils.ReportUtils.getReportParameters;
import static org.cmdbuild.report.inner.utils.ReportUtils.getSubreports;
import static org.cmdbuild.report.inner.utils.ReportUtils.prepareDesignImagesForZipExport;
import static org.cmdbuild.report.inner.utils.ReportUtils.prepareDesignSubreportsForZipExport;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReportProcessorServiceImpl implements ReportProcessorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ReportHelper reportHelper;
    private final DataSource dataSource;
    private final LanguageService languageService;

    public ReportProcessorServiceImpl(ReportHelper reportHelper, DataSource dataSource, LanguageService languageService) {
        this.reportHelper = checkNotNull(reportHelper);
        this.dataSource = checkNotNull(dataSource);
        this.languageService = checkNotNull(languageService);
    }

    @Override
    public DataHandler executeReport(ReportData report, ReportFormat reportExtension, Map<String, Object> parameters) {
        try {
            return new DbReportProcessorHelper(report, reportExtension, parameters).executeReport();
        } catch (Exception e) {
            throw new ReportException(e, "error processing report = %s", report);
        }
    }

    private class DbReportProcessorHelper {

        private final ReportData reportData;
        private final ReportFormat reportExtension;
        private final Map<String, Object> params;

        public DbReportProcessorHelper(ReportData reportData, ReportFormat reportExtension, Map<String, Object> params) {
            this.reportData = checkNotNull(reportData);
            this.reportExtension = checkNotNull(reportExtension);
            this.params = map(checkNotNull(params)).immutable();
        }

        public DataHandler executeReport() {
            if (equal(reportExtension, ReportFormat.ZIP)) {
                return new DataHandler(exportReportTemplatesAsZip());
            } else {
                return reportHelper.exportReport(doExecuteReport(), getBaseFileName(), reportExtension);
            }
        }

        private JasperPrint doExecuteReport() {
            try {
                List<JasperReport> subReports = reportData.getRichReportsAsJasperReports();
                List<InputStream> images = reportData.getImages().stream().map((b) -> new ByteArrayInputStream(b)).collect(toList());
                JasperReport masterReport = reportData.getMasterReportAsJasperReport();

                checkNotNull(params, "must set parameters for report");

                Map<String, Object> userParams = getReportParameters(masterReport).stream().collect(toMap(ReportParameter::getName, (parameter) -> {
                    String key = parameter.getName();
                    Object rawValue = params.get(key);
                    Object value = parameter.parseValue(rawValue);
                    checkArgument(parameter.isOptional() || value != null, "missing report param value for key = %s", key);
                    return value;
                }));

                logger.trace("report params =\n\n{}\n", mapToLoggableString(userParams));

                Map<String, Object> resourcesParams = map();

                for (int k = 0; k < subReports.size(); k++) {
                    resourcesParams.put(PARAM_SUBREPORT + (k + 1), subReports.get(k));//subreports index begin with 1 (and not 0) for legacy reasons
                }
                for (int i = 0; i < images.size(); i++) {
                    resourcesParams.put(PARAM_IMAGE + i, images.get(i));
                }

                logger.trace("report resources =\n\n{}\n", mapToLoggableString(resourcesParams));

                Map<String, Object> paramsForReport = map(userParams).with(resourcesParams).with(
                        JRParameter.REPORT_LOCALE, languageService.getRequestLocale()
                );

                logger.debug("executing report = {}", reportData);
                Stopwatch stopwatch = Stopwatch.createStarted();
                JasperPrint jasperPrint;
                try (Connection connection = dataSource.getConnection()) {
                    jasperPrint = JasperFillManager.fillReport(masterReport, paramsForReport, connection);
                }

                logger.debug("executed report = {} in {} secs", reportData, stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000d);
                return jasperPrint;

            } catch (Exception exception) {
                throw new ReportException(exception, "error processing report = %s", reportData);
            }
        }

        public String getContentType() {
            return reportHelper.getContentTypeForReportFormat(reportExtension);
        }

        public String getBaseFileName() {
            return reportData.getCode().replaceAll(" ", "");
        }

        private javax.activation.DataSource exportReportTemplatesAsZip() {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(out)) {

                JasperReport masterReport = reportData.getMasterReportAsJasperReport();
                JasperDesign jasperDesign = jasperReportToJasperDesign(masterReport);

                List<String> imageNames = reportData.getImageNames();
                List<byte[]> images = reportData.getImages();

                List<JasperReport> subReports = reportData.getRichReportsAsJasperReports();

                List<JRDesignImage> designImages = getImages(jasperDesign);

                prepareDesignImagesForZipExport(designImages, imageNames);
                prepareDesignSubreportsForZipExport(getSubreports(jasperDesign), subReports);

                {
                    JasperReport masterReportForExport = JasperCompileManager.compileReport(jasperDesign);
                    zos.putNextEntry(new ZipEntry(format("%s.jrxml", masterReportForExport.getName())));
                    JRXmlWriter.writeReport(masterReportForExport, zos, "UTF-8");
                    zos.closeEntry();
                }

                Set<String> alreadyIncludedFiles = set();

                for (int i = 0; i < imageNames.size(); i++) {
                    byte[] imageData = images.get(i);
                    String imageFilename = imageNames.get(i);
                    if (alreadyIncludedFiles.add(imageFilename)) {
                        zos.putNextEntry(new ZipEntry(imageFilename));
                        zos.write(imageData);
                        zos.closeEntry();
                    } else {
                        logger.debug("image file = {} already included in zip file, skipping", imageFilename);
                    }
                }

                for (JasperReport subReport : subReports) {
                    String subreportFileName = format("%s.jrxml", subReport.getName());
                    if (alreadyIncludedFiles.add(subreportFileName)) {
                        zos.putNextEntry(new ZipEntry(subreportFileName));
                        JRXmlWriter.writeReport(subReport, zos, "UTF-8");
                        zos.closeEntry();
                    } else {

                    }
                }

            } catch (Exception ex) {
                throw new ReportException(ex, "error writing zip report for report = %s", reportData);
            }
            return newDataSource(out.toByteArray(), "application/zip", FilenameUtils.getBaseName(getBaseFileName()) + ".zip");
        }

    }

    private static JasperDesign jasperReportToJasperDesign(JasperReport masterReport) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JRXmlWriter.writeReport(masterReport, out, "UTF-8");
        byte[] data = out.toByteArray();
        JasperDesign jasperDesign = JRXmlLoader.load(new ByteArrayInputStream(data));
        return jasperDesign;
    }

}
