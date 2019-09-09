Ext.define('CMDBuildUI.view.administration.content.gis.geoserverslayers.Topbar', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.geoserverslayers.TopbarController'
    ],

    alias: 'widget.administration-content-gis-geoserverslayers-topbar',
    controller: 'administration-content-gis-geoserverslayers-topbar',
    viewModel: {},

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',

        items: [{
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.administration.gis.addlayer,
                localized:{
                    text: 'CMDBuildUI.locales.Locales.administration.gis.addlayer'
                },
                ui: 'administration-action-small',
                reference: 'addlayer',
                itemId: 'addlayer',
                iconCls: 'x-fa fa-plus',
                autoEl: {
                    'data-testid': 'administration-email-account-addLayerBtn'
                }
            },
            {
                xtype: 'textfield',
                name: 'search',
                width: 250,
                enableKeyEvents: true,

                emptyText: CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search,
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search'
                },
                reference: 'accountsearchtext',
                itemId: 'accountsearchtext',
                cls: 'administration-input',
                bind: {
                    value: '{search.value}'
                },
                listeners: {
                    keyup: 'onKeyUp'
                },
                triggers: {
                    clear: {
                        cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                        handler: 'onSearchClear'
                    }
                }
            }
        ]
    }]
});