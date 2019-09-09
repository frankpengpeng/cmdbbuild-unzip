/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;

public interface TempService {

	String storeTempData(byte[] data, long timeToLiveSeconds);

	@Nullable
	byte[] getTempDataOrNull(String key);

	default byte[] getTempData(String key) {
		return checkNotNull(getTempDataOrNull(key), "temp data not found for key = %s", key);
	}

	default String storeTempData(byte[] data) {
		return storeTempData(data, 60 * 60);
	}

}
