Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.AttributesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-lookuptypes-tabitems-values-attributes',
    data: {
        theValue: {}
    },
    formulas: {
        getSelectedValue: {
            bind: '{theValue}',
            get: function (theValue) {
                if (theValue) {
                    return theValue;
                }
            }
        },
        allValuesProxy: {
            bind: '{theValue.name}',
            get: function (objectTypeName) {
                if (objectTypeName) {
                    return {
                        url: Ext.String.format("/lookup_types/{0}/values", CMDBuildUI.util.Utilities.stringToHex(objectTypeName)),
                        type: 'baseproxy'
                    };
                }
            }
        }
    },
    stores: {
        allValues: {
            model: "CMDBuildUI.model.lookups.Lookup",
            
            proxy: '{allValuesProxy}',
            autoLoad: true,
            autoDestroy: true,
            remoteSort: false, //true for server sorting
            sorters: [{
               property: 'number',
               direction: 'ASC' // or 'ASC'
             }]
        }

    }
});
