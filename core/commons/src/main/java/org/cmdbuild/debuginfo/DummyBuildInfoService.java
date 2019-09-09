/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

public enum DummyBuildInfoService implements BuildInfoService {

    INSTANCE;

    @Override
    public boolean hasBuildInfo() {
        return false;
    }

    @Override
    public BuildInfo getBuildInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVersionNumberOrUnknownIfNotAvailable() {
        throw new UnsupportedOperationException();
    }
}
