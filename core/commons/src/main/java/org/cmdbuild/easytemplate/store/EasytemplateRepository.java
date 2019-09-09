package org.cmdbuild.easytemplate.store;

import javax.annotation.Nullable;

public interface EasytemplateRepository {

	/**
	 * Returns the template associated with the specified name.
	 *
	 * @param name
	 *
	 * @return the template, {@code null} if not found.
	 */
	@Nullable
	String getTemplate(String name);

}
