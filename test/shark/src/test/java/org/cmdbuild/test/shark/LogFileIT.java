package org.cmdbuild.test.shark;

import static java.lang.String.format;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.cmdbuild.shark.test.utils.LoggerEventManager.ACTIVITY_CLOSED_LOG;
import static org.cmdbuild.shark.test.utils.LoggerEventManager.ACTIVITY_STARTED_LOG;
import static org.cmdbuild.shark.test.utils.LoggerEventManager.PROCESS_CLOSED_LOG;
import static org.cmdbuild.shark.test.utils.LoggerEventManager.PROCESS_STARTED_LOG;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.cmdbuild.test.shark.utils.AbstractRemoteSharkServiceTest;
import static org.cmdbuild.workflow.XpdlTest.randomName;
import org.cmdbuild.workflow.shark.xpdl.XpdlActivity;
import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper.ScriptLanguage;
import org.cmdbuild.workflow.shark.xpdl.XpdlProcess;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFileIT extends AbstractRemoteSharkServiceTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private XpdlProcess process;

	private final String processId = randomName();
	private final String activityId = randomName();

	@Before
	public void createAndUploadPackage() throws Exception {
		process = xpdlDocument.createProcess(processId);
	}

	@Test
	@Ignore //TODO fix and restore this
	public void emptyAtStartupNotEmptyAfterProcessStart() throws Exception {
		logger.info("emptyAtStartupNotEmptyAfterProcessStart BEGIN");
		try {
			assertTrue("file is not empty before startProcess", fileIsEmpty());
			startProcess();
			logger.info("log file content = {}", FileUtils.readFileToString(getLogFile()));
			assertFalse("file is empty after startProcess", fileIsEmpty());
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		} finally {
			getTomcatManager().getLogManager().flushLogs();
		}
		logger.info("emptyAtStartupNotEmptyAfterProcessStart END");
	}

	@Test
	@Ignore //TODO fix and restore this
	public void someExpectedLines() throws Exception {
		logger.info("someExpectedLines BEGIN");
		try {
			assertTrue("file is not empty before startProcess", fileIsEmpty());
			startProcess();
			logger.info("log file content = {}", FileUtils.readFileToString(getLogFile()));
			assertFalse("file is empty after startProcess", fileIsEmpty());
			assertThat(logLines(), hasItem(entryWithId(PROCESS_STARTED_LOG, processId)));
			assertThat(logLines(), hasItem(entryWithId(ACTIVITY_STARTED_LOG, activityId)));
			assertThat(logLines(), hasItem(entryWithId(ACTIVITY_CLOSED_LOG, activityId)));
			assertThat(logLines(), hasItem(entryWithId(PROCESS_CLOSED_LOG, processId)));
		} catch (Exception ex) {
			logger.error("error", ex);
			throw ex;
		} finally {
			getTomcatManager().getLogManager().flushLogs();
		}
		logger.info("someExpectedLines END");
	}

	private boolean fileIsEmpty() throws IOException {
		List<String> lines = FileUtils.readLines(LOGFILE);
		return lines.isEmpty();
	}

	private void startProcess() throws Exception {
		logger.info("create activity for process = {} activityId = {}", process, activityId);
		XpdlActivity activity = process.createActivity(activityId);
		activity.setScriptingType(ScriptLanguage.JAVA, "");
		uploadXpdlAndStartProcess(process);
	}

	private String entryWithId(String message, String id) {
		return format("%s: %s", message, id);
	}

}
