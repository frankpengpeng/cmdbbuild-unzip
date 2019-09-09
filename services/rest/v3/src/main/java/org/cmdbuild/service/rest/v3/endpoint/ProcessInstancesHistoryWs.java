package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.data.filter.SorterElement.SorterElementDirection.DESC;

import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.service.rest.common.utils.ProcessStatusUtils;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_INSTANCE_ID;
import org.cmdbuild.service.rest.common.utils.ProcessStatus;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import org.cmdbuild.service.rest.v3.serializationhelpers.FlowWsSerializationHelper;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.workflow.FlowHistoryService;
import org.cmdbuild.workflow.core.LookupHelper;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.FlowActivity;

@Path("processes/{" + PROCESS_ID + "}/instances/{" + PROCESS_INSTANCE_ID + "}/history")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstancesHistoryWs {

    private final FlowHistoryService service;
    private final FlowWsSerializationHelper helper;
    private final LookupHelper lookupHelper;

    public ProcessInstancesHistoryWs(FlowHistoryService service, FlowWsSerializationHelper helper, LookupHelper lookupHelper) {
        this.service = checkNotNull(service);
        this.helper = checkNotNull(helper);
        this.lookupHelper = checkNotNull(lookupHelper);
    }

    @GET
    @Path("")
    public Object getHistory(@PathParam(PROCESS_ID) String classId, @PathParam(PROCESS_INSTANCE_ID) Long cardId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed) {
        DaoQueryOptionsImpl query = DaoQueryOptionsImpl.builder()
                .withPaging(offset, limit)
                .orderBy(ATTR_BEGINDATE, DESC)
                .build();
        PagedElements<Flow> history = service.getHistory(classId, cardId, query);
        return response(history.stream().map(detailed ? this::serializeDetailedCard : this::serializeBasicCard), history.totalSize());
    }

    @GET
    @Path("{recordId}/")
    public Object getHistoryRecord(@PathParam(PROCESS_ID) String classId, @PathParam(PROCESS_INSTANCE_ID) Long id, @PathParam("recordId") Long recordId) {
        Flow record = service.getHistoryRecord(classId, recordId);
        checkArgument(equal(record.getCurrentId(), id));
        return response(serializeDetailedCard(record));
    }

    private FluentMap<String, Object> serializeBasicCard(Flow flow) {//TODO improve this, remove duplicate code
        return (FluentMap) map(
                "_type", flow.getType().getName(),
                "_id", flow.getId(),
                "_endDate", toIsoDateTime(flow.getEndDate()),
                "_beginDate", toIsoDateTime(flow.getBeginDate()),
                "_user", flow.getUser(),
                "_status", flow.getCardStatus().name()).accept(m -> {

            ProcessStatus processStatus = lookupHelper.getFlowStatusLookup(flow).transform(ProcessStatusUtils::toProcessStatus).orNull();//TODO duplicate code with FlowWsSerializationHelper
            m.put("status", processStatus != null ? processStatus.getId() : null,
                    "_status_description", processStatus == null ? null : processStatus.getDescription());
        }).accept((Consumer) serializieTaskInfos(flow));
    }

    private FluentMap<String, Object> serializeDetailedCard(Flow flow) {//TODO improve this, remove duplicate code
        return helper.serializeFlow(flow).with(
                "_endDate", toIsoDateTime(flow.getEndDate()),
                "_status", flow.getCardStatus().name()).accept((Consumer) serializieTaskInfos(flow));
    }

    private Consumer<FluentMap> serializieTaskInfos(Flow flow) {
        return c -> {
            List<FlowActivity> activities = flow.getFlowActivities();
            if (activities.size() == 1) {
                FlowActivity activity = getOnlyElement(activities);
                c.put(
                        "_activity_code", activity.getDefinitionId(),
                        "_activity_description", activity.getDefinitionId(),//TODO get description
                        "_activity_performer", activity.getPerformerGroup());
            }
            c.put("activities", activities.stream().map(activity -> map(
                    "code", activity.getDefinitionId(),
                    "description", activity.getDefinitionId(), //TODO get description
                    "performer", activity.getPerformerGroup())).collect(toList()));
        };
    }

}
