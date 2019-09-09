package org.cmdbuild.lookup;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import javax.annotation.Nullable;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;

public interface LookupRepository {

    @Nullable
    Lookup getByIdOrNull(long lookupId);

    Collection<Lookup> getAll();

    Collection<Lookup> getByType(String type, CmdbFilter filter);

    Collection<LookupType> getAllTypes();

    @Nullable
    Lookup getOneByTypeAndCodeOrNull(String type, String code);

    @Nullable
    Lookup getOneByTypeAndDescriptionOrNull(String type, String description);

    Lookup createOrUpdate(Lookup lookup);

    LookupType getTypeByName(String lookupTypeName);

    LookupType createLookupType(LookupType lookupType);

    LookupType updateLookupType(String lookupTypeId, LookupType lookupType);

    void deleteLookupRecord(long lookupValueId);

    void deleteLookupType(String lookupTypeId);

    default Lookup getById(long lookupId) {
        return checkNotNull(getByIdOrNull(lookupId), "lookup not found for id = %s", lookupId);
    }

    default Collection<Lookup> getAllByType(String type) {
        return getByType(type, CmdbFilterImpl.noopFilter());
    }

    default Collection<Lookup> getAllByType(LookupType type) {
        return getAllByType(type.getName());
    }

    default Lookup getOneByTypeAndCode(String type, String code) {
        return checkNotNull(getOneByTypeAndCodeOrNull(type, code), "lookup not found for type =< %s > and code =< %s >", type, code);
    }

    default Lookup getOneByTypeAndCodeOrDescription(String type, String codeOrDescription) {
        Lookup value = getOneByTypeAndCodeOrNull(type, codeOrDescription);
        if (value == null) {
            value = getOneByTypeAndDescriptionOrNull(type, codeOrDescription);
        }
        return checkNotNull(value, "lookup not found for type =< %s > and code or description =< %s >", type, codeOrDescription);
    }

    default Lookup getOneByTypeAndDescription(String type, String description) {
        return checkNotNull(getOneByTypeAndDescriptionOrNull(type, description), "lookup not found for type =< %s > and description =< %s >", type, description);
    }

    default Lookup getOneByTypeAndId(String type, long lookupValueId) {
        return checkNotNull(getOneByTypeAndIdOrNull(type, lookupValueId), "lookup not found for type =< %s > and id = %s", type, lookupValueId);
    }

    default @Nullable
    Lookup getOneByTypeAndIdOrNull(String type, long lookupValueId) {
        Lookup lookup = getByIdOrNull(lookupValueId);
        if (lookup != null && equal(lookup.getType().getName(), type)) {
            return lookup;
        } else {
            return null;
        }
    }

    default boolean hasLookupWithTypeAndId(String type, long id) {
        return getOneByTypeAndIdOrNull(type, id) != null;
    }

    default boolean hasLookupWithTypeAndCode(String type, String code) {
        return getOneByTypeAndCodeOrNull(type, code) != null;
    }

    default boolean hasLookupWithTypeAndDescription(String type, String description) {
        return getOneByTypeAndDescriptionOrNull(type, description) != null;
    }

}
