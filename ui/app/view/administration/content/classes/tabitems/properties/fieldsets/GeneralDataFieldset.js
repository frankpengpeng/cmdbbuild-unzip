Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.GeneralDataFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-generaldatafieldset',

    viewModel: {

    },
    ui: 'administration-formpagination',

    items: [{
        xtype: 'fieldset',
        layout: 'column',
        elementId: 'calssgeneralproperties-fieldset',
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 0.5,
            items: [{
                /********************* Name **********************/
                // create
                xtype: 'textfield',
                vtype: 'nameInputValidation',
                reference: 'classnamefieldadd',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.name.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.name.label'
                },
                name: 'classnamefieldadd',
                enforceMaxLength: true,
                allowBlank: false,
                maxLength: 20,
                tabIndex: 1,
                hidden: true,
                bind: {
                    value: '{theObject.name}',
                    hidden: '{actions.view}',
                    disabled: '{actions.edit}'
                },
                listeners: {
                    change: function (input, newVal, oldVal) {
                        CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(input, newVal, oldVal, '[name="description"]');
                    }
                }
            }, {
                // view
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.name.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.name.label'
                },
                reference: 'classnamefieldview',
                hidden: true,
                tabIndex: 0,
                bind: {
                    value: '{theObject.name}',
                    hidden: '{!actions.view}'
                }
            }, {
                /********************* parent **********************/
                // create
                xtype: 'combobox',
                tabIndex: 2,
                editable: false,
                reference: 'parentField',
                displayField: 'description',
                valueField: 'name',
                name: 'parent',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.parent.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.parent.label'
                },
                hidden: true,
                bind: {
                    store: '{superclassesStore}',
                    value: '{theObject.parent}',
                    hidden: '{hideParentCombobox}'
                }
            }, {
                // view
                xtype: 'displayfield',
                tabIndex: 2,
                name: 'parent',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.parent.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.parent.label'
                },
                hidden: true,
                bind: {
                    value: '{parentDescription}',
                    hidden: '{hideParentDisplayfield}'
                }
            }, {
                // edit
                xtype: 'textfield',
                tabIndex: 2,
                name: 'parent',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.parent.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.parent.label'
                },
                hidden: true,
                disabled: true,
                bind: {
                    value: '{parentDescription}',
                    hidden: '{hideParentDisabledfield}'
                }
            }, {
                /********************* Class type **********************/
                // create
                xtype: 'combobox',
                tabIndex: 4,
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.classType.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.classType.label'
                },
                name: 'type',
                hidden: true,
                bind: {
                    store: '{classTypeStore}',
                    value: '{theObject.type}',
                    hidden: '{actions.view}',
                    disabled: '{actions.edit}'
                },
                listeners: {
                    change: function (combobox, newValue, oldValue, eOpts) {
                        var vm = combobox.up('administration-content-classes-tabitems-properties-fieldsets-generaldatafieldset').getViewModel().getParent();
                        if (oldValue !== null) {
                            if (newValue === 'standard') {
                                vm.set('theObject.parent', 'Class');
                            } else {
                                vm.set('theObject.parent', '');
                            }
                        }
                    }
                }
            }, {
                // view 
                xtype: 'displayfield',
                tabIndex: 4,
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.classType.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.classType.label'
                },
                name: 'type',
                hidden: true,
                bind: {
                    value: '{theObject.type}',
                    hidden: '{!actions.view}'
                },
                renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                    switch (value) {
                        case 'standard':
                            return CMDBuildUI.locales.Locales.administration.classes.texts.standard; // Standard
                        case 'simple':
                            return CMDBuildUI.locales.Locales.administration.classes.texts.simple; // Simple
                    }
                }
            }, {
                /********************* Active **********************/
                // create / edit / view
                xtype: 'checkbox',
                tabIndex: 4,
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active, // Active
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                },
                name: 'active',
                hidden: true,
                bind: {
                    value: '{theObject.active}',
                    readOnly: '{actions.view}',
                    hidden: '{!theObject}'
                }
            }]
        }, {
            columnWidth: 0.5,
            style: {
                paddingLeft: '15px'
            },
            items: [
                /********************* description **********************/
                {
                    // create
                    xtype: 'textfield',
                    tabIndex: 1,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description, // Description
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                    },
                    allowBlank: false,
                    name: 'description',
                    hidden: true,
                    bind: {
                        value: '{theObject.description}',
                        hidden: '{!actions.add}'
                    },

                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClick'
                }, {
                    // view
                    xtype: 'displayfield',
                    tabIndex: 1,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description, // Description
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                    },
                    name: 'description',
                    hidden: true,
                    bind: {
                        value: '{theObject.description}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    //edit
                    xtype: 'textfield',
                    tabIndex: 1,
                    name: 'description',
                    bind: {
                        value: '{theObject.description}',
                        hidden: '{!actions.edit}'
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description',
                        labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                    },
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClick'
                }, {
                    /********************* Superclass **********************/
                    // create / edit / view
                    xtype: 'checkbox',
                    tabIndex: 3,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.superclass, // Superclass
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.superclass'
                    },
                    name: 'prototype',
                    hidden: true,
                    bind: {
                        value: '{theObject.prototype}',
                        readOnly: '{actions.view}',
                        disabled: '{actions.edit}',
                        hidden: '{isSimpleClass}'
                    }
                }, {
                    /********************* Active **********************/
                    // create / edit / view
                    xtype: 'checkbox',
                    tabIndex: 4,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetail, // Master detail
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetail'
                    },
                    name: 'active',
                    hidden: true,
                    bind: {
                        value: '{theObject.isMasterDetail}',
                        readOnly: '{actions.view}',
                        hidden: '{!isSimpleClass}'
                    }
                }, {
                    /********************* Multitenant mode **********************/
                    xtype: 'fieldcontainer',
                    tabIndex: 5,
                    flex: 1,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.multitenantmode, // Multitenant mode
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.multitenantmode'
                    },
                    bind: {
                        hidden: '{!isMultitenant}'
                    },
                    items: [{
                        // edit / add
                        xtype: 'combobox',
                        name: 'multitenantMode',
                        displayField: 'label',
                        valueField: 'value',
                        forceSelection: true,
                        bind: {
                            value: '{theObject.multitenantMode}',
                            hidden: '{isMultitenatModeHiddenCombo}',
                            store: '{multitenantModeStore}'
                        }
                    }, {
                        // view
                        xtype: 'displayfield',
                        name: 'multitenantMode',
                        hidden: true,
                        bind: {
                            value: '{theObject.multitenantMode}',
                            hidden: '{isMultitenatModeHiddenDisplay}'
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
                }
            ]
        }]
    }]
});