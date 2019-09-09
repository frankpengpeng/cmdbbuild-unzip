package org.cmdbuild.config;

import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.entrytype.AttachmentDescriptionMode;
import static org.cmdbuild.dao.entrytype.AttachmentDescriptionModeUtils.serializeAttachmentDescriptionMode;
import org.cmdbuild.dms.cmis.CmisDmsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.dms")
public final class DmsConfigurationImpl implements CmisDmsConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ENABLED = "enabled";
    private static final String CATEGORY_LOOKUP = "category.lookup";

    private static final String CMIS_URL = "service.cmis.url";
    private static final String CMIS_USER = "service.cmis.user";
    private static final String CMIS_PASSWORD = "service.cmis.password";
    private static final String CMIS_PATH = "service.cmis.path";

    @ConfigValue(key = "service.type", description = "dms service (cmis, postgres); cmis is a standard protocol used, for example, by Alfresco dms; postgres is an embedded dms implementation that relies upon cmdbuild postgres db", defaultValue = "cmis")
    private String service;

    @ConfigValue(key = ENABLED, description = "", defaultValue = FALSE)
    private boolean isEnabled;

    @ConfigValue(key = CATEGORY_LOOKUP, description = "", defaultValue = "AlfrescoCategory", category = CC_ENV)
    private String categoryLookup;

    @ConfigValue(key = CMIS_URL, description = "", defaultValue = "http://localhost:10080/alfresco/api/-default-/public/cmis/versions/1.1/atom", category = CC_ENV)
    private String cmisUrl;

    @ConfigValue(key = CMIS_USER, description = "", defaultValue = "admin", category = CC_ENV)
    private String cmisUser;

    @ConfigValue(key = CMIS_PASSWORD, description = "", defaultValue = "admin", category = CC_ENV)
    private String cmisPassword;

    @ConfigValue(key = CMIS_PATH, description = "", defaultValue = "/User Homes/cmdbuild", category = CC_ENV)
    private String cmdisPath;

    @ConfigValue(key = "attachmentDescriptionMode", description = "default attachment description mode; valid values: mandatory,optional,hidden", defaultValue = "mandatory")
    private AttachmentDescriptionMode defaultAttachmentDescriptionMode;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public String getDefaultDocumentCategoryLookup() {
        return categoryLookup;
    }

    @Override
    public String getDefaultAttachmentDescriptionMode() {
        return serializeAttachmentDescriptionMode(defaultAttachmentDescriptionMode);
    }

    @Override
    public String getCmisUrl() {
        return cmisUrl;
    }

    @Override
    public String getCmisUser() {
        return cmisUser;
    }

    @Override
    public String getCmisPassword() {
        return cmisPassword;
    }

    @Override
    public String getCmisPath() {
        return cmdisPath;
    }

}
