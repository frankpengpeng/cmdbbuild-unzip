/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.repository;

import org.cmdbuild.dao.entrytype.AttributeGroupImpl;
import org.cmdbuild.dao.driver.repository.AttributeGroupRepository;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.eventbus.Subscribe;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.event.AttributeGroupModifiedEvent;
import org.cmdbuild.dao.event.DaoEventServiceImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.dao.driver.repository.ClassStructureChangedEvent;
import org.cmdbuild.dao.entrytype.AttributeGroupData;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.EntryType.EntryTypeType;
import org.cmdbuild.dao.event.AttributeStructureChangedEvent;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.sqlTableToEntryTypeName;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.springframework.context.annotation.Primary;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;

@Component
@Primary
public class AttributeGroupRepositoryImpl implements AttributeGroupRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DaoEventServiceImpl eventService;
    private final Holder<List<AttributeGroupData>> allAttributeGroups;
    private final CmCache<Optional<AttributeGroupData>> attributeGroupsByKey;
    private final CmCache<List<AttributeGroupData>> attributeGroupsByOwner;

    public AttributeGroupRepositoryImpl(JdbcTemplate jdbcTemplate, CacheService cacheService, DaoEventServiceImpl eventService) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        this.eventService = checkNotNull(eventService);
        allAttributeGroups = cacheService.newHolder("all_attribute_groups", CacheConfig.SYSTEM_OBJECTS);
        attributeGroupsByKey = cacheService.newCache("attribute_groups_by_key", CacheConfig.SYSTEM_OBJECTS);
        attributeGroupsByOwner = cacheService.newCache("attribute_groups_by_owner", CacheConfig.SYSTEM_OBJECTS);
        eventService.getEventBus().register(new Object() {

            @Subscribe
            public void handleClassCreatedEvent(ClassStructureChangedEvent event) {//TODO improve this, intercept only class create ev.
                invalidateCache();
            }

            @Subscribe
            public void handleClassCreatedEvent(AttributeStructureChangedEvent event) {//TODO improve this 
                invalidateCache();
            }
        });
    }

    @Override
    public List<AttributeGroupData> getAll() {
        return allAttributeGroups.get(this::doGetAll);
    }

    @Override
    @Nullable
    public AttributeGroupData getOrNull(String ownerName, EntryType.EntryTypeType ownerType, String groupId) {
        return attributeGroupsByKey.get(key(ownerName, ownerType, groupId), () -> {
            return getAll().stream().filter((a) -> equal(a.getOwnerName(), ownerName) && equal(a.getOwnerType(), ownerType) && equal(a.getName(), groupId)).collect(toOptional());
        }).orElse(null);
    }

    @Override
    public AttributeGroupData create(AttributeGroupData group) {
        jdbcTemplate.update("INSERT INTO \"_AttributeGroup\" (\"Code\",\"Description\",\"Owner\",\"Index\") VALUES (?,?,_cm3_utils_name_to_regclass(?),?)",
                group.getName(), group.getDescription(), entryTypeToSqlExpr(group.getOwnerName(), group.getOwnerType()), group.getIndex());
        invalidateCache();
        eventService.post(AttributeGroupModifiedEvent.INSTANCE);
        return get(group.getOwnerName(), group.getOwnerType(), group.getName());
    }

    @Override
    public AttributeGroupData update(AttributeGroupData group) {
        jdbcTemplate.update("UPDATE \"_AttributeGroup\" SET \"Description\" = ?, \"Index\" = ? WHERE \"Code\" = ? AND \"Owner\" = _cm3_utils_name_to_regclass(?) AND \"Status\" = 'A'",
                group.getDescription(), group.getIndex(), group.getName(), entryTypeToSqlExpr(group.getOwnerName(), group.getOwnerType()));
        invalidateCache();
        eventService.post(AttributeGroupModifiedEvent.INSTANCE);
        return get(group.getOwnerName(), group.getOwnerType(), group.getName());
    }

    @Override
    public List<AttributeGroupData> getAttributeGroupsForEntryType(String ownerName, EntryType.EntryTypeType ownerType) {
        return attributeGroupsByOwner.get(key(ownerName, ownerType), () -> getAll().stream().filter((a) -> equal(a.getOwnerName(), ownerName) && equal(a.getOwnerType(), ownerType)).collect(toImmutableList()));
    }

    @Override
    public void delete(AttributeGroupData group) {
        jdbcTemplate.update("UPDATE \"_AttributeGroup\" SET \"Status\" = 'N' WHERE \"Code\" = ? AND \"Owner\" = _cm3_utils_name_to_regclass(?) AND \"Status\" = 'A'",
                group.getName(), entryTypeToSqlExpr(group.getOwnerName(), group.getOwnerType()));
        invalidateCache();
    }

    private void invalidateCache() {
        allAttributeGroups.invalidate();
        attributeGroupsByKey.invalidateAll();
        attributeGroupsByOwner.invalidateAll();
    }

    private List<AttributeGroupData> doGetAll() {
        return jdbcTemplate.query("WITH q AS (SELECT \"Code\",\"Description\",_cm3_utils_regclass_to_name(\"Owner\") _owner_name, _cm3_class_type_get(\"Owner\") _owner_type, \"Index\" FROM \"_AttributeGroup\" WHERE \"Status\" = 'A') SELECT * FROM q ORDER BY _owner_name, _owner_type, \"Index\"", (ResultSet rs, int rowNum) -> {
            EntryTypeType ownerType = parseEnum(rs.getString("_owner_type"), EntryTypeType.class);
            return AttributeGroupImpl.builder()
                    .withName(rs.getString("Code"))
                    .withDescription(rs.getString("Description"))
                    .withIndex(rs.getInt("Index"))
                    .withOwnerType(ownerType)
                    .withOwnerName(sqlTableToEntryTypeName(rs.getString("_owner_name"), ownerType))
                    .build();
        });
    }

}
