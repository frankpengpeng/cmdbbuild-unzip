Ext.define('CMDBuildUI.store.classes.Cards', {
    extend: 'Ext.data.BufferedStore',
    // extend: 'Ext.data.Store',

    requires: [
        
    ],

    alias: 'store.classes-cards',

    pageSize: 100,
    remoteFilter: true,
    remoteSort: true
});