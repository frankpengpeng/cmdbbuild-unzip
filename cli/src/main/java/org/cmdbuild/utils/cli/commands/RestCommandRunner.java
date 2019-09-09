/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Stopwatch;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.Maps;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.io.Files.toByteArray;
import com.google.common.net.UrlEscapers;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import java.net.URLDecoder;
import java.time.Duration;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import static java.util.stream.Collectors.joining;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Base64;
import static org.apache.commons.codec.binary.Base64.isBase64;
import static org.apache.commons.collections.MapUtils.toProperties;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.io.IOUtils.copyLarge;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.audit.RequestData;
import org.cmdbuild.audit.RequestInfo;
import org.cmdbuild.auth.login.file.FileAuthUtils.AuthFile;
import static org.cmdbuild.auth.login.file.FileAuthUtils.buildAuthFile;
import org.cmdbuild.client.rest.RestClient;
import static org.cmdbuild.client.rest.RestClientImpl.build;
import org.cmdbuild.client.rest.api.AttachmentApi.AttachmentData;
import org.cmdbuild.client.rest.api.AttachmentApi.AttachmentPreview;
import org.cmdbuild.client.rest.api.ClassApi;
import static org.cmdbuild.client.rest.api.LoginApi.RSA_KEY_PASSWORD_PREFIX;
import org.cmdbuild.client.rest.api.SessionApi.SessionInfo;
import org.cmdbuild.client.rest.api.SystemApi.LoggerInfo;
import org.cmdbuild.client.rest.api.WokflowApi.FlowDataAndStatus;
import org.cmdbuild.client.rest.api.WokflowApi.TaskDetail;
import org.cmdbuild.client.rest.model.Attachment;
import org.cmdbuild.client.rest.model.Card;
import org.cmdbuild.client.rest.model.SimpleCard;
import org.cmdbuild.client.rest.model.SimpleFlowData;
import org.cmdbuild.config.api.ConfigDefinition;
import org.cmdbuild.services.SystemStatus;
import org.cmdbuild.utils.cli.commands.restcommandutils.CliAction;
import org.cmdbuild.utils.cli.commands.restcommandutils.CliCommand;
import org.cmdbuild.utils.cli.commands.restcommandutils.CliCommandParser;
import static org.cmdbuild.utils.xml.CmXmlUtils.prettifyIfXml;
import org.cmdbuild.client.rest.api.LookupApi;
import org.cmdbuild.client.rest.api.SystemApi;
import org.cmdbuild.client.rest.api.SystemApi.ClusterStatus;
import org.cmdbuild.client.rest.api.WokflowApi;
import org.cmdbuild.client.rest.model.AttributeData;
import org.cmdbuild.client.rest.model.AttributeRequestData;
import org.cmdbuild.client.rest.model.ClassData;
import org.cmdbuild.client.rest.model.CustomPageInfo;
import org.cmdbuild.client.rest.model.SimpleAttributeRequestData;
import org.cmdbuild.client.rest.model.SimpleClassData;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_WFY_PASSTOKEN_HEADER_OR_COOKIE;
import static org.cmdbuild.common.http.HttpConst.WFY_PASSTOKEN_DEFAULT;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_URL;
import org.cmdbuild.dao.entrytype.AttributePermissionMode;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportInfoImpl;
import static org.cmdbuild.utils.cli.Main.isRunningFromWebappDir;
import org.cmdbuild.utils.cli.commands.restcommandutils.CliCommandUtils;
import static org.cmdbuild.utils.cli.commands.restcommandutils.CliCommandUtils.prepareAction;
import static org.cmdbuild.utils.date.CmDateUtils.dateTimeFileSuffix;
import static org.cmdbuild.utils.date.CmDateUtils.getReadableTimezoneOffset;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.io.CmZipUtils.dirToZip;
import static org.cmdbuild.utils.json.CmJsonUtils.prettifyIfJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.w3c.dom.Document;
import org.cmdbuild.debuginfo.BugReportInfo;
import org.cmdbuild.debuginfo.BuildInfo;
import static org.cmdbuild.platform.UpgradeUtils.validateWarData;
import static org.cmdbuild.utils.gui.GuiFileEditor.editFile;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.date.CmDateUtils.toUserReadableDateTime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.cli.commands.restcommandutils.CliCommandParser.printActionHelp;
import static org.cmdbuild.utils.crypto.CmRsaUtils.parsePrivateKey;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.cli.Main.getCliHome;
import static org.cmdbuild.utils.cli.Main.getWarFile;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_ERROR;
import static org.cmdbuild.services.SystemServiceStatusUtils.serializeMinionStatus;
import static org.cmdbuild.services.SystemStatusUtils.serializeSystemStatus;
import static org.cmdbuild.utils.cli.commands.DbconfigCommandRunner.prepareDumpFile;
import static org.cmdbuild.utils.url.CmUrlUtils.decodeUrlParams;
import static org.cmdbuild.auth.AuthConst.GOD_USER;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_NAMESPACE;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.jobs.JobRun;
import static org.cmdbuild.jobs.JobRunStatusImpl.serializeJobRunStatus;
import static org.cmdbuild.services.SystemStatus.SYST_READY;
import static org.cmdbuild.utils.cli.utils.CliUtils.getDbdumpFile;
import static org.cmdbuild.utils.cli.utils.CliUtils.hasInteractiveConsole;
import static org.cmdbuild.utils.date.CmDateUtils.toDuration;
import static org.cmdbuild.utils.encode.CmPackUtils.isPacked;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytes;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.io.CmIoUtils.javaTmpDir;
import org.cmdbuild.utils.io.CmZipUtils;
import static org.cmdbuild.utils.io.CmZipUtils.unzipToDir;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmStringUtils.multilineWithOffset;
import static org.cmdbuild.workflow.WorkflowCommonConst.RIVER;
import org.cmdbuild.utils.io.StreamProgressListener;
import static org.cmdbuild.utils.tomcatmanager.TomcatManagerUtils.sleepSafe;

