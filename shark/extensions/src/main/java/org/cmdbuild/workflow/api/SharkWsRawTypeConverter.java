package org.cmdbuild.workflow.api;

import static com.google.common.reflect.Reflection.newProxy;
import static org.cmdbuild.common.utils.Reflection.unsupported;

import org.cmdbuild.api.fluent.ws.WsFluentApiExecutor.RawTypeConverter;
import org.cmdbuild.api.fluent.ws.WsFluentApiExecutor.WsType;
import org.cmdbuild.workflow.inner.AttributeInfo;
import org.cmdbuild.workflow.api.SharkSchemaApi.ForwardingAttributeInfo;

public class SharkWsRawTypeConverter extends SharkWsTypeConverter implements RawTypeConverter {

	private static final AttributeInfo UNSUPPORTED = newProxy(AttributeInfo.class, unsupported("should not be used"));

	private final WorkflowApi workflowApi;
	private final SharkWsTypeConverterConfig configuration;

	public SharkWsRawTypeConverter(final WorkflowApi workflowApi, final SharkWsTypeConverterConfig configuration) {
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
	public String toWsType(final WsType wsType, final Object value) {
		return super.toWsType(new ForwardingAttributeInfo() {

			@Override
			protected AttributeInfo delegate() {
				return UNSUPPORTED;
			}

			@Override
			public WsType getWsType() {
				return wsType;
			}

		}, value);
	}

}
