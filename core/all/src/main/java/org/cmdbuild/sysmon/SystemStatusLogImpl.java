/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysmon;

import static com.google.common.base.Strings.emptyToNull;
import javax.annotation.Nullable;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_SystemStatusLog")
public class SystemStatusLogImpl implements SystemStatusLog {

    private final int javaMemoryUsed, javaMemoryAvailable, activeSessionCount, javaPid;
    private final double loadAvg;
    private final Integer filesystemMemoryUsed, filesystemMemoryAvailable, systemMemoryUsed, systemMemoryAvailable;
    private final String warnings, hostname, nodeId, buildInfo;

    private SystemStatusLogImpl(SystemStatusRecordImplBuilder builder) {
        this.javaMemoryUsed = (builder.javaMemoryUsed);
        this.javaMemoryAvailable = (builder.javaMemoryAvailable);
        this.javaPid = (builder.javaPid);
        this.hostname = checkNotBlank(builder.hostname);
        this.nodeId = builder.nodeId;
        this.buildInfo = builder.buildInfo;
        this.systemMemoryUsed = (builder.systemMemoryUsed);
        this.systemMemoryAvailable = (builder.systemMemoryAvailable);
        this.activeSessionCount = (builder.activeSessionCount);
        this.loadAvg = (builder.loadAvg);
        this.filesystemMemoryUsed = builder.filesystemMemoryUsed;
        this.filesystemMemoryAvailable = builder.filesystemMemoryAvailable;
        this.warnings = emptyToNull(builder.warnings);
    }

    @Override
    @CardAttr
    public int getJavaMemoryUsed() {
        return javaMemoryUsed;
    }

    @Override
    @CardAttr
    public int getJavaMemoryAvailable() {
        return javaMemoryAvailable;
    }

    @Override
    @CardAttr("Pid")
    public int getJavaPid() {
        return javaPid;
    }

    @Override
    @CardAttr
    public String getHostname() {
        return hostname;
    }

    @Override
    @Nullable
    @CardAttr
    public String getNodeId() {
        return nodeId;
    }

    @Override
    @Nullable
    @CardAttr
    public String getBuildInfo() {
        return buildInfo;
    }

    @Override
    @Nullable
    @CardAttr
    public Integer getSystemMemoryUsed() {
        return systemMemoryUsed;
    }

    @Override
    @Nullable
    @CardAttr
    public Integer getSystemMemoryAvailable() {
        return systemMemoryAvailable;
    }

    @Override
    @CardAttr
    public int getActiveSessionCount() {
        return activeSessionCount;
    }

    @Override
    @CardAttr
    public double getLoadAvg() {
        return loadAvg;
    }

    @Override
    @Nullable
    @CardAttr
    public Integer getFilesystemMemoryUsed() {
        return filesystemMemoryUsed;
    }

    @Override
    @Nullable
    @CardAttr
    public Integer getFilesystemMemoryAvailable() {
        return filesystemMemoryAvailable;
    }

    @Override
    @Nullable
    @CardAttr
    public String getWarnings() {
        return warnings;
    }

    public static SystemStatusRecordImplBuilder builder() {
        return new SystemStatusRecordImplBuilder();
    }

    public static SystemStatusRecordImplBuilder copyOf(SystemStatusLog source) {
        return new SystemStatusRecordImplBuilder()
                .withJavaMemoryUsed(source.getJavaMemoryUsed())
                .withJavaMemoryAvailable(source.getJavaMemoryAvailable())
                .withJavaPid(source.getJavaPid())
                .withHostname(source.getHostname())
                .withNodeId(source.getNodeId())
                .withBuildInfo(source.getBuildInfo())
                .withSystemMemoryUsed(source.getSystemMemoryUsed())
                .withSystemMemoryAvailable(source.getSystemMemoryAvailable())
                .withActiveSessionCount(source.getActiveSessionCount())
                .withLoadAvg(source.getLoadAvg())
                .withFilesystemMemoryUsed(source.getFilesystemMemoryUsed())
                .withFilesystemMemoryAvailable(source.getFilesystemMemoryAvailable())
                .withWarnings(source.getWarnings());
    }

    public static class SystemStatusRecordImplBuilder implements Builder<SystemStatusLogImpl, SystemStatusRecordImplBuilder> {

        private Integer javaMemoryUsed;
        private Integer javaMemoryAvailable, javaPid;
        private Integer systemMemoryUsed;
        private Integer systemMemoryAvailable;
        private Integer activeSessionCount;
        private Double loadAvg;
        private Integer filesystemMemoryUsed;
        private Integer filesystemMemoryAvailable;
        private String warnings, hostname, nodeId, buildInfo;

        public SystemStatusRecordImplBuilder withJavaMemoryUsed(Integer javaMemoryUsed) {
            this.javaMemoryUsed = javaMemoryUsed;
            return this;
        }

        public SystemStatusRecordImplBuilder withJavaMemoryAvailable(Integer javaMemoryAvailable) {
            this.javaMemoryAvailable = javaMemoryAvailable;
            return this;
        }

        public SystemStatusRecordImplBuilder withJavaPid(Integer javaPid) {
            this.javaPid = javaPid;
            return this;
        }

        public SystemStatusRecordImplBuilder withHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public SystemStatusRecordImplBuilder withNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public SystemStatusRecordImplBuilder withBuildInfo(String buildInfo) {
            this.buildInfo = buildInfo;
            return this;
        }

        public SystemStatusRecordImplBuilder withSystemMemoryUsed(Integer systemMemoryUsed) {
            this.systemMemoryUsed = systemMemoryUsed;
            return this;
        }

        public SystemStatusRecordImplBuilder withSystemMemoryAvailable(Integer systemMemoryAvailable) {
            this.systemMemoryAvailable = systemMemoryAvailable;
            return this;
        }

        public SystemStatusRecordImplBuilder withActiveSessionCount(Integer activeSessionCount) {
            this.activeSessionCount = activeSessionCount;
            return this;
        }

        public SystemStatusRecordImplBuilder withLoadAvg(Double loadAvg) {
            this.loadAvg = loadAvg;
            return this;
        }

        public SystemStatusRecordImplBuilder withFilesystemMemoryUsed(Integer filesystemMemoryUsed) {
            this.filesystemMemoryUsed = filesystemMemoryUsed;
            return this;
        }

        public SystemStatusRecordImplBuilder withFilesystemMemoryAvailable(Integer filesystemMemoryAvailable) {
            this.filesystemMemoryAvailable = filesystemMemoryAvailable;
            return this;
        }

        public SystemStatusRecordImplBuilder withWarnings(String warnings) {
            this.warnings = warnings;
            return this;
        }

        @Override
        public SystemStatusLogImpl build() {
            return new SystemStatusLogImpl(this);
        }

    }
}
