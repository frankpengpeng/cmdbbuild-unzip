Ext.define('CMDBuildUI.model.attachments.Attachment', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        descriptionmodes: {
            hidden: 'hidden',
            optional: 'optional',
            mandatory: 'mandatory'
        }
    },

    fields: [{
        name: 'name',
        type: 'string'
    }, {
        name: 'author',
        type: 'string'
    }, {
        name: 'created',
        type: 'date'
    }, {
        name: 'category',
        type: 'integer'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'version',
        type: 'string'
    }, {
        name: 'modified',
        type: 'date'
    }, {
        name: 'file'
    }],

    proxy: {
        type: 'baseproxy'
    }
});