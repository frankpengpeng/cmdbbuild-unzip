Ext.define('CMDBuildUI.view.administration.content.users.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.users.GridController',
        'CMDBuildUI.view.administration.content.users.GridModel',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],

    alias: 'widget.administration-content-users-grid',
    controller: 'administration-content-users-grid',
    viewModel: {
        type: 'administration-content-users-grid'
    },
    bind: {
        store: '{allUsers}',
        selection: '{selected}'
    },

    columns: [{
        text: CMDBuildUI.locales.Locales.administration.emails.username,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.username'
        },
        dataIndex: 'username',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
        },
        dataIndex: 'description',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email'
        },
        dataIndex: 'email',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.active,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
        },
        dataIndex: 'active',
        align: 'center',
        xtype: 'checkcolumn',
        disabled: true,
        disabledCls: '' // or don't add this config if you want the field to look disabled
    }],

    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',
        
        widget: {
            xtype: 'administration-content-users-card-viewinrow',
            autoHeight: true,
            ui: 'administration-tabandtools',
            bind: {
                theUser: '{theUser}'
            },
            viewModel: {}
        }
    }],

    autoEl: {
        'data-testid': 'administration-content-users-grid'
    },

    forceFit: true,
    loadMask: true,

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    labelWidth: "auto"
});