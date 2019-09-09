
Ext.define('CMDBuildUI.view.administration.navigation.Tree', {
    extend: 'Ext.list.Tree',

    requires: [
        'CMDBuildUI.view.administration.navigation.TreeController',
        'CMDBuildUI.view.administration.navigation.TreeModel',

        'CMDBuildUI.store.administration.MenuAdministration'
    ],

    id: 'administrationNavigationTree',
    alias: 'widget.administration-navigation-tree',
    controller: 'administration-navigation-tree',
    viewModel: {
        type: 'administration-navigation-tree'
    },
    autoEl: {
        'data-testid': 'administration-navigation-tree'
    },
    config:{
        selected: null,
        store: null,
        defaults:{
            xtype:'administration-treelistitem'
        }
    },
    ui: 'administration-navigation-tree',
    expanderOnly: true,

    bind: {
        store: '{menuItems}',
        selection: '{selected}'
    }

});
