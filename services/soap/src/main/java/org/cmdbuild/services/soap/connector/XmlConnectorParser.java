package org.cmdbuild.services.soap.connector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlConnectorParser implements ConnectorParser {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final String xmlString;

	public XmlConnectorParser(final String xmlString) {
		this.xmlString = xmlString;
	}

	@Override
	public Document parse() {
		Document document = null;
		try {
			document = DocumentHelper.parseText(xmlString);
		} catch (final DocumentException e) {
			logger.error("Cannot parse the xml string");
		}
		return document;
	}

}
