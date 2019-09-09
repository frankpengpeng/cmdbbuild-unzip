Ext.define('CMDBuildUI.view.relations.fieldset.FieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-fieldset',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addrelationbtn': {
            click: 'onAddRelationBtnClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.relations.fieldset.Fieldset} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel();
        var me = this;
        vm.bind("{targetmodel}", function (targetmodel) {
            if (targetmodel) {
                // details xtype
                var detailxtype, btnHidden;
                if (vm.get("targettype") === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                    detailxtype = 'processes-instances-instance-view';
                    btnHidden = true;
                } else {
                    detailxtype = 'classes-cards-card-view';
                    btnHidden = false;
                }
                
                btnHidden = view.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.read ? true : btnHidden;

                vm.set("addrelationbtn.hidden", btnHidden);
                // get columns
                var cols = CMDBuildUI.util.helper.GridHelper.getColumns(targetmodel.getFields());
                view.add({
                    xtype: 'relations-fieldset-grid',
                    columns: cols,
                    reference: 'relgrid',

                    plugins: [{
                        ptype: 'forminrowwidget',
                        expandOnDblClick: true,
                        removeWidgetOnCollapse: true,
                        widget: {
                            xtype: detailxtype,
                            viewModel: {
                                data: {
                                    basepermissions: view.lookupViewModel().get("basepermissions")
                                }
                            }, // do not remove otherwise the viewmodel will not be initialized
                            tabpaneltools: [{
                                xtype: 'tool',
                                itemId: 'viewcardaction',
                                iconCls: 'x-fa fa-external-link',
                                cls: 'management-tool',
                                tooltip: CMDBuildUI.locales.Locales.relations.opencard,
                                callback: function (panel, tool, event) {
                                    me.onItemViewCardActionClick(panel.up());
                                },
                                autoEl: {
                                    'data-testid': 'relations-fieldset-viewcardaction'
                                }
                            }, /*{
                                xtype: 'tool',
                                itemId: 'editrelationaction',
                                iconCls: 'x-fa fa-pencil',
                                cls: 'management-tool',
                                disabled: true,
                                hidden: btnHidden,
                                tooltip: CMDBuildUI.locales.Locales.relations.editrelation,
                                callback: function (panel, tool, event) {
                                    me.onItemEditRelationActionClick(panel.up());
                                },
                                autoEl: {
                                    'data-testid': 'relations-fieldset-editrelaction'
                                },
                                bind: {
                                    disabled: '{!basepermissions.edit && !permissions.edit}'
                                }
                            }, {
                                xtype: 'tool',
                                itemId: 'deleterelationaction',
                                iconCls: 'x-fa fa-trash',
                                cls: 'management-tool',
                                disabled: true,
                                hidden: btnHidden,
                                tooltip: CMDBuildUI.locales.Locales.relations.deleterelation,
                                callback: function (panel, tool, event) {
                                    me.onItemDeleteRelationActionClick(panel.up());
                                },
                                autoEl: {
                                    'data-testid': 'relations-fieldset-deleterelaction'
                                },
                                bind: {
                                    disabled: '{!basepermissions.delete && !permissions.delete}'
                                }
                            }, */{
                                xtype: 'tool',
                                itemId: 'editcardaction',
                                iconCls: 'x-fa fa-pencil-square-o',
                                cls: 'management-tool',
                                disabled: true,
                                hidden: btnHidden,
                                tooltip: CMDBuildUI.locales.Locales.relations.editcard,
                                callback: function (panel, tool, event) {
                                    me.onItemEditCardActionClick(panel.up());
                                },
                                autoEl: {
                                    'data-testid': 'relations-fieldset-editcardaction'
                                },
                                bind: {
                                    disabled: '{!basepermissions.edit && !permissions.edit}'
                                }
                            }]
                        }
                    }]
                });
            }
        });

        vm.bind("{records}", function(records) {
            records.addListener("load", function() {
                vm.set("recordscount", records.getTotalCount());
            });
        });
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onAddRelationBtnClick: function(button, eOpts) {
        var vm = this.getViewModel();
        var view = this.getView();
        var domain = vm.get("domain");
        var direction = vm.get("direction") === "_1" ? 'inverse' : 'direct';
        
        var popup;
        var title = vm.get("basetitle");
        var config = {
            xtype: 'relations-list-add-gridcontainer',
            originTypeName: vm.get("objectTypeName"),
            originId: vm.get("objectId"),
            multiSelect: true,
            viewModel: {
                data: {
                    objectTypeName: vm.get("targettypename"),
                    relationDirection: direction,
                    theDomain: domain
                }
            },
            listeners: {
                popupclose: function () {
                    popup.removeAll(true);
                    popup.close();
                }
            },
            onSaveSuccess: function() {
                view.down("grid").getStore().reload();
            }
        };

        popup = CMDBuildUI.util.Utilities.openPopup('popup-add-relation', title, config, null);

    },

    privates: {
        /**
         * 
         * @param {Ext.form.Panel} view 
         */
        onItemViewCardActionClick: function (view) {
            var path;
            var vm = view.lookupViewModel();
            switch (vm.get("objectType")) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    path = Ext.String.format('classes/{0}/cards/{1}/view', vm.get("objectTypeName"), vm.get("objectId"));
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                    path = Ext.String.format('processes/{0}/instances/{1}', vm.get("objectTypeName"), vm.get("objectId"));
                    break;
            }
            this.redirectTo(path);
        },

        /**
         * 
         * @param {Ext.form.Panel} view 
         */
        onItemEditCardActionClick: function (view) {
            var popup;
            var vm = view.lookupViewModel();
            // open popup
            var config = {
                xtype: 'classes-cards-card-edit',
                objectTypeName: vm.get("objectTypeName"),
                objectId: vm.get("objectId"),

                buttons: [{
                    ui: 'management-action',
                    reference: 'detailsavebtn',
                    itemId: 'detailsavebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    autoEl: {
                        'data-testid': 'relations-fieldset-editcard-save'
                    },
                    formBind: true,
                    handler: function (btn, event) {
                        popup.down("classes-cards-card-edit").getController().saveForm(function (record) {
                            view.up("grid").updateRowWithExpader(record);
                            popup.destroy();
                        });
                    }
                }, {
                    ui: 'secondary-action',
                    reference: 'detailclosebtn',
                    itemId: 'detailclosebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.close,
                    autoEl: {
                        'data-testid': 'relations-fieldset-editcard-cancel'
                    },
                    handler: function (btn, event) {
                        popup.destroy();
                    }
                }],

                listeners: {
                    itemupdated: function () {
                        popup.close();
                        grid.getStore().load();
                    },
                    cancelupdating: function () {
                        popup.close();
                    }
                }
            };
            popup = CMDBuildUI.util.Utilities.openPopup('popup-edit-card', CMDBuildUI.locales.Locales.relations.editcard, config);
        }
    }
});
