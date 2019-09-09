package org.cmdbuild.task.startworkflow;

import static org.cmdbuild.common.utils.BuilderUtils.a;

//import org.cmdbuild.task.scheduler.AbstractJobFactory;
//import org.cmdbuild.scheduler.command.Command;
import org.cmdbuild.workflow.WorkflowService;
import org.springframework.stereotype.Component;

@Component
public class StartWorkflowTaskJobFactory   {

	private final WorkflowService workflowLogic;

	public StartWorkflowTaskJobFactory(WorkflowService workflowLogic) {
		this.workflowLogic = workflowLogic;
	}

//	@Override
//	protected Class<StartWorkflowTask> getType() {
//		return StartWorkflowTask.class;
//	}
//
//	@Override
//	protected Command command(StartWorkflowTask task) {
//		StartProcessAction startProcess = a(StartProcessAction.newInstance()
//				.withWorkflowLogic(workflowLogic)
//				.withClassName(task.getProcessClass())
//				.withAttributes(task.getAttributes()));
//		return startProcess::execute;
//	}

}
