/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailContentTest {

    private final String rawContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_1_raw_payload.txt")),
            htmlContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_1_html_content.txt")),
            plaintextContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/email/test/test_email_1_plaintext_content.txt")),
            contentType = "multipart/MIXED; boundary=\"=_cbd9373c11e1eb9e792e2db543e9f2f8\"";

    @Test
    public void testEmailContentParsing() {
        Email email = mock(Email.class);
        when(email.getContent()).thenReturn(rawContent);
        when(email.getContentType()).thenReturn(contentType);

        assertEquals(rawContent, email.getContent());
        assertEquals(contentType, EmailContentUtils.getContentTypeOrAutoDetect(email));
        assertEquals(plaintextContent, EmailContentUtils.getContentPlaintext(email));
        assertEquals(htmlContent, readToString(EmailContentUtils.getContentHtmlOrWrappedPlaintext(email)));
    }

}
