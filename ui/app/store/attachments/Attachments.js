Ext.define('CMDBuildUI.store.attachments.Attachments', {
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.attachments',

    model: 'CMDBuildUI.model.attachments.Attachment',

    sorters: ['created'],
    groupField: '_category_description',

    autoLoad: true,
    pageSize: 0 // disable pagination
});