Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.SatrtWorkflowMixin', {
    mixinId: 'administration-task-startworkflowmixin',
    mixins: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin'
    ],
    requires: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin',
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],
    startworkflow: {
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
                        ctx.getRowFieldContainer([
                            ctx.getProcessesInput(theVmObject, 'config.classname'),
                            ctx.getTaskUserInput(theVmObject, 'config.username')
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
        }

    }



});