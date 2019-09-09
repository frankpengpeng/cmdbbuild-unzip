/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;

public interface DocumentDataRepository {

	@Nullable
	byte[] getDocumentDataOrNull(long infoCardId);

	default byte[] getDocumentData(long infoCardId) {
		return checkNotNull(getDocumentDataOrNull(infoCardId), "document data not found for id = %s", infoCardId);
	}

	void updateDocumentData(long infoCardId, byte[] data);

	void createDocumentData(long infoCardId, byte[] data);

}
