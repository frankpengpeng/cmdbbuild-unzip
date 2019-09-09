Ext.define('CMDBuildUI.view.administration.content.tasks.card.CardMixin', {

    mixins: ['CMDBuildUI.view.administration.content.tasks.card.helpers.FieldsetsHelper'],
    mixinId: 'administration-importexportmixin',

    onEditBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = view.getViewModel();
        var viewModel = {
            data: {
                taskType: vm.get('theTask.type') || vm.get('type') || vm.get('taskType'),
                theTask: view.getViewModel().get('selected') || view.getViewModel().get('theTask'),
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                actions: {
                    edit: true,
                    view: false,
                    add: false
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-tasks-card',
            viewModel: viewModel
        });
    },

    onDeleteBtnClick: function (button) {
        var me = this;
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-task');
                    var grid = button.up('administration-content-tasks-grid');
                    grid.getStore().remove(me.getViewModel().getData().theTask);
                    grid.getStore().sync();
                }
            }, this);
    },


    onViewBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = view.getViewModel();
        var viewModel = {
            data: {
                taskType: vm.get('taskType'),
                theTask: view.getViewModel().get('selected') || view.getViewModel().get('theTask'),
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                actions: {
                    edit: false,
                    view: true,
                    add: false
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-tasks-card',
            viewModel: viewModel
        });
    },

    onCloneBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var newTask = vm.get('theTask').getData();//vm.get('theTask').copyForClone();
        var config = vm.get('theTask').getAssociatedData().config;
        delete config._id;

        newTask.code = Ext.String.format('{0}-clone', newTask.code);

        delete newTask._id;
        newTask.description = Ext.String.format('{0}-clone', newTask.description);
        var theTask = Ext.create(view.lookupViewModel().get('taskModelName'), newTask);
        theTask.set('config', config);

        var viewModel = {
            data: {
                taskType: theTask.get('type') || vm.get('taskType'),
                theTask: theTask,
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                actions: {
                    edit: false,
                    view: false,
                    add: true
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-tasks-card',
            viewModel: viewModel
        });
    },
    cronValidator: function (input, newValue, oldValue) {
       
        var form = input.up('form');
        if(!form){
            return true;
        }
        var vm = form.lookupViewModel();
        var cronExpression = Ext.String.format('{0} {1} {2} {3} {4}',
            vm.get('advancedCronMinuteValue'),
            vm.get('advancedCronHourValue'),
            vm.get('advancedCronDayValue'),
            vm.get('advancedCronMonthValue'),
            vm.get('advancedCronDayofweekValue')
        );
        var regex = /^\s*($|#|\w+\s*=|(\?|\*|(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?(?:,(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?)*)\s+(\?|\*|(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?(?:,(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?)*)\s+(\?|\*|(?:[01]?\d|2[0-3])(?:(?:-|\/|\,)(?:[01]?\d|2[0-3]))?(?:,(?:[01]?\d|2[0-3])(?:(?:-|\/|\,)(?:[01]?\d|2[0-3]))?)*)\s+(\?|\*|(?:0?[1-9]|[12]\d|3[01])(?:(?:-|\/|\,)(?:0?[1-9]|[12]\d|3[01]))?(?:,(?:0?[1-9]|[12]\d|3[01])(?:(?:-|\/|\,)(?:0?[1-9]|[12]\d|3[01]))?)*)\s+(\?|\*|(?:[1-9]|1[012])(?:(?:-|\/|\,)(?:[1-9]|1[012]))?(?:L|W)?(?:,(?:[1-9]|1[012])(?:(?:-|\/|\,)(?:[1-9]|1[012]))?(?:L|W)?)*|\?|\*|(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?(?:,(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)*)\s+(\?|\*|(?:[0-6])(?:(?:-|\/|\,|#)(?:[0-6]))?(?:L)?(?:,(?:[0-6])(?:(?:-|\/|\,|#)(?:[0-6]))?(?:L)?)*|\?|\*|(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?(?:,(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?)*)(|\s)+(\?|\*|(?:|\d{4})(?:(?:-|\/|\,)(?:|\d{4}))?(?:,(?:|\d{4})(?:(?:-|\/|\,)(?:|\d{4}))?)*))$/;
        var isValid = regex.test('* ' + cronExpression);        
        
        if (!isValid || (
            !form.down('[name="advancedcron_minute"]').getValue()||
            !form.down('[name="advancedcron_hour"]').getValue()||
            !form.down('[name="advancedcron_day"]').getValue()||
            !form.down('[name="advancedcron_month"]').getValue()||
            !form.down('[name="advancedcron_dayofweek"]').getValue())
        ) {
            form.down('[name="advancedcron_minute"]').markInvalid('Cron expression is invalid', true);
            form.down('[name="advancedcron_hour"]').markInvalid('Cron expression is invalid', true);
            form.down('[name="advancedcron_day"]').markInvalid('Cron expression is invalid', true);
            form.down('[name="advancedcron_month"]').markInvalid('Cron expression is invalid', true);
            form.down('[name="advancedcron_dayofweek"]').markInvalid('Cron expression is invalid', true);
            return false;
        } else {
            form.down('[name="advancedcron_minute"]').markInvalid(null, false);
            form.down('[name="advancedcron_hour"]').markInvalid(null, false);
            form.down('[name="advancedcron_day"]').markInvalid(null, false);
            form.down('[name="advancedcron_month"]').markInvalid(null, false);
            form.down('[name="advancedcron_dayofweek"]').markInvalid(null, false);
            return true;
        }
    },
    /**
     * 
     * @param {*} row 
     * @param {*} record 
     * @param {*} element 
     * @param {*} rowIndex 
     * @param {*} e 
     * @param {*} eOpts 
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var view = this.getView(),
            container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var formInRow = view.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        view.setSelection(record);

        container.removeAll();
        container.add({
            xtype: 'administration-content-tasks-card',
            viewModel: {
                data: {
                    taskType: view.getViewModel().get('taskType'),
                    theTask: record,
                    grid: view.ownerGrid,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                }
            }
        });
    },

    onPrevBtnClick: function (button, event, eOpts) {
        var vm = button.lookupViewModel();
        var newCurrentStep = vm.get('currentStep') - 1;
        vm.set('currentStep', newCurrentStep);
    },

    onNextBtnClick: function (button, event, eOpts) {
        var vm = button.lookupViewModel();
        var newCurrentStep = vm.get('currentStep') + 1;
        vm.set('currentStep', newCurrentStep);
    },

    onSenderRegexClick: function (event, button, eOpts) {
        var me = this;

        var content = {
            xtype: 'administration-components-splitstring-grid',
            viewModel: {
                data: {
                    theMessage: me.getViewModel().get('theTask.config.filter_from_regex'),
                    theDivisor: ' OR ',
                    actions: me.getViewModel().get('actions')
                }
            }
        };

        // custom panel listeners
        var listeners = {
            returnString: function (result, eOpts) {
                me.getViewModel().get('theTask')._config.set('filter_from_regex', result);
                me.getView().down('#filterSenderRegex_input').setValue(result);
                CMDBuildUI.util.Utilities.closePopup('administration-content-localizations-imports-view');
            },
            close: function (panel, eOpts) {
                CMDBuildUI.util.Utilities.closePopup('administration-content-localizations-imports-view');
            }
        };

        var popUp = CMDBuildUI.util.Utilities.openPopup(
            'administration-content-localizations-imports-view',
            'Regex filters',
            content,
            listeners, {
                ui: 'administration-actionpanel',
                width: '50%',
                height: '50%'
            }
        );
    },

    onSubjectRegexClick: function (button, event, eOpts) {
        var me = this;
        var content = {
            xtype: 'administration-components-splitstring-grid',
            viewModel: {
                data: {
                    theMessage: me.getViewModel().get('theTask.config.filter_subject_regex'),
                    theDivisor: ' OR ',
                    actions: me.getViewModel().get('actions')
                }
            }
        };

        // custom panel listeners
        var listeners = {
            returnString: function (result, eOpts) {
                me.getViewModel().get('theTask')._config.set('filter_subject_regex', result);
                me.getView().down('#filterSubjectRegex_input').setValue(result);
                CMDBuildUI.util.Utilities.closePopup('administration-content-localizations-imports-view');
            },
            close: function (panel, eOpts) {
                CMDBuildUI.util.Utilities.closePopup('administration-content-localizations-imports-view');
            }
        };

        var popUp = CMDBuildUI.util.Utilities.openPopup(
            'administration-content-localizations-imports-view',
            'Regex filters',
            content,
            listeners, {
                ui: 'administration-actionpanel',
                width: '50%',
                height: '50%'
            }
        );
    },

    privates: {

        generateCardFor: function (type, data, view) {
            switch (type) {
                case 'import_export':
                case 'import_file':
                case 'export_file':
                    // var fiedlsetHelper = CMDBuildUI.view.administration.content.tasks.card.helpers.FieldsetsHelper;
                    view.addStep('General properties', 0, [this.getGeneralPropertyPanel('theTask', 'step1', data)]);
                    view.addStep('Settings', 1, [this.getSettingsPanel('theTask', 'step2', data)]);
                    view.addStep('Cron', 2, [this.getCronPanel('theTask', 'step3', data)]);
                    view.addStep('Notifications', 3, [this.getNotificationPanel('theTask', 'step4', data)]);
                    break;
                case 'emailService':
                    view.addStep('General properties', 0, [this.getGeneralPropertyPanel('theTask', 'step1', data)]);
                    view.addStep('Settings', 1, [this.getSettingsPanel('theTask', 'step2', data)]);
                    view.addStep('Cron', 2, [this.getCronPanel('theTask', 'step3', data)]);
                    view.addStep('Parsing', 3, [this.getParsePanel('theTask', 'step4', data)]);
                    view.addStep('Process', 4, [this.getProcessPanel('theTask', 'step5', data)]);
                    view.addStep('Notifications', 5, [this.getNotificationPanel('theTask', 'step6', data)]);
                    break;
                case 'workflow':
                    view.addStep('General properties', 0, [this.getGeneralPropertyPanel('theTask', 'step1', data)]);
                    view.addStep('Cron', 1, [this.getCronPanel('theTask', 'step2', data)]);
                    break;

                default:
                    break;
            }
        }
    }
});