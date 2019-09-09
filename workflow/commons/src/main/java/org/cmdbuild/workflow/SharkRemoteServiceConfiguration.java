package org.cmdbuild.workflow;

import java.util.Properties;

public interface SharkRemoteServiceConfiguration {

	String getSharkServerUrl();

	String getSharkUsername();

	String getSharkPassword();

	default Properties getClientProperties() {
		Properties clientProps = new Properties();
		clientProps.put("ClientType", "WS");
		clientProps.put("SharkWSURLPrefix", getSharkServerUrl());
		return clientProps;
	}

}
