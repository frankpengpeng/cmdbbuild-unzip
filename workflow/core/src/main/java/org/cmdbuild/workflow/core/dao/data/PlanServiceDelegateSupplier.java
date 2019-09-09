/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.dao.data;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.workflow.inner.PlanServiceDelegate;

public interface PlanServiceDelegateSupplier {

	List<PlanServiceDelegate> getActiveDelegates();

	List<PlanServiceDelegate> getAllDelegates();

	@Nullable
	PlanServiceDelegate getOrNull(String provider);

	default PlanServiceDelegate get(String provider) {
		return checkNotNull(getOrNull(provider), "provider not found for name = %s (non-existing or not active)", provider);
	}
}
