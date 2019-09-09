package org.cmdbuild.workflow.api;

import org.cmdbuild.workflow.commons.fluentapi.AnotherWfApi;
import org.cmdbuild.api.fluent.FluentApi;
import org.cmdbuild.api.fluent.MailApi;
import org.cmdbuild.services.soap.client.beans.Private;

public interface WorkflowApi extends FluentApi, SharkSchemaApi, MailApi, AnotherWfApi<WorkflowApi> {//TODO merge this interface with ExtendedApi

    Private soap();

}
