/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.dao.data;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.util.Collections.emptyList;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.services.PostStartup;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.inner.PlanServiceDelegate;
import org.springframework.stereotype.Component;

@Component
public class PlanServiceDelegateSupplierImpl implements PlanServiceDelegateSupplier {

	private final List<PlanServiceDelegate> allDelegates;
	private final WorkflowConfiguration configuration;

	private List<PlanServiceDelegate> activeDelegates;

	public PlanServiceDelegateSupplierImpl(List<PlanServiceDelegate> delegates, WorkflowConfiguration configuration) {
		this.allDelegates = copyOf(checkNotNull(delegates));
		this.configuration = checkNotNull(configuration);
	}

	@PostStartup
	@ConfigListener(WorkflowConfiguration.class)
	public final void loadConfig() {
		if (configuration.isEnabled()) {
			activeDelegates = allDelegates.stream().filter((d) -> configuration.isWorkflowProviderEnabled(d.getName())).collect(toImmutableList());
		} else {
			activeDelegates = emptyList();
		}
	}

	@Override
	public List<PlanServiceDelegate> getActiveDelegates() {
		return checkNotNull(activeDelegates);
	}

	@Override
	public List<PlanServiceDelegate> getAllDelegates() {
		return allDelegates;
	}

	@Override
	public @Nullable
	PlanServiceDelegate getOrNull(String provider) {
		return getActiveDelegates().stream().filter((p) -> equal(p.getName(), provider)).collect(toOptional()).orElse(null);
	}

}
