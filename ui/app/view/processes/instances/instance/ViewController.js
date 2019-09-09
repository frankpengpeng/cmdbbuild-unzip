Ext.define('CMDBuildUI.view.processes.instances.instance.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.processes-instances-instance-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            clicK: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#relgraphBtn': {
            click: 'onRelationGraphBtnClick'
        },
        '#openTabsBtn': {
            click: 'onOpenTabsBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.card.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = this.getViewModel();

        // read instance parameters from row configuration
        // when view is rendered inside grid row
        if (!view.getShownInPopup()) {
            var config = view.getInitialConfig();
            if (!Ext.isEmpty(config._rowContext)) {
                // get widget record
                var record = config._rowContext.record;
                if (record && record.getData()) {
                    // set view model variable
                    vm.set("objectId", record.getId());
                    vm.set("objectTypeName", record.get("_type"));
                    vm.set("activityId", record.get("_activity_id"));
                }
            }
        }

        function modelLoadSuccess(model) {
            vm.set("objectModel", model);

            // load process instance
            vm.linkTo("theObject", {
                type: model.getName(),
                id: vm.get("objectId")
            });

            // load activity
            view.loadActivity();
        }

        // get instance model
        CMDBuildUI.util.helper.ModelHelper.getModel(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
            vm.get("objectTypeName")
        ).then(modelLoadSuccess, function () {
            Ext.Msg.alert('Error', 'Process non found!');
        });
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.card.View} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        view.loadmask = null;
        if (!view.getShownInPopup()) {
            view.setMinHeight(100);
            view.loadmask = CMDBuildUI.util.Utilities.addLoadMask(view);
        }
    },

    /**
     * Triggered on edit tool click.
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (tool, event, eOpts) {
        CMDBuildUI.util.Ajax.setActionId("proc.inst.edit");
        this.redirectTo(this.getBasePath(true) + "/edit", true);
    },

    /**
     * Triggered on edit tool click.
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */

    onOpenBtnClick: function (tool, event, eOpts) {
        CMDBuildUI.util.Ajax.setActionId("proc.inst.view");
        this.redirectTo(this.getBasePath(true) + "/view", true);
    },

    /**
     * Triggered on edit tool click.
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */

    onDeleteBtnClick: function (tool, event, eOpts) {
        var view = this.getView();
        var vm = view.lookupViewModel();
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.processes.abortconfirmation,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId("proc.inst.delete");
                    // get the object
                    vm.get("theObject").erase({
                        success: function (record, operation) {
                            // fire global card deleted event
                            Ext.GlobalEvents.fireEventArgs("processinstanceaborted");
                            // close detail window
                            if (view.getShownInPopup()) {
                                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
                            }
                        }
                    });
                }
            }, this);
    },

    /**
     * triggered on the relation graph btn click
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onRelationGraphBtnClick: function (tool, event, eOpts) {
        CMDBuildUI.util.Ajax.setActionId("proc.inst.relgraph.open");
        var me = this;
        var theObject = me.getViewModel().get('theObject');
        CMDBuildUI.util.Utilities.openPopup('graphPopup', CMDBuildUI.locales.Locales.relationGraph.relationGraph, {
            xtype: 'graph-graphcontainer',
            _id: theObject.get('_id'),
            _type_name: theObject.get('_type_name'),
            _type: theObject.get('_type')
        });
    },

    /**
     * Triggered on open tabs button click.
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onOpenTabsBtnClick: function (tool, event, eOpts) {
        var me = this;
        var configAttachments = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled);
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        var items = [];

        // notes action
        if (privileges.flow_tab_note_access) {
            items.push({
                tooltip: CMDBuildUI.locales.Locales.common.tabs.notes,
                iconCls: 'x-fa fa-sticky-note',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        CMDBuildUI.util.Ajax.setActionId("proc.inst.notes.open");
                        me.redirectTo(me.getBasePath(true) + '/notes', true);
                    }
                }
            });
        }

        // relations action
        if (privileges.flow_tab_relation_access) {
            items.push({
                tooltip: CMDBuildUI.locales.Locales.common.tabs.relations,
                iconCls: 'x-fa fa-link',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        CMDBuildUI.util.Ajax.setActionId("proc.inst.relations.open");
                        me.redirectTo(me.getBasePath(true) + '/relations', true);
                    }
                }
            });
        }

        // history action
        if (privileges.flow_tab_history_access) {
            items.push({
                tooltip: CMDBuildUI.locales.Locales.common.tabs.history,
                iconCls: 'x-fa fa-history',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        CMDBuildUI.util.Ajax.setActionId("proc.inst.history.open");
                        me.redirectTo(me.getBasePath(true) + '/history', true);
                    }
                }
            });
        }

        // email action
        if (privileges.flow_tab_email_access) {
            items.push({
                tooltip: CMDBuildUI.locales.Locales.common.tabs.emails,
                iconCls: 'x-fa fa-envelope',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        CMDBuildUI.util.Ajax.setActionId("proc.inst.emails.open");
                        me.redirectTo(me.getBasePath(true) + '/emails', true);
                    }
                }
            });
        }

        // attachments action
        if (configAttachments && privileges.flow_tab_attachment_access) {
            items.push({
                tooltip: CMDBuildUI.locales.Locales.common.tabs.attachments,
                iconCls: 'x-fa fa-paperclip',
                height: 32,
                hidden: !configAttachments,
                listeners: {
                    click: function (menuitem, eOpts) {
                        CMDBuildUI.util.Ajax.setActionId("proc.inst.attachments.open");
                        me.redirectTo(me.getBasePath(true) + '/attachments', true);
                    }
                }
            });
        }

        if (items.length) {
            var menu = Ext.create('Ext.menu.Menu', {
                autoShow: true,
                items: items,
                ui: 'actionmenu'
            });
            menu.setMinWidth(35);
            menu.setWidth(35);
            menu.alignTo(tool.el.id, 't-b?');
        }
    },

    privates: {
        /**
         * Get resource base path for routing.
         * @param {Boolean} includeactivity
         * @return {String}
         */
        getBasePath: function (includeactivity) {
            var vm = this.getViewModel();
            var url = Ext.String.format(
                "processes/{0}/instances/{1}",
                vm.get("objectTypeName"),
                vm.get("objectId")
            );
            if (includeactivity) {
                url = Ext.String.format(
                    "{0}/activities/{1}",
                    url,
                    vm.get("activityId")
                );
            }
            return url;
        }
    }

});