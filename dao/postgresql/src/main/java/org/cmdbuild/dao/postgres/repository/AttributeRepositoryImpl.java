/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.repository;

import org.cmdbuild.dao.driver.repository.AttributeGroupRepository;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Maps.transformValues;
import com.google.common.eventbus.Subscribe;
import java.sql.ResultSet;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.createAttributeType;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.getSqlTypeString;
import org.cmdbuild.dao.driver.repository.ClassStructureChangedEvent;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwnerImpl;
import org.cmdbuild.dao.event.AttributeModifiedEventImpl;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMultimap;
import org.cmdbuild.dao.driver.repository.InnerAttributeRepository;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.dao.driver.repository.DaoEventService;
import org.cmdbuild.dao.driver.repository.DomainStructureChangedEvent;
import org.cmdbuild.dao.event.AttributesCreatedEventImpl;
import static org.cmdbuild.dao.postgres.utils.CommentUtils.ATTRIBUTE_COMMENT_TO_METADATA_MAPPING;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.transformKeys;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import org.cmdbuild.dao.entrytype.AttributeGroupInfo;
import org.cmdbuild.dao.entrytype.EntryType.EntryTypeType;
import org.cmdbuild.dao.event.AttributeStructureChangedEvent;
import static org.cmdbuild.dao.postgres.utils.CommentUtils.parseCommentFromFeatures;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.sqlTableToEntryTypeName;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;

@Component
@Primary
public class AttributeRepositoryImpl implements InnerAttributeRepository {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoEventService daoEventService;
    private final JdbcTemplate jdbcTemplate;
    private final AttributeGroupRepository groupRepository;
    private final CmCache<List<AttributeWithoutOwner>> attributesByOwner;

    public AttributeRepositoryImpl(CacheService cacheService, AttributeGroupRepository attributeGroupRepository, DaoEventService daoEventService, @Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate) {
        this.daoEventService = checkNotNull(daoEventService);
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        this.groupRepository = checkNotNull(attributeGroupRepository);
        attributesByOwner = cacheService.newCache("entry_type_attributes_by_owner", CacheConfig.SYSTEM_OBJECTS);
        daoEventService.getEventBus().register(new Object() {
            @Subscribe
            public void handleClassStructureChangedEvent(ClassStructureChangedEvent event) {
                if (event.impactAllClasses()) {
                    invalidateWholeCache();
                } else {
                    invalidateCache(event.getAffectedClassOids());
                }
            }

            @Subscribe
            public void handleClassStructureChangedEvent(DomainStructureChangedEvent event) {
                if (event.impactAllDomains()) {
                    invalidateWholeCache();
                } else {
                    invalidateCache(event.getAffectedDomainOids());
                }
            }
        });
    }

    private void invalidateWholeCache() {
        attributesByOwner.invalidateAll();
    }

    private void invalidateCache(Iterable<Long> affectedClassOids) {
        affectedClassOids.forEach(attributesByOwner::invalidate);
    }

    @Override
    public Attribute createAttribute(Attribute definition) {
        logger.info("creating attribute '{}'", definition.getName());
        AttributeWithoutOwner attribute = doCreateAttribute(definition);
        daoEventService.post(new AttributesCreatedEventImpl(attribute, definition.getOwner()));//TODO move this in repo service 
        return AttributeImpl.copyOf(attribute).withOwner(definition.getOwner()).build();//TODO refresh owner after attr update and cache cleanup
    }

    @Override
    public List<Attribute> updateAttributes(List<Attribute> definitions) {
        if (isNullOrEmpty(definitions)) {
            return emptyList();
        } else {
            EntryType owner = definitions.stream().map(Attribute::getOwner).distinct().collect(onlyElement());
            List<AttributeWithoutOwner> attributes = doUpdateAttributes(owner, definitions);
            daoEventService.post(new AttributeModifiedEventImpl(attributes, owner));//TODO move this in repo service 
            return attributes.stream().map((a) -> AttributeImpl.copyOf(a).withOwner(owner).build()).collect(toList());//TODO refresh owner after attr update and cache cleanup
        }
    }

