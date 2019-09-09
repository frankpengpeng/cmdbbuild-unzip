Ext.define('CMDBuildUI.view.classes.cards.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-tabpanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChange'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        // set view model variables
        var vm = this.getViewModel();
        var action = vm.get("action");
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        var isSimpleClass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(vm.get("objectTypeName")).isSimpleClass();

        // update url on window close
        view.mon(CMDBuildUI.util.Navigation.getManagementDetailsWindow(), 'beforeclose', this.onManagementDetailsWindowBeforeClose, this);

        // init tabs
        var configAttachments = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled);
        var card, tabcard, tabmasterdetail, tabnotes, tabrelations, tabhistory, tabemails, tabattachments;

        if (action === CMDBuildUI.mixins.DetailsTabPanel.actions.edit) {
            card = {
                xtype: 'classes-cards-card-edit',
                tab: CMDBuildUI.mixins.DetailsTabPanel.actions.edit,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.edit,
                hideInlineElements: false
            };
        } else if (action === CMDBuildUI.mixins.DetailsTabPanel.actions.create) {
            card = {
                xtype: 'classes-cards-card-create',
                tab: CMDBuildUI.mixins.DetailsTabPanel.actions.create,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.create
            };
        } else if (action === CMDBuildUI.mixins.DetailsTabPanel.actions.clone) {
            card = {
                xtype: 'classes-cards-card-create',
                cloneObject: true,
                tab: CMDBuildUI.mixins.DetailsTabPanel.actions.clone,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.clone
            };
        } else {
            card = {
                xtype: 'classes-cards-card-view',
                objectTypeName: vm.get("objectTypeName"),
                objectId: vm.get("objectId"),
                shownInPopup: true,
                autoScroll: true,
                tab: 'view',
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.view,
                hideInlineElements: false
            };
        }

        // Card tab
        tabcard = view.add({
            xtype: "panel",
            iconCls: 'x-fa fa-file-text',
            items: [Ext.apply(card, view.getObjectFormBaseConfig())],
            reference: card.tab,
            bodyPadding: 0,
            layout: 'fit',
            autoScroll: true,
            padding: 0,
            tabAction: card.tabAction,
            tabConfig: {
                tabIndex: 0,
                title: null,
                tooltip: CMDBuildUI.locales.Locales.common.tabs.card
            },
            bind: {
                disabled: '{disabled.card}'
            }
        });

        // Master/Detail tab
        if (!isSimpleClass && privileges.card_tab_detail_access) {
            tabmasterdetail = view.add({
                xtype: 'relations-masterdetail-tabpanel',
                iconCls: 'x-fa fa-th-list',
                reference: "details",
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.masterdetail,
                tabConfig: {
                    tabIndex: 1,
                    title: null,
                    tooltip: CMDBuildUI.locales.Locales.common.tabs.details
                }
            });
        }

        // Notes tab
        if (!isSimpleClass && privileges.card_tab_note_access) {
            tabnotes = view.add({
                xtype: 'notes-panel',
                iconCls: 'x-fa fa-sticky-note',
                reference: "notes",
                autoScroll: true,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.notes,
                tabConfig: {
                    tabIndex: 2,
                    title: null,
                    tooltip: CMDBuildUI.locales.Locales.common.tabs.notes
                },
                bind: {
                    disabled: '{disabled.notes}'
                }
            });
        }

        // Relations tab
        if (!isSimpleClass && privileges.card_tab_relation_access) {
            tabrelations = view.add({
                xtype: 'relations-list-container',
                iconCls: 'x-fa fa-link',
                reference: 'relations',
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.relations,
                tabConfig: {
                    tabIndex: 3,
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
        if (!isSimpleClass && privileges.card_tab_history_access) {
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
                    tabIndex: 4,
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
        if (!isSimpleClass && privileges.card_tab_email_access) {
            tabemails = view.add({
                xtype: "emails-grid",
                iconCls: 'x-fa fa-envelope',
                reference: view._emailReference,
                autoScroll: true,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.emails,
                bodyPadding: 0,
                tabConfig: {
                    tabIndex: 5,
                    title: null,
                    tooltip: CMDBuildUI.locales.Locales.common.tabs.emails
                },
                bind: {
                    disabled: '{disabled.email}'
                }
            });
        }

        // Attachments tab
        if (!isSimpleClass && configAttachments && privileges.card_tab_attachment_access) {
            tabattachments = view.add({
                xtype: "attachments-container",
                iconCls: 'x-fa fa-paperclip',
                reference: 'attachments',
                autoScroll: true,
                tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.attachments,
                tabConfig: {
                    tabIndex: 6,
                    title: null,
                    tooltip: CMDBuildUI.locales.Locales.common.tabs.attachments
                },
                padding: 0,
                viewModel: {
                    data: {
                        targetType: vm.get("objectType"),
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
            case CMDBuildUI.mixins.DetailsTabPanel.actions.masterdetail:
                activetab = tabmasterdetail;
                break;
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
            activetab = tabcard;
        }
        view.setActiveTab(activetab);
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
        CMDBuildUI.util.Ajax.setActionId("class.card." + newtab.tabAction + ".open");
        this.redirectTo(this.getBasePath() + '/' + newtab.tabAction);
    },

    onManagementDetailsWindowBeforeClose: function() {
        var me = this;
        CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(undefined);
        // CMDBuildUI.util.Utilities.redirectTo(me.getBasePath(), true);
    },

    privates: {
        /**
         * Get resource base path for routing.
         * @return {String}
         */
        getBasePath: function () {
            var vm = this.getViewModel();
            var url = 'classes/' + vm.get("objectTypeName") + '/cards';
            if (vm.get("objectId")) {
                url += '/' + vm.get("objectId");
            }
            return url;
        }
    }
});
