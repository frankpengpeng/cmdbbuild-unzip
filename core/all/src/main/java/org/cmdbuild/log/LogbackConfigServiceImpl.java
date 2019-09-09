/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.log;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.common.log.LoggerConfig;
import org.cmdbuild.common.log.LoggerConfigImpl;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.cmdbuild.config.api.DirectoryService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.cmdbuild.common.log.LoggersConfigService;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;

@Component
public class LogbackConfigServiceImpl implements LoggersConfigService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LogbackConfigServiceHelper configHelper = LogbackConfigServiceHelper.getInstance();

    private final LogbackConfigStore repository;
    private final DirectoryService directoryService;

    public LogbackConfigServiceImpl(LogbackConfigStore repository, DirectoryService directoryService) {
        this.repository = checkNotNull(repository);
        this.directoryService = checkNotNull(directoryService);
        upgradeLoggerConfigFileIfRequired();
    }

    @Override
    public List<File> getLogFiles() {
        List<File> configFiles = list(configHelper.getLogFiles());
        if (directoryService.hasContainerDirectory()) {
            File catalinaOut = new File(directoryService.getContainerDirectory(), "logs/catalina.out");//TODO improve this
            if (catalinaOut.exists()) {
                configFiles.add(catalinaOut);
            }
        }
        return configFiles;
    }

    @Override
    public List<LoggerConfig> getAllLoggerConfig() {
        logger.debug("getAllLoggerConfig");
        return asList(getCurrentConfigAsDocument().getElementsByTagName("logger")).stream().map((node) -> new LoggerConfigImpl(((Element) node).getAttribute("name"), ((Element) node).getAttribute("level"))).collect(toList());
    }

    @Override
    public void removeLoggerConfig(String category) {
        logger.info("removeLoggerConfig = {}", category);
        Document document = getCurrentConfigAsDocument();
        asList(document.getElementsByTagName("logger")).stream().filter((node) -> equal(((Element) node).getAttribute("name"), category)).forEach((node) -> {
            node.getParentNode().removeChild(node);
        });
        setConfigFromDocument(document);
    }

    @Override
    public void setLoggerConfig(LoggerConfig loggerConfig) {
        logger.info("setLoggerConfig = {}", loggerConfig);
        Document document = getCurrentConfigAsDocument();
        setLoggerConfigInDocument(document, loggerConfig);
        setConfigFromDocument(document);
    }

    @Override
    public String getConfigFileContent() {
        return buildConfigFromTemplate(getConfigContentOrTemplate());
    }

    private boolean isLogbackAutoconfigurationEnabled() {
        return toBooleanOrDefault(System.getProperty("org.cmdbuild.logback.autoconfigure"), true);
    }

    private void upgradeLoggerConfigFileIfRequired() {
        if (!isLogbackAutoconfigurationEnabled()) {
            logger.info("logback autoconfiguration is disabled");
        } else if (directoryService.hasConfigDirectory()) {
            try {
                List<LoggerConfig> curLoggers = getAllLoggerConfig();
                String curConfig = getConfigFileContent();
                boolean autoUpgrade = toBooleanOrDefault(XPathFactory.newInstance().newXPath().compile("//*[local-name()='property'][@name='CM_AUTO_UPGRADE_CONFIG']/@value").evaluate(stringToDocument(curConfig), XPathConstants.STRING), true);
                if (autoUpgrade) {
                    Document document = stringToDocument(buildConfigFromTemplate(repository.getDefaultLogbackXmlConfiguration()));
                    curLoggers.forEach(l -> setLoggerConfigInDocument(document, l));
                    String newConfig = documentToString(document);
                    if (!equal(curConfig, newConfig)) {
                        logger.info("upgrade logger config file");
                        setConfig(newConfig);
                    }
                } else {
                    logger.debug("skip logger config auto upgrade");
                }
            } catch (Exception ex) {
                logger.error("error processing logger config file", ex);
            }
        }
    }

    private String getConfigContentOrTemplate() {
        return firstNotBlank(repository.getLogbackXmlConfiguration(), repository.getDefaultLogbackXmlConfiguration());
    }

    private String buildConfigFromTemplate(String template) {
        return checkNotBlank(template)
                .replaceAll("LOG_DIR_PLACEHOLDER", directoryService.getContainerLogDirectory().getAbsolutePath())
                .replaceAll("WEBAPP_NAME_PLACEHOLDER", checkNotBlank(directoryService.getWebappName()));
    }

    private void setLoggerConfigInDocument(Document document, LoggerConfig loggerConfig) {
        Stream<Element> stream = asList(document.getElementsByTagName("logger")).stream().map((Node n) -> ((Element) n));

        Optional<Element> thisLogger = stream.filter((element) -> equal(element.getAttribute("name"), loggerConfig.getCategory())).findFirst();
        if (thisLogger.isPresent()) {
            logger.debug("logger config already present, update element = {}", thisLogger.get());
            thisLogger.get().setAttribute("level", loggerConfig.getLevel());
        } else {
            logger.debug("logger config not present, insert new logger element before root logger element, for category = {}", loggerConfig.getCategory());
            Element rootLogger = (Element) document.getElementsByTagName("root").item(0);
            Element newLogger = document.createElement("logger");
            newLogger.setAttribute("name", loggerConfig.getCategory());
            newLogger.setAttribute("level", loggerConfig.getLevel());
            rootLogger.getParentNode().insertBefore(newLogger, rootLogger);
            rootLogger.getParentNode().insertBefore(document.createTextNode("\n\n\t"), rootLogger);
        }

    }

    private static List<Node> asList(NodeList nodeList) {//TODO move this to common xml lib
        List<Node> list = newArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            list.add(nodeList.item(i));
        }
        return list;
    }

    private Document getCurrentConfigAsDocument() {
        try {
            return stringToDocument(getConfigFileContent());
        } catch (Exception ex1) {
            logger.error("error reading current log config, trying to reset config", ex1);
            try {
                return stringToDocument(buildConfigFromTemplate(repository.getDefaultLogbackXmlConfiguration()));
            } catch (Exception ex2) {
                logger.error("error processing default log config, using fallback config", ex2);
                return stringToDocument(buildConfigFromTemplate(repository.getFallbackLogbackXmlConfiguration()));
            }
        }
    }

    private Document stringToDocument(String content) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(false);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            return documentBuilder.parse(new InputSource(new StringReader(content)));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw runtime(ex);
        }
    }

    private String documentToString(Document document) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException ex) {
            throw runtime(ex);
        }
    }

    private void setConfigFromDocument(Document document) {
        String config = documentToString(document);
        setConfig(config);
    }

    private void setConfig(String config) {
        configHelper.configureLogback(config);//TODO propagate on cluster
        repository.setLogbackXmlConfiguration(config);
    }
}
