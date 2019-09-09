/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.fluentapi;

import java.util.Map;

public interface WorkflowApiService {

    Map<String, Object> getWorkflowApiAsDataMap();

    ExtendedApi getWorkflowApi();

}
