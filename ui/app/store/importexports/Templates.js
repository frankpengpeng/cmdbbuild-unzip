Ext.define('CMDBuildUI.store.importexports.Templates', {
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.importexports-templates',

    model: 'CMDBuildUI.model.importexports.Template',

    proxy: {
        type: 'baseproxy',
        url: '/etl/templates',
        extraParams:{
            detailed:true
        }
    },
    
    autoLoad: true,
    autoDestroy: true,
    pageSize: 0 // disable pagination
});