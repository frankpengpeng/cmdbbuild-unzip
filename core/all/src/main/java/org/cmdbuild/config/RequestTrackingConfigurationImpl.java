/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static org.cmdbuild.config.RequestTrackingConfiguration.LogTrackingMode.LTM_NEVER;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.audit")
public class RequestTrackingConfigurationImpl implements RequestTrackingConfiguration {

    @ConfigValue(key = "enabled", defaultValue = TRUE)
    private boolean isRequestTrackingEnabled;

    @ConfigValue(key = "includePayload", defaultValue = TRUE)
    private boolean includeRequestPayload;

    @ConfigValue(key = "includeResponse", defaultValue = TRUE)
    private boolean includeResponsePayload;

    @ConfigValue(key = "maxPayloadSize", defaultValue = "100000000")// 100 MB (base64)
    private int maxPayloadLength;

    @ConfigValue(key = "maxRecordsToKeep", defaultValue = "100000")
    private Integer maxRecordsToKeep;

    @ConfigValue(key = "maxRecordAgeToKeepSeconds", defaultValue = "-1")
    private Integer maxRecordAgeToKeepSeconds;

    @ConfigValue(key = "exclude")
    private String excludeRegex;

    @ConfigValue(key = "include", defaultValue = "^/services/")
    private String includeRegex;

    @ConfigValue(key = "logTrackingMode", defaultValue = "always", description = "log tacking mode, one of `always`, `never`, `on_error`; in production it is recommended to leave it on `on_error`")
    private LogTrackingMode logTrackingMode;

    @Override
    public boolean isRequestTrackingEnabled() {
        return isRequestTrackingEnabled;
    }

    @Override
    public boolean includeRequestPayload() {
        return includeRequestPayload;
    }

    @Override
    public boolean includeResponsePayload() {
        return includeResponsePayload;
    }

    @Override
    public int getMaxPayloadLength() {
        return maxPayloadLength;
    }

    @Override
    public Integer getMaxRecordsToKeep() {
        return maxRecordsToKeep;
    }

    @Override
    public Integer getMaxRecordAgeToKeepSeconds() {
        return maxRecordAgeToKeepSeconds;
    }

    @Override
    public String getRegexForPathsToExclude() {
        return excludeRegex;
    }

    @Override
    public String getRegexForPathsToInclude() {
        return includeRegex;
    }

    @Override
    public LogTrackingMode getLogTrackingMode() {
        return firstNotNull(logTrackingMode, LTM_NEVER);
    }

}
