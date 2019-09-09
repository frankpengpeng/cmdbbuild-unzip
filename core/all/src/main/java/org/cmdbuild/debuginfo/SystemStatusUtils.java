/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import static org.cmdbuild.utils.io.CmNetUtils.getHostname;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemStatusUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String getIpAddr() {
        String myIpAddr = "127.0.0.1";
        try {
            myIpAddr = InetAddress.getByName(getHostname()).getHostAddress();
        } catch (Exception ex) {
            LOGGER.warn(marker(), "error retrieving my ip addr", ex);
        }
        return myIpAddr;
    }
}
