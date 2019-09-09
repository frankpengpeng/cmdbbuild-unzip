/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import static java.lang.String.format;
import java.time.ZonedDateTime;
import java.util.Properties;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

public class BuildInfoUtils {

    public static BuildInfo parseBuildInfo(Properties properties) {
        try {
            return new BuildInfoImpl(properties);
        } catch (Exception ex) {
            throw runtime(ex, "error parsing build info");
        }
    }

    private static class BuildInfoImpl implements BuildInfo {

        private final String commitId, branch, versionNumber;
        private final ZonedDateTime timestamp;
        private final boolean isDirty;

        private BuildInfoImpl(Properties properties) {
            commitId = checkNotBlank(properties.getProperty("git.commit.id.abbrev"));
            branch = checkNotBlank(properties.getProperty("git.branch"));
            timestamp = CmDateUtils.toDateTime(checkNotBlank(properties.getProperty("git.commit.time")));
            isDirty = toBoolean(properties.getProperty("git.dirty"));
            versionNumber = checkNotBlank(properties.getProperty("org.cmdbuild.version"));
        }

        @Override
        public String getCommitInfo() {
            return format("%s/%s (%s)%s", commitId, branch, toIsoDateTimeUtc(timestamp), isDirty ? " (dirty)" : "");
        }

        @Override
        public String getVersionNumber() {
            return versionNumber;
        }

        @Override
        public String toString() {
            return "BuildInfoImpl{" + "info=" + getCommitInfo() + ", version=" + versionNumber + '}';
        }

    }
}
