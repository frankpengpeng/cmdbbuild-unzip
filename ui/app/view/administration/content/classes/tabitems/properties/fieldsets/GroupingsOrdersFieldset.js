Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.GroupingsOrdersFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-groupingsordersfieldset',
    controller: 'administration-content-classes-tabitems-properties-fieldsets-groupingsordersfieldset',

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
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group'
                    },
                    dataIndex: 'description',
                    align: 'left',
                    editor: {
                        xtype: 'textfield',
                        listeners: {
                            focus: function (textfield, event, eOpts) {
                                Ext.suspendLayouts();
                                var view = this.up();
                                var record = view.editingPlugin.context.record;
                                record.set('editing', true);
                            },
                            blur: function (textfield, event, eOpts) {
                                var view = this.up();
                                var record = view.editingPlugin.context.record;
                                record.set('editing', false);
                                record.commit();
                                Ext.resumeLayouts(true);
                                view.up().refresh();
                            }
                        }
                    },
                    variableRowHeight: true
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
                            grid.editingPlugin.startEdit(record, 0);
                        },
                        style: {
                            margin: '10'
                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            CMDBuildUI.util.Logger.log("getclass", CMDBuildUI.util.Logger.levels.debug);
                            if (record.get('editing')) {
                                return 'x-fa fa-check';
                            }
                            return 'x-fa fa-pencil';
                        }
                    }, {
                        iconCls: 'x-fa fa-arrow-up',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.moveup,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.moveup'
                        },
                        handler: 'moveUp',
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            if (!record.get('editing')) {
                                rowIndex = rowIndex == null ? view.getStore().findExact('id', record.get('id')) : rowIndex;
                                return rowIndex == 0;
                            } else {
                                return true;
                            }
                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            if (record.get('editing')) {
                                return 'x-fa fa-ellipsis-h';
                            }
                            return 'x-fa fa-arrow-up';
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
                        handler: 'moveDown',
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            if (!record.get('editing')) {
                                rowIndex = rowIndex == null ? view.getStore().findExact('id', record.get('id')) : rowIndex;
                                return rowIndex >= view.store.getCount() - 1;
                            } else {
                                return true;
                            }

                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            if (record.get('editing')) {
                                return 'x-fa fa-ellipsis-h';
                            }
                            return 'x-fa  fa-arrow-down';
                        },
                        margin: '0 10 0 10'
                    }, {
                        iconCls: 'x-fa fa-trash',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.remove,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.remove'
                        },
                        handler: 'deleteRow',
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            if (!record.get('editing')) {
                                return false;
                            } else {
                                return true;
                            }

                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            if (record.get('editing')) {
                                return 'x-fa fa-ellipsis-h';
                            }
                            return 'x-fa  fa-times';
                        },
                        margin: '0 10 0 10'
                    }, {
                        iconCls: 'x-fa fa-flag',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.localize,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.localize'
                        },
                        handler: function (grid, rowIndex, colIndex, item, event, record) {
                            var me = this;
                            var vm = grid.lookupViewModel();
                            var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassGroupDescription(grid.lookupViewModel().get('objectTypeName'), grid.getStore().getAt(rowIndex).get('name') || '.');
                            CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, CMDBuildUI.util.administration.helper.FormHelper.formActions.edit, Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 10), vm);
                        },
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            if (!record.get('editing')) {
                                return false;
                            } else {
                                return true;
                            }

                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            if (record.get('editing')) {
                                return 'x-fa fa-ellipsis-h';
                            }
                            return 'x-fa  fa-flag';
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
                    localized: {
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
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.attributes.strings.addnewgroup'
                        },
                        handler: 'onAddGroupClick'
                    }]
                }]
            }]
        }]
    }]
});