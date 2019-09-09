/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.annotation.Nullable;
import org.cmdbuild.dao.driver.repository.AttributeGroupRepository;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.springframework.stereotype.Component;

@Component
public class AttributeGroupServiceImpl implements AttributeGroupService {

    private final AttributeGroupRepository attributeGroupRepository;

    public AttributeGroupServiceImpl(AttributeGroupRepository attributeGroupRepository) {
        this.attributeGroupRepository = checkNotNull(attributeGroupRepository);
    }

    @Override
    public List<AttributeGroupData> getAttributeGroupsForEntryType(String ownerName, EntryType.EntryTypeType ownerType) {
        return attributeGroupRepository.getAttributeGroupsForEntryType(ownerName, ownerType);
    }

    @Override
    public List<AttributeGroupInfo> updateAttributeGroupsForEntryType(EntryType entryType, List<AttributeGroupInfo> attributeGroups) {
        Set<String> curGroups = attributeGroupRepository.getAttributeGroupsForEntryType(entryType).stream().map(AttributeGroupInfo::getName).collect(toSet()),
                newGroups = attributeGroups.stream().map(AttributeGroupInfo::getName).collect(toSet()),
                toDelete = set(curGroups).without(newGroups);
        entryType.getAllAttributes().stream().filter(Attribute::hasGroup).forEach(a -> checkArgument(newGroups.contains(a.getGroupName()), "cannot delete group = %s : it is used by attribute = %s", a.getGroupName(), a));
        AtomicInteger index = new AtomicInteger(0);
        attributeGroups = attributeGroups.stream().map(a -> AttributeGroupImpl.copyOf(a).withOwner(entryType).withIndex(index.incrementAndGet()).build()).map(attributeGroupRepository::createOrUpdate).collect(toList());
        toDelete.stream().map(g -> attributeGroupRepository.get(entryType, g)).forEach(attributeGroupRepository::delete);
        return attributeGroups;
    }

    @Override
    public List<AttributeGroupData> getAll() {
        return attributeGroupRepository.getAll();
    }

    @Override
    @Nullable
    public AttributeGroupData getOrNull(String ownerName, EntryType.EntryTypeType ownerType, String groupId) {
        return attributeGroupRepository.getOrNull(ownerName, ownerType, groupId);
    }

    @Override
    public AttributeGroupData create(AttributeGroupData group) {
        return attributeGroupRepository.create(group);
    }

    @Override
    public AttributeGroupData update(AttributeGroupData group) {
        return attributeGroupRepository.update(group);
    }

    @Override
    public void delete(AttributeGroupData group) {
        attributeGroupRepository.delete(group);
    }

}
