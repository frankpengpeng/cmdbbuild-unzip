package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import static java.util.Collections.singleton;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.WILDCARD;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.config.api.ConfigDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.config.api.GlobalConfigService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import org.springframework.security.access.prepost.PreAuthorize;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

@Path("system/config")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
public class SystemConfigWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreConfiguration coreConfig;
    private final GlobalConfigService configService;

    public SystemConfigWs(GlobalConfigService configService, CoreConfiguration coreConfig) {
        this.configService = checkNotNull(configService);
        this.coreConfig = checkNotNull(coreConfig);
    }

    @GET
    @Path("")
    public Object getSystemConfig(@QueryParam("detailed") boolean detailed) {
        logger.debug("get system config");
        if (detailed) {
            Map<String, ConfigDefinition> definitions = configService.getConfigDefinitions();
            Map<String, String> configs = configService.getConfigAsMap();
            return response(set(definitions.keySet()).with(configs.keySet()).stream().sorted(Ordering.natural()).map((k) -> {
                boolean hasDefinition = definitions.containsKey(k);
                boolean hasValue = configs.containsKey(k);
                FluentMap map = map("hasDefinition", hasDefinition, "hasValue", hasValue);
                if (hasDefinition) {
                    ConfigDefinition definition = definitions.get(k);
                    map.put("description", definition.getDescription(),
                            "default", definition.getDefaultValue(),
                            "category", serializeEnum(definition.getCategory()),
                            "location", serializeEnum(definition.getLocation()));
                }
                if (hasValue) {
                    map.put("value", configs.get(k));
                }
                return Pair.of(k, map);
            }).collect(toMap(Pair::getLeft, Pair::getRight)));
        } else {
            return response(map(configService.getConfigAsMap()));
        }
    }

    @GET
    @Path("/{key}")
    public Object getSystemConfig(@PathParam("key") String key, @QueryParam("include_default") @DefaultValue("true") Boolean includeDefault) {
        String value;
        if (includeDefault) {
            value = configService.getStringOrDefault(key);
        } else {
            value = configService.getString(key);
        }
        return response(value);
    }

    @PUT
    @Path("/{key}")
    @Consumes(TEXT_PLAIN)
    public Object updateSystemConfigValue(@PathParam("key") String key, String value, @QueryParam("encrypt") Boolean encrypt) {
        checkCanEdit();
        checkNotProtected(singleton(key));
        logger.info("update system config for key = {} value = {}", key, value);
        configService.putString(key, value, firstNonNull(encrypt, false));
        return success();
    }

    @PUT
    @Path("/_MANY")
    public Object updateSystemConfigValues(Map<String, String> data) {
        checkCanEdit();
        checkNotProtected(data.keySet());
        logger.info("update system config with data = {}", data);
        configService.putStrings(data);
        return success();
    }

    @DELETE
    @Path("/{key}")
    public Object deleteSystemConfigValue(@PathParam("key") String key) {
        checkCanEdit();
        checkNotProtected(singleton(key));
        logger.info("delete system config by key = {}", key);
        configService.delete(key);
        return success();
    }

    @POST
    @Path("/reload")
    @Consumes(WILDCARD)
    public Object reloadConfig() {
        logger.info("reload config");
        configService.reload();
        return success();
    }

    private void checkCanEdit() {
        checkArgument(coreConfig.allowConfigUpdateViaWs(), "CM_CUSTOM_EXCEPTION: system configuration update is disabled for this instance (demo mode)");//TODO check message
    }

    private void checkNotProtected(Set<String> keys) {
        keys = keys.stream().filter(configService::isProtected).collect(toSet());
        checkArgument(keys.isEmpty(), "you are not allowed to manually update these protected config params = %s : operation not allowed", keys);
    }

}
