Ext.define('CMDBuildUI.model.navigationTrees.TreeNode', { //TODO: change to map
    extend: 'CMDBuildUI.model.base.Base',
    fields: [{
        name: 'domain',
        type: 'string'
    }, {
        name: 'filter',
        type: 'string'
    }, {
        name: 'recursionEnabled',
        type: 'boolean'
    }, {
        name: 'showOnlyOne',
        type: 'boolean'
    }, {
        name: 'direction',
        type: 'string'
    }, {
        name: 'parent',
        type: 'string'
    }, {
        name: 'targetClass',
        type: 'string'
    }, {
        name: 'params',
        type: 'string'
    }] //Recursion Enabled param is not inserted //TODO: avoid the inserting of the reade of metadata cmp.
});

