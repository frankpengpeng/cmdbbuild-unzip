package org.cmdbuild.test.shark.utils;

import com.google.common.eventbus.EventBus;
import org.cmdbuild.shark.test.utils.AbstractSharkServiceTest;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.cmdbuild.clustering.ClusterMessage;
import org.cmdbuild.clustering.ClusterNode;
import org.cmdbuild.utils.tomcatmanager.TomcatConfig;
import org.cmdbuild.utils.tomcatmanager.TomcatManager;
import org.cmdbuild.workflow.shark.engine.WorkflowRemoteService;
import org.cmdbuild.workflow.SharkRemoteServiceConfiguration;
import org.cmdbuild.workflow.shark.SharkEventService;
import org.cmdbuild.workflow.shark.SharkEventServiceImpl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.clustering.ClusterService;

/**
 *
 * Don't forget to initialize the variable {@code ws} in every subclass.
 *
 */
public abstract class AbstractRemoteSharkServiceTest extends AbstractSharkServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Defined in the {@code Shark.conf} configuration file.
     */
    protected static final File LOGFILE = new File(SystemUtils.getJavaIoTmpDir(), "it-shark-4.4.log");

    protected static final String USERNAME = "admin";
    protected static final String PASSWORD = "enhydra";
    protected static final String SERVER_HOST = "localhost";

    private static TomcatManager tomcatManager;

    @BeforeClass
    public static void initClass() {
        LOGGER.info("initClass BEGIN");
        tomcatManager = new TomcatManager(TomcatConfig.builder()
                .withProperty("tomcat_deploy_artifacts", System.getProperty("basedir") + "/target/shark.war")
                .build());
        tomcatManager.buildAndStart(false);
        tomcatManager.waitForTomcatStartup();
        LOGGER.info("initClass END");
    }

    @AfterClass
    public static void cleanupClass() {
        LOGGER.info("cleanupClass BEGIN");
        tomcatManager.stopAndCleanup();
        tomcatManager = null;
        LOGGER.info("cleanupClass END");
    }

    protected final ChangeableRemoteSharkServiceConfiguration configuration = new ChangeableRemoteSharkServiceConfiguration();
    private WorkflowRemoteService remoteService;

    @Override
    protected WorkflowRemoteService getWorkflowRemoteService() {
        return remoteService;
    }

    protected File getLogFile() {
        return LOGFILE;
    }

    @Before
    public void cleanLogFile() throws Exception {
        FileUtils.writeStringToFile(getLogFile(), EMPTY);

//		SharkRemoteServiceConfiguration config = new SharkRemoteServiceConfigurationForTest();
//	SharkWebserviceClient sharkWebserviceClient = new SharkWebserviceClientImpl(configuration);
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
        //	SharkTransactionService sharkTransactionService = new SharkTransactionServiceImpl(sharkWebserviceClient, sharkEventService);
//		TransactedSharkMethodAspectjConfig transactedSharkMethodAspectjConfig = new TransactedSharkMethodAspectjConfig(sharkTransactionService);

        //	remoteService = new SharkWorkflowRemoteServiceAndRepositoryImpl(sharkWebserviceClient, transactedSharkMethodAspectjConfig);
//		remoteService = transactedSharkMethodAspectjConfig.createAspectjProxy(remoteService);
    }

    protected List<String> logLines() throws IOException {
        return FileUtils.readLines(getLogFile());
    }

    protected TomcatManager getTomcatManager() {
        checkNotNull(tomcatManager);
        return tomcatManager;
    }

    protected class ChangeableRemoteSharkServiceConfiguration implements SharkRemoteServiceConfiguration {

        private String serverHost = SERVER_HOST;
//		private int serverPort = SERVER_PORT;
        private String webappName = "shark";
        private String username = USERNAME;
        private String password = PASSWORD;

//		private final Set<ChangeListener> changeListeners = new HashSet<>();
        @Override
        public String getSharkServerUrl() {
            return String.format("http://%s:%d/%s", serverHost, getTomcatManager().getConfig().getHttpPort(), webappName);
        }

        public void setServerHost(String serverHost) {
            this.serverHost = serverHost;
        }

//		public void setServerPort( int serverPort) {
//			this.serverPort = serverPort;
//		}
        public void setWebappName(String webappName) {
            this.webappName = webappName;
        }

        @Override
        public String getSharkUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public String getSharkPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void notifyChange() {
            throw new UnsupportedOperationException("TODO"); //TODO
        }

    }
}
