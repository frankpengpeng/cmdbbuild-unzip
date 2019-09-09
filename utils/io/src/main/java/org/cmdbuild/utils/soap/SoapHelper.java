/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.soap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import java.io.StringReader;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import java.util.Iterator;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang3.StringEscapeUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.xml.CmXmlUtils.nodeToString;
import static org.cmdbuild.utils.xml.CmXmlUtils.prettifyXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class SoapHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String url, soapAction, requestBody;
    private final Map<String, String> namespaces = map();

    public static SoapHelper newSoap() {
        return new SoapHelper();
    }

    public SoapHelper withUrl(String url) {
        this.url = url;
        return this;
    }

    public SoapHelper withSoapAction(String soapAction) {
        this.soapAction = soapAction;
        return this;
    }

    public SoapHelper withBody(String requestBody, Object... args) {
        this.requestBody = format(requestBody, list(args).stream().map(CmStringUtils::toStringOrEmpty).map(StringEscapeUtils::escapeXml10).collect(toList()).toArray(new Object[]{}));
        return this;
    }

    public SoapHelper withNamespace(String prefix, String namespace) {
        this.namespaces.put(prefix, namespace);
        return this;
    }

    public SoapResponse call() {
        String requestPayload = format("<?xml version=\"1.0\"?>"
                + "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope/\" soap:encodingStyle=\"http://www.w3.org/2003/05/soap-encoding\">"
                + "<soap:Body>%s</soap:Body>"
                + "</soap:Envelope>", checkNotNull(requestBody, "request body is null"));

        logger.debug("preparing soap call to url =< {} > method =< {} >", url, soapAction);
        logger.trace("soap request payload = \n\n{}\n", lazyString(() -> prettifyXml(requestPayload)));

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(checkNotBlank(url, "soap url endpoint is null"));
            request.setEntity(new StringEntity(requestPayload, ContentType.create("application/soap+xml")));
            if (isNotBlank(soapAction)) {
                request.setHeader("SOAPAction", soapAction);
            }
            CloseableHttpResponse response = httpClient.execute(request);
            logger.debug("response status = {}", response.getStatusLine());
            checkArgument(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK, "error: response status is %s", response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String responsePayload = EntityUtils.toString(entity);
            logger.trace("soap response payload = \n\n{}\n", lazyString(() -> prettifyXml(responsePayload)));
            EntityUtils.consumeQuietly(entity);//TODO check if this is really required
            return new SoapResponseImpl(responsePayload, namespaces);
        } catch (Exception ex) {
            throw runtime(ex, "error invoking soap ws url =< %s > method =< %s >", url, soapAction);
        }
    }

    public static class SoapResponseImpl implements SoapResponse {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Map<String, String> namespaces;
        private final String responsePayload;
        private final Supplier<Document> document = Suppliers.memoize(this::doToDocument);

        public SoapResponseImpl(String responsePayload, Map<String, String> namespaces) {
            this.responsePayload = checkNotBlank(responsePayload);
            this.namespaces = ImmutableMap.copyOf(namespaces);
            logger.debug("prepared soap response =< {} >", abbreviate(responsePayload));
        }

        @Override
        public String asString() {
            return responsePayload;
        }

        @Override
        public Document asDocument() {
            return document.get();
        }

        @Override
        @Nullable
        public String evalXpath(String expr) {
            return evalXpath(expr, XPathConstants.STRING);
        }

        @Override
        @Nullable
        public String evalXpathNode(String expr) {
            Node node = evalXpath(expr, XPathConstants.NODE);
            return node == null ? null : nodeToString(node);
        }

        private <T> T evalXpath(String expr, QName type) {
            try {
                XPath xpath = XPathFactory.newInstance().newXPath();
                xpath.setNamespaceContext(new NamespaceContextImpl((Map) map("soap", "http://www.w3.org/2003/05/soap-envelope/").with(namespaces)));
                return (T) xpath.compile(checkNotBlank(expr, "xpath expr is null")).evaluate(document.get(), type);
            } catch (Exception ex) {
                throw runtime(ex, "error processing xpath expr =< %s >", expr);
            }
        }

        private Document doToDocument() {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                return factory.newDocumentBuilder().parse(new InputSource(new StringReader(responsePayload)));
            } catch (Exception ex) {
                throw runtime(ex, "error parsing xml document =< %s >", abbreviate(responsePayload));
            }
        }

    }

    private static class NamespaceContextImpl implements NamespaceContext {

        private final BiMap<String, String> namespaces;

        public NamespaceContextImpl(Map<String, String> namespaces) {
            this.namespaces = ImmutableBiMap.copyOf(namespaces);
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return namespaces.get(prefix);
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return namespaces.inverse().get(namespaceURI);
        }

        @Override
        public Iterator getPrefixes(String namespaceURI) {
            return namespaces.inverse().containsKey(namespaceURI) ? singleton(getPrefix(namespaceURI)).iterator() : emptyList().iterator();
        }
    }
}
