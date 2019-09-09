Ext.define('CMDBuildUI.view.administration.components.attributesfilters.PanelController', {
    extend: 'CMDBuildUI.view.filters.attributes.PanelController',
    alias: 'controller.administration-filters-attributes-panel',
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
        view.setTitle(null);
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
        if (vm.get('actions.view')) {
            this.lookupReference('addattrfiltercontainer').hide();
        }

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
    privates: {
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
            var vm = this.getViewModel();
            var isEditMode = !vm.get('actions.view');

            var attributerowFieldset = isEditMode ? 'administration-filters-attributes-row' : 'administration-filters-attributes-rowdisplay';
            var isAdministrationModule = vm.get('isAdministrationModule');
            var fieldsetid = "attributecontainer-" + values.attribute;
            var fieldset = this.lookup(fieldsetid);
            var fieldsetui = isAdministrationModule ? 'administration-formpagination' : 'formpagination';
            var showLabels = false; //  isAdministrationModule ? true : false;
            if (!fieldset) {
                var attr = this.getViewModel().get("allfields")[values.attribute];
                fieldset = Ext.create("Ext.form.FieldSet", {
                    reference: fieldsetid,
                    title: attr.attributeconf.description_localized || attr.description,
                    ui: fieldsetui,
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
            if (!isEditMode) {
                // add the row to fieldset
                fieldset.add({
                    xtype: 'administration-filters-attributes-rowdisplay',
                    allowInputParameter: this.getView().getAllowInputParameter(),
                    viewModel: {
                        data: {
                            showLabels: showLabels,
                            values: Ext.clone(values)
                        }
                    }
                });
            } else {
                // add the row to fieldset
                fieldset.add({
                    xtype: 'administration-filters-attributes-row',
                    allowInputParameter: this.getView().getAllowInputParameter(),
                    viewModel: {
                        data: {
                            showLabels: showLabels,
                            values: Ext.clone(values)
                        }
                    }
                });
            }

        }
    }

});