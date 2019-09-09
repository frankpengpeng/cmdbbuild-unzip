/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.multitenant;

import static com.google.common.base.Objects.equal;
import com.google.common.base.Optional;
import org.cmdbuild.auth.multitenant.api.TenantLoginData;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import static com.google.common.collect.Streams.stream;
import static java.lang.String.format;
import java.sql.ResultSet;
import java.util.Collections;
import static java.util.Collections.emptySet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.cmdbuild.auth.multitenant.UserAvailableTenantContextImpl.fullAccess;
import static org.cmdbuild.auth.multitenant.UserAvailableTenantContextImpl.minimalAccess;
import org.cmdbuild.auth.multitenant.api.TenantInfo;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.IGNORE_TENANT_POLICIES_TENANT_ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import static org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext.TenantActivationPrivileges.TAP_ONE;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MULTITENANT_CONFIG_PROPERTY_MODE;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MULTITENANT_CONFIG_PROPERTY_TENANT_CLASS;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MULTITENANT_CONFIG_PROPERTY_TENANT_DOMAIN;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MultitenantMode.CMDBUILD_CLASS;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MultitenantMode.DB_FUNCTION;
import static org.cmdbuild.auth.user.UserData.USER_CLASS_NAME;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.ConfigurableDataSource;
import static org.cmdbuild.dao.beans.CardIdAndClassNameImpl.card;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_RESERVED;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.services.MinionStatus;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.cmdbuild.services.PostStartup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.cmdbuild.services.MinionComponent;
import org.cmdbuild.services.MinionConfig;
import static org.cmdbuild.services.MinionConfig.MC_DISABLED;
import static org.cmdbuild.services.MinionConfig.MC_ENABLED;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_ERROR;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrEmpty;

