
Ext.define('CMDBuildUI.view.processes.instances.instance.Create', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.processes.instances.instance.CreateController',
        'CMDBuildUI.view.processes.instances.instance.CreateModel'
    ],

    mixins: [
        'CMDBuildUI.view.processes.instances.instance.Mixin'
    ],

    alias: 'widget.processes-instances-instance-create',
    controller: 'processes-instances-instance-create',
    viewModel: {
        type: 'processes-instances-instance-create'
    },

    modelValidation: true,
    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.create,

    /**
     * Load activity data and display form.
     * Overrides Mixin loadActivity
     * 
     * @param {CMDBuildUI.model.processes.Instance} model
     */
    loadActivity: function (model) {
        var vm = this.getViewModel();

        var activitiesStore = Ext.create("Ext.data.Store", {
            model: 'CMDBuildUI.model.processes.Activity',
            autoLoad: false,
            autoDestroy: true,
            proxy: {
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Processes.getStartActivitiesUrl(
                    vm.get("objectTypeName")
                )
            }
        });

        // load activity and save variables in ViewModel
        activitiesStore.load({
            scope: this,
            callback: function (records, operation, success) {
                if (success && records && records.length) {
                    vm.set("activityId", records[0].getId());
                    vm.set("theActivity", records[0]);

                    // get the process definition
                    var processes = Ext.getStore('processes.Processes');
                    vm.set("theProcess", processes.getById(vm.get("objectTypeName")));

                    // render form
                    this.showForm();
                }
            }
        });
    },

    /**
     * Render form fields
     */
    showForm: function () {
        var me = this;
        var vm = this.getViewModel();

        // attributes configuration from activity
        var attrsConf = this.getAttributesConfigFromActivity();

        // message panel
        var message_panel = this.getMessageBox();

        // action combobox
        var action_field = this.getActionField();

        if (vm.get("activity_action.fieldname")) {
            Ext.Array.remove(attrsConf.visibleAttributes, vm.get("activity_action.fieldname"));
        }

        // get form fields as fieldsets
        var formitems = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get("objectModel"), {
            mode: CMDBuildUI.util.helper.FormHelper.formmodes.create,
            showAsFieldsets: true,
            attributesOverrides: attrsConf.overrides,
            visibleAttributes: attrsConf.visibleAttributes
        });

        // add action_field as first element in form items
        Ext.Array.insert(formitems, 0, [message_panel, action_field]);

        this.add(this.getMainPanelForm(formitems));
        this.addConditionalVisibilityRules();

        // add auto value rules
        this.addAutoValueRules();

        // validate form before edit
        Ext.asap(function() {
            me.isValid();
        });
    }

});
