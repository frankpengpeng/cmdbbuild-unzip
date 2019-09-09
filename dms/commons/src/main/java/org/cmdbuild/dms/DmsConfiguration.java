package org.cmdbuild.dms;

import static com.google.common.base.Objects.equal;

public interface DmsConfiguration {

	boolean isEnabled();

	String getService();

	String getDefaultDocumentCategoryLookup();

	String getDefaultAttachmentDescriptionMode();

	default boolean isEnabled(String dmsProviderServiceName) {
		return isEnabled() && equal(getService(), dmsProviderServiceName);
	}
}
