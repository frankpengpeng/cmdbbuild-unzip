Ext.define('CMDBuildUI.view.map.tab.cards.NavigationTreeModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-tab-cards-navigationtree',

    stores: {
        navigationTreeProxyStore: {
            model: 'CMDBuildUI.model.map.navigation.NavigationTree',
            proxy: {
                url: '/classes/_ANY/cards/_ANY/geovalues',
                type: 'baseproxy',
                extraParams: {
                    attach_nav_tree: true
                },
                reader: {
                    type: 'json',
                    rootProperty: 'meta.nav_tree_items'
                }
            }
        }
    }
});
