
Ext.define('CMDBuildUI.view.bim.tab.cards.Tree', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.bim.tab.cards.TreeController',
        'CMDBuildUI.view.bim.tab.cards.TreeModel'
    ],

    alias: 'widget.bim-tab-cards-tree',

    controller: 'bim-tab-cards-tree',

    viewModel: {
        type: 'bim-tab-cards-tree'
    },

    //this store doesn't have a proper model. Should have one
    root: {
        text: CMDBuildUI.locales.Locales.bim.tree.root,
        children: []
    },

    rootVisible: false,
    layout: 'fit',

    columns: [{
        xtype: 'treecolumn',
        text: CMDBuildUI.locales.Locales.bim.tree.columnLabel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.bim.tree.columnLabel'
        },
        dataIndex: 'text',
        flex: 1
    }, {
        xtype: 'actioncolumn',
        arrowTree: 'arrowTree',
        align: 'center',
        tooltip: CMDBuildUI.locales.Locales.bim.tree.arrowTooltip,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.bim.tree.arrowTooltip'
        },
        menuDisabled: true,
        width: 30,
        getClass: function (v, meta, row, rowIndex, colIndex, store) {
            var leaf = row.get('leaf');
            if (leaf) {
                return 'x-fa fa-arrow-right arrowTree';
            }
            return null;
        },
        handler: function (v, rowIndex, colIndex, item, e, record, row) {
            var oid = record.get('oid');
            var leaf = record.get('leaf');
            CMDBuildUI.util.bim.Viewer.select(oid, leaf);
        }

    }/*, {
        xtype: 'actioncolumn',
        itemId: 'singleIfcOpacity',
        align: 'center',
        width: 30,
        getClass: function (v, meta, row, rowIndex, colIndex, store) {
            // var leaf = row.get('leaf');
            // if (leaf) {
            var clicks = row.get('clicks');

            switch (clicks) {
                case 0:
                    return 'x-fa fa-eye open';
                    break;
                case 2:
                    return 'x-fa fa-eye close'
                    break;
                default:
                    break;
            }
            // }
        }
    } */]

    /**
     * NOTE:
     * The tree is popolated in the controller. The function wich popolates the tree
     * is called after a global event is fired
     */
});
