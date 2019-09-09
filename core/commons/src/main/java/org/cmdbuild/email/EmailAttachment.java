/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface EmailAttachment {

    String getFileName();

    String getMimeType();

    byte[] getData();

    default boolean isOfType(String mimetype) {
        checkNotBlank(mimetype);
        return getMimeType().toLowerCase().startsWith(mimetype.toLowerCase());//TODO check this
    }

    default String getDataAsString() {
        return new String(getData());//TODO handle charset
    }

}
