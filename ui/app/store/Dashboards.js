Ext.define('CMDBuildUI.store.Dashboards', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.Dashboard'
    ],

    alias: 'store.dashboards',

    model: 'CMDBuildUI.model.Dashboard',

    sorters: ['description'],
    pageSize: 0, // disable pagination
    autoLoad: false

});