package org.cmdbuild.task.dao;

import org.cmdbuild.data.store.Store;
import org.cmdbuild.data.store.dao.DataViewStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_TWO;
import org.cmdbuild.dao.view.DataView;

@Configuration
public class TaskStoreConfig {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier(SYSTEM_LEVEL_TWO)
	private DataView systemDataView;
	@Autowired
	private TaskDefinitionConverter taskDefinitionConverter;
	@Autowired
	private TaskParameterConverter taskParameterConverter;
	@Autowired
	private TaskRuntimeConverter taskRuntimeConverter;

	@Bean
	protected Store<TaskDefinition> taskDefinitionStore() {
		return DataViewStore.<TaskDefinition>builder().withDataView(systemDataView).withStorableConverter(taskDefinitionConverter).build();
	}

	@Bean
	protected Store<TaskParameter> taskParameterStore() {
		return DataViewStore.<TaskParameter>builder().withDataView(systemDataView).withStorableConverter(taskParameterConverter).build();
	}

	@Bean
	protected Store<TaskRuntime> taskRuntimeStore() {
		return DataViewStore.<TaskRuntime>builder() //
				.withDataView(systemDataView) //
				.withStorableConverter(taskRuntimeConverter) //
				.build();
	}

}
