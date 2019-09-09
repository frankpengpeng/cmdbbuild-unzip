Ext.define('CMDBuildUI.view.filters.attributes.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.filters-attributes-panel',

    data: {
        allfields: undefined
    },

    formulas: {
        operatorsdata: {
            get: function () {
                var f = CMDBuildUI.model.base.Filter;
                return [{
                    value: f.operators.equal,
                    label: f.getOperatorDescription(f.operators.equal),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                    ]
                }, {
                    value: f.operators.notequal,
                    label: f.getOperatorDescription(f.operators.notequal),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                    ]
                }, {
                    value: f.operators.null,
                    label: f.getOperatorDescription(f.operators.null),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                    ]
                }, {
                    value: f.operators.notnull,
                    label: f.getOperatorDescription(f.operators.notnull),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                    ]
                }, {
                    value: f.operators.greater,
                    label: f.getOperatorDescription(f.operators.greater),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                    ]
                }, {
                    value: f.operators.less,
                    label: f.getOperatorDescription(f.operators.less),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                    ]
                }, {
                    value: f.operators.between,
                    label: f.getOperatorDescription(f.operators.between),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                    ]
                }, {
                    value: f.operators.contain,
                    label: f.getOperatorDescription(f.operators.contain),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                    ]
                }, {
                    value: f.operators.notcontain,
                    label: f.getOperatorDescription(f.operators.notcontain),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                    ]
                }, {
                    value: f.operators.begin,
                    label: f.getOperatorDescription(f.operators.begin),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                    ]
                }, {
                    value: f.operators.notbegin,
                    label: f.getOperatorDescription(f.operators.notbegin),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                    ]
                }, {
                    value: f.operators.end,
                    label: f.getOperatorDescription(f.operators.end),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                    ]
                }, {
                    value: f.operators.notend,
                    label: f.getOperatorDescription(f.operators.notend),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                    ]
                }, {
                    value: f.operators.netcontains,
                    label: f.getOperatorDescription(f.operators.netcontains),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress
                    ]
                }, {
                    value: f.operators.netcontained,
                    label: f.getOperatorDescription(f.operators.netcontained),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress
                    ]
                }, {
                    value: f.operators.netcontainsorequal,
                    label: f.getOperatorDescription(f.operators.netcontainsorequal),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress
                    ]
                }, {
                    value: f.operators.netcontainedorequal,
                    label: f.getOperatorDescription(f.operators.netcontainedorequal),
                    availablefor: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress
                    ]
                }];
            }
        }
    },

    stores: {
        attributeslist: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            }
        },
        operatorslist: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            data: '{operatorsdata}'
        }
    }

});
