Ext.define('CMDBuildUI.view.administration.content.emails.templates.Topbar', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.TopbarController'
    ],

    alias: 'widget.administration-content-emails-templates-topbar',
    controller: 'administration-content-emails-templates-topbar',
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
                text: CMDBuildUI.locales.Locales.administration.emails.addtemplate,
                ui: 'administration-action-small',
                reference: 'addtemplate',
                itemId: 'addtemplate',
                iconCls: 'x-fa fa-plus',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.emails.addtemplate'
                },
                autoEl: {
                    'data-testid': 'administration-template-toolbar-addTemplateBtn'
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
                reference: 'templatessearchtext',
                itemId: 'templatessearchtext',
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