package org.cmdbuild.task.wizardconnector;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Sets.newHashSet;
import static org.cmdbuild.common.utils.guava.Functions.build;

import java.util.Collection;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;
import org.cmdbuild.common.java.sql.DataSourceHelper;
import org.cmdbuild.common.java.sql.DataSourceTypes.DataSourceType;
import org.cmdbuild.common.java.sql.DataSourceTypes.DataSourceTypeVisitor;
import org.cmdbuild.common.java.sql.DataSourceTypes.MySql;
import org.cmdbuild.common.java.sql.DataSourceTypes.Oracle;
import org.cmdbuild.common.java.sql.DataSourceTypes.PostgreSql;
import org.cmdbuild.common.java.sql.DataSourceTypes.SqlServer;
import org.cmdbuild.task.wizardconnector.ConnectorTask.SourceConfiguration;
import org.cmdbuild.task.wizardconnector.ConnectorTask.SourceConfigurationVisitor;
import org.cmdbuild.task.wizardconnector.ConnectorTask.SqlSourceConfiguration;
//import org.cmdbuild.scheduler.command.Command;
import org.cmdbuild.legacy.etl.ClassType;
import org.cmdbuild.legacy.etl.SimpleAttribute;
import org.cmdbuild.legacy.etl.Store;
import org.cmdbuild.legacy.etl.StoreSynchronizer;
import org.cmdbuild.legacy.etl.AttributeValueAdapter;
import org.cmdbuild.legacy.etl.BuildableCatalog;
import org.cmdbuild.legacy.etl.Catalog;
import org.cmdbuild.legacy.etl.InternalStore;
import org.cmdbuild.legacy.etl.sql.BuildableAttributeMapping;
import org.cmdbuild.legacy.etl.sql.BuildableTableOrViewMapping;
import org.cmdbuild.legacy.etl.sql.BuildableTypeMapper;
import org.cmdbuild.legacy.etl.sql.SqlStore;
import org.cmdbuild.legacy.etl.sql.SqlType;
import org.cmdbuild.legacy.etl.sql.TableOrViewMapping;
import org.cmdbuild.legacy.etl.sql.TypeMapping;

import com.google.common.base.Function;
import org.cmdbuild.dao.view.DataView;
import static org.cmdbuild.legacy.etl.LoggingStore.logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//class ConnectorTaskCommandWrapper implements Command {
class ConnectorTaskCommandWrapper   {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final Function<Builder<? extends ClassType>, ClassType> BUILD_CLASS_TYPE = build();
	private static final Function<Builder<? extends TypeMapping>, TypeMapping> BUILD_TYPE_MAPPING = build();

	private final DataView dataView;
	private final DataSourceHelper dataSourceHelper;
	private final AttributeValueAdapter attributeValueAdapter;
	private final ConnectorTask task;

	public ConnectorTaskCommandWrapper(DataView dataView, DataSourceHelper dataSourceHelper, AttributeValueAdapter attributeValueAdapter, ConnectorTask task) {
		this.dataView = dataView;
		this.dataSourceHelper = dataSourceHelper;
		this.attributeValueAdapter = attributeValueAdapter;
		this.task = task;
	}

//	@Override
//	public void execute() {
//		logger.info("creating catalog");
//		final Catalog catalog = catalog();
//		logger.info("creating left store");
//		final Store left = left(catalog);
//		logger.info("creating right/target store");
//		final Store rightAndTarget = logging(InternalStore.newInstance() //
//				.withDataView(dataView) //
//				.withCatalog(catalog) //
//				.withAttributeValueAdapter(attributeValueAdapter) //
//				.build());
//		logger.info("synchronizing");
//		StoreSynchronizer.newInstance() //
//				.withLeft(left) //
//				.withRight(rightAndTarget) //
//				.withTarget(wrap(rightAndTarget)) //
//				.build() //
//				.sync();
//	}

	private Catalog catalog() {
		final Map<String, ClassType.Builder> typeBuildersByName = newHashMap();
		for (final ConnectorTask.AttributeMapping attributeMapping : task.getAttributeMappings()) {
			logger.debug("handling attribute mapping '{}'", attributeMapping);
			final String typeName = attributeMapping.getTargetType();
			logger.debug("getting type '{}'", typeName);
			ClassType.Builder typeBuilder = typeBuildersByName.get(typeName);
			if (typeBuilder == null) {
				logger.debug("type '{}' not found, creating new one", typeName);
				typeBuilder = ClassType.newInstance().withName(typeName);
				typeBuildersByName.put(typeName, typeBuilder);
			}
			final SimpleAttribute attribute = SimpleAttribute.newInstance() //
					.withName(attributeMapping.getTargetAttribute()) //
					.withKeyStatus(attributeMapping.isKey()) //
					.build();
			logger.debug("creating new attribute '{}'", attribute);
			typeBuilder.withAttribute(attribute);
		}
		final Iterable<ClassType> types = transformValues(typeBuildersByName, BUILD_CLASS_TYPE).values();
		final Catalog catalog = BuildableCatalog.newInstance() //
				.withTypes(types) //
				.build();
		logger.debug("catalog successfully created '{}'", catalog);
		return catalog;
	}

