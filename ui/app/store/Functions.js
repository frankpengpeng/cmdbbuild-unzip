Ext.define('CMDBuildUI.store.Functions', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.Function'
    ],

    alias: 'store.functions',

    model: 'CMDBuildUI.model.Function',

    sorters: ['description'],
    pageSize: 0, // disable pagination
    autoLoad: false

});