    @Override
    public void deleteAttribute(Attribute attribute) {
        logger.info("deleting attribute = {}", attribute.getName());
        doDeleteAttribute(attribute);
        daoEventService.post(new AttributeModifiedEventImpl(attribute, attribute.getOwner()));//TODO move this in repo service
    }

    @Override
    public List<AttributeWithoutOwner> getNonReservedEntryTypeAttributesForType(long entryTypeId) {
        return firstNonNull(attributesByOwner.get(String.valueOf(entryTypeId), () -> {
            synchronized (AttributeRepositoryImpl.this) {
                if (attributesByOwner.asMap().isEmpty()) {
                    Map<Long, List<AttributeWithoutOwner>> map = doGetNonReservedEntryTypeAttributesByOwner();
                    filterKeys(map, not(equalTo(entryTypeId))).forEach((k, v) -> attributesByOwner.put(String.valueOf(k), v));
                    return map.getOrDefault(entryTypeId, emptyList());
                } else {
                    return doGetNonReservedEntryTypeAttributesByOwner(entryTypeId);
                }
            }
        }), emptyList());
    }

    private List<AttributeWithoutOwner> doGetNonReservedEntryTypeAttributesByOwner(long ownerOid) {
        return jdbcTemplate.query("select _cm3_utils_regclass_to_name(owner::regclass) _classname, _cm3_class_type_get(owner::regclass) _owner_type, owner::oid::int _classid, * FROM _cm3_attribute_list_detailed(?::oid::regclass)", (ResultSet rs, int rowNum) -> parseAttributesRecord(rs), ownerOid)
                .stream().map(Pair::getValue).collect(toImmutableList());
    }

    private Map<Long, List<AttributeWithoutOwner>> doGetNonReservedEntryTypeAttributesByOwner() {
        return map(transformValues(jdbcTemplate.query("select _cm3_utils_regclass_to_name(owner::regclass) _classname, _cm3_class_type_get(owner::regclass) _owner_type, owner::oid::int _classid, * FROM _cm3_attribute_list_detailed()", (ResultSet rs, int rowNum) -> parseAttributesRecord(rs))
                .stream().collect(toMultimap(Pair::getKey, Pair::getValue)).asMap(), ImmutableList::copyOf));
    }

    private AttributeWithoutOwner doGetNonReservedEntryTypeAttribute(long ownerOid, String attrName) {
        return jdbcTemplate.query("select _cm3_utils_regclass_to_name(owner::regclass) _classname, _cm3_class_type_get(owner::regclass) _owner_type, owner::oid::int _classid, * FROM _cm3_attribute_list_detailed(?::oid::regclass) WHERE name = ?", (ResultSet rs, int rowNum) -> parseAttributesRecord(rs), ownerOid, attrName)
                .stream().map(Pair::getValue).collect(onlyElement("attribute not found for owner oid = %s name = %s", ownerOid, attrName));
    }

