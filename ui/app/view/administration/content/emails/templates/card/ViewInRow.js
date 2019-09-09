Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.emails.templates.card.ViewInRowModel',
        'Ext.layout.*'
    ],

    alias: 'widget.administration-content-emails-templates-card-viewinrow',
    controller: 'administration-content-emails-templates-card-viewinrow',
    viewModel: {
        type: 'administration-content-emails-templates-card-viewinrow'
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
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                },
                name: 'name',
                bind: {
                    value: '{theTemplate.name}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                },
                name: 'description',
                bind: {
                    value: '{theTemplate.description}'
                }
            },
            {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.keepsync,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.keepsync'
                },
                name: 'keepSynchronization',
                readOnly: true,
                bind: {
                    value: '{theTemplate.keepSynchronization}'
                }
            },
            {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.promptsync,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.promptsync'
                },
                name: 'promptSynchronization',
                readOnly: true,
                bind: {
                    value: '{theTemplate.promptSynchronization}'
                }
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.delay,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.delay'
                },
                name: 'delay',
                bind: {
                    value: '{theTemplate.delay}'
                }
            }
        ]

    }, {
        title: CMDBuildUI.locales.Locales.administration.emails.template,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.template'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        items: [{
                xtype: 'displayfield',
                readOnly: true,
                name: 'account',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.defaultaccount,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.defaultaccount'
                },
                bind: {
                    value: '{accountDescription}'
                }
            },{
                xtype: 'displayfield',
                name: 'contenttype',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.contenttype,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.contenttype'
                },
                bind: {
                    value: '{theTemplate.contentType}'
                },
                renderer: CMDBuildUI.util.administration.helper.RendererHelper.getEmailContentType
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.from,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.from'
                },
                name: 'from',
                bind: {
                    value: '{theTemplate.from}'
                }
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.to,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.to'
                },
                name: 'to',
                bind: {
                    value: '{theTemplate.to}'
                }
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.cc,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.cc'
                },
                name: 'cc',
                bind: {
                    value: '{theTemplate.cc}'
                }
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.bcc,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.bcc'
                },
                name: 'bcc',
                bind: {
                    value: '{theTemplate.bcc}'
                }
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.subject,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.subject'
                },
                name: 'subject',
                bind: {
                    value: '{theTemplate.subject}'
                }
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.body,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.body'
                },
                name: 'body',
                bind: {
                    value: '{theTemplate.body}'
                }
            }
        ]
    }],
    tools: [{
        xtype: 'tbfill'
    }, {
        xtype: 'tool',
        itemId: 'templatesEditBtn',
        reference: 'templatesEditBtn',
        iconCls: 'x-fa fa-pencil',
        tooltip: CMDBuildUI.locales.Locales.administration.common.tooltips.edit,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.tooltips.edit'
        },
        callback: 'onEditBtnClick',
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-emails-templates-card-viewInRow-editBtn'
        }
    }, {
        xtype: 'tool',
        itemId: 'templatesOpenBtn',
        iconCls: 'x-fa fa-external-link',
        tooltip: CMDBuildUI.locales.Locales.administration.common.tooltips.open,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.tooltips.open'
        },
        callback: 'onOpenBtnClick',
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-emails-templates-card-viewInRow-openBtn'
        },
        bind: {
            hidden: '{hideOpenBtn}'
        }
    }, {
        xtype: 'tool',
        itemId: 'templatesCloneBtn',
        reference: 'templatesCloneBtn',
        iconCls: 'x-fa fa-clone',
        tooltip: CMDBuildUI.locales.Locales.administration.emails.clonetemplate,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.clonetemplate'
        },
        callback: 'onCloneBtnClick',
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-emails-templates-card-viewInRow-cloneBtn'
        }
    }, {
        xtype: 'tool',
        itemId: 'templatesDeleteBtn',
        reference: 'templatesDeleteBtn',
        iconCls: 'x-fa fa-trash',
        tooltip: CMDBuildUI.locales.Locales.administration.emails.removetemplate,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.removetemplate'
        },
        callback: 'onDeleteBtnClick',
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-emails-templates-card-viewInRow-editBtn'
        }
    }]

});