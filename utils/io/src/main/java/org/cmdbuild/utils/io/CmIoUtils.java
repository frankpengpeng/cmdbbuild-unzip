/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Predicate;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.base.Supplier;
import java.io.FileInputStream;
import java.net.URI;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import static org.cmdbuild.utils.date.CmDateUtils.dateTimeFileSuffix;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.ONE_GB;
import static org.apache.commons.io.FileUtils.ONE_KB;
import static org.apache.commons.io.FileUtils.ONE_MB;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public final class CmIoUtils {

    private final static Tika TIKA = new Tika();
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void writeToFile(File file, String content) {
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            FileUtils.write(file, content);
        } catch (IOException ex) {
            throw runtime(ex, "error writitng to file = %s", file);
        }
    }

    public static void writeToFile(File file, byte[] data) {
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            FileUtils.writeByteArrayToFile(file, data);
        } catch (IOException ex) {
            throw runtime(ex, "error writitng to file = %s", file);
        }
    }

    public static void writeToFile(File file, BigByteArray data) {
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            FileUtils.copyInputStreamToFile(data.toInputStream(), file);
        } catch (IOException ex) {
            throw runtime(ex, "error writitng to file = %s", file);
        }
    }

    public static String readToString(File file) {
        try {
            return FileUtils.readFileToString(file);
        } catch (IOException ex) {
            throw runtime(ex, "error reading file = %s", file);
        }
    }

    public static List<String> readLines(String string) {
        try {
            return IOUtils.readLines(new StringReader(string));
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static byte[] toByteArray(File file) {
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException ex) {
            throw runtime(ex, "error reading file = %s", file);
        }
    }

    public static BigByteArray toBigByteArray(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            BigByteArrayOutputStream out = new BigByteArrayOutputStream();
            IOUtils.copyLarge(in, out);
            return out.toBigByteArray();
        } catch (IOException ex) {
            throw runtime(ex, "error reading file = %s", file);
        }
    }

    public static BigByteArray toBigByteArray(InputStream in) {
        try {
            BigByteArrayOutputStream out = new BigByteArrayOutputStream();
            IOUtils.copyLarge(in, out);
            return out.toBigByteArray();
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    /**
     * return temp file; will set deleteOnExit().
     *
     * @return
     */
    public static File tempFile() {
        return tempFile(null, null);
    }

    /**
     * return temp file with given prefix and optional suffix; will set
     * deleteOnExit().
     *
     * @param prefix optional file prefix, trailing [-_]+ will be stripped and
     * replaced with single _
     * @param suffix optional file suffix, leading [.]+ will be stripped and
     * replaced with single .
     * @return
     */
    public static File tempFile(@Nullable String prefix, @Nullable String suffix) {
        File tempFile = new File(cmTmpDir(), (firstNonNull(prefix, "cmdbuild_temp_").replaceAll("[_-]+$", "") + "_" + tempId()) + "." + firstNonNull(suffix, "file").replaceAll("^[.]+", ""));
        checkArgument(tempFile.getParentFile().isDirectory(), "java temp dir error, tmpdir = (%s)", tempFile.getParent());
        tempFile.deleteOnExit();
        return tempFile;
    }

    /**
     * return temp dir; will set deleteOnExit().
     *
     * @return
     */
    public static File tempDir() {
        return tempDir(null);
    }

    /**
     * return temp dir with optional prefix; will set deleteOnExit().
     *
     * @param prefix dir prefix, trailing [-_]+ will be stripped and replaced
     * with single _
     * @return
     */
    public static File tempDir(@Nullable String prefix) {
        return tempDir(prefix, true);
    }

    public static File tempDir(@Nullable String prefix, boolean deleteOnExit) {
        File tempDir = new File(cmTmpDir(), (firstNonNull(prefix, "cmdbuild_temp_").replaceAll("[_-]+$", "") + "_" + tempId()));
        checkArgument(tempDir.mkdirs(), "unable to create temp dir %s", tempDir);
        tempDir.deleteOnExit();
        return tempDir;
    }

    public static File javaTmpDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    public static File cmTmpDir() {
        for (String name : list(System.getProperty("cmdbuild.tmpdir"), System.getenv("CMDBUILD_TMP"), System.getProperty("java.io.tmpdir"))) {
            if (isNotBlank(name)) {
                File file = new File(name);
                file.mkdirs();
                if (file.isDirectory()) {
                    return file;
                }
            }
        }
        throw runtime("error: unable to find temp dir for cmdbuild");
    }

    public static File cmSlowCacheDir() {
        return javaTmpDir();
    }

    /**
     *
     * @return a temp id build with date and random uuid, to be used as part of
     * a temporary file name
     */
    public static String tempId() {
        return dateTimeFileSuffix() + "_" + UUID.randomUUID().toString().substring(0, 6);
    }

    public static File fetchFileWithCache(String sha1checksum, String url) {
        return fetchFileWithCache(sha1checksum, () -> {
            try {
                LOGGER.info("fetch resource from url = {}", url);
                return new URI(url).toURL().openStream();
            } catch (Exception ex) {
                throw runtime(ex);
            }
        });
    }

    public static File fetchFileWithCache(String sha1checksum, Supplier<InputStream> fetcher) {
        checkNotBlank(sha1checksum);
        return fetchFileWithCache(new File(cmSlowCacheDir(), "cm_" + DigestUtils.md5Hex(sha1checksum + nullToEmpty(System.getProperty("user.name"))) + ".cache"), (f) -> {
            try {
                return DigestUtils.sha1Hex(new FileInputStream(f)).equalsIgnoreCase(sha1checksum);
            } catch (IOException ex) {
                throw runtime(ex);
            }
        }, fetcher);
    }

    public static File fetchFileWithCache(File cacheFile, Predicate<File> fileChecker, Supplier<InputStream> fetcher) {
        if (!(cacheFile.exists() && fileChecker.apply(cacheFile))) {
            cacheFile.getParentFile().mkdirs();
            copy(fetcher.get(), cacheFile);
        }
        return cacheFile;
    }

    public static void copy(InputStream in, OutputStream out) {
        try {
            IOUtils.copy(in, out);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static void copy(InputStream in, File out) {
        try {
            FileUtils.copyInputStreamToFile(in, out);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static void writeToFile(String data, File out) {
        try {
            FileUtils.write(out, data);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static void writeToFile(byte[] data, File out) {
        try {
            FileUtils.writeByteArrayToFile(out, data);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static void writeToFile(DataSource data, File out) {
        try {
            FileUtils.copyInputStreamToFile(data.getInputStream(), out);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static void copy(DataHandler in, File out) {
        try {
            FileUtils.copyInputStreamToFile(in.getInputStream(), out);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static byte[] serializeObject(Object object) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(out)) {
            objectOutputStream.writeObject(object);
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return out.toByteArray();
    }

    public static <T> T deserializeObject(byte[] data) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (T) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw runtime(ex);
        }
    }

    public static Properties loadProperties(byte[] data) {
        try {
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(data));
            return properties;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] toByteArray(DataHandler dataHandler) {
        try (InputStream in = dataHandler.getInputStream()) {
            return IOUtils.toByteArray(in);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] toByteArray(DataSource dataSource) {
        try (InputStream in = dataSource.getInputStream()) {
            return IOUtils.toByteArray(in);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] toByteArray(InputStream inputStream) {
        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            return data;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] readBytes(InputStream inputStream, int count) {
        try {
            byte[] buffer = new byte[count];
            int res = inputStream.read(buffer);
            checkArgument(res == count, "try to read %s bytes, but only %s available", count, res);
            return buffer;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] urlToByteArray(String url) {
        try {
            try (InputStream in = URI.create(url).toURL().openStream()) {
                return toByteArray(in);
            }
        } catch (IOException ex) {
            throw runtime(ex, "error processing url =< %s >", url);
        }
    }

    public static String readToString(DataHandler dataHandler) {
        try {
            return IOUtils.toString(dataHandler.getInputStream());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String readToString(DataSource dataSource) {
        try {
            return IOUtils.toString(dataSource.getInputStream());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String readToString(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static DataHandler toDataHandler(InputStream data) {
        return toDataHandler(toByteArray(data));
    }

    public static DataHandler toDataHandler(byte[] data) {
        try {
            String contentType = TIKA.detect(data);
            String filename = getFilenameFromContentType(contentType);
            return newDataHandler(data, contentType, filename);
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static String getFilenameFromContentType(String contentType) {
        String ext = getExtFromContentType(contentType);
        if (isBlank(ext)) {
            return "file";
        } else {
            return "file." + ext;
        }
    }

    @Nullable
    public static String getExtFromContentType(String contentType) {
        try {
            return TikaConfig.getDefaultConfig().getMimeRepository().forName(contentType).getExtension();
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static String getContentType(byte[] data) {
        String contentType = TIKA.detect(data);
        if (isContentType(contentType, "text/plain") && isHtml(new String(data))) {
            contentType = "text/html";
        }
        return contentType;
    }

    private static boolean isHtml(String mayBeHtml) {
        Matcher matcher = Pattern.compile("</?(div|p|br|span|b|i)", Pattern.CASE_INSENSITIVE).matcher(mayBeHtml);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count > 0 && count > mayBeHtml.length() / 500;
    }

    @Nullable
    public static String getCharsetFromContentType(String contentType) {
        Matcher matcher = Pattern.compile("charset *= *([a-z0-9-]+)", Pattern.CASE_INSENSITIVE).matcher(nullToEmpty(contentType));
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static String setCharsetInContentType(String contentType, String charset) {
        Matcher matcher = Pattern.compile("(charset *= *)([a-z0-9-]+)", Pattern.CASE_INSENSITIVE).matcher(contentType);
        if (matcher.find()) {
            return matcher.replaceFirst(Matcher.quoteReplacement(format("charset=%s", charset)));
        } else {
            return format("%s;charset=%s", contentType, charset);
        }
    }

    public static boolean isContentType(String contentTypeString, String contentTypeExprPattern) {
        return contentTypeString.toLowerCase().startsWith(contentTypeExprPattern.replace("*", "").toLowerCase());
    }

    @Nullable
    public static String getExt(byte[] data) {
        return getExtFromContentType(TIKA.detect(data));
    }

    public static DataHandler newDataHandler(byte[] data, String contentType, @Nullable String fileName) {
        return new DataHandler(newDataSource(data, contentType, fileName));
    }

    public static DataHandler newDataHandler(byte[] data, String contentType) {
        return newDataHandler(data, contentType, null);
    }

    public static DataHandler newDataHandler(BigByteArray data, String contentType, @Nullable String fileName) {
        return new DataHandler(newDataSource(data, contentType, fileName));
    }

    public static DataHandler newDataHandler(String data, String contentType) {
        return newDataHandler(data.getBytes(), contentType, null);//TODO check charset
    }

    public static DataHandler newDataHandler(byte[] data) {
        return newDataHandler(data, TIKA.detect(data), null);
    }

    public static DataSource urlToDataSource(URL url) {
        return urlToDataSource(url.toString());
    }

    public static DataSource urlToDataSource(String url) {
        byte[] data = urlToByteArray(url);
        String filename, path = URI.create(url).getPath();
        if (isNotBlank(path)) {
            filename = new File(path).getName();
        } else {
            filename = "file";
        }
        return newDataSource(data, TIKA.detect(data), filename);
    }

    public static DataSource toDataSource(DataHandler dataHandler) {
        return new DataSource() {
            @Override
            public String getContentType() {
                return dataHandler.getContentType();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return dataHandler.getInputStream();
            }

            @Override
            public String getName() {
                return dataHandler.getName();
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return dataHandler.getOutputStream();
            }
        };
    }

    public static DataSource newDataSource(String data, String contentType) {
        return newDataSource(data.getBytes(StandardCharsets.UTF_8), contentType);
    }

    public static DataSource newDataSource(File file) {
        byte[] data = toByteArray(file);
        return newDataSource(data, getContentType(data), file.getName());
    }

    public static DataSource newDataSource(String data) {
        return newDataSource(data.getBytes(StandardCharsets.UTF_8));
    }

    public static DataSource newDataSource(byte[] data) {
        return newDataSource(data, getContentType(data));
    }

    public static DataSource newDataSource(byte[] data, String contentType) {
        return newDataSource(data, contentType, null);
    }

    public static DataSource newDataSource(String data, String contentType, @Nullable String fileName) {
        return newDataSource(data.getBytes(StandardCharsets.UTF_8), contentType, fileName);
    }

    public static DataSource newDataSource(byte[] data, String contentType, @Nullable String fileName) {
        checkNotNull(data);
        checkNotBlank(contentType);
        return new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(data);
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public String getName() {
                return nullToEmpty(fileName);
            }
        };
    }

    public static DataSource newDataSource(BigByteArray data, String contentType, @Nullable String fileName) {
        checkNotNull(data);
        checkNotBlank(contentType);
        return new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return new BigByteArrayInputStream(data);
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public String getName() {
                return nullToEmpty(fileName);
            }
        };
    }

    public static long countBytes(DataHandler dataHandler) {
        try {
            return IOUtils.copyLarge(dataHandler.getInputStream(), new NullOutputStream());
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static long countBytes(DataSource dataSource) {
        try {
            return IOUtils.copyLarge(dataSource.getInputStream(), new NullOutputStream());
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static long getAvailableLong(InputStream in) throws IOException {
        if (in instanceof BigInputStream) {
            return ((BigInputStream) in).availableLong();
        } else if (in instanceof FileInputStream) {
            return ((FileInputStream) in).available() == 0 ? 0l : ((FileInputStream) in).getChannel().size() - ((FileInputStream) in).getChannel().position();
        } else {
            return in.available();
        }
    }

    public static String byteCountToDisplaySize(long size) {
        String displaySize;
        if (size / ONE_GB > 0) {
            displaySize = String.valueOf(size / ONE_GB) + " GB";
        } else if (size / ONE_MB > 0) {
            displaySize = String.valueOf(size / ONE_MB) + " MB";
        } else if (size / ONE_KB > 0) {
            displaySize = String.valueOf(size / ONE_KB) + " KB";
        } else {
            displaySize = String.valueOf(size) + " bytes";
        }
        return displaySize;
    }
}
