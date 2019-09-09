package org.cmdbuild.service.rest.v2.endpoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.annotation.Nullable;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.common.utils.PositionOf;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.IdAndDescription;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_INSTANCE_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.data.filter.utils.CmdbSorterUtils;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.service.rest.v2.serializationhelpers.FlowConverterServicev2;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Task;

@Path("processes/{processId}/instances/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstancesWsV2 {

    private final WorkflowService workflowService;
    private final FlowConverterServicev2 converterService;
    private final CardFilterService filterService;

    public ProcessInstancesWsV2(WorkflowService workflowService, FlowConverterServicev2 converterService, CardFilterService filterService) {
        this.workflowService = checkNotNull(workflowService);
        this.converterService = checkNotNull(converterService);
        this.filterService = checkNotNull(filterService);
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("processId") String processId, WsFlowData processInstance) {
        Process processClass = workflowService.getProcess(processId);
        FlowAdvanceResponse response = workflowService.startProcess(
                processId,
                convertInputValuesForFlow(processClass, processInstance),
                processInstance.isAdvance());
        return map("data", response.getFlowCard().getCardId());
    }

    @GET
    @Path("{processInstanceId}")
    public Object readOne(@PathParam(PROCESS_ID) String planClasseId, @PathParam(PROCESS_INSTANCE_ID) Long flowCardId) {
        Flow card = workflowService.getFlowCard(planClasseId, flowCardId);
        Map metaRef = map();
        for (Map.Entry<String, Object> attribute : card.getAttributeValues()) {
            if (card.get(attribute.getKey()) instanceof IdAndDescription) {
                IdAndDescription idAndDesc = (IdAndDescription) card.get(attribute.getKey());
                metaRef.put(idAndDesc.getId(), map("description", idAndDesc.getDescription()));
            }
        }
        return map("data", converterService.serializeFlow(card), "meta", map("total", null, "references", metaRef));
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam(PROCESS_ID) String processId,
            @QueryParam(FILTER) String filter,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(POSITION_OF) Long instanceId) {

        Process found = workflowService.getProcess(processId);
        if (instanceId != null && limit == null) {
            limit = Integer.MAX_VALUE;
        }
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder()
                .withFilter(CmdbFilterUtils.parseFilter(getFilterOrNull(filter)))
                .withSorter(CmdbSorterUtils.parseSorter(sort))
                .withPaging(offset, limit)
                .withPositionOf(instanceId, Boolean.TRUE)
                .build();
        PagedElements<? extends Flow> elements;

        elements = workflowService.getUserFlowCardsByClasseIdAndQueryOptions(found.getName(), queryOptions);
        Map metaRef = map();
        for (Card card : elements) {
            for (Map.Entry<String, Object> attribute : card.getAttributeValues()) {
                if (card.get(attribute.getKey()) instanceof IdAndDescription) {
                    IdAndDescription idAndDesc = (IdAndDescription) card.get(attribute.getKey());
                    metaRef.put(idAndDesc.getId(), map("description", idAndDesc.getDescription()));
                }
            }
        }
        return map("data", elements.stream().map(converterService::serializeFlow).collect(toList()), "meta", map("total", elements.totalSize(), "positions", handlePositionOfAndGetMeta(queryOptions, elements)));
    }

    @PUT
    @Path("{processInstanceId}")
    public Object update(@PathParam("processId") String processId, @PathParam("processInstanceId") Long instanceId, WsFlowData processInstance) {

        Flow flowCard = workflowService.getFlowCard(processId, instanceId);
        Task task = workflowService.getTask(flowCard, checkNotBlank(processInstance.getActivity(), "must set 'activity' param"));

        Map<String, Object> map = convertInputValuesForFlow(flowCard.getType(), processInstance);
        map = convertTaskValues(task, map);

        FlowAdvanceResponse response = workflowService.updateProcess(processId,
                instanceId,
                task.getId(),
                map,
                processInstance.isAdvance());

        return map("data", toData(response));
    }

    @DELETE
    @Path("{processInstanceId}")
    public Object delete(@PathParam("processId") String processId, @PathParam("processInstanceId") Long instanceId) {
        return null;
    }

    public static Map<String, Object> handlePositionOfAndGetMeta(DaoQueryOptions queryOptions, PagedElements paged) {
        long offset = queryOptions.getOffset();
        if (paged.hasPositionOf()) {
            PositionOf positionOf = paged.getPositionOf();
            Map positionMeta;
            if (positionOf.foundCard()) {
                offset = positionOf.getActualOffset();
                positionMeta = map("hasPosition", true,
                        "position", positionOf.getPositionInTable());
            } else {
                positionMeta = map("found", false);
            }
            return map(queryOptions.getPositionOf(), positionMeta);
        } else {
            return emptyMap();
        }
    }

    private Object toData(FlowAdvanceResponse response) {
        List tasklist = response.getTasklist().stream().map((task) -> converterService.taskToTaskResponseWithFullDetail(response.getFlowCard(), task)).collect(toList());
        return converterService.serializeFlow(response.getFlowCard()).with("_flowStatus", response.getAdvancedFlowStatus().name(), "_flowId", response.getFlowId(), "_tasklist", tasklist);
    }

    private Map<String, Object> convertTaskValues(Task task, Map<String, Object> values) {
        Map<String, Object> map = map(values);
        task.getWidgets().forEach((w) -> {
            if (w.hasOutputKey() && w.hasOutputType()) {
                Object rawValue = values.get(w.getOutputKey());
                Object value = rawToSystem(w.getOutputType(), rawValue);
                map.put(w.getOutputKey(), value);
            }
        });
        return map;
    }

    @Nullable
    private String getFilterOrNull(@Nullable String filter) {
        return org.cmdbuild.service.rest.v3.endpoint.CardWs.getFilterOrNull(filter, (id) -> filterService.getById(id).getConfiguration());
    }

    private Map<String, Object> convertInputValuesForFlow(Process userProcessClass, WsFlowData processInstanceAdvanceable) {
        return convertValues(userProcessClass, firstNonNull(processInstanceAdvanceable.getValues(), emptyMap()));
    }

    private Map<String, Object> convertValues(Process planClasse, Map<String, Object> values) {
        Map<String, Object> map = map();
        values.forEach((key, value) -> {
            if (planClasse.hasAttribute(key)) {
                value = rawToSystem(planClasse.getAttribute(key).getType(), value);
            }
            map.put(key, value);
        });
        return map;
    }

    public static class WsFlowData {

        private final Map<String, Object> values;
        private final boolean advance;
        private final String taskId;

        @JsonCreator
        public WsFlowData(Map<String, Object> values) {
            this.values = values;
            advance = toBooleanOrDefault(values.get("_advance"), false);
            taskId = emptyToNull(toStringOrNull(values.get("_activity")));
        }

        public Map<String, Object> getValues() {
            return values;
        }

        public boolean isAdvance() {
            return advance;
        }

        public @Nullable
        String getActivity() {
            return taskId;
        }

    }

}
