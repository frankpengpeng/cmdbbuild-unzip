
Ext.define('CMDBuildUI.view.importexport.Import', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.importexport.ImportController',
        'CMDBuildUI.view.importexport.ImportModel'
    ],

    alias: 'widget.importexport-import',
    controller: 'importexport-import',
    viewModel: {
        type: 'importexport-import'
    },

    config: {
        /**
         * @cfg {CMDBuildUI.model.importexports.Template []}
         * Allowed templates for data import.
         */
        templates: [],

        /**
         * @cfg {CMDBuildUI.model.classes.Class}
         * Class instance
         */
        object: null
    },

    publish: [
        'templates'
    ],

    twoWayBindable: [
        'templates'
    ],

    bind: {
        'templates': '{templatesList}'
    },
    scrollable: true,

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,

    items: [{
        xtype: 'combobox',
        fieldLabel: CMDBuildUI.locales.Locales.importexport.template,
        allowNull: false,
        valueField: '_id',
        displayField: 'description',
        bind: {
            store: '{templates}',
            value: '{values.template}',
            disabled: '{disabled.template}'
        }
    }, {
        xtype: 'filefield',
        reference: 'filefield',
        name: 'file',
        fieldLabel: CMDBuildUI.locales.Locales.attachments.file,
        allowBlank: false,
        anchor: '100%',
        bind: {
            disabled: '{disabled.file}'
        },
        autoEl: {
            'data-testid': 'attachmentform-file'
        }
    }, {
        xtype: 'formpaginationfieldset',
        title: 'Template definition',
        reference: 'templatedefinition',
        hidden: true,
        collapsible: true,
        bind: {
            hidden: '{!values.template}'
        },
        items: [{
            xtype: 'grid',
            sortableColumns: false,
            enableColumnHide: false,
            enableColumnMove: false,
            enableColumnResize: false,

            selModel: {
                pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
            },

            forceFit: true,
            loadMask: true,

            columns: [{
                flex: 1,
                text: CMDBuildUI.locales.Locales.administration.attributes.attribute,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.attributes.attribute'
                },
                dataIndex: 'attribute',
                align: 'left',
                renderer: function(value, metadata, record, rowindex, colindex, store, view) {
                    var f = view.lookupViewModel().get("classmodel").getField(value);
                    if (f) {
                        return f.attributeconf.description_localized;
                    }
                    return value;
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.importexport.texts.columnname,
                flex: 1,
                dataIndex: 'columnName',
                align: 'left'
            }, {
                text: CMDBuildUI.locales.Locales.administration.importexport.texts.mode,
                flex: 1,
                dataIndex: 'mode',
                align: 'left'
            }, {
                text: CMDBuildUI.locales.Locales.administration.importexport.texts.default,
                flex: 1,
                dataIndex: 'default',
                align: 'left'
            }],

            columnWidth: 1,
            autoEl: {
                'data-testid': 'importexport-import-columns-grid'
            },
            bind: {
                store: '{columns}'
            }
        }, {
            xtype: 'fieldcontainer',
            layout: 'column',
            defaults: {
                xtype: 'displayfield',
                columnWidth: 0.5,
                layout: 'anchor',
                labelAlign: "left"
            },
            items: [{
                fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.importkeattribute,
                labelWidth: 150,
                labelSeparator: ':',
                fieldBodyCls: 'field-with-top-margin',
                bind: {
                    value: '{labels.importKeyAttribute}'
                }
            }, {
                fieldLabel: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.missingrecords,
                labelWidth: 150,
                labelSeparator: ':',
                fieldBodyCls: 'field-with-top-margin',
                bind: {
                    value: '{labels.mergeMode}'
                }
            }]
        }]
    }, {
        xtype: 'fieldset',
        title: CMDBuildUI.locales.Locales.importexport.importresponse,
        hidden: true,
        ui: 'formpagination',
        bind: {
            hidden: '{hidden.response}'
        },
        items: [{
            xtype: 'panel',
            padding: '0 0 15px 0',
            bind: {
                html: '{responseText}'
            }
        }, {
            xtype: 'grid',
            title: CMDBuildUI.locales.Locales.importexport.response.errors,
            localized: {
                title: 'CMDBuildUI.locales.Locales.importexport.response.errors'
            },
            cls: 'import-errors-grid',
            hidden: true,
            forceFit: true,
            scrollable: true,
            columns: [{
                text: CMDBuildUI.locales.Locales.importexport.response.recordnumber,
                dataIndex: 'recordNumber',
                align: 'left',
                menuDisabled: true,
                sortable: false,
                minWidth: 150
            }, {
                text: CMDBuildUI.locales.Locales.importexport.response.linenumber,
                dataIndex: 'lineNumber',
                align: 'left',
                menuDisabled: true,
                sortable: false,
                minWidth: 150
            }, {
                text: CMDBuildUI.locales.Locales.importexport.response.message,
                dataIndex: 'message',
                flex: 1,
                align: 'left',
                menuDisabled: true,
                sortable: false
            }],
            bind: {
                store: '{errors}',
                hidden: '{!response.hasErrors}'
            },
            plugins: [{
                pluginId: 'forminrowwidget',
                ptype: 'forminrowwidget',
                id: 'forminrowwidget',
                expandOnDblClick: true,
                removeWidgetOnCollapse: true,
                widget: {
                    xtype: 'container',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    html: '',
                    listeners: {
                        beforerender: 'onBeforePanelRender'
                    }
                }
            }]
        }]
    }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.importexport.import,
        formBind: true,
        ui: 'management-action',
        itemId: 'importbtn',
        bind: {
            hidden: '{hidden.importbtn}',
            disabled: '{disabled.importbtn}'
        },
        autoEl: {
            'data-testid': 'importexport-import-importbtn'
        }
    }, {
        text: CMDBuildUI.locales.Locales.importexport.downloadreport,
        formBind: true,
        ui: 'management-action',
        itemId: 'downloadreportbtn',
        hidden: true,
        bind: {
            hidden: '{hidden.downloadreportbtn}',
            href: '{downloadreportsrc}'
        }
    }, {
        text: CMDBuildUI.locales.Locales.importexport.sendreport,
        formBind: true,
        ui: 'management-action',
        itemId: 'sendreportbtn',
        hidden: true,
        bind: {
            hidden: '{hidden.sendreportbtn}'
        },
        autoEl: {
            'data-testid': 'importexport-import-sendreportbtn'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.close,
        ui: 'secondary-action',
        itemId: 'closebtn',
        bind: {
            disabled: '{disabled.closebtn}'
        },
        autoEl: {
            'data-testid': 'importexport-import-closebtn'
        }
    }]
});
