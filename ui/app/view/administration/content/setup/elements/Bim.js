Ext.define('CMDBuildUI.view.administration.content.setup.elements.Bim', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.BimController',
        'CMDBuildUI.view.administration.content.setup.elements.BimModel'
    ],

    alias: 'widget.administration-content-setup-elements-bim',
    controller: 'administration-content-setup-elements-bim',
    viewModel: {
        type: 'administration-content-setup-elements-bim'
    },
    items: [{
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
                    /********************* org.cmdbuild.bim.enabled **********************/
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'isEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__enabled}',
                        readOnly: '{actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    /********************* org.cmdbuild.gis.initialLatitude **********************/
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.url,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.url'
                    },
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__url}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'textfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.url,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.url'
                    },
                    hidden: true,
                    placeholder: 'http:// or https://',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__url}',
                        hidden: '{actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    name: 'username',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__username}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'textfield',
                    name: 'username',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                    },
                    listeners: {
                        afterrender: function (cmp) {
                            cmp.inputEl.set({
                                autocomplete: 'new-username'
                            });
                        }
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__username}',
                        hidden: '{actions.view}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'displayfield',
                    name: 'password',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                    },
                    value: '****',
                    bind: {
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'textfield',
                    inputType: 'password',
                    name: 'password',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                    },
                    listeners: {
                        afterrender: function (cmp) {
                            cmp.inputEl.set({
                                autocomplete: 'new-password'
                            });
                        }
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__password}',
                        hidden: '{actions.view}'
                    },
                    triggers: {
                        showPassword: {
                            cls: 'x-fa fa-eye',
                            hideTrigger: false,
                            scope: this,
                            handler: function (field, button, e) {
                                // set the element type to text
                                field.inputEl.el.set({
                                    type: 'text'
                                });
                                field.getTrigger('showPassword').setVisible(false);
                                field.getTrigger('hidePassword').setVisible(true);
                            }
                        },
                        hidePassword: {
                            cls: 'x-fa fa-eye-slash',
                            hidden: true,
                            scope: this,
                            handler: function (field, button, e) {
                                // set the element type to password
                                field.inputEl.el.set({
                                    type: 'password'
                                });
                                field.getTrigger('showPassword').setVisible(true);
                                field.getTrigger('hidePassword').setVisible(false);
                            }
                        }
                    }
                }]
            }]
        }]
    }]

});