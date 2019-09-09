/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.activation.DataHandler;
import static org.cmdbuild.auth.AuthConst.GOD_USER;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportInfoImpl;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.report.utils.ReportFilesUtils;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class ReportIT {

	private final SessionService sessionService;
	private final ReportService service;

	public ReportIT(SessionService sessionService, ReportService service) {
		this.sessionService = checkNotNull(sessionService);
		this.service = checkNotNull(service);
	}

	@Before
	public void init() {
		sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(GOD_USER));
	}

	@After
	public void cleanup() {
		sessionService.deleteCurrentSessionIfExists();
		//TODO autcleanup of created reports
	}

	@Test
	public void testImport() {
		service.createReport(ReportInfoImpl.builder().withCode("BuonoScarico").build(),
				ReportFilesUtils.unpackReportFiles(map("file.zip", toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/test_report_1.zip")))));

		assertNotNull(service.getByCode("BuonoScarico"));

		service.deleteReport("BuonoScarico");
	}
	
	@Test
	@Ignore("TODO fix this")//TODO
	public void testReport() {
		service.createReport(ReportInfoImpl.builder().withCode("BuonoScarico").build(),
				ReportFilesUtils.unpackReportFiles(map("file.zip", toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/test_report_1.zip")))));

		assertNotNull(service.getByCode("BuonoScarico"));

		DataHandler dataHandler = service.executeReportAndDownload("BuonoScarico", ReportFormat.PDF);
		assertNotNull(dataHandler);
		assertTrue(dataHandler.getName().endsWith(".pdf"));
		assertTrue(toByteArray(dataHandler).length > 0);
		assertEquals("application/pdf", dataHandler.getContentType());

		service.deleteReport("BuonoScarico");
	}
}
