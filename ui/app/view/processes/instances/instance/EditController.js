Ext.define('CMDBuildUI.view.processes.instances.instance.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.processes-instances-instance-edit',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#executeBtn': {
            click: 'onExecuteBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.card.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.set("basepermissions.edit", true);
        // get instance model
        CMDBuildUI.util.helper.ModelHelper.getModel(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
            vm.get("objectTypeName")
        ).then(function (model) {
            vm.set("objectModel", model);

            // load process instance
            vm.linkTo("theObject", {
                type: model.getName(),
                id: vm.get("objectId")
            });

            // load activity
            view.loadActivity();
        }, function () {
            Ext.Msg.alert('Error', 'Process non found!');
        });
    },

    /**
     * Unlock card on management details window close.
     * @param {CMDBuildUI.view.processes.instances.instance.Edit} view 
     * @param {Object} eOpts 
     */
    onBeforeDestroy: function (view, eOpts) {
        if (view._isLocked) {
            view.lookupViewModel().get("theObject").removeLock();
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onExecuteBtnClick: function (button, e, eOpts) {
        var me = this;
        this.getView().executeProcess({
            success: function (record, operation) {
                var forcereload = false;
                // get available tasks
                var tasks = record.get("_tasklist");
                var isRunning = record.get("status") == me.getView().getOpenRunningStatusId();
                // redirect to next task
                if (tasks && tasks.length === 1 && isRunning) {
                    var activity = tasks[0];
                    var path = Ext.String.format(
                        'processes/{0}/instances/{1}/activities/{2}',
                        record.get("_type"),
                        record.getId(),
                        activity._id
                    );
                    // redirect to edit form if activity is writablo by the user
                    if (activity.writable) {
                        path += "/edit";
                    } else {
                        path += "/view";
                    }
                    // clear context activity to disable redirect prevention.
                    CMDBuildUI.util.Navigation.updateCurrentManagementContextActivity(null);
                    me.redirectTo(path, true);
                } else {
                    forcereload = isRunning;
                    me.closeWindow();
                }

                if (!isRunning) {
                    var path = Ext.String.format(
                        'processes/{0}/instances',
                        record.get("_type")
                    );
                    me.redirectTo(path);
                    record = null;
                }


                // fire global event process instance updated
                Ext.GlobalEvents.fireEventArgs("processinstanceupdated", [record, forcereload]);
            },
            callback: function (record, operation, success) {
                if (me.getView() && me.getView().loadMask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(me.getView().loadMask);
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        this.getView().saveProcess({
            success: function (record, operation) {
                // fire global event process instance updated
                Ext.GlobalEvents.fireEventArgs("processinstanceupdated", [record]);

                me.closeWindow();
            },
            callback: function (record, operation, success) {
                if (me.getView() && me.getView().loadMask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(me.getView().loadMask);
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.closeWindow();
    },

    privates: {
        /**
         * Close window
         */
        closeWindow: function () {
            this.getView().closeDetailWindow();
        }
    }

});