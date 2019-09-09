Ext.define('CMDBuildUI.store.administration.common.AttachmentDescriptionMode', {
    extend: 'CMDBuildUI.store.Base',
    requires: ['CMDBuildUI.model.base.ComboItem'],
    model: 'CMDBuildUI.model.base.ComboItem',
    alias: 'store.common-attachmentdescriptionmode',
    fields: ['value', 'label'],
    sorters: ['label']
});