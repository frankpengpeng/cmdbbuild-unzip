Ext.define('CMDBuildUI.view.administration.content.setup.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.ViewController',
        'CMDBuildUI.view.administration.content.setup.ViewModel'
    ],
    alias: 'widget.administration-content-setup-view',
    controller: 'administration-content-setup-view',
    viewModel: {
        type: 'administration-content-setup-view'
    },

    
    cls: 'administration-mainview-tabpanel tab-hidden',
    ui: 'administration-tabandtools',
    layout: 'border',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        borderBottom: 1,
        items: [{

            xtype: 'button',
            itemId: 'spacer',
            style: {
                "visibility": "hidden"
            }
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'tool',
            itemId: 'editAttributeBtn',
            iconCls: 'x-fa fa-pencil',
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
            },    
            callback: 'onEditSetupBtnClick',
            cls: 'administration-tool',
            hidden: true,
            bind: {
                hidden: '{isEditBtnHidden}'
            },
            autoEl: {
                'data-testid': 'administration-setup-view-editBtn'
            }
        }, {
            xtype: 'tool',            
            iconCls: 'x-fa fa-pencil',
            tooltip: CMDBuildUI.locales.Locales.administration.systemconfig.editmultitenantisnotallowed,
            localized:{
                tooltip: 'CMDBuildUI.locales.Locales.administration.systemconfig.editmultitenantisnotallowed'
            },
            callback: 'onEditSetupBtnClick',
            cls: 'administration-tool',
            disabled: true,
            hidden: true,
            bind: {
                hidden: '{!isEditButtonDisabledVisible}'
            },
            autoEl: {
                'data-testid': 'administration-setup-view-disabledEditBtn'
            }
        }]
    }, {
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: []
    }],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        //margin: '5 5 5 5',
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
            xtype: 'tbfill'
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.save,
            localized:{
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.save'
            },
            ui: 'administration-action-small',
            listeners: {
                click: 'onSaveBtnClick'
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
            localized:{
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
            },
            ui: 'administration-secondary-action-small',
            listeners: {
                click: 'onCancelBtnClick'
            }
        }]
    }]
});