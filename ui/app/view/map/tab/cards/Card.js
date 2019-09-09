
Ext.define('CMDBuildUI.view.map.tab.cards.Card', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.CardController',
        'CMDBuildUI.view.map.tab.cards.CardModel'
    ],
    alias: 'widget.map-tab-cards-card',
    controller: 'map-tab-cards-card',
    viewModel: {
        type: 'map-tab-cards-card'
    },
    autoScroll: true,
    items: [{
        title: CMDBuildUI.locales.Locales.gis.geographicalAttributes,
        xtype: 'fieldset',
        ui: 'formpagination',
        localized: {
            text: 'CMDBuildUI.locales.Locales.gis.geographicalAttributes'
        },
        items: [{
            reference: 'map-geoattributes-grid',
            xtype: 'grid',
            displayField: 'name',

            store: {
                type: 'array',
                storeId: 'geoattributeGrid',
                fields: ['name', '_owner_type', 'zoomDef', 'zoomMin', 'zoomMax', 'type', 'subtype', 'layer_id', '_id', 'coordinates', 'add', 'edit', 'remove', 'view']
            },
            columns: [{
                menudDisabled: true,
                align: 'left',
                dataIndex: 'name',
                flex: 1
            }, {
                menuDisabled: true,
                xtype: 'actioncolumn',
                items: [{
                    glyph: 'f055@FontAwesome', //plus icon
                    tooltip: CMDBuildUI.locales.Locales.common.actions.add,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.common.actions.add'
                    },
                    iconCls: 'margin-left', //5 px margin
                    handler: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {
                        actioncolumn.getBubbleParent().getBubbleParent().getBubbleParent().getBubbleParent().fireEvent("addbtnclick", actioncolumn, rowIndex, colIndex, item, event, record, row);
                    },
                    isDisabled: function (tableview, rowindex, colindex, item, record) {
                        if (record.get('add') == null) {
                            return true;
                        } else {
                            return record.get('add');
                        }
                    }
                }, {
                    glyph: 'xf040@FontAwesome', //pencil icon
                    tooltip: CMDBuildUI.locales.Locales.common.actions.edit,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.common.actions.edit'
                    },
                    iconCls: 'margin-left', //5 px margin
                    handler: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {
                        actioncolumn.getBubbleParent().getBubbleParent().getBubbleParent().getBubbleParent().fireEvent("modifybtnclick", actioncolumn, rowIndex, colIndex, item, event, record, row);
                    },
                    isDisabled: function (tableview, rowindex, colindex, item, record) {
                        if (record.get('edit') == null) {
                            return true;
                        } else {
                            return record.get('edit');
                        }
                    }
                }, {
                    glyph: 'f057@FontAwesome', //X icon
                    tooltip: CMDBuildUI.locales.Locales.common.actions.remove,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.common.actions.remove'
                    },
                    iconCls: 'margin-left', //5 px margin
                    handler: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {
                        actioncolumn.getBubbleParent().getBubbleParent().getBubbleParent().getBubbleParent().fireEvent("removebtnclick", actioncolumn, rowIndex, colIndex, item, event, record, row);
                    },
                    isDisabled: function (tableview, rowindex, colindex, item, record) {
                        if (record.get('remove') == null) {
                            return true;
                        } else {
                            return record.get('remove');
                        }
                    }
                }, {
                    glyph: 'f06e@FontAwesome', //eye icon
                    tooltip: CMDBuildUI.locales.Locales.gis.view,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.gis.view'
                    },
                    iconCls: 'margin-left', //5 px margin
                    handler: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {
                        actioncolumn.getBubbleParent().getBubbleParent().getBubbleParent().getBubbleParent().fireEvent("viewbtnclick", actioncolumn, rowIndex, colIndex, item, event, record, row);
                    },
                    isDisabled: function (tableview, rowindex, colindex, item, record) {
                        if (record.get('view') == null) {
                            return true;
                        } else {
                            return record.get('view');
                        }
                    }
                }]
            }]
        }]
    }, {
        xtype: 'classes-cards-card-view',
        shownInPopup: true,
        hideTools: true,
        hideWidgets: true
    }]
});
