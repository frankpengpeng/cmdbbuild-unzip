Ext.define('CMDBuildUI.view.processes.instances.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.processes-instances-grid',

    listen: {
        global: {
            processinstanceaborted: 'onProcessInstanceAborted',
            processinstancecreated: 'onProcessInstanceUpdated',
            processinstanceupdated: 'onProcessInstanceUpdated'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            rowdblclick: 'onRowDblClick',
            selectionchange: 'onSelectionChange',
            selectedidchanged: 'onSelectedIdChanged'
        },
        '#addbtn': {
            beforerender: 'onAddBtnBeforeRender'
        },
        '#statuscombo': {
            beforerender: 'onStatusComboBeforeRender',
            cleartrigger: 'onStatusComboClear'
        },
        '#refreshBtn': {
            click: 'onRefreshBtnClick'
        },
        '#printPdfBtn': {
            click: 'onPrintBtnClick'
        },
        '#printCsvBtn': {
            click: 'onPrintBtnClick'
        },
        '#contextMenuBtn': {
            beforerender: 'onContextMenuBtnBeforeRender'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        var objectTypeName = vm.get("objectTypeName");
        CMDBuildUI.util.helper.ModelHelper.getModel(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
            objectTypeName
        ).then(function (model) {
            vm.set("isModelLoaded", true);

            // get columns definition
            var columns = CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                allowFilter: view.getAllowFilter()
            });

            columns.push(CMDBuildUI.util.helper.GridHelper.getProcessFlowStatusColumn());

            // hide selection column
            if (!view.isMultiSelectionEnabled() && view.selModel.column) {
                view.selModel.column.hide();
            }

            // reconfigure columns
            view.reconfigure(null, columns);
        });
    },

    /**
     * @param {Ext.selection.RowModel} element
     * @param {CMDBuildUI.model.classes.Card[]} record
     * @param {HTMLElement} rowIndex
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRowDblClick: function (element, record, rowIndex, e, eOpts) {
        var url = Ext.String.format(
            "processes/{0}/instances/{1}/activities/{2}/edit",
            record.getRecordType(),
            record.getRecordId(),
            record.get("_activity_id")
        );

        this.redirectTo(url, true);
        return false;
    },

    /**
     * @param {Ext.selection.RowModel} selection
     * @param {CMDBuildUI.model.classes.Card[]} selected
     * @param {Object} eOpts
     */
    onSelectionChange: function (selection, selected, eOpts) {
        var sel = selected.length ? selected[0] : null;
        var vm = this.getViewModel();
        if (
            this.getView().isMainGrid() &&
            sel &&
            sel.getId() != vm.get("selectedId")
        ) {
            var path = this.getRouteUrl(vm.get("objectTypeName"), sel.getId(), sel.get("_activity_id"));
            this.redirectTo(path);
            vm.set("selectedId", null);
            vm.set("selectedActivity", null);
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Numeric|String} newid
     * @param {Numeric|String} oldid
     */
    onSelectedIdChanged: function (view, newid, oldid) {
        var vm = this.getViewModel();
        var me = this;

        /**
         * 
         * @param {CMDBuildUI.store.classes.Cards} store 
         * @param {Ext.data.operation.Read} operation 
         * @param {Object} eOpts 
         */
        function onFirstBeforeLoad(store, operation, eOpts) {
            var extraparams = store.getProxy().getExtraParams();
            extraparams.positionOf = newid;
            extraparams.positionOf_goToPage = false;
        }

        // bind cards store to open selected card
        vm.bind({
            bindTo: '{instances}'
        }, function (store) {
            if (store && !store.isLoaded()) {
                store.on({
                    beforeload: {
                        fn: onFirstBeforeLoad,
                        scope: this,
                        single: true
                    },
                    load: {
                        fn: function (store, records, successful, operation, eOpts) {
                            me.afterLoadWithPosition(store, newid);
                        },
                        scope: this,
                        single: true
                    }
                });
            }
        });
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
        var searchTerm = vm.get("search").value;
        if (searchTerm) {
            CMDBuildUI.util.Ajax.setActionId("proc.inst.search");
            // add filter
            var store = vm.get("instances");
            store.getAdvancedFilter().addQueryFilter(searchTerm);
            store.load();
        } else {
            CMDBuildUI.util.Ajax.setActionId("proc.inst.clearsearch");
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
        var store = vm.get("instances");
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
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddBtnBeforeRender: function (button, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        this.getView().updateAddButton(
            button,
            function (item, event, eOpts) {
                me.onAddBtnClick(item, event, eOpts);
            },
            vm.get("objectTypeName"),
            vm.get("objectType")
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, eOpts) {
        CMDBuildUI.util.Ajax.setActionId("proc.inst.add");
        var url = 'processes/' + button.objectTypeName + '/instances/new';
        this.redirectTo(url, true);
    },

    /**
     * @param {CMDBuildUI.model.processes.Instance} instance
     */
    onProcessInstanceUpdated: function (instance) {
        var view = this.getView();
        var me = this;
        var store = view.getStore();
        if (instance) {
            var newid = instance.getId();
            // update extra params to get new card position
            var extraparams = store.getProxy().getExtraParams();
            extraparams.positionOf = newid;
            extraparams.positionOf_goToPage = false;
            // add event listener. Use event listener instaed of callback
            // otherwise the load listener used within afterLoadWithPosition
            // is called at first load.
            store.on({
                load: {
                    fn: function () {
                        me.afterLoadWithPosition(store, newid);
                    },
                    scope: this,
                    single: true
                }
            });
        }
        // load store
        store.load();
    },

    /**
     * On process instance aborted
     */
    onProcessInstanceAborted: function () {
        var store = this.getView().getStore();
        store.load();
    },

    /**
     * 
     * @param {CMDBuildUI.view.processes.instances.Grid} combo 
     * @param {Ext.event.Event} event 
     * @param {Object} eOpts 
     */
    onStatusComboBeforeRender: function (combo, event, eOpts) {
        var vm = combo.lookupViewModel();
        vm.bind({
            bindTo: {
                store: '{instances}',
                value: '{statuscombo.value}',
                field: '{statuscombo.field}'
            }
        }, this.onStatusComboChange);
    },

    /**
     * 
     * @param {Object} data 
     * @param {String} data.field
     * @param {Object} data.value
     * @param {Ext.data.Store} data.store
     */
    onStatusComboChange: function (data) {
        var basefilter;
        if (data.store) {
            // set status filter
            if (data.value && data.value !== "__ALL__") {
                basefilter = {
                    attribute: {
                        simple: {
                            attribute: data.field,
                            operator: 'equal',
                            value: [data.value]
                        }
                    }
                };
            } else if (data.value !== "__ALL__") {
                var record = this.getView().getOpenRunningStatusValue();
                if (record) {
                    basefilter = {
                        attribute: {
                            simple: {
                                attribute: CMDBuildUI.model.processes.Process.flowstatus.field,
                                operator: 'equal',
                                value: [record.getId()]
                            }
                        }
                    };
                }
            }
            if (basefilter) {
                data.store.getAdvancedFilter().addBaseFilter(basefilter);
            } else {
                data.store.getAdvancedFilter().clearBaseFilter();
            }
            data.store.load();
        }
    },

    /**
     * On status combo clear trigger
     * @param {Ext.form.field.ComboBox} combo 
     * @param {Ext.form.trigger.Trigger} trigger 
     * @param {Object} eOpts 
     */
    onStatusComboClear: function (combo, trigger, eOpts) {
        combo.setValue(null);
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Event} event 
     * @param {Object} eOpts 
     */
    onRefreshBtnClick: function (button, event, eOpts) {
        button.lookupViewModel().get("instances").reload();
    },

    /**
     * 
     * @param {Ext.menu.Item} menuitem 
     * @param {Ext.event.Event} event 
     * @param {Object} eOpts 
     */
    onPrintBtnClick: function (menuitem, event, eOpts) {
        var format = menuitem.printformat;
        var store = this.getViewModel().get("instances");
        var queryparams = {};

        // url and format
        var url = CMDBuildUI.util.api.Classes.getPrintCardsUrl(this.getViewModel().get("objectTypeName"), format);
        queryparams.extension = format;

        // visibile columns
        var columns = this.getView().getVisibleColumns();
        var attributes = [];
        columns.forEach(function (c) {
            if (c.dataIndex) {
                attributes.push(c.dataIndex);
            }
        });
        queryparams.attributes = Ext.JSON.encode(attributes);

        // apply sorters
        var sorters = store.getSorters().getRange();
        if (sorters.length) {
            queryparams.sort = store.getProxy().encodeSorters(sorters);
        }

        // filters
        var filter = store.getAdvancedFilter();
        if (!(filter.isEmpty() && filter.isBaseFilterEmpty())) {
            queryparams.filter = filter.encode();
        }

        // open file in popup
        CMDBuildUI.util.Utilities.openPrintPopup(url + "?" + Ext.Object.toQueryString(queryparams));
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onContextMenuBtnBeforeRender: function (button, eOpts) {
        this.getView().initContextMenu(button);
    },

    privates: {
        /**
         * @param {String} processName
         * @param {Numeric|String} instanceId
         * @param {String} activityId
         * @param {String} action
         * @return {String}
         */
        getRouteUrl: function (processName, instanceId, activityId, action) {
            var path = 'processes/' + processName + '/instances';
            if (instanceId) {
                path += '/' + instanceId;
                if (activityId) {
                    path += '/activities/' + activityId;
                }
            }
            if (action) {
                path += '/' + action;
            }
            return path;
        },

        /**
         * 
         * @param {Ext.data.Store} store 
         * @param {Numeric} newid 
         */
        afterLoadWithPosition: function (store, newid) {
            var view = this.getView();
            var vm = view.lookupViewModel();

            // function to expand row
            function expandRow() {
                view.expandRowAfterLoadWithPosition(store, newid);
                var extraparams = store.getProxy().getExtraParams();
                delete extraparams.positionOf;
                delete extraparams.positionOf_goToPage;
            }

            // check if item is found with filers
            var metadata = store.getProxy().getReader().metaData;
            if (metadata.positions[newid] && metadata.positions[newid].found) {
                expandRow();
            } else if (!CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.keepfilteronupdatedcard)) {
                var advancedFitler = store.getAdvancedFilter();
                // clear search query
                vm.set("search.value", "");
                advancedFitler.clearQueryFilter();
                // clear attributes and relations filter
                view.lookupReference("filterslauncher").clearFilter(true);
                // remove status filter
                vm.set("statuscombo.value", "__ALL__");
                // show message to user
                Ext.asap(function () {
                    CMDBuildUI.util.Notifier.showInfoMessage(CMDBuildUI.locales.Locales.common.grid.filterremoved);
                });
                // load store
                store.on({
                    load: {
                        fn: function () {
                            var meta = store.getProxy().getReader().metaData;
                            if (meta.positions[newid] && meta.positions[newid].found) {
                                expandRow();
                            } else {
                                // show not found message to user
                                Ext.asap(function () {
                                    CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.common.grid.itemnotfound);
                                });
                            }
                        },
                        scope: this,
                        single: true
                    }
                });
            } else {
                this.redirectTo(this.getRouteUrl(vm.get("objectTypeName")), false);
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
            }
        }
    }

});
