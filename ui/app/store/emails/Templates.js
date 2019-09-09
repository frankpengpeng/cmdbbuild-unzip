Ext.define('CMDBuildUI.store.emails.Templates', {
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.templates',

    model: 'CMDBuildUI.model.emails.Template',

    sorters: ['name'],
    autoLoad: false,
    autoDestroy: true,
    pageSize: 0 // disable pagination
});