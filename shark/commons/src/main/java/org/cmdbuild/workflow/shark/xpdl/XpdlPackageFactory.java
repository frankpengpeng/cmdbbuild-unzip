package org.cmdbuild.workflow.shark.xpdl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.enhydra.jxpdl.XMLInterface;
import org.enhydra.jxpdl.XMLInterfaceImpl;
import org.enhydra.jxpdl.XPDLRepositoryHandler;
import org.enhydra.jxpdl.elements.Package;
import org.w3c.dom.Document;

public class XpdlPackageFactory {

	private static final boolean IS_XML_REPRESENTATION = true;

	public static Package readXpdl(InputStream is) throws XpdlException {
		try {
			return readXpdl(IOUtils.toByteArray(is));
		} catch (Exception e) {
			throw new XpdlException(e);
		}
	}

	public static Package readXpdl(byte[] pkgContent) throws XpdlException {
		try {
			return xmlInterface().openPackageFromStream(pkgContent, IS_XML_REPRESENTATION);
		} catch (Exception e) {
			throw new XpdlException(e);
		}
	}

	private static XMLInterface xmlInterface() {
		return new XMLInterfaceImpl();
	}

	public static byte[] xpdlByteArray(Package pkg) throws XpdlException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		XpdlPackageFactory.writeXpdl(pkg, os);
		return os.toByteArray();
	}

	public static void writeXpdl(Package pkg, OutputStream os) throws XpdlException {
		try {
			Document document = writeDocument(pkg);
			writeDocumentToOutputStream(document, os);
		} catch (Exception e) {
			throw new XpdlException(e);
		}
	}

	private static void writeDocumentToOutputStream(Document document, OutputStream os) throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException, IOException {
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(os);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty("encoding", "UTF8");
		transformer.setOutputProperty("indent", "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(source, result);
		os.close();
	}

	private static Document writeDocument(Package pkg) throws ParserConfigurationException {
		Document document = newXmlDocument();
		repositoryHandler().toXML(document, pkg);
		return document;
	}

	private static Document newXmlDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.newDocument();
	}

	private static XPDLRepositoryHandler repositoryHandler() {
		XPDLRepositoryHandler repH = new XPDLRepositoryHandler();
		repH.setXPDLPrefixEnabled(true);
		return repH;
	}
}
