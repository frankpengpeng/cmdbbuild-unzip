/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.sql.utils;

import org.apache.maven.artifact.versioning.ComparableVersion;

public interface SqlFunction {

    String getSignature();

    String getRequiredPatchVersion();

    String getFunctionDefinition();

    String getHash();

    default ComparableVersion getRequiredPatchVersionAsComparableVersion() {
        return new ComparableVersion(getRequiredPatchVersion());
    }

}
