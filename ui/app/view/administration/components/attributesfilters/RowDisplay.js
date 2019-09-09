Ext.define('CMDBuildUI.view.administration.components.attributesfilters.RowDisplay', {
    extend: 'CMDBuildUI.view.filters.attributes.Row',

    requires: [
        'CMDBuildUI.view.administration.components.attributesfilters.RowController',
        'CMDBuildUI.view.administration.components.attributesfilters.RowModel'
    ],
    controller: 'administration-filters-attributes-row',
    alias: 'widget.administration-filters-attributes-rowdisplay',
    viewModel: {
        type: 'administration-filters-attributes-row'
    },
    items: [{
        xtype: 'fieldcontainer',
        hidden: true,
        bind: {
            flex: '{flexes.attribute}'
            // hidden: '{hiddenfields.attribute}'
        },
        items: [{
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.filters.attribute,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.filters.attribute'
            },
            autoEl: {
                'data-testid': 'filters-attributes-row-attributecombo'
            },
            bind: {
                fieldLabel: '{labels.attribute}',
                value: '{values.attribute}'
            }
        }]
    }, {
        xtype: 'fieldcontainer',
        bind: {
            flex: '{flexes.operator}'
        },
        items: [{
            xtype: 'displayfield',
            hidden: true,

            autoEl: {
                'data-testid': 'filters-attributes-row-operatorcombo'
            },
            bind: {
                fieldLabel: '{labels.operator}',
                value: '{values.operator}',
                hidden: '{!values.operator}'
            },
            renderer: function (value, field) {
                var store = field
                    .up('filters-attributes-panel')
                    .getViewModel()
                    .getStore('operatorslist');
                if (store) {
                    var record = store.findRecord('value', value);
                    var label = record && record.get('label');
                    return label;
                }
                return value;
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
            disabled: true,
            disabledCls: '',
            fieldLabel: CMDBuildUI.locales.Locales.filters.typeinput,
            autoEl: {
                'data-testid': 'filters-attributes-row-typecheck'
            },
            bind: {
                fieldLabel: '{labels.typeinput}',
                value: '{values.typeinput}',
                hidden: '{!values.typeinput}'
            }
        }]
    }, {
        xtype: 'fieldcontainer',
        bind: {
            flex: '{flexes.values}'
        },
        items: [{
            xtype: 'displayfield',
            hidden: true,
            bind: {
                fieldLabel: '{labels.value}',
                value: '{values.value1}',
                hidden: '{!values.value1}'
            }
        }, {
            xtype: 'displayfield',
            hidden: true,
            bind: {
                fieldLabel: '{labels.value}',
                value: '{values.value2}',
                hidden: '{!values.value2}'
            }
        }]
    }]
});