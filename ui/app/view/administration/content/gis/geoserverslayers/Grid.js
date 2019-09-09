Ext.define('CMDBuildUI.view.administration.content.gis.geoserverslayers.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.geoserverslayers.GridController'
    ],

    alias: 'widget.administration-content-gis-geoserverslayers-grid',
    controller: 'administration-content-gis-geoserverslayers-grid',
    viewModel: {},

    forceFit: true,
    loadMask: true,
    bind: {
        store: '{layersStore}'
    },

    viewConfig: {
        markDirty: false,
        plugins: [{
            ptype: 'gridviewdragdrop',
            dragText: CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop,
            // TODO: localized not work as expected
            localized: {
                dragText: 'CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop'
            },
            containerScroll: true,
            pluginId: 'gridviewdragdrop'
        }]
    },

    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',
        expandOnDblClick: true,
        removeWidgetOnCollapse: true,
        widget: {
            xtype: 'administration-content-gis-geoserverslayers-card-viewinrow',
            autoHeight: true,
            ui: 'administration-tabandtools',
            bind: {},
            viewModel: {}
        }
    }],

    columns: [{
            text: CMDBuildUI.locales.Locales.administration.common.labels.name,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
            },
            dataIndex: 'name',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            dataIndex: 'description',
            align: 'left'
        }, {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.type,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.type'
            },
            dataIndex: 'type',
            align: 'left'
        }, {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom'
            },
            dataIndex: 'zoomMin',
            align: 'left'
        }, {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom'
            },
            dataIndex: 'zoomMax',
            align: 'left'
        }, {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom'
            },
            dataIndex: 'zoomDef',
            align: 'left'
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            disabled: true,
            disabledCls: '',
            xtype: 'checkcolumn',
            dataIndex: 'active'
        }
    ],

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.gis.layersorder);
        this.callParent(arguments);
    }
});