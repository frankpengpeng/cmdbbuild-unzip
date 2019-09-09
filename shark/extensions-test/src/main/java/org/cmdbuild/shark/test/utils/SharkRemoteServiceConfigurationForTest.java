/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.shark.test.utils;

import java.util.Properties;
import org.cmdbuild.workflow.SharkRemoteServiceConfiguration;

public class SharkRemoteServiceConfigurationForTest implements SharkRemoteServiceConfiguration {

	@Override
	public String getSharkServerUrl() {
		return "";
	}

	@Override
	public String getSharkUsername() {
		return "admin";
	}

	@Override
	public String getSharkPassword() {
		return "";
	}

	@Override
	public Properties getClientProperties() {
		Properties properties = new Properties();
//		properties.put("ClientType", "WS");//TODO check this
		properties.put("SharkConfResourceLocation", "Shark.conf");
		return properties;
	}

}
