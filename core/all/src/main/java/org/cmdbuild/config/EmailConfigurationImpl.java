package org.cmdbuild.config;

import javax.annotation.Nullable;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.springframework.stereotype.Component;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;

@Component
@ConfigComponent("org.cmdbuild.email")
public final class EmailConfigurationImpl implements EmailQueueConfiguration {

    @ConfigValue(key = "queue.enabled", description = "", defaultValue = FALSE, category = CC_ENV)
    private boolean queueEnabled;

    @ConfigValue(key = "queue.time", description = "", defaultValue = "0")
    private int queueTime;

    @ConfigValue(key = "queue.maxErrors", description = "max failed tryes, after which a mail status is set to ERROR", defaultValue = "5")
    private int maxErrors;

    @ConfigValue(key = "queue.retry.delay.min", description = "delay for first retry, in case of email send error (in seconds)", defaultValue = "600")
    private int minRetryDelaySeconds;

    @ConfigValue(key = "queue.retry.delay.max", description = "max delay for next tries, in case of email send error (in seconds)", defaultValue = "86400")
    private int maxRetryDelaySeconds;

    @ConfigValue(key = "queue.retry.delay.increment", description = "proportional delay increment for subsequent retryes ( delay(n) = delay(n-1)*increment )", defaultValue = "3.0")
    private double retryDelayIncrement;

    @ConfigValue(key = "accountDefault", description = "default email account code")
    private String defaultEmailAccount;

    @Override
    public boolean isQueueProcessingEnabled() {
        return queueEnabled;
    }

    @Override
    public long getQueueTime() {
        return queueTime;
    }

    @Override
    public int getMaxErrors() {
        return maxErrors;
    }

    @Override
    public int getMinRetryDelaySeconds() {
        return minRetryDelaySeconds;
    }

    @Override
    public int getMaxRetryDelaySeconds() {
        return maxRetryDelaySeconds;
    }

    @Override
    public double getRetryDelayIncrement() {
        return retryDelayIncrement;
    }

    @Override
    @Nullable
    public String getDefaultEmailAccountCode() {
        return defaultEmailAccount;
    }

}
