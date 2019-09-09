Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.FormWidgetFieldset', {
    extend: 'Ext.panel.Panel',
    controller: 'administration-content-classes-tabitems-properties-fieldsets-formwidgetfieldset',

    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-formwidgetfieldset',
    viewModel: {
        type: 'administration-content-classes-tabitems-properties-fieldsets-formwidgetfieldset'
    },
    items: [{
        xtype: 'fieldset',
        collapsible: true,
        collapsed: true,
        layout: 'column',
        bind: {
            title: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formWidgets + ' ({formWidgetCount})'
        },
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 1,
            items: [{
                xtype: 'components-grid-reorder-grid',
                bind: {
                    store: '{theObject.widgets}'
                    //  hideHeaders: '{!formWidgetCount}'
                },
                columnWidth: 0.5,
                reference: 'formWidgetGrid',
                flex: 1,
                viewConfig: {
                    markDirty: false
                },
                columns: [{
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.widgetname, // Widget Name
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.widgetname'
                    },
                    align: 'left',
                    dataIndex: '_label'
                }, {
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.guicustom, // GUI custom
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.guicustom'
                    },
                    align: 'left',
                    dataIndex: '_type',
                    renderer: function (value, checkbox, record) {
                        if (value) {
                            var store = Ext.getStore('administration.common.WidgetTypes');
                            var index = store.findExact('value', value);
                            return store.getData().items[index].get('label');
                        }
                        return '';
                    }
                }, {
                    flex: 2,
                    text: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.guicustomparameters, // GUI custom
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.guicustomparameters'
                    },
                    xtype: 'widgetcolumn',
                    align: 'left',
                    widget: {
                        xtype: 'aceeditortextarea',
                        inputField: '_config',
                        vmObjectName: 'record',
                        config: {
                            options: {
                                readOnly: true
                            }
                        },
                        theMainObject: 'theWidget',
                        bind: {
                            value: '{record._config}'
                        }
                    },
                    // called when the widget is initially instantiated
                    // on the widget column
                    onWidgetAttach: function (col, widget, rec) {
                        // put the widget inside record for later use in controller
                        // on edit btn click event. Needed for value change in widget.
                        rec.widget = widget;
                    }
                }, {
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.common.labels.status,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.labels.status'
                    },
                    xtype: 'widgetcolumn',
                    align: 'left',
                    dataIndex: '_active',
                    widget: {
                        xtype: 'checkbox', // textfield | combo | radio
                        bind: '{record._active}',
                        boxLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                        localized: {
                            boxLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                        },
                        readOnly: true
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
                        iconCls: 'x-fa fa-pencil',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.tooltips.edittrigger,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.tooltips.edittrigger'
                        },
                        handler: 'onEditBtn'
                    }, {
                        iconCls: 'x-fa fa-arrow-up',
                        tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.actions.moveUp.tooltip,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.actions.moveUp.tooltip'
                        },
                        handler: 'moveUp',
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            return rowIndex === 0;
                        }
                    }, {
                        iconCls: 'x-fa fa-arrow-down',
                        tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.actions.moveDown.tooltip,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.actions.moveDown.tooltip'
                        },
                        handler: 'moveDown',
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            return rowIndex >= view.store.getCount() - 1;
                        }
                    }, {
                        iconCls: 'x-fa fa-times',
                        tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.actions.deleteTrigger.tooltip,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.actions.deleteTrigger.tooltip'
                        },
                        handler: 'deleteRow'
                    }]
                }]
            }, {
                columnWidth: 1,
                items: [{

                    xtype: 'components-grid-reorder-grid',
                    bind: {
                        store: '{formWidgetsStoreNew}',
                        hidden: '{!actions.edit}'
                    },
                    columnWidth: 0.5,
                    flex: 1,
                    viewConfig: {
                        markDirty: false
                    },
                    columns: [{
                        flex: 1,
                        text: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.createnewwidget,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.createnewwidget'
                        },
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: '_label',
                        widget: {
                            xtype: 'textfield',
                            label: '',
                            inputField: '_label',
                            bind: {
                                value: '{record._label}'
                            },
                            listeners: {
                                change: function (element, newValue, oldValue) {
                                    element.up('administration-content-classes-tabitems-properties-fieldsets-formwidgetfieldset').getViewModel().getParent().get('formWidgetsStoreNew').getData().items[0].set('_label', newValue);

                                }
                            }
                        }
                    }, {
                        flex: 1,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: '_type',
                        widget: {
                            xtype: 'combobox',
                            inputField: '_type',
                            editable: false,
                            forceSelection: true,
                            allowBlank: false,
                            displayField: 'label',
                            valueField: 'value',
                            store: 'administration.common.WidgetTypes',
                            bind: {
                                value: '{record._type}'
                            },
                            listeners: {
                                change: function (element, newValue, oldValue) {
                                    element.up('administration-content-classes-tabitems-properties-fieldsets-formwidgetfieldset').getViewModel().getParent().get('formWidgetsStoreNew').getData().items[0].set('_type', newValue);
                                }
                            }
                        }
                    },
                    {
                        flex: 2,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        widget: {
                            xtype: 'component',
                            html: '<div id="newFormWidgetScriptField" style="min-height:58px;height:100%;min-width:20px; width:100%"></div>',
                            listeners: {
                                afterrender: function (cmp) {
                                    var me = this;
                                    var editor = window.newFormWidgetScriptField = ace.edit('newFormWidgetScriptField');

                                    //set the theme
                                    //
                                    editor.setTheme('ace/theme/chrome');

                                    //set the mode
                                    //
                                    editor.getSession().setMode('ace/mode/javascript');

                                    //set some options
                                    //
                                    editor.setOptions({
                                        showLineNumbers: true,
                                        showPrintMargin: false
                                    });

                                    //set a value
                                    //
                                    editor.setValue('');

                                    editor.getSession().on('change', function (event, _editor) {
                                        var vm = me.up('administration-content-classes-tabitems-properties-fieldsets-formwidgetfieldset').getViewModel().getParent();
                                        vm.get('formWidgetsStoreNew').getData().items[0].set('_config', _editor.getValue());
                                    });
                                }
                            }
                        }
                    },
                    {
                        flex: 1,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: '_active',
                        widget: {
                            xtype: 'checkbox', // textfield | combo | radio
                            bind: '{record._active}',
                            boxLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                            localized: {
                                boxLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                            }
                        }
                    },
                    {
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
                            localized: {
                                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.add'
                            },
                            handler: 'onAddNewWidgetMenuBtn'
                        }]
                    }
                    ]
                }]
            }]
        }]
    }]
});