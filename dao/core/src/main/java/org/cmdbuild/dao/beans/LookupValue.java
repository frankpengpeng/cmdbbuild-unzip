/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import javax.annotation.Nullable;

public interface LookupValue extends IdAndDescription {

	@Nullable
	String getLookupType();
}
