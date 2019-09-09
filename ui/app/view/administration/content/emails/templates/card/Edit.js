Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.Edit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.card.EditController',
        'CMDBuildUI.view.administration.content.emails.templates.card.EditModel'
    ],

    alias: 'widget.administration-content-emails-templates-card-edit',
    controller: 'administration-content-emails-templates-card-edit',
    viewModel: {
        type: 'administration-content-emails-templates-card-edit'
    },

    modelValidation: true,
    scrollable: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    items: [{
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },

            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                    xtype: 'textfield',
                    allowBlank: false,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                    },
                    name: 'name',
                    bind: {
                        value: '{theTemplate.name}'
                    },
                    disabled: true
                }, {
                    xtype: 'textfield',
                    allowBlank: false,
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
                    bind: {
                        value: '{theTemplate.promptSynchronization}'
                    }
                },
                {
                    xtype: 'combobox',
                    padding: '0 15 0 0',
                    reference: 'delay',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.delay,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.emails.delay'
                    },
                    displayField: 'label',
                    valueField: ['value'],
                    bind: {
                        store: '{delaylist}',
                        value: '{theTemplate.delay}'
                    }
                }
            ]

        }, {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            layout: 'fit',
            title: CMDBuildUI.locales.Locales.administration.emails.template,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.template'
            },
            items: [{
                    xtype: 'combobox',
                    name: 'account',
                    displayField: 'name',
                    valueField: ['_id'],
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.defaultaccount,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.emails.defaultaccount'
                    },
                    bind: {
                        value: '{theTemplate.account}',
                        store: '{account}'
                    }
                },
                {
                    xtype: 'combobox',
                    name: 'account',
                    displayField: 'label',
                    valueField: 'value',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.contenttype,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.emails.contenttype'
                    },
                    store: 'administration.emails.ContentTypes',
                    bind: {
                        value: '{theTemplate.contentType}'
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'from',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.from,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.emails.from'
                    },
                    bind: {
                        value: '{theTemplate.from}'
                    },
                    labelToolIconCls: 'fa-list',
                    labelToolIconQtip: 'Edit values',
                    labelToolIconClick: 'onEditValueBtnClick'
                },
                {
                    xtype: 'textfield',
                    name: 'to',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.to,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.emails.to'
                    },
                    bind: {
                        value: '{theTemplate.to}'
                    },
                    labelToolIconCls: 'fa-list',
                    labelToolIconQtip: 'Edit values',
                    labelToolIconClick: 'onEditValueBtnClick'
                },
                {
                    xtype: 'textfield',
                    name: 'cc',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.cc,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.emails.cc'
                    },
                    bind: {
                        value: '{theTemplate.cc}'
                    },
                    labelToolIconCls: 'fa-list',
                    labelToolIconQtip: 'Edit values',
                    labelToolIconClick: 'onEditValueBtnClick'
                },
                {
                    xtype: 'textfield',
                    name: 'bcc',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.bcc,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.emails.bcc'
                    },
                    bind: {
                        value: '{theTemplate.bcc}'
                    },
                    labelToolIconCls: 'fa-list',
                    labelToolIconQtip: 'Edit values',
                    labelToolIconClick: 'onEditValueBtnClick'
                },
                {
                    xtype: 'textfield',
                    name: 'subject',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.subject,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.emails.subject'
                    },
                    bind: {
                        value: '{theTemplate.subject}'
                    },
                    labelToolIconCls: 'fa-list',
                    labelToolIconQtip: 'Edit values',
                    labelToolIconClick: 'onEditValueBtnClick'
                },
                {
                    xtype: 'htmleditor',
                    name: 'body',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.body,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.emails.body'
                    },
                    bind: {
                        value: '{theTemplate.body}'
                    },
                    labelToolIconCls: 'fa-list',
                    labelToolIconQtip: 'Edit values',
                    labelToolIconClick: 'onEditValueBtnClick'
                }
            ]
        }
        // , 
        // {
        //     margin: '0 0 0 15',
        //     xtype: 'button',
        //     iconCls: 'x-fa fa-pencil',
        //     text: CMDBuildUI.locales.Locales.administration.emails.editvalues,
        //     localized: {
        //         title: 'CMDBuildUI.locales.Locales.administration.emails.editvalues'
        //     },
        //     reference: 'editValuesBtn',
        //     itemId: 'editValuesBtn',
        //     ui: 'administration-action-small'

        // }
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
});