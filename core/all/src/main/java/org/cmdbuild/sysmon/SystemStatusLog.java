/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysmon;

import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

public interface SystemStatusLog {

    int getJavaMemoryUsed();

    int getJavaMemoryAvailable();

    int getJavaPid();

    String getHostname();

    @Nullable
    String getNodeId();

    @Nullable
    String getBuildInfo();

    @Nullable
    Integer getSystemMemoryUsed();

    @Nullable
    Integer getSystemMemoryAvailable();

    double getLoadAvg();

    int getActiveSessionCount();

    @Nullable
    Integer getFilesystemMemoryUsed();

    @Nullable
    Integer getFilesystemMemoryAvailable();

    @Nullable
    String getWarnings();

    default boolean hasWarnings() {
        return !isBlank(getWarnings());
    }
}
