Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.ClassesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-permissions-tabitems-classes-classes',

    mixins: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsMixin'
    ],

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#administration-classes-permissions': {
            actionfiltersrowclick: 'onActionFiltersClick'
        }
    },

    onBeforeRender: function () {
        var vm = this.getView().up('administration-content-groupsandpermissions-tabitems-permissions-permissions').getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getView().up('administration-content-groupsandpermissions-tabitems-permissions-permissions').getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.toggleEnablePermissionsTabs(0);
        this.toggleEnableTabs(1);
        button.up().down('grid').reconfigure();
    },


    /**
     * On disabled action button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDisabledActionClick: function (grid, rowIndex, colIndex, button, event, record) {
        grid.setSelection(record);
        var formMode = grid.grid.getViewModel().get('actions');
        var fbar;
        var me = this;

        switch (formMode.edit) {
            case true:
                fbar = [{
                    text: CMDBuildUI.locales.Locales.administration.common.actions.ok,
                    localized: {
                        text: CMDBuildUI.locales.Locales.administration.common.actions.ok
                    },
                    reference: 'savebutton',
                    itemId: 'savebutton',
                    viewModel: {},
                    listeners: {
                        click: function (button, event, eOpts) {
                            var record = this.getViewModel().get('record');
                            var fields = this.up('form').form.getFields().items;
                            if (Ext.Object.getSize(record.modified)) {
                                Ext.Array.forEach(fields, function (element) {
                                    record.set(element.config.field, element.checked);
                                    if (record.previousValues && record.modified) {
                                        delete record.previousValues[element.config.field];
                                        delete record.modified[element.config.field];
                                    }
                                });
                            }
                            button.up('#popup-disabled-actions').fireEvent('close');
                        }
                    },
                    ui: 'administration-action'
                }, {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
                    localized: {
                        text: CMDBuildUI.locales.Locales.administration.common.actions.cancel
                    },
                    reference: 'cancelbutton',
                    ui: 'administration-secondary-action',
                    viewModel: {},
                    listeners: {
                        click: function (button, event, eOpts) {
                            button.up('#popup-disabled-actions').fireEvent('close');
                        }
                    }
                }];
                break;

            default:
                fbar = [{
                    text: CMDBuildUI.locales.Locales.administration.common.actions.close,
                    localized: {
                        text: CMDBuildUI.locales.Locales.administration.common.actions.close
                    },
                    reference: 'closebutton',
                    ui: 'administration-secondary-action',
                    handler: function (button) {
                        button.up('#popup-disabled-actions').fireEvent('close');
                    }
                }];
                break;
        }
        var content = {
            xtype: 'form',
            scrollable: 'y',
            padding: 10,
            reference: 'customPrivilegesChecks',
            bind: {
                actions: '{actions}',
                record: '{record}'
            },
            config: {
                selection: grid.getSelection(),
                record: grid.getSelection()
            },
            viewModel: {
                data: {
                    index: rowIndex
                }
            },
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 150
            },
            items: [{
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.actions.create,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.actions.create'
                },
                config: {
                    field: '_card_create_disabled',
                    privilege: 'create'
                },
                value: record.get('_card_create_disabled'),
                viewModel: {},
                readOnly: formMode.view,
                listeners: {
                    change: me.onDisabledActionsCheckChange
                }
            }, {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.actions.update,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.actions.update'
                },
                config: {
                    field: '_card_update_disabled',
                    privilege: 'update'
                },
                value: record.get('_card_update_disabled'),
                readOnly: formMode.view,
                listeners: {
                    change: me.onDisabledActionsCheckChange
                }
            }, {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.actions.delete,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.actions.delete'
                },
                config: {
                    field: '_card_delete_disabled',
                    privilege: 'delete'
                },
                value: record.get('_card_delete_disabled'),
                readOnly: formMode.view,
                listeners: {
                    change: me.onDisabledActionsCheckChange
                }
            }, {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.actions.clone,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.actions.clone'
                },
                config: {
                    field: '_card_clone_disabled',
                    privilege: 'clone'
                },
                value: record.get('_card_clone_disabled'),
                viewModel: {},
                readOnly: formMode.view,
                listeners: {
                    change: me.onDisabledActionsCheckChange
                }
            }, {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.actions.relationchart,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.actions.relationchart'
                },
                config: {
                    field: '_card_relation_disabled',
                    privilege: 'relation'
                },
                value: record.get('_card_relation_isabled'),
                viewModel: {},
                readOnly: formMode.view,
                listeners: {
                    change: me.onDisabledActionsCheckChange
                }
            }, {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.actions.print,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.actions.print'
                },
                config: {
                    field: '_card_print_disabled',
                    privilege: 'print'
                },
                value: record.get('_card_print_disabled'),
                viewModel: {},
                readOnly: formMode.view,
                listeners: {
                    change: me.onDisabledActionsCheckChange
                }
            }],

            buttonAlign: 'center',
            fbar: fbar
        };
        // custom panel listeners
        var listeners = {
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                var fields = this.down('form').form.getFields().items;
                var record = this.getViewModel().get('record');
                Ext.Array.forEach(fields, function (element) {
                    if (record.previousValues && record.previousValues[element.config.field]) {
                        record.set(element.config.field, record.previousValues[element.config.field]);
                        delete record.previousValues[element.config.field];
                        delete record.modified[element.config.field];
                    }
                });
                record.crudState = record.crudStateWas = Ext.Object.getSize(record.modified) ? 'U' : 'R';

                CMDBuildUI.util.Utilities.closePopup('popup-disabled-actions');
            }
        };

        // create panel
        CMDBuildUI.util.Utilities.openPopup(
            'popup-disabled-actions',
            CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.disabledactions,
            content,
            listeners, {
                ui: 'administration-actionpanel',
                width: '200px',
                height: '320px',
                reference: 'popup-disabled-actions',
                viewModel: {
                    data: {
                        index: rowIndex,
                        grid: grid,
                        record: record
                    }
                }
            }
        );
    },

    onDisabledActionsCheckChange: function (check, newValue, oldValue) {

        var field = check.config.field;
        var vm = check.up('#popup-disabled-actions').getViewModel();
        var record = vm.get('record');
        var grid = vm.get('grid');
        var gridStore = grid.getStore();

        gridStore.getById(record.getId()).set(field, newValue);
        vm.set('record.' + field, newValue);

    },

    // /**
    //  * On remove filter action click
    //  * @param {Ext.button.Button} button
    //  * @param {Event} e
    //  * @param {Object} eOpts
    //  */
    // onRemoveFilterActionClick: function (grid, rowIndex, colIndex, button, event, record) {
    //     record.set('filter', '');
    //     record.set('attributePrivileges', {});        
    //     if (record.previousValues && (record.previousValues.filter || record.previousValues.attributePrivileges)) {
    //         delete record.previousValues.filter;
    //         delete record.previousValues.attributePrivileges;
    //         delete record.modified.filter;
    //         delete record.modified.attributePrivileges;
    //     }
    //     record.crudState = record.crudStateWas = Ext.Object.getSize(record.modified) ? 'U' : 'R';
    // },

    /**
     * On remove disabled action click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRemoveDisabledActionClick: function (grid, rowIndex, colIndex, button, event, record) {
        var fields = ['_card_create_disabled', '_card_update_disabled', '_card_delete_disabled', '_card_clone_disabled', '_card_realtion_disabled', '_card_print_disabled'];
        Ext.Array.forEach(fields, function (field) {
            record.set(field, false);
            if (record.previousValues && record.previousValues[field]) {
                delete record.previousValues[field];
                delete record.modified[field];
            }
        });
        record.crudState = record.crudStateWas = Ext.Object.getSize(record.modified) ? 'U' : 'R';
    }
});