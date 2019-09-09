package org.cmdbuild.shark.test.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.io.File;
import static java.util.Collections.emptyList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.cmdbuild.clustering.ClusterMessage;
import org.cmdbuild.clustering.ClusterNode;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import org.cmdbuild.workflow.SimpleEventManager;
import org.cmdbuild.workflow.shark.engine.WorkflowRemoteService;
import org.cmdbuild.workflow.SharkRemoteServiceConfiguration;
import org.cmdbuild.workflow.shark.SharkEventService;
import org.cmdbuild.workflow.shark.SharkTransactionService;
import org.cmdbuild.workflow.shark.SharkTransactionServiceImpl;
import org.cmdbuild.workflow.shark.SharkWebserviceClient;
import org.cmdbuild.workflow.shark.SharkWebserviceClientImpl;
import org.cmdbuild.workflow.shark.SharkWorkflowRemoteServiceAndRepositoryImpl;
import org.cmdbuild.workflow.shark.TransactedSharkMethodAspectjConfig;
import org.cmdbuild.workflow.shark.SharkEventServiceImpl;
import static org.enhydra.dods.CommonConstants.DODS_CONFIG_FILE_PROPERTY_NAME;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.cmdbuild.clustering.ClusterService;

public abstract class AbstractLocalSharkServiceTest extends AbstractSharkServiceTest {

	public static final String USERNAME = "admin";

	protected SimpleEventManager eventManager;

	private WorkflowRemoteService remoteService;

	@Before
	public void initializeEventManager() {
		SharkRemoteServiceConfiguration config = new SharkRemoteServiceConfigurationForTest();
		SharkWebserviceClient sharkWebserviceClient = new SharkWebserviceClientImpl(null, config, null);

		SharkEventService sharkEventService = new SharkEventServiceImpl(new ClusterService() {

			private final EventBus eventBus = new EventBus();

			@Override
			public void sendMessage(ClusterMessage clusterMessage) {
			}

			@Override
			public EventBus getEventBus() {
				return eventBus;
			}

			@Override
			public boolean isRunning() {
				return false;
			}

			@Override
			public List<ClusterNode> getClusterNodes() {
				return emptyList();
			}

		});
		SharkTransactionService sharkTransactionService = new SharkTransactionServiceImpl(sharkWebserviceClient, sharkEventService);
		TransactedSharkMethodAspectjConfig transactedSharkMethodAspectjConfig = new TransactedSharkMethodAspectjConfig(sharkTransactionService);

		remoteService = new SharkWorkflowRemoteServiceAndRepositoryImpl(sharkWebserviceClient, transactedSharkMethodAspectjConfig);
		remoteService = transactedSharkMethodAspectjConfig.createAspectjProxy(remoteService);

		eventManager = MockEventAuditManager.mock;
	}

	@After
	public void resetEventManagerMock() {
		Mockito.reset(eventManager);
		remoteService = null;
	}

	@Override
	protected WorkflowRemoteService getWorkflowRemoteService() {
		return checkNotNull(remoteService);
	}

	private static File databaseManagerConfig;

	@BeforeClass
	public static void initClass() throws Exception {
		databaseManagerConfig = tempFile();
		FileUtils.copyInputStreamToFile(checkNotNull(AbstractLocalSharkServiceTest.class.getResourceAsStream("/databaseManager.conf")), databaseManagerConfig);
		System.setProperty(DODS_CONFIG_FILE_PROPERTY_NAME, databaseManagerConfig.getAbsolutePath());
	}

	@AfterClass
	public static void cleanupClass() {
		System.clearProperty(DODS_CONFIG_FILE_PROPERTY_NAME);
		FileUtils.deleteQuietly(databaseManagerConfig);
		databaseManagerConfig = null;
	}

}
