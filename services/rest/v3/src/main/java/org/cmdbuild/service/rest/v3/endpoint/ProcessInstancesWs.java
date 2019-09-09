package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.util.Collections.emptyMap;

import java.util.Map;

import org.cmdbuild.common.utils.PagedElements;

import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.cardfilter.CardFilterService;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.workflow.WorkflowService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.service.rest.v3.serializationhelpers.FlowWsSerializationHelper;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.data.filter.utils.CmdbSorterUtils;
import static org.cmdbuild.service.rest.v3.endpoint.CardWs.mapClassValues;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_INSTANCE_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF_GOTOPAGE;
import static org.cmdbuild.service.rest.v3.endpoint.CardWs.mapSorterAttributeNames;
import static org.cmdbuild.service.rest.v3.endpoint.ProcessActivityWs.handlePositionOfAndGetMeta;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.WorkflowGraphService;

@Path("{a:processes}/{" + PROCESS_ID + "}/{b:instances}/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstancesWs {

    private final WorkflowGraphService graphService;
    private final WorkflowService workflowService;
    private final FlowWsSerializationHelper converterService;
    private final CardFilterService filterService;

    public ProcessInstancesWs(WorkflowGraphService graphService, WorkflowService workflowService, FlowWsSerializationHelper converterService, CardFilterService filterService) {
        this.graphService = checkNotNull(graphService);
        this.workflowService = checkNotNull(workflowService);
        this.converterService = checkNotNull(converterService);
        this.filterService = checkNotNull(filterService);
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam(PROCESS_ID) String processId, WsFlowData processInstance) {
        Process processClass = workflowService.getProcess(processId);
        FlowAdvanceResponse response = workflowService.startProcess(
                processId,
                convertInputValuesForFlow(processClass, processInstance),
                //				adaptWidgets(processInstance.getWidgets()),
                processInstance.isAdvance());
        return response(toData(response));
    }

    @PUT
    @Path("{" + PROCESS_INSTANCE_ID + "}")
    public Object update(@PathParam(PROCESS_ID) String planClassId, @PathParam(PROCESS_INSTANCE_ID) Long flowCardId, WsFlowData processInstance) {
        Flow flowCard = workflowService.getFlowCard(planClassId, flowCardId);
        Task task = workflowService.getTask(flowCard, checkNotBlank(processInstance.getActivity(), "must set 'activity' param"));

        Map<String, Object> map = convertInputValuesForFlow(flowCard.getType(), processInstance);
        map = convertTaskValues(task, map);

        FlowAdvanceResponse response = workflowService.updateProcess(planClassId,
                flowCardId,
                task.getId(),
                map,
                //				adaptWidgets(processInstance.getWidgets()),
                processInstance.isAdvance());
        return response(toData(response));
    }

    @GET
    @Path("{" + PROCESS_INSTANCE_ID + "}")
    public Object read(@PathParam(PROCESS_ID) String planClasseId, @PathParam(PROCESS_INSTANCE_ID) Long flowCardId) {
        Flow card = workflowService.getFlowCard(planClasseId, flowCardId);
        return response(converterService.serializeFlow(card));

    }

    @GET
    @Path("{" + PROCESS_INSTANCE_ID + "}/graph/")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler plotGraph(@PathParam(PROCESS_ID) String processId, @PathParam(PROCESS_INSTANCE_ID) Long cardId, @QueryParam("simplified") boolean simplified) {
        Flow card = workflowService.getFlowCard(processId, cardId);
        DataSource graph;
        if (simplified) {
            graph = graphService.getSimplifiedGraphImageForFlow(card);
        } else {
            graph = graphService.getGraphImageForFlow(card);
        }
        return new DataHandler(graph);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(
            @PathParam(PROCESS_ID) String processId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sorterStr,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(POSITION_OF) Long positionOfCard,
            @QueryParam(POSITION_OF_GOTOPAGE) @DefaultValue(TRUE) Boolean goToPage) {
        Process found = workflowService.getProcess(processId);

        CmdbFilter filter = CmdbFilterUtils.parseFilter(getFilterOrNull(filterStr));//TODO map filter attribute names
        CmdbSorter sorter = mapSorterAttributeNames(CmdbSorterUtils.parseSorter(sorterStr));
        // TODO do it better
//		// <<<<<
//		String regex = "\"attribute\"[\\s]*:[\\s]*\"" + UNDERSCORED_STATUS + "\"";
//		String replacement = "\"attribute\":\"" + ATTR_FLOW_STATUS + "\"";
//		String _filter = defaultString(getFilter(filter)).replaceAll(regex, replacement);
//		// <<<<<
//		Iterable<String> attributes = activeAttributes(found);
//		Iterable<String> _attributes = concat(attributes, asList(ATTR_FLOW_STATUS));
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder()
                //				.onlyAttributes(_attributes)
                .withFilter(filter)
                .withSorter(sorter)
                .withPaging(offset, limit)
                .withPositionOf(positionOfCard, goToPage)
                .build();
        
        PagedElements< Flow> elements = workflowService.getUserFlowCardsByClasseIdAndQueryOptions(found.getName(), queryOptions);

        return response(elements.stream().map(converterService::serializeFlow).collect(toList()), elements.totalSize(), handlePositionOfAndGetMeta(queryOptions, elements));

    }

    @DELETE
    @Path("{" + PROCESS_INSTANCE_ID + "}")
    public void delete(@PathParam(PROCESS_ID) String processId, @PathParam(PROCESS_INSTANCE_ID) Long instanceId) {
        workflowService.abortProcess(processId, instanceId);
    }

    private Object toData(FlowAdvanceResponse response) {
        List tasklist = response.getTasklist().stream().map((task) -> converterService.taskToTaskResponseWithFullDetail(response.getFlowCard(), task)).collect(toList());
        return converterService.serializeFlow(response.getFlowCard()).with("_flowStatus", response.getAdvancedFlowStatus().name(), "_flowId", response.getFlowId(), "_tasklist", tasklist);
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
        return CardWs.getFilterOrNull(filter, (id) -> filterService.getById(id).getConfiguration());
    }

    public static class WsFlowData {

        private final Map<String, Object> values;
        private final boolean advance;
        private final String taskId;

        @JsonCreator
        public WsFlowData(Map<String, Object> values) {
            this.values = mapClassValues(checkNotNull(values)).immutable();
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
