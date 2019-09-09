package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import static org.cmdbuild.dao.entrytype.ClassMultitenantMode.CMM_NEVER;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.dao.entrytype.EntryType.EntryTypeType.ET_CLASS;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public interface Classe extends EntryType, PrivilegeSubjectWithInfo, ClassPermissions, ClasseInfo {

    @Override
    ClassMetadata getMetadata();

    @Override
    String getName();

    List<String> getAncestors();

    default List<String> getAncestorsAndSelf() {
        return list(getAncestors()).with(getName());
    }

    default boolean isProcess() {
        return getMetadata().isProcess();
    }

    @Override
    default EntryTypeType getEtType() {
        return ET_CLASS;
    }

    default boolean hasParent() {
        return getParentOrNull() != null;
    }

    @Nullable
    default String getParentOrNull() {
        if (getAncestors().isEmpty()) {
            return null;
        } else {
            return Iterables.getLast(getAncestors());
        }
    }

    default String getParent() {
        return checkNotBlank(getParentOrNull(), "this class does not have a parent");
    }

    @Override
    default String getDescription() {
        return firstNotBlank(getMetadata().getDescription(), getName());
    }

    @Override
    default long getOid() {
        return getId();
    }

    default boolean isSuperclass() {
        return getMetadata().isSuperclass();
    }

    @Override
    default boolean hasHistory() {
        return getMetadata().holdsHistory();
    }

    default boolean isSimpleClass() {
        return !hasHistory();
    }

    default boolean isStandardClass() {
        return !isSimpleClass();
    }

    default ClassType getClassType() {
        return isSimpleClass() ? ClassType.SIMPLE : ClassType.STANDARD;
    }

    @Deprecated//workflow specific, remove from class interface
    default boolean isUserStoppable() {
        return getMetadata().isUserStoppable();
    }

    default ClassMultitenantMode getMultitenantMode() {
        return getMetadata().getMultitenantMode();
    }

    default boolean hasMultitenantEnabled() {
        return !equal(getMultitenantMode(), CMM_NEVER);
    }

    @Override
    default String getPrivilegeId() {
        return String.format("Class:%s", getName());
    }

    default boolean hasAttachmentTypeLookupType() {
        return !isBlank(getAttachmentTypeLookupTypeOrNull());
    }

    default String getAttachmentTypeLookupType() {
        return checkNotBlank(getAttachmentTypeLookupTypeOrNull());
    }

    @Nullable
    default String getAttachmentTypeLookupTypeOrNull() {
        return getMetadata().getAttachmentTypeLookupTypeOrNull();
    }

    @Nullable
    default AttachmentDescriptionMode getAttachmentDescriptionMode() {
        return getMetadata().getAttachmentDescriptionMode();
    }

    default List<Attribute> getAttributesForDefaultOrder() {
        return getCoreAttributes().stream().filter((a) -> a.getClassOrder() != 0).sorted(Ordering.natural().onResultOf(compose(Math::abs, Attribute::getClassOrder))).collect(toList());
    }

    default boolean isAncestorOf(Classe otherClass) {
        return otherClass.getAncestors().contains(this.getName());
    }

    default boolean equalToOrAncestorOf(Classe otherClass) {
        return equal(this.getName(), otherClass.getName()) || isAncestorOf(otherClass);
    }

    default boolean equalToOrDescendantOf(String otherClassName) {
        return equal(this.getName(), otherClassName) || getAncestors().contains(otherClassName);
    }

    @Nullable
    default AttributeGroupInfo getAttributeGroupOrNull(String name) {
        return getAllAttributes().stream().map(Attribute::getGroupOrNull).filter(not(isNull())).filter(g -> equal(g.getName(), name)).findFirst().orElse(null);
    }

    default AttributeGroupInfo getAttributeGroup(String name) {
        return checkNotNull(getAttributeGroupOrNull(name), "attribute group not found for name =< %s >", name);
    }

    default boolean hasAttributeGroup(String name) {
        return getAttributeGroupOrNull(name) != null;
    }

    default boolean hasReferenceForDomain(Domain domain) {
        return getAllAttributes().stream().filter(a -> a.isOfType(REFERENCE)).map(Attribute::getType).map(ReferenceAttributeType.class::cast).anyMatch(a -> equal(a.getDomainName(), domain.getName()));
    }

}
