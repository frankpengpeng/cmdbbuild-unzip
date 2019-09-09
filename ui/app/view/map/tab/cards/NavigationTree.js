
Ext.define('CMDBuildUI.view.map.tab.cards.NavigationTree', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.NavigationTreeController',
        'CMDBuildUI.view.map.tab.cards.NavigationTreeModel',

        'CMDBuildUI.store.map.Tree'
    ],
    alias: 'widget.map-tab-cards-navigationtree',
    controller: 'map-tab-cards-navigationtree',
    viewModel: {
        type: 'map-tab-cards-navigationtree'
    },

    cls: 'noicontree',

    store: {
        type: 'map-tree',
        reference: 'navigationTreeStore',
        storeId: 'navigationTreeStore',
        autoSync: true,
        sorters: [{
            property: 'text',
            direction: 'ASC'
        }]
    },

    hideHeaders: true,
    me: this,
    columns: [{
        xtype: 'treecolumn',
        dataIndex: 'text',
        flex: 20
    }, {
        xtype: 'actioncolumn',
        width: '100',
        id: 'navTreeActioncolumn',
        iconCls: 'x-fa fa-arrow-circle-right NavigationTree',
        flex: 1
    }]

    /**
     * @event opennavigationtrepath
     * Event fired on row selection change
     * @param {object} row the selected row
     */

    /**
     * @event treedomainsloading
     * this function is fired when the treedomain is loaded
     * @param tree the treeDomain store loades
     */
});
