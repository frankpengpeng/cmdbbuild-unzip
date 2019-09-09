package org.cmdbuild.report.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.DataHandler;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static java.lang.String.format;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_REPORTS_MODIFY;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_REPORTS_VIEW;
import org.cmdbuild.auth.user.OperationUserStore;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportException;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.report.ReportData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.report.dao.ReportRepository;
import static org.cmdbuild.report.ReportConst.PARAM_IMAGE;
import static org.cmdbuild.report.ReportConst.PARAM_SUBREPORT;
import org.cmdbuild.report.dao.ReportDataImpl;
import org.cmdbuild.report.dao.ReportDataImpl.ReportDataImplBuilder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import org.cmdbuild.report.inner.utils.ReportUtils;
import static org.cmdbuild.report.inner.utils.ReportUtils.getImages;
import static org.cmdbuild.report.inner.utils.ReportUtils.getReportParameters;
import static org.cmdbuild.report.inner.utils.ReportUtils.getSubreports;
import static org.cmdbuild.report.inner.utils.ReportUtils.toByteArray;
import static org.cmdbuild.report.inner.utils.ReportUtils.toJasperDesign;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class ReportServiceImpl implements ReportService {//TODO localization of report (description, other)

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ReportRepository reportStore;
    private final OperationUserStore operationUser;
    private final ReportProcessorService processor;

    public ReportServiceImpl(ReportRepository reportStore, OperationUserStore operationUser, ReportProcessorService processor) {
        this.reportStore = checkNotNull(reportStore);
        this.operationUser = checkNotNull(operationUser);
        this.processor = checkNotNull(processor);
    }

    @Override
    public List<ReportInfo> getAll() {
        return (List) reportStore.getAllActiveReports();
    }

    @Override
    public List<ReportInfo> getForCurrentUser() {
        return reportStore.getAllActiveReports().stream().filter(this::canRead).map(ReportInfo.class::cast).collect(toList());
    }

    @Override
    public ReportInfo getById(long reportId) {
        return checkCanRead(reportStore.getById(reportId));
    }

    @Override
    public PrivilegeSubjectWithInfo getReportAsPrivilegeSubjectById(long reportId) {
        return reportStore.getById(reportId);
    }

    @Override
    public boolean isAccessibleByCode(String reportCode) {
        return canRead(reportStore.getReportByCode(reportCode));
    }

    @Override
    public ReportInfo getByCode(String code) {
        return checkCanRead(reportStore.getReportByCode(code));
    }

    @Override
    public ReportData getReportData(long reportId) {
        return checkCanRead(reportStore.getById(reportId));
    }

    @Override
    public ReportData updateReportTemplate(long reportId, Map<String, byte[]> reportFiles) {
        ReportData reportData = checkCanWrite(getReportData(reportId));
        return reportStore.updateReport(ReportDataImpl.copyOf(reportData)
                .accept(updateReportData(reportFiles))
                .build());
    }

    @Override
    public ReportData createReport(ReportInfo data, Map<String, byte[]> files) {
        checkCanCreate();
        ReportData reportData = ReportDataImpl.copyOf(data)
                .accept(updateReportData(files))
                .build();
        return reportStore.createReport(reportData);
    }

    @Override
    public ReportData updateReportInfo(ReportInfo info) {
        ReportData reportData = getReportData(info.getId());
        reportData = ReportDataImpl.copyOf(reportData).withInfo(info).build();
        return reportStore.updateReport(reportData);
    }

    @Override
    public ReportData updateReport(ReportInfo info, Map<String, byte[]> files) {
        ReportData reportData = checkCanWrite(getReportData(info.getId()));
        reportData = ReportDataImpl.copyOf(reportData)
                .withInfo(info)
                .accept(updateReportData(files))
                .build();
        return reportStore.updateReport(reportData);
    }

    @Override
    public void deleteReport(long reportId) {
        ReportInfo report = checkCanWrite(getReportData(reportId));
        reportStore.deleteReportById(report.getId());
    }

    private boolean canRead(ReportInfo report) {
        return operationUser.hasPrivileges((p) -> p.hasPrivileges(RP_ADMIN_REPORTS_VIEW) || p.hasReadAccess(report));//TODO auto grant read from reports view
    }

    private <T extends ReportInfo> T checkCanRead(T report) {
        checkArgument(canRead(report), "CM: access denied: you are not allowed to access this report");
        return report;
    }

    private <T extends ReportInfo> T checkCanWrite(T report) {
        checkArgument(operationUser.getPrivileges().hasPrivileges(RP_ADMIN_REPORTS_MODIFY), "CM: permission denied: you are not allowed to modify this report");
        return report;
    }

    private void checkCanCreate() {
        checkArgument(operationUser.getPrivileges().hasPrivileges(RP_ADMIN_REPORTS_MODIFY), "CM: permission denied: you are not allowed to create reports");
    }

    private Consumer<ReportDataImplBuilder> updateReportData(Map<String, byte[]> reportFilesParam) {
        return (builder) -> {
            Map<String, byte[]> reportFiles = map(reportFilesParam);
            reportFiles.forEach((k, v) -> checkArgument(isNotBlank(k) && v != null && v.length > 0, "invalid file = %s", k));

            List<Pair<String, JasperDesign>> reportSources = reportFiles.keySet().stream().filter((p) -> p.endsWith(".jrxml")).map((k) -> {
                byte[] data = checkNotNull(reportFiles.get(k));
                try {
                    return Pair.of(k, toJasperDesign(data));
                } catch (Exception ex) {
                    throw new ReportException(ex, "error processing report from file = {} ({})", k, org.apache.commons.io.FileUtils.byteCountToDisplaySize(data.length));
                }
            }).collect(toList());

            Entry<String, JasperDesign> masterReport;
            try {
                checkArgument(!reportSources.isEmpty());
                if (reportSources.size() == 1) {
                    masterReport = getOnlyElement(reportSources);
                } else {
                    masterReport = reportSources.stream().filter((r) -> !getSubreports(r.getValue()).isEmpty()).collect(onlyElement());
                }
            } catch (Exception ex) {
                throw new ReportException(ex, "unable to find master report; expected one and only one <file>.jrxml master report file");
            }

            logger.debug("processing master report from file = {}", masterReport.getKey());

            reportSources.stream().filter((p) -> !equal(p.getKey(), masterReport.getKey())).forEach((p) -> {
                String compiledReportName = format("%s.jasper", FilenameUtils.getBaseName(p.getKey()));
                logger.debug("compiling sub report source file = {} to file = {}", p.getKey(), compiledReportName);
                try {
                    byte[] data = toByteArray(p.getValue());
                    reportFiles.remove(p.getKey());
                    reportFiles.put(compiledReportName, data);
                } catch (Exception ex) {
                    throw new ReportException(ex, "error compiling sub report file = %s", p.getKey());
                }
            });

            JasperDesign jasperDesign = masterReport.getValue();
            List<String> expectedImages, expectedSubreports;

//		checkJasperDesignParameters(jd); TODO
            List<JRDesignImage> designImages = getImages(jasperDesign);
            expectedImages = designImages.stream().map(ReportUtils::getImageFileName).collect(toList());
            prepareDesignImagesForUpload(designImages);

            List<JRSubreport> subreports = getSubreports(jasperDesign);
            expectedSubreports = subreports.stream().map(ReportUtils::getSubreportName).collect(toList());
            prepareDesignSubreportsForUpload(subreports);

            List<String> missingFiles = list(expectedImages).with(expectedSubreports).stream().distinct().filter(not(reportFiles.keySet()::contains)).collect(toList());
            checkArgument(missingFiles.isEmpty(), "missing required files = %s", missingFiles.stream().collect((joining(","))));

            reportFiles.keySet().stream().filter(not(set(expectedImages).with(expectedSubreports).with(masterReport.getKey())::contains)).forEach((superflousFile) -> {
                logger.warn(marker(), "found unnecessary file = {} (this file will be ignored and discarded)", superflousFile);
            });

            List<byte[]> imageDataList = expectedImages.stream().map(reportFiles::get).collect(toList()),
                    subreportDataList = expectedSubreports.stream().map(reportFiles::get).collect(toList());

            byte[] compiledMasterReport = toByteArray(jasperDesign);

            String query = jasperDesign.getQuery() == null ? null : jasperDesign.getQuery().getText().replaceAll("\"", "\\\"");

            builder
                    .withImageNames(expectedImages)
                    .withImages(imageDataList)
                    .withSubReports(subreportDataList)
                    .withMainReport(compiledMasterReport)
                    .withQuery(query);
        };
    }

    @Override
    public List<Attribute> getParamsById(long id) {
        ReportData reportData = reportStore.getById(id);
        return getReportParameters(reportData).stream().map(ReportParameter::toCardAttribute).collect(toList());
    }

    @Override
    public DataHandler executeReportAndDownload(long reportId, ReportFormat reportExtension, Map<String, Object> parameters) {
        ReportData report = reportStore.getById(reportId);
        return processor.executeReport(report, reportExtension, parameters);
    }

//    @Override
//    public File getReportDir() {
//        return new File(directoryService.getWebappDirectory(), "WEB-INF/reports");
//    }
    @Override
    public DataHandler executeReportFromFile(String fileName, ReportFormat reportExtension, Map<String, Object> params, Function<JasperDesign, JasperDesign> reportTransformer) {
        throw new UnsupportedOperationException("TODO");
//        try {
//            String basename = FilenameUtils.getBaseName(fileName);
//            JasperDesign originalJasperDesign = JRXmlLoader.load(new File(getReportDir(), basename + ".jrxml")),
//                    actualJasperDesign = reportTransformer.apply(originalJasperDesign);
//            ReportProcessor reportProcessor = new ReportProcessorFromFileImpl(reportHelper, dataSource, configuration, dao, getReportDir(), params, reportExtension, actualJasperDesign, basename);
//            return doExecuteReportAndDownload(reportProcessor);
//        } catch (JRException ex) {
//            throw new ReportException(ex);
//        }
    }

    private static void prepareDesignSubreportsForUpload(List<JRSubreport> subreportsList) {
        for (int i = 0; i < subreportsList.size(); i++) {
            JRDesignSubreport jrSubreport = (JRDesignSubreport) subreportsList.get(i);
            JRDesignExpression newExpr = new JRDesignExpression();
            String newSubreportName = PARAM_SUBREPORT + (i + 1);
            newExpr.setText("$P{REPORT_PARAMETERS_MAP}.get(\"" + newSubreportName + "\")");
            jrSubreport.setExpression(newExpr);
        }
    }

    private static void prepareDesignImagesForUpload(List<JRDesignImage> designImagesList) {
        for (int i = 0; i < designImagesList.size(); i++) {
            JRDesignImage jrImage = designImagesList.get(i);

            // set expression
            JRDesignExpression newImageExpr = new JRDesignExpression();
            String newImageName = PARAM_IMAGE + i;
            newImageExpr.setText("$P{REPORT_PARAMETERS_MAP}.get(\"" + newImageName + "\")");
            jrImage.setExpression(newImageExpr);

            // set options
            jrImage.setUsingCache(true);
            jrImage.setOnErrorType(OnErrorTypeEnum.BLANK);
        }
    }

}
