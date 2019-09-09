Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.Properties', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.PropertiesController'
    ],

    alias: 'widget.administration-content-groupsandpermissions-tabitems-group-properties',
    controller: 'administration-content-groupsandpermissions-tabitems-group-properties',
    autoScroll: false,
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    layout: 'border',
    items: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
            xtype: 'button',
            itemId: 'spacer',
            style: {
                "visibility": "hidden"
            }
        }]
    }, {
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        bind: {
            hidden: '{!actions.view}'
        },
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
                'data-testid': 'administration-groupandpermission-group-tool-editbtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.edit}'
            }
        }, {
            xtype: 'tool',
            align: 'right',
            itemId: 'disableBtn',
            cls: 'administration-tool',
            iconCls: 'x-fa fa-ban',
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.disable,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.disable'
            },
            callback: 'onToggleEnableBtnClick',
            hidden: true,
            autoEl: {
                'data-testid': 'administration-groupandpermission-group-tool-disablebtn'
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
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.enable,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.enable'
            },
            callback: 'onToggleEnableBtnClick',
            autoEl: {
                'data-testid': 'administration-groupandpermission-group-tool-enablebtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.enable}'
            }
        }]
    }, {
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: [{
            xtype: 'administration-content-groupsandpermissions-tabitems-group-fieldsets-generaldatafieldset'
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
            text: CMDBuildUI.locales.Locales.administration.common.actions.save,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.save'
            },
            formBind: true, //only enabled once the form is valid
            disabled: true,
            ui: 'administration-action-small',
            listeners: {
                click: 'onSaveBtnClick'
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
            },
            ui: 'administration-secondary-action-small',
            listeners: {
                click: 'onCancelBtnClick'
            }
        }]
    }]
});