    private Pair<Long, AttributeWithoutOwner> parseAttributesRecord(ResultSet rs) {
        String name = "<unknown>", ownerTableName = "<unknown>";
        Long classId = null;
        try {
            ownerTableName = rs.getString("_classname");
            classId = checkNotNullAndGtZero(rs.getLong("_classid"));
            name = rs.getString("name");
            Map<String, String> metadata = fromJson(rs.getString("metadata"), MAP_OF_STRINGS),
                    comment = fromJson(rs.getString("comment"), MAP_OF_STRINGS),
                    features = map(metadata).with(comment);
            AttributeMetadata meta = new AttributeMetadataImpl(map(features)
                    .withoutKeys(ATTRIBUTE_COMMENT_TO_METADATA_MAPPING.keySet())
                    .withoutKeys(ATTRIBUTE_COMMENT_TO_METADATA_MAPPING.values())
                    .with(parseCommentFromFeatures(features, ATTRIBUTE_COMMENT_TO_METADATA_MAPPING))
                    .skipNullValues().with(
                            AttributeMetadata.INHERITED, Boolean.toString(rs.getBoolean("inherited")),
                            AttributeMetadata.DEFAULT, rs.getString("default_value"),
                            AttributeMetadata.MANDATORY, Boolean.toString(rs.getBoolean("not_null_constraint")),
                            AttributeMetadata.UNIQUE, Boolean.toString(rs.getBoolean("unique_constraint"))
                    ));
            EntryTypeType ownerType = parseEnum(rs.getString("_owner_type"), EntryTypeType.class);
            String ownerName = sqlTableToEntryTypeName(ownerTableName, ownerType);
            AttributeGroupInfo group = isBlank(meta.getGroup()) ? null : groupRepository.get(ownerName, ownerType, meta.getGroup());
            CardAttributeType<?> type = createAttributeType(rs.getString("sql_type"), meta);
            return Pair.of(classId, (AttributeWithoutOwner) AttributeWithoutOwnerImpl.builder()
                    .withName(name)
                    .withType(type)
                    .withMeta(meta)
                    .withGroup(group)
                    .build());
        } catch (Exception ex) {
            throw new DaoException(ex, "error processing attribute = \"%s\" with owner_oid = %s owher_table = %s", name, classId, ownerTableName);
        }
    }

    private AttributeWithoutOwner doCreateAttribute(Attribute attribute) {
        EntryType owner = attribute.getOwner();
        if (attribute.getIndex() < 0) {
            attribute = AttributeImpl.copyOf(attribute).withIndex(owner.getAllAttributes().stream().map(Attribute::getIndex).reduce(Integer::max).orElse(-1) + 1).build();
        }
        jdbcTemplate.queryForObject("SELECT _cm3_attribute_create(?::regclass,?,?,?::jsonb)", Object.class, entryTypeToSqlExpr(owner), attribute.getName(), getSqlTypeString(attribute.getType()), toJson(buildFeaturesForAttribute(attribute)));
        daoEventService.post(AttributeStructureChangedEventImpl.INSTANCE);
        return doGetNonReservedEntryTypeAttribute(owner.getId(), attribute.getName());
    }

    private List<AttributeWithoutOwner> doUpdateAttributes(EntryType owner, List<Attribute> definitions) {
        return definitions.stream().map((definition) -> {
            checkArgument(equal(definition.getOwner(), owner));
            return doUpdateAttribute(definition);
        }).collect(toList());
    }

    private AttributeWithoutOwner doUpdateAttribute(Attribute attribute) {
        EntryType owner = attribute.getOwner();
        jdbcTemplate.queryForObject("SELECT _cm3_attribute_modify(?::regclass,?,?,?::jsonb)", Object.class, entryTypeToSqlExpr(owner), attribute.getName(), getSqlTypeString(attribute.getType()), toJson(buildFeaturesForAttribute(attribute)));
        daoEventService.post(AttributeStructureChangedEventImpl.INSTANCE);
        return doGetNonReservedEntryTypeAttribute(owner.getId(), attribute.getName());
    }

    private void doDeleteAttribute(Attribute attribute) {
        jdbcTemplate.queryForObject("SELECT _cm3_attribute_delete(?::regclass,?)", Object.class, entryTypeToSqlExpr(attribute.getOwner()), attribute.getName());
    }

    private Map<String, String> buildFeaturesForAttribute(Attribute attribute) {
        return transformKeys(attribute.getMetadata().getAll(), k -> firstNotNull(ATTRIBUTE_COMMENT_TO_METADATA_MAPPING.inverse().get(k), k));
    }

    private enum AttributeStructureChangedEventImpl implements AttributeStructureChangedEvent {
        INSTANCE;
    }
}
