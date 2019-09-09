/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms;

import javax.annotation.Nullable;

public interface DocumentData {

	@Nullable
	String getAuthor();

	String getFilename();

	@Nullable
	String getCategory();

	@Nullable
	String getDescription();

	boolean isMajorVersion();

	@Nullable
	byte[] getData();

	default boolean hasData() {
		return getData() != null;
	}

}
