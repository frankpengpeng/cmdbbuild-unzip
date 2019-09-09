package org.cmdbuild.api.fluent;

import java.net.URL;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import org.cmdbuild.workflow.type.ReferenceType;

public interface NewMail {

    /**
     * @return a {@link NewMail} object, can be {@code this} or a new
     *         instance.
     */
    NewMail withFrom(String from);

    /**
     * @return a {@link NewMail} object, can be {@code this} or a new
     *         instance.
     */
    NewMail withTo(String to);

    /**
     * @return a {@link NewMail} object, can be {@code this} or a new
     *         instance.
     */
    NewMail withTo(String... tos);

    /**
     * @return a {@link NewMail} object, can be {@code this} or a new
     *         instance.
     */
    NewMail withTo(Iterable<String> tos);

    /**
     * @return a {@link NewMail} object, can be {@code this} or a new
     *         instance.
     */
    NewMail withCc(String cc);

    /**
     * @return a {@link NewMail} object, can be {@code this} or a new
     *         instance.
     */
    NewMail withCc(String... ccs);

    /**
     * @return a {@link NewMail} object, can be {@code this} or a new
     *         instance.
     */
    NewMail withCc(Iterable<String> ccs);

    /**
     * @return a {@link NewMail} object, can be {@code this} or a new
     *         instance.
     */
    NewMail withBcc(String bcc);

    /**
     * @return a {@link NewMail} object, can be {@code this} or a new
     *         instance.
     */
    NewMail withBcc(String... bccs);

    /**
     * @return a {@link NewMail} object, can be {@code this} or a new
     *         instance.
     */
    NewMail withBcc(Iterable<String> bccs);

    /**
     * @return a {@link NewMail} object, can be {@code this} or a new
     *         instance.
     */
    NewMail withSubject(String subject);

    /**
     * @return a {@link NewMail} object, can be {@code this} or a new
     *         instance.
     */
    NewMail withContent(String content);

    NewMail withContentType(String contentType);

    NewMail withAttachment(URL url);

    NewMail withAttachment(URL url, String name);

    NewMail withAttachment(String url);

    NewMail withAttachment(String url, String name);

    NewMail withAttachment(DataHandler dataHandler);

    NewMail withAttachment(DataHandler dataHandler, String name);

    NewMail withAsynchronousSend(boolean asynchronous);

    void send();

    NewMail withCard(@Nullable String className, @Nullable Long cardId);

    default NewMail withCard(@Nullable Long cardId) {
        return this.withCard(null, cardId);
    }

    default NewMail withCard(@Nullable CardDescriptor card) {
        return this.withCard(card == null ? null : card.getClassName(), card == null ? null : card.getId());
    }

    default NewMail withCard(@Nullable ReferenceType card) {
        return this.withCard(card == null ? null : card.getClassName(), card == null ? null : card.getId());
    }
}
