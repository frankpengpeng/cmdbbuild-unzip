/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import static java.util.Arrays.asList;
import javax.annotation.Nullable;

public interface DirectoryService {

    File getConfigDirectory();

    File getContainerDirectory();

    File getWebappDirectory();

    boolean hasConfigDirectory();

    boolean hasWebappDirectory();

    boolean hasContainerDirectory();

    default File getBackupDirectory() {
        File file = new File(getConfigDirectory(), "backup");
        file.mkdirs();
        return file;
    }

    default boolean hasBackupDirectory() {
        return hasConfigDirectory();
    }

    default String getWebappName() {
        return getWebappDirectory().getName();
    }

    default File getWebappLibDirectory() {
        return new File(getWebappDirectory(), "WEB-INF/lib");
    }

    default File getContainerLibDirectory() {
        return new File(getContainerDirectory(), "lib");
    }

    default File getContainerLogDirectory() {
        File logDir = new File(getContainerDirectory(), "logs");
        checkArgument(logDir.isDirectory(), "container log dir is not available");
        return logDir;
    }

    default File getFileRelativeToContainerDirectoryIfAvailableAndNotAbsolute(File file) {
        if (!file.isAbsolute() && hasContainerDirectory()) {
            return new File(getContainerDirectory(), file.getPath());
        } else {
            return file;
        }
    }

    /**
     * get file from lib dir, by regexp pattern (match against whole name)
     */
    default @Nullable
    File getLibByPattern(String pattern) {
        File libDir = getWebappLibDirectory();
        return libDir == null ? null : asList(firstNonNull(libDir.listFiles(), new File[]{})).stream().filter((file) -> file.isFile() && file.getName().matches(pattern)).findFirst().orElse(null);
    }

}
