
Ext.define('CMDBuildUI.view.graph.tab.bottomInfo.BottomInfo', {
    extend: 'Ext.toolbar.Toolbar',

    requires: [
        'CMDBuildUI.view.graph.tab.bottomInfo.BottomInfoController',
        'CMDBuildUI.view.graph.tab.bottomInfo.BottomInfoModel'
    ],

    controller: 'graph-tab-bottominfo-bottominfo',
    viewModel: {
        type: 'graph-tab-bottominfo-bottominfo'
    },
    alias: 'widget.graph-tab-bottominfo-bottominfo',
    items: [{
        xtype: 'tbtext',
        itemId: 'nodesNumber',
        bind: {
            html: 'Nodes: {nodesNumber}'
        }
    }, {
        xtype: 'tbtext',
        bind: {
            html: 'Edges: {edgesNumber}'
        }
    }]
});
