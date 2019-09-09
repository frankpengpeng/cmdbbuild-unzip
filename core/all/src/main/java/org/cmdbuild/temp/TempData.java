/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import javax.annotation.Nullable;

public interface TempData {

	@Nullable
	Long getId();

	long getTimeToLiveSeconds();

	byte[] getData();

}
