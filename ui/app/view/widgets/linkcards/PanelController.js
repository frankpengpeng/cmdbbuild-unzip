Ext.define('CMDBuildUI.view.widgets.linkcards.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-linkcards-panel',

    control: {
        "#": {
            beforerender: "onBeforeRender"
        },
        grid: {
            selectionchange: "onSelectionChange"
        },
        tableview: {
            actionviewobject: "onActionViewObject",
            actioneditobject: "onActionEditObject"
        },
        '#togglefilter': {
            toggle: 'onToggleFilterToggle'
        },
        '#refreshselection': {
            click: 'onRefreshSelectionClick'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.linkcards.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        var widget = vm.get("theWidget");
        var typeinfo = CMDBuildUI.view.widgets.linkcards.Panel.getTypeInfo(widget);
        var objectTypeName = typeinfo.objectTypeName,
            objectType = typeinfo.objectType;

        if (objectType) {
            vm.set("objectType", objectType);
            vm.set("objectTypeName", objectTypeName);
        } else {
            Ext.asap(function () {
                CMDBuildUI.util.Notifier.showErrorMessage(Ext.String.format(CMDBuildUI.locales.Locales.errors.classnotfound, objectTypeName));
            });
            return;
        }

        // get the model for objtect type name
        CMDBuildUI.util.helper.ModelHelper.getModel(
            objectType,
            objectTypeName
        ).then(
            function (model) {
                // set model variable into view model
                vm.set("model", model);
                // get columns from model
                var columns = CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                    allowFilter: false,
                    addTypeColumn: CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, vm.get("objectType")).get("prototype")
                });

                // add view object action columns
                columns.push({
                    xtype: 'actioncolumn',
                    minWidth: 30,
                    maxWidth: 30,
                    hideable: false,
                    disabled: true,
                    align: 'center',
                    bind: {
                        disabled: '{disableViewAction}'
                    },
                    iconCls: 'x-fa fa-external-link',
                    tooltip: CMDBuildUI.locales.Locales.widgets.linkcards.opencard,
                    handler: function (grid, rowIndex, colIndex) {
                        var record = grid.getStore().getAt(rowIndex);
                        grid.fireEvent("actionviewobject", grid, record, rowIndex, colIndex);
                    }
                });
                // add edit object action columns
                columns.push({
                    xtype: 'actioncolumn',
                    minWidth: 30,
                    maxWidth: 30,
                    hideable: false,
                    disabled: true,
                    align: 'center',
                    bind: {
                        disabled: '{disableEditAction}'
                    },
                    iconCls: 'x-fa fa-pencil',
                    tooltip: CMDBuildUI.locales.Locales.widgets.linkcards.editcard,
                    handler: function (grid, rowIndex, colIndex) {
                        var record = grid.getStore().getAt(rowIndex);
                        grid.fireEvent("actioneditobject", grid, record, rowIndex, colIndex);
                    }
                });

                // define selection model
                var selModel = {
                    selType: 'checkboxmodel',
                    showHeaderCheckbox: false,
                    checkOnly: true
                };
                if (vm.get("theWidget").get("NoSelect")) {
                    selModel = null;
                }
                if (vm.get("theWidget").get("SingleSelect")) {
                    selModel.mode = "SINGLE";
                }

                // add grid
                view.add({
                    xtype: 'grid',
                    columns: columns,
                    forceFit: true,
                    loadMask: true,
                    itemId: 'grid',
                    reference: 'grid',
                    selModel: selModel,
                    bind: {
                        store: '{gridrows}',
                        selection: '{selection}'
                    },
                    bubbleEvents: [
                        'itemupdated'
                    ]
                });

                // default selection
                
                if (!vm.get('theTarget').get(view.getOutput()).length)                
                {
                    me.onRefreshSelectionClick(view);
                }
                
            });
    },

    /**
     * @param {Ext.grid.Panel} view
     * @param {Ext.data.Model[]} selected
     * @param {Object} eOpts
     */
    onSelectionChange: function (grid, selected, eOpts) {
        var view = this.getView();
        if (view.getOutput()) {
            var sel = [];
            for (var i = 0; i < selected.length; i++) {
                sel.push({
                    _id: selected[i].getId()
                });
            }
            view.getTarget().set(view.getOutput(), sel);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Boolean} selected
     * @param {Object} eOpts
     */
    onToggleFilterToggle: function (button, selected, eOpts) {
        var params = {
            filter: null
        };

        // get the filter if toggle is not selected 
        if (!selected) {
            var vm = this.getViewModel();
            var filter = vm.get("theWidget").get("_Filter_ecql");
            var target = vm.get("theTarget");

            if (filter) {
                // calculate ecql
                var ecql = CMDBuildUI.util.ecql.Resolver.resolve(
                    filter,
                    target
                );
                if (ecql) {
                    params.filter = Ext.JSON.encode({
                        ecql: ecql
                    });
                }
            }
        }

        this.getView().lookupReference("grid").getStore().load({
            params: params
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRefreshSelectionClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        var me = this;
        CMDBuildUI.view.widgets.linkcards.Panel.loadDefaults(vm.get("theWidget"), vm.get("theTarget"), function (records) {
            var grid = me.getView().lookupReference("grid");
            if (grid) {
                grid.getSelectionModel().deselectAll();
            }
            vm.bind({
                bindTo: '{gridrows}'
            }, function (store) {
                var selection = [];
                records.forEach(function (record) {
                    var r = store.getById(record.getId());
                    if (r) {
                        selection.push(r);
                    }
                });
                vm.set("selection", selection);
            });
        });
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     */
    onActionViewObject: function (grid, record, rowIndex, colIndex) {
        var title, config;
        var vm = this.getViewModel();
        if (vm.get("objectType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass) {
            title = CMDBuildUI.util.helper.ModelHelper.getClassDescription(record.get("_type"));
            config = {
                xtype: 'classes-cards-card-view',
                viewModel: {
                    data: {
                        objectTypeName: record.get("_type"),
                        objectId: record.getId()
                    }
                },
                shownInPopup: true,
                hideTools: true
            };
        }
        CMDBuildUI.util.Utilities.openPopup('popup-open-card', title, config);
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     */
    onActionEditObject: function (grid, record, rowIndex, colIndex) {
        var title, config, popup;
        var me = this;
        var vm = this.getViewModel();
        if (vm.get("objectType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass) {
            title = CMDBuildUI.util.helper.ModelHelper.getClassDescription(record.get("_type"));
            config = {
                xtype: 'classes-cards-card-edit',
                objectTypeName: record.get("_type"),
                objectId: record.getId(),
                redirectAfterSave: false,
                buttons: [{
                        ui: 'management-action',
                        reference: 'savebtn',
                        itemId: 'savebtn',
                        text: CMDBuildUI.locales.Locales.common.actions.save,
                        autoEl: {
                            'data-testid': 'widgets-linkcards-save'
                        },
                        formBind: true,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.common.actions.save'
                        },
                        handler: function (btn, event) {
                            var panel = btn.lookupController();
                            panel.saveForm(function (record) {
                                popup.destroy();
                            });
                            grid.getStore().reload();
                            me.onToggleFilterToggle(null, vm.get("disablegridfilter"), null);
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.common.actions.cancel,
                        reference: 'cancelbtn',
                        itemId: 'cancelbtn',
                        ui: 'secondary-action-small',
                        autoEl: {
                            'data-testid': 'widgets-linkcards-cancel'
                        },
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                        },
                        handler: function (btn, event) {
                            popup.destroy();

                        }
                    }
                ]
            };
        }
        popup = CMDBuildUI.util.Utilities.openPopup('popup-edit-card', title, config);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloseBtnClick: function (button, e, eOpts) {
        this.getView().fireEvent("popupclose");
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // get value
        var searchTerm = vm.get("search.value");
        if (searchTerm) {
            // add filter
            var store = vm.get("gridrows");
            store.getAdvancedFilter().addQueryFilter(searchTerm);
            store.load();
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // clear store filter
        var store = vm.get("gridrows");
        store.getAdvancedFilter().clearQueryFilter();
        store.load();
        // reset input
        field.reset();
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field);
        }
    }
});