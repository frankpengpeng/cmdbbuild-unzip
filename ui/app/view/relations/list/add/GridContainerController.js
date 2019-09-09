Ext.define('CMDBuildUI.view.relations.list.add.GridContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-add-gridcontainer',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        'grid': {
            selectionchange: 'onGridSelectionChange'
        },
        'tableview': {
            selectioncheckrendered: 'onSelectionCheckRendered'
        },
        '#savebutton': {
            click: 'onSaveButtonClick'
        },
        '#cancelbutton': {
            click: 'onCancelButtonClick'
        }
    },

    onBeforeRender: function (view, e, c) {
        var me = this, vm = view.lookupViewModel();
        var mode = view.getMultiSelect() ? "MULTI" : "SINGLE";
        view.lookupReference("gridcontainer").add({
            xtype: 'relations-list-add-grid',
            selModel: {
                type: 'cmdbuildcheckboxmodel',
                pruneRemoved: false,
                checkOnly: true,
                allowDeselect: true,
                mode: mode
            },
            viewModel: {
                data: {
                    originId: view.getOriginId(),
                    storeinfo: {
                        type: null,
                        proxyurl: null,
                        autoload: false,
                        ecqlfilter: null
                    }
                    // selection: view.getSelection()
                }
            }
        });
        vm.bind({
            bindTo: {
                domain: '{theDomain}'
            }
        }, function (data) {
            data.domain.getAttributes().then(function (attributes) {
                if (attributes.getTotalCount()) {
                    me.addAttributesPanel(attributes, view.lookupReference("attributesform"));
                } else {
                    vm.set("attributesvalid", true);
                }
            });
        });

        vm.bind({
            theRelation: "{theRelation}"
        }, function(data) {
            if (data.theRelation) {
                for (var k in data.theRelation.getData()) {
                    if (!Ext.String.startsWith(k, "_")) {
                        vm.set("values." + k, data.theRelation.get(k));
                    }
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveButtonClick: function (button, e, eOpts) {
        var me = this;
        var selection = this.getView().down("grid").getSelection();
        var view = me.getView();
        var vm = button.lookupViewModel();

        if (vm.get("theRelation")) {
            var sel = selection[0];
            // edit current relation
            var relation = vm.get("theRelation");
            relation.set("_destinationId", sel.get("_id"));
            relation.set("_destinationType", sel.get("_type"));

            if (vm.get("values")) {
                var values = vm.get("values");
                for (var k in values) {
                    relation.set(k, values[k]);
                }
            }
            relation.save({
                success: function () {
                    Ext.callback(view.onSaveSuccess);
                    view.fireEvent("popupclose");
                }
            });
        } else {
            // create relations
            var _is_direct = vm.get("relationDirection") === 'direct';
            var store = Ext.create(Ext.data.Store, {
                model: 'CMDBuildUI.model.domains.Relation',
                autoDestroy: true,
                proxy: {
                    type: 'baseproxy',
                    url: Ext.String.format('/classes/{0}/cards/{1}/relations', view.getOriginTypeName(), view.getOriginId())
                }
            });
    
            for (var i = 0; i < selection.length; i++) {
                store.add(Ext.merge({
                    _type: view.getViewModel().get("theDomain").get("name"),
                    _destinationId: selection[i].get("_id"),
                    _destinationType: selection[i].get("_type"),
                    _sourceType: view.getOriginTypeName(),
                    _sourceId: view.getOriginId(),
                    _is_direct: _is_direct
                }, vm.get("values")));
            }
    
            store.sync({
                success: function (batch, options) {
                    Ext.callback(view.onSaveSuccess);
                    view.fireEvent("popupclose");
                }
            });
        }
    },

    /**
     * @param {String|Numeric} value
     * @param {Object} cell
     * @param {Ext.data.Model} record
     * @param {Numeric} rowIndex
     * @param {Numeric} colIndex
     * @param {Ext.data.Store} store
     * @param {Ext.grid.column.Check} check
     * @param {Ext.view.Table}
     */
    onSelectionCheckRendered: function (value, cell, record, rowIndex, colIndex, store, check, tableview) {
        var vm = tableview.lookupViewModel();
        var domainName = vm.get("theDomain").get("name");

        if (!record.get("_" + domainName + "_available")) {
            // disable cell
            cell.tdCls = CMDBuildUI.view.relations.list.add.Grid.disabledcls;
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelButtonClick: function (button, e, eOpts) {
        this.getView().fireEvent("popupclose");
    },
    /**
     * @param {Ext.selection.RowModel} selection
     * @param {CMDBuildUI.model.classes.Card[]} selected
     * @param {Object} eOpts
     */
    onGridSelectionChange: function (selection, selected, eOpts) {
        this.getViewModel().set("relselection", selected);
    },

    privates: {
        /**
         * 
         * @param {Ext.data.Store} attributes
         * @param {Ext.form.Panel} form
         */
        addAttributesPanel: function (attributes, form) {
            var fields = [], requiredattributes = [];
            // generate form fields from attributes list
            attributes.getRange().forEach(function (attr) {
                var field = CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attr);
                // replace field name with attribute name
                if (field){
                    field.name = attr.get('name');
                    var config = {
                        mode: field.mode,
                        linkName: null
                    };
                    var item = CMDBuildUI.util.helper.FormHelper.getFormField(field, config);
                    item.bind.value = '{values.' + field.name + '}';
                    fields.push(item);
    
                    // add controls for mandatory fields
                    if(field.mandatory && !field.hidden) {
                        requiredattributes.push(attr.get('name'));
                    }
                }

            });

            var left = [], right = [], counter = 0;
            tabindex = 0;
            fields.forEach(function (f, fi) {
                if (!f.hidden) {
                    f.tabIndex = tabindex++;
                    if (counter % 2) {
                        right.push(f);
                    } else {
                        left.push(f);
                    }
                    counter++;
                }
            });
            form.add([{
                items: left
            }, {
                items: right
            }]);
            form.setHidden(false);

            form.lookupViewModel().bind({
                bindTo: '{values}',
                deep: true
            }, function(values) {
                if (Object.keys(values)) {
                    var isValid = form.isValid();
                    requiredattributes.forEach(function(a) {
                        if (Ext.isEmpty(values[a])) {
                            isValid = false;
                        }
                    });
                    this.set("attributesvalid", isValid);
                } else {
                    this.set("attributesvalid", false);
                }
            });
        }
    }

});
