Ext.define('CMDBuildUI.store.navigationtrees.NavigationTrees', {
    extend: 'CMDBuildUI.store.Base',

    model: 'CMDBuildUI.model.navigationTrees.DomainTree',

    alias: 'store.navigationTrees.navigationTrees',

    config: {
        defaultRootProperty: 'data'
    }
});