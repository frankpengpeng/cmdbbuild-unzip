Ext.define('CMDBuildUI.view.administration.content.tasks.card.CardController', {
    extend: 'Ext.app.ViewController',
    mixins: ['CMDBuildUI.view.administration.content.tasks.card.CardMixin'],
    alias: 'controller.view-administration-content-tasks-card',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#prevBtn': {
            click: 'onPrevBtnClick'
        },
        '#nextBtn': {
            click: 'onNextBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        }

    },

    /**
     * @param {CMDBuildUI.view.administration.content.tasks.card.CardController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        var formButtons;

        if (!CMDBuildUI.util.Stores.loaded.emailaccounts) {
            CMDBuildUI.util.Stores.loadEmailAccountsStore();
        }
        if (!CMDBuildUI.util.Stores.loaded.emailtemplates) {
            CMDBuildUI.util.Stores.loadEmailTemplatesStore();
        }

        vm.bind({
            bindTo: {
                theTask: '{theTask}'
            }
        }, function (data) {

            if (data.theTask) {
                var type = data.theTask.get('type');
                me.generateCardFor(type, data, view);
                Ext.asap(function () {
                    try {
                        view.setHidden(false);
                        view.up().unmask();
                    } catch (error) {
                    }
                }, this);

            }
        });
        // isView | isEdit
        if (!vm.get('theTask') || !vm.get('theTask').phantom) {
            vm.linkTo("theTask", {
                type: vm.get('grid').getViewModel().get('taskModelName'),
                id: vm.get('grid').getSelection()[0].get('_id')
            });
        }
        // isClone
        if (vm.get('theTask') && vm.get('theTask').phantom) {
            var config = vm.get('theTask')._config;
            config.updateDataFromObject(vm.get('theTask').get('config'));
        }

        if (vm.get('actions.view')) {
            var topbar = {
                xtype: 'components-administration-toolbars-formtoolbar',
                dock: 'top',
                hidden: true,
                bind: {
                    hidden: '{!actions.view}'
                },
                items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                    edit: true, // #editBtn set true for show the button
                    view: false, // #viewBtn set true for show the button
                    clone: true, // #cloneBtn set true for show the button
                    'delete': true, // #deleteBtn set true for show the button
                    activeToggle: false // #enableBtn and #disableBtn set true for show the buttons       
                },

                    /* testId */
                    'importexporttemplates',

                    /* viewModel object needed only for activeTogle */
                    'theTask',

                    /* add custom tools[] on the left of the bar */
                    [],

                    /* add custom tools[] before #editBtn*/
                    [],

                    /* add custom tools[] after at the end of the bar*/
                    []
                )
            };
            view.addDocked(topbar);

        }
        formButtons = {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            items: CMDBuildUI.util.administration.helper.FormHelper.getPrevNextSaveCancelButtons(false, // formBind
                {
                    // prev
                    bind: {
                        disabled: '{isPrevDisabled}'
                    }
                }, {
                    // next
                    bind: {
                        disabled: '{isNextDisabled}'
                    }
                }, {
                    // save
                    bind: {
                        hidden: '{actions.view}',
                        disabled: '{!isNextDisabled}'
                    }
                }, {
                    // cancel
                    bind: {
                        hidden: '{actions.view}'
                    }
                }
            )
        };

        view.addDocked(formButtons);
    },

    onAfterRender: function (view) {

    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = me.getView();
        var vm = me.getViewModel();
        var theTask = vm.get('theTask');
        if (form && me.validateForm(form)) {
            var configData = theTask._config.getData();
            if (vm.get('isAdvancedCron')) {
                var cronExpression = Ext.String.format('{0} {1} {2} {3} {4}',
                    vm.get('advancedCronMinuteValue'),
                    vm.get('advancedCronHourValue'),
                    vm.get('advancedCronDayValue'),
                    vm.get('advancedCronMonthValue'),
                    vm.get('advancedCronDayofweekValue')
                );
                var regex = /^\s*($|#|\w+\s*=|(\?|\*|(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?(?:,(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?)*)\s+(\?|\*|(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?(?:,(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?)*)\s+(\?|\*|(?:[01]?\d|2[0-3])(?:(?:-|\/|\,)(?:[01]?\d|2[0-3]))?(?:,(?:[01]?\d|2[0-3])(?:(?:-|\/|\,)(?:[01]?\d|2[0-3]))?)*)\s+(\?|\*|(?:0?[1-9]|[12]\d|3[01])(?:(?:-|\/|\,)(?:0?[1-9]|[12]\d|3[01]))?(?:,(?:0?[1-9]|[12]\d|3[01])(?:(?:-|\/|\,)(?:0?[1-9]|[12]\d|3[01]))?)*)\s+(\?|\*|(?:[1-9]|1[012])(?:(?:-|\/|\,)(?:[1-9]|1[012]))?(?:L|W)?(?:,(?:[1-9]|1[012])(?:(?:-|\/|\,)(?:[1-9]|1[012]))?(?:L|W)?)*|\?|\*|(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?(?:,(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)*)\s+(\?|\*|(?:[0-6])(?:(?:-|\/|\,|#)(?:[0-6]))?(?:L)?(?:,(?:[0-6])(?:(?:-|\/|\,|#)(?:[0-6]))?(?:L)?)*|\?|\*|(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?(?:,(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?)*)(|\s)+(\?|\*|(?:|\d{4})(?:(?:-|\/|\,)(?:|\d{4}))?(?:,(?:|\d{4})(?:(?:-|\/|\,)(?:|\d{4}))?)*))$/;
                var isValid = regex.test('* ' + cronExpression);
                configData.cronExpression = cronExpression;
            }
            switch (theTask.get('type')) {
                case 'workflow':
                    configData.classname = vm.get('workflowClassName');
                    configData.attributes = vm.serializeAttributesMapStore();
                    break;
                case 'emailService':
                    configData.action_workflow_class_name = vm.get('workflowClassName');
                    configData.action_workflow_fields_mapping = vm.serializeAttributesMapStore();
                    break;

                default:
                    break;
            }

            theTask.set('config', configData);
            theTask.save({
                success: function (record, operation) {
                    vm.get('grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [vm.get('grid'), record, this]);
                    form.up().fireEvent("closed");
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        vm.get("theTask").reject(); // discard changes
        this.getView().up().fireEvent("closed");
    },
    
    privates: {

        validateForm: function (form) {

            var me = this,
                invalid;
            Ext.suspendLayouts();
            var taskType = form.getViewModel().get('taskType');

            switch (taskType) {
                case 'import_export':
                    invalid = me.importexport.validateForm(form);
                    break;
                case 'emailService':
                    invalid = me.emailservice.validateForm(form);
                    break;
                case 'workflow':
                    invalid = me.startworkflow.validateForm(form);
                    break;
                default:
                    break;
            }
            return !invalid.length;

        }
    }
});