/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.common.error.ErrorOrWarningEvent.ErrorEventLevel;

public class ErrorMessageDataImpl implements ErrorMessageData {

	private final ErrorEventLevel level;
	private final String message;
	private final String stackTrace;

	public ErrorMessageDataImpl(@JsonProperty("level") ErrorEventLevel level, @JsonProperty("message") String message, @JsonProperty("exception") String stackTrace) {
		this.level = checkNotNull(level);
		this.message = message;
		this.stackTrace = stackTrace;
	}

	@Override
	@JsonProperty("level")
	public ErrorEventLevel getLevel() {
		return level;
	}

	@Override
	@JsonProperty("message")
	public String getMessage() {
		return message;
	}

	@Override
	@JsonProperty("exception")
	public String getStackTrace() {
		return stackTrace;
	}

	@Override
	public String toString() {
		return "ErrorMessageDataImpl{" + "level=" + level + ", message=" + message + '}';
	}

}
