Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.TopbarController'
    ],
    viewModel: {},
    alias: 'widget.administration-content-groupsandpermissions-topbar',
    controller: 'administration-content-groupsandpermissions-topbar',

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.addgroup,
        localized:{
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.addgroup'
        },
        ui: 'administration-action-small',
        reference: 'addgroup',
        itemId: 'addgroup',
        iconCls: 'x-fa fa-plus',
        autoEl: {
            'data-testid': 'administration-groupandpermission-toolbar-addGroupBtn'
        }
    }, {
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText: CMDBuildUI.locales.Locales.administration.groupandpermissions.emptytexts.searchgroups,
        localized:{
            emptyText: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.emptytexts.searchgroups'
        },
        cls: 'administration-input',
        reference: 'searchtext',
        itemId: 'searchtext',
        bind: {
            value: '{search.value}'
        },
        listeners: {
            specialkey: 'onSearchSpecialKey'
        },
        triggers: {
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                handler: 'onSearchSubmit',
                autoEl: {
                    'data-testid': 'administration-groupandpermission-toolbar-form-search-trigger'
                }
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear',
                autoEl: {
                    'data-testid': 'administration-groupandpermission-toolbar-form-clear-trigger'
                }
            }
        },
        autoEl: {
            'data-testid': 'administration-groupandpermission-toolbar-search-form'
        }
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            hidden: '{isFormHidden}',
            html: '{groupLabel}: <b data-testid="administration-groupandpermission-toolbar-groupName">{theGroup.name}</b>' // TODO: translate
        }
    }]
});