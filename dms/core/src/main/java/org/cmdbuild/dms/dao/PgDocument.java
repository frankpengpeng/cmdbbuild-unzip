/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;

public interface PgDocument extends DocumentInfoAndDetail {

	@Nullable
	Long getId();

	long getCardId();

	@Override
	default String getDocumentId() {
		return Long.toString(getId());
	}

	@Override
	default boolean hasContent() {
		return isNotBlank(getHash());
	}

}
