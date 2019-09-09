package org.cmdbuild.dao.beans;

import static com.google.common.collect.Streams.stream;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

import org.cmdbuild.utils.lang.CmMapUtils;
import org.joda.time.DateTime;
import org.cmdbuild.dao.entrytype.EntryType;

public interface DatabaseRecord extends DatabaseRecordValues {

    EntryType getType();

    @Nullable
    Long getId();

    @Nullable
    default Long getCurrentId() {
        return null;//TODO
    }

    @Nullable
    default Long getTenantId() {
        return null;//TODO implement everywhere, remove default from here
    }

    String getUser();

    DateTime getBeginDate();

    @Nullable
    DateTime getEndDate();

    Iterable<Entry<String, Object>> getRawValues();

    default Map<String, Object> getAllValuesAsMap() {
        return stream(getRawValues()).collect(CmMapUtils.toMap(Entry::getKey, Entry::getValue));
    }

}
