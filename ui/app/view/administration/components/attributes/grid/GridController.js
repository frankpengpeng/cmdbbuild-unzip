Ext.define('CMDBuildUI.view.administration.components.attributes.grid.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-attributes-grid-grid',
    listen: {
        global: {
            attributeupdated: 'onAttributeUpdated',
            attributecreated: 'onAttributeCreated'
        }
    },

    control: {
        '#addattribute': {
            click: 'onNewBtnClick'
        },
        '#': {
            sortchange: 'onSortChange'
        },
        tableview: {
            deselect: 'onDeselect',
            select: 'onSelect',
            beforedrop: 'onBeforeDrop'
        }
    },

    /**
     * 
     * @param {Ext.grid.header.Container} ct 
     * @param {Ext.grid.column.Column} column 
     * @param {String} direction 
     * @param {Object} eopts 
     */
    onSortChange: function (ct, column, direction, eopts) {
        var grid = ct.grid;
        var selected = grid.getSelection()[0];
        if (selected) {
            var store = grid.getStore();
            var index = store.findExact("id", selected.getId());
            var storeItem = store.getById(selected.getId());
            var formInRowPlugin = grid.getPlugin('administration-forminrowwidget').view;
            // TODO: this is a workaround, find best method
            formInRowPlugin.fireEventArgs('togglerow', [null, storeItem, index]);
            formInRowPlugin.fireEventArgs('togglerow', [null, storeItem, index]);
        }

    },

    onBeforeDrop: function (node, data, overModel, dropPosition, dropHandlers) {
        // Defer the handling
        var vm = this.getViewModel();
        var view = this.getView();

        var filterCollection = vm.get("allAttributes").getFilters();
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

            var attributes = vm.get('allAttributes').getData().getIndices();
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
                    '{0}/{1}/{2}/attributes/order',
                    CMDBuildUI.util.Config.baseUrl,
                    Ext.util.Inflector.pluralize(vm.get('objectType')).toLowerCase(),
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

    /**
     * @event change
     * Fires when the value of a field is changed. The value of a field is 
     * checked for changes when the field's setValue method 
     * is called and when any of the events listed in 
     * checkChangeEvents are fired.
     * @param {Ext.form.field.Field} field
     * @param {Boolean} newValue The new value
     * @param {Boolean} oldValue The original value
     */
    onIncludeInheritedChange: function (field, newValue, oldValue) {
        var vm = this.getViewModel();
        // check if grid have selected row
        var grid = this.getView();
        var selected = grid.getSelection()[0];
        if (selected) {
            var store = grid.getStore();
            var index = store.findExact("id", selected.getId());
            var storeItem = store.getById(selected.getId());
            var formInRowPlugin = grid.getPlugin('administration-forminrowwidget').view;
            // TODO: this is a workaround, find best method
            formInRowPlugin.fireEventArgs('togglerow', [grid, storeItem, index]);
        }
        // get attributes filter
        var filterCollection = vm.get("allAttributes").getFilters();

        if (newValue === true) {
            // show all attributes
            filterCollection.removeByKey('inheritedFilter');
        } else {
            // remove all inherited attributes
            filterCollection.add({
                id: 'inheritedFilter',
                property: 'inherited',
                value: newValue
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
        // get value
        var searchTerm = vm.getData().search.value;
        var filterCollection = vm.get("allAttributes").getFilters();
        if (searchTerm) {
            filterCollection.add([{
                id: 'nameFilter',
                property: 'name',
                operator: 'like',
                value: searchTerm
            }, {
                id: 'descriptionFilter',
                property: 'description',
                operator: 'like',
                value: searchTerm
            }]);
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
        var filterCollection = vm.get("allAttributes").getFilters();
        filterCollection.removeByKey('nameFilter');
        filterCollection.removeByKey('descriptionFilter');

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
        var dragDropPlugin = row.view.getPlugin('gridviewdragdrop');
        var formInRowPlugin = row.view.grid.getPlugin('administration-forminrowwidget');
        if (formInRowPlugin &&
            !Ext.Object.isEmpty(formInRowPlugin.recordsExpanded)) {
            dragDropPlugin.disable();
        } else {
            dragDropPlugin.enable();
        }
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (item, event, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        container.add({
            xtype: 'administration-components-attributes-actionscontainers-create',
            viewModel: {
                data: {
                    objectTypeName: vm.get('objectTypeName'),
                    objectType: vm.get('objectType'),
                    attributes: this.getView().getStore().getRange(),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    grid: Ext.copy(view)

                }
            }
        });
    },

    /**
     * 
     * @param {CMDBuildUI.model.Attribute} record 
     */
    onAttributeUpdated: function (record) {
        var view = this.getView();
        var plugin = view.getPlugin('administration-forminrowwidget');
        if (plugin) {
            plugin.view.fireEventArgs('itemupdated', [view, record, this]);
        }
    },

    /**
     * 
     * @param {CMDBuildUI.model.Attribute} record 
     */
    onAttributeCreated: function (record) {
        var view = this.getView();
        var store = view.getStore();
        var index = store.findExact("_id", record.getId());
        view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemcreated', [view, record, index]);
    }
});