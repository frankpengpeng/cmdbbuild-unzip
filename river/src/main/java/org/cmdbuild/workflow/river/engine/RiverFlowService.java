/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine;

public interface RiverFlowService {

    RiverPlan getPlanById(String planId);

    RiverFlow startFlow(RiverFlow flow, String entryPointId);

    RiverFlow completedTask(RiverTaskCompleted completedTask);

    RiverFlow completedTask(RiverFlow flow, RiverTaskCompleted completedTask);

    default RiverFlow createFlow(String planId) {
        return RiverFlowService.this.createFlow(getPlanById(planId));
    }

    RiverFlow createFlow(RiverPlan riverPlan);

    RiverFlow suspendFlow(RiverFlow flow);

    RiverFlow resumeFlow(RiverFlow flow);

    RiverFlow terminateFlow(RiverFlow flow);

}
