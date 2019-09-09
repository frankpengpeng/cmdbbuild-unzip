/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.providers;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.DOTALL;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.cmdbuild.auth.login.AuthenticationException;
import org.cmdbuild.common.error.ErrorAndWarningCollectorService;
import org.cmdbuild.common.error.ErrorOrWarningEvent;
import org.cmdbuild.common.error.ErrorOrWarningEvent.LeveOrderErrorsFirst;
import org.cmdbuild.common.error.ErrorOrWarningEvent.ErrorEventLevel;
import org.cmdbuild.common.error.ErrorOrWarningEventCollector;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Provider
public class ExceptionHandlerService implements ExceptionMapper<Exception> {

	private static final String MESSAGE_LEVEL_INFO = "INFO", MESSAGE_LEVEL_WARNING = "WARNING", MESSAGE_LEVEL_ERROR = "ERROR";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ErrorAndWarningCollectorService errorAndWarningCollectorService;

	public ExceptionHandlerService(ErrorAndWarningCollectorService errorAndWarningCollectorService) {
		this.errorAndWarningCollectorService = checkNotNull(errorAndWarningCollectorService);
	}

	@Override
	public Response toResponse(Exception exception) {
		Response.Status status = getResponseStatus(exception);
		List messages;
		if (status == Response.Status.UNAUTHORIZED) {
			logger.warn("ws access denied (unauthorized)");
			logger.debug("ws access error", exception);
			messages = list(map("level", MESSAGE_LEVEL_ERROR, "show_user", true, "message", "access denied"));
		} else {
			logger.error("ws processing error", exception);
			ErrorOrWarningEventCollector collector = errorAndWarningCollectorService.getCurrentRequestEventCollector();
			collector.addError(exception);
			messages = buildResponseMessages(collector);
		}
		return Response
				.status(status)
				.entity(map("success", false, "messages", messages))
				.type(MediaType.APPLICATION_JSON_TYPE)
				.build();
	}

	private Response.Status getResponseStatus(Exception exception) {
		if (exception instanceof AuthenticationException || exception instanceof AccessDeniedException) {
			return Response.Status.UNAUTHORIZED; // TODO: this may be wrong, check
		} else if (exception instanceof IllegalArgumentException) {
			return Response.Status.BAD_REQUEST;
		} else if (exception.toString().toLowerCase().contains("not found")) {
			return Response.Status.NOT_FOUND;
		} else {
			return Response.Status.INTERNAL_SERVER_ERROR;//TODO error codes mapping
		}
	}

	public static List buildResponseMessages(ErrorOrWarningEventCollector errors) {
		return errors.getCollectedEvents().stream().sorted(Ordering.from(LeveOrderErrorsFirst.INSTANCE).onResultOf(ErrorOrWarningEvent::getLevel)).map(ExceptionHandlerService::errorToMessage).flatMap(List::stream).collect(toList());
	}

	public static List<Object> errorToMessage(ErrorOrWarningEvent event) {
		List<Object> list = list();
		Matcher matcher = Pattern.compile("(CM_CUSTOM_EXCEPTION|CM *: *)(.*?)((: *|; nested exception is )?[a-zA-Z0-9_.-]+[.][a-zA-Z0-9_]+(Exception|Error):.*)?$", DOTALL).matcher(event.getMessage());
		if (matcher.find()) {
			String userMessage = matcher.group(2);
			list.add(map("level", serializeLevel(event.getLevel()), "show_user", true, "message", userMessage));
		}
		list.add(map("level", serializeLevel(event.getLevel()), "show_user", false, "message", event.getMessage()));
		return list;
	}

	private static String serializeLevel(ErrorEventLevel level) {
		switch (level) {
			case ERROR:
				return MESSAGE_LEVEL_ERROR;
			case WARNING:
				return MESSAGE_LEVEL_WARNING;
			case INFO:
				return MESSAGE_LEVEL_INFO;
			default:
				throw unsupported("unsupported message level = %s", level);
		}
	}
}
