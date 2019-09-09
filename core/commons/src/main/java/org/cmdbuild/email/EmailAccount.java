package org.cmdbuild.email;

import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface EmailAccount {

    @Nullable
    Long getId();

    String getName();

    @Nullable
    String getUsername();

    @Nullable
    String getPassword();

    String getAddress();

    @Nullable
    String getSmtpServer();

    @Nullable
    Integer getSmtpPort();

    boolean getSmtpSsl();

    boolean getSmtpStartTls();

    default boolean isSmtpConfigured() {
        return isNotBlank(getSmtpServer());
    }

    @Nullable
    String getSentEmailFolder();

    @Nullable
    String getImapServer();

    @Nullable
    Integer getImapPort();

    boolean getImapSsl();

    boolean getImapStartTls();

    default boolean isImapConfigured() {
        return isNotBlank(getImapServer());
    }

    default boolean isAuthenticationEnabled() {
        return isNotBlank(getUsername());
    }

    default boolean hasSentEmailFolder() {
        return isImapConfigured() && isNotBlank(getSentEmailFolder());
    }

}
