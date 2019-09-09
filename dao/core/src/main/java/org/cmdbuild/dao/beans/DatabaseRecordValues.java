package org.cmdbuild.dao.beans;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Streams.stream;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import org.cmdbuild.utils.lang.CmMapUtils;

public interface DatabaseRecordValues {

    @Nullable
    Object get(String key);

    Iterable<Map.Entry<String, Object>> getAttributeValues();

    @Nullable
    default <T> T get(String key, Class<? extends T> requiredType) {
        return convert(get(key), requiredType);
    }

    default <T> T getNotNull(String key, Class<? extends T> requiredType) {
        return checkNotNull(get(key, requiredType), "value is null or missing for key =< %s > within record = %s", key, this);
    }

    default <T> T get(String key, Class<? extends T> requiredType, T defaultValue) {
        return defaultIfNull(get(key, requiredType), defaultValue);
    }

    @Nullable
    default String getString(String key) {
        return get(key, String.class);
    }

    @Nullable
    default Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    @Nullable
    default String getDescriptionOf(String key) {
        return Optional.ofNullable(get(key, IdAndDescription.class)).map(IdAndDescription::getDescription).orElse(null);
    }

    static DatabaseRecordValues fromMap(Map map) {
        return new DatabaseRecordValues() {
            @Override
            public Object get(String key) {
                return map.get(key);
            }

            @Override
            public Iterable<Map.Entry<String, Object>> getAttributeValues() {
                return map.values();
            }
        };
    }

    default Map<String, Object> toMap() {
        return stream(getAttributeValues()).collect(CmMapUtils.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    default boolean allValuesEqualTo(DatabaseRecordValues other) {
        Map<String, Object> thisValues = toMap(), otherValues = other.toMap();
        return equal(thisValues.keySet(), otherValues.keySet()) && thisValues.keySet().stream().allMatch(k -> {
            Object one = thisValues.get(k), two = otherValues.get(k);
            if (equal(one, two)) {
                return true;
            } else if (one == null || two == null) {
                return false;
            } else if (one instanceof IdAndDescription && two instanceof IdAndDescription) {
                return equal(((IdAndDescription) one).getId(), ((IdAndDescription) two).getId());
            } else {
                return false;
            }
        });
    }

    default Set<String> getAttrsChangedFrom(DatabaseRecordValues other) {//TODO merge with above
        Map<String, Object> thisValues = toMap(), otherValues = other.toMap();
        return set(thisValues.keySet()).with(otherValues.keySet()).without(k -> {
            Object one = thisValues.get(k), two = otherValues.get(k);
            if (equal(one, two)) {
                return true;
            } else if (one == null || two == null) {
                return false;
            } else if (one instanceof IdAndDescription && two instanceof IdAndDescription) {
                return equal(((IdAndDescription) one).getId(), ((IdAndDescription) two).getId());
            } else {
                return false;
            }
        });
    }

}
