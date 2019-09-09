/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.utils;

import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.workflow.model.FlowStatus;
import static org.cmdbuild.workflow.model.FlowStatus.ABORTED;
import static org.cmdbuild.workflow.model.FlowStatus.COMPLETED;
import static org.cmdbuild.workflow.model.FlowStatus.OPEN;
import static org.cmdbuild.workflow.model.FlowStatus.SUSPENDED;
import static org.cmdbuild.workflow.model.FlowStatus.TERMINATED;

public class FlowStatusUtils {

    public static boolean isCompleted(FlowStatus status) {
        switch (status) {
            case TERMINATED:
            case COMPLETED:
            case ABORTED:
                return true;
            case OPEN:
            case SUSPENDED:
                return false;
            default:
                throw unsupported("unsupported flow status = %s", status);
        }
    }
}
