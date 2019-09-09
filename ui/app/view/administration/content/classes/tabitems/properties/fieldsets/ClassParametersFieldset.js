Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.ClassParamentersFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-classparametersfieldset',

    items: [{
        xtype: 'fieldset',
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.classParameters, // Class Parameters
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.classParameters'
        },
        ui: 'administration-formpagination',
        collapsible: true,
        items: [{
            columnWidth: 1,
            items: [{
                width: '50%',
                items: [{
                    // create / edit 
                    xtype: 'combobox',
                    queryMode: 'local',
                    displayField: 'description',
                    valueField: '_id',
                    name: 'defaultFilter',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.defaultfilter, // Default filter
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.defaultfilter'
                    },
                    allowBlank: true,
                    hidden: true,
                    bind: {
                        store: '{defaultFilterStore}',
                        value: '{theObject.defaultFilter}',
                        hidden: '{actions.view}'
                    }
                }, {
                    // view
                    xtype: 'displayfield',
                    name: 'defaultFilter',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.defaultfilter, // Default filter
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.defaultfilter'
                    },
                    hidden: true,
                    bind: {
                        value: '{theObject.defaultFilter}',
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value) {                        
                        var defaultFilterStore = Ext.getStore('searchfilters.Searchfilters');
                        if (defaultFilterStore) {
                            var record = defaultFilterStore.findRecord('_id', value);
                            if (record) {
                                return record.get('description');
                            }
                        }
                        return value;
                    }
                }]
            }]
        }, {
            columnWidth: 0.5,
            bind: {
                hidden: '{actions.add}'
            },
            items: [{
                // edit 
                xtype: 'combobox',
                queryMode: 'local',
                displayField: 'description',
                valueField: 'code',
                name: 'defaultimporttemplate',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultimporttemplate, // Default template for data import
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultimporttemplate'
                },
                allowBlank: true,
                hidden: true,
                bind: {
                    store: '{defaultImportTemplateStore}',
                    value: '{theObject.defaultImportTemplate}',
                    hidden: '{actions.view}'
                }
            }, {
                // view
                xtype: 'displayfield',
                name: 'defaultimporttemplate',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultimporttemplate, // Default template for data import
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultimporttemplate'
                },
                hidden: true,
                bind: {
                    value: '{theObject.defaultImportTemplate}',
                    hidden: '{!actions.view}'
                }
            }]
        }, {
            columnWidth: 0.5,
            bind: {
                hidden: '{actions.add}'
            },
            style: {
                paddingLeft: '15px'
            },
            items: [{
                // edit 
                xtype: 'combobox',
                queryMode: 'local',
                displayField: 'description',
                valueField: 'code',
                name: 'defaultexporttemplate',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultexporttemplate, // Default template for data export
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultexporttemplate'
                },
                allowBlank: true,
                hidden: true,
                bind: {
                    store: '{defaultExportTemplateStore}',
                    value: '{theObject.defaultExportTemplate}',
                    hidden: '{actions.view}'
                }
            }, {
                // view
                xtype: 'displayfield',
                name: 'defaultexporttemplate',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultexporttemplate, // Default template for data export
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.defaultexporttemplate'
                },
                hidden: true,
                bind: {
                    value: '{theObject.defaultExportTemplate}',
                    hidden: '{!actions.view}'
                }
            }]
        }, {
            columnWidth: 0.5,
            items: [{
                /********************* Inline notes **********************/
                // create / edit / view
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.noteinline, // Inline notes
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.noteinline'
                },
                name: 'noteinline',
                hidden: true,
                bind: {
                    value: '{theObject.noteInline}',
                    readOnly: '{actions.view}',
                    hidden: '{!theObject}'
                }
            }]
        }, {
            columnWidth: 0.5,
            style: {
                paddingLeft: '15px'
            },
            items: [{
                /********************* Closed inline notes **********************/
                // create / edit / view
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.noteinlineclosed, // Closed inline notes
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.noteinlineclosed'
                },
                name: 'noteinlineclosed',
                hidden: true,
                bind: {
                    value: '{theObject.noteInlineClosed}',
                    readOnly: '{actions.view}',
                    hidden: '{!theObject}',
                    disabled: '{checkboxNoteInlineClosed.disabled}'
                }
            }]
        }]
    }]
});
