Ext.define('CMDBuildUI.view.filters.attributes.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.filters-attributes-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addbutton': {
            click: 'onAddButtonClick'
        },
        '#removebutton': {
            click: 'onRemoveButtonClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.filters.attributes.Panel} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        view._fieldsetsreferences = [];
        var me = this;
        var vm = this.getViewModel();
        var fields = {};
        var store = vm.get("attributeslist");
        CMDBuildUI.util.helper.ModelHelper.getModel(vm.get("objectType"), vm.get("objectTypeName")).then(function (model) {
            // var fields = model.getFields();
            var items = [];
            model.getFields().forEach(function (field) {
                if (!Ext.String.startsWith(field.name, "_")) {
                    fields[field.name] = field;
                    items.push(Ext.create("CMDBuildUI.model.base.ComboItem", {
                        value: field.name,
                        label: field.attributeconf.description_localized
                    }));
                }
            });
            store.add(items);
            vm.set("allfields", fields);

            me._populateWithFilterData();
        });

        this.lookupReference("addattrfiltercontainer").add({
            xtype: 'filters-attributes-row',
            newRow: true,
            reference: 'newfiltercontainer',
            itemId: 'newfiltercontainer',
            allowInputParameter: view.getAllowInputParameter()
        });
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Event} e 
     * @param {Object} eOpts 
     */
    onAddButtonClick: function (button, e, eOpts) {
        // new row data
        var values = this.lookup("newfiltercontainer").getViewModel().get("values");
        // add filter
        this._addFilterRow(values);
        // clear new attriute fieldset
        this.clearNewFilterRow();
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Event} e 
     * @param {Object} eOpts 
     */
    onRemoveButtonClick: function (button, e, eOpts) {
        // get fieldset
        var fielset = button.up("fieldset");
        var fieldsetid = fielset.getReference();
        // destroy row
        var parent = button.up("filters-attributes-row");
        parent.destroy();
        // first child
        var firstrow = fielset.child("filters-attributes-row");
        if (firstrow) {
            firstrow.getViewModel().set("showLabels", true);
        } else {
            fielset.destroy();
            Ext.Array.remove(this.getView()._fieldsetsreferences, fieldsetid);
        }
    },

    privates: {
        clearNewFilterRow: function () {
            this.lookup("newfiltercontainer").getViewModel().set("values.attribute", null);
        },

        /**
         * 
         * @param {Object} values
         * @param {String} values.attribute
         * @param {String} values.operator
         * @param {Boolean} values.typeinput
         * @param {*} values.value1
         * @param {*} values.value2
         */
        _addFilterRow: function (values) {
            // get attribute fieldset
            var fieldsetid = "attributecontainer-" + values.attribute;
            var fieldset = this.lookup(fieldsetid);
            var showLabels = false;
            if (!fieldset) {
                var attr = this.getViewModel().get("allfields")[values.attribute];
                fieldset = Ext.create("Ext.form.FieldSet", {
                    reference: fieldsetid,
                    title: attr.attributeconf.description_localized || attr.description,
                    ui: 'formpagination',
                    collapsible: false,
                    viewModel: {
                        data: {
                            fielsetid: fieldsetid
                        }
                    }
                });
                this.lookup('attributescontainer').add(fieldset);
                showLabels = true;

                this.getView()._fieldsetsreferences.push(fieldsetid);
            }
            // add the row to fieldset
            fieldset.add({
                xtype: 'filters-attributes-row',
                allowInputParameter: this.getView().getAllowInputParameter(),
                viewModel: {
                    data: {
                        showLabels: showLabels,
                        values: Ext.clone(values)
                    }
                }
            });
        },

        _populateWithFilterData: function () {
            var me = this;
            var filter = this.getViewModel().get("theFilter");
            var config = filter.get("configuration");

            function addFilterRow(v) {
                me._addFilterRow({
                    attribute: v.attribute,
                    operator: v.operator,
                    typeinput: v.parameterType === CMDBuildUI.model.base.Filter.parametersypes.runtime,
                    value1: Ext.isArray(v.value) && v.value[0] || undefined,
                    value2: Ext.isArray(v.value) && v.value[1] || undefined
                }); 
            }
            if (config && config.attribute) {
                CMDBuildUI.view.filters.Launcher.analyzeAttributeRecursive(config.attribute,addFilterRow);
            }
        }
    }
});
