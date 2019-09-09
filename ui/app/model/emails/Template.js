Ext.define('CMDBuildUI.model.emails.Template', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
            name: 'name',
            type: 'string',
            critical: true,
            validators: ['presence']
        },
        {
            name: 'description',
            type: 'string',
            critical: true,
            validators: ['presence']
        },
        {
            name: 'from',
            type: 'string',
            critical: true
        },
        {
            name: 'to',
            type: 'string',
            critical: true
        },
        {
            name: 'cc',
            type: 'string',
            critical: true
        },
        {
            name: 'bcc',
            type: 'string',
            critical: true
        },
        {
            name: 'subject',
            type: 'string',
            critical: true
        },
        {
            name: 'body',
            type: 'string',
            critical: true
        },
        {
            name: 'account',
            type: 'string',
            critical: true
        },
        {
            name: 'keepSynchronization',
            type: 'boolean',
            critical: true
        },
        {
            name: 'promptSynchronization',
            type: 'boolean',
            critical: true
        },
        {
            name: 'delay',
            type: 'number',
            critical: true
        },
        {
            name: 'data',
            type: 'auto',
            critical: true
        },
        {
            name: 'contentType',
            type: 'string',
            defaultValue: 'text/html',
            critical: true
        }],

    proxy: {
        url: CMDBuildUI.util.api.Emails.getTemplatesUrl(),
        type: 'baseproxy'
    },

    clone: function () {
        var newTemplate = this.copy();
        newTemplate.set('_id', undefined);
        newTemplate.set('name', '');
        newTemplate.set('description', '');
        newTemplate.crudState = "C";
        newTemplate.phantom = true;
        delete newTemplate.crudStateWas;
        delete newTemplate.previousValues;
        delete newTemplate.modified;
        return newTemplate;
    }
});