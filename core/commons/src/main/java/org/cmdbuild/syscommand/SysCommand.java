/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.syscommand;

import static com.google.common.base.Objects.equal;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.scheduler.JobClusterMode;
import static org.cmdbuild.scheduler.JobClusterMode.CM_RUN_ON_ALL_NODES;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface SysCommand {

    String getAction();

    Map<String, Object> getData();

    @Nullable
    default Object get(String key) {
        return getData().get(key);
    }

    @Nullable
    default <T> T get(String key, Class<T> type) {
        return convert(getData().get(key), type);
    }

    default String getId() {
        return checkNotBlank(get("id", String.class));
    }

    default JobClusterMode getCommandClusterMode() {
        return parseEnumOrDefault(get("cluster_mode", String.class), CM_RUN_ON_ALL_NODES);
    }

    default boolean runOnAllClusterNodes() {
        return equal(CM_RUN_ON_ALL_NODES, getCommandClusterMode());
    }
}
