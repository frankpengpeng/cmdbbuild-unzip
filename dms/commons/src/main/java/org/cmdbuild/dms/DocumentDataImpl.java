/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms;

import java.io.InputStream;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class DocumentDataImpl implements DocumentData {

    private final String author, filename, category, description;
    private final boolean majorVersion;
    private final byte[] data;

    private DocumentDataImpl(DocumentDataImplBuilder builder) {
        this.author = builder.author;
        this.filename = checkNotBlank(FilenameUtils.getName(builder.filename), "document file name cannot be null");
        this.category = builder.category;
        this.description = builder.description;
        this.majorVersion = builder.majorVersion;
        this.data = builder.data;
    }

    @Override
    @Nullable
    public String getAuthor() {
        return author;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    @Nullable
    public String getCategory() {
        return category;
    }

    @Override
    @Nullable
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isMajorVersion() {
        return majorVersion;
    }

    @Override
    @Nullable
    public byte[] getData() {
        return data;
    }

    public static DocumentDataImplBuilder builder() {
        return new DocumentDataImplBuilder();
    }

    public static DocumentDataImplBuilder copyOf(DocumentData source) {
        return new DocumentDataImplBuilder()
                .withAuthor(source.getAuthor())
                .withFilename(source.getFilename())
                .withCategory(source.getCategory())
                .withDescription(source.getDescription())
                .withMajorVersion(source.isMajorVersion())
                .withData(source.getData());
    }

    public static DocumentDataImplBuilder copyOf(DocumentInfoAndDetail source) {
        return new DocumentDataImplBuilder()
                .withAuthor(source.getAuthor())
                .withFilename(source.getFileName())
                .withCategory(source.getCategory())
                .withDescription(source.getDescription());
    }

    public static class DocumentDataImplBuilder implements Builder<DocumentDataImpl, DocumentDataImplBuilder> {

        private String author;
        private String filename;
        private String category;
        private String description;
        private boolean majorVersion = false;
        private byte[] data;

        public DocumentDataImplBuilder withAuthor(String author) {
            this.author = author;
            return this;
        }

        public DocumentDataImplBuilder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public DocumentDataImplBuilder withCategory(String category) {
            this.category = category;
            return this;
        }

        public DocumentDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public DocumentDataImplBuilder withMajorVersion(boolean majorVersion) {
            this.majorVersion = majorVersion;
            return this;
        }

        public DocumentDataImplBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        public DocumentDataImplBuilder withData(InputStream data) {
            return this.withData(toByteArray(data));
        }

        public DocumentDataImplBuilder withData(DataHandler dataHandler) {
            if (dataHandler == null) {
                return this.withData((byte[]) null);
            } else {
                return this.withData(toByteArray(dataHandler));
            }
        }

        @Override
        public DocumentDataImpl build() {
            return new DocumentDataImpl(this);
        }

    }
}
