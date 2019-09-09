/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.utils;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.io.IOException;
import static java.lang.Math.toIntExact;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;

public class ProxyUtils {

	public static HttpUriRequest buildRequest(String requestUrl, HttpServletRequest httpServletRequest) throws IOException {
		String method = httpServletRequest.getMethod();
		switch (method.toUpperCase()) {
			case HttpGet.METHOD_NAME:
				return new HttpGet(requestUrl);
			case HttpPost.METHOD_NAME:
				HttpPost httpPost = new HttpPost(requestUrl);
				httpPost.setHeader("Content-Type", httpServletRequest.getContentType());
				if (nullToEmpty(httpServletRequest.getContentType()).toLowerCase().contains("application/x-www-form-urlencoded")) {
					httpPost.setEntity(new StringEntity(getOnlyElement(httpServletRequest.getParameterMap().keySet())));//TODO improve this; thiw works only for single-entry form post without value, a degenerate case used by bimserver
				} else {
					httpPost.setEntity(new InputStreamEntity(httpServletRequest.getInputStream()));
				}
				return httpPost;
			default:
				throw unsupported("unsupported method = %s", method);
		}
	}

	public static void proxyRequest(HttpUriRequest httpRequest, HttpServletResponse httpServletResponse) throws IOException {
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build(); CloseableHttpResponse clientResponse = httpClient.execute(httpRequest)) {//TODO cache client
			String contentType = clientResponse.getFirstHeader("content-type").getValue();
			String contentDisposition = Optional.ofNullable(clientResponse.getFirstHeader("Content-Disposition")).map(Header::getValue).orElse(null);
			Integer resposeStatus = Optional.ofNullable(clientResponse.getStatusLine()).map(StatusLine::getStatusCode).orElse(null);
			Long contentLength = ltEqZeroToNull(clientResponse.getEntity().getContentLength());

			if (resposeStatus != null) {
				httpServletResponse.setStatus(resposeStatus);
			}
			httpServletResponse.setContentType(contentType);
			if (isNotBlank(contentDisposition)) {
				httpServletResponse.setHeader("Content-Disposition", contentDisposition);
			}
			if (contentLength != null) {
				httpServletResponse.setContentLength(toIntExact(contentLength));//TODO use long method when moving to servlet api 3.1
			}
			if (clientResponse.getEntity() != null) {
				clientResponse.getEntity().writeTo(httpServletResponse.getOutputStream());
				EntityUtils.consume(clientResponse.getEntity());
			}
		}
	}
}
