/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.shark;

import org.enhydra.shark.api.client.wfmc.wapi.WAPI;
import org.enhydra.shark.api.client.wfmc.wapi.WMConnectInfo;
import org.enhydra.shark.api.client.wfservice.SharkInterface;

public interface SharkWebserviceClient {

	WAPI getWapi();

	SharkInterface getSharkInterface();

	WMConnectInfo getConnectionInfo();
}
