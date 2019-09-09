Ext.define('CMDBuildUI.view.administration.content.bim.projects.Topbar', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.bim.projects.TopbarController',
        'CMDBuildUI.view.administration.content.bim.projects.TopbarModel'
    ],

    alias: 'widget.administration-content-bim-projects-topbar',
    controller: 'administration-content-bim-projects-topbar',
    viewModel: {
        type: 'administration-content-bim-projects-topbar'
    },

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
                text: CMDBuildUI.locales.Locales.administration.bim.addproject,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.bim.addproject'
                },
                ui: 'administration-action-small',
                reference: 'addproject',
                itemId: 'addproject',
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