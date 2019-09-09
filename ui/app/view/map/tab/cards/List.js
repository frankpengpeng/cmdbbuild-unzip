
Ext.define('CMDBuildUI.view.map.tab.cards.List',{
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.ListController',
        'CMDBuildUI.view.map.tab.cards.ListModel'
    ],

    xtype: 'map-tab-cards-list',
    controller: 'map-tab-cards-list',
    viewModel: {
        type: 'map-tab-cards-list'
    },
    
    bind: {
        store: '{cards}'
    },

    config: {
        objectTypeName: null,
        allowFilter: true
    },

    forceFit: true,
    loadMask: true,
    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    }
    /**
     * @event selectedfeaturechange
     * this function is launched when the selected feature changes
     * @param {array} featrure the feature clicked in maps
     */
});