@Component
@MinionComponent(name = "Multitenant", configBean = MultitenantConfiguration.class)
public class MultitenantServiceImpl implements MultitenantService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MultitenantConfiguration config;
    private final JdbcTemplate jdbcTemplate;
    private final GlobalConfigService configService;
    private final DaoService dao;
    private final ConfigurableDataSource dataSource;

    public MultitenantServiceImpl(MultitenantConfiguration config, @Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate, GlobalConfigService configService, DaoService dao, ConfigurableDataSource dataSource) {
        this.config = checkNotNull(config);
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        this.configService = checkNotNull(configService);
        this.dao = checkNotNull(dao);
        this.dataSource = checkNotNull(dataSource);
    }

    @PostStartup
    public void check() {
        if (isEnabled() && dataSource.isSuperuser()) {
            logger.warn(marker(), "CM: postgres is configured with a 'superuser' account; for row level security and multitenant to work a regular (non-superuser) user account is required");
        }
    }

    public MinionStatus getServiceStatus() {
        if (!isEnabled()) {
            return MS_DISABLED;
        } else {
            return checkConfig() == true ? MS_READY : MS_ERROR;
        }
    }

    public MinionConfig getMinionConfig() {
        return isEnabled() ? MC_ENABLED : MC_DISABLED;
    }

    @Override
    public boolean isEnabled() {
        return config.isMultitenantEnabled();
    }

    @Override
    public boolean isUserTenantUpdateEnabled() {
        return CMDBUILD_CLASS.equals(config.getMultitenantMode());
    }

    @Override
    public void setUserTenants(long userId, List<Long> newTenants) {
        logger.debug("set user tenants for userId = {} to tenant list = {}", userId, newTenants);
        checkArgument(isUserTenantUpdateEnabled());
        Set<Long> currentTenants = getUserTenantIds(userId);
        Set<Long> tenantsToAdd = set(newTenants).without(currentTenants),
                tenantsToRemove = set(currentTenants).without(newTenants);

        logger.debug("set user tenants for userId = {}, add tenants = {}, remove tenants = {}", userId, tenantsToAdd, tenantsToRemove);

        Domain tenantDomain = getTenantDomain();
        Classe tenantClass = getTenantClass();

        tenantsToAdd.forEach(t -> dao.createRelation(tenantDomain, card(USER_CLASS_NAME, userId), card(tenantClass.getName(), t)));
        tenantsToRemove.forEach(t -> dao.delete(dao.getRelation(tenantDomain.getName(), userId, t)));
    }

    @Override
    public UserTenantContext buildUserTenantContext(UserAvailableTenantContext availableTenantContext, @Nullable TenantLoginData tenantLoginData) {
        Set<Long> availableTenantIds = availableTenantContext.getAvailableTenantIds(),
                activeTenantIds = (tenantLoginData == null || tenantLoginData.getActiveTenants() == null) ? availableTenantIds : Sets.intersection(availableTenantIds, tenantLoginData.getActiveTenants());
        boolean ignoreTenantPolicies;
        if (availableTenantContext.ignoreTenantPolicies()) {
            if (tenantLoginData == null || tenantLoginData.ignoreTenantPolicies() == null) {
                ignoreTenantPolicies = config.tenantAdminIgnoresTenantByDefault();
            } else {
                ignoreTenantPolicies = tenantLoginData.ignoreTenantPolicies();
            }
        } else {
            ignoreTenantPolicies = false;
        }
        Long defaultTenant = Optional.fromNullable(tenantLoginData == null ? null : tenantLoginData.getDefaultTenant())
                .or(Optional.fromNullable(availableTenantContext.getDefaultTenantId()))
                .or(Optional.fromNullable(activeTenantIds.size() == 1 ? Iterables.getOnlyElement(activeTenantIds) : null))
                .orNull();
        checkArgument(defaultTenant == null || availableTenantIds.contains(defaultTenant));
        UserTenantContext userTenantContext = new UserTenantContextImpl(ignoreTenantPolicies, activeTenantIds, defaultTenant);
        if (equal(availableTenantContext.getTenantActivationPrivileges(), TAP_ONE)) {
            checkArgument(userTenantContext.getActiveTenantIds().size() <= 1, "cannot activate more than one tenant: permission denied");
        }
        return userTenantContext;
    }

    @Override
    public UserAvailableTenantContext getAvailableTenantContextForUser(Long userId) {
        logger.debug("fetchTenantIdsForUser = {}", userId);
        checkNotNull(userId);
        logger.debug("multitenant mode = {}", config.getMultitenantMode());
        try {
            Set<Long> tenantIds = getUserTenantIds(checkNotNull(userId));
            logger.debug("tenantIds = {}", tenantIds);
            boolean ignoreTenantPolicies = tenantIds.contains(IGNORE_TENANT_POLICIES_TENANT_ID);
            if (ignoreTenantPolicies) {
                tenantIds = getAllActiveTenantIds();//admin see all active tenants as available
            }
            return UserAvailableTenantContextImpl.builder().withAvailableTenantIds(tenantIds).withIgnoreTenantPolicies(ignoreTenantPolicies).build();
        } catch (Exception ex) {
            logger.error(marker(), "unable to retrieve tenant context for user = %s", userId, ex);
            return minimalAccess();
        }
    }

    private boolean checkConfig() {
        try {
            getUserTenantIds(-1l);
            checkArgument(dataSource.isNotSuperuser());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public UserAvailableTenantContext getAdminAvailableTenantContext() {
        try {
            return UserAvailableTenantContextImpl.builder().withAvailableTenantIds(getAllActiveTenantIds()).withIgnoreTenantPolicies(true).build();
        } catch (Exception ex) {
            logger.error(marker(), "unable to retrieve tenant context for admin", ex);
            return fullAccess();
        }
    }

    @Override
    public Set<Long> getAllActiveTenantIds() {
        return getUserTenantIds(null);
    }

    @Override
    public void enableMultitenantFunctionMode() {
        if (!equal(config.getMultitenantMode(), DB_FUNCTION)) {
            checkArgument(config.isMultitenantDisabled(), "cannot change multitenant mode: operation not allowed");
            configService.putString(MULTITENANT_CONFIG_PROPERTY_MODE, DB_FUNCTION.name());
        }
    }

    @Override
    public void enableMultitenantClassMode(String tenantClassId) { //TODO make this method transactional
        if (equal(config.getMultitenantMode(), CMDBUILD_CLASS)) {
            checkArgument(equal(tenantClassId, config.getTenantClass()), "cannot change tenant class: operation not allowed");
            logger.warn(marker(), "CM: multitenant mode is already enabled with tenant class = {}", tenantClassId);
        } else {
            checkArgument(config.isMultitenantDisabled(), "cannot change multitenant mode: operation not allowed");
            checkArgument(dataSource.isNotSuperuser(), "CM: postgres is configured with a 'superuser' account; for row level security and multitenant to work a regular (non-superuser) user account is required");

            Classe tenantClass = dao.getClasse(tenantClassId),
                    userClass = dao.getClasse(USER_CLASS_NAME);
            Domain tenantDomain = dao.createDomain(DomainDefinitionImpl.builder()
                    .withSourceClass(userClass)
                    .withTargetClass(tenantClass)
                    .withName(format("%s%s", userClass.getName(), tenantClass.getName()))
                    .withMetadata(DomainMetadataImpl.builder()
                            .withMode(CPM_RESERVED)
                            .withCardinality("N:N")//TODO
                            .withDirectDescription("belongs to tenant")
                            .withInverseDescription("has tenant user")
                            .build())
                    .build());
            String tenantDomainId = tenantDomain.getName();

            dao.getJdbcTemplate().queryForObject("SELECT _cm3_multitenant_tenant_class_trigger_install(_cm3_utils_name_to_regclass(?))", Object.class, tenantClass.getName());

            configService.putStrings(map(
                    MULTITENANT_CONFIG_PROPERTY_MODE, CMDBUILD_CLASS.name(),
                    MULTITENANT_CONFIG_PROPERTY_TENANT_CLASS, tenantClassId,
                    MULTITENANT_CONFIG_PROPERTY_TENANT_DOMAIN, tenantDomainId
            ));
        }
    }

    /**
     *
     * @param userId if null, return all active ids
     * @return
     */
    private Set<Long> getUserTenantIds(@Nullable Long userId) {
        switch (config.getMultitenantMode()) {
            case DISABLED: {
                //disabled, do nothing
                return emptySet();
            }
            case CMDBUILD_CLASS: {
                String tenantClass = getTenantClass().getName();
                if (userId == null) {
                    return ImmutableSet.copyOf(jdbcTemplate.queryForList(format("SELECT \"Id\" FROM \"%s\" WHERE \"Status\" = 'A'", tenantClass), Long.class));
                } else {
                    String tenantDomainName = getTenantDomain().getName();
                    logger.debug("getting tenants for user = {} from tenant class = {} domain = {}", userId, tenantClass, tenantDomainName);
                    return ImmutableSet.copyOf(jdbcTemplate.query(format("SELECT m.\"IdObj2\" tenant_id FROM \"Map_%s\" m WHERE m.\"Status\" = 'A' AND m.\"IdObj1\" = ?", tenantDomainName),//TODO handle inverse domains
                            (ResultSet rs, int rowNum) -> rs.getLong("tenant_id"), userId));
                }
            }
            case DB_FUNCTION: {
                String functionName = trimAndCheckNotBlank(config.getDbFunction(), "multitenant db function name cannot be null");
                checkArgument(functionName.matches("[a-z0-9_]+"), "unsupported multitenant function name syntax %s (must match /^[a-z0-9_]+$/)", functionName);
                logger.debug("querying tenant function = {} for user id = {}", functionName, userId);
                return ImmutableSet.copyOf(jdbcTemplate.queryForList("SELECT " + functionName + "(?)", new Object[]{firstNonNull(userId, -1l)}, Long.class));
            }
            default:
                throw unsupported("unsupported multitenant mode = %s", config.getMultitenantMode());
        }
    }

    @Override
    public Map<Long, String> getTenantDescriptions(Iterable<Long> tenantIds) {
        logger.debug("get tenant descriptions for tenants = {}", tenantIds);
        if (Iterables.isEmpty(tenantIds)) {
            return Collections.emptyMap();
        } else {
            Map<Long, String> map = map();
            jdbcTemplate.query(format("SELECT \"Id\",\"Description\",\"Code\" FROM \"Class\" WHERE \"Id\" IN (%s)", stream(tenantIds).map((t) -> "?").collect(joining(","))), (ResultSet rs) -> { //TODO translation
                map.put(rs.getLong("Id"), firstNotBlankOrEmpty(rs.getString("Description"), rs.getString("Code")));
            }, list(tenantIds).toArray());
            return map;
        }
    }

    @Override
    public List<TenantInfo> getAllActiveTenants() {
        Set<Long> tenantIds = getAllActiveTenantIds();
        Map<Long, String> tenantDescriptions = getTenantDescriptions(tenantIds);
        return tenantIds.stream().map((id) -> new TenantInfoImpl(id, tenantDescriptions.get(id))).sorted(Ordering.natural().onResultOf(TenantInfo::getDescription)).collect(toList());
    }

    @Override
    public List<TenantInfo> getAvailableUserTenants(UserAvailableTenantContext tenantContext) {
        return getAllActiveTenants().stream().filter((t) -> tenantContext.getAvailableTenantIds().contains(t.getId())).collect(toList());
    }

    private Classe getTenantClass() {
        checkArgument(CMDBUILD_CLASS.equals(config.getMultitenantMode()));
        return dao.getClasse(trimAndCheckNotBlank(config.getTenantClass()));
    }

    private Domain getTenantDomain() {
        checkArgument(CMDBUILD_CLASS.equals(config.getMultitenantMode()));
        return dao.getDomain(trimAndCheckNotBlank(config.getTenantDomain()));
    }

    private static class TenantInfoImpl implements TenantInfo {

        private final long id;
        private final String description;

        public TenantInfoImpl(Long id, String description) {
            this.id = id;
            this.description = description;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getDescription() {
            return description;
        }

    }

}
