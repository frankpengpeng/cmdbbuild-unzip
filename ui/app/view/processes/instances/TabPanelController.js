Ext.define('CMDBuildUI.view.processes.instances.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.processes-instances-tabpanel',

    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChange'
        }
    },

    /**
     * @param {CMDBuildUI.view.processes.instances.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        // set view model variables
        var vm = this.getViewModel();
        var action = vm.get("action");
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");

        // update url on window close
        view.mon(CMDBuildUI.util.Navigation.getManagementDetailsWindow(), 'beforeclose', this.onManagementDetailsWindowBeforeClose, this);

        // init tabs
        var configAttachments = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled);
        var activity, tabactivity, tabnotes, tabrelations, tabhistory, tabemails, tabattachments;

        if (action === CMDBuildUI.mixins.DetailsTabPanel.actions.edit) {
            activity = {
                xtype: 'processes-instances-instance-edit',
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.edit
            };
        } else if (action === CMDBuildUI.mixins.DetailsTabPanel.actions.create) {
            activity = {
                xtype: 'processes-instances-instance-create',
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.create
            };
        } else {
            activity = {
                xtype: 'processes-instances-instance-view',
                shownInPopup: true,
                autoScroll: true,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.view
            };
        }

        // Activity tab
        tabactivity = view.add({
            xtype: "panel",
            iconCls: 'x-fa fa-file-text',
            items: [Ext.apply(activity, view.getObjectFormBaseConfig())],
            reference: "activity",
            layout: 'fit',
            autoScroll: true,
            padding: 0,
            bodyPadding: 0,
            tabAction: activity.tabAction,
            tabConfig: {
                tabIndex: 0,
                title: null,
                tooltip: CMDBuildUI.locales.Locales.common.tabs.activity
            },
            bind: {
                disabled: '{disabled.activity}'
            }
        });

        // Notes tab
        if (privileges.flow_tab_note_access) {
            tabnotes = view.add({
                xtype: 'notes-panel',
                iconCls: 'x-fa fa-sticky-note',
                itemId: 'tab-notes',
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.notes,
                tabConfig: {
                    tabIndex: 1,
                    title: null,
                    tooltip: CMDBuildUI.locales.Locales.common.tabs.notes
                },
                bind: {
                    disabled: '{disabled.notes}'
                }
            });
        }

        // Relations tab
        if (privileges.flow_tab_relation_access) {
            tabrelations = view.add({
                xtype: 'relations-list-container',
                iconCls: 'x-fa fa-link',
                reference: 'relations',
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.relations,
                tabConfig: {
                    tabIndex: 2,
                    title: null,
                    tooltip: CMDBuildUI.locales.Locales.common.tabs.relations
                },
                autoScroll: true,
                bind: {
                    disabled: '{disabled.relations}'
                }
            });
        }

        // History tab
        if (privileges.flow_tab_history_access) {
            tabhistory = view.add({
                xtype: "panel",
                iconCls: 'x-fa fa-history',
                items: [{
                    xtype: 'history-grid',
                    autoScroll: true
                }],
                reference: 'history',
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.history,
                tabConfig: {
                    tabIndex: 3,
                    title: null,
                    tooltip: CMDBuildUI.locales.Locales.common.tabs.history
                },
                layout: 'fit',
                autoScroll: true,
                padding: 0,
                bind: {
                    disabled: '{disabled.history}'
                }
            });
        }

        // Email tab
        if (privileges.flow_tab_email_access) {
            tabemails = view.add({
                xtype: "emails-grid",
                iconCls: 'x-fa fa-envelope',
                itemId: 'tab-emails',
                reference: view._emailReference,
                disabled: true,
                bodyPadding: 0,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.emails,
                tabConfig: {
                    tabIndex: 4,
                    title: null,
                    tooltip: CMDBuildUI.locales.Locales.common.tabs.emails
                },
                bind: {
                    disabled: '{disabled.emails}'
                }
            });
        }

        // Attachments tab
        if (configAttachments && privileges.flow_tab_attachment_access) {
            tabattachments = view.add({
                xtype: "attachments-container",
                iconCls: 'x-fa fa-paperclip',
                itemId: 'tab-attachments',
                reference: 'attachments',
                autoScroll: true,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.attachments,
                tabConfig: {
                    tabIndex: 5,
                    title: null,
                    tooltip: CMDBuildUI.locales.Locales.common.tabs.attachments
                },
                padding: 0,
                viewModel: {
                    data: {
                        targetType: 'process',
                        targetTypeName: vm.get("objectTypeName"),
                        targetId: vm.get("objectId")
                    }
                },
                bind: {
                    disabled: '{disabled.attachments}'
                }
            });
        }

        // set view active tab
        var activetab;
        switch (action) {
            case CMDBuildUI.mixins.DetailsTabPanel.actions.notes:
                activetab = tabnotes;
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.relations:
                activetab = tabrelations;
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.history:
                activetab = tabhistory;
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.emails:
                activetab = tabemails;
                break;
            case CMDBuildUI.mixins.DetailsTabPanel.actions.attachments:
                activetab = tabattachments;
                break;
        }
        if (!activetab) {
            activetab = tabactivity;
        }
        view.setActiveTab(activetab);
    },

    onManagementDetailsWindowBeforeClose: function () {
        var me = this;
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(undefined);
        CMDBuildUI.util.Navigation.updateCurrentManagementContextActivity(undefined);
        // CMDBuildUI.util.Utilities.redirectTo(me.getBasePath(true), true);
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChange: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(newtab.tabAction);
        this.getViewModel().getParent().set("actionDescription", newtab.tabConfig.tooltip);
        CMDBuildUI.util.Ajax.setActionId("proc.inst." + newtab.tabAction + ".open");
        this.redirectTo(this.getBasePath(true) + '/' + newtab.tabAction);
    },

    privates: {
        /**
         * Get resource base path for routing.
         * @param {Boolean} includeactivity
         * @return {String}
         */
        getBasePath: function (includeactivity) {
            includeactivity = includeactivity || false;
            var vm = this.getViewModel();
            var url = Ext.String.format(
                "processes/{0}/instances",
                vm.get("objectTypeName")
            );
            if (vm.get("objectId")) {
                url += '/' + vm.get("objectId");
                if (includeactivity && vm.get("activityId")) {
                    url += '/activities/' + vm.get("activityId");
                }
            }
            return url;
        }
    }
});