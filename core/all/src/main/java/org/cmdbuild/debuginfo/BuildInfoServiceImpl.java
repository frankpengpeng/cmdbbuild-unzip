/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.DirectoryService;
import static org.cmdbuild.debuginfo.BuildInfoUtils.parseBuildInfo;

@Component
public class BuildInfoServiceImpl implements BuildInfoService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BuildInfo buildInfo;
    private final String versionNumber;

    public BuildInfoServiceImpl(DirectoryService directoryService) {
        if (directoryService.hasWebappDirectory()) {
            BuildInfo thisBuildInfo;
            try {
                Properties properties = new Properties();
                try (FileInputStream in = new FileInputStream(new File(directoryService.getWebappDirectory(), "WEB-INF/classes/git.properties"))) {
                    properties.load(in);
                }
                try (FileInputStream in = new FileInputStream(new File(directoryService.getWebappDirectory(), "WEB-INF/classes/org/cmdbuild/version.properties"))) {
                    properties.load(in);
                }
                thisBuildInfo = parseBuildInfo(properties);
            } catch (Exception ex) {
                logger.warn(marker(), "error whire reading build info", ex);
                thisBuildInfo = null;
            }
            buildInfo = thisBuildInfo;
            versionNumber = buildInfo.getVersionNumber();
        } else {
            buildInfo = null;
            versionNumber = "unknown";
        }
    }

    @Override
    public BuildInfo getBuildInfo() {
        return checkNotNull(buildInfo, "build info not available!");
    }

    @Override
    public boolean hasBuildInfo() {
        return buildInfo != null;
    }

    @Override
    public String getVersionNumberOrUnknownIfNotAvailable() {
        return versionNumber;
    }

}
