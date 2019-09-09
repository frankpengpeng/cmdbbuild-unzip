Ext.define('CMDBuildUI.view.administration.content.emails.accounts.card.View', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.accounts.card.ViewController',
        'CMDBuildUI.view.administration.content.emails.accounts.card.ViewModel'
    ],

    alias: 'widget.administration-content-emails-accounts-card-view',
    controller: 'administration-content-emails-accounts-card-view',
    viewModel: {
        type: 'administration-content-emails-accounts-card-view'
    },

    scrollable: true,

    cls: 'administration tab-hidden',
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true
    },
        /* testId */
        'emailaccounts'
    ),

    items: [{
        xtype: "container",
        items: [{
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            xtype: "fieldset",
            collapsible: true,
            ui: 'administration-formpagination',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                columnWidth: 1,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                },
                name: 'name',
                bind: {
                    value: '{theAccount.name}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                },
                name: 'username',
                bind: {
                    value: '{theAccount.username}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                },
                name: 'password',
                bind: {
                    value: '{hiddenPassword}'
                }
            }]

        }, {

            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.emails.outgoing,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.outgoing'
            },
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.address,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.address'
                },
                name: 'address',
                bind: {
                    value: '{theAccount.address}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.smtpserver,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.smtpserver'
                },
                name: 'smtp_server',
                bind: {
                    value: '{theAccount.smtp_server}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.smtpport,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.smtpport'
                },
                name: 'smtp_port',
                bind: {
                    value: '{theAccount.smtp_port}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.sentfolder,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.sentfolder'
                },
                name: 'imap_output_folder',
                bind: {
                    value: '{theAccount.imap_output_folder}'
                }
            }, {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.enablessl,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.enablessl'
                },
                readOnly: true,
                name: 'smtp_ssl',
                bind: {
                    value: '{theAccount.smtp_ssl}'
                }
            }, {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.enablestarttls,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.enablestarttls'
                },
                readOnly: true,
                name: 'smtp_starttls',
                bind: {
                    value: '{theAccount.smtp_starttls}'
                }
            }]
        }, {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.emails.incoming,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.incoming'
            },
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.imapserver,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.imapserver'
                },
                name: 'imap_server',
                bind: {
                    value: '{theAccount.imap_server}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.imapport,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.imapport'
                },
                name: 'imap_port',
                bind: {
                    value: '{theAccount.imap_port}'
                }
            }, {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.enablessl,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.enablessl'
                },
                readOnly: true,
                name: 'imap_ssl',
                bind: {
                    value: '{theAccount.imap_ssl}'
                }
            }, {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.enablestarttls,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.enablestarttls'
                },
                readOnly: true,
                name: 'imap_starttls',
                bind: {
                    value: '{theAccount.imap_starttls}'
                }
            }]
        }]
    }]
});