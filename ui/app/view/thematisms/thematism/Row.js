
Ext.define('CMDBuildUI.view.thematisms.thematism.Row', {
    extend: 'CMDBuildUI.components.tab.FieldSet',

    requires: [
        'CMDBuildUI.view.thematisms.thematism.RowController',
        'CMDBuildUI.view.thematisms.thematism.RowModel'
    ],

    alias: 'widget.thematisms-thematism-row',
    controller: 'thematisms-thematism-row',
    viewModel: {
        type: 'thematisms-thematism-row'
    },

    title: CMDBuildUI.locales.Locales.thematism.defineThematism,
    collapsible: true,

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    defaults: {
        xtype: 'fieldcontainer',
        layout: 'column',
        defaults: {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            flex: '0.5',
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            layout: 'anchor'
        }
    },

    items: [{
        items: [{
            items: [{
                xtype: 'textfield',
                name: 'name',
                fieldLabel: CMDBuildUI.locales.Locales.thematism.name,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.thematism.name'
                },
                bind: {
                    value: '{values.name}'
                }
            }]
        }]
    }, {
        items: [{
            items: [{
                xtype: 'combobox',
                fieldLabel: CMDBuildUI.locales.Locales.thematism.analysisType,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.thematism.analysisType'
                },
                valueField: 'value',
                displayField: 'label',
                allowBlank: false,
                tabIndex: 1,
                bind: {
                    store: '{analysistypes}',
                    value: '{values.analysistype}'
                }
                // value: CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual
            }]
        }, {
            items: [{
                xtype: 'combobox',
                itemId: 'layerCombo',
                fieldLabel: CMDBuildUI.locales.Locales.thematism.geoAttribute,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.thematism.geoAttribute'
                },
                valueField: 'value',
                displayField: 'label',
                allowBlank: false,
                tabIndex: 2,
                bind: {
                    store: '{geoAttributes}',
                    value: '{values.geoattribute}'
                }
            }]
        }]
    }, {
        items: [{
            items: [{
                xtype: 'combobox',
                itemId: 'sourceCombo',
                fieldLabel: CMDBuildUI.locales.Locales.thematism.source,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.thematism.source'
                },
                valueField: 'value',
                displayField: 'label',
                allowBlank: false,
                tabIndex: 3,
                bind: {
                    store: '{sources}',
                    value: '{values.source}'
                }
                // value: CMDBuildUI.model.thematisms.Thematism.sources.table
            }]
        }, {
            items: [{
                xtype: 'combobox',
                itemId: 'attributeCombo',
                fieldLabel: CMDBuildUI.locales.Locales.thematism.attribute,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.thematism.attribute'
                },
                hidden: true,
                valueField: 'value',
                displayField: 'label',
                tabIndex: 4,
                bind: {
                    hidden: '{hiddenfields.attributecombo}',
                    store: '{attributesstore}',
                    value: '{values.attributecombo}'
                }
            }, {
                xtype: 'combobox',
                itemId: 'functionCombo',
                fieldLabel: CMDBuildUI.locales.Locales.thematism.function,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.thematism.function'
                },
                hidden: true,
                valueField: 'value',
                displayField: 'label',
                tabIndex: 4,
                bind: {
                    hidden: '{hiddenfields.functioncombo}',
                    store: '{functionstore}',
                    value: '{values.functioncombo}'
                }
            }]
        }]
    }],

    /**
     * @returns {Object} the fields contained in the form
     */
    getFields: function () {
        var vm = this.getViewModel();

        return {
            name: vm.get('values.name'),
            description: vm.get('values.name'),
            analysistype: vm.get('values.analysistype'),
            attribute: vm.get('values.geoattribute'),
            type: vm.get('values.source'),
            function: vm.get('values.functioncombo'),
            _targetClassAttribute: vm.get('values.attributecombo')
        };
    }
    //NOTE: Puo essere possibile bindare theThematism.name, theThematism.description ....
});
