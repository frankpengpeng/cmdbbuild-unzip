package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ENDDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDDOMAIN;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LongAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.RegclassAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.dao.entrytype.EntryType.EntryTypeType.ET_DOMAIN;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface Domain extends EntryType, ClassPermissions, DomainDefinition {

    static final String DOMAIN_MANY_TO_ONE = "N:1",
            DOMAIN_ONE_TO_MANY = "1:N",
            DOMAIN_MANY_TO_MANY = "N:N";

    @Override
    default void accept(CMEntryTypeVisitor visitor) {
        visitor.visit(this);
    }

    default String getDirectDescription() {
        return getMetadata().getDirectDescription();
    }

    default String getInverseDescription() {
        return getMetadata().getInverseDescription();
    }

    default String getCardinality() {
        return getMetadata().getCardinality();
    }

    default boolean hasCardinality(String cardinality) {
        return equal(cardinality, getCardinality());
    }

    default boolean isMasterDetail() {
        return getMetadata().isMasterDetail();
    }

    default String getMasterDetailDescription() {
        return getMetadata().getMasterDetailDescription();
    }

    default String getMasterDetailFilter() {
        return getMetadata().getMasterDetailFilter();
    }

    @Override
    default boolean hasHistory() {
        return true;
    }

    default Collection<String> getDisabledSourceDescendants() {
        return getMetadata().getDisabledSourceDescendants();
    }

    default Collection<String> getDisabledTargetDescendants() {
        return getMetadata().getDisabledTargetDescendants();
    }

    default boolean isDisabledSourceDescendant(Classe classe) {
        return getDisabledSourceDescendants().contains(classe.getName());
    }

    default boolean isDisabledTargetDescendant(Classe classe) {
        return getDisabledTargetDescendants().contains(classe.getName());
    }

    /**
     * return optional ordering for domains, on the side of {@link #getSourceClass()}
     * (used to order instances of {@link #getSourceClass()} as seen in the detail
     * (relations) view of a single instance of {@link #getTargetClass()})
     *
     * @return ordering, or {@link DEFAULT_INDEX_VALUE} if not set
     */
    default int getIndexForSource() {
        return getMetadata().getIndexForSource();
    }

    /**
     * return optional ordering for domains, on the side of {@link #getTargetClass()}
     * (used to order instances of {@link #getTargetClass()} as seen in the detail
     * (relations) view of a single instance of {@link #getSourceClass()})
     *
     * @return ordering, or {@link DEFAULT_INDEX_VALUE} if not set
     */
    default int getIndexForTarget() {
        return getMetadata().getIndexForTarget();
    }

    @Override
    default Long getOid() {
        return getId();
    }

    static int DEFAULT_INDEX_VALUE = -1;

    @Override
    default String getPrivilegeId() {
        return String.format("Domain:%d", getId());
    }

    @Override
    default EntryTypeType getEtType() {
        return ET_DOMAIN;
    }

    @Override
    default boolean isActive() {
        return EntryType.super.isActive();
    }

    @Override
    default String getDescription() {
        return EntryType.super.getDescription();
    }

    default Classe getReferencedClass(ReferenceAttributeType attribute) {
        switch (attribute.getDirection()) {
            case RD_DIRECT:
                return getTargetClass();
            case RD_INVERSE:
                return getSourceClass();
            default:
                throw new IllegalArgumentException("unsupported domain direction = " + attribute.getDirection());
        }
    }

    default Classe getReferencedClass(Attribute attribute) {
        checkArgument(attribute.isOfType(REFERENCE));
        return getReferencedClass((ReferenceAttributeType) attribute.getType());
    }

    default boolean isDomainForClasse(Classe classe) {
        return isDomainForSourceClasse(classe) || isDomainForTargetClasse(classe);
    }

    default boolean isDomainForSourceClasse(Classe classe) {
        return getSourceClass().equalToOrAncestorOf(classe) && !getDisabledSourceDescendants().contains(classe.getName());
    }

    default boolean isDomainForTargetClasse(Classe classe) {
        return getTargetClass().equalToOrAncestorOf(classe) && !getDisabledTargetDescendants().contains(classe.getName());
    }

    /**
     * @return reoriented domain for this classe (so that this classe is on the 'source' side); if this classe is both a valid source and target, return two records (one for each direction); otherwise return one record
     */
    default List<Domain> getThisDomainDirectAndOrReversedForClass(Classe classe) {
        List<Domain> list = list();
        if (isDomainForSourceClasse(classe)) {
            list.add(this);
        }
        if (isDomainForTargetClasse(classe)) {
            list.add(ReverseDomain.of(this));
        }
        return checkNotEmpty(list, "this domain = %s is not a valid domain for class = %s", this, classe);
    }

    default Domain getThisDomainWithDirection(RelationDirection direction) {
        switch (direction) {
            case RD_DIRECT:
                return this;
            case RD_INVERSE:
                return ReverseDomain.of(this);
            default:
                throw new UnsupportedOperationException();
        }
    }

    default Attribute getIdObjAttrAsFkAttr(String name) {
        switch (name) {
            case ATTR_IDOBJ1:
                return AttributeImpl.builder().withOwner(this).withName(name).withType(new ForeignKeyAttributeType(getSourceClass())).build();
            case ATTR_IDOBJ2:
                return AttributeImpl.builder().withOwner(this).withName(name).withType(new ForeignKeyAttributeType(getTargetClass())).build();
            default:
                throw new IllegalArgumentException("invalid attr name = " + name);
        }
    }

    default boolean hasDomainKeyAttrs() {
        return getAllAttributes().stream().anyMatch(Attribute::isDomainKey);
    }

}
