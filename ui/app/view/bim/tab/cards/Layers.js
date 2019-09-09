
Ext.define('CMDBuildUI.view.bim.tab.cards.Layers', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.bim.tab.cards.LayersController',
        'CMDBuildUI.view.bim.tab.cards.LayersModel'
    ],

    alias: 'widget.bim-tab-cards-layers',
    controller: 'bim-tab-cards-layers',
    viewModel: {
        type: 'bim-tab-cards-layers'
    },
    scrollable: 'y',
    bind: {
        store: '{bimIfcLayerStore}'
    },
    columns: [{
        text: CMDBuildUI.locales.Locales.bim.layers.name,
        localized: {
            text: 'CMDBuildUI.locales.Locales.bim.layers.name'
        },
        dataIndex: 'name',
        flex: 5,
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.bim.layers.qt,
        localized: {
            text: 'CMDBuildUI.locales.Locales.bim.layers.qt'
        },
        dataIndex: 'qt',
        flex: 2,
        align: 'center'

    }, {
        xtype: 'actioncolumn',
        itemId: 'gridActionColumn',
        flex: 1,
        align: 'center',
        menuDisabled: true,
        text: CMDBuildUI.locales.Locales.bim.layers.visivility,
        localized: {
            text: 'CMDBuildUI.locales.Locales.bim.layers.visivility'
        },
        getClass: function (v, meta, row, rowIndex, colIndex, store) {
            switch (row.get('clicks')) {
                case 0:
                    return 'x-fa fa-eye open';
                case 1:
                    return 'x-fa fa-eye-slash half';
                case 2:
                    return 'x-fa fa-eye close';
            }
        }
    }],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            xtype: 'tbfill'
        }, {
            xtype: 'tool',
            itemId: 'topMenuShowAll',
            iconCls: 'x-fa fa-eye open',
            cls: 'management-tool',
            tooltip: CMDBuildUI.locales.Locales.bim.layers.menu.showAll,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.bim.layers.menu.showAll'
            }
        }, {
            xtype: 'tool',
            itemId: 'topMenuHideAll',
            iconCls: 'x-fa fa-eye close',
            cls: 'management-tool',
            tooltip: CMDBuildUI.locales.Locales.bim.layers.menu.hideAll,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.bim.layers.menu.hideAll'
            }
        }]
    }]
});
