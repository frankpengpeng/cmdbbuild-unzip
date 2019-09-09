/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

public interface BuildInfoService {

    boolean hasBuildInfo();

    String getVersionNumberOrUnknownIfNotAvailable();

    BuildInfo getBuildInfo();

    default String getCommitInfo() {
        return getBuildInfo().getCommitInfo();
    }

    default String getCommitInfoOrUnknownIfNotAvailable() {
        return hasBuildInfo() ? getCommitInfo() : "unknown";
    }

}
