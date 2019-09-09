Ext.define('CMDBuildUI.model.users.Grant', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'role',
        type: 'number',
        critical: true
    }, {
        name: 'mode',
        type: 'string',
        critical: true,
        defaultValue: '-'
    }, {
        name: 'modeTypeNone',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return (rec.get('mode') === '-') ? true : false;
        },
        depends: ['mode']
    }, {
        name: 'modeTypeAllow',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return (rec.get('mode') === 'r') ? true : false;
        },
        depends: ['mode']
    }, {
        name: 'modeTypeRead',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return (rec.get('mode') === 'r') ? true : false;
        },
        depends: ['mode']
    }, {
        name: 'modeTypeWrite',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return (rec.get('mode') === 'w') ? true : false;
        },
        depends: ['mode']
    }, {
        name: 'modeTypeDefault',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return (rec.get('mode') === 'w') ? true : false;
        },
        depends: ['mode']
    }, {
        name: 'modeTypeDefaultRead',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return (rec.get('mode') === 'r') ? true : false;
        },
        depends: ['mode']
    }, {
        name: 'objectType',
        type: 'string',
        critical: true
    }, {
        name: 'objectTypeName',
        type: 'string',
        critical: true
    }, {
        name: '_object_description',
        type: 'string',
        critical: false
    }, {
        name: 'filter',
        type: 'string',
        critical: true,
        defaultValue : ''
    }, {
        name: 'attributePrivileges',
        type: 'auto',
        critical: true,
        defaultValue: {}
    }, {
        name: '_card_clone_disabled',
        type: 'boolean',
        critical: true,
        defaultValue: false
    }, {
        name: '_card_create_disabled',
        type: 'boolean',
        critical: true,
        defaultValue: false
    }, {
        name: '_card_delete_disabled',
        type: 'boolean',
        critical: true,
        defaultValue: false
    }, {
        name: '_card_update_disabled',
        type: 'boolean',
        critical: true,
        defaultValue: false
    },{
        name: '_card_relation_disabled',
        type: 'boolean',
        critical: true,
        defaultValue: false
    },{
        name: '_card_print_disabled',
        type: 'boolean',
        critical: true,
        defaultValue: false
    }
],

    convertOnSet: true,

    changeMode: function (mode) {
        this.set('mode', mode);
    },

    proxy: {
        url: '/roles/',
        type: 'baseproxy',
        extraParams: {
            includeObjectDescription: true,
            includeRecordsWithoutGrant: true,
            ext: true
        }
    }
});