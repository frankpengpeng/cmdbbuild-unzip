package org.cmdbuild.workflow.river.engine.xpdl;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.FluentIterable.concat;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.MoreCollectors.onlyElement;
import com.google.common.collect.Multimap;
import java.io.StringReader;
import static java.lang.String.format;
import java.lang.reflect.Array;
import java.util.Collection;
import static java.util.Collections.singletonList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.workflow.river.engine.core.Step;
import org.cmdbuild.workflow.river.engine.core.Step.IncomingHandler;
import org.cmdbuild.workflow.river.engine.core.Step.OutgoingHandler;
import static org.cmdbuild.workflow.river.engine.core.EmptyOutgoingHandler.goingNowere;
import org.cmdbuild.workflow.river.engine.core.FixedOutgoingHandler;
import org.cmdbuild.workflow.river.engine.core.PlanImpl;
import org.cmdbuild.workflow.river.engine.core.StepImpl;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Activities;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Activity;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Condition;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.ExtendedAttribute;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.ExtendedAttributes;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.PackageType;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.ProcessType;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Transition;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Transitions;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.river.engine.core.StepTransitionImpl;
import org.cmdbuild.workflow.river.engine.core.VariableInfoImpl;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.DataFields;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.DataType;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.TypeDeclaration;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.ActivitySet;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.ActivitySets;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.BlockActivity;
import static org.cmdbuild.utils.lang.CmCollectionUtils.queue;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.workflow.river.engine.core.FromTaskOutgoingHandler;
import static org.cmdbuild.workflow.river.engine.core.Step.IncomingHandler.ACTIVATE_WHEN_ALL_INCOMING_STEPS_HAVE_COMPLETED;
import static org.cmdbuild.workflow.river.engine.core.Step.IncomingHandler.ACTIVATE_WHEN_ANY_INCOMING_STEP_HAVE_COMPLETED;
import org.cmdbuild.workflow.river.engine.task.ScriptTaskExtraAttr;
import org.cmdbuild.workflow.river.engine.task.TaskImpl;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TASK_ATTR_ACTIVITY_ID;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TASK_ATTR_NAME;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TASK_ATTR_PERFORMER_TYPE;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TASK_ATTR_PERFORMER_VALUE;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TASK_PERFORMER_TYPE_EXPR;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TASK_PERFORMER_TYPE_ROLE;
import org.cmdbuild.workflow.river.engine.beans.XpdlParticipant;
import org.cmdbuild.workflow.river.engine.beans.XpdlParticipants;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Event;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Implementation;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Performer;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Performers;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.TaskScript;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.TransitionRestriction;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.TransitionRestrictions;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.multimap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.river.engine.core.StepTransition;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlUtils.buildStepIdPrefixFromParentActivityId;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlUtils.buildUserTaskId;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Participants;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

