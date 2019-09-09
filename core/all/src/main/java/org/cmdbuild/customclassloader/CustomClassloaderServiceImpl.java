/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.customclassloader;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.io.Files;
import java.io.File;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.singletonList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.apache.commons.io.FileUtils;
import static org.cmdbuild.cache.CacheConfig.SYSTEM_OBJECTS;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.easyupload.EasyuploadItemInfo;
import org.cmdbuild.easyupload.EasyuploadService;
import static org.cmdbuild.utils.classpath.ClasspathUtils.buildClassloaderWithJarOverride;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomClassloaderServiceImpl implements CustomClassloaderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;
    private final EasyuploadService easyuploadService;

    private final CmCache<ClassLoader> classLoadersByName;

    public CustomClassloaderServiceImpl(DirectoryService directoryService, EasyuploadService easyuploadService, CacheService cacheService) {
        this.directoryService = checkNotNull(directoryService);
        this.easyuploadService = checkNotNull(easyuploadService);
        this.classLoadersByName = cacheService.newCache("custom_class_loaders_by_name", SYSTEM_OBJECTS);//TODO cache invalidate ??
    }

    @Override
    public ClassLoader getCustomClassLoader(String path) {
        return classLoadersByName.get(path, () -> doGetCustomClassLoader(path));
    }

    private ClassLoader doGetCustomClassLoader(String path) {
        checkNotBlank(path);

        logger.trace("lookup classloader for path =< {} > as absolute path", path);
        File fileFromAbsolutePath = new File(path);
        if (fileFromAbsolutePath.exists()) {
            return loadCustomClassLoaderFromFileOrDir(fileFromAbsolutePath);
        }

        File fileFromConfigDir = new File(new File(directoryService.getConfigDirectory(), "lib"), path);
        logger.trace("lookup classloader for path =< {} > with config dir (file = {} )", path, fileFromConfigDir);
        if (directoryService.hasConfigDirectory() && fileFromConfigDir.exists()) {
            return loadCustomClassLoaderFromFileOrDir(fileFromConfigDir);
        }

        logger.trace("lookup classloader for path =< {} > within easyupload files", path);

        List<EasyuploadItemInfo> files = easyuploadService.getByDir(path);
        if (!files.isEmpty()) {
            return loadCustomClassLoaderFromEasyuploadItems(files);
        }

        EasyuploadItemInfo file = easyuploadService.getByPathOrNull(path);
        if (file != null) {
            return loadCustomClassLoaderFromEasyuploadItems(singletonList(file));
        }

        throw new IllegalArgumentException(format("custom class loader not found for path =< %s >", path));
    }

    private ClassLoader loadCustomClassLoaderFromFileOrDir(File file) {
        if (file.isFile() && equal(Files.getFileExtension(file.getName()), "jar")) {
            return loadCustomClassLoaderFromJarFiles(singletonList(file));
        } else if (file.isDirectory()) {
            return loadCustomClassLoaderFromJarFiles(FileUtils.listFiles(file, new String[]{"jar"}, true));
        } else {
            throw new IllegalArgumentException(format("invalid class loader file =< %s >", file));
        }
    }

    private ClassLoader loadCustomClassLoaderFromEasyuploadItems(List<EasyuploadItemInfo> files) {
        files.forEach(f -> {
            checkArgument(equal(Files.getFileExtension(f.getFileName()), "jar"), "invalid file = %s", f);
        });
        File tempDir = tempDir();
        return loadCustomClassLoaderFromJarFiles(files.stream().map(f -> {
            File file = new File(tempDir, f.getFileName());
            writeToFile(easyuploadService.getById(f.getId()).getContent(), file);
            return file;
        }).collect(toList()));
    }

    private ClassLoader loadCustomClassLoaderFromJarFiles(Collection<File> jarFiles) {
        return buildClassloaderWithJarOverride(jarFiles, getClass().getClassLoader());
    }

}
