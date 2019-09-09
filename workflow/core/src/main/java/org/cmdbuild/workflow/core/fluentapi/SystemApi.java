/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.fluentapi;

import javax.annotation.Nullable;

public interface SystemApi {

    void executeQuery(String query);

    @Nullable
    String getSystemConfig(String key);

}
