/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.fluentapi;

import org.cmdbuild.api.fluent.FluentApi;
import org.cmdbuild.api.fluent.MailApi;
import org.cmdbuild.workflow.core.fluentapi.beans.ApiRole;
import org.cmdbuild.workflow.core.fluentapi.beans.ApiUser;

public interface ExtendedApi extends FluentApi, ExtendedApiMethods, MailApi  {

    ApiUser getCurrentUser();

    ApiRole getRole(String name);

    SystemApi getSystemApi();
}
