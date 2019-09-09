package org.cmdbuild.workflow;

import java.util.List;
import javax.annotation.Nullable;

public interface WorkflowConfiguration extends SharkRemoteServiceConfiguration {

    boolean returnNullPlanOnPlanError();

    boolean isEnabled();

    boolean hideSaveButton();

    boolean isSynchronizationOfMissingVariablesDisabled();

    List<String> getEnabledWorkflowProviders();

    boolean isUserTaskParametersValidationEnabled();

    @Nullable
    String getDefaultUserForWfJobs();

    default String getDefaultWorkflowProvider() {
        return getEnabledWorkflowProviders().get(0);
    }

    default boolean isWorkflowProviderEnabled(String name) {
        return getEnabledWorkflowProviders().contains(name);
    }
}
