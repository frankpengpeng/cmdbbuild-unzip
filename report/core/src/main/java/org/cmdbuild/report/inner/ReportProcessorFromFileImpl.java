package org.cmdbuild.report.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import static java.lang.String.format;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;

import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.report.ReportException;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportProcessor;
import static org.cmdbuild.report.inner.utils.ReportUtils.getBands;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportProcessorFromFileImpl implements ReportProcessor {

    private static final Pattern PARAM_PATTERN = Pattern.compile("([^\\?]+)?(\\?)?");

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final DataSource dataSource;
    private final CoreConfiguration configuration;

    protected JasperPrint jasperPrint;

    private final ReportHelper reportHelper;
    protected final DaoService dao;
    private final Map<String, Object> reportParams;
    private final File reportDir;
    private final ReportFormat extension;
    private final JasperDesign jasperDesign;
    private final String basename;

    public ReportProcessorFromFileImpl(ReportHelper reportHelper, DataSource dataSource, CoreConfiguration configuration, DaoService dao, File reportDir, Map<String, Object> params, ReportFormat extension, JasperDesign jasperDesign, String basename) {
        this.dataSource = checkNotNull(dataSource);
        this.configuration = checkNotNull(configuration);
        this.dao = dao;
        this.reportParams = map(params);
        this.reportDir = checkNotNull(reportDir);
        this.reportHelper = checkNotNull(reportHelper);
        this.extension = checkNotNull(extension);
        this.jasperDesign = checkNotNull(jasperDesign);
        this.basename = checkNotBlank(basename);

        updateImagesPath();
//        updateSubreportsPath();
    }

    public JasperDesign getJasperDesign() {
        return jasperDesign;
    }

    @Override
    public ReportFormat getReportExtension() {
        return extension;
    }

    @Override
    public String getContentType() {
        return reportHelper.getContentTypeForReportFormat(extension);
    }

    public boolean isReportFilled() {
        return jasperPrint != null;
    }

    public JasperPrint getJasperPrint() {
        return jasperPrint;
    }

    @Override
    public javax.activation.DataSource executeReport() {
        fillReport();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JRExporter exporter = null;

        switch (getReportExtension()) {
            case PDF:
                exporter = new JRPdfExporter();
                break;

            case CSV:
                exporter = new JRCsvExporter();
                exporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, ";");
                break;

            case ODT:
                exporter = new JROdtExporter();
                break;

            case RTF:
                exporter = new JRRtfExporter();
                break;
        }

        if (exporter != null) {
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            try {
                exporter.exportReport();
            } catch (Exception ex) {
                throw new ReportException(ex, "error printing report");
            }
        }
        return buildReportDataSource(out.toByteArray());
    }

    protected javax.activation.DataSource buildReportDataSource(byte[] data) {
        return newDataSource(data, getContentType(), format("%s.%s", basename, getReportExtension().name().toLowerCase()));
    }

    protected void fillReport() {
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(getJasperDesign());
            doFillReport(jasperReport, reportParams);
        } catch (JRException ex) {
            throw new ReportException(ex, "error filling jasper report");
        }
    }

    protected void doFillReport(JasperReport report, Map<String, Object> paramsForReport) {
        paramsForReport = map(paramsForReport).with(JRParameter.REPORT_LOCALE, configuration.getDefaultLocale());

        logger.debug("executing report = {}", report);
        Stopwatch stopwatch = Stopwatch.createStarted();

        try (Connection connection = dataSource.getConnection()) {
            jasperPrint = JasperFillManager.fillReport(report, paramsForReport, connection);
        } catch (Exception exception) {
            throw new ReportException(exception, "error processing jasper report = %s", report);
        }

        logger.debug("executed report = {} in {} secs", report, stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000d);
    }

    protected final File getReportDir() {
        return reportDir;
    }

    protected void addFillParameter(final String key, final Object value) throws JRException {
        reportParams.put(key, value);
    }

    /**
     * Update images path only in title band; images are supposed to be in the
     * same folder of master report
     */
    private void updateImagesPath() {
        Object obj = null;
        final JRBand title = getJasperDesign().getTitle();
        final List<JRChild> f = title.getChildren();
        final Iterator<JRChild> it = f.iterator();

        while (it.hasNext()) {
            obj = it.next();
            if (obj instanceof JRDesignImage) {
                final JRDesignImage img = (JRDesignImage) obj;
                final JRDesignExpression varExp = (JRDesignExpression) img.getExpression();
                String path = "\"" + new File(getReportDir(), varExp.getText().substring(1, varExp.getText().length() - 1)).getPath() + "\"";
                path = escapeWinSeparators(path);
                varExp.setText(path);
            }
        }
    }

    /**
     * Update subreports path (in every JRBand); subreports are supposed to be
     * in the same folder of master report
     */
    private void updateSubreportsPath() {
        final List<JRBand> bands = getBands(getJasperDesign());

        for (final JRBand band : bands) {
            if (band != null) {
                final List<JRChild> f = band.getChildren();
                final Iterator<JRChild> it = f.iterator();

                Object obj = null;
                while (it.hasNext()) {
                    obj = it.next();
                    if (obj instanceof JRDesignSubreport) {
                        final JRDesignSubreport subreport = (JRDesignSubreport) obj;
                        final JRDesignExpression varExp = (JRDesignExpression) subreport.getExpression();
                        String path = "\"" + new File(getReportDir(), varExp.getText().substring(1, varExp.getText().length() - 1)).getPath() + "\"";
                        path = escapeWinSeparators(path);
                        varExp.setText(path);
                    }
                }
            }
        }
    }

    private String escapeWinSeparators(String path) {
        final StringBuffer newpath = new StringBuffer();
        final char sep = '\\';
        if (File.separator.toCharArray()[0] == sep) {
            final char[] ca = path.toCharArray();
            char ct;
            for (int i = 0; i < ca.length; i++) {
                ct = ca[i];
                if (ct != sep) {
                    newpath.append(ct);
                } else {
                    newpath.append(sep);
                    newpath.append(ct);
                }
            }
            path = newpath.toString();
        }
        return path;
    }

}
