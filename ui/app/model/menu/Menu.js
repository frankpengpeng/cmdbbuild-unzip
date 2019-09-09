Ext.define('CMDBuildUI.model.menu.Menu', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'group', // only for root 
        type: 'string'// form menu tree creation
    }, {
        // form menu tree creation compatibility
        name: 'name',
        type: 'string',
        calculate: function (data) {
            return data.group;
        }
    }, {
        // form menu tree creation compatibility
        name: 'description',
        type: 'string',
        calculate: function (data) {
            return data.group;
        }
    }, {
        name: 'menuType',
        type: 'string',
        critical: true
    }, {
        name: 'objectDescription',
        type: 'string' // description in only in child nodes
    }, {
        name: 'objectId',
        type: 'auto' // only for report
    }, {
        name: 'objectType',
        type: 'string',
        critical: true
    }, {
        name: '_objectDescription_translation',
        type: 'string'
    }, {
        nane: 'children',
        type: 'auto',
        critical: true
    }],

    proxy: {
        url: '/menu/',
        type: 'baseproxy'
    }
});
