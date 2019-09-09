Ext.define('CMDBuildUI.view.classes.cards.grid.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-grid-grid',

    listen: {
        global: {
            cardcreated: 'onCardCreated',
            carddeleted: 'onCardDeleted',
            cardupdated: 'onCardUpdated'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            selectedidchanged: 'onSelectedIdChanged',
            selectionchange: 'onSelectionChange',
            reload: 'onReload',
            rowdblclick: 'onRowDblClick'
        },
        tableview: {
            select: 'onSelect'
        }
    },

    /**
     * Update grid on card update.
     * 
     * @param {CMDBuildUI.model.classes.Card} record 
     */
    onCardUpdated: function (record) {
        this.getView().updateRowWithExpader(record);
    },

    /**
     * Update grid on card creation.
     * 
     * @param {CMDBuildUI.model.classes.Card} record
     */
    onCardCreated: function (record) {
        var me = this;
        var view = this.getView();
        var store = view.getStore();
        var newid = record.getId();
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
        // load store
        store.load();
    },

    /**
     * Update grid of card deletion.
     */
    onCardDeleted: function () {
        var store = this.getView().getStore();
        store.load();
    },

    onReload: function (record, action, eOpts) {
        var currentPage;
        var view = this.getView();
        var store = view.getStore();
        var selection = view.getSelection();
        var proxy = store.getProxy();

        if (action === 'edit' || action === 'delete') {

            currentPage = Math.ceil(view.getSelectionModel().getSelection()[0].internalId / store.getConfig().pageSize);
            var selection = view.getSelectionModel().getSelection()[0];
            var index = view.store.indexOf(selection);
            view.getView().deselect(selection);


            view.suspendLayouts();
            store.load({
                params: {
                    limit: store.getConfig().pageSize,
                    page: 1,
                    start: 0
                },
                callback: function (records, operation, success) {

                    view.getView().refresh();
                    view.resumeLayouts();
                },
                scope: this
            });


        } else if (action === 'add') {
            view.suspendLayouts();
            view.getStore().loadPage(1, {
                callback: function (r, o) {
                    view.getView().refresh();


                    view.resumeLayouts();
                }
            });
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        var objectTypeName = view.getObjectTypeName();
        if (!objectTypeName) {
            objectTypeName = vm.get("objectTypeName");
        }

        // get grid columns
        CMDBuildUI.util.helper.ModelHelper.getModel("class", objectTypeName).then(function (model) {
            view.reconfigure(null, CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                allowFilter: view.getAllowFilter(),
                addTypeColumn: CMDBuildUI.util.helper.ModelHelper.getClassFromName(objectTypeName).get("prototype")
            }));

            // hide selection column
            if (!view.isMultiSelectionEnabled() && view.selModel.column) {
                view.selModel.column.hide();
            }
        });

    },

    /**
     * @param {Ext.selection.RowModel} element
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {HTMLElement} rowIndex
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRowDblClick: function (element, record, rowIndex, e, eOpts) {
        var url = Ext.String.format(
            "classes/{0}/cards/{1}/edit",
            record.getRecordType(),
            record.getRecordId()
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
        var view = this.getView();
        if (view.isMainGrid() && !view.isMultiSelectionEnabled()) {
            var selid;
            if (selected.length) {
                selid = selected[0].getId();
            }
            var path = this.getRouteUrl(this.getViewModel().get("objectTypeName"), selid);
            Ext.util.History.add(path);
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Numeric|String} newid
     * @param {Numeric|String} oldid
     */
    onSelectedIdChanged: function (view, newid, oldid) {
        var me = this;
        var vm = this.getViewModel();
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
            bindTo: '{cards}'
        }, function (cards) {
            if (!cards.isLoaded()) {
                cards.on({
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
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */

    onSelect: function (row, record, index, eOpts) {
        if (CMDBuildUI.util.helper.Configurations.get('cm_system_gis_enabled') == true) {
            CMDBuildUI.map.util.Util.setSelection(
                record.get('_id'),
                record.get('_type')
            );
        }
    },

    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // get value
        var searchTerm = vm.getData().search.value;
        if (searchTerm) {
            // add filter
            var store = vm.get("cards");
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
        var store = vm.get("cards");
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
     * @param {String} objectTypeName The name of the Class
     * @param {String} targetTypeDescription The description of the class
     */
    showAddCardFormPopup: function (objectTypeName, targetTypeDescription) {
        var vm = this.getViewModel();
        var me = this;
        CMDBuildUI.util.helper.ModelHelper.getModel('class', objectTypeName).then(function (model) {
            var panel;
            var title = Ext.String.format("New {0}", targetTypeDescription);
            var config = {
                xtype: 'classes-cards-card-create',
                viewModel: {
                    data: {
                        objectTypeName: objectTypeName
                    }
                },
                defaultValues: [{
                    value: objectTypeName,
                    editable: false
                }]
            };
            panel = CMDBuildUI.util.Utilities.openPopup('popup-add-class-form', title, config, null);
        }, function () {
        });
    },

    privates: {
        /**
         * @param {String} className
         * @param {Numeric} cardId
         * @param {String} action
         * @return {String}
         */
        getRouteUrl: function (className, cardId, action) {
            var path = 'classes/' + className + '/cards';
            if (cardId) {
                path += '/' + cardId;
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
            } else if (
                !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.keepfilteronupdatedcard) &&
                !store.getAdvancedFilter().isEmpty()
            ) {
                var advancedFitler = store.getAdvancedFilter();
                // clear search query
                vm.set("search.value", "");
                advancedFitler.clearQueryFilter();
                // clear attributes and relations filter
                var filterslauncher = view.up().lookupReference("filterslauncher");
                if (filterslauncher) {
                    filterslauncher.clearFilter(true);
                }
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
                store.load();
            } else {
                // show not found message to user
                Ext.asap(function () {
                    CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.common.grid.itemnotfound);
                });
            }
        }
    }
});
