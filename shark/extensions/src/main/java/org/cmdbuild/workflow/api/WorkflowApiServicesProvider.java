/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.api;

import org.cmdbuild.api.fluent.FluentApi;
import org.cmdbuild.api.fluent.MailApi;
import org.cmdbuild.services.soap.client.beans.Private;

public interface WorkflowApiServicesProvider {

	FluentApi fluentApi();

	Private proxy();

	SharkSchemaApi schemaApi();

	MailApi mailApi();

	/**
	 * Needed to perform other operations after the instantiation of the object.
	 */
	void callback(WorkflowApiImpl object);

	WorkflowApiServicesProvider impersonate(String username, String group);

}
