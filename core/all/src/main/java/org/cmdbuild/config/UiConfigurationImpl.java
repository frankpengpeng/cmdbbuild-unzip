/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.ui")
public class UiConfigurationImpl implements UiFilterConfiguration, UiConfiguration {

    @ConfigValue(key = "uiServiceBaseUrl", description = "ui service base url", defaultValue = AUTO_URL, category = CC_ENV)
    private String uiServiceBaseUrl;

    @ConfigValue(key = "manifest", description = "cmdbuild ui manifest", defaultValue = "cmdbuild")
    private String uiManifest;

    @ConfigValue(key = "timeout", description = "ui ws call timeout in seconds", defaultValue = "60")
    private int timeout;

    @ConfigValue(key = "detailWindow.width", description = "", defaultValue = "75")
    private Integer detailWindowWidth;

    @ConfigValue(key = "detailWindow.height", description = "", defaultValue = "95")
    private Integer detailWindowHeight;

    @ConfigValue(key = "referencecombolimit", description = "", defaultValue = "500")
    private Integer referencecombolimit;

    @ConfigValue(key = "keepFilterOnUpdatedCard", description = "", defaultValue = TRUE)
    private Boolean keepFilterOnUpdatedCard;

    @ConfigValue(key = "decimalsSeparator", description = "", defaultValue = ".")
    private String decimalsSeparator;

    @ConfigValue(key = "thousandsSeparator", description = "", defaultValue = "")
    private String thousandsSeparator;

    @ConfigValue(key = "dateFormat", description = "", defaultValue = "Y-m-d")
    private String dateFormat;

    @ConfigValue(key = "timeFormat", description = "", defaultValue = "H:i:s")
    private String timeFormat;

    @Override
    public String getDecimalsSeparator() {
        return decimalsSeparator;
    }

    @Override
    public String getThousandsSeparator() {
        return thousandsSeparator;
    }

    @Override
    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public String getTimeFormat() {
        return timeFormat;
    }

    @Override
    public String getBaseUrl() {
        return uiServiceBaseUrl;
    }

    @Override
    public String getUiManifest() {
        return uiManifest;
    }

    @Override
    public int getUiTimeout() {
        return timeout;
    }

    @Override
    public int getDetailWindowWidth() {
        return detailWindowWidth;
    }

    @Override
    public int getDetailWindowHeight() {
        return detailWindowHeight;
    }

    @Override
    public int getReferencecombolimit() {
        return referencecombolimit;
    }

    @Override
    public boolean getKeepFilterOnUpdatedCard() {
        return keepFilterOnUpdatedCard;
    }

}
