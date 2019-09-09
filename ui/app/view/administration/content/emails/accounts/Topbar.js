Ext.define('CMDBuildUI.view.administration.content.emails.accounts.Topbar', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.accounts.TopbarController'
    ],

    alias: 'widget.administration-content-emails-accounts-topbar',
    controller: 'administration-content-emails-accounts-topbar',
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
                text: CMDBuildUI.locales.Locales.administration.emails.addaccount,
                localized:{
                    text: 'CMDBuildUI.locales.Locales.administration.emails.addaccount'
                },
                ui: 'administration-action-small',
                reference: 'addaccount',
                itemId: 'addaccount',
                iconCls: 'x-fa fa-plus',
                autoEl: {
                    'data-testid': 'administration-email-account-addAccountBtn'
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
                    value: '{search.value}',
                    hidden: '{!canFilter}'
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