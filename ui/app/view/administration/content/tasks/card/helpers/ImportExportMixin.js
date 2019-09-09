Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.ImportExportMixin', {
    mixinId: 'administration-task-importexportmixin',
    mixins: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin'
    ],
    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin',
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],
    importexport: {
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
                        ctx.getTypeInput(theVmObject, 'type'),
                        ctx.getTemplateInput(theVmObject, 'config.template')
                    ]
                )
            ];

            return items;

        },

        getSettingsPanel: function (theVmObject, step, data, ctx) {
            var items = [
                /**
                 * Step 2 (per Type Import):
                 * Source type: combo con i valori File on server, Url.
                 * Directory: stringa visibile solo se Source type è File on server. Obbligatorio se visibile.
                 * File name: stringa visibile solo se Source type è File on server. Obbligatorio se visibile. Può essere una RegExp.
                 * Url: stringa visibile solo se Source type è Url. Obbligatorio se visibile.
                 **/
                ctx.getRowFieldContainer(
                    [
                        ctx.getRowFieldContainer(
                            [
                                ctx.getSourceInput(theVmObject, 'config.source')
                            ]
                        ),
                        ctx.getRowFieldContainer(
                            [
                                ctx.getDirectoryInput(theVmObject, 'config.directory'),
                                ctx.getFilepatternInput(theVmObject, 'config.filePattern')
                            ], {
                                bind: {
                                    hidden: '{!isSourceFile}'
                                }
                            }
                        ),
                        ctx.getRowFieldContainer(
                            [
                                ctx.getUrlInput(theVmObject, 'config.url')
                            ], {
                                bind: {
                                    hidden: '{!isSourceUrl}'
                                }
                            }
                        ),

                        ctx.getRowFieldContainer(
                            [
                                ctx.getPostImportActionInput(theVmObject, 'config.postImportAction'),
                                ctx.getTargetDirectoryInput(theVmObject, 'config.targetDirectory')
                            ]
                        )
                    ], {
                        bind: {
                            hidden: '{!isImport}'
                        }
                    }
                ),
                /**
                * Step 2 (per Type Export):
                * Directory: stringa. Obbligatorio.
                * File name: stringa. Obbligatorio.
                **/
                ctx.getRowFieldContainer([
                    ctx.getRowFieldContainer(
                        [
                            ctx.getDirectoryInput(theVmObject, 'config.directory'),
                            ctx.getFilenameInput(theVmObject, 'config.fileName')
                        ]
                    )
                ], {
                        bind: {
                            hidden: '{!isExport}'
                        }
                    })
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

        getNotificationPanel: function (theVmObject, step, data, ctx) {
            var items = [

                ctx.getRowFieldContainer([
                    ctx.getNotificationInput(theVmObject, 'config.notificationMode')
                ]),
                ctx.getRowFieldContainer([
                    /*
                    * Error email template: combo con elenco dei template delle email. Placeholder: Use the one defined in template.
                    * Account: combo con elenco degli account. Placeholder: Use the one defined in template.
                    */
                    ctx.getRowFieldContainer(
                        [
                            ctx.getErrorEmailTemplateInput(theVmObject, 'config.errorEmailTemplate'),
                            ctx.getErrorEmailAccountInput(theVmObject, 'config.errorEmailAccount')
                        ], {
                            bind: {
                                hidden: '{!isImport}'
                            }
                        }),
                    /**
                    * email template: combo con elenco dei template delle email. Placeholder: Use the one defined in template.
                    * Account: combo con elenco degli account. Placeholder: Use the one defined in template.
                    */

                    ctx.getRowFieldContainer(
                        [
                            ctx.getEmailTemplateInput(theVmObject, 'config.emailTemplate'),
                            ctx.getEmailAccountInput(theVmObject, 'config.emailAccount')
                        ], {
                            bind: {
                                hidden: '{!isExport}'
                            }
                        })
                ], {
                        bind: {
                            hidden: '{isNeverNotification}'
                        }
                    })


            ];

            return items;
        },

        validateForm: function (form) {

            var me = this,
                _form = form.form,
                invalid = _form.getFields().filterBy(function (field) {
                    var type = form.down('[name="type"]').getValue();
                    var fieldName = field.getName();
                    var isNeverNotification = form.getViewModel().get('isNeverNotification');

                    switch (fieldName) {
                        case 'config.errorEmailTemplate':
                        case 'config.errorEmailAccount':

                            switch (type) {
                                case 'import_file':
                                    me.setAllowBlank(field, isNeverNotification, _form);
                                    break;
                                case 'export_file':
                                    me.setAllowBlank(field, true, _form);
                                    break;

                                default:
                                    break;
                            }
                            break;
                        case 'config.source':
                            switch (type) {
                                case 'import_file':
                                    me.setAllowBlank(field, false, _form);
                                    break;
                                case 'export_file':
                                    me.setAllowBlank(field, true, _form);
                                    break;

                                default:
                                    break;
                            }
                            break;

                        case 'config.postImportAction':
                            switch (type) {
                                case 'import_file':
                                    if (form.down('[name="config.source"]').getValue() === 'file') {
                                        me.setAllowBlank(field, false, _form);
                                    } else {
                                        me.setAllowBlank(field, true, _form);
                                    }
                                    break;
                                case 'export_file':
                                    me.setAllowBlank(field, true, _form);
                                    break;

                                default:
                                    break;
                            }
                            break;

                        case 'config.url':
                            switch (type) {
                                case 'import_file':
                                    if (form.down('[name="config.source"]').getValue() === 'url') {
                                        me.setAllowBlank(field, false, _form);
                                    } else {
                                        me.setAllowBlank(field, true, _form);
                                    }
                                    break;
                                case 'export_file':
                                    me.setAllowBlank(field, true, _form);
                                    break;

                                default:
                                    break;
                            }
                            break;
                        case 'config.directory':
                        case 'config.filePattern':
                            switch (type) {
                                case 'import_file':
                                    if (form.down('[name="config.source"]').getValue() === 'url') {
                                        me.setAllowBlank(field, true, _form);
                                    } else {
                                        me.setAllowBlank(field, false, _form);
                                    }
                                    break;
                                case 'export_file':
                                    me.setAllowBlank(field, true, _form);
                                    break;

                                default:
                                    break;
                            }
                            break;




                        case 'config.targetDirectory':
                            switch (type) {
                                case 'import_file':
                                    if (form.down('[name="config.postImportAction"]').getValue() === 'move_files') {
                                        me.setAllowBlank(field, false, _form);
                                    } else {
                                        me.setAllowBlank(field, true, _form);
                                    }
                                    break;
                                case 'export_file':
                                    me.setAllowBlank(field, true, _form);
                                    break;

                                default:
                                    break;
                            }
                            break;

                        case 'config.fileName':
                            switch (type) {
                                case 'import_file':
                                    me.setAllowBlank(field, true, _form);
                                    break;
                                case 'export_file':
                                    me.setAllowBlank(field, false, _form);
                                    break;

                                default:
                                    break;
                            }
                            break;
                        case 'config.emailTemplate':
                        case 'config.emailAccount':
                            switch (type) {
                                case 'import_file':
                                    me.setAllowBlank(field, true, _form);
                                    break;
                                case 'export_file':
                                    me.setAllowBlank(field, isNeverNotification, _form);
                                    break;

                                default:
                                    break;
                            }
                            break;

                        case 'advancedcron_minute':
                        case 'advancedcron_hour':
                        case 'advancedcron_day':
                        case 'advancedcron_month':
                        case 'advancedcron_dayofweek':
                            if (!form.getViewModel().get('isAdvancedCron')) {
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