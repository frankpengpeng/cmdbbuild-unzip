Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.grid.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabitems-values-grid-grid',
    listen: {
        global: {
            lookupvalueupdated: 'onLookupValueUpdated',
            lookupvaluecreated: 'onAttributeCreated'
        }
    },

    control: {
        '#addlookupvalue': {
            click: 'onNewBtnClick'
        },
        tableview: {
            deselect: 'onDeselect',
            select: 'onSelect',
            beforedrop: 'onBeforeDrop'
        }
    },
    onBeforeDrop: function (node, data, overModel, dropPosition, dropHandlers) {
        // Defer the handling
        var vm = this.getViewModel();
        var view = this.getView();

        var filterCollection = vm.get("allValues").getFilters();
        view.getView().mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
        dropHandlers.wait = true;
        // by default allAttributes store have one filter for hide notes and idTenant attributes 
        if (filterCollection.length > 1) {
            var w = Ext.create('Ext.window.Toast', {
                ui: 'administration',
                width: 250,
                title: CMDBuildUI.locales.Locales.administration.common.messages.attention,
                html: CMDBuildUI.locales.Locales.administration.common.messages.cannotsortitems,
                iconCls: 'x-fa fa-exclamation-circle',
                align: 'br'
            });
            w.show();
            dropHandlers.cancelDrop();
            view.getView().unmask();

        } else {

            var moved = data.records[0].getId();
            var reference = overModel.getId();

            var attributes = vm.get('allValues').getData().getIndices();
            var sortableAttributes = [];
            for (var key in attributes) {
                if (attributes.hasOwnProperty(key)) {
                    sortableAttributes.push([key, attributes[key]]); // each item is an array in format [key, value]
                }
            }

            // sort items by value
            sortableAttributes.sort(function (a, b) {
                return a[1] - b[1]; // compare numbers
            });

            var jsonData = [];
            Ext.Array.forEach(sortableAttributes, function (val, key) {
                if (moved !== val[0]) {
                    if (dropPosition === 'before' && reference === val[0]) {
                        jsonData.push(moved);
                    }
                    jsonData.push(val[0]);
                    if (dropPosition === 'after' && reference === val[0]) {
                        jsonData.push(moved);
                    }
                }
            });

            Ext.Ajax.request({
                url: Ext.String.format(
                    '{0}/lookup_types/{1}/values/order',
                    CMDBuildUI.util.Config.baseUrl,
                    vm.get('objectTypeName')
                ),
                method: 'POST',
                jsonData: jsonData,
                success: function (response) {
                    var res = JSON.parse(response.responseText);

                    if (res.success) {
                        view.getView().grid.getStore().load();
                        dropHandlers.processDrop();
                    } else {
                        dropHandlers.cancelDrop();
                    }
                    view.getView().unmask();
                },
                error: function (response) {
                    dropHandlers.cancelDrop();
                    view.getView().unmask();
                }
            });
        }

    },

    onIncludeInheritedChange: function (field, newValue, oldValue) {
        var vm = this.getViewModel();

        var filterCollection = vm.get("allValues").getFilters();

        if (newValue === true) {
            filterCollection.removeByKey('inheritedFilter');
        } else {
            filterCollection.add({
                id: 'inheritedFilter',
                property: 'inherited',
                value: !newValue
            });

        }
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        var searchTerm = field.getValue();
        if (searchTerm) {
            vm.get("allValues").clearFilter();
            CMDBuildUI.util.administration.helper.GridHelper.searchMoreFields(vm.get("allValues"), searchTerm);           
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
        vm.get("allValues").clearFilter();
        // reset input
        field.reset();
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onDeselect: function (row, record, index, eOpts) {
        this.getView().getView().getPlugins()[0].enable();
    },

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onSelect: function (row, record, index, eOpts) {
        new Ext.util.DelayedTask(function () { }).delay(
            200,
            function (row, record, index, eOpts) {
                var vm = row.view.lookupViewModel();
                vm.set('theValue', record);
                var dragDropPlugin = row.view.getPlugin('gridviewdragdrop');
                var formInRowPlugin =  row.view.grid.getPlugin('administration-forminrowwidget');
                if (formInRowPlugin && !Ext.Object.isEmpty(formInRowPlugin.recordsExpanded)) {
                    dragDropPlugin.disable();
                } else {
                    dragDropPlugin.enable();
                }
            },
            this,
            arguments);
    },

    onAddAttributeButtonBeforeRender: function (button) {

    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (item, event, eOpts) {

        var view = this.getView();
        var vm = this.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        var objecttype = vm.getParent().getParent().get('objectTypeName');

        container.add({
            xtype: 'administration-content-lookuptypes-tabitems-values-card-create',
            viewModel: {
                data: {
                    lookupTypeName: objecttype,
                    values: view.getStore().getRange(),
                    title: objecttype + ' - ' + 'Value',
                    grid: view
                }
            }
        });
    },

    onLookupValueUpdated: function (record) {
        var view = this.getView();
        view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [view, record, this, true]);
    },

    onAttributeCreated: function (record) {
        var view = this.getView();
        var store = view.getStore();        
        var index = store.findExact("_id", record.getId());
        view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemcreated', [view, record, index]);
    }
});