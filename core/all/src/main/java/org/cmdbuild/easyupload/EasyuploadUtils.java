/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class EasyuploadUtils {

    public static String[] pathToArray(String normalizedPath) {
        return Splitter.on("/").splitToList(normalizedPath).toArray(new String[]{});
    }

    public static String normalizePath(String path) {
        return normalizePath(new String[]{checkNotBlank(path)});
    }

    public static String normalizePath(String... parts) {
        return firstNotBlank(Joiner.on("/").join(parts).replaceFirst("^[.]/", "").replaceAll("/[.]", "").replaceAll("/+", "/").replaceAll("^/|/$", ""), "/");
    }

    public static String getFolder(String normalizedPath) {
        return normalizePath(normalizedPath.replaceFirst("[^/]+$", ""));
    }

    public static DataHandler toDataHandler(EasyuploadItem item) {
        return new DataHandler(new EasyuploadItemDataSourceAdapter(item));
    }

    private static class EasyuploadItemDataSourceAdapter implements DataSource {

        private final EasyuploadItem item;

        public EasyuploadItemDataSourceAdapter(EasyuploadItem item) {
            this.item = checkNotNull(item);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(item.getContent());
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getContentType() {
            return item.getMimeType();
        }

        @Override
        public String getName() {
            return item.getFileName();
        }
    }

}
