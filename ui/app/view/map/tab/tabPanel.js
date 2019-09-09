
Ext.define('CMDBuildUI.view.map.tab.tabPanel', {
    extend: 'Ext.tab.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.tabPanelController',
        'CMDBuildUI.view.map.tab.tabPanelModel'

    ],

    alias: 'widget.map-tab-tabpanel',
    controller: 'map-tab-tabpanel',
    viewModel: {
        type: 'map-tab-tabpanel'
    },
    me: this,
    ui: 'managementlighttabpanel',
    deferredRender: false,
    collapseDirection: 'left',
    items: [{
        xtype: 'map-tab-cards-list',
        title: CMDBuildUI.locales.Locales.gis.list,
        localized: {
            title: 'CMDBuildUI.locales.Locales.gis.list'
        }
    }, {
        xtype: 'map-tab-cards-card',
        title: CMDBuildUI.locales.Locales.gis.card,
        localized: {
            title: 'CMDBuildUI.locales.Locales.gis.card'
        }
    }, {
        xtype: 'container',
        title: CMDBuildUI.locales.Locales.gis.layers,
        localized: {
            title: 'CMDBuildUI.locales.Locales.gis.layers'
        },
        layout: 'fit',
        reference: 'map-tab-cards-layer',
        deferredRender: false,
        items: [{
            xtype: 'map-tab-cards-layers'
        }]
    }, {
        xtype: 'panel',
        scrollable: true,
        itemId: 'map-legend',
        title: CMDBuildUI.locales.Locales.thematism.legend,
        localized: {
            title: 'CMDBuildUI.locales.Locales.thematism.legend'
        },
        items: [{
            xtype: 'thematisms-thematism-rules',
            hidden: true,
            bind: {
                hidden: '{legendTabHidden}'
            }
        }],
        disabled: true,
        bind: {
            disabled: '{legendTabHidden}'
        }

    }]
});
