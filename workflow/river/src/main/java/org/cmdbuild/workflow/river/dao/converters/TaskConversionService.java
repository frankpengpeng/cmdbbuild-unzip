/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.dao.converters;

import java.util.List;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Task;
import static org.cmdbuild.workflow.river.dao.converters.FlowConversionMode.CM_FULL;

public interface TaskConversionService {

    Task toUserTask(Flow flow, RiverTask task, String userTaskId, String taskPerformer);

    List<Task> getTaskList(Flow flow, FlowConversionMode mode);

    Task getTask(Flow card, String userTaskId, FlowConversionMode mode);

    default List<Task> getTaskList(Flow flow) {
        return getTaskList(flow, CM_FULL);
    }

    default Task getTask(Flow card, String userTaskId) {
        return getTask(card, userTaskId, CM_FULL);
    }

}
