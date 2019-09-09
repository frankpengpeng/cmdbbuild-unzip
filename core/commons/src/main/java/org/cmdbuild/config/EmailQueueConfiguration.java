package org.cmdbuild.config;

import javax.annotation.Nullable;

public interface EmailQueueConfiguration {

    boolean isQueueProcessingEnabled();

    long getQueueTime();

    int getMaxErrors();

    int getMinRetryDelaySeconds();

    int getMaxRetryDelaySeconds();

    double getRetryDelayIncrement();

    String getDefaultEmailAccountCode();

    @Nullable
    default Integer getSmtpTimeoutSeconds() {
        return 30;
    }
}
