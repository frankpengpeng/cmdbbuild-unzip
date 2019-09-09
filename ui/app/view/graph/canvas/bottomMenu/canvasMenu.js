
Ext.define('CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenu', {
    extend: 'Ext.toolbar.Toolbar',
    requires: [
        'CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenuController',
        'CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenuModel'
    ],

    controller: 'graph-canvas-bottommenu-canvasmenu',
    viewModel: {
        type: 'graph-canvas-bottommenu-canvasmenu'
    },
    alias: 'widget.graph-canvas-bottommenu-canvasmenu',
    items: [{
        xtype: 'tbtext',
        html: CMDBuildUI.locales.Locales.relationGraph.level,
        localized: {
            html: 'CMDBuildUI.locales.Locales.relationGraph.level'
        }
    }, {
        xtype: 'slider',
        id: 'sliderLevel',
        width: 200,
        increment: 1,
        minValue: 1,
        maxValue: 10
    }, {
        xtype: 'tbtext',
        id: 'sliderValue'
    }, {
        iconCls: 'x-fa fa-comment enableTooltip',
        tooltip: CMDBuildUI.locales.Locales.relationGraph.enableTooltips,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.relationGraph.enableTooltips'
        },
        cls: 'management-tool',
        xtype: 'button',
        itemId: 'enableTooltip',
        enableToggle: true
    }]
});
