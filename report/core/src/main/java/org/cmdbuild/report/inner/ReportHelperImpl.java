/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayOutputStream;
import static java.lang.String.format;
import javax.activation.DataHandler;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.FilenameUtils;
import org.cmdbuild.report.ReportException;
import org.cmdbuild.report.ReportFormat;
import static org.cmdbuild.report.inner.utils.ReportUtils.createExporter;
import static org.cmdbuild.report.inner.utils.ReportUtils.getFileExtensionForReportFormat;
import org.cmdbuild.userconfig.UserPreferencesService;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.springframework.stereotype.Component;

@Component
public class ReportHelperImpl implements ReportHelper {

    private final UserPreferencesService userPreferencesService;

    public ReportHelperImpl(UserPreferencesService userPreferencesService) {
        this.userPreferencesService = checkNotNull(userPreferencesService);
    }

    @Override
    public String getContentTypeForReportFormat(ReportFormat reportExtension) {
        switch (reportExtension) {
            case PDF:
                return "application/pdf";
            case CSV:
                switch (userPreferencesService.getUserPreferences().getPreferredOfficeSuite()) {
                    case POS_MSOFFICE:
                        return "application/vnd.ms-excel";
                    case POS_DEFAULT:
                    default:
                        return "text/plain";//note: we use text/plain instead of text/csv because text/plain is more browser-friendly (open inline)
                }
            case ODT:
                return "application/vnd";
            case RTF:
                return "application/rtf";
            case ZIP:
                return "application/zip";
            default:
                throw unsupported("unsupported report extension = %s", reportExtension);
        }
    }

    @Override
    public DataHandler exportReport(JasperPrint reportOutput, String basename, ReportFormat format) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JRExporter exporter = createExporter(format);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportOutput);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            exporter.exportReport();
            return newDataHandler(out.toByteArray(), getContentTypeForReportFormat(format), format("%s.%s", FilenameUtils.getBaseName(basename), getFileExtensionForReportFormat(format)));
        } catch (JRException ex) {
            throw new ReportException(ex, "error printing report result");
        }
    }

}
