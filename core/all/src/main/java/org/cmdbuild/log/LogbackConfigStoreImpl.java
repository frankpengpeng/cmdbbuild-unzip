/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.log;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import javax.annotation.Nullable;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogbackConfigStoreImpl implements LogbackConfigStore {

    private final static String LOGBACK_CONFIG_FILE_NAME = "logback.xml",
            DEFAULT_LOGBACK_CONFIG_TEMPLATE = checkNotBlank(readToString(LogbackConfigStoreImpl.class.getResourceAsStream("/org/cmdbuild/log/logback_default.xml"))),
            FALLBACK_LOGBACK_CONFIG_TEMPLATE = checkNotBlank(readToString(LogbackConfigStoreImpl.class.getResourceAsStream("/org/cmdbuild/log/logback_fallback.xml")));

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;

    public LogbackConfigStoreImpl(DirectoryService directoryService) {
        this.directoryService = checkNotNull(directoryService);
    }

    @Override
    public String getDefaultLogbackXmlConfiguration() {
        return DEFAULT_LOGBACK_CONFIG_TEMPLATE;
    }

    @Override
    public String getFallbackLogbackXmlConfiguration() {
        return FALLBACK_LOGBACK_CONFIG_TEMPLATE;
    }

    @Override
    @Nullable
    public String getLogbackXmlConfiguration() {
        if (directoryService.hasConfigDirectory()) {
            File file = new File(directoryService.getConfigDirectory(), LOGBACK_CONFIG_FILE_NAME);
            if (file.exists()) {
                try {
                    return CmIoUtils.readToString(new File(directoryService.getConfigDirectory(), LOGBACK_CONFIG_FILE_NAME));
                } catch (Exception ex) {
                    logger.error("error reading logback config from file = {}", file, ex);
                }
            }
        }
        return null;
    }

    @Override
    public void setLogbackXmlConfiguration(String xmlConfiguration) {
        checkArgument(directoryService.hasConfigDirectory(), "unable to store logback config: config directory is not available");
        File file = new File(directoryService.getConfigDirectory(), LOGBACK_CONFIG_FILE_NAME);
        logger.info("update logback config file = {}", file.getAbsolutePath());
        writeToFile(file, xmlConfiguration);
    }
}
