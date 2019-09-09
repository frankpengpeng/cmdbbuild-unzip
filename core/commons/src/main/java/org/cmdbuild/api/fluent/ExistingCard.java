package org.cmdbuild.api.fluent;

import java.util.Set;


public interface ExistingCard extends Card {

    ExistingCard withCode(String value);

    ExistingCard withDescription(String value);

    ExistingCard with(String name, Object value);

    ExistingCard withAttribute(String name, Object value);

    ExistingCard limitAttributes(String... names);

    Set<String> getRequestedAttributes();

    Iterable<Attachment> getAttachments();

    ExistingCard withAttachment(String url, String fileName, String documentCategory, String description);

    ExistingCard with(Attachment attachment);

    void update();

    void delete();

    Card fetch();

    Attachments attachments();

}
