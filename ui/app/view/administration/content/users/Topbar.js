Ext.define('CMDBuildUI.view.administration.content.users.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.users.TopbarController'
    ],

    alias: 'widget.administration-content-users-topbar',
    controller: 'administration-content-users-topbar',
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
            text: CMDBuildUI.locales.Locales.administration.users.toolbar.addUserBtn.text,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.users.toolbar.addUserBtn.text'
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
            emptyText: CMDBuildUI.locales.Locales.administration.users.toolbar.searchTextInput.emptyText,
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.administration.users.toolbar.searchTextInput.emptyText'
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
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'tbtext',
            dock: 'right',
            bind: {
                html: CMDBuildUI.locales.Locales.administration.users.toolbar.classLabel + ': <b data-testid="administration-user-toolbar-userName">{theUser.name}</b>'
            }
        }]
    }]
});