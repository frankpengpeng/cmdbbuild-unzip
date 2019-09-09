Ext.define('CMDBuildUI.view.processes.instances.instance.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.processes-instances-instance-create',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
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

        // get instance model
        CMDBuildUI.util.helper.ModelHelper.getModel(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
            vm.get("objectTypeName")
        ).then(function (model) {
            vm.set("objectModel", model);

            // create process instance
            vm.linkTo("theObject", {
                type: model.getName(),
                create: true
            });

            // load activity for new process
            view.loadActivity();
        }, function () {
            Ext.Msg.alert('Error', 'Process non found!');
        });
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
                    me.redirectTo(path);
                } else {
                    forcereload = true;
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
