/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cardfilter;

import static java.lang.String.format;
import javax.annotation.Nullable;
import org.cmdbuild.auth.grant.PrivilegeSubject;

public interface StoredFilter extends PrivilegeSubject {

	@Nullable
	Long getId();

	String getName();

	String getDescription();

	String getClassName();

	String getConfiguration();

	boolean isShared();

	@Nullable
	Long getUserId();

	@Override
	default String getPrivilegeId() {
		return format("Filter:%d", getId());
	}
}
