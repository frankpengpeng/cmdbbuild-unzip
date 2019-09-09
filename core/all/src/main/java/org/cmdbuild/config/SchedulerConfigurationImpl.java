package org.cmdbuild.config;

import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Maps.filterKeys;
import java.util.Map;
import static org.cmdbuild.config.SchedulerConfigurationImpl.SCHEDULER_CONFIG_NAMESPACE;
import org.cmdbuild.config.api.ConfigComponent;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.ConfigService;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.config.api.NamespacedConfigService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Component
@ConfigComponent(SCHEDULER_CONFIG_NAMESPACE)
public final class SchedulerConfigurationImpl implements SchedulerConfiguration {

    public static final String SCHEDULER_CONFIG_NAMESPACE = "org.cmdbuild.scheduler",
            SCHEDULER_CONFIG_ENABLED_KEY = "enabled";

    private final static Map<String, String> QUARTZ_DEFAULT_CONFIG_PARAMS = ImmutableMap.<String, String>builder()
            .put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool")
            .put("org.quartz.threadPool.threadCount", "5")
            .put("org.quartz.threadPool.threadPriority", "4")
            .put("org.quartz.scheduler.skipUpdateCheck", "true")
            .put("org.quartz.scheduler.instanceId", "AUTO")
            .put("org.quartz.scheduler.instanceName", "CMDBuildScheduler")
            .put("org.quartz.jobStore.useProperties", "true")
            .put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate")
            .put("org.quartz.jobStore.tablePrefix", "quartz.qrtz_")
            .put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX")
            .put("org.quartz.jobStore.dataSource", "CMDBuildDatasource")
            .put("org.quartz.jobStore.isClustered", "true")
            .build();

    @ConfigValue(key = SCHEDULER_CONFIG_ENABLED_KEY, description = "enable quartz scheduler (run core scheduled jobs, and custom jobs, if they are enabled)", defaultValue = TRUE)
    private boolean isEnabled;

    @ConfigService
    private NamespacedConfigService config;

    @Override
    public Map<String, String> getQuartzProperties() {
        return map(QUARTZ_DEFAULT_CONFIG_PARAMS).with(filterKeys(config.getAsMap(), k -> k.startsWith("org.quartz.")));
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

}
