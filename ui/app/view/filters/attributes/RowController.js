Ext.define('CMDBuildUI.view.filters.attributes.RowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.filters-attributes-row',

    control: {
        '#attributecombo': {
            change: 'onAttributeComboChange'
        },
        '#operatorcombo': {
            change: 'onOperatorComboChange'
        },
        '#typecheck': {
            change: 'onTypeCkeckChange'
        },
        '#addbutton': {
            beforerender: 'onAddButtonBeforeRender'
            // click event is managed on parent panel
        },
        '#removebutton': {
            beforerender: 'onRemoveButtonBeforeRender'
            // click event is managed on parent panel
        }
    },

    /**
     * Executed when changes the value of Attribute combobox.
     * 
     * @param {Ext.form.field.ComboBox} combo 
     * @param {String} newValue 
     * @param {String} oldValue 
     * @param {Object} eOpts 
     */
    onAttributeComboChange: function (combo, newValue, oldValue, eOpts) {
        var vm = this.getViewModel();

        this.updateOperatorFieldVisibility();
        this.updateButtonsVisibility();

        // clear values
        if (this.getView().getNewRow()) {
            vm.set("values.operator", null);
            vm.set("values.value1", null);
            vm.set("values.value2", null);
            vm.set("values.typeinput", null);
        }

        // add values fields
        this.addValuesFields(newValue);
    },

    /**
     * Executed when changes the value of Operator combobox.
     * 
     * @param {Ext.form.field.ComboBox} combo 
     * @param {String} newValue 
     * @param {String} oldValue 
     * @param {Object} eOpts 
     */
    onOperatorComboChange: function (combo, newValue, oldValue, eOpts) {
        this.updateTypeFieldVisibility();
        this.updateValue1FieldVisibility();
        this.updateValue2FieldVisibility();
    },

    /**
     * Executed when changes the value of Operator combobox.
     * 
     * @param {Ext.form.field.Checkbox} check 
     * @param {Boolean} newValue 
     * @param {Boolean} oldValue 
     * @param {Object} eOpts 
     */
    onTypeCkeckChange: function (check, newValue, oldValue, eOpts) {
        this.updateValue1FieldVisibility();
        this.updateValue2FieldVisibility();
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onAddButtonBeforeRender: function (button, eOpts) {
        this.applyUiIfAdmin(button);
        this.updateButtonsVisibility();
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onRemoveButtonBeforeRender: function (button, eOpts) {
        this.applyUiIfAdmin(button);
        this.updateButtonsVisibility();
    },

    privates: {
        /**
         * Add a container with value fields.
         * @param {String} attributename 
         */
        addValuesFields: function (attributename) {
            var container = this.lookupReference('valuescontainer');
            var vm = this.getViewModel();
            // empty container
            container.removeAll(true);

            var allattributes = this.getViewModel().get("allfields");
            if (attributename && allattributes[attributename]) {
                var attribute = allattributes[attributename];
                if (attribute.type == 'date' && vm.get('values.value1')) {
                    vm.set('values.value1', new Date(vm.get('values.value1')));
                    if (vm.get('values.value2')) {
                        vm.set('values.value2', new Date(vm.get('values.value2')));
                    }
                }
                var editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(
                    attribute
                );
                container.add([
                    Ext.apply({
                        hidden: true,
                        bind: {
                            fieldLabel: '{labels.value}',
                            value: '{values.value1}',
                            hidden: '{hiddenfields.value1}'
                        }
                    }, editor),
                    Ext.apply({
                        hidden: true,
                        bind: {
                            value: '{values.value2}',
                            hidden: '{hiddenfields.value2}'
                        }
                    }, editor)
                ]);
            }
        },

        /**
         * @param {Ext.button.Button} button 
         */
        applyUiIfAdmin: function (button) {
            var vm = button.lookupViewModel();
            if (vm && vm.get('isAdministrationModule')) {
                button.ui = 'administration-action';
            }
        },

        updateOperatorFieldVisibility: function () {
            var vm = this.getViewModel();
            var isHidden = true;
            var attribute = vm.get("values.attribute");
            if (attribute) {
                isHidden = false;
            }
            vm.set("hiddenfields.operator", isHidden);
        },

        updateTypeFieldVisibility: function () {
            var vm = this.getViewModel();
            var isHidden = true;
            var allowInputParameter = this.getView().getAllowInputParameter();
            var operator = vm.get("values.operator");
            if (
                allowInputParameter &&
                operator &&
                operator !== CMDBuildUI.model.base.Filter.operators.null &&
                operator !== CMDBuildUI.model.base.Filter.operators.notnull
            ) {
                isHidden = false;
            }
            vm.set("hiddenfields.typeinput", isHidden);
        },

        updateValue1FieldVisibility: function () {
            var vm = this.getViewModel();
            var isHidden = true;
            var operator = vm.get("values.operator");
            var typeinput = vm.get("values.typeinput");
            if (
                operator &&
                operator !== CMDBuildUI.model.base.Filter.operators.null &&
                operator !== CMDBuildUI.model.base.Filter.operators.notnull &&
                !typeinput
            ) {
                isHidden = false;
            }
            vm.set("hiddenfields.value1", isHidden);
        },

        updateValue2FieldVisibility: function () {
            var vm = this.getViewModel();
            var isHidden = true;
            var operator = vm.get("values.operator");
            var typeinput = vm.get("values.typeinput");
            if (
                operator &&
                operator === CMDBuildUI.model.base.Filter.operators.between &&
                !typeinput
            ) {
                isHidden = false;
            }
            vm.set("hiddenfields.value2", isHidden);
        },

        updateButtonsVisibility: function () {
            var hideadd = false,
                hideremove = false;
            var vm = this.getViewModel();
            if (!vm.get("values.attribute")) {
                hideadd = true;
                hideremove = true;
            } else if (this.getView().getNewRow()) {
                hideremove = true;
            } else if (!this.getView().getNewRow()) {
                hideadd = true;
            }
            vm.set("hiddenfields.addbutton", hideadd);
            vm.set("hiddenfields.removebutton", hideremove);
        }
    }
});