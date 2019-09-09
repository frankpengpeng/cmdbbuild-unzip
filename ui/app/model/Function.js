Ext.define('CMDBuildUI.model.Function', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'name',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.Attribute',
        name: 'parameters',
        associationKey: 'parameters'
    }],

    proxy: {
        type: 'baseproxy',
        url: '/functions/',
        extraParams: {
            detailed: true
        }
    }
});
