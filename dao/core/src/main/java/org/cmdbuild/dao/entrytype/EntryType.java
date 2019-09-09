package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.auth.grant.PrivilegeSubject;
import static org.cmdbuild.dao.entrytype.EntryType.EntryTypeType.ET_FUNCTION;
import static org.cmdbuild.dao.entrytype.EntryType.EntryTypeType.ET_DOMAIN;
import static org.cmdbuild.dao.entrytype.EntryType.EntryTypeType.ET_OTHER;
import static org.cmdbuild.dao.entrytype.EntryType.EntryTypeType.ET_CLASS;

public interface EntryType extends PrivilegeSubject, EntryTypeOrAttribute {

    Long getId();

    String getName();

    void accept(CMEntryTypeVisitor visitor);

    default boolean isActive() {
        return getMetadata().isActive();
    }

    default String getDescription() {
        return getMetadata().getDescription();
    }

    boolean hasHistory();

    default List<Attribute> getCoreAttributes() {
        return getAllAttributes().stream().filter(Attribute::hasCoreListPermission).collect(toList());
    }

    default List<Attribute> getServiceAttributes() {
        return getAllAttributes().stream().filter(Attribute::hasServiceListPermission).collect(toList());
    }

    Map<String, Attribute> getAllAttributesAsMap();

    default Collection<Attribute> getAllAttributes() {
        return getAllAttributesAsMap().values();
    }

    @Nullable
    default Attribute getAttributeOrNull(String name) {
        return getAllAttributesAsMap().get(checkNotBlank(name));
    }

    default Attribute getAttribute(String name) {
        return checkNotNull(getAttributeOrNull(name), "attribute not found for key = %s within classe = %s", name, this);
    }

    default boolean hasAttribute(String key) {
        return getAttributeOrNull(key) != null;
    }

    EntryTypeMetadata getMetadata();

    default EntryTypeType getEtType() {
        return ET_OTHER;
    }

    default boolean isClasse() {
        return equal(getEtType(), ET_CLASS);
    }

    default boolean isDomain() {
        return equal(getEtType(), ET_DOMAIN);
    }

    default boolean isFunction() {
        return equal(getEtType(), ET_FUNCTION);
    }

    default Domain asDomain() {
        return (Domain) this;
    }

    default Classe asClasse() {
        return (Classe) this;
    }

    enum EntryTypeType {
        ET_CLASS, ET_DOMAIN, ET_FUNCTION, ET_OTHER
    }

}
