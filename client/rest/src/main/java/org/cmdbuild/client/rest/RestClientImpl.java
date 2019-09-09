/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest;

import org.cmdbuild.utils.io.StreamProgressListener;
import org.cmdbuild.utils.io.StreamProgressEvent;
import org.cmdbuild.client.rest.core.InnerRestClient;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.Reflection;
import java.io.IOException;
import static java.lang.String.format;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static java.util.Arrays.asList;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.apache.commons.lang3.builder.Builder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.cmdbuild.client.rest.impl.MenuApiImpl;
import org.cmdbuild.client.rest.impl.CardApiImpl;
import org.cmdbuild.client.rest.core.RestClientException;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.impl.SessionApiImpl;
import org.cmdbuild.client.rest.impl.AttachmentApiImpl;
import org.cmdbuild.client.rest.impl.AuditApiImpl;
import org.cmdbuild.client.rest.impl.LoginApiImpl;
import org.cmdbuild.client.rest.impl.LookupApiImpl;
import org.cmdbuild.client.rest.impl.SystemApiImpl;
import org.cmdbuild.client.rest.impl.WorkflowApiImpl;
import org.cmdbuild.client.rest.api.AttachmentApi;
import org.cmdbuild.client.rest.api.AuditApi;
import org.cmdbuild.client.rest.api.CardApi;
import org.cmdbuild.client.rest.api.LoginApi;
import org.cmdbuild.client.rest.api.LookupApi;
import org.cmdbuild.client.rest.api.SessionApi;
import org.cmdbuild.client.rest.api.SystemApi;
import org.cmdbuild.client.rest.api.WokflowApi;
import org.cmdbuild.client.rest.impl.ClasseApiImpl;
import org.cmdbuild.client.rest.api.ClassApi;
import org.cmdbuild.client.rest.api.CustomPageApi;
import org.cmdbuild.client.rest.api.MenuApi;
import org.cmdbuild.client.rest.api.ReportApi;
import org.cmdbuild.client.rest.api.UploadApi;
import org.cmdbuild.client.rest.impl.CustomPageApiImpl;
import org.cmdbuild.client.rest.impl.ReportApiImpl;
import org.cmdbuild.client.rest.impl.UploadApiImpl;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class RestClientImpl implements RestClient, RestWsClient, InnerRestClient {

	private String sessionToken, actionId;
	private final String serverUrl;
	private CloseableHttpClient httpClient;
	private final Map<String, String> customHeaders = map();
	private final EventBus eventBus = new EventBus();

	private RestClientImpl(String serverUrl) {
		this.serverUrl = checkNotNull(serverUrl);
	}

	@Override
	public void addUploadProgressListener(StreamProgressListener listener) {
		checkNotNull(listener);
		eventBus.register(new Object() {

			@Subscribe
			public void handleUploadProgressEvent(StreamProgressEvent event) {
				listener.handleStreamProgressEvent(event);
			}
		});
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	private void init() {
		httpClient = HttpClients.custom()
				.setDefaultHeaders(customHeaders.entrySet().stream().map((h) -> new BasicHeader(h.getKey(), h.getValue())).collect(toList()))
				.build();
	}

	@Override
	public void close() {
		if (httpClient != null) {
			try {
				httpClient.close();
			} catch (IOException ex) {
				throw runtime(ex);
			} finally {
				httpClient = null;
			}
		}
	}

	@Override
	public CloseableHttpClient getHttpClient() {
		if (httpClient == null) {
			init();
		}
		return httpClient;
	}

	@Override
	public void setHeader(String key, String value) {
		customHeaders.put(key, value);
		close();
	}

	@Override
	public String getSessionToken() {
		return checkNotNull(sessionToken, "session token not set");
	}

	@Override
	public void setSessionToken(String sessionToken) {
		this.sessionToken = checkNotNull(trimToNull(sessionToken));
	}

	@Override
	public String getServerUrl() {
		return checkNotNull(serverUrl, "server url not set");
	}

	@Override
	public LoginApi login() {
		return proxy(LoginApi.class, new LoginApiImpl(this));
	}

	@Override
	public WokflowApi workflow() {
		return proxy(WokflowApi.class, new WorkflowApiImpl(this));
	}

	@Override
	public SessionApi session() {
		return proxy(SessionApi.class, new SessionApiImpl(this));
	}

	@Override
	public CardApi card() {
		return proxy(CardApi.class, new CardApiImpl(this));
	}

	@Override
	public ClassApi classe() {
		return proxy(ClassApi.class, new ClasseApiImpl(this));
	}

	@Override
	public AttachmentApi attachment() {
		return proxy(AttachmentApi.class, new AttachmentApiImpl(this));
	}

	@Override
	public SystemApi system() {
		return proxy(SystemApi.class, new SystemApiImpl(this));
	}

	@Override
	public MenuApi menu() {
		return proxy(MenuApi.class, new MenuApiImpl(this));
	}

	@Override
	public AuditApi audit() {
		return proxy(AuditApi.class, new AuditApiImpl(this));
	}

	@Override
	public LookupApi lookup() {
		return proxy(LookupApi.class, new LookupApiImpl(this));
	}

	@Override
	public UploadApi uploads() {
		return proxy(UploadApi.class, new UploadApiImpl(this));
	}

	@Override
	public ReportApi report() {
		return proxy(ReportApi.class, new ReportApiImpl(this));
	}

	@Override
	public CustomPageApi customPage() {
		return proxy(CustomPageApi.class, new CustomPageApiImpl(this));
	}

	@Override
	public InnerRestClient inner() {
		return this;
	}

	public static RestClientBuilder builder() {
		return new RestClientBuilder();
	}

	public static RestClient build(String url) {
		return builder().withServerUrl(url).build();
	}

	@Override
	public void setActionId(@Nullable String actionId) {
		this.actionId = trimToNull(actionId);
	}

	@Override
	public String getActionId() {
		return actionId;
	}

	public static class RestClientBuilder implements Builder<RestClient> {

		private String serverUrl;

		public RestClientBuilder withServerUrl(String serverUrl) {
			this.serverUrl = checkNotNull(trimToNull(serverUrl));
			if (!this.serverUrl.endsWith("/")) {
				this.serverUrl = this.serverUrl + "/";
			}
			return this;
		}

		@Override
		public RestClient build() {
			return new RestClientImpl(serverUrl);
		}

	}

	private <T> T proxy(Class<T> type, T service) {
		return Reflection.newProxy(type, new ExceptionWrappingInvocationHandler(service));
	}

	private class ExceptionWrappingInvocationHandler<T> implements InvocationHandler {

		private final T service;

		public ExceptionWrappingInvocationHandler(T service) {
			this.service = checkNotNull(service);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				return method.invoke(service, args);
			} catch (InvocationTargetException ex) {
				Throwable innerException = ex.getCause();
				String serviceDescription = getOnlyElement(asList(proxy.getClass().getInterfaces())).getSimpleName().replaceFirst("Service$", "").toLowerCase();
				String methodName = method.getName();
				throw new RestClientException(format("error calling rest ws method %s.%s", serviceDescription, methodName), innerException);
			}
		}

	}

}
