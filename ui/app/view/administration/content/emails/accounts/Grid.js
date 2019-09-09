Ext.define('CMDBuildUI.view.administration.content.emails.accounts.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.accounts.GridController'
    ],

    alias: 'widget.administration-content-emails-accounts-grid',
    controller: 'administration-content-emails-accounts-grid',
    viewModel: {},

    forceFit: true,

    columns: [{
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default,
        localized:{
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default'
        },
        dataIndex: 'default',
        xtype: 'checkcolumn',
        disabled: true,
        disabledCls: '', // or don't add this config if you want the field to look disabled
        width: '10%'
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.name,
        localized:{
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
        },
        dataIndex: 'name',
        align: 'left',
        width: '45%'
    }, {
        text: CMDBuildUI.locales.Locales.administration.emails.address,
        localized:{
            text: 'CMDBuildUI.locales.Locales.administration.emails.address'
        },
        dataIndex: 'address',
        align: 'left',
        width: '45%'
    }],
    bind: {
        store: '{accounts}'
    },

    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',
        expandOnDblClick: true,
        removeWidgetOnCollapse: true,
        widget: {
            xtype: 'administration-content-emails-accounts-card-viewinrow',
            autoHeight: true,
            ui: 'administration-tabandtools',
            bind: {},
            viewModel: {}
        }
    }]
});