	private Store left(final Catalog catalog) {
		return new SourceConfigurationVisitor() {

			private Store store;

			public Store store() {
				final SourceConfiguration sourceConfiguration = task.getSourceConfiguration();
				logger.debug("handling configuration '{}'", sourceConfiguration);
				sourceConfiguration.accept(this);
				Validate.notNull(store, "conversion error");
				return store;
			}

			@Override
			public void visit(final SqlSourceConfiguration sourceConfiguration) {
				logger.debug("creating data source from configuration", sourceConfiguration);
				final DataSource dataSource = dataSourceHelper.create(sourceConfiguration);
				final Map<String, Map<String, BuildableTypeMapper.Builder>> allTypeMapperBuildersByTableOrViewName = newHashMap();
				for (final ConnectorTask.AttributeMapping attributeMapping : task.getAttributeMappings()) {
					final String tableOrViewName = attributeMapping.getSourceType();
					final String typeName = attributeMapping.getTargetType();
					Map<String, BuildableTypeMapper.Builder> typeMapperBuildersByTypeName = allTypeMapperBuildersByTableOrViewName
							.get(tableOrViewName);
					if (typeMapperBuildersByTypeName == null) {
						typeMapperBuildersByTypeName = newHashMap();
						allTypeMapperBuildersByTableOrViewName.put(tableOrViewName, typeMapperBuildersByTypeName);
					}
					BuildableTypeMapper.Builder typeMapperBuilder = typeMapperBuildersByTypeName.get(typeName);
					if (typeMapperBuilder == null) {
						final ClassType type = catalog.getType(typeName, ClassType.class);
						typeMapperBuilder = BuildableTypeMapper.newInstance().withType(type);
						typeMapperBuildersByTypeName.put(typeName, typeMapperBuilder);
					}
					typeMapperBuilder.withAttributeMapper(BuildableAttributeMapping.newInstance() //
							.withFrom(attributeMapping.getSourceAttribute()) //
							.withTo(attributeMapping.getTargetAttribute()) //
							.build());
				}
				logger.debug("creating table/view mappings");
				final Collection<TableOrViewMapping> tableOrViewMappings = newHashSet();
				for (final Map.Entry<String, Map<String, BuildableTypeMapper.Builder>> entry : allTypeMapperBuildersByTableOrViewName
						.entrySet()) {
					final String tableOrViewName = entry.getKey();
					final Map<String, TypeMapping> typeMappingBuildersByTypeName = transformValues(entry.getValue(),
							BUILD_TYPE_MAPPING);
					final TableOrViewMapping tableOrViewMapping = BuildableTableOrViewMapping.newInstance() //
							.withName(tableOrViewName) //
							.withTypeMappings(typeMappingBuildersByTypeName.values()) //
							.build();
					logger.trace("table/view mapping created '{}'", tableOrViewMapping);
					tableOrViewMappings.add(tableOrViewMapping);
				}

				logger.debug("creating store for\n\t- data source '{}'\n\t- mappings '{}'", dataSource,
						tableOrViewMappings);
				store = logging(SqlStore.newInstance() //
						.withDataSource(dataSource) //
						.withTableOrViewMappings(tableOrViewMappings) //
						.withType(typeOf(sourceConfiguration.getType())) //
						.build());
			}

			private SqlType typeOf(final DataSourceType dataSourceType) {
				return new DataSourceTypeVisitor() {

					private SqlType _type;

					public SqlType type() {
						dataSourceType.accept(this);
						return _type;
					}

					@Override
					public void visit(final MySql type) {
						_type = SqlType.MYSQL;
					}

					@Override
					public void visit(final Oracle type) {
						_type = SqlType.ORACLE;
					}

					@Override
					public void visit(final PostgreSql type) {
						_type = SqlType.POSTGRESQL;
					}

					@Override
					public void visit(final SqlServer type) {
						_type = SqlType.SQLSERVER;
					}

				}.type();
			}

		}.store();
	}

	private Store wrap(final Store store) {
		return new PermissionBasedStore(store, new ConnectorTaskPermission(task));
	}

}
