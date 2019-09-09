
Ext.define('CMDBuildUI.view.processes.instances.instance.Edit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.processes.instances.instance.EditController',
        'CMDBuildUI.view.processes.instances.instance.EditModel'
    ],

    mixins: [
        'CMDBuildUI.view.processes.instances.instance.Mixin'
    ],

    alias: 'widget.processes-instances-instance-edit',
    controller: 'processes-instances-instance-edit',
    viewModel: {
        type: 'processes-instances-instance-edit'
    },

    modelValidation: true,
    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.update,

    /**
     * Render form fields
     * 
     * @param {CMDBuildUI.model.processes.Instance} model
     */
    showForm: function () {
        var me = this;
        var vm = this.getViewModel();

        vm.bind({
            bindTo: '{theObject}'
        }, function(object) {
            var activity = vm.get("theActivity");

            function redirectToView() {
                if (activity) {
                    CMDBuildUI.util.Utilities.redirectTo(Ext.String.format(
                        'processes/{0}/instances/{1}/activities/{2}/view',
                        object.get("_type"),
                        object.getId(),
                        vm.get("theActivity").getId()
                    ));
                } else {
                    CMDBuildUI.util.Utilities.redirectTo(Ext.String.format(
                        'processes/{0}/instances/{1}/view',
                        object.get("_type"),
                        object.getId()
                    ));
                }
            }

            // check edit permission
            if (activity && vm.get("theActivity").get("writable")) {
                object.addLock().then(function(success) {
                    if (success) {
                        me._isLocked = true;
                        // attributes configuration from activity
                        var attrsConf = me.getAttributesConfigFromActivity();

                        // message panel
                        var message_panel = me.getMessageBox();

                        // action combobox
                        var action_field = me.getActionField();

                        if (vm.get("activity_action.fieldname")) {
                            Ext.Array.remove(attrsConf.visibleAttributes, vm.get("activity_action.fieldname"));
                        }

                        // get form fields as fieldsets
                        var formitems = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get("objectModel"), {
                            mode: me.formmode,
                            showAsFieldsets: true,
                            attributesOverrides: attrsConf.overrides,
                            visibleAttributes: attrsConf.visibleAttributes
                        });

                        // add action_field as first element in form items
                        Ext.Array.insert(formitems, 0, [message_panel, action_field]);
                        // create items
                        var items = [
                            me.getProcessStatusBar(), 
                            me.getMainPanelForm(formitems)
                        ];

                        me.add(items);
                        me.addConditionalVisibilityRules();

                        // add auto value rules
                        me.addAutoValueRules();

                        // validate form before edit
                        Ext.asap(function() {
                            me.isValid();
                        });
                    } else {
                        redirectToView(); 
                    }
                });
            } else {
                redirectToView(); 
            }
        });
    }
});
