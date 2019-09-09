Ext.define('CMDBuildUI.view.filters.attributes.Row', {
    extend: 'Ext.form.FieldContainer',

    requires: [
        'CMDBuildUI.view.filters.attributes.RowController',
        'CMDBuildUI.view.filters.attributes.RowModel'
    ],

    alias: 'widget.filters-attributes-row',
    controller: 'filters-attributes-row',
    viewModel: {
        type: 'filters-attributes-row'
    },

    config: {
        /**
         * @cfg {Boolean} newRow
         * When set to `true`, attribute field and add button will be shown.
         */
        newRow: false,

        /**
         * @cfg {Boolean} showLabels
         * When set to `false`, attribute field labels will be hide.
         */
        showLabels: true,

        /**
         * @cfg {Boolean} allowInputParameter
         */
        allowInputParameter: true
    },

    publishes: [
        'showLabels'
    ],

    twoWayBindable: [
        'showLabels'
    ],

    bind: {
        showLabels: '{showLabels}'
    },

    layout: 'hbox',
    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    defaults: {
        margin: 'auto 10 auto auto',
        layout: 'anchor'
    },

    items: [{
        xtype: 'fieldcontainer',
        hidden: true,
        bind: {
            flex: '{flexes.attribute}',
            hidden: '{hiddenfields.attribute}'
        },
        items: [{
            xtype: 'combobox',
            valueField: 'value',
            displayField: 'label',
            queryMode: 'local',
            forceSelection: true,
            reference: 'attributecombo',
            itemId: 'attributecombo',
            fieldLabel: CMDBuildUI.locales.Locales.filters.attribute,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.filters.attribute'
            },
            autoEl: {
                'data-testid': 'filters-attributes-row-attributecombo'
            },
            bind: {
                fieldLabel: '{labels.attribute}',
                store: '{attributeslist}',
                value: '{values.attribute}'
            }
        }]
    }, {
        xtype: 'fieldcontainer',
        bind: {
            flex: '{flexes.operator}'
        },
        items: [{
            xtype: 'combobox',
            valueField: 'value',
            displayField: 'label',
            queryMode: 'local',
            forceSelection: true,
            hidden: true,
            reference: 'operatorcombo',
            itemId: 'operatorcombo',
            autoEl: {
                'data-testid': 'filters-attributes-row-operatorcombo'
            },
            bind: {
                fieldLabel: '{labels.operator}',
                store: '{operators}',
                value: '{values.operator}',
                hidden: '{hiddenfields.operator}'
            }
        }]
    }, {
        xtype: 'fieldcontainer',
        bind: {
            flex: '{flexes.typeinput}'
        },
        items: [{
            xtype: 'checkboxfield',
            hidden: true,
            reference: 'typecheck',
            itemId: 'typecheck',
            fieldLabel: CMDBuildUI.locales.Locales.filters.typeinput,
            autoEl: {
                'data-testid': 'filters-attributes-row-typecheck'
            },
            bind: {
                fieldLabel: '{labels.typeinput}',
                value: '{values.typeinput}',
                hidden: '{hiddenfields.typeinput}'
            }
        }]
    }, {
        xtype: 'fieldcontainer',
        reference: 'valuescontainer',
        itemId: 'valuescontainer',
        bind: {
            flex: '{flexes.values}'
        }
    }, {
        xtype: 'fieldcontainer',
        reference: 'actionscontainer',
        itemId: 'actionscontainer',
        bind: {
            flex: '{flexes.actions}',
            fieldLabel: '{labels.actions}'
        },
        items: [{
            xtype: 'tbfill',
            flex: 1
        }, {
            xtype: 'button',
            iconCls: 'x-fa fa-plus',
            ui: 'management-action',
            reference: 'addbutton',
            itemId: 'addbutton',
            hidden: true,
            tooltip: CMDBuildUI.locales.Locales.filters.addfilter,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.filters.addfilter'
            },
            autoEl: {
                'data-testid': 'filters-attributes-row-addbutton'
            },
            bind: {
                hidden: '{hiddenfields.addbutton}'
            }
        }, {
            xtype: 'button',
            iconCls: 'x-fa fa-trash-o',
            ui: 'management-action',
            reference: 'removebutton',
            itemId: 'removebutton',
            hidden: true,
            tooltip: CMDBuildUI.locales.Locales.common.actions.remove,
            autoEl: {
                'data-testid': 'filters-attributes-row-removebutton'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.actions.remove'
            },
            bind: {
                hidden: '{hiddenfields.removebutton}'
            }
        }]
    }],

    /**
     * @return {Object} An object with filter data.
     */
    getRowData: function () {
        var vm = this.getViewModel();
        var data = {
            attribute: vm.get("values.attribute"),
            operator: vm.get("values.operator"),
            parameterType: vm.get("values.typeinput") ? CMDBuildUI.model.base.Filter.parametersypes.runtime : CMDBuildUI.model.base.Filter.parametersypes.fixed,
            value: []
        };
        if (
            data.parameterType === CMDBuildUI.model.base.Filter.parametersypes.fixed &&
            data.operator !== CMDBuildUI.model.base.Filter.operators.null &&
            data.operator !== CMDBuildUI.model.base.Filter.operators.notnull
        ) {
            var type = vm.get('allfields')[data.attribute].cmdbuildtype;
            if (vm.get('values.value1') == null && type == 'boolean') {
                data.value.push(false);
            } else {
                data.value.push(vm.get("values.value1"));
            }
            // add value2 when operator is `between`
            if (data.operator === CMDBuildUI.model.base.Filter.operators.between) {
                data.value.push(vm.get("values.value2"));
            }
        }
        return data;
    }
});