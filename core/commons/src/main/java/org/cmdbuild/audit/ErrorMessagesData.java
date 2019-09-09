/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.common.error.ErrorOrWarningEvent;

public class ErrorMessagesData {

	private final List<ErrorMessageDataImpl> data;

	@JsonCreator
	public ErrorMessagesData(@JsonProperty("data") List<ErrorMessageDataImpl> data) {
		this.data = ImmutableList.copyOf(data);
	}

	@JsonProperty("data")
	public List<ErrorMessageDataImpl> getData() {
		return data;
	}

	public static ErrorMessagesData fromErrorsAndWarningEvents(List<ErrorOrWarningEvent> events) {
		return new ErrorMessagesData(events.stream().map((e) -> new ErrorMessageDataImpl(e.getLevel(), e.getMessage(), e.getStackTraceAsString())).collect(toList()));
	}
}
