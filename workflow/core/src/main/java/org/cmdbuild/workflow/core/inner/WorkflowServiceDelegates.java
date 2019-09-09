/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.in;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.services.PostStartup;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.inner.WorkflowServiceDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.workflow.WorkflowCommonConst.WORKFLOW_ENGINES;

@Component
public class WorkflowServiceDelegates {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, WorkflowServiceDelegate> services;
	private final WorkflowConfiguration configuration;

	private List<WorkflowServiceDelegate> servicesDefaultFirst;
	private WorkflowServiceDelegate defaultService;
	private Map<String, WorkflowServiceDelegate> activeServices;

	public WorkflowServiceDelegates(List<WorkflowServiceDelegate> services, WorkflowConfiguration configuration) {
		this.configuration = checkNotNull(configuration);
		this.services = uniqueIndex(checkNotNull(services), WorkflowServiceDelegate::getName);
		checkArgument(!this.services.isEmpty());
	}

	@PostStartup
	@ConfigListener(WorkflowConfiguration.class)
	public final void loadConfig() {
		try {
			if (configuration.isEnabled()) {
				activeServices = ImmutableMap.copyOf(filterKeys(services, in(configuration.getEnabledWorkflowProviders())));
				checkArgument(!activeServices.isEmpty());
				defaultService = getService(configuration.getDefaultWorkflowProvider());
				servicesDefaultFirst = ImmutableList.copyOf(activeServices.values().stream().sorted((s1, s2) -> ComparisonChain.start()
						.compareTrueFirst(equal(s1.getName(), configuration.getDefaultWorkflowProvider()), equal(s2.getName(), configuration.getDefaultWorkflowProvider()))
						.compare(s1.getName(), s2.getName()).result()).collect(toList()));
				checkArgument(!servicesDefaultFirst.isEmpty());
			} else {
				configureDisabledWorkflow();
			}
		} catch (Exception ex) {
			logger.error("unable to start workflow service facade, invalid workflow config", ex);
			configureDisabledWorkflow();
		}
	}

	private void configureDisabledWorkflow() {
		activeServices = emptyMap();
		defaultService = new DummyWorkflowServiceDelegate();
		servicesDefaultFirst = singletonList(defaultService);
	}

	public WorkflowServiceDelegate getDefault() {
		return checkNotNull(defaultService, "invalid workflow configuration, unable default provider not found");
	}

	public WorkflowServiceDelegate getService(String key) {
		checkArgument(WORKFLOW_ENGINES.contains(key), "invalid engine name = %s", key);
		return checkNotNull(activeServices.get(key), "workflow service provider = %s is not active", key);
	}

	public List<WorkflowServiceDelegate> getServicesDefaultFirst() {
		return checkNotNull(servicesDefaultFirst, "invalid workflow configuration, unable to get providers");
	}
}
