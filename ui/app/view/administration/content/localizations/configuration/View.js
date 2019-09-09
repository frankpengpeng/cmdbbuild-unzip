Ext.define('CMDBuildUI.view.administration.content.localizations.configuration.View', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.localizations.configuration.ViewController',
        'CMDBuildUI.view.administration.content.localizations.configuration.ViewModel'
    ],

    alias: 'widget.administration-content-localizations-configuration-view',
    controller: 'administration-content-localizations-configuration-view',
    viewModel: {
        type: 'administration-content-localizations-configuration-view'
    },
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    cls: 'administration tab-hidden',
    ui: 'administration-tabandtools',

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true
    }, 'localization'),
    items: [{
        xtype: 'form',
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        items: [{
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.localizations.languageconfiguration,
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.localizations.languageconfiguration'
            },
            items: [{
                    xtype: 'container',
                    columnWidth: 1,
                    autoEl: {
                        'data-testid': 'administration-content-localizations-configuration-view-languageConfigurationContainer'
                    },
                    items: [{
                        xtype: 'combo',
                        width: '50%',
                        forceSelection: true,
                        queryMode: 'local',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.defaultlanguage,
                        displayField: 'description',
                        valueField: ['code'],
                        reference: 'defaultLanguageCombo',
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.defaultlanguage'
                        },
                        autoEl: {
                            'data-testid': 'administration-content-localizations-configuration-view-languageConfigurationTextfield'
                        },
                        disabled: true,
                        bind: {
                            disabled: '{actions.view}',
                            value: '{defaultlanguage}'
                        },
                        disabledCls: ''
                    }]
                },
                {
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.showlanguagechoice,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.showlanguagechoice'
                    },
                    disabled: true,
                    bind: {
                        disabled: '{actions.view}',

                        value: '{languageprompt}'
                    },
                    disabledCls: '',
                    autoEl: {
                        'data-testid': 'administration-content-localizations-configuration-view-languageChoiceCheckbox'
                    }
                }
            ]
        }, {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.localizations.enabledlanguages,
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.localizations.enabledlanguages'
            },
            items: [{
                xtype: 'checkboxgroup',
                columns: 4,
                columnWidth: 1,
                itemId: 'languagescheckboxGroup',
                reference: 'languagescheckboxGroup',
                vertical: true,
                labelAlign: 'top',
                items: []
            }]
        }]
    }],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
    }],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
        var vm = this.getViewModel();
        vm.getParent().set('title', 'Configuration');
        this.callParent(arguments);
    }

});