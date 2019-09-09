Ext.define('CMDBuildUI.model.users.Session', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    statics: {
        temporary_id: 'session_temporary_id'
    },

    fields: [{
        name: 'userDescription',
        type: 'string'
    }, {
        name: 'username',
        type: 'string',
        critical: true,
        validators: [
            'presence'
        ]
    }, {
        name: 'password',
        type: 'string',
        validators: [
            'presence'
        ]
    }, {
        name: 'role',
        type: 'string',
        critical: true
    }, {
        name: 'availableRoles'
    }, {
        name: 'activeTenants',
        critical: true
    }, {
        name: 'availableTenants'
    }, {
        name: 'scope',
        type: 'string',
        defaultValue: 'ui'
    }],

    proxy: {
        url: '/sessions/',
        type: 'baseproxy',
        extraParams: {
            ext: true
        }
    }
});


