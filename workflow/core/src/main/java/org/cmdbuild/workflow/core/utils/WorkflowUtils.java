/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.common.Constants.ROLE_CLASS_NAME;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.inner.WfReference;
import org.cmdbuild.workflow.model.WfReferenceImpl;
import static org.cmdbuild.workflow.model.Process.ADMIN_PERFORMER_AS_GROUP;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.auth.role.Role;
import static org.cmdbuild.auth.role.RolePrivilege.RP_PROCESS_ALL_EXEC;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;

public class WorkflowUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static TaskDefinition getEntryTaskForCurrentUser(Process plan, OperationUser user) {
        return checkNotNull(getEntryTaskForCurrentUserOrNull(plan, user), "entry task not found for plan = %s user = %s", plan, user);
    }

    public static @Nullable
    TaskDefinition getEntryTaskForCurrentUserOrNull(Process plan, OperationUser operationUser) {
        Collection<String> groups = operationUser.getGroupNames();
        String defaultGroup = operationUser.getDefaultGroupName();
        LOGGER.debug("selecting entry task from plan = {} for user = {} with default group = {} groups = {}", plan, operationUser, defaultGroup, groups);
        LOGGER.debug("available entry task = \n\n{}\n", lazyString(() -> plan.getAllEntryTasks().stream().map(t -> format("\t%-40s %s", t.getId(), t.getPerformers().stream().map(p -> format("%s:%s", p.getType().name(), p.getValue())).collect(joining(",")))).collect(joining("\n"))));

        TaskDefinition task = plan.getEntryTaskByGroupOrNull(defaultGroup);
        if (task != null) {
            LOGGER.debug("selected entry task by default group = {} task = {}", defaultGroup, task);
            return task;
        }

        List<Pair<String, TaskDefinition>> groupsAndTasks = groups.stream().map((g) -> Pair.of(g, plan.getEntryTaskByGroupOrNull(g))).filter((p) -> p.getRight() != null).collect(toList());
        if (!groupsAndTasks.isEmpty()) {
            if (groupsAndTasks.size() == 1) {
                task = getOnlyElement(groupsAndTasks).getRight();
                LOGGER.debug("selected entry task from user groups = {} task = {}", groups, task);
                return task;
            } else {
                LOGGER.warn(marker(), "found more than one entry task for current user = {} and process = {} and matching groups = {}", operationUser, plan, groupsAndTasks.stream().map(Pair::getLeft).collect(toList()));
            }
        }

        if (operationUser.hasPrivileges(RP_PROCESS_ALL_EXEC)) {
            task = plan.getEntryTaskByGroupOrNull(ADMIN_PERFORMER_AS_GROUP);
            if (task != null) {
                LOGGER.debug("selected entry task for admin user, task = {}", task);
                return task;
            }
            if (plan.getAllEntryTasks().size() == 1) {
                task = getOnlyElement(plan.getAllEntryTasks());
                LOGGER.debug("selected single entry task for admin user, task = {}", task);
                return task;
            }
            task = plan.getEntryTaskByGroupOrNull("SuperUsers");
            if (task != null) {
                LOGGER.debug("selected entry task for admin user (match legacy group \"SuperUsers\", task = {}", task);
                return task;
            }
        }

        return null;
    }

    public static @Nullable
    WfReference workflowReferenceFromCmGroup(@Nullable Role group) {
        WfReference output;
        if (group == null || group.getId() == null) {
            output = null;
        } else {
            output = new WfReferenceImpl(group.getId(), ROLE_CLASS_NAME);
        }
        return output;
    }

}