public class RestCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;
    private String username, password, baseUrl;
    private RestClient client;

    public RestCommandRunner() {
        super("restws", "test cmdbuild rest ws");
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("url", true, "set cmdbuild root url for rest ws (default is 'http://localhost:8080/cmdbuild/')");
        options.addOption("username", true, "set ws username (default is 'admin')");
        options.addOption("password", true, "set ws password (default is 'admin')");
        options.addOption(Option.builder("wfy").optionalArg(true).desc("enable wfy client mode (with optional passtoken)").build());
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        System.out.println("\navailable rest methods:");
        printActionHelp(actions);
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        baseUrl = getBaseUrl(cmd.getOptionValue("url"));
        logger.debug("selected base url = {}", baseUrl);

        username = firstNonNull(cmd.getOptionValue("username"), GOD_USER);
        password = cmd.getOptionValue("password");
        if (isBlank(password)) {
            if (baseUrl.matches(".*://localhost:.*") && isRunningFromWebappDir()) {
                AuthFile authFile = tryToBuildFilePassword();
                if (authFile != null) {
                    logger.debug("authenticating with file password = {}", authFile.getFile().getAbsolutePath());
                    authFile.getFile().deleteOnExit();
                    password = authFile.getPassword();
                }
            }
        }
        if (isBlank(password)) {
            File rsaKeyFile = new File(System.getProperty("user.home"), ".ssh/id_rsa_cmdbuild");
            if (rsaKeyFile.exists()) {
                try {
                    String rsaKeyData = readToString(rsaKeyFile);
                    parsePrivateKey(rsaKeyData);
                    password = RSA_KEY_PASSWORD_PREFIX + rsaKeyData;
                    logger.debug("using rsa key from file = {}", rsaKeyFile);
                } catch (Exception ex) {
                    logger.debug("unable to read private key data from file = {}", rsaKeyFile, ex);
                    logger.warn("unable to read private key data from file = {} : {}", rsaKeyFile, ex.toString());
                }
            }
        }
        checkNotBlank(password, "missing 'password' param for user = %s", username);

        Iterator<String> iterator = cmd.getArgList().iterator();
        if (!iterator.hasNext()) {
            System.out.println("no rest call requested, doing nothing...");
        } else {
            CliCommandUtils.ExecutableAction action = prepareAction(actions, iterator);
            client = build(baseUrl).withActionId("cli_restws_" + action.getAction().getName());
            if (cmd.hasOption("wfy")) {
                String wfyPasstoken = firstNotNull(trimToNull(cmd.getOptionValue("wfy")), WFY_PASSTOKEN_DEFAULT);
                client.withHeader(CMDBUILD_WFY_PASSTOKEN_HEADER_OR_COOKIE, wfyPasstoken);
            }
            try {
                action.execute();
            } finally {
                client.close();
            }
        }
    }

    private @Nullable
    AuthFile tryToBuildFilePassword() {
        try {
            checkArgument(isRunningFromWebappDir(), "cannot use file auth: not running from webapp dir");
            File authDir = new File(getCliHome(), "../../temp/");
            return buildAuthFile(authDir);
        } catch (Exception ex) {
            logger.error("error building file password", ex);
            return null;
        }
    }

    private String getBaseUrl(@Nullable String urlParam) {
        if (isNotBlank(urlParam)) {
            return urlParam;
        } else {
            int port = 8080;
            String webapp = "cmdbuild";
            if (isRunningFromWebappDir()) {
                try {
                    webapp = getCliHome().getCanonicalFile().getName();
                    File tomcatConf = new File(getCliHome(), "../../conf/server.xml");
                    if (tomcatConf.exists()) {
                        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(tomcatConf);
                        port = Integer.valueOf(checkNotBlank(XPathFactory.newInstance().newXPath().compile("string(//*[local-name()='Connector'][@protocol='HTTP/1.1']/@port)").evaluate(document)));
                        logger.debug("selected tomcat port = {}", port);
                    }
                } catch (Exception ex) {
                    logger.error("error processing tomcat config file", ex);
                }
            }
            return format("http://localhost:%s/%s/", port, webapp);
        }
    }

    @CliCommand
    protected void curl() {
        System.out.printf(buildCurlCli(client.doLoginWithAnyGroup(username, password).getSessionToken(), "services/rest/v3/"));
    }

    @CliCommand
    protected void status() {
        SystemStatus systemStatus = client.system().getStatus();
        System.out.println("system status: " + serializeSystemStatus(systemStatus));
        if (equal(systemStatus, SystemStatus.SYST_READY)) {
            System.out.println();
            SystemApi system = client.doLoginWithAnyGroup(username, password).system();
            Map<String, String> systemInfo = system.getSystemInfo();
            system.getServicesStatus().stream().forEach(s
                    -> System.out.printf("%-24s    %s   %s\n", s.getServiceName(), map(MS_READY, "*", MS_READY, "*", MS_ERROR, "E").getOrDefault(s.getServiceStatus(), " "), serializeMinionStatus(s.getServiceStatus())));//TODO duplicate code, refactor
            ClusterStatus clusterStatus = system.getClusterStatus();
            System.out.printf("\nversion: %s\nbuild:   %s\n", systemInfo.get("version"), systemInfo.get("build_info"));
            System.out.printf("uptime:  %s %s\n", systemInfo.get("hostname"), toUserDuration(systemInfo.get("uptime")));
            if (clusterStatus.isRunning()) {
                System.out.printf("\ncluster nodes:\n");
                clusterStatus.getNodes().forEach(n -> System.out.printf("\t%s  %-10s    %s\n", n.isThisNode() ? "(this node)" : "           ", n.getNodeId(), n.getAddress()));
            }
        }
    }

    @CliCommand
    protected void dump() throws IOException {
        byte[] data = client.doLoginWithAnyGroup(username, password).system().dumpDatabase();
        if (hasInteractiveConsole()) {
            File file = new File(format("cmdbuild_%s.dump", dateTimeFileSuffix()));
            writeToFile(file, data);
            System.out.printf("stored dump to file = %s\n (%s)", file.getAbsoluteFile(), FileUtils.byteCountToDisplaySize(data.length));
        } else {
            System.out.write(data);
        }
    }

    @CliCommand("reconfigure")
    protected void reconfigureDatabase(String newDatabaseUrl) throws IOException {
        System.out.printf("reconfigure database with new url = %s\n", newDatabaseUrl);
        client.doLoginWithAnyGroup(username, password).system().reconfigureDatabase(map(DATABASE_CONFIG_URL, checkNotBlank(newDatabaseUrl)));
    }

    @CliCommand(alias = {"debug", "downloadDebugInfo", "downloadBugreport", "getbugreport"})
    protected void debugInfo() throws IOException {
        byte[] data = client.doLoginWithAnyGroup(username, password).system().downloadDebugInfo();
        if (hasInteractiveConsole()) {
            File outputFile = new File(format("debug_%s.zip", dateTimeFileSuffix()));
            writeToFile(outputFile, data);
            System.out.printf("output written to %s %s\n", outputFile.getAbsolutePath(), FileUtils.byteCountToDisplaySize(outputFile.length()));
        } else {
            System.out.write(data);
        }
    }

    @CliCommand("sendbugreport")
    protected void bugreport() throws IOException {
        System.out.printf("bug report message: ");
        String message = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.println("sending bug report");
        BugReportInfo debugInfo = client.doLoginWithAnyGroup(username, password).system().sendBugReport(message);
        System.out.printf("bug report sent, file name = %s\n", debugInfo.getFileName());
    }

    @CliCommand
    protected void eval(String script) {
        if (new File(script).isFile()) {
            script = readToString(new File(script));
        }
        Object output = client.doLoginWithAnyGroup(username, password).system().eval(script);
        System.out.printf("output: %s\n", output);
    }

    @CliCommand
    protected void urlEncode(String val) {
        System.out.println(UrlEscapers.urlFormParameterEscaper().escape(val));
    }

    @CliCommand
    protected void urlDecode(String val) {
        System.out.println(URLDecoder.decode(val));
    }

    @CliCommand
    protected void test() {
        boolean ok = true;
        try {
            SessionInfo sessionInfo = client.doLoginWithAnyGroup(username, password).session().getSessionInfo();
            logger.info("current session info = {}", sessionInfo);
        } catch (Exception ex) {
            logger.error("error", ex);
            ok = false;
        }
        System.out.println("test " + (ok ? "OK" : "ERROR"));
    }

    @CliCommand
    protected void getSessionToken() {
        SessionInfo sessionInfo = client.doLoginWithAnyGroup(username, password).session().getSessionInfo();
        System.out.println(sessionInfo.getSessionToken());
    }

    @CliCommand("sessions")
    protected void getSessions() {
        List<SessionInfo> sessions = client.doLoginWithAnyGroup(username, password).session().getAllSessionsInfo();
        System.out.printf("active session count = %s\n\n", sessions.size());
        sessions.forEach(s -> System.out.printf("    %s   %-16s    last active %s ago ( %s )\n", s.getSessionToken(), s.getUsername(), toUserDuration(Duration.between(s.getLastActive(), now())), toIsoDateTime(s.getLastActive())));
    }

    @CliCommand(alias = {"lookupValues", "lookup", "getlookup"})
    protected void getLookupValues(String lookupTypeId) {
        List<LookupApi.LookupValue> values = client.doLoginWithAnyGroup(username, password).lookup().getValues(lookupTypeId);
        System.out.println("received lookup values for type: " + lookupTypeId);
        values.forEach((value) -> {
            System.out.format("\t%-16s\t%-16s\t%s\n", value.getId(), value.getCode(), value.getDescription());
        });
    }

    @CliCommand("class")
    protected void getClass(String classId) {
        ClassApi api = client.doLoginWithAnyGroup(username, password).classe();
        ClassData classeData = api.getById(classId);
        System.out.println("received class for id: " + classeData.getId() + "\n");
        printClass(classeData);
        List<AttributeData> attributes = api.getAttributes(classId);
        System.out.println("\nclass attributes: \n");
        attributes.forEach((attr) -> {
            System.out.printf("\t%-16s\t%s\n", attr.getName(), attr.getType());
        });
    }

    @CliCommand
    protected void editClass(String classId) {
        ClassApi classApi = client.doLoginWithAnyGroup(username, password).classe();
        String classJsonData = classApi.getRawJsonById(classId);
        String modifiedData = editFile(classJsonData, classId);
        if (!equal(classJsonData, modifiedData)) {
            ClassData classData = classApi.update(classId, modifiedData).getClasse();
            System.out.println("updated classe for id: " + classData.getId());
        }
    }

    @CliCommand("classes")
    protected void getClasses() {
        List<ClassData> classes = client.doLoginWithAnyGroup(username, password).classe().getAll();
        System.out.println("received class data for " + classes.size() + " classes:");
        classes.forEach((classeData) -> {
            System.out.println();
            printClass(classeData);
        });
    }

    @CliCommand
    protected void createClass(String classId, Map<String, String> data) {
        ClassData classData = cliToClassData(classId, data);
        classData = client.doLoginWithAnyGroup(username, password).classe().create(classData).getClasse();
        System.out.println("created classe for id: " + classData.getId());
        printClass(classData);
    }

    @CliCommand
    protected void updateClass(String classId, Map<String, String> data) {
        ClassData classData = cliToClassData(classId, data);
        classData = client.doLoginWithAnyGroup(username, password).classe().update(classData).getClasse();
        System.out.println("updated classe for id: " + classData.getId());
        printClass(classData);
    }

    @CliCommand
    protected void deleteClass(String classId) {
        client.doLoginWithAnyGroup(username, password).classe().deleteById(classId);
        System.out.println("deleted class for id: " + classId);
    }

    @CliCommand(alias = {"getAttr", "readAttr", "readAttribute"})
    protected void getAttribute(String classId, String attrId) {
        AttributeData attributeData = client.doLoginWithAnyGroup(username, password).classe().getAttr(classId, attrId);
        System.out.println("get attr for id: " + attributeData.getName());
        printAttribute(attributeData);
    }

    @CliCommand("createAttr")
    protected void createAttribute(String classId, Map<String, String> data) {
        AttributeRequestData requestData = paramToAttrData(data.get("name"), data);
        AttributeData attributeData = client.doLoginWithAnyGroup(username, password).classe().createAttr(classId, requestData).getAttr();
        System.out.println("created attr for id: " + attributeData.getName());
        printAttribute(attributeData);
    }

    @CliCommand("updateAttr")
    protected void updateAttribute(String classId, String attrId, Map<String, String> data) {
        AttributeRequestData requestData = paramToAttrData(attrId, data);
        AttributeData attributeData = client.doLoginWithAnyGroup(username, password).classe().updateAttr(classId, requestData).getAttr();
        System.out.println("updated attr for id: " + attributeData.getName());
        printAttribute(attributeData);
    }

    @CliCommand("deleteAttr")
    protected void deleteAttribute(String classId, String attrId) {
        client.doLoginWithAnyGroup(username, password).classe().deleteAttr(classId, attrId);
        System.out.println("deleted attr for id: " + attrId);
    }

    private void printAttribute(AttributeData attr) {
        System.out.printf("\t%-16s\t%s\n", attr.getName(), attr.getType());//TODO
//		{"success":true,"data":{"type":"string","name":"Description","description":"Description","displayableInList":true,"domainName":null,"unique":true,"mandatory":true,"inherited":true,"active":true,"index":2,"defaultValue":null,"group":"","precision":null,"scale":null,"targetClass":null,"targetType":null,"length":250,"editorType":null,"filter":null,"values":[],"writable":true,"hidden":false,"metadata":{},"classOrder":null,"ipType":null,"lookupType":null,"_id":"Description"}}

    }

    private AttributeRequestData paramToAttrData(String attrId, Map<String, String> data) {
        return SimpleAttributeRequestData.builder()
                .withActive(toBooleanOrDefault(data.get("active"), true))
                .withDescription(firstNonNull(data.get("description"), attrId))
                .withName(attrId)
                .withMode(firstNotBlank(data.get("mode"), AttributePermissionMode.APM_WRITE.name()))
                .withType(firstNonNull(trimToNull(data.get("type")), "string"))
                .withShowInGrid(toBooleanOrDefault(data.get("showInGrid"), true))
                .withUnique(toBooleanOrDefault(data.get("unique"), false))
                .withRequired(toBooleanOrDefault(data.get("required"), false))
                //TODO handle all data
                .build();
    }

    private ClassData cliToClassData(String classId, Map<String, String> data) {
        return SimpleClassData.builder()
                .withActive(toBooleanOrDefault(data.get("active"), true))
                .withName(classId)
                .withDescription(firstNonNull(trimToNull(data.get("description")), classId))
                .withParentId(emptyToNull(data.get("parent")))
                .withSuperclass(toBooleanOrDefault(data.get("prototype"), false))
                .withType(firstNonNull(trimToNull(data.get("type")), "standard"))
                .build();
    }

    private void printClass(ClassData classeData) {
        map("name", classeData.getName(),
                "description", classeData.getDescription(),
                "type", classeData.getType(),
                "parent", classeData.getParentId(),
                "superclass", classeData.isSuperclass(),
                "active", classeData.isActive()).entrySet().forEach((entry) -> {
            System.out.format("%-16s\t%-32s\n", entry.getKey(), entry.getValue());
        });
    }

    @CliCommand
    protected void getCard(String classId, String cardId) {
        Card card = client.doLoginWithAnyGroup(username, password).card().getCard(classId, cardId);
        System.out.println("received card for id: " + card.getCardId());
        card.getAttributes().entrySet().forEach((entry) -> {
            System.out.format("\t%-32s\t%-32s\n", entry.getKey(), entry.getValue());
        });
    }

    @CliCommand("cards")
    protected void getCards(String classeId) {
        List<Card> cards = client.doLoginWithAnyGroup(username, password).card().getCards(classeId);
        System.out.println("received card values for classe: " + classeId);
        cards.forEach((card) -> {
            System.out.format("\t%-10s\t%s\n", card.getCardId(), card.getDescription());
        });
    }

    @CliCommand("query")
    protected void queryCards(String filter, String sort, String offset, String limit) {
        List<Card> cards = client.doLoginWithAnyGroup(username, password).card().queryCards()
                .filter(filter)
                .sort(sort)
                .limit(toIntegerOrNull(limit))
                .offset(toIntegerOrNull(offset))
                .getCards();
        System.out.println("received card values for query");
        cards.forEach((card) -> {
            System.out.format("\t%-10s\t%s\n", card.getCardId(), card.getDescription());
        });
    }

    @CliCommand
    protected void deleteCard(String classId, String cardId) {
        client.doLoginWithAnyGroup(username, password).card().deleteCard(classId, cardId);
        System.out.println("deleted card for id: " + cardId);
    }

    @CliCommand
    protected void createCard(String classId, Map<String, String> data) {
        Card card = client.doLoginWithAnyGroup(username, password).card().createCard(classId, new SimpleCard(data)).getCard();
        System.out.println("created card for id: " + card.getCardId());
    }

    @CliCommand
    protected void getAttachments(String classId, String cardId) {
        List<Attachment> attachments = client.doLoginWithAnyGroup(username, password).attachment().getCardAttachments(classId, cardId);
        attachments.forEach((attachment) -> {
            System.out.printf("%-32s\t%-32s\t%-32s\n", attachment.getId(), attachment.getFileName(), attachment.getVersion());
        });
    }

    @CliCommand
    protected void getAttachmentHistory(String classId, String cardId, String attachmentId) {
        List<Attachment> attachments = client.doLoginWithAnyGroup(username, password).attachment().getAttachmentHistory(classId, cardId, attachmentId);
        attachments.forEach((attachment) -> {
            System.out.printf("%-32s\t%-32s\n", attachment.getFileName(), attachment.getVersion());
        });
    }

    @CliCommand
    protected void createAttachment(String classId, String cardId, String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        checkArgument(file.isFile(), "file %s is not a valid file", file);
        Attachment attachment = client.doLoginWithAnyGroup(username, password).attachment().createCardAttachment(classId, cardId, file.getName(), new FileInputStream(file)).getAttachment();
        System.out.printf("created attachment: %s\n", attachment);
    }

    @CliCommand
    protected void updateAttachment(String classId, String cardId, String attachmentId, String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        checkArgument(file.isFile(), "file %s is not a valid file", file);
        Attachment attachment = client.doLoginWithAnyGroup(username, password).attachment().updateCardAttachment(classId, cardId, attachmentId, file.getName(), new FileInputStream(file)).getAttachment();
        System.out.printf("updated attachment: %s\n", attachment);
    }

    @CliCommand
    protected void deleteattachment(String classId, String cardId, String attachmentId) {
        client.doLoginWithAnyGroup(username, password).attachment().deleteCardAttachment(classId, cardId, attachmentId);
        System.out.printf("OK\n");
    }

    @CliCommand
    protected void getAttachment(String classId, String cardId, String attachmentId) throws IOException {
        AttachmentData data = client.doLoginWithAnyGroup(username, password).attachment().download(classId, cardId, attachmentId).getData();
        IOUtils.write(data.toByteArray(), System.out);
    }

    @CliCommand
    protected void getAttachmentPreview(String classId, String cardId, String attachmentId) throws IOException {
        AttachmentPreview data = client.doLoginWithAnyGroup(username, password).attachment().preview(classId, cardId, attachmentId).getPreview();
        if (data.hasPreview()) {
            IOUtils.write(data.toByteArray(), System.out);
        } else {
            System.err.println("NO PREVIEW AVAILABLE");
            System.exit(1);//TOOD set return value, and do clean shutdown
        }
    }

    @CliCommand
    protected void exportAttachments() throws IOException {
        BigByteArray data = client.doLoginWithAnyGroup(username, password).attachment().exportAllDocumentsToZipFile();
        if (hasInteractiveConsole()) {
            File outputFile = new File(format("dms_export_%s.zip", dateTimeFileSuffix()));
            writeToFile(outputFile, data);
            System.out.printf("output written to %s %s\n", outputFile.getAbsolutePath(), FileUtils.byteCountToDisplaySize(outputFile.length()));
        } else {
            copyLarge(data.toInputStream(), System.out);
        }
    }

    @CliCommand
    protected void importFromDms() {
        client.doLoginWithAnyGroup(username, password).system().importFromDms();
        System.out.println("OK");
    }

    @CliCommand
    protected void getProcesses() {
        client.doLoginWithAnyGroup(username, password).workflow().getPlans().forEach((plan) -> {
            System.out.format("%-32s\t%-6s\t%-32s\n", plan.getId(), nullToEmpty(plan.getProvider()), plan.getDescription());
        });
    }

    @CliCommand
    protected void getProcess(String processId) {
        WokflowApi workflow = client.doLoginWithAnyGroup(username, password).workflow();
        WokflowApi.PlanInfo plan = workflow.getPlan(processId);
        List<WokflowApi.PlanVersionInfo> planVersions = workflow.getPlanVersions(processId);
        System.out.printf("plan %s (%s):\n", plan.getId(), plan.getDescription());
        planVersions.forEach((version) -> {
            System.out.printf("\t%1s  %-16s\t%-16s\t%s\n", version.isDefault() ? "*" : "", version.getProvider(), version.getVersion(), version.getPlanId());
        });
    }

    @CliCommand
    protected void getProcessTemplate(String processId) {
        String xpdlTemplate = client.doLoginWithAnyGroup(username, password).workflow().getXpdlTemplate(processId);
        System.out.println(xpdlTemplate);
    }

    @CliCommand(alias = {"uploadProcessXpdl", "setProcessXpdl"})
    protected void uploadProcess(String processId, String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        checkArgument(file.isFile(), "file %s is not a valid file", file);
        WokflowApi.PlanVersionInfo planVersionInfo = client.doLoginWithAnyGroup(username, password).workflow().uploadPlanVersion(processId, new FileInputStream(file)).getPlanVersionInfo();
        System.out.printf("created plan: %s\n", planVersionInfo);
    }

    @CliCommand
    protected void migrateProcess(String processId, String fileName) throws FileNotFoundException {
        migrateProcess(processId, fileName, RIVER);
    }

    @CliCommand
    protected void migrateProcess(String processId) throws FileNotFoundException {
        migrateProcess(processId, null);
    }

    @CliCommand
    protected void migrateProcess(String processId, @Nullable String fileName, String provider) throws FileNotFoundException {
        WokflowApi workflow = client.doLoginWithAnyGroup(username, password).workflow();
        if (isBlank(fileName)) {
            workflow.migrateProcess(processId);
        } else {
            File file = new File(fileName);
            checkArgument(file.isFile(), "file %s is not a valid file", file);
            workflow.uploadPlanVersionAndMigrateProcess(processId, new FileInputStream(file));
        }
        System.out.printf("migrated process to provider: %s\n", RIVER);
    }

    @CliCommand
    protected void downloadProcess(String classId, String planId) {
        System.out.println(client.doLoginWithAnyGroup(username, password).workflow().downloadPlanVersion(classId, planId));
    }

    @CliCommand(alias = {"getProcessXpdl"})
    protected void downloadProcessXpdl(String classId) {
        WokflowApi workflow = client.doLoginWithAnyGroup(username, password).workflow();
        List<WokflowApi.PlanVersionInfo> versions = workflow.getPlanVersions(classId);
        System.out.println(workflow.downloadPlanVersion(classId, versions.iterator().next().getPlanId()));
    }

    @CliCommand(alias = {"flowgraph", "fg"})
    protected void getFlowGraph(String classId, String cardId) throws IOException {
        byte[] data = client.doLoginWithAnyGroup(username, password).workflow().downloadFlowGraph(classId, toLong(cardId));
        File file = new File(tempDir(), "file.png");
        FileUtils.writeByteArrayToFile(file, data);
        Desktop.getDesktop().open(file);
    }

    @CliCommand(alias = {"simplifiedflowgraph", "sfg"})
    protected void getSimplifiedFlowGraph(String classId, String cardId) throws IOException {
        byte[] data = client.doLoginWithAnyGroup(username, password).workflow().downloadSimplifiedFlowGraph(classId, toLong(cardId));
        File file = new File(tempDir(), "file.png");
        FileUtils.writeByteArrayToFile(file, data);
        Desktop.getDesktop().open(file);
    }

    @CliCommand
    protected void uploadCustomPageFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        checkArgument(file.isFile(), "file %s is not a valid file", file);
        uploadCustomPage(new FileInputStream(file));
    }

    @CliCommand
    protected void uploadCustomPageDir(String dirName) throws FileNotFoundException {
        File dir = new File(dirName);
        checkArgument(dir.isDirectory(), "dir %s is not a valid directory", dir);
        byte[] data = dirToZip(dir);
        uploadCustomPage(new ByteArrayInputStream(data));
    }

    private void uploadCustomPage(InputStream in) {
        CustomPageInfo customPageInfo = client.doLoginWithAnyGroup(username, password).customPage().upload(in).getCustomPageInfo();
        System.out.printf("uploaded custom page: %s %s (%s)\n", customPageInfo.getId(), customPageInfo.getName(), customPageInfo.getDescription());
    }

    @CliCommand(alias = {"startprocess"})
    protected void startFlow(String processId, Map<String, Object> data) {
        FlowDataAndStatus flow = client.doLoginWithAnyGroup(username, password).workflow().start(processId, SimpleFlowData.builder().withAttributes(data).build()).getFlowData();
        printFlowActionOutput("started", flow);
    }

    @CliCommand
    protected void completeTask(String processId, String instanceId, String taskId, Map<String, Object> data) {
        FlowDataAndStatus flow = client.doLoginWithAnyGroup(username, password).workflow().advance(processId, instanceId, taskId, SimpleFlowData.builder().withAttributes(data).build()).getFlowData();
        printFlowActionOutput("advanced", flow);
    }

    private void printFlowActionOutput(String action, FlowDataAndStatus flow) {
        System.out.printf("%s process instance with id: %s\n\tflow status is: %s\n\ttasklist size: %s\n", action, flow.getFlowCardId(), flow.getFlowStatus(), flow.getTaskList().size());
        flow.getTaskList().forEach((task) -> {
            System.out.println();
            printTaskDetail(task);
        });
    }

    @CliCommand(alias = {"getprocessinstance", "flow"})
    protected void getFlow(String processId, String instanceId) {
        WokflowApi.FlowData walk = client.doLoginWithAnyGroup(username, password).workflow().get(processId, instanceId);
        System.out.println("received process instance for id: " + walk.getFlowId());
        System.out.printf("status is: %s\n", walk.getStatus());
        walk.getAttributes().entrySet().forEach((entry) -> {
            System.out.format("\t%-32s\t%-32s\n", entry.getKey(), entry.getValue());
        });
    }

    @CliCommand(alias = {"tasks", "tasklist"})
    protected void getTaskList(String processId, String instanceId) {
        List<WokflowApi.TaskInfo> list = client.doLoginWithAnyGroup(username, password).workflow().getTaskList(processId, instanceId);
        System.out.println("received process instance activities for id: " + instanceId);
        list.forEach((activity) -> {
            System.out.format("\t%-48s\t%-32s\n", activity.getId(), activity.getDescription());
        });
    }

    @CliCommand(alias = {"task"})
    protected void getTask(String processId, String instanceId, String taskId) {
        WokflowApi.TaskDetail task = client.doLoginWithAnyGroup(username, password).workflow().getTask(processId, instanceId, taskId);
        printTaskDetail(task);
    }

    @CliCommand(alias = {"getstarttask"})
    protected void getStartProcessTask(String processId) {
        WokflowApi.TaskDetail task = client.doLoginWithAnyGroup(username, password).workflow().getStartProcessTask(processId);
        printTaskDetail(task);
    }

    private void printTaskDetail(TaskDetail task) {
//		System.out.format("received task detail for task %s (%s)\n", taskId, task.getDescription());
        System.out.format("task detail for task %s (%s)\n", task.getId(), task.getDescription());
        task.getParams().forEach((param) -> {
            System.out.format("\t%-32s\trequired = %-5s\twritable = %-5s\taction = %-5s\ttype = %-10s\t%-32s", param.getName(), param.isRequired(), param.isWritable(), param.isAction(), param.getDetail().getType(), param.getDetail().targetInfoToString());
            if (param.getDetail().hasFilter()) {
                System.out.printf("\tfilter = %s", param.getDetail().getFilter());
            }
            System.out.println();
        });

    }

    @CliCommand(alias = {"loggers"})
    protected void getLoggers() {
        List<LoggerInfo> loggers = client.doLoginWithAnyGroup(username, password).system().getLoggers();
        loggers.forEach((loggerInfo) -> {
            System.out.format("%-32s\t%-32s\n", loggerInfo.getCategory(), loggerInfo.getLevel());
        });
    }

    @CliCommand
    protected void setLogger(String loggerCategory, String loggerLevel) {
        client.doLoginWithAnyGroup(username, password).system().setLogger(loggerCategory, loggerLevel);
        System.out.println("set logger " + loggerCategory + " to level " + loggerLevel);
    }

    @CliCommand
    protected void deleteLogger(String loggerCategory) {
        client.doLoginWithAnyGroup(username, password).system().deleteLogger(loggerCategory);
        System.out.println("removed logger " + loggerCategory);
    }

    @CliCommand("tail")
    protected void streamLogMessages() throws InterruptedException, ExecutionException {
        client.doLoginWithAnyGroup(username, password).system().streamLogMessages(x -> {
            System.out.println(x.getLine());
        }).get();
    }

    @CliCommand(alias = {"configs"})
    protected void getConfigs() {
        Map<String, String> config = client.doLoginWithAnyGroup(username, password).system().getConfig();
        config.entrySet().forEach((entry) -> {
            System.out.format("%-70s\t%s\n", abbreviate(entry.getKey(), 70), abbreviate(entry.getValue(), 100));
        });
    }

    @CliCommand(alias = {"config"})
    protected void getConfig(String key) {
        String value = client.doLoginWithAnyGroup(username, password).system().getConfig(key);
        System.out.println(value);
    }

    @CliCommand(alias = {"configinfo", "configdefinition", "configdesc", "configinfos", "configdefinitions", "configdescs", "getconfiginfos", "getconfigdefinitions", "getconfigdescs", "configsinfo", "configsdefinition", "configsdesc"})
    protected void getConfigDesc() {
        Map<String, ConfigDefinition> configDefinitions = client.doLoginWithAnyGroup(username, password).system().getConfigDefinitions();
        System.out.format("%-70s%-10s%-10s   %-30s   %s\n\n", "key", "category", "location", "default", "description");
        configDefinitions.entrySet().forEach((entry) -> {
            System.out.format("%-70s%-10s%-10s   %-30s   %s\n", abbreviate(entry.getKey(), 70), serializeEnum(entry.getValue().getCategory()), serializeEnum(entry.getValue().getLocation()), abbreviate(entry.getValue().getDefaultValue(), 30), multilineWithOffset(entry.getValue().getDescription(), 60, 126));
        });
    }

    @CliCommand(alias = {"exportconfig", "exportconfigs"})
    protected void getConfigProperties() throws IOException {
        Map<String, String> config = filterConfigs(client.doLoginWithAnyGroup(username, password).system().getConfig());
        Properties properties = new Properties();
        properties.putAll(Maps.filterEntries(config, (entry) -> entry.getValue() != null));
        properties.store(System.out, null);//TODO sort by key
    }

    @CliCommand("exportconfigsql")
    protected void getConfigSql() throws IOException {
        Map<String, String> config = filterKeys(filterConfigs(client.doLoginWithAnyGroup(username, password).system().getConfig()), k -> !k.startsWith(DATABASE_CONFIG_NAMESPACE));
        config.forEach((k, v) -> System.out.printf("SELECT _cm3_system_config_set('%s','%s');\n", systemToSqlExpr(k), systemToSqlExpr(v)));
    }

    @CliCommand(alias = {"importconfig", "importconfigs"})
    protected void setConfigProperties(String propertyFileOrUrlParams) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertyFileOrUrlParams));
        System.out.printf("import config from file = %s :\n", propertyFileOrUrlParams);
        doImportProperties(properties);
    }

    @CliCommand
    protected void setConfigs(String configs) throws IOException {
        doImportProperties(toProperties(decodeUrlParams(configs)));
    }

    @CliCommand(alias = {"importconfig", "importconfigs"})
    protected void setConfigProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(System.in);
        System.out.printf("import config from stdin\n");
        doImportProperties(properties);
    }

    private void doImportProperties(Properties properties) {
        properties.forEach((key, value) -> {
            System.out.printf("\tset config %s = %s\n", key, value);
        });
        client.doLoginWithAnyGroup(username, password).system().setConfigs(map(properties));
    }

    @CliCommand
    protected void setConfig(String key, String value) {
        client.doLoginWithAnyGroup(username, password).system().setConfig(key, value);
        System.out.println("set config " + key + " = " + value);
    }

    @CliCommand
    protected void deleteConfig(String key) {
        client.doLoginWithAnyGroup(username, password).system().deleteConfig(key);
        System.out.printf("delete config = %s\n", key);
    }

    @CliCommand(alias = {"reloadconfig"})
    protected void reloadConfig() {
        client.doLoginWithAnyGroup(username, password).system().reloadConfig();
        System.out.println("OK");
    }

    private static Map<String, String> filterConfigs(Map<String, String> configs) {//TODO refactor this...handle kkepconfigfile server side (?)
        return filterKeys(configs, (k) -> !k.endsWith("keepconfigfile"));
    }

    @CliCommand
    protected void editConfig() throws IOException {//TODO merge code with getConfigProperties()
        SystemApi system = client.doLoginWithAnyGroup(username, password).system();
        Map<String, String> config = filterConfigs(system.getConfig());
        Map<String, ConfigDefinition> defs = system.getConfigDefinitions();
        StringBuilder editableConfig = new StringBuilder();
        String[] curPrefix = {""};
        defs.keySet().stream().sorted().forEach((key) -> {
            ConfigDefinition def = defs.get(key);
            String prefix = key.replaceFirst("org.cmdbuild.([^.]+).*", "$1");
            if (!equal(prefix, curPrefix[0])) {
                editableConfig.append(format("\n# === %s ===\n\n", prefix.toUpperCase()));
                curPrefix[0] = prefix;
            }
            if (def.hasDescription()) {
                editableConfig.append("#\n# ").append(def.getDescription()).append(":\n");
            }
            if (config.containsKey(key)) {
                editableConfig.append(format("%s=%s", key, config.get(key))).append("\n");
            } else {
                editableConfig.append(format("#%s=%s", key, nullToEmpty(def.getDefaultValue()))).append("\n");
            }
            if (def.hasDescription()) {
                editableConfig.append("#\n");
            }
        });

        editableConfig.append(format("\n# === %s ===\n\n", "OTHER"));
        config.keySet().stream().filter(not(defs::containsKey)).sorted().forEach((key) -> {
            editableConfig.append(format("%s=%s", key, config.get(key))).append("\n");
        });

        editableConfig.append("\n\n");
        String editedConfig = editFile(editableConfig.toString(), "system config");
        Properties properties = new Properties();
        properties.load(new StringReader(editedConfig));
        Map<String, String> toSet = map();
        properties.forEach((key, value) -> {
            if (!equal(value, config.get((String) key))) {
                System.out.printf("set config %s = %s\n", key, value);
                toSet.put((String) key, (String) value);
            }
        });
        if (!toSet.isEmpty()) {
            system.setConfigs(toSet);
        }
        config.keySet().stream().filter((k) -> !k.endsWith("keepconfigfile")).filter(not(properties::containsKey)).forEach((key) -> {//TODO handle kkepconfigfile server side (?)
            System.out.printf("delete config = %s\n", key);
            system.deleteConfig(key);
        });
    }

    @CliCommand
    protected void mark() {
        String mark = client.doLoginWithAnyGroup(username, password).audit().mark();
        System.out.printf("audit mark: %s\n", mark);
    }

    @CliCommand(alias = {"lastrequests", "glr"})
    protected void getLastRequests() {
        getLastRequests(25);
    }

    @CliCommand(alias = {"lastrequests", "glr"})
    protected void getLastRequests(int limit) {
        List<RequestInfo> requests = client.doLoginWithAnyGroup(username, password).audit().getLastRequests(limit);
        printRequests(requests);
    }

    @CliCommand(alias = {"getRequests", "requests", "gr"})
    protected void getRequestsSince(String mark) {
        checkNotBlank(mark, "missing mark");
        List<RequestInfo> requests = client.doLoginWithAnyGroup(username, password).audit().getRequestsSince(mark);
        printRequests(requests);
    }

    @CliCommand(alias = {"lasterrors", "gle"})
    protected void getLastErrors() {
        getLastErrors(25);
    }

    @CliCommand(alias = {"lasterrors", "gle"})
    protected void getLastErrors(int limit) {
        List<RequestInfo> requests = client.doLoginWithAnyGroup(username, password).audit().getLastErrors(limit);
        printRequests(requests);
    }

    private void printRequests(List<RequestInfo> requests) {
        System.out.printf("timestamp (%6s) requestId                       actionId                    sessionId                   user               elap  method response path                                               query    \n", getReadableTimezoneOffset());
        requests.stream().filter((request) -> !request.getActionId().matches("cli_restws_(get)?requests|cli_restws_mark")).forEach((request) -> {
            System.out.printf("%-18s %-24s %-32s %-24s %-16s  %6s %-6s %9s %-50s %s %s\n",
                    toUserReadableDateTime(request.getTimestamp()),
                    abbreviate(request.getRequestId(), 24),
                    abbreviate(request.getActionId(), 32),
                    nullToEmpty(request.getSessionId()),
                    nullToEmpty(request.getUser()),
                    request.isCompleted() ? (request.getElapsedTimeMillis() + "ms") : "",
                    request.getMethod(),
                    responseCode(request),
                    request.getPath(),
                    request.getQuery(),
                    request.isSoap() ? nullToEmpty(request.getSoapActionOrMethod()) : "");
        });
    }

    @CliCommand(alias = {"request", "gr"})
    protected void getRequest(String requestId) {
        RequestData requestData = client.doLoginWithAnyGroup(username, password).audit().getRequestData(requestId.replaceAll("[.]", ""));
        String payload = prettifyIfJson(prettifyIfXml(nullToEmpty(requestData.getBestPlaintextPayload())));
        String response = prettifyIfJson(prettifyIfXml(nullToEmpty(requestData.getBestPlaintextResponse())));
        System.out.printf("actionId   : %s\n", requestData.getActionId());
        System.out.printf("requestId  : %s\n", requestData.getRequestId());
        System.out.printf("sessionId  : %s (%s)\n\n", requestData.getSessionId(), requestData.getUser());

        System.out.printf("node       : %s\n", requestData.getNodeId());
        System.out.printf("client     : %s (%s)\n\n", requestData.getClient(), requestData.getUserAgent());

        System.out.printf("request    : %s %s\n", requestData.getMethod(), requestData.getPathWithQuery());
        System.out.printf("response   : %s\n\n", responseCode(requestData));

        if (isNotBlank(requestData.getQuery())) {
            try {
                decodeUrlParams(requestData.getQuery()).forEach((k, v) -> System.out.printf("query param: %10s = %s\n", k, v));
            } catch (Exception ex) {
                System.out.printf("query params: %s\n", requestData.getQuery());
            }
            System.out.println();
        }

        System.out.printf("=== payload (%s %s) ===\n%s\n", nullToEmpty(requestData.getPayloadContentType()), byteCountToDisplaySize(requestData.getPayloadSize()), payload);
        System.out.printf("=== response (%s %s) ===\n%s\n===         end         ===\n", nullToEmpty(requestData.getResponseContentType()), byteCountToDisplaySize(requestData.getResponseSize()), response);

        if (!requestData.getErrorOrWarningEvents().isEmpty()) {
            System.out.printf("\n\n=== errors and messages ===\n\n");
            requestData.getErrorOrWarningEvents().forEach((e) -> {
                System.out.printf("level   :   %s\n", e.getLevel());
                System.out.printf("message :   %s\n\n", e.getMessage());
                System.out.printf("stacktrace :   %s\n", e.getStackTrace());
            });
            System.out.printf("===        end          ===\n");
        }

        if (requestData.hasLogs()) {
            System.out.printf("\n\n===      logs        ===\n%s\n===         end logs    ===\n", requestData.getLogs());
        }

        System.out.println();

        String payloadForCurl;
        if (requestData.isBinaryPayload() || requestData.getPayloadSize() > 250) {
            File tempFile = new File(javaTmpDir(), format("%s.file", randomId(6)));
            writeToFile(tempFile, requestData.getBinaryPayload());
            payloadForCurl = format("@%s", tempFile.getAbsolutePath());
            if (requestData.getPayloadSize() < 10000) {
                System.out.printf("echo '%s' | base64 -d > '%s'\n\n", Base64.encodeBase64String(requestData.getBinaryPayload()), tempFile.getAbsolutePath());
            }
        } else {
            payloadForCurl = requestData.getPayload();
        }

        System.out.println(buildCurlCli(requestData.getSessionId(), requestData.getPathWithQuery(), requestData.getMethod(), payloadForCurl, requestData.getPayloadContentType(), !requestData.isSoap()));
    }

    @CliCommand(alias = {"lastjoberrors", "lje"})
    protected void getLastJobErrors() {
        getLastJobErrors(25);
    }

    @CliCommand(alias = {"lastjoberrors", "lje"})
    protected void getLastJobErrors(int limit) {
        List<JobRun> runs = client.doLoginWithAnyGroup(username, password).system().getLastJobErrors(limit);
        printRuns(runs);
    }

    @CliCommand(alias = {"lastjobruns", "ljr"})
    protected void getLastJobRuns() {
        getLastJobRuns(25);
    }

    @CliCommand(alias = {"lastjobruns", "ljr"})
    protected void getLastJobRuns(int limit) {
        List<JobRun> runs = client.doLoginWithAnyGroup(username, password).system().getLastJobRuns(limit);
        printRuns(runs);
    }

    private void printRuns(List<JobRun> runs) {
        System.out.printf("timestamp (%6s)   runId   job                                                              status       elap\n\n", getReadableTimezoneOffset());
        runs.stream().forEach((run) -> {
            System.out.printf("%-18s %12s %-64s %-12s %ss\n",
                    toUserReadableDateTime(run.getTimestamp()),
                    run.getId(),
                    run.getJobCode(),
                    serializeJobRunStatus(run.getJobStatus()),
                    Optional.ofNullable(run.getElapsedTime()).map(t -> (Object) (t / 1000d)).orElse(""));
        });
    }

    @CliCommand(alias = {"jobrun", "jr"})
    protected void getJobRun(String id) {
        JobRun run = client.doLoginWithAnyGroup(username, password).system().getJobRun(parseLong(id));

        System.out.printf("timestamp   : %s\n", toUserReadableDateTime(run.getTimestamp()));
        System.out.printf("runId  : %s\n", run.getId());
        System.out.printf("job : %s\n", run.getJobCode());
        System.out.printf("status  : %s\n", serializeJobRunStatus(run.getJobStatus()));

        if (!run.getErrorOrWarningEvents().isEmpty()) {
            System.out.printf("\n\n=== errors and messages ===\n\n");
            run.getErrorOrWarningEvents().forEach((e) -> {
                System.out.printf("level   :   %s\n", e.getLevel());
                System.out.printf("message :   %s\n\n", e.getMessage());
                System.out.printf("stacktrace :   %s\n", e.getStackTrace());
            });
            System.out.printf("===        end          ===\n");
        }
    }

    private String responseCode(RequestInfo request) {
        return format("%s%s", request.hasError() ? "ERROR " : "", request.getStatusCode()).trim();
    }

    @CliCommand("patches")
    protected void getPatches() {
        client.system().getPatches().forEach((patch) -> {
            System.out.printf("%-24s %-24s %s\n", patch.getCategory(), patch.getName(), nullToEmpty(patch.getDescription()));
        });
    }

    @CliCommand
    protected void applyPatches() {
        client.system().applyPatches();
        System.out.println("OK");
    }

    @CliCommand
    protected void dropAllCaches() {
        client.doLoginWithAnyGroup(username, password).system().dropAllCaches();
        System.out.println("OK");
    }

    @CliCommand
    protected void reload() {
        client.doLoginWithAnyGroup(username, password).system().reload();
        System.out.println("OK");
    }

    @CliCommand
    protected void dropCache(String cacheId) {
        client.doLoginWithAnyGroup(username, password).system().dropCache(cacheId);
        System.out.println("OK");
    }

    @CliCommand
    protected void upload(String targetPathAndFileName, String fileNameOrBase64Content) throws FileNotFoundException, IOException {
        byte[] data;
        File file = new File(fileNameOrBase64Content);
        if (!file.exists() && isPacked(fileNameOrBase64Content)) {
            data = unpackBytes(fileNameOrBase64Content);
        } else if (!file.exists() && isBase64(fileNameOrBase64Content)) {
            data = Base64.decodeBase64(fileNameOrBase64Content);
        } else {
            checkArgument(file.isFile(), "file %s is not a valid file", file);
            data = toByteArray(file);
        }
        client.doLoginWithAnyGroup(username, password).withUploadProgressListener(prepareUploadProgressListener()).uploads().upload(targetPathAndFileName, data);
        System.out.printf("completed upload of file = %s\n", targetPathAndFileName);
    }

    @CliCommand
    protected void upload(String zipFileNameOrDir) throws FileNotFoundException, IOException {
        File file = new File(zipFileNameOrDir);
        byte[] data;
        if (file.isDirectory()) {
            System.out.printf("upload files from dir = %s\n", file.getAbsolutePath());
            data = CmZipUtils.dirToZip(file);
        } else {
            checkArgument(zipFileNameOrDir.endsWith(".zip"), "file %s is not a valid zip file", file);
            System.out.printf("upload files from zip = %s\n", file.getAbsolutePath());
            data = toByteArray(file);
        }
        client.doLoginWithAnyGroup(username, password).withUploadProgressListener(prepareUploadProgressListener()).uploads().uploadMany(data);
    }

    @CliCommand
    protected void download(String path) throws IOException {
        byte[] data = client.doLoginWithAnyGroup(username, password).uploads().download(path).toByteArray();
        IOUtils.write(data, System.out);
    }

    @CliCommand("exportUploads")
    protected void downloadAll() throws IOException {
        byte[] data = client.doLoginWithAnyGroup(username, password).uploads().downloadAll().toByteArray();
        if (hasInteractiveConsole()) {
            File outputFile = new File(format("uploads_export_%s.zip", dateTimeFileSuffix()));
            writeToFile(outputFile, data);
            System.out.printf("output written to %s %s\n", outputFile.getAbsolutePath(), FileUtils.byteCountToDisplaySize(outputFile.length()));
        } else {
            System.out.write(data);
        }
    }

    @CliCommand("exportUploadsToDir")
    protected void downloadAllToDir(String dir) throws IOException {
        File target = new File(checkNotBlank(dir));
        if (!target.exists()) {
            target.mkdirs();
        }
        checkArgument(target.isDirectory());
        byte[] data = client.doLoginWithAnyGroup(username, password).uploads().downloadAll().toByteArray();
        unzipToDir(data, target);
        System.out.printf("exported uploads to dir = %s\n", target.getAbsolutePath());
    }

    @CliCommand
    protected void printReport(String reportId, String ext, Map<String, String> params) throws IOException {
        byte[] data = client.doLoginWithAnyGroup(username, password).report().executeAndDownload(reportId, ReportFormat.valueOf(ext.toUpperCase()), (Map) params).toByteArray();
        IOUtils.write(data, System.out);
    }

    @CliCommand
    protected void downloadReport(String reportId) throws IOException {
        printReport(reportId, "zip", emptyMap());
    }

    @CliCommand
    protected void createReport(String code, String reportTemplateDir) throws IOException {
        checkArgument(new File(reportTemplateDir).isDirectory(), "file %s is not a directory", reportTemplateDir);
        List<File> files = list(new File(reportTemplateDir).listFiles());
        System.out.printf("create report = %s files = %s\n", code, files.stream().map(File::getName).collect(joining(",")));
        ReportInfo info = ReportInfoImpl.builder()
                .withActive(true)
                .withCode(code)
                .withDescription(code)
                .build();
        ReportInfo reportInfo = client.doLoginWithAnyGroup(username, password).report().createReport(info, files);
        System.out.printf("created report = %s %s\n", reportInfo.getId(), reportInfo.getCode());
    }

    @CliCommand
    protected void uploadReport(String reportId, String reportTemplateDir) throws IOException {
        List<File> files = list(new File(reportTemplateDir).listFiles());
        System.out.printf("upload report template for report = %s files = %s\n", reportId, files.stream().map(File::getName).collect(joining(",")));
        client.doLoginWithAnyGroup(username, password).report().uploadReportTemplate(toLong(reportId), files);
    }

    @CliCommand("upgrade")
    protected void upgradeWebapp(String fileName) throws FileNotFoundException, IOException {
        System.out.println("preparing upgrade, check war file");
        File file = new File(fileName);
        checkArgument(file.isFile(), "file %s is not a valid file", file);
        logger.debug("load war data from file = {}", file.getAbsolutePath());
        byte[] data = toByteArray(file);
        BuildInfo buildInfo = validateWarData(data);
        System.out.printf("upgrade cmdbuild webapp, load war file = %s rev %s\n", file.getAbsolutePath(), buildInfo.getCommitInfo());
        ZonedDateTime begin = now();
        client.doLoginWithAnyGroup(username, password).withUploadProgressListener(prepareUploadProgressListener()).system().upgradeWebapp(new ByteArrayInputStream(data));
        System.out.println("execute upgrade, wait for restart");
        Stopwatch stopwatch = Stopwatch.createStarted();
        while (true) {
            try {
                sleepSafe(1000);
                SystemApi system = client.doLoginWithAnyGroup(username, password).system();
                if (equal(system.getStatus(), SYST_READY)//TODO handle patch required
                        && equal(buildInfo.getCommitInfo(), system.getSystemInfo().get("build_info"))
                        && now().minus(toDuration(system.getSystemInfo().get("uptime"))).isAfter(begin)) {
                    status();
                    return;
                } else {
                    logger.debug("system not ready");
                }
                checkArgument(stopwatch.elapsed(TimeUnit.SECONDS) < 60, "startup timeout: system failed to restart/upgrade in 60 seconds");
            } catch (Exception ex) {
                logger.debug("system not ready, error = {}", ex.toString());
            }
        }
    }

    @CliCommand("upgrade")
    protected void upgradeWebapp() throws FileNotFoundException, IOException {
        upgradeWebapp(getWarFile().getAbsolutePath());
    }

    @CliCommand(alias = {"recreate", "importDatabase", "importDb"})
    protected void recreateDatabase(String fileName) throws FileNotFoundException, IOException {
        doImportDb(fileName, false);
    }

    @CliCommand(alias = {"importDb_freezeSessions", "importDb_fs", "importDbfs"})
    protected void importDbFreezeSessions(String fileName) throws FileNotFoundException, IOException {
        doImportDb(fileName, true);
    }

    private void doImportDb(String fileName, boolean freezesessions) throws FileNotFoundException, IOException {
        File file = getDbdumpFile(fileName);
        System.out.printf("recreate cmdbuild database, import db from file = %s (freeze sessions = %s)\n", file.getAbsolutePath(), freezesessions);
        file = prepareDumpFile(file);
        client.doLoginWithAnyGroup(username, password).withUploadProgressListener(prepareUploadProgressListener()).system().recreateDatabase(new FileInputStream(file), freezesessions);
        System.out.println("done");

    }

    @CliCommand("stop")
    protected void stopTomcat() {
        client.doLoginWithAnyGroup(username, password).system().stop();
    }

    @CliCommand("restart")
    protected void restartTomcat() {
        client.doLoginWithAnyGroup(username, password).system().restart();
    }

    @CliCommand("broadcast")
    protected void sendBroadcastMessage() throws IOException {
        System.out.print("message: ");
        String message = new BufferedReader(new InputStreamReader(System.in)).readLine();
        client.doLoginWithAnyGroup(username, password).system().sendBroadcast(message);
        System.out.println("message sent");
    }

    @CliCommand("broadcast")
    protected void sendBroadcastMessage(String message) {
        System.out.printf("send broadcast message: %s\n", checkNotBlank(message));
        client.doLoginWithAnyGroup(username, password).system().sendBroadcast(message);
        System.out.println("message sent");
    }

    private StreamProgressListener prepareUploadProgressListener() {
        AtomicBoolean isFirst = new AtomicBoolean(true);
        return (e) -> {
            if (!isFirst.getAndSet(false)) {
                System.out.print("\033[1A\033[2K");
            }
            System.out.printf("  upload progress: %s\n", e.getProgressDescriptionDetailed());
        };
    }

    private String buildCurlCli(String authToken, String service) {
        return buildCurlCli(authToken, service, "get", null, null, true);
    }

    private String buildCurlCli(String authToken, String service, String method, @Nullable String payload, @Nullable String contentType, boolean isJson) {
        String methodParam, otherParams = "";
        switch (method.toUpperCase()) {
            case "GET":
                methodParam = "";
                break;
            default:
                methodParam = format("-X %s", method.toUpperCase());
                if (payload != null) {
                    otherParams += format(" --data-binary '%s'", payload);
                }
        }
        if (isNotBlank(contentType)) {
            otherParams += format(" -H'Content-Type:%s'", contentType);
        }

        return format("cmdbuild_auth_token='%s'\ncurl %s -vv -H\"Cmdbuild-authorization:${cmdbuild_auth_token}\" \"%s\" %s%s\n",
                authToken, methodParam, baseUrl + service.replaceFirst("^/", ""), otherParams,
                isJson ? " | jshon" : " | xmlstarlet fo"); //TODO improve this
    }
}
