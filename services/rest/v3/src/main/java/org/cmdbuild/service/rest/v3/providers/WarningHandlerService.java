package org.cmdbuild.service.rest.v3.providers;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.cmdbuild.common.error.ErrorAndWarningCollectorService;
import org.cmdbuild.common.error.ErrorOrWarningEventCollector;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.service.rest.v3.providers.ExceptionHandlerService.buildResponseMessages;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Provider
public class WarningHandlerService implements ContainerResponseFilter {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final CoreConfiguration config;
	private final ErrorAndWarningCollectorService errorAndWarningCollectorService;

	public WarningHandlerService(CoreConfiguration config, ErrorAndWarningCollectorService errorAndWarningCollectorService) {
		this.config = checkNotNull(config);
		this.errorAndWarningCollectorService = checkNotNull(errorAndWarningCollectorService);
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		if (config.showInfoAndWarningMessages()) {
			if (isJsonResponse(responseContext)) {
				ErrorOrWarningEventCollector collector = errorAndWarningCollectorService.getCurrentRequestEventCollector();
				if (collector.hasEvents()) {
					Object entity = responseContext.getEntity();
					if (entity instanceof Map) {
						Map map = (Map) entity;
						if (isSuccessResponse(map)) {
							responseContext.setEntity(map(map).with("messages", list().accept((l) -> {
								if (map.containsKey("messages")) {
									l.addAll((Collection) map.get("messages"));
								}
							}).with(buildResponseMessages(collector))));
						}
					} else {
						logger.warn("tracked error/warning events, but unable to attach them to response entity of type = {}", getClassOfNullable(responseContext.getEntity()));
						//TODO handle other kind of response beans ? 
					}
				}
			}
		}
	}

	private boolean isJsonResponse(ContainerResponseContext responseContext) {
		return equal(responseContext.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
	}

	private boolean isSuccessResponse(Map map) {
		return equal(toStringOrNull(map.get("success")), Boolean.TRUE.toString());
	}

}
