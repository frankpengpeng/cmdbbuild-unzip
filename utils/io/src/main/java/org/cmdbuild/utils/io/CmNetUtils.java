/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.any;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.ServerSocket;
import static java.util.Arrays.asList;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmNetUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String getHostname() {
        String myHostname = "localhost";
        try {
            myHostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception ex) {
//			LOGGER.warn(marker(), "error retrieving my hostname", ex);
            LOGGER.warn("error retrieving my hostname", ex);
        }
        return myHostname;
    }

    /**
     *
     * scan ports to find a suitable offset
     *
     * @param intitialOffset offset from which to start scanning
     * @param defaultPorts all ports for which to find a suitable offset
     * @return a suitable port offset
     */
    public static int scanPortOffset(int intitialOffset, Integer... defaultPorts) {
        return scanPortOffset(intitialOffset, asList(checkNotNull(defaultPorts)));
    }

    /**
     *
     * scan ports to find a suitable offset
     *
     * @param intitialOffset offset from which to start scanning
     * @param defaultPorts all ports for which to find a suitable offset
     * @return a suitable port offset
     */
    public static int scanPortOffset(int intitialOffset, Iterable<Integer> defaultPorts) {
        checkNotNull(defaultPorts);
        checkArgument(!any(defaultPorts, isNull()));
        for (int i = intitialOffset; i < Integer.MAX_VALUE; i++) {
//				logger.debug("scan port offset = {}", i);
            final int offset = i;
            if (all(defaultPorts, (Integer port) -> isPortAvailable(port + offset))) {
                return offset;
            }
        }
        throw new RuntimeException("unable to find port offset for available ports!");
    }

    /**
     * return true if the (tcp) port is available, false otherwise
     *
     * @param port
     * @return port available
     */
    public static boolean isPortAvailable(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            return true;
        } catch (IOException e) {
        } finally {
            IOUtils.closeQuietly(serverSocket);
        }
        return false;
    }
}
