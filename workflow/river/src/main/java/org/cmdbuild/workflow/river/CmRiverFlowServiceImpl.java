/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river;

import org.cmdbuild.workflow.river.engine.core.RiverFlowServiceImpl;
import org.cmdbuild.workflow.river.engine.data.RiverFlowRepository;
import org.cmdbuild.workflow.river.engine.data.RiverPlanRepository;
import org.springframework.stereotype.Component;
import org.cmdbuild.workflow.river.engine.task.RiverTaskService;

@Component
public class CmRiverFlowServiceImpl extends RiverFlowServiceImpl {

//	private final FlowPersistenceService persistenceService;
    public CmRiverFlowServiceImpl(RiverPlanRepository planRepository, RiverFlowRepository flowRepository, RiverTaskService taskService) {
        super(planRepository, flowRepository, taskService);
//		this.persistenceService = checkNotNull(persistenceService);
//		bridge.setConsumer((completedTask) -> completeTaskAndSaveFlow(completedTask));
    }

//	private void completeTaskAndSaveFlow(RiverTaskCompleted completedTask) {
//		//TODO check that we have lock on flow !!!
//		RiverFlow flow = completedTask(completedTask);
//		persistenceService.updateFlowCard(flow);
//	}
}
