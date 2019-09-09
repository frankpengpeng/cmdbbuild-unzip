/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import javax.annotation.Nullable;

public interface BimObject {

	@Nullable
	Long getId();

	String getOwnerClassId();

	long getOwnerCardId();

	@Nullable
	String getProjectId();

	@Nullable
	String getGlobalId();
}