public class XpdlParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, String> defaultAttributes = map();

    public static RiverPlan parseXpdlWithDefaultOptions(String xpdlContent) {
        return new XpdlParser().parseXpdl(xpdlContent);
    }

    public RiverPlan parseXpdl(String xpdlContent) {
        return new XpdlParserHelper(xpdlContent).parseXpdl();
    }

    public XpdlParser withDefaultAttributes(Map<String, String> attrs) {
        defaultAttributes.putAll(attrs);
        return this;
    }

    private class XpdlParserHelper {

        private final String xpdlContent;
        private String planId, defaultScriptType;
        private PackageType xpdlPackage;

        private String processName;
        private final List<XpdlActivityWrapper> activities = list();
        private final List<SimpleTransitionData> transitions = list();
        private final Map<String, String> extendedAttributes = map();
        private final Set<String> entryPointIds = set();
        private final Map<String, RiverVariableInfo<?>> globals = map();
        private final Map<String, String> attributes = map();
        private final Map<String, ActivitySet> activitySets = map();

        private final Map<String, Step> stepsById = map();
        private final List<StepTransition> flags = list();

        private XpdlParticipants participants;

        public XpdlParserHelper(String xpdlContent) {
            this.xpdlContent = checkNotBlank(xpdlContent, "xpdl content cannot be blank");
        }

        private RiverPlan parseXpdl() {
            planId = randomId();//hash(xpdlContent);

            parseXpdlXmlContent();
//			checkVersion();

            loadProcess();
            loadAttributes();
            loadGlobals();
            loadParticipants();
            preloadActivitySets();
            loadTransitions();
            loadActivities();
            loadExtendedAttributes();

            buildSteps();
            buildFlags();

            trimNopStartingSteps();

            return PlanImpl.builder()
                    .withXpdlContent(xpdlContent)
                    .withPlanId(planId)
                    .withPlanName(processName)
                    .withGlobals(globals)
                    .withEntryPoints(entryPointIds)
                    .withStepsAndFlags(stepsById.values(), flags)
                    .withAttributes(map(defaultAttributes).with(attributes))
                    .build();
        }

        private Step getStepById(String stepId) {
            return checkNotNull(stepsById.get(stepId), "step not found for id = %s", stepId);
        }

        private void trimNopStartingSteps() {
            logger.debug("trimming noop starting steps");
            Queue<String> stepsToCheckIds = queue(entryPointIds);
            while (!stepsToCheckIds.isEmpty()) {
                String entryPointStepId = stepsToCheckIds.poll();
                Step step = getStepById(entryPointStepId);
                if (shouldTrimNopStartingStep(step)) {
                    trimNopStartingStep(step);
                    stepsToCheckIds = queue(entryPointIds);
                }
            }
        }

        private boolean shouldTrimNopStartingStep(Step step) {
            return step.getTask().isNoop()
                    && step.getOutgoingHandler() instanceof FixedOutgoingHandler
                    && ((FixedOutgoingHandler) step.getOutgoingHandler()).getOutgoingStepTransitionIds().size() == 1;
        }

        private void trimNopStartingStep(Step step) {
            logger.debug("trimming noop starting step = {}", step);
            checkArgument(shouldTrimNopStartingStep(step));
            String nextStepFlagId = getOnlyElement(((FixedOutgoingHandler) step.getOutgoingHandler()).getOutgoingStepTransitionIds());
            StepTransition flag = flags.stream().filter((f) -> equal(f.getStepTransitionId(), nextStepFlagId)).collect(onlyElement());
            String nextStepId = flag.getTargetStepId();
//		Step nextStep = checkNotNull(stepsById.get(nextStepId));

            checkArgument(flags.remove(flag));
            checkArgument(stepsById.remove(step.getId(), step));
            checkArgument(entryPointIds.remove(step.getId()));

            checkArgument(entryPointIds.add(nextStepId));
        }

        private void buildSteps() {
            logger.debug("building steps");
            activities.forEach((activity) -> {
                buildStep(activity);
            });
        }

        private void buildStep(XpdlActivityWrapper activity) {
            String activityId = activity.getId(),
                    stepId = activityId;
            logger.debug("build step id = {}", stepId);
            IncomingHandler incomingHandler;
            RiverTask task;
            OutgoingHandler outgoingHandler;

            // incoming
            if (activity.isStartEvent()) {
                incomingHandler = ACTIVATE_WHEN_ANY_INCOMING_STEP_HAVE_COMPLETED;
                entryPointIds.add(stepId);
            } else {
                Collection<SimpleTransitionData> incomingTransitions = transitions.stream().filter((t) -> equal(t.getTo(), activityId)).collect(toList());
                if (incomingTransitions.isEmpty()) {
                    throw new XpdlParserException(format("activity %s has 0 incoming transitions", activityId));
                } else if (incomingTransitions.size() == 1) {
                    incomingHandler = ACTIVATE_WHEN_ANY_INCOMING_STEP_HAVE_COMPLETED;
                } else {
                    JoinMode joinMode = activity.getJoinMode();
                    switch (joinMode) {
                        case ALL:
                            incomingHandler = ACTIVATE_WHEN_ALL_INCOMING_STEPS_HAVE_COMPLETED;
                            break;
                        case ANY:
                            incomingHandler = ACTIVATE_WHEN_ANY_INCOMING_STEP_HAVE_COMPLETED;
                            break;
                        default:
                            throw new XpdlParserException(format("unsupported join mode = %s", joinMode));
                    }
                }
            }

            // outgoing
            if (activity.isEndEvent()) {
                outgoingHandler = goingNowere();
            } else {
                Collection<SimpleTransitionData> activityTransitions = transitions.stream().filter((t) -> equal(t.getFrom(), activityId)).collect(toList());
                if (activityTransitions.isEmpty()) {
                    outgoingHandler = goingNowere();
                } else if (activityTransitions.stream().allMatch(SimpleTransitionData::hasNoCondition)) {
                    Set<String> outgoingFlags = activityTransitions.stream().map(SimpleTransitionData::getFlagId).collect(toSet());
                    outgoingHandler = new FixedOutgoingHandler(outgoingFlags);
                } else {
                    outgoingHandler = buildOutgoingHandlerWithConditions(stepId, activityTransitions);
                }
            }

            //task
            task = activity.getTask();

            Step step = new StepImpl(stepId, incomingHandler, task, outgoingHandler);
            stepsById.put(stepId, step);
        }

        private void buildFlags() {
            transitions.forEach((transition) -> {
                checkArgument(!transition.hasCondition());
                Step stepFrom = checkNotNull(stepsById.get(transition.getFrom()), "step not found for transition from = %s", transition.getFrom());
                Step stepTo = checkNotNull(stepsById.get(transition.getTo()), "step not found for transition to = %s", transition.getTo());
                StepTransition flag = new StepTransitionImpl(transition.getFlagId(), stepFrom.getId(), stepTo);
                flags.add(flag);
            });
        }

        private void parseXpdlXmlContent() {
            logger.debug("parseXpdlXmlContent");
            try {
                JAXBContext jAXBContext = JAXBContext.newInstance("org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21:org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_10"); //TODO cache this?
                Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
                JAXBElement<PackageType> jaxbElement = unmarshaller.unmarshal(new StreamSource(new StringReader(xpdlContent)), PackageType.class);
                checkArgument(jaxbElement != null && !jaxbElement.isNil(), "jaxb deserialization returned null element");
                this.xpdlPackage = checkNotNull(jaxbElement.getValue());
            } catch (JAXBException ex) {
                throw new XpdlParserException(ex);
            }
        }

//		private void checkVersion() {
//			XPDLVersion xpdlVersion = xpdlPackage.getPackageHeader().getXPDLVersion();
//			checkArgument(xpdlVersion != null && !isBlank(xpdlVersion.getValue()), "missing xpdl version");
//			logger.debug("xpdl version = {}", xpdlVersion.getValue());
////			checkArgument(equal(xpdlVersion.getValue(), "2.1"), "unsupported xpdl version %s", xpdlVersion.getValue());
//		}
        private void loadProcess() {
            logger.debug("load process");
            if (xpdlPackage.getScript() != null) {
                defaultScriptType = xpdlPackage.getScript().getType();
                logger.debug("default script type = {}", defaultScriptType);
            }
            checkArgument(xpdlPackage.getWorkflowProcesses().getWorkflowProcess().size() == 1, "found %s workflow processes, should be exactly 1", xpdlPackage.getWorkflowProcesses().getWorkflowProcess().size());
            processName = checkNotBlank(xpdlPackage.getWorkflowProcesses().getWorkflowProcess().get(0).getId(), "process name (Id) is null");
            logger.debug("found process name = '{}'", processName);
        }

        private Stream<ProcessType> wfProcess() {
            return xpdlPackage.getWorkflowProcesses().getWorkflowProcess().stream();
        }

        private void loadAttributes() {
            wfProcess().forEach((process) -> {
                process.getContent().stream().filter(instanceOf(ExtendedAttributes.class)).map(ExtendedAttributes.class::cast).forEach(this::loadAttributes);
            });
            if (xpdlPackage.getExtendedAttributes() != null) {
                loadAttributes(xpdlPackage.getExtendedAttributes());
            }
        }

        private void loadAttributes(ExtendedAttributes attrs) {
            attrs.getExtendedAttribute().forEach((attr) -> {
                attributes.put(attr.getName(), attr.getValue());
            });
        }

        private void loadGlobals() {
            wfProcess().forEach((process) -> {
                process.getContent().stream().filter(instanceOf(DataFields.class)).map(DataFields.class::cast).forEach((vars) -> {
                    vars.getDataField().forEach((var) -> {
                        String key = checkNotBlank(var.getId());
                        checkArgument(!var.isIsArray(), "found unsupported array var with id = %s", key);
                        DataType dataType = var.getDataType();
                        Class javaType;
                        if (dataType.getBasicType() != null) {
                            String basicType = checkNotBlank(dataType.getBasicType().getType());
                            javaType = basicTypeToJava(basicType);
                        } else if (dataType.getDeclaredType() != null) {
                            TypeDeclaration typeDeclaration = checkNotNull((TypeDeclaration) dataType.getDeclaredType().getId());
                            String className = checkNotBlank(typeDeclaration.getExternalReference().getLocation());
                            javaType = getClassForName(className);
                        } else {
                            throw new XpdlParserException(format("unable to process var type %s for var id %s", dataType, key));
                        }
                        Optional defaultValue;
                        if (var.getInitialValue() != null) {
                            defaultValue = Optional.ofNullable(getOnlyElement(var.getInitialValue().getContent()));//TODO handle expression in initial value
                        } else {
                            defaultValue = Optional.empty();
                        }
                        RiverVariableInfo variableInfo = new VariableInfoImpl(key, javaType, defaultValue);
                        logger.debug("found global variable = {}", variableInfo);
                        globals.put(key, variableInfo);
                    });
                });
            });
        }

        private void loadParticipants() {
            participants = new XpdlParticipants(list(xpdlPackage.getParticipants())
                    .accept(l -> xpdlPackage.getWorkflowProcesses().getWorkflowProcess().stream().flatMap(p -> p.getContent().stream().filter(Participants.class::isInstance).map(Participants.class::cast)).forEach(l::add))
                    .stream().filter(not(isNull())).filter(p -> p.getParticipant() != null).flatMap(p -> p.getParticipant().stream())
                    .filter((p) -> isNotBlank(p.getId()) && p.getParticipantType() != null && isNotBlank(p.getParticipantType().getType()))
                    .map((p) -> new XpdlParticipant(p.getId(), p.getParticipantType().getType()))
                    .collect(toImmutableList()));
        }

        private Class getClassForName(String className) {
            try {
                Class javaType;
                if (className.endsWith("<>")) {
                    String innerClassName = className.replaceFirst("[<][>]", "");
                    Class innerType = Class.forName(innerClassName);
                    javaType = Array.newInstance(innerType, 0).getClass();//TODO there should be a better way to do this
                } else {
                    javaType = Class.forName(className);
                }
                return javaType;
            } catch (ClassNotFoundException ex) {
                throw new XpdlParserException(format("unable to load class %s", className), ex);
            }
        }

        private Class basicTypeToJava(String basicType) {
            switch (basicType.toUpperCase()) {
                case "PERFORMER":
                    return String.class;//TODO
                case "STRING":
                    return String.class;
                case "BOOLEAN":
                    return Boolean.class;
                case "DATETIME":
                    return Date.class;
                case "DATE":
                    return Date.class;//TODO
                case "TIME":
                    return Date.class;//TODO
                case "INTEGER":
                    return Long.class;
                case "FLOAT":
                    return Double.class;
                default:
                    throw new XpdlParserException(format("unsupported basic var type = %s", basicType));
            }
        }

        private void preloadActivitySets() {
            wfProcess().forEach((wfProcess) -> {
                wfProcess.getContent().stream().filter(instanceOf(ActivitySets.class)).map(ActivitySets.class::cast).forEach((sets) -> {
                    sets.getActivitySet().forEach((activitySet) -> {
                        String activitySetId = checkNotBlank(activitySet.getId());
                        checkArgument(!activitySets.containsKey(activitySetId), "found duplicate activity set id = %s", activitySetId);
                        activitySets.put(activitySetId, activitySet);
                    });
                });
            });
        }

        private void loadActivities() {
            logger.debug("load activities");
            wfProcess().forEach((wfProcess) -> {
                wfProcess.getContent().stream().filter(instanceOf(Activities.class)).forEach((thisActivities) -> {
                    ((Activities) thisActivities).getActivity().forEach((activity) -> {
                        loadActivity(activity);
                    });
                });
            });
        }

        private void loadActivity(Activity activity) {
            if (isActivitySet(activity)) {
                loadActivitySet(activity);
            } else {
                loadSimpleActivity(activity);
            }
        }

        private boolean isActivitySet(Activity activity) {
            return activity.getContent().stream().anyMatch(instanceOf(BlockActivity.class));
        }

        private void loadActivitySet(Activity activity) {
            logger.debug("load activity set id = {}, name = {}", activity.getId(), activity.getName());
            String parentActivityId = getActivityId(activity);
            String activitySetId = checkNotBlank(activity.getContent().stream().filter(instanceOf(BlockActivity.class)).map(BlockActivity.class::cast).findAny().get().getActivitySetId());
            ActivitySet activitySet = checkNotNull(activitySets.get(activitySetId), "activity set not found for id = %s", activitySetId);
            //create activity IN e activity OUT

            String startActivityId = parentActivityId, endActivityId = parentActivityId + "_activityset_outgoing";

            Activity startActivity = activity;
            loadSimpleActivity(startActivity);

            logger.debug("create dummy end activity = {} to handle activity set return point", endActivityId);

            activities.add(new XpdlActivityWrapperBuilder().withId(endActivityId).withJoinMode(JoinMode.ANY).build());

            list(transitions).stream().filter((tr) -> equal(tr.getFrom(), startActivityId)).forEach((transition) -> {
                SimpleTransitionData replacement = SimpleTransitionData.copyOf(transition)
                        .withFrom(endActivityId)
                        .build();
                transitions.remove(transition);
                transitions.add(replacement);
            });

            loadActivitiesAndTransitionsFromActivitySet(buildStepIdPrefixFromParentActivityId(parentActivityId), startActivityId, endActivityId, activitySet);
        }

        private void loadActivitiesAndTransitionsFromActivitySet(String prefix, String startActivityId, String endActivityId, ActivitySet activitySet) {
            try {
                List<XpdlActivityWrapper> list = list(activitySet.getActivities().getActivity().stream().map((activity) -> newActivityData(activity, prefix)).collect(toList()));
                XpdlActivityWrapper startEvent = list.stream().filter(XpdlActivityWrapper::isStartEvent).collect(onlyElement());
                List<XpdlActivityWrapper> endEvents = list.stream().filter(XpdlActivityWrapper::isEndEvent).collect(toImmutableList());

                list.removeAll(list(startEvent).with(endEvents));

                list.add(copyOf(startEvent).withStartEvent(false).build());

                endEvents.stream().map((endEvent) -> copyOf(endEvent).withEndEvent(false).build()).forEach(list::add);

                transitions.add(SimpleTransitionData.builder()
                        .withFrom(startActivityId)
                        .withTo(startEvent.getId())
                        .withFlagId(startActivityId + "_to_" + startEvent.getId())
                        .build());

                endEvents.forEach((endEvent) -> {
                    transitions.add(SimpleTransitionData.builder()
                            .withFrom(endEvent.getId())
                            .withTo(endActivityId)
                            .withFlagId(endEvent.getId() + "_to_" + endActivityId)
                            .build());
                });

                activities.addAll(list);

                activitySet.getTransitions().getTransition().forEach((transition) -> {
                    loadTransition(transition, prefix);
                });
            } catch (Exception ex) {
                throw new XpdlParserException(ex, "error processing activity set = %s", activitySet.getId());
            }
        }

        private XpdlActivityWrapper newActivityData(Activity activity, @Nullable String prefix) {
            return builder().withActivity(activity, prefix).build();
        }

        private String getActivityId(Activity activity) {
            return checkNotBlank(activity.getId(), "activity id is blank for activity = %s", activity);
        }

        private void loadSimpleActivity(Activity activity) {
            logger.debug("load activity id = {}, name = {}", activity.getId(), activity.getName());
            activities.add(newActivityData(activity, null));
        }

        private void loadTransitions() {
            logger.debug("load transitions");
            wfProcess().forEach((wfProcess) -> {
                wfProcess.getContent().stream().filter(instanceOf(Transitions.class)).forEach((thisTransitions) -> {
                    ((Transitions) thisTransitions).getTransition().forEach((transition) -> {
                        loadTransition(transition);
                    });
                });
            });
        }

        private void loadTransition(Transition transition) {
            loadTransition(transition, null);
        }

        private void loadTransition(Transition transition, @Nullable String prefix) {
            SimpleTransitionData transitionData = fromXpdlTransition(transition, prefix);
            logger.debug("load transition from = {}, to = {}, id = {}", transitionData.getFrom(), transitionData.getTo(), transitionData.getFlagId());
            transitions.add(transitionData);
        }

        private void loadExtendedAttributes() {
            logger.debug("load extended attributes");
            if (xpdlPackage.getExtendedAttributes() != null) {
                xpdlPackage.getExtendedAttributes().getExtendedAttribute().forEach((attr) -> loadExtendedAttribute(attr));
            }
            wfProcess().forEach((wfProcess) -> {
                wfProcess.getContent().stream().filter(instanceOf(ExtendedAttributes.class)).forEach((extAttrs) -> {
                    ((ExtendedAttributes) extAttrs).getExtendedAttribute().forEach((attr) -> loadExtendedAttribute(attr));
                });
            });
        }

        private void loadExtendedAttribute(ExtendedAttribute attr) {
            String key = attr.getName(), value = attr.getValue();
            logger.debug("loaded extended attr key = {}, value = {}", key, value);
            extendedAttributes.put(key, value);
        }

        private OutgoingHandler buildOutgoingHandlerWithConditions(String fromActivityId, Collection<SimpleTransitionData> activityTransitions) {
            List<SimpleTransitionData> alwaysActive = activityTransitions.stream().filter(SimpleTransitionData::hasNoCondition).collect(toList());
            List<SimpleTransitionData> conditionalTransitions = activityTransitions.stream().filter(SimpleTransitionData::hasCondition).collect(toList());

            Pair<RiverTask, Set<String>> outgoingHandlerStuff = XpdlConditionScriptBuilder.buildOutgoingHandlerWithConditions(planId, attributes::get, fromActivityId, conditionalTransitions);
            RiverTask conditionTask = outgoingHandlerStuff.getLeft();
            Set<String> allOutgoingStepTransitionIds = outgoingHandlerStuff.getValue();

            String conditionScriptStepId = "check_" + fromActivityId,
                    conditionFlagId = "flag_" + fromActivityId + "_" + conditionScriptStepId;

            IncomingHandler incomingHandler = ACTIVATE_WHEN_ANY_INCOMING_STEP_HAVE_COMPLETED;
            OutgoingHandler outgoingHandler = new FromTaskOutgoingHandler(allOutgoingStepTransitionIds);

            Step conditionProcessingStep = new StepImpl(conditionScriptStepId, incomingHandler, conditionTask, outgoingHandler);
            stepsById.put(conditionScriptStepId, conditionProcessingStep);

            conditionalTransitions.forEach((conditionalTransition) -> {
                transitions.remove(conditionalTransition);
                SimpleTransitionData simpleTransition = SimpleTransitionData.builder()
                        .withFrom(conditionScriptStepId)
                        .withTo(conditionalTransition.getTo())
                        .withFlagId(conditionalTransition.getFlagId())
                        .build();
                transitions.add(simpleTransition);
            });
            SimpleTransitionData simpleTransition = SimpleTransitionData.builder()
                    .withFrom(fromActivityId)
                    .withTo(conditionScriptStepId)
                    .withFlagId(conditionFlagId)
                    .build();
            transitions.add(simpleTransition);

            Collection<String> outgoingFlagsForSurceStep = concat(
                    transform(alwaysActive, SimpleTransitionData::getFlagId),
                    singletonList(simpleTransition.getFlagId())).toSet();
            return new FixedOutgoingHandler(outgoingFlagsForSurceStep);
        }

        private SimpleTransitionData fromXpdlTransition(Transition transition, @Nullable String prefix) {
            prefix = nullToEmpty(prefix);
            String from = prefix + checkNotBlank(transition.getFrom());
            String to = prefix + checkNotBlank(transition.getTo());
            String flagId = prefix + checkNotBlank(transition.getId());
            Condition condition = transition.getCondition();
            boolean hasCondition, hasOtherwiseCondition;
            String conditionScript, conditionScriptType;
            if (condition == null) {
                hasCondition = hasOtherwiseCondition = false;
                conditionScript = conditionScriptType = null;
            } else {
                String conditionType = checkNotBlank(condition.getType(), "condition type not found");
                switch (conditionType) {
                    case "OTHERWISE": {
                        conditionScript = conditionScriptType = null;
                        hasCondition = true;
                        hasOtherwiseCondition = true;
                    }
                    break;
                    case "CONDITION": {
                        try {
                            hasCondition = true;
                            hasOtherwiseCondition = false;
                            logger.debug("found condition content = {}", condition.getContent());
                            conditionScript = checkNotBlank(condition.getContent().stream()
                                    .filter(String.class::isInstance).map(String.class::cast)
                                    .filter(not(StringUtils::isBlank))
                                    .collect(onlyElement()));
                            //TODO handle <xpdl:Expression ScriptType="text/java"/> and other elements
                            conditionScriptType = defaultScriptType;
                        } catch (Exception ex) {
                            throw new XpdlParserException(format("error processing condition content = %s", condition.getContent()), ex);
                        }
                    }
                    break;
                    default:
                        throw new XpdlParserException("unsupported condition type = " + conditionType);
                }
            }
            return SimpleTransitionData.builder()
                    .withFrom(from)
                    .withTo(to)
                    .withFlagId(flagId)
                    .withConditionScript(conditionScript)
                    .withConditionScriptType(conditionScriptType)
                    .withHasCondition(hasCondition)
                    .withHasOtherwiseCondition(hasOtherwiseCondition)
                    .build();
        }

        private XpdlActivityWrapperBuilder builder() {
            return new XpdlActivityWrapperBuilder();
        }

        private XpdlActivityWrapperBuilder copyOf(XpdlActivityWrapper source) {
            return new XpdlActivityWrapperBuilder()
                    .withHasImplementation(source.getHasImplementation())
                    .withStartEvent(source.isStartEvent())
                    .withEndEvent(source.isEndEvent())
                    .withSplitMode(source.splitMode)
                    .withJoinMode(source.joinMode)
                    .withAttributes(source.getAttributes())
                    .withId(source.getId())
                    .withTask(source.getTask());
        }

        private class XpdlActivityWrapper {

            private final boolean hasImplementation,
                    isStartEvent,
                    isEndEvent;
            private final SplitMode splitMode;
            private final JoinMode joinMode;
            private final Multimap<String, String> attributes;
            private final String id;
            private final RiverTask task;

            private XpdlActivityWrapper(XpdlActivityWrapperBuilder builder) {
                this.id = checkNotBlank(builder.stepId);
                this.hasImplementation = (builder.hasImplementation);
                this.isStartEvent = (builder.isStartEvent);
                this.isEndEvent = (builder.isEndEvent);
                this.splitMode = (builder.splitMode);
                this.joinMode = (builder.joinMode);
                this.attributes = multimap(checkNotNull(builder.attributes));
                this.task = checkNotNull(builder.task);
            }

            public String getId() {
                return id;
            }

            public boolean getHasImplementation() {
                return hasImplementation;
            }

            public boolean isStartEvent() {
                return isStartEvent;
            }

            public boolean isEndEvent() {
                return isEndEvent;
            }

            public SplitMode getSplitMode() {
                return checkNotNull(splitMode, "split mode not set for activity %s", getId());
            }

            public JoinMode getJoinMode() {
                return checkNotNull(joinMode, "join mode not set for activity %s", getId());
            }

            public Multimap<String, String> getAttributes() {
                return attributes;
            }

            public RiverTask getTask() {
                return task;
            }

        }

        private class XpdlActivityWrapperBuilder implements Builder<XpdlActivityWrapper, XpdlActivityWrapperBuilder> {

            private boolean hasImplementation;
            private boolean isStartEvent;
            private boolean isEndEvent;
            private SplitMode splitMode;
            private JoinMode joinMode;
            private Multimap<String, String> attributes = multimap();
            private String stepId;
            private RiverTask task;

            public XpdlActivityWrapperBuilder withActivity(Activity activity) {
                return withActivity(activity, null);
            }

            public XpdlActivityWrapperBuilder withActivity(Activity activity, @Nullable String namespace) {
                this.stepId = nullToEmpty(namespace) + checkNotBlank(activity.getId(), "activity id is blank for activity = %s", activity);
                loadTaskAttributes(activity);
                loadImplementation(activity);

                isStartEvent = false;
                isEndEvent = false;
                activity.getContent().stream().filter(instanceOf(Event.class)).map(Event.class::cast).forEach((event) -> {
                    if (event.getStartEvent() != null) {
                        isStartEvent = true;
                    }
                    if (event.getEndEvent() != null) {
                        isEndEvent = true;
                    }
                });
                loadTransitionRestrictions(activity);
                this.task = createTask(activity);
                return this;
            }

            public XpdlActivityWrapperBuilder withHasImplementation(boolean hasImplementation) {
                this.hasImplementation = hasImplementation;
                return this;
            }

            public XpdlActivityWrapperBuilder withStartEvent(boolean isStartEvent) {
                this.isStartEvent = isStartEvent;
                return this;
            }

            public XpdlActivityWrapperBuilder withEndEvent(boolean isEndEvent) {
                this.isEndEvent = isEndEvent;
                return this;
            }

            public XpdlActivityWrapperBuilder withSplitMode(SplitMode splitMode) {
                this.splitMode = splitMode;
                return this;
            }

            public XpdlActivityWrapperBuilder withJoinMode(JoinMode joinMode) {
                this.joinMode = joinMode;
                return this;
            }

            public XpdlActivityWrapperBuilder withAttributes(Multimap<String, String> attributes) {
                this.attributes = attributes;
                return this;
            }

//			public XpdlActivityWrapperBuilder withPlanId(String planId) {//TODO is this required?
//				this.planId = checkNotBlank(planId);
//				return this;
//			}
            public XpdlActivityWrapperBuilder withId(String id) {
                this.stepId = checkNotBlank(id);
                return this;
            }

            public XpdlActivityWrapperBuilder withTask(RiverTask task) {
                this.task = (task);
                return this;
            }

            @Override
            public XpdlActivityWrapper build() {
                if (task == null) {
                    task = buildNoopTask();
                }
                return new XpdlActivityWrapper(this);
            }

            private void loadTransitionRestrictions(Activity activity) {
                Optional<TransitionRestrictions> transitionRestrictionOpt = activity.getContent().stream().filter(instanceOf(TransitionRestrictions.class)).map(TransitionRestrictions.class::cast).findAny();
                if (transitionRestrictionOpt.isPresent()) {
                    TransitionRestriction transitionRestriction = getOnlyElement(transitionRestrictionOpt.get().getTransitionRestriction());
                    logger.debug("found transition restriction = {}", transitionRestriction);
                    if (transitionRestriction.getJoin() != null) {
                        String type = transitionRestriction.getJoin().getType();
                        logger.debug("found transition restriction join = {}", type);
                        if (type.toLowerCase().matches("parallel|inclusive|and|or")) {
                            joinMode = JoinMode.ALL;
                        } else if (type.toLowerCase().matches("exclusive|xor")) {
                            joinMode = JoinMode.ANY;
                        } else {
                            throw new XpdlParserException(format("unsupported transition join type %s", type));//TODO
                        }
                    }
                    if (transitionRestriction.getSplit() != null) {
                        String type = transitionRestriction.getSplit().getType();
                        logger.debug("found transition restriction split = {}", type);
                        if (type.toLowerCase().matches("parallel|inclusive|and|or")) {
                            splitMode = SplitMode.ALL;
                        } else if (type.toLowerCase().matches("exclusive|xor")) {
                            splitMode = SplitMode.CONDITIONAL;
                        } else {
                            throw new XpdlParserException(format("unsupported transction split type %s", type));//TODO
                        }
                    }
                }
            }

            private void loadTaskAttributes(Activity activity) {
                activity.getContent().stream().filter(instanceOf(ExtendedAttributes.class)).map(ExtendedAttributes.class::cast).forEach((attrs) -> {
                    attrs.getExtendedAttribute().forEach((attr) -> {
                        attributes.put(attr.getName(), attr.getValue());
                    });
                });
            }

            private void loadImplementation(Activity activity) {
                Optional<Implementation> optional = activity.getContent().stream().filter(instanceOf(Implementation.class)).map(Implementation.class::cast).findAny();
                if (!optional.isPresent()) {
                    hasImplementation = false;
                } else {
                    Implementation implementation = optional.get();
                    if (implementation.getNo() != null) {
                        hasImplementation = false;
                    } else {
                        hasImplementation = true;
                    }
                }
            }

            private RiverTask createTask(Activity activity) {
                Optional<Implementation> implOptional = activity.getContent().stream().filter(instanceOf(Implementation.class)).map(Implementation.class::cast).findAny();
                if (implOptional.isPresent()) {
                    Implementation implementation = implOptional.get();
                    if (implementation.getNo() != null) {
                        Optional<Performers> performerOpt = activity.getContent().stream().filter(instanceOf(Performers.class)).map(Performers.class::cast).findAny();
                        if (performerOpt.isPresent()) {
                            String taskId = buildUserTaskId(stepId);
                            logger.debug("create user task {}", taskId);
                            Performer performerElement = getOnlyElement(performerOpt.get().getPerformer());
                            String performerValue = checkNotBlank(performerElement.getValue());
                            XpdlParticipant xpdlParticipant = participants.getXpdlParticipant(performerValue);
                            String performerType;
                            if (xpdlParticipant == null || !equal(xpdlParticipant.getType(), "ROLE")) {
                                performerType = TASK_PERFORMER_TYPE_EXPR;
                            } else {
                                performerType = TASK_PERFORMER_TYPE_ROLE;
                            }

                            attributes.put(TASK_ATTR_ACTIVITY_ID, stepId);
                            attributes.put(TASK_ATTR_PERFORMER_VALUE, performerValue);
                            attributes.put(TASK_ATTR_PERFORMER_TYPE, performerType);
                            attributes.put(TASK_ATTR_NAME, activity.getName());
//						attributes.put(TASK_ATTR_DESCRIPTION, activity.getDescription());TODO get description
                            return TaskImpl.user()
                                    .withPlanId(planId)
                                    .withTaskId(taskId)
                                    .withAttributes(attributes)
                                    .build();
                        } else {
                            logger.debug("create noop task {} (noop) (for implementation NO and no performer)", stepId);
                            return TaskImpl.noop()
                                    .withPlanId(planId)
                                    .withTaskId(stepId + "_noop")
                                    .withAttributes(attributes)
                                    .build();
                        }
                    } else if (implementation.getTask() != null) {
                        String taskId = stepId + "_script";
                        logger.debug("create script task {}", taskId);
                        org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Task taskEntry = implementation.getTask();
                        TaskScript taskScript = taskEntry.getTaskScript();
                        checkNotNull(taskScript, "unsupported task with empty task script = %s", taskEntry);
                        String scriptContent = (String) taskScript.getScript().getContent().get(0);
                        String scriptType = taskScript.getScript().getScriptType();
                        TaskImpl.SimpleTaskBuilder builder;

                        if (attributes.containsKey("INLINE")) {
                            builder = TaskImpl.inline();
                            attributes.removeAll("INLINE");
                        } else if (attributes.containsKey("BATCH")) {
                            builder = TaskImpl.batch();
                            attributes.removeAll("BATCH");
                        } else {
                            builder = TaskImpl.inline();
                        }

                        return builder
                                .withPlanId(planId)
                                .withTaskId(taskId)
                                .withAttributes(attributes)
                                .withExtraAttr(new ScriptTaskExtraAttr(scriptType, scriptContent))
                                .build();
                    }
                }
                logger.debug("no implementation found, create noop task {} (noop)", stepId);
                return buildNoopTask();
            }

            private RiverTask buildNoopTask() {
                return TaskImpl.noop()
                        .withPlanId(planId)
                        .withTaskId(checkNotBlank(stepId) + "_noop")
                        .withAttributes(attributes)
                        .build();
            }

        }
    }

    public enum SplitMode {
        ALL, CONDITIONAL
    }

    public enum JoinMode {
        ALL, ANY
    }
}
