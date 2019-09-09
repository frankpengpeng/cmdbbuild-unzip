package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import org.cmdbuild.config.BimserverConfiguration;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.config.GisConfiguration;
import org.cmdbuild.config.GraphConfiguration;
import org.cmdbuild.config.UiConfiguration;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.dms.DmsConfiguration;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmCollectionUtils.transformKeys;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("configuration/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ConfigurationWs {

    private final DmsConfiguration dmsConfiguration;
    private final MultitenantConfiguration multitenantConfiguration;
    private final WorkflowConfiguration workflowConfig;
    private final GisConfiguration gisConfiguration;
    private final BimserverConfiguration bimConfiguration;
    private final CoreConfiguration coreConfiguration;
    private final GraphConfiguration graphConfiguration;
    private final BuildInfoService buildInfoService;
    private final UiConfiguration uiConfiguration;
    private final AuthenticationConfiguration authConfiguration;

    public ConfigurationWs(DmsConfiguration dmsConfiguration, MultitenantConfiguration multitenantConfiguration, WorkflowConfiguration workflowConfig, GisConfiguration gisConfiguration, BimserverConfiguration bimConfiguration, CoreConfiguration coreConfiguration, GraphConfiguration graphConfiguration, BuildInfoService buildInfoService, UiConfiguration uiConfiguration, AuthenticationConfiguration authConfiguration) {
        this.dmsConfiguration = checkNotNull(dmsConfiguration);
        this.multitenantConfiguration = checkNotNull(multitenantConfiguration);
        this.workflowConfig = checkNotNull(workflowConfig);
        this.gisConfiguration = checkNotNull(gisConfiguration);
        this.bimConfiguration = checkNotNull(bimConfiguration);
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.graphConfiguration = checkNotNull(graphConfiguration);
        this.buildInfoService = checkNotNull(buildInfoService);
        this.uiConfiguration = checkNotNull(uiConfiguration);
        this.authConfiguration = checkNotNull(authConfiguration);
    }

    @GET
    @Path("public")
    public Object getPublicConfig() {
        return response(getPublicConfigData());
    }

    @GET
    @Path("system")
    public Object getSystemConfig() {
        return response(getPublicConfigData().with(
                "cm_system_logout_redirect", authConfiguration.getLogoutRedirectUrl(),
                "cm_system_dms_enabled", dmsConfiguration.isEnabled(),
                "cm_system_dms_category_lookup", dmsConfiguration.getDefaultDocumentCategoryLookup(),
                "cm_system_dms_description_mode", dmsConfiguration.getDefaultAttachmentDescriptionMode(),
                "cm_system_workflow_enabled", workflowConfig.isEnabled(),
                "cm_system_workflow_hideSaveButton", workflowConfig.hideSaveButton(),
                "cm_system_gis_enabled", gisConfiguration.isEnabled(),
                "cm_system_gis_geoserver_enabled", gisConfiguration.isGeoServerEnabled(),
                "cm_system_gis_navigation_enabled", gisConfiguration.isNavigationEnabled(),
                "cm_system_bim_enabled", bimConfiguration.isEnabled(),
                "cm_system_ui_detailwindow_width", uiConfiguration.getDetailWindowWidth(),
                "cm_system_ui_detailwindow_height", uiConfiguration.getDetailWindowHeight(),
                "cm_system_ui_referencecombolimit", uiConfiguration.getReferencecombolimit(),
                "cm_system_cardlock_enabled", coreConfiguration.getCardlockEnabled(),
                "cm_system_cardlock_showuser", coreConfiguration.getCardlockShowUser(),
                "cm_system_keep_filter_on_updated_card", uiConfiguration.getKeepFilterOnUpdatedCard(),
                "cm_system_ui_decimalsSeparator", uiConfiguration.getDecimalsSeparator(),
                "cm_system_ui_thousandsSeparator", uiConfiguration.getThousandsSeparator(),
                "cm_system_ui_dateFormat", uiConfiguration.getDateFormat(),
                "cm_system_ui_timeFormat", uiConfiguration.getTimeFormat(),
                "cm_system_ui_relationlimit", coreConfiguration.getRelationLimit()
        ).accept((m) -> {
            graphConfiguration.getConfig().forEach((k, v) -> {
                m.put(format("cm_system_relgraph_%s", k), v);
            });
            if (gisConfiguration.isEnabled()) {
                m.putAll(transformKeys(map(
                        "centerLat", gisConfiguration.getCenterLat(),
                        "centerLon", gisConfiguration.getCenterLon(),
                        "initialZoomLevel", gisConfiguration.getInitialZoomLevel(),
                        "osmMinZoom", gisConfiguration.getOsmMinZoom(),
                        "osmMaxZoom", gisConfiguration.getOsmMaxZoom()
                ), k -> format("cm_system_gis_%s", k)));
            }
        }));
    }

    private FluentMap<String, Object> getPublicConfigData() {
        return (FluentMap) map(
                "cm_system_instance_name", coreConfiguration.getInstanceName(),
                "cm_system_version", buildInfoService.getVersionNumberOrUnknownIfNotAvailable(),
                "cm_system_language_default", coreConfiguration.getDefaultLanguage(),
                "cm_system_use_language_prompt", coreConfiguration.useLanguagePrompt(),
                "cm_system_multitenant_enabled", multitenantConfiguration.isMultitenantEnabled()).skipNullValues().with(
                "cm_system_company_logo", coreConfiguration.getCompanyLogoUploadsId(),
                "cm_system_timeout", uiConfiguration.getUiTimeout()
        ).then();
    }

}
