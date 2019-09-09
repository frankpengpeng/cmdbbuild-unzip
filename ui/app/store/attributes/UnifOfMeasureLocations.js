Ext.define('CMDBuildUI.store.attributes.UnifOfMeasureLocations', {
    extend: 'Ext.data.Store',
    alias: 'store.attributes-unitofmeasurelocation',
    
    requires: [
        'CMDBuildUI.model.base.ComboItem'
    ],

    model: 'CMDBuildUI.model.base.ComboItem',

    data: [{
        value: 'AFTER',
        label: 'After'
    },{
        value: 'BEFORE',
        label: 'Before'
    }],
    proxy: {
        type: 'memory'
    },
    pageSize: 0
});