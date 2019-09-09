package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import static java.util.stream.Collectors.toList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ACTIVITY_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_INSTANCE_ID;
import org.cmdbuild.service.rest.v3.serializationhelpers.FlowWsSerializationHelper;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.Flow;

@Path("processes/{" + PROCESS_ID + "}/instances/{" + PROCESS_INSTANCE_ID + "}/activities/")
@Produces(APPLICATION_JSON)
public class ProcessInstanceActivityWs {

	private final WorkflowService workflowService;
	private final FlowWsSerializationHelper converterService;

	public ProcessInstanceActivityWs(WorkflowService workflowService, FlowWsSerializationHelper converterService) {
		this.workflowService = checkNotNull(workflowService);
		this.converterService = checkNotNull(converterService);
	}

	@GET
	@Path("")
	public Object read(@PathParam(PROCESS_ID) String classId, @PathParam(PROCESS_INSTANCE_ID) Long cardId) {//TODO pagination

		List<Task> tasks = workflowService.getTaskListForCurrentUserByClassIdAndCardId(classId, cardId);

		return response(tasks.stream().map(FlowWsSerializationHelper::taskToTaskResponseWithBasicDetail).collect(toList()));
	}

	@GET
	@Path("{" + PROCESS_ACTIVITY_ID + "}/")
	public Object read(@PathParam(PROCESS_ID) String classId, @PathParam(PROCESS_INSTANCE_ID) Long cardId, @PathParam(PROCESS_ACTIVITY_ID) String taskId) {
		Flow card = workflowService.getFlowCard(classId, cardId);
		Task task = workflowService.getUserTask(card, taskId);
		return response(converterService.taskToTaskResponseWithFullDetail(card, task));
	}

}
