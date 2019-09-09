(function () {
    var statics = {
        bim : {
            enabled: 'cm_system_bim_enabled',
            password: 'bimPassword',
            user: 'bimEmail'
        },
        common : {
            defaultlanguage: 'cm_system_language_default',
            instancename: 'cm_system_instance_name',
            companylogo: 'cm_system_company_logo',
            uselanguageprompt: 'cm_system_use_language_prompt',
            keepfilteronupdatedcard: 'cm_system_keep_filter_on_updated_card',
            version: 'cm_system_version',
            ajaxtimeout: 'cm_system_timeout',
            redirectonlogout: 'cm_system_logout_redirect'
        },
        cardlock: {
            enabled: 'cm_system_cardlock_enabled',
            showuser: 'cm_system_cardlock_showuser'
        },
        dms: {
            enabled: 'cm_system_dms_enabled',
            categorylookup: 'cm_system_dms_category_lookup',
            descriptionmode: 'cm_system_dms_description_mode'
        },
        gis : {
            enabled: 'cm_system_gis_enabled',
            geoserverEnabled: 'cm_system_gis_geoserver_enabled',
            navigationTreeEnabled: 'gis_navigation_enabled',
            initialZoom: 'cm_system_gis_initialZoomLevel',
            initialLat: 'cm_system_gis_centerLat',
            initialLon: 'cm_system_gis_centerLon',
            maxZoom: 'cm_system_gis_osmMaxZoom',
            minZoom: 'cm_system_gis_osmMinZoom'
        },
        multitenant : {
            enabled: 'cm_system_multitenant_enabled'
        },
        processes: {
            enabled: 'cm_system_workflow_enabled'
        },
        relgraph : {
            enabled: 'cm_system_relgraph_enabled',
            baselevel: 'cm_system_relgraph_baseLevel',
            clusteringThreshold: 'cm_system_relgraph_clusteringThreshold',
            displayLabel: 'cm_system_relgraph_displayLabel',
            edge: {
                color: 'cm_system_relgraph_edgeColor',
                tooltipEnabled: 'cm_system_relgraph_enableEdgeTooltip'
            },
            node: {
                tooltipEnabled: 'cm_system_relgraph_enableNodeTooltip',
                spriteDimension: 'cm_system_relgraph_spriteDimension',
                stepRadius: 'cm_system_relgraph_stepRadius'
            },
            viewport: {
                distance: 'cm_system_relgraph_viewPointDistance',
                height: 'cm_system_relgraph_viewPointHeight'
            }
        },
        ui: {
            detailwindow: {
                width: 'cm_system_ui_detailwindow_width',
                height: 'cm_system_ui_detailwindow_height'
            },
            referencecombolimit: 'cm_system_ui_referencecombolimit',
            relationlimit: 'cm_system_ui_relationlimit',
            fields: {
                decimalsSeparator: 'cm_system_ui_decimalsSeparator',
                thousandsSeparator: 'cm_system_ui_thousandsSeparator',
                dateFormat: 'cm_system_ui_dateFormat',
                timeFormat: 'cm_system_ui_timeFormat'
            }
        }
    };

    Ext.define('CMDBuildUI.model.Configuration', {
        extend: 'Ext.data.Model',

        statics: statics,
        fields: [{
            name: statics.dms.enabled,
            type: 'boolean'
        }, {
            name: statics.dms.categorylookup,
            type: 'string'
        }, {
            name: statics.dms.descriptionmode,
            type: 'string'
        }, {
            name: statics.processes.enabled,
            type: 'boolean'
        }, {
            name: statics.common.ajaxtimeout,
            type: 'integer',
            defaultValue: 60
        }, {
            name: statics.common.redirectonlogout,
            type: 'string',
            defaultValue: null
        }, {
            name: statics.common.instancename,
            type: 'string'
        }, {
            name: statics.common.companylogo,
            type: 'integer'
        }, {
            name: statics.common.defaultlanguage,
            type: 'string',
            defaultValue: 'en'
        }, {
            name: statics.common.uselanguageprompt,
            type: 'boolean'
        }, {
            name: statics.common.version,
            type: 'string'
        }, {
            name: statics.multitenant.enabled,
            type: 'boolean'
        },{
            name: statics.gis.enabled,//'cm_system_gis_enabled',
            type: 'boolean'
        }, {
            name: statics.gis.geoserverEnabled,//'cm_system_gis_geoserver_enabled',
            type: 'boolean'
        }, {
            name: statics.bim.enabled,//'cm_system_bim_enabled',
            type: 'boolean'
        }, {
            name: statics.bim.user,//'bimEmail',
            type: 'string',
            defaultValue: '__bimserver_username_placeholder__'
        }, {
            name: statics.bim.password, //'bimPassword',
            type: 'string',
            defaultValue: '__bimserver_password_placeholder__'
        }, {
            name: statics.gis.navigationTreeEnabled,//'cm_system_gis_navigation_enabled',
            type: 'boolean',
            defaultValue: false
        }, { //RELATION GRAPH
            name: statics.relgraph.enabled, //cm_system_relgraph_enabled ok 
            type: 'boolean',
            defaultValue: true
        }, {
            name: statics.relgraph.baselevel,//'cm_system_relgraph_baseLevel',
            type: 'integer',
            defaultValue: 3
        }, {
            name: statics.relgraph.clusteringThreshold,//'cm_system_relgraph_clusteringThreshold', //ok
            type: 'integer',
            defaultValue: 20
        }, {
            name: statics.relgraph.displayLabel,//'cm_system_relgraph_displayLabel',
            type: 'string', //
            defaultValue: 'none' //
        }, {
            name: statics.relgraph.edge.color,//'cm_system_relgraph_edgeColor',
            type: 'string',
            defaultValue: '#3D85C6'
        }, {
            name: statics.relgraph.edge.tooltipEnabled, //cm_system_relgraph_nodeTooltipEnabled
            type: 'boolean',
            defaultValue: true
        }, {
            name: statics.relgraph.node.tooltipEnabled,//'cm_system_relgraph_nodeTooltipEnabled',
            type: 'boolean',
            defaultValue: true
        }, {
            name: statics.relgraph.node.spriteDimension,//'cm_system_relgraph_spriteDimension',
            type: 'integer',
            defaultValue: 20
        }, {
            name: statics.relgraph.node.stepRadius,//'cm_system_relgraph_stepRadius',
            type: 'integer',
            defaultValue: 60
        }, {
            name: statics.relgraph.viewport.distance,//'cm_system_relgraph_viewPointDistance',
            type: 'integer',
            defaultValue: 50
        }, {
            name: statics.relgraph.viewport.height,//'cm_system_relgraph_viewPointHeight',
            type: 'integer',
            defaultValue: 50
        }, {
            name: statics.ui.detailwindow.width,
            type: 'integer',
            defaultValue: 75
        }, {
            name: statics.ui.detailwindow.height,
            type: 'integer',
            defaultValue: 95
        }, {
            name: statics.ui.referencecombolimit,
            type: 'integer',
            defaultValue: 500
        }, {
            name: statics.ui.relationlimit,
            type: 'integer',
            defaultValue: 20
        }, {
            name: statics.ui.fields.thousandsSeparator,
            type: 'string',
            defaultValue: ','
        }, {
            name: statics.ui.fields.decimalsSeparator,
            type: 'string',
            defaultValue: '.'
        }, {
            name: statics.ui.fields.dateFormat,
            type: 'string'
        }, {
            name: statics.ui.fields.timeFormat,
            type: 'string'
        }, {
            name: statics.cardlock.enabled,
            type: 'boolean',
            defaultValue: false
        }, {
            name: statics.cardlock.showuser,
            type: 'boolean',
            defaultValue: false
        }, {
            name: statics.common.keepfilteronupdatedcard,
            type: 'boolean',
            defaultValue: false
        }]
    });
})();
