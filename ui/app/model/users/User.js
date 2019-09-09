Ext.define('CMDBuildUI.model.users.User', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    fields: [{
        name: 'description',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'username',
        type: 'string',
        validators: [
            'presence'
        ],
        persist: true,
        critical: true
    }, {
        name: 'email',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'lastExpiringNotification',
        type: 'date'
    }, {
        name: 'lastPasswordChange',
        type: 'date'
    }, {
        name: 'passwordExpiration',
        type: 'date'
    }, {
        name: 'service',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'defaultUserGroup',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'multiGroup',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true,
        persist: true,
        critical: true
    }, {
        name: 'userGroups',
        type: 'auto',
        defaultValue: [],
        persist: true,
        critical: true
    }, {
        name: 'userTenants',
        type: 'auto',
        defaultValue: [],
        persist: true,
        critical: true
    }, {
        name: 'password',
        type: 'string'
    }, {
        name: 'rolePrivileges',
        type: 'auto'
    }, {
        name: 'multiTenantActivationPrivileges',
        type: 'string',
        critical: true,
        defaultValue: 'any'
    }],

    proxy: {
        url: '/users/',
        type: 'baseproxy',
        extraParams: {
            ext: true
        }
    },

    clone: function () {
        var newUser = this.copy();
        newUser.set('_id', undefined);
        newUser.set('username', '');
        newUser.set('description', '');
        newUser.crudState = "C";
        newUser.phantom = true;
        delete newUser.crudStateWas;
        delete newUser.previousValues;
        delete newUser.modified;
        return newUser;
    }
});


