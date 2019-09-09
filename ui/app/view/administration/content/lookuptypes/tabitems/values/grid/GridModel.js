Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.grid.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-lookuptypes-tabitems-values-grid-grid',
    data: {
        selected: null
    },
    stores: {
        allValues: {
            model: "CMDBuildUI.model.lookups.Lookup",
            proxy: '{lookupValuesProxy}',
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0,
            sorters: [{
                property: 'index',
                direction: 'ASC'
            }]
        }
    }

});