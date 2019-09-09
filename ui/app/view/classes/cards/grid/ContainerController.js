Ext.define('CMDBuildUI.view.classes.cards.grid.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-grid-container',

    control: {
        "#": {
            beforerender: "onBeforeRender"
        },
        '#addcard': {
            beforerender: 'onAddCardButtonBeforeRender'
        },
        '#contextMenuBtn': {
            beforerender: 'onContextMenuBtnBeforeRender'
        },
        '#refreshBtn': {
            click: 'onRefreshBtnClick'
        },
        '#printPdfBtn': {
            click: 'onPrintBtnClick'
        },
        '#printCsvBtn': {
            click: 'onPrintBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.grid.Container} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        CMDBuildUI.util.helper.ModelHelper.getModel("class", view.getObjectTypeName()).then(function (model) {
            vm.set("objectTypeName", view.getObjectTypeName());
            if (vm.getParent().get('activeView') == 'grid-list') {
                
                //Reset position variables related with the olMap
                vm.getParent().set('actualZoom', null);
                vm.getParent().set('bbox', null);

                view.add([{
                    itemId: view.referenceGridId,
                    reference: view.referenceGridId,
                    xtype: 'classes-cards-grid-grid',
                    maingrid: view.isMainGrid(),
                    selModel: {
                        pruneRemoved: false, // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                        selType: 'checkboxmodel',
                        checkOnly: true,
                        mode: 'SINGLE'
                    }
                }]);

            } else if (vm.getParent().get('activeView') == 'map') {
                view.add([{
                    itemId: view.referenceMapId,
                    reference: view.referenceMapId,
                    xtype: 'map-container'
                }]);
            }
        });

    },

    addMapView: function () {
        this.view.add({
            itemId: 'map-container-view',
            reference: 'map-container-view',
            xtype: 'map-container'
        });

        //this.MapView = this.view.getReferences()['map-container-view'];
        //this.onShowMapListButtonClick();
    },

    addGridView: function () {
        this.view.add([
            {
                itemId: 'classes-cards-grid-grid-view',
                reference: 'classes-cards-grid-grid-view',
                xtype: 'classes-cards-grid-grid'
            }]);

        //this.GridView = this.view.getReferences()['classes-cards-grid-grid-view'];
        //this.onShowMapListButtonClick();
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
    onShowMapListButtonClick: function (event, eOpts) { //TODO: Implement Here\
        var vm = this.getViewModel();
        var view = this.getView();
        var activeView = vm.get('activeView');

        /*    if (!this.lookupReference(referenceGridId) || !this.lookupReference(referenceMapId)) {    //workaround for bug
               this.settingUp();
           } */

        if (activeView == 'grid-list') {
            if (!this.lookupReference(view.referenceMapId)) {
                this.addMapView();
            }
            this.lookupReference(view.referenceGridId).hide();
            this.lookupReference(view.referenceMapId).show();
            vm.getParent().set('activeView', 'map');

        } else if (activeView == 'map') {
            if (!this.lookupReference(view.referenceGridId)) {
                this.addGridView();
            }
            this.lookupReference(view.referenceMapId).hide();
            this.lookupReference(view.referenceGridId).show();
            vm.getParent().set('activeView', 'grid-list');
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddCardButtonBeforeRender: function (button, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        var view = this.getView();
        view.updateAddButton(
            button,
            function (item, event, eOpts) {
                me.onNewBtnClick(item, event, eOpts);
            },
            view.getObjectTypeName(),
            vm.get("objectType")
        );
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (item, event, eOpts) {
        if (this.getView().isMainGrid()) {
            CMDBuildUI.util.helper.SessionHelper.setItem('activeCardIndex', 0);
            var url = 'classes/' + item.objectTypeName + '/cards/new';
            this.redirectTo(url, true);
        } else {
            this.showAddCardFormPopup(item.objectTypeName, item.text);
        }
    },

    /**
     * 
     * @param {String} objectTypeName The name of the Class
     * @param {String} targetTypeDescription The description of the class
     */
    showAddCardFormPopup: function (objectTypeName, targetTypeDescription) {
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
            Ext.Msg.alert('Error', 'Class non found!');
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onContextMenuBtnBeforeRender: function (button, eOpts) {
        this.getView().initContextMenu(button);
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Event} event 
     * @param {Object} eOpts 
     */
    onRefreshBtnClick: function (button, event, eOpts) {
        button.lookupViewModel().get("cards").reload();
    },

    /**
     * 
     * @param {Ext.menu.Item} menuitem 
     * @param {Ext.event.Event} event 
     * @param {Object} eOpts 
     */
    onPrintBtnClick: function (menuitem, event, eOpts) {
        var format = menuitem.printformat;
        var view = this.getView();
        var store = this.getViewModel().get("cards");
        var queryparams = {};

        // url and format
        var url = CMDBuildUI.util.api.Classes.getPrintCardsUrl(this.getViewModel().get("objectTypeName"), format);
        queryparams.extension = format;

        // visibile columns
        var columns = view.lookupReference(view.referenceGridId).getVisibleColumns();
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
    }

});
