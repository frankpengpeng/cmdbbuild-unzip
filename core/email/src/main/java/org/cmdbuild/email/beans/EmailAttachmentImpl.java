/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.beans;

import org.cmdbuild.email.EmailAttachment;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.activation.DataSource;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.getFilenameFromContentType;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import org.cmdbuild.utils.lang.Builder;

public class EmailAttachmentImpl implements EmailAttachment {

    private final byte[] data;
    private final String mimeType, fileName;

    private EmailAttachmentImpl(EmailAttachmentImplBuilder builder) {
        this.data = checkNotNull(builder.data);
        this.mimeType = isBlank(builder.mimeType) ? CmIoUtils.getContentType(data) : builder.mimeType;
        this.fileName = isBlank(builder.fileName) ? getFilenameFromContentType(mimeType) : builder.fileName;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public static EmailAttachmentImplBuilder builder() {
        return new EmailAttachmentImplBuilder();
    }

    public static EmailAttachmentImplBuilder copyOf(EmailAttachment source) {
        return new EmailAttachmentImplBuilder()
                .withData(source.getData())
                .withMimeType(source.getMimeType())
                .withFileName(source.getFileName());
    }

    public static EmailAttachmentImplBuilder copyOf(DataSource data) {
        return builder()
                .withData(toByteArray(data))
                .withFileName(data.getName())
                .withMimeType(data.getContentType());
    }

    public static EmailAttachmentImpl build(DataSource data) {
        return copyOf(data).build();
    }

    public static class EmailAttachmentImplBuilder implements Builder<EmailAttachmentImpl, EmailAttachmentImplBuilder> {

        private byte[] data;
        private String mimeType;
        private String fileName;

        public EmailAttachmentImplBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        public EmailAttachmentImplBuilder withMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public EmailAttachmentImplBuilder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        @Override
        public EmailAttachmentImpl build() {
            return new EmailAttachmentImpl(this);
        }

    }
}
