Ext.define('CMDBuildUI.view.administration.content.tasks.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.tasks.TopbarController'
    ],

    alias: 'widget.administration-content-tasks-topbar',
    controller: 'administration-content-tasks-topbar',
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
            text: CMDBuildUI.locales.Locales.administration.tasks.texts.addtask,
            localized:{
                text: 'CMDBuildUI.locales.Locales.administration.tasks.texts.addtask'
            },
            ui: 'administration-action-small',
            reference: 'adduser',
            itemId: 'adduser',
            iconCls: 'x-fa fa-plus',
            autoEl: {
                'data-testid': 'administration-user-toolbar-addUserBtn'
            }
        }, {
            xtype: 'textfield',
            name: 'search',
            width: 250,            
            emptyText: CMDBuildUI.locales.Locales.administration.tasks.emptytexts.searchingrid,
            localized:{
                emptyText: 'CMDBuildUI.locales.Locales.administration.tasks.emptytexts.searchingrid'
            },
            cls: 'administration-input',
            reference: 'searchtext',
            itemId: 'searchtext',
            bind: {
                value: '{search.value}',
                hidden: '{!canFilter}'
            },
            listeners: {
                specialkey: 'onSearchSpecialKey'
            },
            triggers: {
                search: {
                    cls: Ext.baseCSSPrefix + 'form-search-trigger',
                    handler: 'onSearchSubmit',
                    autoEl: {
                        'data-testid': 'administration-user-toolbar-form-search-trigger'
                    }
                },
                clear: {
                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                    handler: 'onSearchClear',
                    autoEl: {
                        'data-testid': 'administration-user-toolbar-form-clear-trigger'
                    }
                }
            },
            autoEl: {
                'data-testid': 'administration-user-toolbar-search-form'
            }
        }]
    }]
});