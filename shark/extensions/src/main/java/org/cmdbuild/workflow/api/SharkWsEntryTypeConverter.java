package org.cmdbuild.workflow.api;

import org.cmdbuild.api.fluent.ws.EntryTypeAttributeImpl;
import org.cmdbuild.api.fluent.ws.WsFluentApiExecutor.EntryTypeConverter;

public class SharkWsEntryTypeConverter extends SharkWsTypeConverter implements EntryTypeConverter {

	private final WorkflowApi workflowApi;
	private final SharkWsTypeConverterConfig configuration;

	public SharkWsEntryTypeConverter(final WorkflowApi workflowApi, final SharkWsTypeConverterConfig configuration) {
		this.workflowApi = workflowApi;
		this.configuration = configuration;
	}

	@Override
	protected WorkflowApi workflowApi() {
		return workflowApi;
	}

	@Override
	protected SharkWsTypeConverterConfig configuration() {
		return configuration;
	}

	@Override
	public String toWsType(final EntryTypeAttributeImpl entryTypeAttribute, final Object clientValue) {
		return toWsType(workflowApi.findAttributeFor(entryTypeAttribute), clientValue);
	}

	@Override
	public Object toClientType(final EntryTypeAttributeImpl entryTypeAttribute, final String wsValue) {
		return toClientType(workflowApi.findAttributeFor(entryTypeAttribute), wsValue);
	}

}
