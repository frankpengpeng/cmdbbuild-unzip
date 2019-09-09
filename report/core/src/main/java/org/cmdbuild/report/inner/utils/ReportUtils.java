/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Iterables.filter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import static java.util.Arrays.stream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseReport;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.ReportException;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportProcessor;
import org.cmdbuild.report.inner.ReportParameter;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.normalize;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

public class ReportUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String getFileExtensionForReportFormat(ReportFormat reportExtension) {
        return reportExtension.name().toLowerCase();//TODO
    }

    public static JasperDesign loadReportFromResources(String reportFileName) throws JRException {
        return JRXmlLoader.load(checkNotNull(ReportUtils.class.getResourceAsStream("/org/cmdbuild/report/files/" + reportFileName), "report not found for file =< %s >", reportFileName));
    }

    public static Map<String, Object> loadReportImageParamsFromResourcesAndFixReport(JasperDesign jasperDesign) throws JRException {
        Map<String, Object> imageParams = map();
        List<JRDesignImage> designImages = getImages(jasperDesign);
        designImages.forEach(i -> {
            JRDesignExpression expression = (JRDesignExpression) i.getExpression();
            String imageFileName = expression.getText().replaceFirst("^[\"](.+)\"$", "$1"),
                    imageId = format("image_%s_%s", normalize(imageFileName), randomId(6));
            imageParams.put(imageId, new ByteArrayInputStream(CmIoUtils.toByteArray(checkNotNull(ReportUtils.class.getResourceAsStream("/org/cmdbuild/report/files/" + imageFileName), "report image not found for name =< %s >", imageFileName))));
            expression.setText(format("$P{REPORT_PARAMETERS_MAP}.get(\"%s\")", imageId));
        });
        return imageParams;
    }

    public static Object loadSubreportFromResources(String subreportFileName) throws JRException {
        return new ByteArrayInputStream(CmIoUtils.toByteArray(checkNotNull(ReportUtils.class.getResourceAsStream("/org/cmdbuild/report/files/" + subreportFileName), "subreport not found for name =< %s >", subreportFileName)));
    }
