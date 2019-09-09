Ext.define('CMDBuildUI.view.widgets.startworkflow.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-startworkflow-panel',
    control: {
        "#": {
            beforerender: "onBeforeRender"
        },
        '#startworkflowcancelBtn': {
            click: 'onstartworkflowcancelBtnClick'
        },
        '#startworkflowexecuteBtn': {
            click: 'onstartworkflowexecuteBtnClick'
        },
        '#startworkflowsaveBtn': {
            click: 'onstartworkflowsaveBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.startworkflow.PanelController} view
     * @param {Object} eOpts
     */

    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = view.getViewModel();
        var theWidget = vm.get('theWidget');
        var theTarget = vm.get('theTarget');
        vm.bind({
            bindTo: '{theTarget}'
        }, function (target) {
            var presets = theWidget.getData().preset;
            for (var key in presets) {
                var presetvar = me.extractVariableFromString(presets[key], target);
                target.set(key, presetvar);
            }
        });
        var objectTypeName = theWidget.get('workflowName');
        vm.set('objectTypeName', objectTypeName);
        var panel = view.add({
            xtype: 'processes-instances-instance-create',
            buttons: [{
                reference: 'startworkflowsaveBtn',
                itemId: 'startworkflowsaveBtn',
                text: CMDBuildUI.locales.Locales.common.actions.save,
                ui: 'management-action-small',
                bind: {
                    hidden: '{!showSaveButton}'
                },
                autoEl: {
                    'data-testid': 'processinstance-save'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                }
            }, {
                reference: 'startworkflowexecuteBtn',
                itemId: 'startworkflowexecuteBtn',
                text: CMDBuildUI.locales.Locales.common.actions.execute,
                ui: 'management-action-small',
                formBind: true, //only enabled once the form is valid
                disabled: true,
                bind: {
                    hidden: '{!showExecuteButton}'
                },
                autoEl: {
                    'data-testid': 'processinstance-execute'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.execute'
                }
            }, {
                reference: 'startworkflowcancelBtn',
                itemId: 'startworkflowcancelBtn',
                ui: 'secondary-action-small',
                text: CMDBuildUI.locales.Locales.common.actions.cancel,
                autoEl: {
                    'data-testid': 'processinstance-cancel'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                }
            }]
        });
        panel.getViewModel().bind({
            bindTo: '{theObject}'
        }, function (theObject) {
            targetData = theTarget.getData();
            for (var key in theObject.getData()) {
                theObject.set(key, targetData[key]);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onstartworkflowcancelBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.Utilities.closePopup('popup-show-widget');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onstartworkflowexecuteBtnClick: function (button, e, eOpts) {
        var panel = this.getView().down('processes-instances-instance-create');
        var theWidget = panel.getViewModel().get('theWidget');
        var theTarget = panel.getViewModel().get('theTarget');
        panel.executeProcess({
            success: function (record, operation) {
                if (theWidget.get('_output')) {
                    theTarget.set(theWidget.get('_output'), record.get('_id'));
                }
                CMDBuildUI.util.Utilities.closePopup('popup-show-widget');
            },
            callback: function (record, operation, success) {
                if (panel && panel.loadMask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(panel.loadMask);
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onstartworkflowsaveBtnClick: function (button, e, eOpts) {
        var panel = this.getView().down('processes-instances-instance-create');
        var theTarget = panel.getViewModel().get('theTarget');
        var theWidget = panel.getViewModel().get('theWidget');
        panel.saveProcess({
            success: function (record, operation) {
                if (theWidget.get('_output')) {
                    theTarget.set(theWidget.get('_output'), record.get('_id'));
                }
                CMDBuildUI.util.Utilities.closePopup('popup-show-widget');
            },
            callback: function (record, operation, success) {
                if (panel && panel.loadMask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(panel.loadMask);
                }
            }
        });
    },

    /**
     * Resolve variable.
     * @param {String} variable
     * @param {CMDBuildUI.model.base.Base} theTarget 
     * @return {*} The variable resolved.
     */
    extractVariableFromString: function (variable, theTarget) {
        variable = variable.replace("{", "").replace("}", "");
        var s_variable = variable.split(":");
        var resolvedVariable = null;
        if (s_variable[0] === "server") {
            resolvedVariable = CMDBuildUI.util.ecql.Resolver.resolveServerVariables([s_variable[1]], theTarget);
            return Object.values(resolvedVariable)[0];
        } else if (s_variable[0] === "client") {
            resolvedVariable = CMDBuildUI.util.ecql.Resolver.resolveClientVariables([s_variable[1]], theTarget);
            return Object.values(resolvedVariable)[0];
        } else if (s_variable.length === 1 && theTarget.getField(s_variable[0])) {
            return theTarget.get(s_variable[0]);
        }
        return variable;
    }
});