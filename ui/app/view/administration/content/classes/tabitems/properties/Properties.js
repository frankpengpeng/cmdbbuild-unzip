Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.Properties', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.classes.tabitems.properties.PropertiesController'
    ],

    alias: 'widget.administration-content-classes-tabitems-properties-properties',
    controller: 'administration-content-classes-tabitems-properties-properties',
    autoScroll: false,
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    layout: 'border',
    items: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        items: [{
                xtype: 'tbfill'
            },
            {
                xtype: 'tool',
                align: 'right',
                itemId: 'editBtn',
                cls: 'administration-tool',
                iconCls: 'x-fa fa-pencil',
                tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.editBtn.tooltip,
                callback: 'onEditBtnClick',
                hidden: true,
                autoEl: {
                    'data-testid': 'administration-class-properties-tool-editbtn'
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
                tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.deleteBtn.tooltip,
                callback: 'onDeleteBtnClick',
                hidden: true,
                autoEl: {
                    'data-testid': 'administration-class-properties-tool-deletebtn'
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
                tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.disableBtn.tooltip,
                callback: 'onDisableBtnClick',
                hidden: true,
                autoEl: {
                    'data-testid': 'administration-class-properties-tool-disablebtn'
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
                tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.enableBtn.tooltip,
                callback: 'onEnableBtnClick',
                autoEl: {
                    'data-testid': 'administration-class-properties-tool-enablebtn'
                },
                bind: {
                    hidden: '{toolbarHiddenButtons.enable}'
                }
            }, {

                xtype: 'button',
                align: 'right',
                itemId: 'printBtn',
                iconCls: 'x-fa fa-print',
                cls: 'administration-tool',
                tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.printBtn.tooltip,
                menu: {
                    items: [{
                        text: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.printBtn.printAsPdf,
                        listeners: {
                            click: 'onPrintMenuItemClick'
                        },
                        fileType: 'PDF',
                        cls: 'menu-item-nospace',
                        autoEl: {
                            'data-testid': 'administration-class-properties-tool-print-pdf'
                        }
                    }, {
                        text: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.printBtn.printAsOdt,
                        listeners: {
                            click: 'onPrintMenuItemClick'
                        },
                        fileType: 'ODT',
                        cls: 'menu-item-nospace',
                        autoEl: {
                            'data-testid': 'administration-class-properties-tool-print-odf'
                        }
                    }]
                },
                visible: false,
                autoEl: {
                    'data-testid': 'administration-class-properties-tool-print'
                },
                bind: {
                    disabled: '{toolbarHiddenButtons.print}'
                }
            }
        ]
    }, {
        //ui:'administration-formpagination',
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: [{
                xtype: 'administration-content-classes-tabitems-properties-fieldsets-generaldatafieldset'
            },
            {
                xtype: 'administration-content-classes-tabitems-properties-fieldsets-classparametersfieldset' /*, bind: { hidden: '{actions.add}' } */
            },
            {
                xtype: 'administration-content-classes-tabitems-properties-fieldsets-attachmentsfieldset'
            },
            {
                xtype: 'administration-content-classes-tabitems-properties-fieldsets-defaultordersfieldset',
                bind: {
                    hidden: '{actions.add}'
                }
            },
            {
                xtype: 'administration-content-classes-tabitems-properties-fieldsets-groupingsordersfieldset'
            },
            {
                xtype: 'administration-content-classes-tabitems-properties-fieldsets-valididationfieldset' /*, bind: { hidden: '{actions.add}' }*/
            },
            {
                xtype: 'administration-content-classes-tabitems-properties-fieldsets-triggersfieldset' /*, bind: { hidden: '{actions.add}' } */
            },
            {
                xtype: 'administration-content-classes-tabitems-properties-fieldsets-contextmenusfieldset' /*, bind: { hidden: '{actions.add}' }*/
            },
            {
                xtype: 'administration-content-classes-tabitems-properties-fieldsets-formwidgetfieldset' /*, bind: { hidden: '{actions.add}' }*/
            },
            {
                xtype: 'administration-content-classes-tabitems-properties-fieldsets-iconfieldset'
            }
        ]
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }]

});