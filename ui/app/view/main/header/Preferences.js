Ext.define('CMDBuildUI.view.main.header.Preferences', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.main.header.PreferencesController',
        'CMDBuildUI.view.main.header.PreferencesModel'
    ],

    alias: 'widget.main-header-preferences',
    controller: 'main-header-preferences',
    viewModel: {
        type: 'main-header-preferences'
    },

    scrollable: true,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    items: [{
        xtype: 'fieldset',
        ui: 'formpagination',
        title: CMDBuildUI.locales.Locales.main.baseconfiguration,
        localized:{
            title: 'CMDBuildUI.locales.Locales.main.baseconfiguration'
        },
        items: [{
            xtype: 'container',
            layout: 'column',
            items: [{
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                flex: '0.5',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                layout: 'anchor',
                items: [{
                    // Language
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labellanguage,
                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                    tabIndex: 1,
                    displayField: 'description',
                    valueField: 'code',
                    forceSelection: true,
                    editable: false,
                    autoSelect: false,
                    triggers: {
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: function () {
                                this.clearValue();
                            }
                        }
                    },
                    bind: {
                        store: '{languages}',
                        value: '{values.language}'
                    },
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labellanguage'
                    }
                }]
            }]
        }, {
            xtype: 'container',
            layout: 'column',
            items: [{
                // Date format
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                flex: '0.5',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                layout: 'anchor',
                items: [{
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldateformat,
                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                    tabIndex: 2,
                    displayField: 'label',
                    valueField: 'value',
                    forceSelection: true,
                    editable: false,
                    autoSelect: false,
                    triggers: {
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: function () {
                                this.clearValue();
                            }
                        }
                    },
                    bind: {
                        store: '{dateFormats}',
                        value: '{values.dateFormat}'
                    },
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeldateformat'
                    }
                }, {
                    // Time format
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.timezone,
                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                    tabIndex: 4,
                    displayField: 'description',
                    valueField: '_id',
                    forceSelection: true,
                    editable: false,
                    autoSelect: false,
                    triggers: {
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: function () {
                                this.clearValue();
                            }
                        }
                    },
                    bind: {
                        store: '{timezones}',
                        value: '{values.timezone}'
                    },
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.timezone'
                    }
                }]
            }, {
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                flex: '0.5',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                layout: 'anchor',
                items: [{
                    // Time format
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeltimeformat,
                    emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                    tabIndex: 3,
                    displayField: 'label',
                    valueField: 'value',
                    forceSelection: true,
                    editable: false,
                    autoSelect: false,
                    triggers: {
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: function () {
                                this.clearValue();
                            }
                        }
                    },
                    bind: {
                        store: '{timeFormats}',
                        value: '{values.timeFormat}'
                    },
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeltimeformat'
                    }
                }]
            }]
        }, {
            xtype: 'container',
            layout: 'column',
            items: [{
                    // Date format
                    xtype: 'fieldcontainer',
                    columnWidth: 0.5,
                    flex: '0.5',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    layout: 'anchor',
                    items: [{
                        // Decimals separator
                        xtype: 'combobox',
                        fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator,
                        emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                        tabIndex: 5,
                        displayField: 'label',
                        valueField: 'value',
                        forceSelection: true,
                        editable: false,
                        autoSelect: false,
                        triggers: {
                            clear: {
                                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                handler: function () {
                                    this.clearValue();
                                }
                            }
                        },
                        bind: {
                            store: '{decimalsSeparators}',
                            value: '{values.decimalsSeparator}',
                            validation: '{validations.decimalsSeparator}'
                        },
                        localized: {
                            emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                            fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator'
                        },
                        validator: function () {
                            this.lookupViewModel().set("values.decimalsSeparator", this.getValue());
                            return true;
                        }
                    }]
                },
                {
                    xtype: 'fieldcontainer',
                    columnWidth: 0.5,
                    flex: '0.5',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    layout: 'anchor',
                    items: [{
                        // Thousands separator
                        xtype: 'combobox',
                        fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labelthousandsseparator,
                        emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                        tabIndex: 6,
                        displayField: 'label',
                        valueField: 'value',
                        forceSelection: true,
                        editable: false,
                        autoSelect: false,
                        triggers: {
                            clear: {
                                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                handler: function () {
                                    this.clearValue();
                                }
                            }
                        },
                        bind: {
                            store: '{thousandsSeparators}',
                            value: '{values.thousandsSeparator}',
                            validation: '{validations.thousandsSeparator}'
                        },
                        localized: {
                            emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                            fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labelthousandsseparator'
                        },
                        validator: function () {
                            this.lookupViewModel().set("values.thousandsSeparator", this.getValue());
                            return true;
                        }
                    }]
                }
            ]
        }, 
        {
            xtype: 'container',
            layout: 'column',
            items: [{
                    // Date format
                    xtype: 'fieldcontainer',
                    columnWidth: 0.5,
                    flex: '0.5',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    layout: 'anchor',
                    items: [{
                        // Decimals separator
                        xtype: 'combobox',
                        fieldLabel: CMDBuildUI.locales.Locales.main.preferences.preferredofficesuite,
                        emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                        tabIndex: 7,
                        displayField: 'label',
                        valueField: 'value',
                        forceSelection: true,
                        editable: false,
                        autoSelect: false,
                        triggers: {
                            clear: {
                                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                handler: function () {
                                    this.clearValue();
                                }
                            }
                        },
                        bind: {
                            store: '{preferredOfficeSuite}',
                            value: '{values.preferredOfficeSuite}',
                            validation: '{validations.preferredOfficeSuite}'
                        },
                        localized: {
                            emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue',
                            fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.preferredofficesuite'
                        },
                        validator: function () {
                            this.lookupViewModel().set("values.preferredOfficeSuite", this.getValue());
                            return true;
                        }
                    }]
                }]
        }
    ]
    }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        itemId: 'savebtn',
        ui: 'management-action-small',
        autoEl: {
            'data-testid': 'main-header-preferences-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        itemId: 'cancelbtn',
        ui: 'secondary-action-small',
        autoEl: {
            'data-testid': 'main-header-preferences-cancel'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }]
});