Ext.define('CMDBuildUI.view.administration.content.localizations.imports.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.localizations.imports.ViewController',
        'CMDBuildUI.view.administration.content.localizations.imports.ViewModel'
    ],

    alias: 'widget.administration-content-localizations-imports-view',
    controller: 'administration-content-localizations-imports-view',
    viewModel: {
        type: 'administration-content-localizations-imports-view'
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    items: [{
        ui: 'administration-formpagination',
        defaults: {
            padding: '0 15 10 15',
            layout: 'fit'
        },
        items: [{
                items: [{
                    anchor: '100%',
                    value: 'CSV',
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.format,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.format'
                    },
                    name: 'localizationImportFormat',
                    id: 'localizationImportFormat',
                    bind: {
                        store: '{formatsStore}'
                    },
                    disabled: true,
                    displayField: 'label',
                    valueField: 'value',
                    width: '50%',
                    autoEl: {
                        'data-testid': 'administration-content-localizations-imports-view-formatCombobox'
                    }
                }]
            },
            {
                items: [{
                    anchor: '100%',
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.separator,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.separator'
                    },
                    name: 'localizationImportSeparator',
                    id: 'localizationImportSeparator',
                    bind: {
                        store: '{separatorsStore}'
                    },
                    forceSelection: true,
                    queryMode: 'local',
                    displayField: 'label',
                    valueField: 'value',
                    width: '50%',
                    autoEl: {
                        'data-testid': 'administration-content-localizations-imports-view-separatorCombobox'
                    }
                }]
            },
            {
                items: [{
                    anchor: '100%',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.file,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.file'
                    },
                    xtype: 'filefield',
                    width: '50%',
                    accept: '.csv',
                    itemId: 'addfileattachment',
                    ui: 'secondary-action',
                    autoEl: {
                        'data-testid': 'administration-content-localizations-imports-view-fileFilefield'
                    }
                }]
            }
        ]
    }],
    buttons: [{
        text: CMDBuildUI.locales.Locales.administration.localizations.import,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.localizations.import'
        },
        reference: 'importBtn',
        itemId: 'importBtn',
        ui: 'administration-action-small',
        autoEl: {
            'data-testid': 'administration-content-localizations-imports-view-importBtn'
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.localizations.cancel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.localizations.cancel'
        },
        reference: 'cancelBtn',
        itemId: 'cancelBtn',
        ui: 'administration-secondary-action-small',
        autoEl: {
            'data-testid': 'administration-content-localizations-imports-view-cancelBtn'
        }
    }],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.localizations.import);
        this.callParent(arguments);
    }
});