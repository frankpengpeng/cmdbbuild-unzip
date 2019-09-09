Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.GeneralDataFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-generaldatafieldset',
    viewModel: {},
    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties, // General Properties
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        items: [{
            columnWidth: 0.5,
            items: [{
                    /********************* Name **********************/
                    // create / edit
                    xtype: 'textfield',
                    vtype: 'nameInputValidation',
                    name: 'processnamefieldadd',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.name.label,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.name.label'
                    },
                    allowBlank: false,
                    enforceMaxLength: true,
                    maxLength: 20,
                    hidden: true,
                    bind: {
                        value: '{theProcess.name}',
                        hidden: '{!actions.add}'
                    },
                    listeners: {
                        change:  function (input, newVal, oldVal) {
                            CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(input, newVal, oldVal,'[name="description"]');
                        }
                    }
                }, {
                    // edit
                    xtype: 'textfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.name.label,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.name.label'
                    },
                    name: 'name',
                    hidden: true,
                    disabled: true,
                    bind: {
                        value: '{theProcess.name}',
                        hidden: '{!actions.edit}'
                    },
                    listeners: {
                        change:  function (input, newVal, oldVal) {
                            CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(input, newVal, oldVal,'[name="description"]');
                        }
                    }
                }, {
                    // view
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.name.label,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.name.label'
                    },
                    name: 'name',
                    hidden: true,
                    bind: {
                        value: '{theProcess.name}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    /********************* description **********************/
                    // create / edit
                    xtype: 'combobox',
                    editable: false,
                    reference: 'parentField',
                    displayField: 'description',
                    valueField: 'name',
                    name: 'parent',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.parent.label,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.parent.label'
                    },
                    hidden: true,
                    bind: {
                        store: '{superprocessesStore}',
                        value: '{theProcess.parent}',
                        hidden: '{hideParentCombobox}',
                        disabled: '{actions.edit}'
                    },
                    renderer: function(value){
                        if(value === 'Class'){
                            return '';
                        }
                        return  value;
                    }
                }, {
                    // view
                    xtype: 'displayfield',
                    name: 'parent',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.parent.label,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.parent.label'
                    },
                    hidden: true,
                    bind: {
                        value: '{theProcess.parent}',
                        hidden: '{hideParentDisplayfield}'
                    }
                },
                // {
                //     /********************* Process type **********************/
                //     // create
                //     xtype: 'combobox',
                //     queryMode: 'local',
                //     displayField: 'label',
                //     valueField: 'value',
                //     fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.parent.label,
                //     name: 'type',
                //     hidden: true,
                //     bind: {
                //         store: '{processParentStore}',
                //         value: '{theProcess.type}',
                //         hidden: '{!actions.add}'
                //     }
                // }, {
                //     // view
                //     xtype: 'displayfield',
                //     fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.parent.label,
                //     name: 'parent',
                //     hidden: true,
                //     bind: {
                //         value: '{theProcess.parent}',
                //         hidden: '{actions.add}'
                //     }
                // },
                {
                    /********************* stoppableByUser **********************/
                    // create / edit / view
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.stoppableByUser.label,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.stoppableByUser.label'
                    },
                    name: 'stoppableByUser',
                    hidden: true,
                    bind: {
                        value: '{theProcess.stoppableByUser}',
                        readOnly: '{actions.view}',
                        hidden: '{!theProcess}'
                    }
                }, {
                    /********************* Active **********************/
                    // create / edit / view
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active, // Active
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'active',
                    hidden: true,
                    bind: {
                        value: '{theProcess.active}',
                        readOnly: '{actions.view}',
                        hidden: '{!theProcess}'
                    }
                }
            ]
        }, {
            columnWidth: 0.5,
            style: {
                paddingLeft: '15px'
            },
            items: [{
                /********************* description **********************/
                // create
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description, // Description
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                },
                name: 'description',
                allowBlank: false,
                hidden: true,
                bind: {
                    value: '{theProcess.description}',
                    hidden: '{!actions.add}'
                },
                labelToolIconCls: 'fa-flag',
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                labelToolIconClick: 'onTranslateClick'
            }, {
                // edit
                columnWidth: 0.5,
                xtype: 'textfield',
                name: 'description',
                bind: {
                    value: '{theProcess.description}',
                    hidden: '{!actions.edit}'
                },
                allowBlank: false,
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description, // Description
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description',
                    labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                },
                labelToolIconCls: 'fa-flag',
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                labelToolIconClick: 'onTranslateClick'
            }, {
                // view
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description, // Description
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                },
                name: 'description',
                hidden: true,
                bind: {
                    value: '{theProcess.description}',
                    hidden: '{!actions.view}'
                }
            }, {
                /********************* Superclass **********************/
                // create / edit / view
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.superclass, // Superclass
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.superclass'
                },
                name: 'prototype',
                hidden: true,
                bind: {
                    value: '{theProcess.prototype}',
                    disabled: '{actions.edit}',
                    readOnly: '{actions.view}',
                    hidden: '{!theProcess}'
                }
            }, {
                /********************* enableSaveButton **********************/
                // create / edit / view
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.enableSaveButton.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.generalData.inputs.enableSaveButton.label'
                },
                name: 'hideSaveButton',
                hidden: true,
                bind: {
                    hidden: '{!theProcess}',
                    value: '{theProcess.hideSaveButton}',
                    readOnly: '{actions.view}'

                },
                listeners: {
                    change: function (checkbox, newValue, oldValue, eOpts) {
                        this.lookupViewModel().get('theProcess').set('enableSaveButton', !newValue);
                    }
                }
            }, {
                /********************* Multitenant mode **********************/
                xtype: 'fieldcontainer',
                flex: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.multitenantmode, // Multitenant mode
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.multitenantmode'
                },
                hidden: true,
                bind: {
                    hidden: '{!isMultitenant}'
                },
                items: [{
                    // edit / add
                    xtype: 'combobox',
                    name: 'multitenantMode',
                    typeAhead: true,
                    queryMode: 'local',
                    displayField: 'label',
                    valueField: 'value',
                    bind: {
                        value: '{theProcess.multitenantMode}',
                        hidden: '{actions.view}',
                        store: '{multitenantModeStore}'
                    }
                }, {
                    // view
                    xtype: 'displayfield',
                    name: 'multitenantMode',
                    hidden: true,
                    bind: {
                        value: '{theProcess.multitenantMode}',
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value) {
                        if (value) {
                            var store = this.up('fieldcontainer').down('combo').getStore();
                            var record = store.findRecord('value', value);
                            if (record) {
                                return record.get('label');
                            }
                        }
                        return value;
                    }
                }]
            }]
        }]
    }]
});