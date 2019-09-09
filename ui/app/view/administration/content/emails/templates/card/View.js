Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.View', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.card.ViewController',
        'CMDBuildUI.view.administration.content.emails.templates.card.ViewModel'
    ],

    alias: 'widget.administration-content-emails-templates-card-view',
    controller: 'administration-content-emails-templates-card-view',
    viewModel: {
        type: 'administration-content-emails-templates-card-view'
    },

    scrollable: true,

    cls: 'administration tab-hidden',
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    tools: [{
        xtype: 'tool',
        itemId: 'editBtn',
        iconCls: 'x-fa fa-pencil',
        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
        },
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-emails-templates-card-view-editBtn'
        }
    }, {
        xtype: 'tool',
        itemId: 'cloneBtn',
        iconCls: 'x-fa fa-clone',
        tooltip: CMDBuildUI.locales.Locales.administration.emails.clonetemplate,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.clonetemplate'
        },
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-emails-templates-card-view-cloneBtn'
        }
    }],

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
                readOnly: true,
                name: 'keepSynchronization',
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
                readOnly: true,
                name: 'promptSynchronization',
                bind: {
                    value: '{theTemplate.promptSynchronization}'
                }
            },
            {
                xtype: 'displayfield',
                reference: 'delay',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.delay,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.delay'
                },
                readOnly: true,
                displayField: 'label',
                valueField: ['value'],
                bind: {
                    value: '{theTemplate.delay}'
                }
            }
            ]

        }, {

            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.emails.template,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.template'
            },
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

            layout: 'column',
            defaults: {
                columnWidth: 1
            },
            items: [{
                xtype: 'displayfield',
                name: 'account',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.defaultaccount,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.defaultaccount'
                },
                bind: {
                    value: '{accountDescription}'
                }
            },
            {
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
            },
            {
                xtype: 'displayfield',
                name: 'from',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.from,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.from'
                },
                bind: {
                    value: '{theTemplate.from}'
                }
            },
            {
                xtype: 'displayfield',
                name: 'to',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.to,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.to'
                },
                bind: {
                    value: '{theTemplate.to}'
                }
            },
            {
                xtype: 'displayfield',
                name: 'cc',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.cc,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.cc'
                },
                bind: {
                    value: '{theTemplate.cc}'
                }
            },
            {
                xtype: 'displayfield',
                name: 'bcc',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.bcc,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.bcc'
                },
                bind: {
                    value: '{theTemplate.bcc}'
                }
            },
            {
                xtype: 'displayfield',
                name: 'subject',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.subject,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.subject'
                },
                bind: {
                    value: '{theTemplate.subject}'
                }
            },
            {
                xtype: 'displayfield',
                name: 'body',
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.body,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.emails.body'
                },
                bind: {
                    value: '{theTemplate.body}'
                }
            }
            ],
            buttons: [{
                text: CMDBuildUI.locales.Locales.administration.common.actions.save,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.common.actions.save'
                },
                formBind: true,
                disabled: true,
                ui: 'administration-action-small',
                listeners: {
                    click: 'onSaveBtnClick'
                }
            }, {
                text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
                },
                ui: 'administration-secondary-action-small',
                listeners: {
                    click: 'onCancelBtnClick'
                }
            }]
        }]
    }]
});