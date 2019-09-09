Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.GroupingsOrdersFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-groupingsordersfieldset',
    controller: 'administration-content-processes-tabitems-properties-fieldsets-groupingsordersfieldset',

    viewModel: {},
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        bind: {
            hidden: '{actions.add}'
        },
        title: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.attributegroupings,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.attributegroupings'
        },
        collapsible: true,
        ui: 'administration-formpagination',
        collapsed: true,
        items: [{
            columnWidth: 0.75,
            items: [{
                xtype: 'components-grid-reorder-grid',
                reference: 'groupingsGrid',
                itemId: 'groupingsAttributesGrid',
                bind: {
                    store: '{attributeGroupsStore}'
                },
                viewConfig: {
                    markDirty: false
                },
                columns: [{
                    flex: 2,
                    text: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group,
                    localized:{
                        text: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group'
                    },
                    dataIndex: 'description',
                    align: 'left'
                }, {
                    xtype: 'actioncolumn',
                    minWidth: 150,
                    maxWidth: 150,
                    bind: {
                        hidden: '{!actions.edit}'
                    },
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-pencil',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
                        },                
                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                            grid.getStore().remove(record);
                            var newStore = grid.up('administration-content-processes-view').getViewModel().getStore('attributeGroupsStoreNew');
                            newStore.removeAll();
                            newStore.add(record);
                        },
                        style: {
                            margin: '10'
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
                            return rowIndex == 0;
                        },
                        style: {
                            margin: '10'
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
                        },
                        margin: '0 10 0 10'
                    }, {
                        iconCls: 'x-fa fa-times',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.remove,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.remove'
                        },
                        handler: 'deleteRow',
                        margin: '0 10 0 10'
                    }, {
                        iconCls: 'x-fa fa-flag',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.localize,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.localize'
                        },
                        handler: function (grid, rowIndex, colIndex) {
                            var content = {
                                xtype: 'administration-localization-localizecontent',
                                scrollable: 'y',
                                viewModel: {
                                    data: {
                                        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                                        translationCode: CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassGroupDescription(grid.getViewModel().get('objectTypeName'), grid.getStore().getAt(rowIndex).get('value'))
                                    }
                                }
                            };
                            // custom panel listeners
                            var listeners = {
                                /**
                                 * @param {Ext.panel.Panel} panel
                                 * @param {Object} eOpts
                                 */
                                close: function (panel, eOpts) {
                                    CMDBuildUI.util.Utilities.closePopup('popup-localization');
                                }
                            };
                            // create panel
                            CMDBuildUI.util.Utilities.openPopup(
                                'popup-localization',
                                CMDBuildUI.locales.Locales.administration.common.strings.localization,
                                content,
                                listeners, {
                                    ui: 'administration-actionpanel',
                                    width: '450px',
                                    height: '450px'
                                }
                            );
                        },
                        margin: '0 10 0 10'
                    }]
                }]
            }, {
                xtype: 'components-grid-reorder-grid',
                margin: '20 0 20 0',
                bind: {
                    store: '{attributeGroupsStoreNew}',
                    hidden: '{!actions.edit}'
                },
                viewConfig: {
                    markDirty: false
                },
                columns: [{
                    flex: 2,
                    text: CMDBuildUI.locales.Locales.administration.attributes.strings.createnewgroup,
                    localized:{
                        text: 'CMDBuildUI.locales.Locales.administration.attributes.strings.createnewgroup'
                    },
                    xtype: 'widgetcolumn',
                    align: 'left',
                    dataIndex: 'description',
                    widget: {
                        xtype: 'textfield',
                        value: '',
                        bind: {
                            value: '{record.description}'
                        }
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
                        iconCls: 'x-fa fa-ellipsis-h',
                        disabled: true
                    }, {
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
                        tooltip: CMDBuildUI.locales.Locales.administration.attributes.strings.addnewgroup,
                        localized:{
                            tooltip: 'CMDBuildUI.locales.Locales.administration.attributes.strings.addnewgroup'
                        },
                        handler: 'onAddGroupClick'
                    }]
                }]
            }]
        }]
    }]
});