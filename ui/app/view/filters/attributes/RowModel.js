Ext.define('CMDBuildUI.view.filters.attributes.RowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.filters-attributes-row',

    data: {
        showLabels: true,
        values: {
            attribute: null,
            operator: null,
            typeinput: null,
            value1: null,
            value2: null
        },
        hiddenfields: {
            attribute: true,
            operator: true,
            typeinput: true,
            value1: true,
            value2: true,
            addbutton: true,
            removebutton: true
        },
        flexes: {
            attribute: 1,
            operator: 0.7,
            typeinput: 0.5,
            values: 1.3,
            actions: 0.5
        },
        labels: {}
    },

    formulas: {
        updateProperties: {
            bind: {},
            get: function () {
                var isNewRow = this.getView().getNewRow();
                this.set("hiddenfields.attribute", !isNewRow);

                if (!isNewRow) {
                    this.set("flexes.attribute", 0);
                    this.set("flexes.operator", 1.7);
                }
            }
        },

        updateLabels: {
            bind: {
                showLabels: '{showLabels}'
            },
            get: function (data) {
                if (data.showLabels) {
                    this.set("labels.attribute", CMDBuildUI.locales.Locales.filters.attribute);
                    this.set("labels.operator", CMDBuildUI.locales.Locales.filters.operator);
                    this.set("labels.typeinput", CMDBuildUI.locales.Locales.filters.typeinput);
                    this.set("labels.value", CMDBuildUI.locales.Locales.filters.value);
                    this.set("labels.actions", "&nbsp;"); // insert empty label to align buttons with fields
                } else {
                    this.set("labels.attribute", null);
                    this.set("labels.operator", null);
                    this.set("labels.typeinput", null);
                    this.set("labels.value", null);
                    this.set("labels.actions", null);
                }
            }
        },

        operatorsFilter: {
            bind: {
                attribute: '{values.attribute}'
            },
            get: function (data) {
                if (data.attribute) {
                    var attribute = this.get("allfields")[data.attribute];
                    if (attribute) {
                        return [{
                            property: 'availablefor',
                            filterFn: function (item) {
                                return item.get("availablefor").indexOf(attribute.cmdbuildtype) !== -1;
                            }
                        }];
                    }
                }
            }
        }
    },

    stores: {
        operators: {
            source: '{operatorslist}',
            filters: '{operatorsFilter}'
        }
    }
});
