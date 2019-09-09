/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.extcomponents.commons;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.Ordering;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.extcomponents.commons.ExtComponentInfoImpl.ExtComponentInfoImplBuilder;
import org.cmdbuild.extcomponents.custompage.CustomPageException;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtComponentUtils {

private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Consumer< ExtComponentInfoImplBuilder> parseExtComponentData(byte[] data) {
        return b -> parseExtComponentData(data, b);
    }

    public static String getCodeFromExtComponentData(byte[] data) {
        return new ZipFileProcessor(data).getCode();
    }

//	public static CustomPageParsedData parseCustomPageBytes(byte[] data) {
//		return new ZipFileProcessor(data);
//	}
    public static byte[] getComponentFile(Object component, byte[] zipData, String path, boolean compressJs) {
        try {
            ZipFile zipFile = new ZipFile(new SeekableInMemoryByteChannel(zipData));
            ZipArchiveEntry entry = checkNotNull(zipFile.getEntry(path), "entry not found for path = %s");
            byte[] data = toByteArray(zipFile.getInputStream(entry));
            if (path.endsWith(".js") && compressJs) {
                try {
                    data = uglifyJs(data);
                } catch (Exception ex) {
                    LOGGER.warn("error minfying file = {} from component = {}", path, component, ex);
                }
            }
            return data;
        } catch (Exception ex) {
            throw new CustomPageException(ex, "error processing component data for component = %s path = %s", component, path);
        }
    }

    public static byte[] uglifyJs(byte[] data) {
        CompilerOptions options = new CompilerOptions();
        com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler();
        SourceFile sourceFile = SourceFile.fromCode("file.js", new String(data));
        Result result = compiler.compile(emptyList(), singletonList(sourceFile), options);
        checkArgument(result.success, "error compressing javascript");
        return compiler.toSource().getBytes();
    }

    private static void parseExtComponentData(byte[] data, ExtComponentInfoImplBuilder builder) {
        new ZipFileProcessor(data).toComponentInfo(builder);
    }

//	public static interface CustomPageParsedData {
//ExtComponentInfo toComponentInfo();
////		CustomPageData toCustomPageData();
////
////		ExtComponentInfoImpl.ExtComponentInfoImplBuilder toCustomPageInfo();
//	}
    private static class ZipFileProcessor {

        private final byte[] inputFile;
        private ZipFile zipFile;
        private String cpName, alias, mainExtClass, dirPrefix;
        private byte[] data;
        private boolean foundMainFile = false;

        public ZipFileProcessor(byte[] inputFile) {
            this.inputFile = checkNotNull(inputFile);
            processFile();
        }

        private void processFile() {
            try {
                zipFile = new ZipFile(new SeekableInMemoryByteChannel(inputFile));//TODO zip file processing
                List<Pair<String, byte[]>> files = list();
                Collections.list(zipFile.getEntries()).stream().filter(not(ZipArchiveEntry::isDirectory)).forEach((entry) -> {
                    try {
                        byte[] file = toByteArray(zipFile.getInputStream(entry));
                        String normalizedFile = normalizeFile(file);
                        if (normalizedFile.matches(".*mixins:\\[[^\\]]*['\"]CMDBuildUI.mixins.(CustomPage|ContextMenuComponent)['\"].*")) {
                            checkArgument(!foundMainFile, "duplicate main file found in component data");
                            foundMainFile = true;
                            {
                                Matcher matcher = Pattern.compile("Ext.define[(]['\"](CMDBuildUI\\.view\\.(custompages|contextmenucomponents)\\.([^.]+)\\.[^.'\"]+)['\"]").matcher(normalizedFile);
                                checkArgument(matcher.find(), "unable to find component id tag");
                                mainExtClass = checkNotBlank(matcher.group(1));
                                cpName = checkNotBlank(matcher.group(3));
                            }
                            {
                                Matcher matcher = Pattern.compile("alias:['\"]([^'\"]+)['\"]").matcher(normalizedFile);
                                checkArgument(matcher.find(), "unable to find alias tag");
                                alias = checkNotBlank(matcher.group(1));
                            }
                            dirPrefix = FilenameUtils.getPath(entry.getName());
                        }
                        files.add(Pair.of(entry.getName(), file));
                    } catch (Exception ex) {
                        throw new CustomPageException(ex, "error processing component file = %s", entry.getName());
                    }
                });
                checkArgument(foundMainFile, "main file not found in component data");

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try (ZipOutputStream zip = new ZipOutputStream(byteArrayOutputStream)) {
                    files.stream().sorted(Ordering.natural().onResultOf(Pair::getLeft)).forEach((p) -> {
                        try {
                            String name = p.getLeft();
                            byte[] file = p.getRight();
                            if (isNotBlank(dirPrefix)) {
                                checkArgument(name.startsWith(dirPrefix));
                                name = name.replaceFirst(Pattern.quote(dirPrefix), "");
                            }
                            ZipEntry entry = new ZipEntry(name);
                            zip.putNextEntry(entry);
                            zip.write(file);
                            zip.closeEntry();
                        } catch (IOException ex) {
                            throw runtime(ex);
                        }
                    });
                }

                data = byteArrayOutputStream.toByteArray();
            } catch (IOException ex) {
                throw new CustomPageException(ex, "error processing component data");
            }
        }

//		@Override
//		public CustomPageData toCustomPageData() {
//			return CustomPageDataImpl.builder()
//					.withName(cpName)
//					.withDescription(cpName)
//					.withData(data)
//					.build();
//		}
//
//		@Override
//		public ExtComponentInfoImpl.ExtComponentInfoImplBuilder toCustomPageInfo() {
//			return ExtComponentInfoImpl.builder()
//					.withName(cpName)
//					.withDescription(cpName)
//					.withExtjsAlias(alias)
//					.withExtjsComponentId(mainExtClass);
//		}
//        
//		@Override
        public void toComponentInfo(ExtComponentInfoImplBuilder builder) {
            builder
                    .withName(cpName)
                    .withDescription(cpName)
                    .withExtjsAlias(alias)
                    .withExtjsComponentId(mainExtClass);
//                    .build();
        }

        public String getCode() {
            return cpName;
        }

        private String normalizeFile(byte[] file) {
            return new String(file).replaceAll("[\n\r\t ]", "");
        }

    }
}
