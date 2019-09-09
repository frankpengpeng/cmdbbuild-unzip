/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.serializationhelpers;

import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.utils.ProcessStatusUtils;
import org.cmdbuild.service.rest.common.utils.WsSerializationUtils;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_ID;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.service.rest.common.utils.ProcessStatus;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.core.LookupHelper;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelper;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.workflow.river.dao.ExtendedRiverPlanRepository;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class FlowWsSerializationHelper {

    private final CardWsSerializationHelper cardSerializationHelper;
    private final AttributeTypeConversionService attributeDetailService;
    private final LookupHelper lookupHelper;
    private final WidgetService widgetService;
    private final ExtendedRiverPlanRepository planRepository;

    public FlowWsSerializationHelper(CardWsSerializationHelper cardSerializationHelper, AttributeTypeConversionService attributeDetailService, LookupHelper lookupHelper, WidgetService widgetService, ExtendedRiverPlanRepository planRepository) {
        this.cardSerializationHelper = checkNotNull(cardSerializationHelper);
        this.attributeDetailService = checkNotNull(attributeDetailService);
        this.lookupHelper = checkNotNull(lookupHelper);
        this.widgetService = checkNotNull(widgetService);
        this.planRepository = checkNotNull(planRepository);
    }

    public Object taskToTaskResponseWithFullDetail(Flow card, Task task) {
        return serializeTask(card.getType(), task)
                .with(
                        "_id", task.getId(),
                        "writable", task.isWritable(),
                        "performer", task.getPerformerName()
                );
    }

    public Object buildTaskResponseWithFullDetail(Process planClasse, TaskDefinition taskDefinition) {
        return serializeTask(planClasse, taskDefinition)
                .with("_id", taskDefinition.getId(), "writable", true);
    }

    private FluentMap serializeTask(Process planClasse, TaskDefinition definition) {
        return serializeTaskWithoutWidgets(planClasse, definition)
                .with("widgets", definition.getWidgets().stream()
                        .map((w) -> widgetService.widgetDataToWidget(w, planRepository.getPlanByClasseId(planClasse.getName()).getDefaultValues()))//TODO move this somewhere else, not in ws layer
                        .map(WsSerializationUtils::serializeWidget)
                        .collect(toList()));
    }

    private FluentMap serializeTask(Process planClasse, Task task) {
        return serializeTaskWithoutWidgets(planClasse, task.getDefinition())
                .with("widgets", list(transform(task.getWidgets(), WsSerializationUtils::serializeWidget)));
    }

    private FluentMap serializeTaskWithoutWidgets(Process planClasse, TaskDefinition definition) {
        Map<String, Object> attributesByName = transformValues(uniqueIndex(planClasse.getCoreAttributes(), Attribute::getName), attributeDetailService::serializeAttributeType);
        return map("description", definition.getDescription(),
                "instructions", definition.getInstructions())
                .with("attributes", buildAttributesResponse(attributesByName, definition));
    }

    private Object buildAttributesResponse(Map<String, Object> attributesByName, TaskDefinition definition) {
        AtomicInteger index = new AtomicInteger(0);
        return definition.getVariables().stream().map((attr) -> {
            return map(
                    "_id", attr.getName(),
                    "mandatory", attr.isMandatory(),
                    "writable", attr.isWritable(),
                    "action", attr.isAction(),
                    "index", index.getAndIncrement()
            )
                    .skipNullValues()
                    .with("detail", attributesByName.get(attr.getName()));
        }).collect(toList());
    }

    public static Object taskToTaskResponseWithBasicDetail(Task task) {
        return map(
                "_id", task.getId(),
                "writable", task.isWritable(),
                "description", Optional.ofNullable(task.getDefinition()).map(TaskDefinition::getDescription).orElse("")
        );
    }

    public FluentMap<String, Object> serializeFlow(Flow flowCard) {
        ProcessStatus processStatus = lookupHelper.getFlowStatusLookup(flowCard).transform(ProcessStatusUtils::toProcessStatus).orNull();
        return (FluentMap) cardSerializationHelper.serializeCard(flowCard).with(
                "name", flowCard.getFlowId(),
                "status", processStatus != null ? processStatus.getId() : null,
                "_status_description", processStatus == null ? null : processStatus.getDescription()
        );

//    private static final Set<String> WF_SYSTEM_ONLY_COLUMNS = ImmutableSet.of(ATTR_TASK_INSTANCE_ID, ATTR_NEXT_EXECUTOR, ATTR_PREV_EXECUTORS, ATTR_TASK_DEFINITION_ID, FLOW_ATTR_DATA);
//        filterKeys(flowCard.getAllValuesAsMap(), not(WF_SYSTEM_ONLY_COLUMNS::contains)).forEach((key, value) -> {
//            Attribute attribute = flowCard.getType().getAttributeOrNull(key);
//            Object output;
//            if (attribute == null) {
//                output = value;
//            } else {
//                CardAttributeType<?> attributeType = attribute.getType();
//                output = toClient(attributeType, value);
//                if (value != null && value instanceof IdAndDescriptionImpl && ((IdAndDescriptionImpl) value).getId() != null) {
//                    map.put("_" + key + "_description", ((IdAndDescriptionImpl) value).getDescription());
//                }
//            }
//            map.put(key, output);
//        });
//        map.skipNullValues().accept((m) -> WsSerializationUtils.expandReferenceAttributes(flowCard, m::put));
//        return map;
    }
}
