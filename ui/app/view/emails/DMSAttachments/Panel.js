
Ext.define('CMDBuildUI.view.emails.DMSAttachments.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.emails.DMSAttachments.PanelController',
        'CMDBuildUI.view.emails.DMSAttachments.PanelModel'
    ],

    alias: 'widget.emails-dmsattachments-panel',
    controller: 'emails-dmsattachments-panel',
    viewModel: {
        type: 'emails-dmsattachments-panel'
    },

    layout: 'vbox',

    tbar: [{
        xtype: 'combobox',
        fieldLabel: CMDBuildUI.locales.Locales.emails.selectaclass,
        localized:{
            fieldLabel: 'CMDBuildUI.locales.Locales.emails.selectaclass'
        },
        displayField: 'label',
        valueField: ['value'],
        queryMode: 'local',
        forceSelection: true,
        width: 500,
        itemId: 'comboclass',
        reference: 'comboclass',
        bind: {
            store: '{attributeslist}'
        }
    }],

    items: [{
        flex: 1,
        layout: 'fit',
        xtype: 'container',
        itemId: 'classcontainer',
        reference: 'classcontainer',
        width: '100%'
    },
    {
        flex: 1,
        layout: 'fit',
        xtype: 'container',
        itemId: 'attachmentcontainer',
        reference: 'attachmentcontainer',
        width: '100%'
    }
    ],
    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        reference: 'saveBtn',
        itemId: 'saveBtn',
        ui: 'management-action',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        reference: 'cancelBtn',
        itemId: 'cancelBtn',
        ui: 'secondary-action',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }]
});
