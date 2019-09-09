Ext.define('CMDBuildUI.view.administration.content.menus.treepanels.DestinationPanel', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.menus.treepanels.DestinationPanelController',
        'CMDBuildUI.view.administration.content.menus.treepanels.DestinationPanelModel'
    ],

    alias: 'widget.administration-content-menus-treepanels-destinationpanel',
    controller: 'administration-content-menus-treepanels-destinationpanel',
    viewModel: {
        type: 'administration-content-menus-treepanels-destinationpanel'
    },

    itemId: 'treepaneldestination',
    margin: '0 15 0 0',
    cls: 'tree-noborder',
    ui: 'administration-navigation-tree',
    reference: 'menuTreeViewDestination',
    align: 'start',
    useArrows: true,
    containerScroll: true,
    
    store: Ext.create('Ext.data.TreeStore', {
        model: 'CMDBuildUI.model.menu.MenuItem',
        storeId: 'menuDestinationTreeStore',
        reference: 'menuDestinationTreeStore',
        root: {
            text: 'Root',
            expanded: true
        },
        rootVisible: false,
        proxy: {
            type: 'memory'
        },
        sorters: [{
            property: 'index',
            direction: 'ASC'
        }],
        autoLoad: true
    }),


    columns: [{
        xtype: 'treecolumn',
        dataIndex: 'text',
        flex: 1,
        editable: true,
        /**
         * @param {String} value
         * @param {Object} metaData
         * @param {Ext.data.Model} record
         * @param {Number} rowIndex
         * @param {Number} colIndex
         * @param {Ext.data.Store} store
         * @param {Ext.view.View} view
         * 
         * @returns {String}
         */
        renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
            metaData.align = 'left';
            return value;
        },
        editor: {
            xtype: 'textfield',
            allowBlank: false
        }
    }, {
        xtype: 'actioncolumn',
        width: '60',
        flex: 1,
        hidden: true,
        items: [{
            iconCls: 'x-fa fa-flag',
            tooltip: CMDBuildUI.locales.Locales.administration.common.tooltips.localize, // Localize
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.tooltips.localize'
            },
            handler: 'onTranslateClick',

            getClass: function (v, meta, rec) { // Or return a class from a function
                if (rec.get('menutype') === 'root') {
                    return '';
                }
                return 'x-fa fa-flag';
            }
        }],
        bind: {
            hidden: '{!actions.edit}'
        }
    }],
    plugins: [{
        pluginId: 'cellediting',
        ptype: 'cellediting',
        clicksToEdit: 2,
        listeners: {
            beforeedit: function (editor, context, eOpts) {
                var column = context.column;
                if (!editor.view.up('administration-content-menus-mainpanel').getViewModel().get('actions.view')) {
                    column.setEditor({
                        xtype: 'textfield',
                        allowBlank: false
                    });
                } else {
                    return false;
                }

            },
            edit: function (editor, context, eOpts) {

                context.record.set('objectdescription', context.value);
                context.record.set('objectDescription', context.value);

                var rec = context.store.getById(context.record.get('_id'));
                rec.set('objectdescription', context.value);
                rec.set('objectDescription', context.value);
                rec.set('text', context.value);
            }
        }
    }],
    viewConfig: {
        plugins: {
            id: 'treeviewdragdropdestination',
            ptype: 'treeviewdragdrop',
            ddGroup: 'TreeDD',
            nodeHighlightOnRepair: false,
            appendOnly: false,
            sortOnDrop: true,
            containerScroll: true,
            allowContainerDrops: true
        }
    },
    listeners: {
        beforedrop: 'onBeforeDrop'
    },
    viewready: function (tree) {
        var view = tree.getView(),
            dd = view.findPlugin('treeviewdragdrop');
        dd.dragZone.onBeforeDrag = function (data, e) {
            var record = view.getRecord(e.getTarget(view.itemSelector));
            return record.isLeaf();
        };
    }
});