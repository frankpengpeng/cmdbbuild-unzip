/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim.utils;

import java.util.Map;
import org.cmdbuild.utils.bimserver.BimserverConfig;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class BimConfigUtils {

    public static final String BIMSERVER_CONFIG_NAMESPACE = "org.cmdbuild.bim",
            BIMSERVER_CONFIG_PREFIX = BIMSERVER_CONFIG_NAMESPACE + ".",
            BIMSERVER_CONFIG_ENABLED = "enabled",
            BIMSERVER_CONFIG_USERNAME = "username",
            BIMSERVER_CONFIG_PASSWORD = "password",
            BIMSERVER_CONFIG_URL = "url";

    public static Map<String, String> bimserverConfigToSysConfig(BimserverConfig config) {
        return map(
                BIMSERVER_CONFIG_PREFIX + BIMSERVER_CONFIG_ENABLED, true,
                BIMSERVER_CONFIG_PREFIX + BIMSERVER_CONFIG_URL, config.getBimserverUrl(),
                BIMSERVER_CONFIG_PREFIX + BIMSERVER_CONFIG_USERNAME, config.getAdminUsername(),
                BIMSERVER_CONFIG_PREFIX + BIMSERVER_CONFIG_PASSWORD, config.getAdminPassword()
        );
    }
}
