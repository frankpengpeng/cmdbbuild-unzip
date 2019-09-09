Ext.define('CMDBuildUI.view.administration.content.emails.accounts.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.accounts.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.emails.accounts.card.ViewInRowModel'
    ],

    alias: 'widget.administration-content-emails-accounts-card-viewinrow',
    controller: 'administration-content-emails-accounts-card-viewinrow',
    viewModel: {
        type: 'administration-content-emails-accounts-card-viewinrow'
    },
    cls: 'administration',
    ui: 'administration-tabandtools',

    items: [{
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        xtype: "fieldset",
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
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                },
                name: 'password',
                bind: {
                    value: '{hiddenPassword}'
                }
            }
        ]
    }, {
        title: CMDBuildUI.locales.Locales.administration.emails.outgoing,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.outgoing'
        },
        xtype: "fieldset",
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        ui: 'administration-formpagination',
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
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.smtpserver,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.smtpserver'
                },
                name: 'smtp_server',
                bind: {
                    value: '{theAccount.smtp_server}'
                }
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.smtpport,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.smtpport'
                },
                name: 'smtp_port',
                bind: {
                    value: '{theAccount.smtp_port}'
                }
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.sentfolder,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.sentfolder'
                },
                name: 'imap_output_folder',
                bind: {
                    value: '{theAccount.imap_output_folder}'
                }
            },
            {
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
            },
            {
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
            }
        ]

    }, {
        title: CMDBuildUI.locales.Locales.administration.emails.incoming,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.incoming'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',
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

    }],
    tools: [{
        xtype: 'tbfill'
    }, {
        xtype: 'tool',
        itemId: 'accountsEditBtn',
        reference: 'accountsEditBtn',
        iconCls: 'x-fa fa-pencil',
        tooltip: CMDBuildUI.locales.Locales.administration.common.tooltips.edit,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.tooltips.edit'
        },
        callback: 'onEditBtnClick',
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-emails-accounts-card-viewInRow-editBtn'
        }
    }, {
        xtype: 'tool',
        itemId: 'accountsOpenBtn',
        iconCls: 'x-fa fa-external-link',
        tooltip: CMDBuildUI.locales.Locales.administration.common.tooltips.open,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.tooltips.open'
        },
        callback: 'onOpenBtnClick',
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-emails-accounts-card-viewInRow-openBtn'
        }
    }, {
        xtype: 'tool',
        itemId: 'accountsDeleteBtn',
        reference: 'accountsDeleteBtn',
        iconCls: 'x-fa fa-trash',
        tooltip: CMDBuildUI.locales.Locales.administration.emails.removeaccount,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.removeaccount'
        },
        callback: 'onDeleteBtnClick',
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-emails-accounts-card-viewInRow-editBtn'
        }
    }, {
        xtype: 'tool',
        itemId: 'defaultAccount',
        reference: 'defaultAccount',
        iconCls: 'x-fa fa-star',
        tooltip: CMDBuildUI.locales.Locales.administration.emails.defaultaccount,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.defaultaccount'
        },
        cls: 'administration-tool',        
        bind: {
            hidden: '{!theAccount.default}'
        },
        autoEl: {
            'data-testid': 'administration-emails-accounts-card-viewInRow-defaultAccount'
        }
    }, {
        xtype: 'tool',
        itemId: 'setDefaultAccount',
        reference: 'setDefaultAccount',
        iconCls: 'x-fa fa-star-o',
        tooltip: CMDBuildUI.locales.Locales.administration.emails.setdefaultaccount,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.setdefaultaccount'
        },
        cls: 'administration-tool',
        bind: {
            hidden: '{theAccount.default}'
        },
        autoEl: {
            'data-testid': 'administration-emails-accounts-card-viewInRow-defaultAccount'
        }
    }]

});