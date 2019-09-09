Ext.define('CMDBuildUI.view.relations.list.add.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-add-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addcardbtn': {
            beforerender: 'onAddCardBtnBeforeRender'
        },
        '#searchtextinput': {
            specialkey: 'onSearchSpecialKey'
        }, 
        '#refreshBtn' : {
            click: 'onRefreshBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.fields.reference.SelectionPopup} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        CMDBuildUI.util.helper.ModelHelper.getModel(
            vm.get("objectType"),
            vm.get("objectTypeName")
        ).then(function (model) {
            if (vm.get("searchvalue")) {
                view.lookupReference("searchtextinput").focus();
            }
            // model name
            var modelname = CMDBuildUI.util.helper.ModelHelper.getModelName(
                vm.get("objectType"),
                vm.get("objectTypeName")
            );
            vm.set("storeinfo.modelname", modelname);

            // reconfigure table
            view.reconfigure(null, CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                allowFilter: true,
                addTypeColumn: CMDBuildUI.util.helper.ModelHelper.getClassFromName(vm.get("objectTypeName")).get("prototype")
            }));

            // set autoload to true
            vm.set("storeinfo.autoload", true);
        });

        vm.bind({
            theRelation: "{theRelation}",
            records: "{records}"
        }, function(data) {
            if (data.theRelation && data.records) {
                
                function selectrecord(index) {
                    view.ensureVisible(index, {
                        select: true,
                        callback: function(success, record, node) {
                            // enable check if disabled
                            if (success) {
                                var checkcell = node.rows[0].cells[0];
                                checkcell.classList.remove(CMDBuildUI.view.relations.list.add.Grid.disabledcls);
                            }
                        }
                    });
                }
                if (!data.records.isLoaded()) {
                    data.records.on({
                        load: {
                            fn: function() {
                                var selrecordindex = data.records.find("_id", data.theRelation.get("_destinationId"));
                                if (selrecordindex !== -1) {
                                    selectrecord(selrecordindex);
                                } else {
                                    var proxy = data.records.getProxy();
                                    var url = proxy.getUrl();
                                    var params = Ext.apply(proxy.lastRequest.getParams(), {
                                        positionOf: data.theRelation.get("_destinationId"),
                                        positionOf_goToPage: false,
                                        page: 1,
                                        start: 0
                                    });
                                    Ext.Ajax.request({
                                        url: url,
                                        params: params,
                                        method: 'GET',
                                        success: function(response, opts) {
                                            var jsonResponse = JSON.parse(response.responseText);
                                            if (jsonResponse && jsonResponse.meta && jsonResponse.meta.positions && jsonResponse.meta.positions[data.theRelation.get("_destinationId")]) {
                                                selectrecord(jsonResponse.meta.positions[data.theRelation.get("_destinationId")].positionInTable);
                                            }
                                        }
                                    });
                                }
                            },
                            single: true
                        }
                    });
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddCardBtnBeforeRender: function (button, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        this.getView().updateAddButton(
            button,
            function(item, event, eOpts) {
                me.onAddCardBtnClick(item, event, eOpts);
            },
            vm.get("objectTypeName"),
            vm.get("objectType")
        );
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onAddCardBtnClick: function (item, event, eOpts) {
        var vm = this.getViewModel();
        var title = Ext.String.format(
            "{0} {1}",
            CMDBuildUI.locales.Locales.classes.cards.addcard,
            vm.get("objectTypeDescription")
        );
        var popup = CMDBuildUI.util.Utilities.openPopup(null, title, {
            xtype: 'classes-cards-card-create',
            fireGlobalEventsAfterSave: false,
            viewModel: {
                data: {
                    objectTypeName: item.objectTypeName
                }
            },
            buttons: [{
                text: CMDBuildUI.locales.Locales.common.actions.save,
                formBind: true, //only enabled once the form is valid
                disabled: true,
                ui: 'management-action-small',
                autoEl: {
                    'data-testid': 'relations-list-add-grid-create-save'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                },
                handler: function (button, e) {
                    var form = button.up("form");
                    if (form.isValid()) {
                        var object = form.getViewModel().get("theObject");
                        object.save({
                            success: function (record, operation) {
                                vm.get("records").getFilters().add({
                                    property: 'positionOf',
                                    value: record.getId()
                                });
                                vm.set("selection", record);
                                popup.destroy(true);
                            }
                        });
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.common.actions.cancel,
                ui: 'secondary-action-small',
                autoEl: {
                    'data-testid': 'relations-list-add-grid-create-cancel'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                },
                handler: function (button, e) {
                    popup.destroy(true);
                }
            }]
        });
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function () {
        var vm = this.getViewModel();
        if (vm.get("searchvalue")) {
            // add filter
            var store = vm.get("records");
            store.getAdvancedFilter().addQueryFilter(vm.get("searchvalue"));
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
        var store = vm.get("records");
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
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Ext.event.Event} event 
     * @param {Object} e 
     */
    onRefreshBtnClick: function(button, event, e) {
        this.getStore().load();
    }
});
