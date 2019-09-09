Ext.define('CMDBuildUI.view.views.items.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.views-items-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#searchtext': {
            specialkey: 'onSearchSpecialKey'
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
     * @param {CMDBuildUI.view.views.items.Grid} view
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        CMDBuildUI.util.helper.ModelHelper.getModel(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
            vm.get("objectTypeName")
        ).then(function (model) {
            // get columns definition
            var columns = CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                allowFilter: view.getAllowFilter()
            });
            columns.forEach(function (column) {
                column.hidden = false;
            });
            view.reconfigure(null, columns);
        });
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Event} event 
     * @param {Object} eOpts 
     */
    onRefreshBtnClick: function (button, event, eOpts) {
        button.lookupViewModel().get("items").reload();
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
        var store = this.getViewModel().get("items");
        var queryparams = {};

        // url and format
        var url = CMDBuildUI.util.api.Views.getPrintItemsUrl(this.getViewModel().get("objectTypeName"), format);
        queryparams.extension = format;

        // visibile columns
        var columns = view.getVisibleColumns();
        var attributes = [];
        columns.forEach(function (c) {
            if (c.attributename) {
                attributes.push(c.attributename);
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
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = field.lookupViewModel();
        // get value
        var searchTerm = vm.get("search.value");
        if (searchTerm) {
            // add filter
            var store = vm.get("items");
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
        var store = vm.get("items");
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