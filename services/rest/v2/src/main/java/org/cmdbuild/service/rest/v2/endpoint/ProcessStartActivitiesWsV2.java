package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.transform;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.service.rest.v2.serializationhelpers.WsSerializationUtilsv2;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.core.utils.WorkflowUtils;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.TaskAttribute;

@Path("processes/{processId}/start_activities/")
@Produces(APPLICATION_JSON)
public class ProcessStartActivitiesWsV2 {
    
    private final WorkflowService workflowService;
    private final OperationUserSupplier userSupplier;
    private final WsSerializationUtilsv2 serializationUtils;
    
    public ProcessStartActivitiesWsV2(OperationUserSupplier userSupplier, WorkflowService workflowService, WsSerializationUtilsv2 serializationUtils) {
        this.workflowService = checkNotNull(workflowService);
        this.userSupplier = checkNotNull(userSupplier);
        this.serializationUtils = checkNotNull(serializationUtils);
    }
    
    @GET
    @Path("{processActivityId}")
    public Object readOne(@PathParam("processId") String processId, @PathParam("processActivityId") String activityId) {
        Process planClasse = workflowService.getProcess(processId);
        TaskDefinition task = WorkflowUtils.getEntryTaskForCurrentUser(planClasse, userSupplier.getUser());
        return serializeActivity(task);
        
    }
    
    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("processId") String processId) {
        Process planClasse = workflowService.getProcess(processId);
        TaskDefinition task = WorkflowUtils.getEntryTaskForCurrentUser(planClasse, userSupplier.getUser());
        return serializeTaskService(task);
    }
    
    private FluentMap<String, Object> serializeTaskService(TaskDefinition task) {
        List<FluentMap<String, Object>> list = list();
        list.add(map("writable", true,
                "description", task.getDescription(),
                "_id", task.getId()));
        FluentMap<String, Object> map = map(
                "data", list,
                "total", list.size()
        );
        return map;
    }
    
    private FluentMap<String, Object> serializeActivity(TaskDefinition task) {
        AtomicInteger i = new AtomicInteger(0);
        return map("data", map(
                "writable", true,
                "description", task.getDescription(),
                "instructions", task.getInstructions(),
                "attributes", task.getVariables().stream().map((x) -> serializeVariable(x).with("index", i.getAndIncrement())).collect(toList()),
                "_id", task.getId()).with("widgets", task.getWidgets().stream().map((p) -> serializationUtils.serializeWidget(p)).collect(toList()))
        );
    }
    
    private FluentMap<String, Object> serializeVariable(TaskAttribute variable) {
        return map(
                "writable", variable.isWritable(),
                "mandatory", variable.isMandatory(),
                "_id", variable.getName()
        );
    }
}
