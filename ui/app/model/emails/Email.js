Ext.define('CMDBuildUI.model.emails.Email', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        statuses: {
            draft: 'draft',
            outgoing: 'outgoing',
            received: 'received',
            sent: 'sent'
        }
    },

    fields: [{
            name: 'keepSynchronization',
            type: 'boolean',
            critical: true
        },
        {
            name: 'account',
            type: 'string',
            critical: true
        },
        {
            name: 'bcc',
            type: 'string',
            critical: true
        },
        {
            name: 'body',
            type: 'string',
            critical: true
        },
        {
            name: 'cc',
            type: 'string',
            critical: true
        },
        {
            name: 'date',
            type: 'date',
            critical: true
        },
        {
            name: 'delay',
            type: 'number',
            critical: true
        },
        {
            name: 'from',
            type: 'string',
            critical: true
        },
        {
            name: 'noSubjectPrefix',
            type: 'boolean',
            critical: true
        },
        {
            name: 'notifyWith',
            type: 'string',
            critical: true
        },
        {
            name: 'promptSynchronization',
            type: 'boolean',
            critical: true
        },
        {
            name: 'status',
            type: 'string',
            critical: true
        },
        {
            name: 'subject',
            type: 'string',
            critical: true,
            validators: ['presence']
        },
        {
            name: 'template',
            type: 'string',
            critical: true
        },
        {
            name: 'to',
            type: 'string',
            critical: true
        },
        {
            name: 'contentType',
            type: 'string',
            defaultValue: 'text/html',
            critical: true
        },
        {
            name: '_content_html',
            persist: false,
            validators: ['presence']
        },
        {
            name: '_content_plain',
            persist: false
        }
    ],
    proxy: {
        type: 'baseproxy'
    }
});