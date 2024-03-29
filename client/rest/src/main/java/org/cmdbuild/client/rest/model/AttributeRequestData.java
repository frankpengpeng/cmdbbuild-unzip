/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import java.util.Map;
import org.cmdbuild.dao.entrytype.AttributePermissionMode;

public interface AttributeRequestData {

	boolean getActive();

	String getDefaultValue();

	String getDescription();

	String getDomainName();

	String getEditorType();

	String getFilter();

	String getGroup();

	Integer getIndex();

	Integer getLength();

	String getLookupType();

	Map<String, String> getMetadata();

	String getName();

	Integer getPrecision();

	boolean getRequired();

	Integer getScale();

	boolean getShowInGrid();

	String getTargetClass();

	String getTargetType();

	String getType();

	boolean getUnique();
	
	AttributePermissionMode getMode();

	String getIpType();

	Integer getClassOrder();
}
