Ext.define('CMDBuildUI.view.administration.navigation.TreeItem',{
    extend:'Ext.list.TreeItem',
    alias:'widget.administration-treelistitem',
    controller: 'administration-navigation-tree-item',
    requires: [
        'CMDBuildUI.view.administration.navigation.TreeItemController'
    ],
    listeners: {
        dblclick: {
            fn:'dblClick',
            element: 'rowElement'
        }
    }
});
