package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.cmdbuild.dao.beans.IdAndDescription;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.workflow.model.Process;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ACTIVE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.WorkflowService;

@Path("processes/")
@Produces(APPLICATION_JSON)
public class ProcessWsV2 {

    private final WorkflowService workflowService;

    public ProcessWsV2(WorkflowService workflowService) {
        this.workflowService = checkNotNull(workflowService);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@QueryParam(ACTIVE) boolean activeOnly, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        Collection<Process> all = workflowService.getAllProcessClasses();
        Map metaRef = map();
        for (Process p : all) {
            for (Attribute attribute : p.getAllAttributes()) {
                if (p instanceof IdAndDescription) {
                    IdAndDescription idAndDesc = (IdAndDescription) attribute;
                    metaRef.put(idAndDesc.getId(), map("description", idAndDesc.getDescription()));
                }
            }
        }
        return map("data", all.stream().map(this::processGeneralDataMapConsumer).collect(toList()), "meta", map("total", all.size(), "references", metaRef));
    }

    @GET
    @Path("{processId}/")
    public Object readOne(@PathParam("processId") String processId) {
        Process classe = workflowService.getProcess(processId);
        Map metaRef = map();
        for (Attribute attribute : classe.getAllAttributes()) {
            if (attribute instanceof IdAndDescription) {
                IdAndDescription idAndDesc = (IdAndDescription) attribute;
                metaRef.put(idAndDesc.getId(), map("description", idAndDesc.getDescription()));
            }
        }
        return map("data", processSpecificDataMapConsumer(classe), "meta", map("total", null, "references", metaRef));
    }

    @GET
    @Path("{processId}/generate_id")
    public Object generateId(@PathParam("processId") String processId) {
        throw new UnsupportedOperationException("not supported");
    }

    private CmMapUtils.FluentMap<String, Object> processSpecificDataMapConsumer(Process p) {
        return CmMapUtils.<String, Object>map(
                "statuses", "", //TODO
                "defaultStatus", "", //TODO
                "defaultOrder", "", //TODO
                "description_attribute_name", "", //TODO
                "name", p.getName(),
                "description", p.getDescription(),
                "parent", p.getParent(),
                "prototype", p.isSuperclass(),
                "_id", p.getId()
        );
    }

    private CmMapUtils.FluentMap<String, Object> processGeneralDataMapConsumer(Process p) {
        return CmMapUtils.<String, Object>map(
                "name", p.getName(),
                "description", p.getDescription(),
                "parent", p.getParent(),
                "prototype", p.isSuperclass(),
                "_id", p.getName()
        );
    }

}
