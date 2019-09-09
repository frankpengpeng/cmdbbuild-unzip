package org.cmdbuild.email;

import java.util.Map;
import javax.annotation.Nullable;

public interface EmailTemplate {

    @Nullable
    Long getId();

    String getName();

    @Nullable
    String getDescription();

    @Nullable
    String getFrom();

    @Nullable
    String getTo();

    @Nullable
    String getCc();

    @Nullable
    String getBcc();

    @Nullable
    String getSubject();

    @Nullable
    String getBody();

    String getContentType();

    Map<String, String> getData();

    @Nullable
    Long getAccount();

    boolean getKeepSynchronization();

    boolean getPromptSynchronization();

    @Nullable
    Long getDelay();

    default boolean hasAccount() {
        return getAccount() != null;
    }

}
