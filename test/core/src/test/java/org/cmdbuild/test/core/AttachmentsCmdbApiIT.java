/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import static org.cmdbuild.auth.AuthConst.GOD_USER;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.report.ReportInfoImpl;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.report.utils.ReportFilesUtils;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.core.fluentapi.WorkflowApiService;
import org.cmdbuild.lang.scriptexecutors.BeanshellScriptExecutor;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class AttachmentsCmdbApiIT {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final WorkflowApiService apiService;
    private final ReportService reportService;
    private final DaoService dao;
    private final SessionService sessionService;
    private final DmsService documentService;
    private final GlobalConfigService configService;

    public AttachmentsCmdbApiIT(WorkflowApiService apiService, ReportService reportService, DaoService dao, SessionService sessionService, DmsService documentService, GlobalConfigService configService) {
        this.apiService = checkNotNull(apiService);
        this.reportService = checkNotNull(reportService);
        this.dao = checkNotNull(dao);
        this.sessionService = checkNotNull(sessionService);
        this.documentService = checkNotNull(documentService);
        this.configService = checkNotNull(configService);
    }

    @Before
    public void init() {
        configService.putStrings(map(
                "org.cmdbuild.dms.service.type", "postgres",
                "org.cmdbuild.dms.enabled", "true"
        ));

        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(GOD_USER));
    }

    @Test
    public void testAttachment() {
        Map<String, Object> input = apiService.getWorkflowApiAsDataMap();

        Classe one = dao.createClass(ClassDefinitionImpl.builder().withName("AttachmentClass").build());
        one = dao.getClasse(one.getName());

        Card card_one = dao.create(CardImpl.buildCard(one, map("Code", "one", "Description", "unooo")));

        reportService.createReport(ReportInfoImpl.builder().withCode("TestReport").withDescription("Test Report").build(),
                ReportFilesUtils.unpackReportFiles(map("file.zip", toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/BlankReport.zip")))));

        Object value = new BeanshellScriptExecutor("exampleReport = cmdb.createReport(\"TestReport\",\"pdf\").download(); "
                + "cmdb.existingCard(\"AttachmentClass\"," + card_one.getId() + ").withAttachment(exampleReport.getUrl(), \"Example.pdf\", \"Document\", \"Example\").update();").execute(input).get("output");

        assertEquals(1, documentService.getCardAttachments(one.getName(), card_one.getId()).size());
    }
}
