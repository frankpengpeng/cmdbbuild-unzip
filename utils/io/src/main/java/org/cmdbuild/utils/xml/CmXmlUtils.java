/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.xml;

import static com.google.common.base.Strings.nullToEmpty;
import java.io.StringReader;
import java.io.StringWriter;
import javax.annotation.Nullable;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class CmXmlUtils {

    private final static Logger logger = LoggerFactory.getLogger(CmXmlUtils.class);

    public static @Nullable
    String prettifyIfXml(@Nullable String mayBeXml) {
        return isXml(mayBeXml) ? prettifyXml(mayBeXml) : mayBeXml;
    }

    public static String prettifyXml(String xml) {
        try {
            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);
            Source source = new StreamSource(new StringReader(xml));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("encoding", "UTF8");
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(source, result);
            return stringWriter.toString();
        } catch (Exception ex) {
            logger.warn("unable to prettify xml = {}", abbreviate(xml.replaceAll("\\n|\\r", ""), 100));
            logger.debug("unable to prettify xml", ex);
            return xml;
        }
    }

    public static boolean isXml(@Nullable String mayBeXml) {
        return nullToEmpty(mayBeXml).startsWith("<");//TODO better euristic; maybe use tika?
    }

    public static String nodeToString(Node node) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (TransformerException ex) {
            throw runtime(ex);
        }
    }

}
