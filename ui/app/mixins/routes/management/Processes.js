Ext.define('CMDBuildUI.mixins.routes.management.Processes', {
    mixinId: 'managementroutes-processes-mixin',

    /******************* PROCESS INSTANCES GRID ********************/
    /**
     * Before show process instances grid
     * 
     * @param {String} processName
     * @param {Object} action
     */
    onBeforeShowProcessInstancesGrid: function (processName, action) {
        var me = this;
        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.process;
        CMDBuildUI.util.Stores.loadFlowStatuses().then(function() {
            CMDBuildUI.util.helper.ModelHelper.getModel(type, processName).then(function (model) {
                if (CMDBuildUI.util.Navigation.checkCurrentContext(type, processName)) {
                    action.stop();
                } else {
                    action.resume();
                }
            }, function () {
                me.redirect("management");
                Ext.Msg.alert('Error', 'Process non found!');
            });
        });
    },
    /**
     * Show process instances grid
     * 
     * @param {String} processName
     * @param {Numeric} instanceId This attribute is used when the function
     * is called dicretly from code, not from router.
     * @param {String} activityId 
     */
    showProcessInstancesGrid: function (processName, instanceId, activityId) {
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
        CMDBuildUI.util.Navigation.addIntoManagemenetContainer('processes-instances-grid', {
            objectTypeName: processName,
            maingrid: true,
            viewModel: {
                data: {
                    objectTypeName: processName,
                    selectedId: instanceId,
                    selectedActivity: activityId
                }
            }
        });

        // fire global event objecttypechanged
        Ext.GlobalEvents.fireEventArgs("objecttypechanged", [processName]);

        // update current context
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
            processName
        );
    },

    /**
     * @param {String} processName 
     * @param {String|Number} instanceId 
     * @param {String} activityId 
     * @param {Object} action 
     */
    onBeforeShowProcessInstance: function (processName, instanceId, activityId, action) {
        var me = this;
        // fix variables if activity id is not defined
        if (!action) {
            action = activityId;
            activityId = null;
        }

        // break if user is asking to create new instance
        if (instanceId === 'new') {
            return action.stop();
        }

        // get model
        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.process;
        CMDBuildUI.util.Stores.loadFlowStatuses().then(function() {
            CMDBuildUI.util.helper.ModelHelper.getModel(type, processName).then(function (model) {
                if (CMDBuildUI.util.Navigation.checkCurrentContext(type, processName, true)) {
                    action.stop();
                } else {
                    action.resume();
                }
            }, function () {
                action.stop();
            });
        });
    },
    /**
     * @param {String} processName 
     * @param {String|Number} instanceId 
     * @param {String} activityId  
     */
    showProcessInstance: function (processName, instanceId, activityId) {
        // show grid
        this.showProcessInstancesGrid(processName, instanceId, activityId);
    },

    /******************* PROCESS INSTANCE DETAIL WINDOW ********************/
    /**
     * Before show process instance view
     * 
     * @param {String} processName
     * @param {Number} instanceId
     * @param {String} activityId
     * @param {Object} action
     */
    onBeforeShowProcessInstanceWindow: function (processName, instanceId, activityId, action) {
        var me = this;
        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.process;

        // fix variables for create form
        if (!action && !activityId) {
            action = instanceId;
            instanceId = null;
        }

        // load model
        CMDBuildUI.util.Stores.loadFlowStatuses().then(function() {
            CMDBuildUI.util.helper.ModelHelper.getModel(type, processName).then(function (model) {
                // check consisntence of main content
                if (!CMDBuildUI.util.Navigation.checkCurrentContext(type, processName, true)) {
                    // show instances grid for processName
                    me.showProcessInstancesGrid(processName, instanceId);
                }
                // resume action
                action.resume();
            }, function () {
                Ext.Msg.alert('Error', 'Process non found!');
                action.stop();
            });
        });
    },

    /**
     * Show process instance create
     * 
     * @param {String} processName
     * @param {Object} action
     */
    showProcessInstanceCreate: function (processName) {
        this.showProcessInstanceTabPanel(
            processName,
            null,
            null,
            CMDBuildUI.mixins.DetailsTabPanel.actions.create
        );
    },

    /**
     * Show process instance view
     * 
     * @param {String} processName
     * @param {Number} instanceId
     * @param {String} activityId
     */
    showProcessInstanceView: function (processName, instanceId, activityId) {
        this.showProcessInstanceTabPanel(
            processName,
            instanceId,
            activityId,
            CMDBuildUI.mixins.DetailsTabPanel.actions.view
        );
    },
    /**
     * Show process instance edit
     * 
     * @param {String} processName
     * @param {Number} instanceId
     * @param {String} activityId
     */
    showProcessInstanceEdit: function (processName, instanceId, activityId) {
        this.showProcessInstanceTabPanel(
            processName,
            instanceId,
            activityId,
            CMDBuildUI.mixins.DetailsTabPanel.actions.edit
        );
    },

    /**
     * Show notes view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showProcessInstanceNotes: function (processName, instanceId, activityId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.flow_tab_note_access) {
            this.showProcessInstanceTabPanel(
                processName,
                instanceId,
                activityId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.notes
            );
        } else {
            this.redirectTo(Ext.String.format("processes/{0}/instances/{1}/view"), className, cardId);
        }
    },

    /**
     * Show relation view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showProcessInstanceRelations: function (processName, instanceId, activityId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.flow_tab_relation_access) {
            this.showProcessInstanceTabPanel(
                processName,
                instanceId,
                activityId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.relations
            );
        } else {
            this.redirectTo(Ext.String.format("processes/{0}/instances/{1}/view"), className, cardId);
        }
    },

    /**
     * Show history view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showProcessInstanceHistory: function (processName, instanceId, activityId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.flow_tab_history_access) {
            this.showProcessInstanceTabPanel(
                processName,
                instanceId,
                activityId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.history
            );
        } else {
            this.redirectTo(Ext.String.format("processes/{0}/instances/{1}/view"), className, cardId);
        }
    },

    /**
     * Show emails view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showProcessInstanceEmails: function (processName, instanceId, activityId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.flow_tab_email_access) {
            this.showProcessInstanceTabPanel(
                processName,
                instanceId,
                activityId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.emails
            );
        } else {
            this.redirectTo(Ext.String.format("processes/{0}/instances/{1}/view"), className, cardId);
        }
    },

    /**
     * Show relation view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showProcessInstanceAttachments: function (processName, instanceId, activityId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled) && privileges.flow_tab_attachment_access) {
            this.showProcessInstanceTabPanel(
                processName,
                instanceId,
                activityId,
                CMDBuildUI.mixins.DetailsTabPanel.actions.attachments
            );
        } else {
            this.redirectTo(Ext.String.format("processes/{0}/instances/{1}/view"), className, cardId);
        }
    },

    privates: {
        /**
         * 
         * @param {String} processName 
         * @param {Number|String} instanceId 
         * @param {String} activityId 
         * @param {String} action 
         */
        showProcessInstanceTabPanel: function (processName, instanceId, activityId, action) {
            if (
                !CMDBuildUI.util.Navigation.checkCurrentManagementContextAction(action) || 
                !CMDBuildUI.util.Navigation.checkCurrentManagementContextActivity(activityId)
            ) {
                CMDBuildUI.util.Navigation.addIntoManagementDetailsWindow('processes-instances-tabpanel', {
                    viewModel: {
                        data: {
                            objectTypeName: processName,
                            objectId: instanceId,
                            activityId: activityId,
                            action: action
                        }
                    }
                });
                CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(action);
                CMDBuildUI.util.Navigation.updateCurrentManagementContextActivity(activityId);
            }
        }
    }
});