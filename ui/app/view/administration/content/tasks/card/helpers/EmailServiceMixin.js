Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.EmailServiceMixin', {
    mixinId: 'administration-task-emailservicemixin',
    mixins: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin'
    ],
    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin',
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],
    emailservice: {
        getGeneralPropertyPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer(
                    [
                        ctx.getNameInput(theVmObject, 'code'),
                        ctx.getDescriptionInput(theVmObject, 'description')
                    ]
                ),
                ctx.getRowFieldContainer(
                    [
                        ctx.getTypeInput(theVmObject, 'type', true)
                    ]
                ),
                ctx.getRowFieldContainer(
                    [
                        ctx.getEmailAccountInput(theVmObject, 'config.account_name', {
                            allowBlank: false,
                            displayField: 'name',
                            valueField: 'name'
                        }),
                        ctx.getDirectoryInput(theVmObject, 'config.folder_incoming', {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.incomingfolder,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.incomingfolder'
                            }
                        })
                    ]
                )

            ];

            return items;

        },

        getSettingsPanel: function (theVmObject, step, data, ctx) {
            var items = [

                ctx.getRowFieldContainer([
                    ctx.getFilterTypeInput(theVmObject, 'config.filter_type')
                ]),

                ctx.getRowFieldContainer([
                    ctx.getFunctionsInput(theVmObject, 'config.filter_function_name')
                ], {

                        bind: {
                            hidden: '{!isFilterFunction}'
                        }

                    }),
                ctx.getRowFieldContainer([
                    ctx.getFilterRegexFromInput(theVmObject, 'config.filter_from_regex'),
                    ctx.getFilterRegexSubjectInput(theVmObject, 'config.filter_subject_regex')
                ], {
                        bind: {
                            hidden: '{!isFilterRegex}'
                        }
                    }),

                ctx.getRowFieldContainer([
                    ctx.getDirectoryInput(theVmObject, 'config.folder_processed', {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.processedfolder,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.processedfolder'
                        }
                    })
                ]),

                ctx.getRowFieldContainer([
                    ctx.getFilterRejectInput(theVmObject, 'config.filter_reject'),
                    ctx.getDirectoryInput(theVmObject, 'config.folder_rejected', {
                        allowBlank: true,
                        fieldcontainer: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.rejectedfolder,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.rejectedfolder'
                            },
                            allowBlank: true,
                            bind: {
                                hidden: '{!isMoveReject}'
                            }
                        }
                    })
                ])
            ];

            return items;
        },
        getCronPanel: function (theVmObject, step, data, ctx) {
            var items = [
                /**
                 * Cron: combo con valori: Every hour, Every day, Every month, Every year, Custom.
                 * Se Cron è Custom allora compariranno i campi per impostare il cron, come per i task asincroni in CMDBuild 2.5.
                 */
                ctx.getRowFieldContainer(
                    [
                        ctx.getBasicCronInput(theVmObject, 'config.cronExpression')
                    ]
                ),
                ctx.getRowFieldContainer(
                    [
                        ctx.getAdvancedCronInput(theVmObject, 'config.cronExpression')
                    ]
                )
            ];

            return items;
        },

        getParsePanel: function (theVmObject, step, data, ctx) {

            var items = [

                ctx.getRowFieldContainer([
                    ctx.getBodyParsingInput(theVmObject, 'config.parsing_active', {
                        listeners: {
                            change: function (checkbox, newValue, oldValue) {
                                checkbox.lookupViewModel().set('isBodyParsing', newValue);
                            }
                        }
                    })
                ]),

                ctx.getRowFieldContainer([
                    ctx.getParsingInput(theVmObject, 'config.parsing_key_init', {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.keystartdelimiter,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.keystartdelimiter'
                        }
                    }),
                    ctx.getParsingInput(theVmObject, 'config.parsing_key_end', {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.keyenddelimiter,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.keyenddelimiter'
                        }
                    })
                ], {

                        bind: {
                            hidden: '{!isBodyParsing}'
                        }
                    }
                ),

                ctx.getRowFieldContainer([
                    ctx.getParsingInput(theVmObject, 'config.parsing_value_init', {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.valuestartdelimiter,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.valuestartdelimiter'
                        }
                    }),
                    ctx.getParsingInput(theVmObject, 'config.parsing_value_end', {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.valueenddelimiter,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.valueenddelimiter'
                        }
                    })
                ], {
                        bind: {
                            hidden: '{!isBodyParsing}'
                        }
                    }
                ),



                ctx.getRowFieldContainer([
                    ctx.getSaveAttachmentInput(theVmObject, 'config.action_attachments_active'),
                    ctx.getAttachmentCategoryInput(theVmObject, 'config.action_attachments_category', {
                        fieldcontainer: {
                            bind: {
                                hidden: '{!isAttachmentActive}'
                            }
                        }
                    })
                ], {
                        bind: {
                            disabled: '{!isDmsEnabled}'
                        }
                    })

            ];

            return items;
        },

        getProcessPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer(
                    [
                        ctx.getStartProcessInput(theVmObject, 'config.action_workflow_active')

                    ]),

                ctx.getRowFieldContainer(
                    [
                        ctx.getRowFieldContainer([
                            ctx.getProcessesInput(theVmObject, 'config.action_workflow_class_name'),
                            ctx.getWorkflowAdvanceInput(theVmObject, 'config.action_workflow_advance')
                        ]),
                        ctx.getRowFieldContainer(
                            [
                                ctx.getProcessAttributesGrid(),
                                ctx.getProcessAttributesGridForm()
                            ], {
                                bind: {
                                    hidden: '{!workflowClassName}'
                                }
                            })
                    ], {
                        bind: {
                            hidden: '{!isStartProcessActive}'
                        }
                    })
            ];

            return items;
        },
        getNotificationPanel: function (theVmObject, step, data, ctx) {
            var items = [
                ctx.getRowFieldContainer([
                    /*
                    * Error email template: combo con elenco dei template delle email. Placeholder: Use the one defined in template.
                    * Account: combo con elenco degli account. Placeholder: Use the one defined in template.
                    */
                    ctx.getRowFieldContainer([
                        ctx.getActionNotificationActiveInput(theVmObject, 'config.action_notification_active'),
                        ctx.getEmailTemplateInput(theVmObject, 'config.action_notification_template', {
                            fieldcontainer: {
                                bind: {
                                    hidden: '{!isNotificationActive}'
                                }
                            }
                        })
                    ])

                ])


            ];

            return items;
        },
        validateForm: function (form) {

            var me = this,
                _form = form.form,
                invalid = _form.getFields().filterBy(function (field) {
                    var fieldName = field.getName();

                    switch (fieldName) {
                        case 'config.filter_from_regex':
                        case 'config.filter_subject_regex':
                            if (form.down('[name="config.filter_type"]').getValue() === 'regex') {
                                me.setAllowBlank(field, false, _form);
                            } else {
                                me.setAllowBlank(field, true, _form);
                            }
                            break;
                        case 'config.action_notification_template':
                            if (form.down('[name="action_notification_active"]').getValue() !== false) {
                                me.setAllowBlank(field, false, _form);
                            } else {
                                me.setAllowBlank(field, true, _form);
                            }
                            break;
                        default:
                            break;
                    }


                    return !field.validate();
                });
            Ext.resumeLayouts(true);

            if (invalid.length) {
                CMDBuildUI.util.administration.helper.FormHelper.showInvalidFieldsMessage(invalid);
            }

            return invalid;
        },
        setAllowBlank: function (field, value, form) {
            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, value, form);
        },
        getFieldcontainerLabel: function (item) {
            var itemUp = item.up('fieldcontainer');
            var label = itemUp.getFieldLabel();
            if (!label) {
                return this.getFieldcontainerLabel(itemUp);
            }
            return label;
        }

    }



});