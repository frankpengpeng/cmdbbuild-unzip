Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.ViewModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        // add some model
    ],
    alias: 'viewmodel.administration-content-groupsandpermissions-view',
    data: {
        activeTab: 0,
        objectTypeName: null,
        theGroup: null,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        disabledTabs: {
            group: false,
            permissions: true,
            listOfUsers: true,
            uiConfig: true,
            defaultFilters: true
        },
        toolbarHiddenButtons: {
            edit: true, // action !== view
            enable: true, //action !== view && theProcess.active
            disable: true // action !== view && !theProcess.active
        }
    },

    formulas: {
        groupLabel: {
            bind: '{theGroup}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group;
            }
        },
        disablePermissionsTabIfAdmin: {
            bind: '{theGroup}',
            get: function (theGroup) {
                var me = this;
                this.set('activeTab', this.getView().up('administration-content').getViewModel().get('activeTabs.groups') || 0);
                var view = me.getView().down('administration-content-groupsandpermissions-tabpanel');

                if (view.items && view.items.items.length) {
                    view.items.items[1].setDisabled(theGroup.get('type') === 'admin');
                }
            }
        },
        invertedField: {
            bind: '{theGroup}',
            get: function (theGroup) {
                this.invertAccessFields(theGroup);
            }
        },
        action: {
            bind: '{theGroup}',
            get: function (theGroup) {
                if (this.get('actions.edit')) {
                    this.set('isFormHidden', false);
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (this.get('actions.add')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else if (this.get('actions.view')) {
                    this.set('isFormHidden', false);
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
                this.configToolbarButtons();
            }
        },
        getToolbarButtons: {
            bind: {
                active: '{theGroup.active}'
            },
            get: function (data) {
                this.configToolbarButtons();
            }
        },
        allPagesData: {
            get: function (get) {
                var data = [];
                var types = {
                    classes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.classes,
                        childrens: Ext.getStore('classes.Classes').getData().getRange()
                    }
                };

                // if workflow is disabled we can't collect processes items
                var wfEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled);
                if (wfEnabled) {
                    types.processes = {
                        label: CMDBuildUI.locales.Locales.administration.navigation.processes,
                        childrens: Ext.getStore('processes.Processes').getData().getRange()
                    };
                }
                types.dashboards = {
                    label: CMDBuildUI.locales.Locales.administration.navigation.dashboards,
                    childrens: Ext.getStore('Dashboards').getData().getRange()
                };
                types.custompages = {
                    label: CMDBuildUI.locales.Locales.administration.navigation.custompages,
                    childrens: Ext.getStore('custompages.CustomPages').getData().getRange()
                };
                types.views = {
                    label: CMDBuildUI.locales.Locales.administration.navigation.views,
                    childrens: Ext.getStore('views.Views').getData().getRange()
                };

                Object.keys(types).forEach(function (type, typeIndex) {
                    types[type].childrens.forEach(function (value, index) {
                        var item = {
                            group: type,
                            groupLabel: types[type].label,
                            _id: value.get('_id'),
                            label: value.get('description')
                        };
                        data.push(item);
                    });
                });
                data.sort(function (a, b) {
                    var aGroup = a.group.toUpperCase();
                    var bGroup = b.group.toUpperCase();
                    var aLabel = a.label.toUpperCase();
                    var bLabel = b.label.toUpperCase();

                    if (aGroup === bGroup) {
                        return (aLabel < bLabel) ? -1 : (aLabel > bLabel) ? 1 : 0;
                    } else {
                        return (aGroup < bGroup) ? -1 : 1;
                    }
                });
                return data;
            }
        }
    },

    stores: {
        getAllPagesStore: {
            data: '{allPagesData}',
            autoDestroy: true
        },
        typesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            fields: ['value', 'label'],
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: [{
                label: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.normal,
                value: 'default'
            }, {
                label: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.readonlyadmin,
                value: 'admin_readonly'
            }, {
                label: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.limitedadmin,
                value: 'admin_limited'
            }, {
                label: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.admin,
                value: 'admin'
            }]
        }
    },


    invertAccessFields: function (theGroup) {
        // all items
        theGroup.set('_rp_class_access', !theGroup.get('_rp_class_access'));
        theGroup.set('_rp_process_access', !theGroup.get('_rp_process_access'));
        theGroup.set('_rp_dataview_access', !theGroup.get('_rp_dataview_access'));
        theGroup.set('_rp_dashboard_access', !theGroup.get('_rp_dashboard_access'));
        theGroup.set('_rp_report_access', !theGroup.get('_rp_report_access'));
        theGroup.set('_rp_custompages_access', !theGroup.get('_rp_custompages_access'));

        // menu
        theGroup.set('_rp_bulkupdate_access', !theGroup.get('_rp_bulkupdate_access'));

        //Tabs Disabled Management Classes
        theGroup.set('_rp_card_tab_detail_access', !theGroup.get('_rp_card_tab_detail_access'));
        theGroup.set('_rp_card_tab_note_access', !theGroup.get('_rp_card_tab_note_access'));
        theGroup.set('_rp_card_tab_relation_access', !theGroup.get('_rp_card_tab_relation_access'));
        theGroup.set('_rp_card_tab_history_access', !theGroup.get('_rp_card_tab_history_access'));
        theGroup.set('_rp_card_tab_email_access', !theGroup.get('_rp_card_tab_email_access'));
        theGroup.set('_rp_card_tab_attachment_access', !theGroup.get('_rp_card_tab_attachment_access'));

        //Tabs Disabled Management Processes
        theGroup.set('_rp_flow_tab_detail_access', !theGroup.get('_rp_flow_tab_detail_access'));
        theGroup.set('_rp_flow_tab_note_access', !theGroup.get('_rp_flow_tab_note_access'));
        theGroup.set('_rp_flow_tab_relation_access', !theGroup.get('_rp_flow_tab_relation_access'));
        theGroup.set('_rp_flow_tab_history_access', !theGroup.get('_rp_flow_tab_history_access'));
        theGroup.set('_rp_flow_tab_email_access', !theGroup.get('_rp_flow_tab_email_access'));
        theGroup.set('_rp_flow_tab_attachment_access', !theGroup.get('_rp_flow_tab_attachment_access'));
    },


    configToolbarButtons: function () {
        this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
        this.set('toolbarHiddenButtons.enable', !this.get('actions.view') || (this.get('actions.view') && this.data.theGroup.data.active /*this.get('theGroup.active')*/));
        this.set('toolbarHiddenButtons.disable', !this.get('actions.view') || (this.get('actions.view') && !this.data.theGroup.data.active /*!this.get('theGroup.active')*/));
        if (this.get('actions.edit')) {
            this.toggleEnableTabs(this.getView().down('administration-content-groupsandpermissions-tabpanel').getActiveTab().tab.getTabIndex());
        } else if (this.get('actions.add')) {
            this.toggleEnableTabs(0);
        } else {
            this.toggleEnableTabs();
        }
        return true;
    },

    toggleEnableTabs: function (currrentTabIndex) {
        var me = this;
        var view = me.getView().down('administration-content-groupsandpermissions-tabpanel');
        var tabs = view.items.items;
        if (typeof currrentTabIndex === 'undefined') {

            tabs.forEach(function (tab) {
                me.set('disabledTabs.' + tab.reference, false);
            });
        } else {
            tabs.forEach(function (tab) {
                if (tab.tabConfig.tabIndex !== currrentTabIndex) {
                    me.set('disabledTabs.' + tab.reference, true);
                }
            });
        }
    }
});