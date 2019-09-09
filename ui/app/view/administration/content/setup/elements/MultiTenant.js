Ext.define('CMDBuildUI.view.administration.content.setup.MultiTenant', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.MultiTenantController',
        'CMDBuildUI.view.administration.content.setup.elements.MultiTenantModel'
    ],
    alias: 'widget.administration-content-setup-elements-multitenant',
    controller: 'administration-content-setup-elements-multitenant',
    viewModel: {
        type: 'administration-content-setup-elements-multitenant'
    },

    items: [{
        xtype: 'container',
        bind: {
            hidden: '{actions.view}'
        },
        margin: 10,
        ui: 'messageinfo',
        html: Ext.String.format(
            CMDBuildUI.locales.Locales.administration.systemconfig.multitenantinfomessage,
            '<a href="http://www.cmdbuild.org/en/documentazione/manuali" target="blank">http://www.cmdbuild.org/en/documentazione/manuali</a>'
        )
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'enabled',
                    bind: {
                        value: '{multiTenantEnabled}',
                        readOnly: '{actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'combo',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.configurationmode,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.configurationmode'
                    },

                    queryMode: 'local',
                    allowBlank: false,
                    displayField: 'label',
                    valueField: 'value',
                    bind: {
                        store: '{getConfigurationModeStore}',
                        value: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}',
                        hidden: '{multitenantConfigurationModeComboHidden}',
                        readOnly: '{actions.view}'
                    }
                }, {
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.configurationmode,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.configurationmode'
                    },
                    allowBlank: false,
                    displayField: 'label',
                    valueField: 'value',
                    bind: {
                        hidden: '{multitenantConfigurationModeDisplayHidden}',
                        value: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}'
                    },
                    renderer: function (value) {
                        switch (value) {
                            case 'CMDBUILD_CLASS':
                                return CMDBuildUI.locales.Locales.administration.localizations.class;
                            case 'DB_FUNCTION':
                                return CMDBuildUI.locales.Locales.administration.common.labels.funktion;
                            default:
                                return '';
                        }
                    }
                }]
            }]
        }, {
            layout: 'column',
            bind: {
                hidden: '{isFunctionMode}'
            },
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'combo',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.class,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.class'
                    },
                    allowBlank: false,
                    editable: false,
                    displayField: 'description',
                    valueField: 'name',
                    bind: {
                        store: '{getFilteredClasses}',
                        value: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__tenantClass}',
                        hidden: '{multitenantConfigurationClassComboHidden}',
                        readOnly: '{actions.view}'
                    }
                }, {
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.class,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.class'
                    },
                    allowBlank: false,
                    displayField: 'label',
                    valueField: 'value',
                    bind: {
                        hidden: '{multitenantConfigurationClassDisplayHidden}',
                        value: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__tenantClass}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.funktion'
                    },
                    value: '_cm3_multitenant_get',
                    bind: {
                        hidden: '{!isFunctionMode}',
                        readOnly: '{actions.view}'
                    }
                }]
            }]
        }]
    }]
});