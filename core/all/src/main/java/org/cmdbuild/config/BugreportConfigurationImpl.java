package org.cmdbuild.config;

import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.config.api.ConfigComponent;

@Component
@ConfigComponent("org.cmdbuild.bugreport")
public class BugreportConfigurationImpl implements BugreportConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ConfigValue(key = "url", description = "bugreport endpoint url", defaultValue = "http://team.cmdbuild.org/bugreportcollector/bugreport")
    private String url;

    @Override
    public String getBugreportEndpoint() {
        return url;
    }

}
