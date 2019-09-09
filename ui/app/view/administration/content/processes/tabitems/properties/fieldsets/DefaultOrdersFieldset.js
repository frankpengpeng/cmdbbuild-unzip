Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.DefaultOrdersFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-defaultordersfieldset',
    viewModel: {},
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        bind: {
            hidden: '{actions.add}'
        },
        title: CMDBuildUI.locales.Locales.administration.classes.strings.datacardsorting,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.classes.strings.datacardsorting'
        },
        collapsible: true,
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 0.75,
            items: [{
                xtype: 'components-grid-reorder-grid',
                reference: 'defaultOrderGrid',
                bind: {
                    store: '{theProcess.defaultOrder}'
                },
                columns: [{
                    flex: 2,
                    text: CMDBuildUI.locales.Locales.administration.common.strings.attribute,
                    localized:{
                        text: 'CMDBuildUI.locales.Locales.administration.common.strings.attribute'
                    },
                    dataIndex: 'attribute',
                    align: 'left'
                }, {
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.classes.texts.direction,
                    localized:{
                        text: 'CMDBuildUI.locales.Locales.administration.classes.texts.direction'
                    },
                    dataIndex: 'direction',
                    align: 'left',
                    renderer: function (value) {
                        return Ext.String.capitalize(value);
                    }
                }, {
                    xtype: 'actioncolumn',
                    minWidth: 150,
                    maxWidth: 150,
                    bind: {
                        hidden: '{!actions.edit}'
                    },
                    align: 'center',
                    items: [{
                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                            grid.getStore().remove(record);
                            var newStore = grid.up('administration-content-processes-view').getViewModel().getStore('defaultOrderStoreNew');
                            newStore.removeAll();
                            newStore.add(record);
                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            return 'x-fa fa-pencil';
                        },
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.tooltips.edit;
                        }
                    }, {
                        iconCls: 'x-fa fa-arrow-up',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.moveup,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.moveup'
                        },
                        handler: function (grid, rowIndex, colIndex) {
                            Ext.suspendLayouts();
                            var store = grid.getStore();
                            var record = store.getAt(rowIndex);
                            rowIndex--;
                            if (!record || rowIndex < 0) {
                                return;
                            }

                            store.remove(record);
                            store.insert(rowIndex, record);

                            this.getView().refresh();
                            Ext.resumeLayouts(true);
                        },
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            return rowIndex === 0;
                        }
                    }, {
                        iconCls: 'x-fa fa-arrow-down',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.movedown,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.movedown'
                        },
                        handler: function (grid, rowIndex, colIndex) {
                            Ext.suspendLayouts();
                            var store = grid.getStore();
                            var record = store.getAt(rowIndex);
                            rowIndex++;
                            if (!record || rowIndex >= store.getCount()) {
                                return;
                            }
                            store.remove(record);
                            store.insert(rowIndex, record);


                            this.getView().refresh();
                            Ext.resumeLayouts(true);
                        },
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            return rowIndex >= view.store.getCount() - 1;
                        }
                    }, {
                        iconCls: 'x-fa fa-times',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.remove,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.remove'
                        },
                        handler: function (grid, rowIndex, colIndex) {
                            Ext.suspendLayouts();
                            var store = grid.getStore();
                            var record = store.getAt(rowIndex);
                            store.remove(record);
                            // force combobox to reload filters
                            var attributeComboStore = this.lookupReferenceHolder().lookupReference("defaultOrderAttribute").getStore();
                            var attributeComboStoreFilter = attributeComboStore.getFilters();
                            attributeComboStore.removeFilter(attributeComboStoreFilter);
                            attributeComboStore.applyFilters(attributeComboStoreFilter.items[0]);

                            this.getView().refresh();
                            Ext.resumeLayouts(true);
                        }
                    }]
                }]
            }, {
                flex: 1,
                margin: '20 0 20 0',
                items: [{
                    xtype: 'components-grid-reorder-grid',
                    bind: {
                        store: '{defaultOrderStoreNew}',
                        hidden: '{!actions.edit}'
                    },

                    flex: 1,
                    columns: [{
                        flex: 2,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: 'attribute',
                        widget: {
                            xtype: 'combobox',
                            queryMode: 'local',
                            typeAhead: true,
                            inputField: 'attribute',
                            reference: 'defaultOrderAttribute',
                            displayField: 'description',
                            valueField: 'name',
                            value: '',
                            bind: {
                                value: '{record.attribute}',
                                store: '{unorderedAttributesStore}'
                            }
                        }
                    }, {
                        flex: 1,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: 'direction',
                        widget: {
                            xtype: 'combobox',
                            reference: 'defaultOrderDirection',
                            editable: false,
                            displayField: 'label',
                            valueField: 'value',
                            bind: {
                                value: '{record.direction}',

                                store: '{defaultOrderDirectionsStore}'
                            },
                            listeners: {
                                change: function (combo, newValue, oldValue, eOpts) {
                                    if (!newValue) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }, {
                        xtype: 'actioncolumn',
                        minWidth: 150,
                        maxWidth: 150,

                        align: 'center',
                        items: [{
                            iconCls: 'x-fa fa-ellipsis-h',
                            disabled: true
                        }, {
                            iconCls: 'x-fa fa-ellipsis-h',
                            disabled: true
                        }, {
                            iconCls: 'x-fa fa-ellipsis-h',
                            disabled: true
                        }, {
                            iconCls: 'x-fa fa-plus',
                            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.add,
                            localized:{
                                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.add'
                            },
                            handler: function (grid, rowIndex, colIndex) {
                                var attribute = this.lookupReferenceHolder().lookupReference("defaultOrderAttribute");
                                var direction = this.lookupReferenceHolder().lookupReference("defaultOrderDirection");
                                var orderGrid = this.lookupReferenceHolder().lookupReference("defaultOrderGrid");
                                var orderStore = orderGrid.getStore();
                                var newRecordStore = grid.getStore();
                                if (attribute.getValue() && direction.getValue()) {
                                    Ext.suspendLayouts();
                                    orderStore.add(CMDBuildUI.model.AttributeOrder.create({
                                        attribute: attribute.getValue(),
                                        direction: direction.getValue()
                                    }));
                                    attribute.getStore().remove(attribute.selection);
                                    newRecordStore.getAt(0).set('attribute', null);
                                    newRecordStore.getAt(0).set('direction', null);
                                    orderGrid.getView().refresh();

                                    Ext.resumeLayouts();
                                }
                            }
                        }]
                    }]
                }]
            }]
        }]
    }]
});