Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.Topbar', {
    extend: 'Ext.toolbar.Toolbar',

    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-permissions'
    },
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-components-topbar',
    dock: 'top',
    forceFit: true,
    loadMask: true,
    border: false,
    style: {
        borderColor: '#fff'
    },
    items: [{
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText: CMDBuildUI.locales.Locales.administration.groupandpermissions.emptytexts.searchingrid,
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.emptytexts.searchingrid'
        },
        cls: 'administration-input',
        reference: 'searchtext',
        itemId: 'searchtext',
        bind: {
            value: '{searchingrid.value}'
        },
        listeners: {
            change: 'onSearchChange'
        },
        triggers: {
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
        xtype: 'button',
        align: 'right',
        itemId: 'copyFrom',
        reference: 'copyfrom',
        iconCls: 'x-fa fa-clone',
        cls: 'administration-tool',
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.copyfrom,
        tooltip: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.copyfrom,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.copyfrom',
            tooltip: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.copyfrom'
        },
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },

        menu: {
            items: []
        },
        visible: false,
        autoEl: {
            'data-testid': 'administration-process-properties-tool-clone'
        }
    }, {
        xtype: 'tool',
        align: 'right',
        itemId: 'editBtn',
        cls: 'administration-tool',
        iconCls: 'x-fa fa-pencil',
        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
        },
        callback: 'onEditBtnClick',
        hidden: true,
        autoEl: {
            'data-testid': 'administration-groupandpermission-permission-tool-editbtn'
        },
        bind: {
            hidden: '{actions.edit}'
        }
    }]
});