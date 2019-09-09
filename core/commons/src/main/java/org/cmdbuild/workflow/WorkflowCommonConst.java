/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow;

import java.util.Set;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public class WorkflowCommonConst {

	public final static String RIVER = "river", SHARK = "shark";

	public final static Set<String> WORKFLOW_ENGINES = set(RIVER, SHARK).immutable();
}
