Ext.define('CMDBuildUI.model.administration.AdminNavTreeItem', {
    extend: 'CMDBuildUI.model.base.Base',
    
    fields: [{
        name: 'direction',
        type: "string"
    }, {
        name: 'domain',
        type: "string"
    },{
        name: 'filter',
        type: "string"
    },{
        name: 'nodes',
        type: "auto"
    },{
        name: 'children',
        type: "auto",
        mapping: 'nodes'
    },{
        name: 'parent',
        type: "string"
    },{
        name: 'recursionEnabled',
        type: "boolean"
    },{
        name: 'showOnlyOne',
        type: "boolean"
    },{
        name: 'targetClass',
        type: "boolean"
    }],

    proxy: {
        type: 'memory'
    }
});