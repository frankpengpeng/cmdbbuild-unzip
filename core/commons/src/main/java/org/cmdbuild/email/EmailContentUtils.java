/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.List;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class EmailContentUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String getContentTypeOrAutoDetect(@Nullable String contentType, String content) {
        if (isBlank(contentType) || isContentType(contentType, "application/octet-stream")) {
            contentType = getContentType(nullToEmpty(content).getBytes());
            if (isContentType(contentType, "application/octet-stream")) {
                contentType = "text/plain";
            }
        }
        return contentType;
    }

    public static String getContentTypeOrAutoDetect(Email email) {
        return getContentTypeOrAutoDetect(email.getContentType(), email.getContent());
    }

    public static String getContentPlaintext(Email email) {
        try {
            String contentType = getContentTypeOrAutoDetect(email);
            if (isContentType(contentType, "text/*")) {
                return email.getContent();
            } else if (isContentType(contentType, "multipart/*")) {
                MimeMultipart mimeMultipart = new MimeMultipart(newDataSource(email.getContent().getBytes(), contentType));
                List<Part> parts = getParts(mimeMultipart);
                checkArgument(!parts.isEmpty(), "multipart content is empty");
                Part part = parts.stream().filter(rethrowPredicate(p -> p.isMimeType("text/*"))).findFirst().orElse(null);
                if (part != null) {
                    return readToString(part.getDataHandler());
                }
            }
            throw new EmailException("plaintext content not found");
        } catch (Exception ex) {
            LOGGER.warn(marker(), "unable to get plaintext content for email = {}", email, ex);
            return email.getContent();
        }
    }

    public static DataHandler getContentHtmlOrRawPlaintext(Email email) {
        return getContentHtml(email, false);
    }

    public static DataHandler getContentHtmlOrWrappedPlaintext(Email email) {
        return getContentHtml(email, true);
    }

    private static DataHandler getContentHtml(Email email, boolean wrapPlaintextFallback) {
        try {
            String contentType = getContentTypeOrAutoDetect(email);
            if (isContentType(contentType, "text/html")) {
                return newDataHandler(email.getContent(), contentType);
            } else if (isContentType(contentType, "multipart/*")) {
                MimeMultipart mimeMultipart = new MimeMultipart(newDataSource(email.getContent(), contentType));
                List<Part> parts = getParts(mimeMultipart);
                checkArgument(!parts.isEmpty(), "multipart content is empty");
                Part part = parts.stream().filter(rethrowPredicate(p -> p.isMimeType("text/html"))).findFirst().orElse(parts.stream().findFirst().get());
                return part.getDataHandler();
            }
        } catch (Exception ex) {
            LOGGER.error(marker(), "unable to get html content for email = {}", email, ex);
        }
        String plaintext = getContentPlaintext(email);
        if (wrapPlaintextFallback) {
            plaintext = format("<pre>%s</pre>", plaintext);
        }
        return newDataHandler(plaintext, "text/plain");
    }

    private static List<Part> getParts(Multipart multipart) throws MessagingException {
        List<Part> parts = list();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            if (part.isMimeType("multipart/*")) {
                parts.addAll(getParts(new MimeMultipart(toDataSource(part.getDataHandler()))));//TODO improve this conversion
            } else {
                parts.add(part);
            }
        }
        return parts;
    }
}