//    public static Map<String, Object> loadSubreportParamsFromResources(String... subreports) throws JRException {
//        
//    }

    public static JRExporter createExporter(ReportFormat format) {
        switch (format) {
            case PDF:
                return new JRPdfExporter();
            case CSV:
                JRCsvExporter csvExporter = new JRCsvExporter();
                csvExporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, ";");
                return csvExporter;
            case ODT:
                return new JROdtExporter();
            case RTF:
                return new JRRtfExporter();
            default:
                throw unsupported("unsupported report extension = %s", format);
        }
    }

    public static String getSubreportName(JRSubreport jrSubreport) {//TODO improve this
        String rawExpr = jrSubreport.getExpression().getText();
        LOGGER.trace("get subreport name, raw expr = {}", rawExpr);
        String subreportPath = rawExpr.replaceAll("\\$P\\{SUBREPORT_DIR\\}", "")
                .replaceAll("\\+", "")
                .replaceAll("[ \"]", "");
        LOGGER.trace("got subreport name, raw expr = {}, name = {}", rawExpr, subreportPath);
        return subreportPath;
    }

    public static List<JRSubreport> getSubreports(JasperDesign jasperDesign) {
        // Search parameter indicating IReport subreport's directory
        String subreportDir = new String();
        JRDesignParameter subreportDirPar;

        Map jdMapParameters = jasperDesign.getParametersMap();
        if (jdMapParameters.containsKey("SUBREPORT_DIR")) {
            subreportDirPar = (JRDesignParameter) jdMapParameters.get("SUBREPORT_DIR");
            subreportDir = subreportDirPar.getDefaultValueExpression().getText();
        }
        subreportDir = subreportDir.replace("\"", ""); // deleting quotes
        if (!subreportDir.trim().equals("")) {
            LOGGER.debug("The directory of subreport is: {}", subreportDir);
        }

        return getBands(jasperDesign).stream().filter((band) -> (band != null && band.getChildren() != null)).map((band) -> searchSubreports(band.getChildren())).flatMap(List::stream).collect(toList());
    }

    private static List<JRSubreport> searchSubreports(List<JRChild> elements) {
        List<JRSubreport> subreportsList = list();
        Iterator<JRChild> i = elements.listIterator();
        while (i.hasNext()) {
            final Object jreg = i.next();
            if (jreg instanceof JRSubreport) {
                subreportsList.add((JRSubreport) jreg);
            } else if (jreg instanceof JRElementGroup) {
                subreportsList.addAll(searchSubreports(((JRElementGroup) jreg).getChildren()));
            }
        }
        return subreportsList;
    }

    public static List<JRDesignImage> getImages(JRBaseReport report) {
        return getBands(report).stream().filter((b) -> (b != null && b.getChildren() != null)).map((b) -> searchImages(b.getChildren())).flatMap(List::stream).collect(toList());
    }

    private static List<JRDesignImage> searchImages(List<JRChild> elements) {
        Iterator<JRChild> i = elements.listIterator();
        List<JRDesignImage> designImagesList = list();
        while (i.hasNext()) {
            Object jreg = i.next();
            if (jreg instanceof JRDesignImage) {
                designImagesList.add((JRDesignImage) jreg);
            } else if (jreg instanceof JRElementGroup) {
                designImagesList.addAll(searchImages(((JRElementGroup) jreg).getChildren()));
            }
        }
        return designImagesList;
    }

    public static List<JRBand> getBands(JRBaseReport jasperDesign) {
        List<JRBand> bands = list();
        bands.add(jasperDesign.getTitle());
        bands.add(jasperDesign.getPageHeader());
        bands.add(jasperDesign.getColumnHeader());
        bands.addAll(list(jasperDesign.getDetailSection().getBands()));
        bands.add(jasperDesign.getColumnFooter());
        bands.add(jasperDesign.getPageFooter());
        bands.add(jasperDesign.getLastPageFooter());
        bands.add(jasperDesign.getSummary());
        for (JRGroup group : jasperDesign.getGroups()) {
            bands.addAll(list(group.getGroupFooterSection().getBands()));
            bands.addAll(list(group.getGroupHeaderSection().getBands()));
        }
        return ImmutableList.copyOf(filter(bands, not(isNull())));
    }

    public static String getImageFileName(JRImage jrImage) {
        String rawExpr = jrImage.getExpression().getText();
        LOGGER.trace("extracting report image file name from expr = {}", rawExpr);
        String filename = new File(rawExpr.replaceAll("\"", "").replaceAll("[\\\\]", "/")).getName();
        LOGGER.trace("extracted report image file name from expr = {}, image file name = {}", rawExpr, filename);
        return filename;
    }

    public static String getImageFormatName(InputStream is) throws IOException { //TODO improve this (use tika?)
        String format = "";
        try (ImageInputStream iis = ImageIO.createImageInputStream(is)) {
            Iterator<ImageReader> readerIterator = ImageIO.getImageReaders(iis);
            if (readerIterator.hasNext()) {
                final ImageReader reader = readerIterator.next();
                format = reader.getFormatName();
            }
        }
        is.reset();
        return format;
    }

    public static DataHandler doExecuteReportAndDownload(ReportProcessor reportFactory) {
        try {
            return new DataHandler(reportFactory.executeReport());
        } catch (Exception e) {
            throw new ReportException(e, "error processing report = %s", reportFactory);
        }
    }

    public static void setImageFilename(JRImage jrImage, String newValue) {
        JRDesignExpression newImageExpr = new JRDesignExpression();
        newImageExpr.setText(newValue);
        ((JRDesignImage) jrImage).setExpression(newImageExpr);
    }

    public static void prepareDesignImagesForZipExport(List<JRDesignImage> designImagesList, List<String> origImagesName) {
        for (int i = 0; i < designImagesList.size(); i++) {
            JRDesignImage jrImage = designImagesList.get(i);
            JRDesignExpression newImageExpr = new JRDesignExpression();
            newImageExpr.setText("\"" + origImagesName.get(i) + "\"");
            jrImage.setExpression(newImageExpr);
        }
    }

    public static void prepareDesignSubreportsForZipExport(List<JRSubreport> designSubreports, List<JasperReport> jasperSubreports) {
        for (int i = 0; i < designSubreports.size(); i++) {
            JRDesignSubreport jrSubreport = (JRDesignSubreport) designSubreports.get(i);
            String subreportName = jasperSubreports.get(i).getName() + ".jasper";
            JRDesignExpression newExpr = new JRDesignExpression();
            newExpr.setText("\"" + subreportName + "\"");
            jrSubreport.setExpression(newExpr);
        }
    }

    public static List<ReportParameter> getReportParameters(ReportData reportCard) {
        return getReportParameters(reportCard.getMasterReportAsJasperReport());
    }

    public static List<ReportParameter> getReportParameters(JasperReport masterReport) {
        return stream(masterReport.getParameters()).filter((p) -> (p.isForPrompting() && !p.isSystemDefined())).map(ReportParameter::parseJrParameter).collect(toList());
    }

    public static JasperDesign toJasperDesign(byte[] data) {
        try {
            return JRXmlLoader.load(new ByteArrayInputStream(data));
        } catch (JRException ex) {
            throw new ReportException(ex, "error deserializing jrxml file");
        }
    }

    public static byte[] toByteArray(JasperDesign jasperDesign) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JasperCompileManager.compileReportToStream(jasperDesign, out);
            return out.toByteArray();
        } catch (JRException ex) {
            throw new ReportException(ex, "error compiling/serializing report");
        }
    }

    public static JasperPrint compileAndFillReport(JasperDesign jasperDesign, Map<String, Object> params, JRDataSource dataSource) {
        try {
            LOGGER.trace("compile report");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            LOGGER.trace("execute report");
            return JasperFillManager.fillReport(jasperReport, params, dataSource);
        } catch (JRException ex) {
            throw new ReportException(ex);
        }
    }
}
