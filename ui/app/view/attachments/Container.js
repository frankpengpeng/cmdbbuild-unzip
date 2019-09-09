
Ext.define('CMDBuildUI.view.attachments.Container',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.attachments.ContainerController',
        'CMDBuildUI.view.attachments.ContainerModel'
    ],

    alias: 'widget.attachments-container',
    controller: 'attachments-container',
    viewModel: {
        type: 'attachments-container'
    },

    layout: 'card',
    items: [{
        xtype: 'attachments-grid'
    }],

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.attachments.add,
        reference: 'addattachment',
        itemId: 'addattachment',
        iconCls: 'x-fa fa-plus',
        ui: 'management-action-small',
        disabled: true,
        hidden: true,
        bind: {
            disabled: '{!basepermissions.edit}',
            hidden: '{disableActions.add}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.attachments.add'
        },
        autoEl: {
            'data-testid': 'attachments-container-addbtn'
        }
    }]
});
