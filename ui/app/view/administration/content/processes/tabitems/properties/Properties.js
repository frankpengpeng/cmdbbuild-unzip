Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.Properties', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.processes.tabitems.properties.PropertiesController'
    ],

    alias: 'widget.administration-content-processes-tabitems-properties-properties',
    controller: 'administration-content-processes-tabitems-properties-properties',
    viewModel: {},
    bind: {
        hidden: '{!theProcess}'
    },
    autoScroll: false,
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    stores: [
        'processes.ProcessVersions'
    ],
    layout: 'border',
    items: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',

        items: [{
            xtype: 'tbfill'
        }, {
            xtype: 'tool',
            align: 'right',
            itemId: 'editBtn',
            cls: 'administration-tool',
            iconCls: 'x-fa fa-pencil',
            tooltip: CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.editBtn.tooltip,
            callback: 'onEditBtnClick',
            hidden: true,
            autoEl: {
                'data-testid': 'administration-process-properties-tool-editbtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.edit}'
            }
        }, {

            xtype: 'tool',
            align: 'right',
            itemId: 'deleteBtn',
            cls: 'administration-tool',
            iconCls: 'x-fa fa-trash',
            tooltip: CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.deleteBtn.tooltip,
            callback: 'onDeleteBtnClick',
            hidden: true,
            autoEl: {
                'data-testid': 'administration-process-properties-tool-deletebtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.delete}'
            }
        }, {
            xtype: 'tool',
            align: 'right',
            itemId: 'disableBtn',
            cls: 'administration-tool',
            iconCls: 'x-fa fa-ban',
            tooltip: CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.disableBtn.tooltip,
            callback: 'onDisableBtnClick',
            hidden: true,
            autoEl: {
                'data-testid': 'administration-process-properties-tool-disablebtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.disable}'
            }
        }, {

            xtype: 'tool',
            align: 'right',
            itemId: 'enableBtn',
            hidden: true,
            cls: 'administration-tool',
            iconCls: 'x-fa fa-check-circle-o',
            tooltip: CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.enableBtn.tooltip,
            callback: 'onEnableBtnClick',
            autoEl: {
                'data-testid': 'administration-process-properties-tool-enablebtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.enable}'
            }
        }, {

            xtype: 'button',
            align: 'right',
            itemId: 'versionBtn',
            iconCls: 'x-fa fa-download',
            cls: 'administration-tool',
            text: CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.versionBtn.tooltip,
            localized:{
                text: 'CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.versionBtn.tooltip',
                tooltip: 'CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.versionBtn.tooltip'
            },
            tooltip: CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.versionBtn.tooltip,
            
            menu: {
                items: []
            },
            visible: false,
            autoEl: {
                'data-testid': 'administration-process-properties-tool-version'
            },
            bind: {}
        }]
    }, {
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: [{
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-generaldatafieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-groupingsordersfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-xpdlfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-processparametersfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-enginefieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-attachmentsfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-defaultordersfieldset',
            bind: {
                hidden: '{actions.add}'
            }
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-contextmenusfieldset'
        }, {
            xtype: 'administration-content-processes-tabitems-properties-fieldsets-iconfieldset'
        }]
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
            xtype: 'component',
            flex: 1
        }, {
            text: CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.saveBtn,
            formBind: true, //only enabled once the form is valid
            disabled: true,
            ui: 'administration-action-small',
            listeners: {
                click: 'onSaveBtnClick'
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.processes.properties.toolbar.cancelBtn,
            ui: 'administration-secondary-action-small',
            listeners: {
                click: 'onCancelBtnClick'
            }
        }]
    }]
});