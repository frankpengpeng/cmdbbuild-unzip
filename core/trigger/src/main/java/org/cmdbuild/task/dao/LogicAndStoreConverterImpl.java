package org.cmdbuild.task.dao;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.math.NumberUtils.createInteger;
import static org.cmdbuild.common.java.sql.DataSourceTypes.mysql;
import static org.cmdbuild.common.java.sql.DataSourceTypes.oracle;
import static org.cmdbuild.common.java.sql.DataSourceTypes.postgresql;
import static org.cmdbuild.common.java.sql.DataSourceTypes.sqlserver;
import static org.cmdbuild.task.wizardconnector.ConnectorTask.NULL_SOURCE_CONFIGURATION;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.cmdbuild.common.java.sql.DataSourceTypes.DataSourceType;
import org.cmdbuild.logic.taskmanager.Task;
import org.cmdbuild.task.dao.ParameterNames.AsynchronousEvent;
import org.cmdbuild.task.dao.ParameterNames.Connector;
import org.cmdbuild.task.dao.ParameterNames.Generic;
import org.cmdbuild.task.dao.ParameterNames.ReadEmail;
import org.cmdbuild.task.dao.ParameterNames.StartWorkflow;
import org.cmdbuild.task.dao.ParameterNames.SynchronousEvent;
import org.cmdbuild.task.wizardconnector.ConnectorTask;
import org.cmdbuild.task.wizardconnector.ConnectorTask.AttributeMapping;
import org.cmdbuild.task.wizardconnector.ConnectorTask.ClassMapping;
import org.cmdbuild.task.wizardconnector.ConnectorTask.SourceConfiguration;
import org.cmdbuild.task.wizardconnector.ConnectorTask.SqlSourceConfiguration;
import org.cmdbuild.task.reademail.mapper.KeyValueMapperEngine;
import org.cmdbuild.task.reademail.mapper.MapperEngine;
import org.cmdbuild.task.reademail.mapper.MapperEngineVisitor;
import org.cmdbuild.task.reademail.mapper.NullMapperEngine;
import org.cmdbuild.task.asyncevent.AsynchronousEventTask;
import org.cmdbuild.task.syncevent.SynchronousEventTaskImpl;
import org.cmdbuild.task.generictask.GenericTask;
import org.cmdbuild.task.startworkflow.StartWorkflowTask;
import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import java.lang.invoke.MethodHandles;
import static org.cmdbuild.task.dao.TaskData.builder;
import org.cmdbuild.logic.taskmanager.SynchronousEventTask;
import org.cmdbuild.logic.taskmanager.SynchronousEventTask.Phase;
import org.cmdbuild.logic.taskmanager.Task.TaskType;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogicAndStoreConverterImpl implements LogicAndStoreConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public TaskData taskToTaskData(Task source) {
        LOGGER.info("converting logic task '{}' to store task", source);
        TaskData.TaskDataBuilder builder = builder().withTaskType(source.getType().name());
        switch (source.getType()) {
            case ASYNC_EVENT:
                AsynchronousEventTask asynchronousEventTask = (AsynchronousEventTask) source;
                return builder.withId(asynchronousEventTask.getId()) //
                        .withDescription(asynchronousEventTask.getDescription()) //
                        .withRunningStatus(asynchronousEventTask.isActive()) //
                        .withCronExpression(asynchronousEventTask.getCronExpression()) //
                        .withLastExecution(asynchronousEventTask.getLastExecution()) //
                        .withParameter(AsynchronousEvent.FILTER_CLASSNAME, asynchronousEventTask.getTargetClassname()) //
                        .withParameter(AsynchronousEvent.FILTER_CARDS, asynchronousEventTask.getFilter()) //
                        .withParameter(AsynchronousEvent.EMAIL_ACTIVE, //
                                Boolean.toString(asynchronousEventTask.isNotificationActive())) //
                        .withParameter(AsynchronousEvent.EMAIL_ACCOUNT, asynchronousEventTask.getNotificationAccount()) //
                        .withParameter(AsynchronousEvent.EMAIL_TEMPLATE, asynchronousEventTask.getNotificationTemplate()) //
                        .withParameter(AsynchronousEvent.REPORT_ACTIVE, Boolean.toString(asynchronousEventTask.isReportActive())) //
                        .withParameter(AsynchronousEvent.REPORT_NAME, asynchronousEventTask.getReportName()) //
                        .withParameter(AsynchronousEvent.REPORT_EXTENSION, asynchronousEventTask.getReportExtension()) //
                        .withParameters(prependPrefixToParameterKeys(AsynchronousEvent.REPORT_PARAMETERS_PREFIX, asynchronousEventTask.getReportParameters())) //
                        .build();
            case CONNECTOR:
                ConnectorTask connectorTask = (ConnectorTask) source;
                SourceConfiguration sourceConfiguration = connectorTask.getSourceConfiguration();
                return builder.withId(connectorTask.getId()) //
                        .withDescription(connectorTask.getDescription()) //
                        .withRunningStatus(connectorTask.isActive()) //
                        .withCronExpression(connectorTask.getCronExpression()) //
                        .withLastExecution(connectorTask.getLastExecution()) //
                        .withParameter(Connector.NOTIFICATION_ACTIVE, //
                                Boolean.toString(connectorTask.isNotificationActive())) //
                        .withParameter(Connector.NOTIFICATION_ACCOUNT, connectorTask.getNotificationAccount()) //
                        .withParameter(Connector.NOTIFICATION_ERROR_TEMPLATE, connectorTask.getNotificationErrorTemplate()) //
                        .withParameters(parametersOf((SqlSourceConfiguration) sourceConfiguration)) //
                        .withParameter(Connector.MAPPING_TYPES,
                                Joiner.on(SPECIAL_SEPARATOR) //
                                        .join( //
                                                FluentIterable.from(connectorTask.getClassMappings()) //
                                                        .transform(CLASS_MAPPING_TO_STRING)) //
                        ) //
                        .withParameter(Connector.MAPPING_ATTRIBUTES,
                                Joiner.on(SPECIAL_SEPARATOR) //
                                        .join( //
                                                FluentIterable.from(connectorTask.getAttributeMappings()) //
                                                        .transform(ATTRIBUTE_MAPPING_TO_STRING)) //
                        ) //
                        .build();
            case GENERIC:
                GenericTask genericTask = (GenericTask) source;
                return builder.withId(genericTask.getId()) //
                        .withDescription(genericTask.getDescription()) //
                        .withRunningStatus(genericTask.isActive()) //
                        .withCronExpression(genericTask.getCronExpression()) //
                        .withLastExecution(genericTask.getLastExecution()) //
                        .withParameters(contextToParameters(genericTask)) //
                        .withParameter(Generic.EMAIL_ACTIVE, Boolean.toString(genericTask.isEmailActive())) //
                        .withParameter(Generic.EMAIL_TEMPLATE, genericTask.getEmailTemplate()) //
                        .withParameter(Generic.EMAIL_ACCOUNT, genericTask.getEmailAccount()) //
                        .withParameter(Generic.REPORT_ACTIVE, Boolean.toString(genericTask.isReportActive())) //
                        .withParameter(Generic.REPORT_NAME, genericTask.getReportName()) //
                        .withParameter(Generic.REPORT_EXTENSION, genericTask.getReportExtension()) //
                        .withParameters(prependPrefixToParameterKeys(Generic.REPORT_PARAMETERS_PREFIX, genericTask.getReportParameters())) //
                        .build();
            case START_WORKFLOW:
                StartWorkflowTask startWorkflowTask = (StartWorkflowTask) source;
                return builder.withId(startWorkflowTask.getId()) //
                        .withDescription(startWorkflowTask.getDescription()) //
                        .withRunningStatus(startWorkflowTask.isActive()) //
                        .withCronExpression(startWorkflowTask.getCronExpression()) //
                        .withLastExecution(startWorkflowTask.getLastExecution()) //
                        .withParameter(StartWorkflow.CLASSNAME, startWorkflowTask.getProcessClass()) //
                        .withParameter(StartWorkflow.ATTRIBUTES,
                                Joiner.on(SPECIAL_SEPARATOR) //
                                        .withKeyValueSeparator(KEY_VALUE_SEPARATOR) //
                                        .join(startWorkflowTask.getAttributes())) //
                        .build();
            case SYNC_EVENT:
                SynchronousEventTask synchronousEventTask = (SynchronousEventTask) source;
                return builder.withId(synchronousEventTask.getId()) //
                        .withDescription(synchronousEventTask.getDescription()) //
                        .withRunningStatus(synchronousEventTask.isActive()) //
                        .withParameter(SynchronousEvent.PHASE, phaseToStore(synchronousEventTask.getPhase())) //
                        .withParameter(SynchronousEvent.FILTER_GROUPS, Joiner.on(GROUPS_SEPARATOR).join(synchronousEventTask.getGroups())) //
                        .withParameter(SynchronousEvent.FILTER_CLASSNAME, synchronousEventTask.getTargetClassname()) //
                        .withParameter(SynchronousEvent.FILTER_CARDS, synchronousEventTask.getFilter()) //
                        .withParameter(SynchronousEvent.EMAIL_ACTIVE, Boolean.toString(synchronousEventTask.isEmailEnabled())) //
                        .withParameter(SynchronousEvent.EMAIL_ACCOUNT, synchronousEventTask.getEmailAccount()) //
                        .withParameter(SynchronousEvent.EMAIL_TEMPLATE, synchronousEventTask.getEmailTemplate()) //
                        .withParameter(SynchronousEvent.WORKFLOW_ACTIVE, Boolean.toString(synchronousEventTask.isWorkflowEnabled())) //
                        .withParameter(SynchronousEvent.WORKFLOW_CLASS_NAME, synchronousEventTask.getWorkflowClassName())
                        .withParameter(SynchronousEvent.WORKFLOW_ATTRIBUTES, Joiner.on(SPECIAL_SEPARATOR).withKeyValueSeparator(KEY_VALUE_SEPARATOR).join(synchronousEventTask.getWorkflowAttributes()))
                        .withParameter(SynchronousEvent.WORKFLOW_ADVANCE, Boolean.toString(synchronousEventTask.isWorkflowAdvanceable()))
                        .withParameter(SynchronousEvent.ACTION_SCRIPT_ACTIVE, Boolean.toString(synchronousEventTask.isScriptingEnabled())) //
                        .withParameter(SynchronousEvent.ACTION_SCRIPT_ENGINE, synchronousEventTask.getScriptingEngine()) //
                        .withParameter(SynchronousEvent.ACTION_SCRIPT_SCRIPT, synchronousEventTask.getScriptingScript()) //
                        .withParameter(SynchronousEvent.ACTION_SCRIPT_SAFE, Boolean.toString(synchronousEventTask.isScriptingSafe())) //
                        .build();
            default:
                throw new UnsupportedOperationException("unsupported task type = " + source.getType());
        }
    }

    private static Map<String, String> prependPrefixToParameterKeys(String prefix, Map<String, String> parameters) {
        Map<String, String> output = newHashMap();
        parameters.entrySet().stream().forEach(input -> output.put(prefix.concat(input.getKey()), input.getValue()));
        return output;
    }

    private String phaseToStore(Phase phase) {
        switch (phase) {
            case AFTER_CREATE:
                return SynchronousEvent.PHASE_AFTER_CREATE;
            case AFTER_UPDATE:
                return SynchronousEvent.PHASE_AFTER_UPDATE;
            case BEFORE_DELETE:
                return SynchronousEvent.PHASE_BEFORE_DELETE;
            case BEFORE_UPDATE:
                return SynchronousEvent.PHASE_BEFORE_UPDATE;
            default:
                throw new UnsupportedOperationException("unsupported phase " + phase);
        }
    }

    private Map<String, String> parametersOf(SqlSourceConfiguration sourceConfiguration) {
        Map<String, String> parameters = Maps.newHashMap();
        Map<String, String> map = Maps.newHashMap();
        map.put(Connector.SQL_TYPE, SqlSourceHandler.of(sourceConfiguration.getType()).store);
        map.put(Connector.SQL_HOSTNAME, sourceConfiguration.getHost());
        map.put(Connector.SQL_PORT, Integer.toString(sourceConfiguration.getPort()));
        map.put(Connector.SQL_DATABASE, sourceConfiguration.getDatabase());
        map.put(Connector.SQL_INSTANCE, sourceConfiguration.getInstance());
        map.put(Connector.SQL_USERNAME, sourceConfiguration.getUsername());
        map.put(Connector.SQL_PASSWORD, sourceConfiguration.getPassword());
        map.put(Connector.SQL_FILTER, sourceConfiguration.getFilter());
        parameters.put(Connector.DATA_SOURCE_TYPE, "sql");
        parameters.put(Connector.DATA_SOURCE_CONFIGURATION,
                Joiner.on(SPECIAL_SEPARATOR) //
                        .withKeyValueSeparator(KEY_VALUE_SEPARATOR) //
                        .useForNull(EMPTY) //
                        .join(map));
        return parameters;
    }

    private Map<String, ? extends String> contextToParameters(GenericTask task) {
        final Map<String, String> output = newHashMap();
        task.getContext().entrySet().stream() //
                .forEach(input -> input.getValue().entrySet().stream() //
                .forEach(_input -> output.put(Generic.context(input.getKey(), _input.getKey()),
                _input.getValue()))
                );
        return output;
    }

    private static final Iterable<ClassMapping> NO_CLASS_MAPPINGS = Collections.emptyList();
    private static final Iterable<AttributeMapping> NO_ATTRIBUTE_MAPPINGS = Collections.emptyList();

    private static final Iterable<String> EMPTY_GROUPS = Collections.emptyList();
    private static final Iterable<String> EMPTY_FILTERS = Collections.emptyList();
    private static final Map<String, String> EMPTY_PARAMETERS = Collections.emptyMap();

    private static Map<String, String> parameters(Map<String, String> parameters, String prefix) {
        Map<String, String> output = newHashMap();
        parameters.entrySet().stream().filter(input -> input.getKey().startsWith(prefix)).forEach(input -> output.put(input.getKey().substring(prefix.length()), input.getValue()));
        return output;
    }

    @Override
    public Task taskDataToTask(TaskData task) {
        switch (TaskType.valueOf(task.getTaskType())) {
            case ASYNC_EVENT:
                return AsynchronousEventTask.newInstance() //
                        .withId(task.getId()) //
                        .withDescription(task.getDescription()) //
                        .withActiveStatus(task.isRunning()) //
                        .withCronExpression(task.getCronExpression()) //
                        .withLastExecution(task.getLastExecution()) //
                        .withTargetClass(task.getParameter(AsynchronousEvent.FILTER_CLASSNAME)) //
                        .withFilter(task.getParameter(AsynchronousEvent.FILTER_CARDS)) //
                        .withNotificationStatus( //
                                Boolean.valueOf(task.getParameter(AsynchronousEvent.EMAIL_ACTIVE))) //
                        .withNotificationAccount(task.getParameter(AsynchronousEvent.EMAIL_ACCOUNT)) //
                        .withNotificationErrorTemplate(task.getParameter(AsynchronousEvent.EMAIL_TEMPLATE)) //
                        .withReportActive(Boolean.valueOf(task.getParameter(AsynchronousEvent.REPORT_ACTIVE))) //
                        .withReportName(task.getParameter(AsynchronousEvent.REPORT_NAME)) //
                        .withReportExtension(task.getParameter(AsynchronousEvent.REPORT_EXTENSION)) //
                        .withReportParameters(parameters(task.getParameters(), AsynchronousEvent.REPORT_PARAMETERS_PREFIX)) //
                        .build();
            case CONNECTOR:
                String dataSourceConfiguration = task.getParameter(Connector.DATA_SOURCE_CONFIGURATION);
                String typeMapping = task.getParameter(Connector.MAPPING_TYPES);
                String attributeMapping = task.getParameter(Connector.MAPPING_ATTRIBUTES);
                return ConnectorTask.newInstance() //
                        .withId(task.getId()) //
                        .withDescription(task.getDescription()) //
                        .withActiveStatus(task.isRunning()) //
                        .withCronExpression(task.getCronExpression()) //
                        .withLastExecution(task.getLastExecution()) //
                        .withNotificationStatus( //
                                Boolean.valueOf(task.getParameter(Connector.NOTIFICATION_ACTIVE))) //
                        .withNotificationAccount(task.getParameter(Connector.NOTIFICATION_ACCOUNT)) //
                        .withNotificationErrorTemplate(task.getParameter(Connector.NOTIFICATION_ERROR_TEMPLATE)) //
                        .withSourceConfiguration(sourceConfigurationOf(dataSourceConfiguration)) //
                        .withClassMappings( //
                                isEmpty(typeMapping) ? NO_CLASS_MAPPINGS
                                : FluentIterable
                                        .from( //
                                                Splitter.on(SPECIAL_SEPARATOR) //
                                                        .split(typeMapping)) //
                                        .transform(STRING_TO_CLASS_MAPPING)) //
                        .withAttributeMappings( //
                                isEmpty(attributeMapping) ? NO_ATTRIBUTE_MAPPINGS
                                : FluentIterable
                                        .from( //
                                                Splitter.on(SPECIAL_SEPARATOR) //
                                                        .split(attributeMapping)) //
                                        .transform(STRING_TO_ATTRIBUTE_MAPPING)) //
                        .build();
            case GENERIC:
                return GenericTask.newInstance() //
                        .withId(task.getId()) //
                        .withDescription(task.getDescription()) //
                        .withActiveStatus(task.isRunning()) //
                        .withCronExpression(task.getCronExpression()) //
                        .withLastExecution(task.getLastExecution()) //
                        .withContext(contextFromTaskData(task)) //
                        .withEmailActive(Boolean.valueOf(task.getParameter(Generic.EMAIL_ACTIVE))) //
                        .withEmailTemplate(task.getParameter(Generic.EMAIL_TEMPLATE)) //
                        .withEmailAccount(task.getParameter(Generic.EMAIL_ACCOUNT)) //
                        .withReportActive(Boolean.valueOf(task.getParameter(Generic.REPORT_ACTIVE))) //
                        .withReportName(task.getParameter(Generic.REPORT_NAME)) //
                        .withReportExtension(task.getParameter(Generic.REPORT_EXTENSION)) //
                        .withReportParameters(parameters(task.getParameters(), Generic.REPORT_PARAMETERS_PREFIX)) //
                        .build();
            case START_WORKFLOW:
                String wfAttributesAsString = defaultString(task.getParameter(StartWorkflow.ATTRIBUTES));
                return StartWorkflowTask.newInstance() //
                        .withId(task.getId()) //
                        .withDescription(task.getDescription()) //
                        .withActiveStatus(task.isRunning()) //
                        .withCronExpression(task.getCronExpression()) //
                        .withLastExecution(task.getLastExecution()) //
                        .withProcessClass(task.getParameter(StartWorkflow.CLASSNAME)) //
                        .withAttributes(isEmpty(wfAttributesAsString) ? EMPTY_PARAMETERS : splitProperties(wfAttributesAsString)) //
                        .build();
            case SYNC_EVENT:
                String groupsAsString = defaultString(task.getParameter(SynchronousEvent.FILTER_GROUPS));
                String sevAttributesAsString = defaultString(task.getParameter(SynchronousEvent.WORKFLOW_ATTRIBUTES));
                return SynchronousEventTaskImpl.newInstance() //
                        .withId(task.getId()) //
                        .withDescription(task.getDescription()) //
                        .withActiveStatus(task.isRunning()) //
                        .withPhase(phaseFromStore(task.getParameter(SynchronousEvent.PHASE)))
                        .withGroups(isEmpty(groupsAsString) ? EMPTY_GROUPS : Splitter.on(GROUPS_SEPARATOR).split(groupsAsString))
                        .withTargetClass(task.getParameter(SynchronousEvent.FILTER_CLASSNAME)) //
                        .withFilter(task.getParameter(SynchronousEvent.FILTER_CARDS)) //
                        .withEmailEnabled(Boolean.valueOf(task.getParameter(SynchronousEvent.EMAIL_ACTIVE))) //
                        .withEmailAccount(task.getParameter(SynchronousEvent.EMAIL_ACCOUNT)) //
                        .withEmailTemplate(task.getParameter(SynchronousEvent.EMAIL_TEMPLATE)) //
                        .withWorkflowEnabled(Boolean.valueOf(task.getParameter(SynchronousEvent.WORKFLOW_ACTIVE))) //
                        .withWorkflowClassName(task.getParameter(SynchronousEvent.WORKFLOW_CLASS_NAME)) //
                        .withWorkflowAttributes(isEmpty(sevAttributesAsString) ? EMPTY_PARAMETERS : splitProperties(sevAttributesAsString)) //
                        .withWorkflowAdvanceable(Boolean.valueOf(task.getParameter(SynchronousEvent.WORKFLOW_ADVANCE))) //
                        .withScriptingEnableStatus(Boolean.valueOf(task.getParameter(SynchronousEvent.ACTION_SCRIPT_ACTIVE))) //
                        .withScriptingEngine(task.getParameter(SynchronousEvent.ACTION_SCRIPT_ENGINE)) //
                        .withScript(task.getParameter(SynchronousEvent.ACTION_SCRIPT_SCRIPT)) //
                        .withScriptingSafeStatus(Boolean.valueOf(task.getParameter(SynchronousEvent.ACTION_SCRIPT_SAFE))) //
                        .build();
            default:
                throw new UnsupportedOperationException("unsupported task type = " + task.getTaskType());
        }
    }

    private Phase phaseFromStore(String phase) {
        switch (phase) {
            case SynchronousEvent.PHASE_AFTER_CREATE:
                return Phase.AFTER_CREATE;
            case SynchronousEvent.PHASE_AFTER_UPDATE:
                return Phase.AFTER_UPDATE;
            case SynchronousEvent.PHASE_BEFORE_DELETE:
                return Phase.BEFORE_DELETE;
            case SynchronousEvent.PHASE_BEFORE_UPDATE:
                return Phase.BEFORE_UPDATE;
            default:
                throw new IllegalArgumentException("unsupported phase " + phase);
        }
    }

    private SourceConfiguration sourceConfigurationOf(final String configuration) {
        final SourceConfiguration sourceConfiguration;
        if (isBlank(configuration)) {
            sourceConfiguration = NULL_SOURCE_CONFIGURATION;
        } else {
            final Map<String, String> map = Splitter.on(SPECIAL_SEPARATOR) //
                    .withKeyValueSeparator(KEY_VALUE_SEPARATOR) //
                    .split(defaultString(configuration));
            sourceConfiguration = SqlSourceConfiguration.newInstance() //
                    .withType(SqlSourceHandler.of(map.get(Connector.SQL_TYPE)).type) //
                    .withHost(map.get(Connector.SQL_HOSTNAME)) //
                    .withPort(createInteger(defaultString(map.get(Connector.SQL_PORT), null))) //
                    .withDatabase(map.get(Connector.SQL_DATABASE)) //
                    .withInstance(map.get(Connector.SQL_INSTANCE)) //
                    .withUsername(map.get(Connector.SQL_USERNAME)) //
                    .withPassword(map.get(Connector.SQL_PASSWORD)) //
                    .withFilter(map.get(Connector.SQL_FILTER)) //
                    .build();
        }
        return sourceConfiguration;
    }

    private Map<String, Map<String, String>> contextFromTaskData(TaskData task) {
        final Map<String, Map<String, String>> output = newHashMap();
        task.getParameters().entrySet().stream() //
                .filter(input -> input.getKey().startsWith(Generic.CONTEXT_PREFIX)) //
                .forEach(input -> {
                    final String contextAndKey = input.getKey().substring(Generic.CONTEXT_PREFIX.length());
                    final String[] elements = contextAndKey.split("\\.");
                    final String context = elements[0];
                    final String key = elements[1];
                    final Map<String, String> sub;
                    if (output.containsKey(context)) {
                        sub = output.get(context);
                    } else {
                        sub = newHashMap();
                        output.put(context, sub);
                    }
                    sub.put(key, input.getValue());
                });
        return output;
    }

    private MapperEngine mapperOf(final Map<String, String> parameters) {
        final String type = parameters.get(ReadEmail.MapperEngine.TYPE);
        return ParametersToMapperConverter.of(type).convert(parameters);
    }

    private Map<String, String> splitProperties(final String value) {
        final Iterable<String> lines = Splitter.on(SPECIAL_SEPARATOR) //
                .omitEmptyStrings() //
                .split(value);
        final Map<String, String> properties = newHashMap();
        for (final String line : lines) {
            final List<String> elements = Splitter.on(KEY_VALUE_SEPARATOR) //
                    .limit(2) //
                    .splitToList(line);
            properties.put(elements.get(0), elements.get(1));
        }
        return properties;
    }

    private static enum SqlSourceHandler {

        MYSQL("mysql", mysql()), //
        ORACLE("oracle", oracle()), //
        POSTGRES("postgresql", postgresql()), //
        SQLSERVER("sqlserver", sqlserver()), //
        UNKNOWN(null, null);

        ;

		public static SqlSourceHandler of(final String store) {
            for (final SqlSourceHandler value : values()) {
                if (ObjectUtils.equals(value.store, store)) {
                    return value;
                }
            }
            return UNKNOWN;
        }

        public static SqlSourceHandler of(final DataSourceType type) {
            for (final SqlSourceHandler value : values()) {
                if (ObjectUtils.equals(value.type, type)) {
                    return value;
                }
            }
            return UNKNOWN;
        }

        public final String store;
        public final DataSourceType type;

        private SqlSourceHandler(final String client, final DataSourceType server) {
            this.store = client;
            this.type = server;
        }

    }

    private static final Function<ClassMapping, String> CLASS_MAPPING_TO_STRING = new Function<ClassMapping, String>() {

        @Override
        public String apply(final ClassMapping input) {
            return Joiner.on(Connector.MAPPING_SEPARATOR) //
                    .join(asList( //
                            input.getSourceType(), //
                            input.getTargetType(), //
                            Boolean.toString(input.isCreate()), //
                            Boolean.toString(input.isUpdate()), //
                            Boolean.toString(input.isDelete()) //
                    ));
        }

    };

    private static final Function<AttributeMapping, String> ATTRIBUTE_MAPPING_TO_STRING = new Function<AttributeMapping, String>() {

        @Override
        public String apply(final AttributeMapping input) {
            return Joiner.on(Connector.MAPPING_SEPARATOR) //
                    .join(asList( //
                            input.getSourceType(), //
                            input.getSourceAttribute(), //
                            input.getTargetType(), //
                            input.getTargetAttribute(), //
                            Boolean.toString(input.isKey()) //
                    ));
        }

    };

    private static final Function<String, ClassMapping> STRING_TO_CLASS_MAPPING = new Function<String, ClassMapping>() {

        @Override
        public ClassMapping apply(final String input) {
            final List<String> elements = Splitter.on(Connector.MAPPING_SEPARATOR).splitToList(input);
            return ClassMapping.newInstance() //
                    .withSourceType(elements.get(0)) //
                    .withTargetType(elements.get(1)) //
                    .withCreateStatus(Boolean.parseBoolean(elements.get(2))) //
                    .withUpdateStatus(Boolean.parseBoolean(elements.get(3))) //
                    .withDeleteStatus(Boolean.parseBoolean(elements.get(4))) //
                    .build();
        }

    };

    private static final Function<String, AttributeMapping> STRING_TO_ATTRIBUTE_MAPPING = new Function<String, AttributeMapping>() {

        @Override
        public AttributeMapping apply(final String input) {
            final List<String> elements = Splitter.on(Connector.MAPPING_SEPARATOR).splitToList(input);
            return AttributeMapping.newInstance() //
                    .withSourceType(elements.get(0)) //
                    .withSourceAttribute(elements.get(1)) //
                    .withTargetType(elements.get(2)) //
                    .withTargetAttribute(elements.get(3)) //
                    .withKeyStatus(Boolean.parseBoolean(elements.get(4))) //
                    .build();
        }

    };

    public static final String KEY_VALUE_SEPARATOR = "=";
    public static final String GROUPS_SEPARATOR = ",";

    /**
     * Used for separate those elements that should be separated by a line-feed
     * but that cannot be used because:<br>
     * 1) it could be used inside values<br>
     * 2) someone could edit database manually from a Windows host<br>
     * It's the HTML entity for the '|' character.
     */
    public static final String SPECIAL_SEPARATOR = "&#124;";

    private static class MapperToParametersConverter implements MapperEngineVisitor {

        public static MapperToParametersConverter of(final MapperEngine mapper) {
            return new MapperToParametersConverter(mapper);
        }

        private final MapperEngine mapper;

        private MapperToParametersConverter(final MapperEngine mapper) {
            this.mapper = mapper;
        }

        private Map<String, String> parameters;

        public Map<String, String> convert() {
            parameters = Maps.newLinkedHashMap();
            mapper.accept(this);
            return parameters;
        }

        @Override
        public void visit(final KeyValueMapperEngine mapper) {
            parameters.put(ReadEmail.MapperEngine.TYPE, ReadEmail.KeyValueMapperEngine.TYPE_VALUE);
            parameters.put(ReadEmail.KeyValueMapperEngine.KEY_INIT, mapper.getKeyInit());
            parameters.put(ReadEmail.KeyValueMapperEngine.KEY_END, mapper.getKeyEnd());
            parameters.put(ReadEmail.KeyValueMapperEngine.VALUE_INIT, mapper.getValueInit());
            parameters.put(ReadEmail.KeyValueMapperEngine.VALUE_END, mapper.getValueEnd());
        }

        @Override
        public void visit(final NullMapperEngine mapper) {
            // nothing to do
        }

    }

    // TODO do in some way with visitor
    private static enum ParametersToMapperConverter {

        KEY_VALUE(ReadEmail.KeyValueMapperEngine.TYPE_VALUE) {

            @Override
            public MapperEngine convert(final Map<String, String> parameters) {
                return KeyValueMapperEngine.newInstance() //
                        .withKey( //
                                parameters.get(ReadEmail.KeyValueMapperEngine.KEY_INIT), //
                                parameters.get(ReadEmail.KeyValueMapperEngine.KEY_END) //
                        ) //
                        .withValue( //
                                parameters.get(ReadEmail.KeyValueMapperEngine.VALUE_INIT), //
                                parameters.get(ReadEmail.KeyValueMapperEngine.VALUE_END) //
                        ) //
                        .build();
            }

        }, //
        UNDEFINED(EMPTY) {

            @Override
            public MapperEngine convert(final Map<String, String> parameters) {
                return NullMapperEngine.getInstance();
            }

        }, //
        ;

        public static ParametersToMapperConverter of(final String type) {
            for (final ParametersToMapperConverter element : values()) {
                if (element.type.equals(type)) {
                    return element;
                }
            }
            return UNDEFINED;
        }

        private final String type;

        private ParametersToMapperConverter(final String type) {
            this.type = type;
        }

        public abstract MapperEngine convert(Map<String, String> parameters);

    }
}
