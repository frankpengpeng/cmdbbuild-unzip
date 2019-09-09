/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.log;

import javax.annotation.Nullable;

public interface LogbackConfigStore {

    @Nullable
    String getLogbackXmlConfiguration();

    String getDefaultLogbackXmlConfiguration();

    String getFallbackLogbackXmlConfiguration();

    void setLogbackXmlConfiguration(String config);

}
