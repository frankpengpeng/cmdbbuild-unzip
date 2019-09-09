Ext.define('CMDBuildUI.view.administration.content.importexportdata.templates.card.helpers.FieldsetsHelper', {
    singleton: true,

    requires: ['CMDBuildUI.util.administration.helper.FormHelper', 'Ext.grid.plugin.DragDrop'],

    getGeneralPropertiesFieldset: function () {
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",
            layout: 'column',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },

            items: [
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCodeInput({
                        code: {
                            allowBlank: false,
                            bind: {
                                value: '{theImportExportTemplate.code}',
                                disabled: '{actions.edit}'
                            }
                        }
                    }, true, '[name="description"]'),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                        description: {
                            allowBlank: false,
                            bind: {
                                value: '{theImportExportTemplate.description}'
                            }
                        }
                    })
                ]),
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('targetType', {
                        targetType: {
                            fieldcontainer: {
                                bind: {
                                    disabled: '{isTargetTypeDisabled}'
                                }
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.applyon,
                            allowBlank: false,                           
                            bind: {
                                value: '{theImportExportTemplate.targetType}',
                                store: '{targetTypesStore}'
                            },
                            listeners: {
                                change: function (input, newVal, oldVal) {
                                    var targetNameField = input.up('fieldset').down('[name="targetName"]');
                                    var targetNameFieldContainer = targetNameField.up('fieldcontainer');
                                    if (newVal && input.getXType() === 'combobox') {
                                        if (targetNameField.getValue() !== '' && oldVal) {
                                            targetNameField.setValue('');
                                        }
                                        targetNameFieldContainer.setFieldLabel(input.getSelectedRecord().get('label'));
                                    }


                                }
                            }
                        }
                    }, true),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('targetName', {
                        targetName: {
                            fieldcontainer: {
                                bind: {
                                    disabled: '{isTargetNameDisabled}'
                                }
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.classdomain,
                            allowBlank: false,
                            displayField: 'description',
                            valueField: 'name',
                            forceSelection: true,
                            bind: {
                                store: '{allClassesOrDomains}',
                                value: '{theImportExportTemplate.targetName}'
                            }
                        }
                    }, true)
                ]),
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('type', {
                        type: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.type,
                            allowBlank: false,                            
                            bind: {
                                value: '{theImportExportTemplate.type}',
                                store: '{templateTypesStore}'
                            },
                            listeners: {
                                change: function (combo, newValue, oldValue) {
                                    var form = combo.up('form');
                                    if (form) {
                                        form.down('#importExportAttributeGrid').getView().refresh();
                                        form.down('#importExportAttributeGridNew').getView().refresh();

                                    }
                                }
                            }
                        }
                    }),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('fileFormat', {
                        fileFormat: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.fileformat,
                            allowBlank: false,
                            // store: CMDBuildUI.model.importexports.Template.fileTypes,
                            bind: {
                                value: '{theImportExportTemplate.fileFormat}',
                                store: '{fileTypesStore}'
                            }
                        }
                    })
                ]),
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('csv_separator', {
                        csv_separator: {
                            fieldcontainer: {
                                columnWidth: 1,
                                bind: {
                                    hidden: '{!isCsv}'
                                }
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.csvseparator,
                            allowBlank: false,
                            columnWidth: 0.5,                            
                            bind: {
                                value: '{theImportExportTemplate.csv_separator}',
                                store: '{csvSeparatorsStore}'
                            }
                        }
                    })
                ]),
                /**
                 * not necessary from 26/06 by F.B.
                 */
                // this.createContainer([
                //     CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('firstCol', {
                //         firstCol: {
                //             fieldcontainer: {
                //                 viewModel: {},
                //                 bind: {
                //                     hidden: '{!isExcell}'
                //                 }
                //             },

                //             fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.firstcolumnnumber,
                //             allowBlank: false,
                //             minValue: 1,
                //             bind: {
                //                 value: '{theImportExportTemplate.firstCol}'
                //             }
                //         }
                //     })
                // ]),
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('headerRow', {
                        headerRow: {
                            fieldcontainer: {
                                viewModel: {},
                                bind: {
                                    hidden: '{!isExcell}'
                                }
                            },

                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.headerrownumber,
                            allowBlank: false,
                            minValue: 0,
                            bind: {
                                value: '{theImportExportTemplate.headerRow}'
                            }
                        }
                    }),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('dataRow', {
                        dataRow: {
                            fieldcontainer: {
                                bind: {
                                    hidden: '{!isExcell}'
                                }
                            },

                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.datarownumber,
                            allowBlank: false,
                            minValue: 1,
                            bind: {
                                value: '{theImportExportTemplate.dataRow}'
                            }
                        }
                    })
                ]),
                /**
                 * not necessary from 26/06 by F.B.
                 */
                // this.createContainer([
                //     CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                //         useHeader: {
                //             fieldcontainer: {
                //                 fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.useheader,
                //                 localized: {
                //                     fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.useheader'
                //                 }
                //             },
                //             disabledCls: '',
                //             bind: {
                //                 value: '{theImportExportTemplate.useHeader}'
                //             }
                //         }
                //     }, 'useHeader'),
                //     CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                //         ignoreColumnOrder: {
                //             fieldcontainer: {
                //                 fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.ignorecolumn,
                //                 localized: {
                //                     fieldLabel: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.ignorecolumn'
                //                 }
                //             },
                //             disabledCls: '',
                //             bind: {
                //                 value: '{theImportExportTemplate.ignoreColumnOrder}'
                //             }
                //         }
                //     }, 'ignoreColumnOrder')
                // ]),

                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                        active: {
                            disabledCls: '',
                            bind: {
                                value: '{theImportExportTemplate.active}'
                            }
                        }
                    })
                ])
            ]
        };
    },

    getAttributesFieldset: function () {
        return {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.attributes.attributes,
            items: [{
                xtype: 'grid',
                headerBorders: false,
                border: false,
                bodyBorder: false,
                rowLines: false,
                sealedColumns: false,
                sortableColumns: false,
                enableColumnHide: false,
                enableColumnMove: false,
                enableColumnResize: false,
                cls: 'administration-reorder-grid',
                itemId: 'importExportAttributeGrid',
                selModel: {
                    pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                },
                plugins: {
                    ptype: 'actionColumnRowEditing',
                    id: 'actionColumnRowEditing',
                    hiddenColumnsOnEdit: ['actionColumnEdit', 'actionColumnMoveUp', 'actionColumnMoveDown', 'actionColumnCancel'],
                    clicksToEdit: 10,
                    buttonsUi: 'button-like-tool',
                    errorSummary: false,
                    placeholdersButtons: [{
                        iconCls: 'x-fa fa-ellipsis-h',
                        disabled: true,
                        xtype: 'button',
                        minWidth: 30,
                        maxWidth: 30,
                        ui: 'button-like-tool'
                    }, {
                        iconCls: 'x-fa fa-ellipsis-h',
                        disabled: true,
                        xtype: 'button',
                        minWidth: 30,
                        maxWidth: 30,
                        ui: 'button-like-tool'
                    }]
                },
                controller: {
                    control: {
                        '#': {
                            edit: function (editor, context, eOpts) {
                                context.record.set('mode', editor.editor.items.items[2].getValue());
                            },
                            beforeedit: function (editor, context, eOpts) {
                                if (editor.view.lookupViewModel().get('actions.view')) {
                                    return false;
                                }
                                var comboStore = editor.view.lookupViewModel().get('attributeModesReferenceStore');
                                comboStore.clearFilter();
                                switch (context.record.get('attribute')) {
                                    case 'IdObj1':
                                    case 'IdObj2':
                                        comboStore.addFilter([function (item) {
                                            return item.get('value') !== 'default';
                                        }]);
                                        editor.editor.items.items[2].setHideTrigger(false);
                                        editor.editor.items.items[2].setDisabled(false);
                                        return true;

                                    default:
                                        var allAttributesStore = editor.view.lookupViewModel().get('allClassOrDomainsAtributes');
                                        allAttributesStore.rejectChanges();
                                        var attribute = allAttributesStore.findRecord('name', context.record.get('attribute'));
                                        if (attribute) {
                                            switch (attribute.get('type')) {
                                                case 'lookup':
                                                case 'reference':
                                                    editor.editor.items.items[2].setHideTrigger(false);
                                                    editor.editor.items.items[2].setEmptyText('Select mode *');
                                                    editor.editor.items.items[2].setDisabled(false);
                                                    break;
                                                default:
                                                    editor.editor.items.items[2].setHideTrigger(true);
                                                    editor.editor.items.items[2].setEmptyText('');
                                                    editor.editor.items.items[2].setDisabled(true);
                                                    break;                                                
                                            }
                                        }
                                        return true;
                                }
                            }
                        }
                    }
                },
                columnWidth: 1,
                autoEl: {
                    'data-testid': 'administration-content-importexportdata-templates-grid'
                },

                forceFit: true,
                loadMask: true,

                labelWidth: "auto",
                bind: {
                    store: '{allSelectedAttributesStore}',
                    hidden: '{isAttributeGridHidden}'
                },
                columns: [{
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.attributes.attribute,
                    // localized: {
                    //     text: ''
                    // },
                    dataIndex: 'attribute',
                    align: 'left',
                    editor: {
                        xtype: 'displayfield',
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        padding: 0,
                        ui: 'reordergrid-editor-combo'
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.columnname,
                    flex: 1,
                    dataIndex: 'columnName',
                    align: 'left',
                    editor: {
                        xtype: 'textfield',
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        padding: 0,
                        ui: 'reordergrid-editor-combo'
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.mode,
                    flex: 1,
                    dataIndex: 'mode',
                    align: 'left',
                    editor: {
                        xtype: 'combo',
                        displayField: 'label',
                        valueField: 'value',
                        queryMode: 'local',
                        emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode,
                        editable: false,
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        padding: 0,
                        ui: 'reordergrid-editor-combo',
                        forceSelection: true,
                        allowBlank: false,
                        bind: {
                            store: '{attributeModesReferenceStore}'
                        },
                        listeners: {
                            beforerender: function (combo) {
                                var grid = this.up('grid');
                                var vm = grid.lookupViewModel();
                                var record = grid.editingPlugin.context.record;
                                var attribute;
                                var allAttributes = vm.get('allClassOrDomainsAtributes');
                                if (allAttributes && allAttributes.getData().length) {
                                    attribute = allAttributes.findRecord('_id', record.get('attribute'));
                                }
                                if (attribute && ['lookup', 'reference'].indexOf(attribute.get('type')) > -1) {
                                    combo.setDisabled(false);
                                } else if (['IdObj1', 'IdObj2'].indexOf(record.get('attribute')) > -1) {
                                    // cell.style = 'color:#83878b!important; border: 1px solid #cf4c35';
                                    combo.setDisabled(false);
                                } else {
                                    combo.setDisabled(true);
                                }
                            }
                        }
                    },
                    renderer: function (value, cell, record, rowIndex, colIndex, store, grid) {
                        var vm = this.lookupViewModel();
                        var attribute;
                        var allAttributes = this.lookupViewModel().get('allClassOrDomainsAtributes');
                        if (allAttributes && allAttributes.getData().length) {
                            attribute = allAttributes.findRecord('_id', record.get('attribute'));
                        }
                        if (!value && attribute && ['lookup', 'reference'].indexOf(attribute.get('type')) > -1) {
                            // cell.style = 'color:#83878b!important; border: 1px solid #cf4c35';
                            return CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode;
                        } else if (!value && ['IdObj1', 'IdObj2'].indexOf(record.get('attribute')) > -1) {
                            // cell.style = 'color:#83878b!important; border: 1px solid #cf4c35';
                            return CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode;
                        }
                        var attributeModesStore = vm.getStore('attributeModesReferenceStore');
                        // var attributeModesStore = CMDBuildUI.model.importexports.Attribute.attributeModes;
                        if (attributeModesStore) {
                            var mode = attributeModesStore.findRecord('value', value);
                            return mode && mode.get('label');
                        }
                        return value;
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.default,
                    flex: 1,
                    dataIndex: 'default',
                    align: 'left',
                    editor: {
                        xtype: 'textfield',
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        ui: 'reordergrid-editor-combo'
                    },
                    renderer: Ext.util.Format.htmlEncode,
                    bind: {
                        hidden: '{!isImport}'
                    }
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnEdit',
                    bind: {
                        hidden: '{actions.view}'
                    },
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                            grid.editingPlugin.startEdit(record, 1);
                        },
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            CMDBuildUI.util.Logger.log("getclass", CMDBuildUI.util.Logger.levels.debug);
                            if (record.get('editing')) {
                                return 'x-fa fa-check';
                            }
                            return 'x-fa fa-pencil';
                        }
                    }]
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnMoveUp',
                    bind: {
                        hidden: '{actions.view}'
                    },
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-arrow-up',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.moveup, // Move up
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.moveup'
                        },
                        handler: function (grid, rowIndex, colIndex) {
                            Ext.suspendLayouts();
                            var store = grid.getStore();
                            var record = store.getAt(rowIndex);
                            var previousRecord = store.getAt(rowIndex - 1);

                            rowIndex--;
                            if (!record || rowIndex < 0) {
                                return;
                            }
                            previousRecord.set('index', rowIndex + 1);
                            record.set('index', rowIndex);

                            try {
                                grid.refresh();
                            } catch (e) {

                            }
                            Ext.resumeLayouts();
                        },
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
                        }
                    }]
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnMoveDown',
                    bind: {
                        hidden: '{actions.view}'
                    },
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-arrow-down',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.movedown, // Move down
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.movedown'
                        },
                        handler: function (grid, rowIndex, colIndex) {
                            Ext.suspendLayouts();
                            var store = grid.getStore();
                            var record = store.getAt(rowIndex);
                            var previousRecord = store.getAt(rowIndex + 1);

                            rowIndex++;
                            if (!record || rowIndex >= store.getCount()) {
                                return;
                            }

                            previousRecord.set('index', rowIndex - 1);
                            record.set('index', rowIndex);
                            try {
                                grid.refresh();
                            } catch (e) {

                            }
                            Ext.resumeLayouts();
                        },
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
                        }
                    }]
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnCancel',
                    bind: {
                        hidden: '{actions.view}'
                    },
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-times',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.remove, // Remove
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.remove'
                        },

                        handler: function (grid, rowIndex, colIndex) {
                            var store = grid.lookupViewModel().get('allSelectedAttributesStore');
                            var record = store.getAt(rowIndex);
                            store.remove(record);

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
                            return 'x-fa  fa-times';
                        }
                    }]
                }]
            },
            {
                margin: '20 0 20 0',
                xtype: 'grid',
                headerBorders: false,
                border: false,
                bodyBorder: false,
                rowLines: false,
                sealedColumns: false,
                sortableColumns: false,
                enableColumnHide: false,
                enableColumnMove: false,
                enableColumnResize: false,
                cls: 'administration-reorder-grid',
                itemId: 'importExportAttributeGridNew',
                selModel: {
                    pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                },
                layout: 'hbox',
                autoEl: {
                    'data-testid': 'administration-content-importexportdata-templates-grid-newrecord'
                },

                forceFit: true,
                loadMask: true,

                labelWidth: "auto",
                bind: {
                    store: '{newSelectedAttributesStore}',
                    hidden: '{isAttributeGridNewHidden}'
                },

                columns: [{
                    xtype: 'widgetcolumn',
                    dataIndex: 'attribute',
                    align: 'left',
                    flex: 1,
                    widget: {
                        xtype: 'combo',
                        queryMode: 'local',
                        forceSelection: true,
                        emptyText: CMDBuildUI.locales.Locales.administration.importexport.texts.selectanattribute,
                        itemId: 'selectAttributeForGrid',
                        displayField: 'description',
                        valueField: 'name',
                        bind: {
                            store: '{allClassOrDomainsAtributesFiltered}'
                        },
                        listeners: {
                            beforequery: function (queryEv) {
                                var allAttrStore = this.lookupViewModel().get('allSelectedAttributesStore');
                                if (this.getStore() && this.getStore().source) {
                                    this.getStore().source.rejectChanges();
                                    this.getStore().clearFilter();
                                    if (allAttrStore.getRange()) {
                                        this.getStore().addFilter(function (item) {
                                            return !allAttrStore.findRecord('attribute', item.get('name'));
                                        });
                                    }
                                }
                                return true;
                            },
                            change: function (combo, newValue, oldValue) {
                                var grid = combo.up('grid');
                                var vm = combo.lookupViewModel();
                                var modeInput = combo.up('grid').down('#newComboMode');
                                var columnName = combo.up('grid').down('#newAttributeColumnName');
                                var modeStore = vm.get('attributeModesReferenceStore');
                                modeStore.clearFilter();

                                modeInput.reset();
                                var allAttributes = grid.up('form').getViewModel().get('allClassOrDomainsAtributes');

                                if (allAttributes && allAttributes.getData().length) {
                                    var attribute = allAttributes.findRecord('_id', newValue);
                                    if (attribute) {
                                        columnName.setValue(attribute.get('description'));
                                        if (attribute && ['lookup', 'reference'].indexOf(attribute.get('type')) > -1) {
                                            modeInput.style = 'color:#83878b!important; border: 1px solid #cf4c35';
                                            modeInput.allowBlank = false;
                                            modeInput.reset();
                                            modeInput.markInvalid('Required');
                                            modeInput.setHideTrigger(false);
                                            modeInput.setEmptyText('Select mode *');
                                            modeInput.enable();
                                        } else {

                                            modeInput.allowBlank = true;
                                            modeInput.style = 'border: 0px solid!important';
                                            modeInput.setHideTrigger(true);
                                            modeInput.setEmptyText('');
                                            modeInput.disable();
                                        }
                                    }
                                } else if (['IdObj1', 'IdObj2'].indexOf(newValue) > -1) {
                                    modeInput.style = 'color:#83878b!important; border: 1px solid #cf4c35';
                                    modeInput.allowBlank = false;
                                    modeInput.markInvalid('Required');
                                    modeInput.setEmptyText('Select mode *');

                                    modeInput.setHideTrigger(false);
                                    modeInput.enable();
                                } else {
                                    // modeInput.style = 'color:#83878b!important; border: 1px solid #d0d0d0';
                                    modeInput.allowBlank = true;
                                    modeInput.setEmptyText('');
                                    modeInput.style = 'border: 0px solid!important';
                                    modeInput.setHideTrigger(true);
                                    modeInput.disable();
                                }
                            }
                        },
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        padding: 0,
                        ui: 'reordergrid-editor-combo'
                    }
                }, {
                    xtype: 'widgetcolumn',
                    dataIndex: 'columnName',
                    align: 'left',
                    flex: 1,
                    widget: {
                        xtype: 'textfield',
                        itemId: 'newAttributeColumnName',
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        padding: 0,
                        ui: 'reordergrid-editor-combo'
                    }
                }, {
                    xtype: 'widgetcolumn',
                    dataIndex: 'mode',
                    align: 'left',
                    flex: 1,
                    widget: {
                        xtype: 'combo',
                        displayField: 'label',
                        valueField: 'value',
                        queryMode: 'local',
                        itemId: 'newComboMode',
                        editable: false,
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        padding: 0,
                        ui: 'reordergrid-editor-combo',
                        hideTrigger: true,
                        forceSelection: true,
                        allowBlank: false,
                        disabled: true,
                        disabledCls: 'x-item-disabled',
                        bind: {
                            store: '{attributeModesReferenceStore}'
                        }
                    }
                }, {
                    xtype: 'widgetcolumn',
                    dataIndex: 'default',
                    align: 'left',
                    flex: 1,

                    widget: {
                        xtype: 'textfield',
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        itemId: 'newAttributeDefaultValue',
                        ui: 'reordergrid-editor-combo'
                    },
                    renderer: Ext.util.Format.htmlEncode,
                    bind: {
                        hidden: '{!isImport}'
                    }
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnEditNew',
                    // hidden: true,
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-ellipsis-h',
                        disabled: true
                    }]
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnMoveUpNew',
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-ellipsis-h',
                        disabled: true
                    }]
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnMoveDownNew',
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-ellipsis-h',
                        disabled: true
                    }]
                }, {
                    xtype: 'actioncolumn',
                    itemId: 'actionColumnCancelNew',
                    width: 30,
                    minWidth: 30, // width property not works. Use minWidth.
                    maxWidth: 30,
                    align: 'center',
                    items: [{
                        iconCls: 'x-fa fa-plus',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.add, // Add
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.add'
                        },
                        handler: function (button, rowIndex, colIndex) {

                            var attributeName = button.up('grid').down('#selectAttributeForGrid');
                            var columnName = button.up('grid').down('#newAttributeColumnName');
                            var attributeMode = button.up('grid').down('#newComboMode');
                            var defaultValue = button.up('grid').down('#newAttributeDefaultValue');


                            if (!attributeMode.isValid()) {
                                attributeMode.focus();
                                return false;
                            }
                            Ext.suspendLayouts();
                            var mainGrid = button.up('form').down('#importExportAttributeGrid');
                            var vm = button.up('form').getViewModel();
                            var attributeStore = vm.getStore('allSelectedAttributesStore');

                            var newAttribute = CMDBuildUI.model.importexports.Attribute.create({
                                attribute: attributeName.getValue(),
                                columnName: columnName.getValue(),
                                mode: attributeMode.getValue(),
                                default: defaultValue.getValue(),
                                index: attributeStore.getRange().length
                            });

                            attributeStore.add(newAttribute);
                            attributeName.reset();
                            columnName.reset();
                            attributeMode.reset();
                            // attributeMode.setHidden(true);
                            defaultValue.reset();
                            Ext.resumeLayouts();
                            mainGrid.getView().refresh();
                        }
                    }]
                }]
            }]
        };
    },

    getImportCriteriaFieldset: function () {
        var me = this;
        return {
            title: CMDBuildUI.locales.Locales.administration.importexport.texts.importmergecriteria,
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            itemId: 'importfieldset',
            ui: 'administration-formpagination',
            xtype: "fieldset",
            layout: 'column',
            collapsible: true,

            bind: {
                disabled: '{!isImport}',
                collapsed: '{!isImport}'
            },

            items: [
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('importKeyAttribute', {
                        importKeyAttribute: {
                            fieldcontainer: {
                                hidden: true,
                                bind: {
                                    hidden: '{isDomain}'
                                }
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.importkeattribute,
                            allowBlank: false,
                            displayField: 'attribute',
                            valueField: 'attribute',
                            bind: {
                                store: '{allSelectedAttributesStore}',
                                value: '{theImportExportTemplate.importKeyAttribute}'
                            }
                        }
                    })
                ]),
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('mergeMode', {
                        mergeMode: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.missingrecords,
                            allowBlank: false,
                            bind: {
                                value: '{theImportExportTemplate.mergeMode}',
                                store: '{mergeModesStore}'
                            }
                        }
                    })
                ]),


                // TODO
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('mergeMode_when_missing_update_attr', {
                        mergeMode_when_missing_update_attr: {
                            fieldcontainer: {
                                bind: {
                                    hidden: '{!isModifyCard}'
                                }
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.attribute,
                            allowBlank: false,
                            queryMode: 'local',
                            displayField: 'description',
                            valueField: 'name',
                            bind: {
                                store: '{allClassOrDomainsAtributes}',
                                value: '{theImportExportTemplate.mergeMode_when_missing_update_attr}'
                            },
                            listeners: {

                                beforequery: function (queryEv) {
                                    if (this.getStore()) {
                                        this.getStore().rejectChanges();
                                        this.getStore().clearFilter();

                                    }
                                    return true;
                                },
                                change: function (combo, attributename, oldValue) {
                                    if (attributename) {
                                        var container = combo.up('panel').down('#mergeMode_when_missing_update_value_fieldcontainer');
                                        var vm = combo.lookupViewModel();
                                        var allattributes = {};
                                        CMDBuildUI.util.helper.ModelHelper.getModel(vm.get("theImportExportTemplate.targetType"), vm.get("theImportExportTemplate.targetName")).then(function (model) {

                                            model.getFields().forEach(function (field) {
                                                allattributes[field.name] = field;
                                            });
                                            if (attributename && allattributes[attributename]) {
                                                var attribute = allattributes[attributename];


                                                var editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(
                                                    attribute
                                                );
                                                var display = CMDBuildUI.util.helper.FormHelper.getReadOnlyField(
                                                    attribute, editor.recordLinkName
                                                );

                                                var field = {
                                                    itemId: 'mergeMode_when_missing_update_value_display',
                                                    bind: {
                                                        hidden: '{!actions.view}',
                                                        value: '{theImportExportTemplate.mergeMode_when_missing_update_value}'
                                                    }
                                                };
                                                if (editor.metadata.type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase()) {
                                                    vm.bind("{theImportExportTemplate.mergeMode_when_missing_update_value}", function (value) {

                                                        if (value) {
                                                            var lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(editor.metadata.lookupType);
                                                            lt.getLookupValues().then(function (values) {
                                                                var v = values.getById(value);
                                                                vm.set("theImportExportTemplate._mergeMode_when_missing_update_value_description", v.get("description"));
                                                            });
                                                        } else {
                                                            vm.set("theImportExportTemplate._mergeMode_when_missing_update_value_description", "");
                                                        }
                                                    });
                                                    field.renderer = function (value) {
                                                        return value;
                                                    };
                                                    field.bind.value = '{theImportExportTemplate._mergeMode_when_missing_update_value_description}';
                                                }
                                                container.removeAll(true);
                                                container.add([
                                                    Ext.apply({
                                                        itemId: 'mergeMode_when_missing_update_value_input',
                                                        bind: {
                                                            value: '{theImportExportTemplate.mergeMode_when_missing_update_value}',
                                                            hidden: '{actions.view}'
                                                        }
                                                    }, editor),
                                                    Ext.apply(display, field)

                                                ]);
                                            }
                                        });

                                    }
                                }
                            }
                        }
                    }),

                    // TODO
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('mergeMode_when_missing_update_value', {
                        mergeMode_when_missing_update_value: {
                            fieldcontainer: {
                                bind: {
                                    hidden: '{!isModifyCard}'
                                }
                            },
                            noDisplayField: true,
                            htmlEncode: true,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.value,
                            allowBlank: false,
                            bind: {
                                // value: '{theImportExportTemplate.mergeMode_when_missing_update_value}'
                            }
                        }
                    })
                ])
            ]
        };
    },

    getExportCriteriaFieldset: function () {
        return {
            title: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.exportfilter,
            xtype: "fieldset",
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            itemId: 'exportfilterfieldset',
            ui: 'administration-formpagination',
            collapsible: true,
            bind: {
                disabled: '{!isExport}',
                collapsed: '{!isExport}',
                hidden: '{isDomain}'
            },

            items: [{
                xtype: 'fieldcontainer',
                items: [{
                    layout: 'column',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [{
                        xtype: 'fieldcontainer',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters,
                        columnWidth: 1,
                        items: [{
                            xtype: 'components-administration-toolbars-formtoolbar',
                            style: 'border:none; margin-top: 5px',
                            items: [{
                                xtype: 'tool',
                                align: 'right',
                                itemId: 'editFilterBtn',
                                cls: 'administration-tool margin-right5',
                                iconCls: 'cmdbuildicon-filter',
                                tooltip: CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters,
                                autoEl: {
                                    'data-testid': 'administration-searchfilter-tool-removefilterbtn'
                                },
                                bind: {
                                    disabled: '{!theImportExportTemplate.targetName}'
                                }
                            }, {

                                xtype: 'tool',
                                align: 'right',
                                itemId: 'removeFilterBtn',
                                cls: 'administration-tool margin-right5',
                                iconCls: 'cmdbuildicon-filter-remove',
                                tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.deleteBtn.tooltip,
                                autoEl: {
                                    'data-testid': 'administration-searchfilter-tool-removefilterbtn'
                                },
                                bind: {
                                    disabled: '{actions.view}'
                                }
                            }]
                        }]
                    }]
                }]
            }]
        };
    },

    getErrorsManagementFieldset: function () {
        var me = this;
        return {
            title: CMDBuildUI.locales.Locales.administration.importexport.texts.errorsmanagements,

            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",
            layout: 'column',
            collapsible: true,
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('errorEmailTemplate', {
                    errorEmailTemplate: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.erroremailtemplate,
                        allowBlank: false,
                        displayField: 'name',
                        valueField: '_id',
                        bind: {
                            store: '{allEmailTemplates}',
                            value: '{theImportExportTemplate.errorEmailTemplate}'
                        }
                    }
                }),

                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('errorEmailAccount', {
                    errorEmailAccount: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.texts.account,
                        displayField: 'name',
                        valueField: '_id',
                        bind: {
                            store: '{allEmailAccounts}',
                            value: '{theImportExportTemplate.errorEmailAccount}'
                        },
                        triggers: {
                            clear: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.clearValue();
                                }
                            }
                        }
                    }
                })
            ]
        };
    },

    createContainer: function (items, config) {
        var container = Ext.merge({}, {
            xtype: 'container',
            layout: 'column',
            columnWidth: 1,
            items: items
        }, config || {});

        return container;

    },

    privates: {

    }

});