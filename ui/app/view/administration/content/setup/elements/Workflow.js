Ext.define('CMDBuildUI.view.administration.content.setup.elements.Workflow', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.WorkflowController',
        'CMDBuildUI.view.administration.content.setup.elements.WorkflowModel'
    ],
    alias: 'widget.administration-content-setup-elements-workflow',
    controller: 'administration-content-setup-elements-workflow',
    viewModel: {
        type: 'administration-content-setup-elements-workflow'
    },

    items: [{
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'enabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__enabled}',
                        readOnly: '{actions.view}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: []
            }]

        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.enableattachmenttoclosedactivities,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.enableattachmenttoclosedactivities'
                    },
                    name: 'enableAddAttachmentOnClosedActivities',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__enableAddAttachmentOnClosedActivities}',
                        readOnly: '{actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.usercandisable,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.usercandisable'
                    },
                    name: 'userCanDisable',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__userCanDisable}',
                        readOnly: '{actions.view}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.hidesavebutton,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.hidesavebutton'
                    },
                    name: 'hideSaveButton',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__hideSaveButton}',
                        readOnly: '{actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',                    
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.dafaultjobusername,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.dafaultjobusername'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__jobs__DOT__defaultUser}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'textfield',
                    name: 'serviceUrl',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.dafaultjobusername,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.dafaultjobusername'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__jobs__DOT__defaultUser}',
                        hidden: '{actions.view}'
                    }
                }]
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        collapsed: false,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.tecnotecariver,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.tecnotecariver'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'riverEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__river__DOT__enabled}',
                        readOnly: '{actions.view}'
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var formView = this.up('administration-content-setup-elements-workflow');
                            var vm = formView.getViewModel().getParent();
                            switch (newValue) {
                                case true:
                                    if (vm.get('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__provider') === "shark" &&
                                        vm.get('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__shark__DOT__enabled').toString() === 'false'
                                    ) {
                                        vm.set('isSharkDefault', false);
                                        vm.set('isRiverDefault', true);
                                        vm.set('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__provider', 'river');
                                        formView.lookupReference('riverDefault').setValue(true);
                                    }
                                    break;
                                case false:
                                    if (vm.get('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__provider') === "river" &&
                                        (vm.get('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__shark__DOT__enabled') && vm.get('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__shark__DOT__enabled').toString() === 'true')) {
                                        vm.set('isRiverDefault', false);
                                        vm.set('isSharkDefault', true);
                                        vm.set('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__provider', 'shark');
                                        formView.lookupReference('sharkDefault').setValue(true);
                                    }
                                    break;
                            }
                        }
                    }
                }]
            }, {
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    style: {
                        paddingLeft: '15px'
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default'
                    },
                    name: 'riverDefault',
                    reference: 'riverDefault',
                    bind: {
                        value: '{isRiverDefault}',
                        readOnly: '{actions.view}',
                        disabled: '{riverDefaultFieldDisabled}'
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var formView = this.up('administration-content-setup-elements-workflow');
                            var vm = formView.getViewModel().getParent();
                            switch (newValue) {
                                case true:
                                    vm.set('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__provider', 'river');
                                    formView.lookupReference('sharkDefault').setValue(false);
                                    formView.lookupReference('riverDefault').setValue(true);
                                    break;
                                case false:
                                    if (vm.get('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__shark__DOT__enabled').toString() === "true") {
                                        vm.set('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__provider', 'shark');
                                        formView.lookupReference('sharkDefault').setValue(true);
                                        formView.lookupReference('riverDefault').setValue(false);
                                    }
                                    break;
                            }
                        }
                    }
                }]
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        collapsed: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.shark,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.shark'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    name: 'username',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__user}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'textfield',
                    name: 'username',
                    listeners: {
                        afterrender: function (cmp) {
                            cmp.inputEl.set({
                                autocomplete: 'new-password'
                            });
                        }
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__user}',
                        hidden: '{actions.view}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'displayfield',
                    name: 'password',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                    },
                    value: '****',
                    bind: {
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'textfield',
                    inputType: 'password',
                    name: 'password',
                    listeners: {
                        afterrender: function (cmp) {
                            cmp.inputEl.set({
                                autocomplete: 'new-password'
                            });
                        }
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__password}',
                        hidden: '{actions.view}'
                    },
                    triggers: {
                        showPassword: {
                            cls: 'x-fa fa-eye',
                            hideTrigger: false,
                            scope: this,
                            handler: function (field, button, e) {
                                // set the element type to text
                                field.inputEl.el.set({
                                    type: 'text'
                                });
                                field.getTrigger('showPassword').setVisible(false);
                                field.getTrigger('hidePassword').setVisible(true);
                            }
                        },
                        hidePassword: {
                            cls: 'x-fa fa-eye-slash',
                            hidden: true,
                            scope: this,
                            handler: function (field, button, e) {
                                // set the element type to password
                                field.inputEl.el.set({
                                    type: 'password'
                                });
                                field.getTrigger('showPassword').setVisible(true);
                                field.getTrigger('hidePassword').setVisible(false);
                            }
                        }
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    name: 'serviceUrl',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.serviceurl,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.serviceurl'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__endpoint}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'textfield',
                    name: 'serviceUrl',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.serviceurl,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.serviceurl'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__endpoint}',
                        hidden: '{actions.view}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.disablesynconmissingvariables,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.disablesynconmissingvariables'
                    },
                    name: 'disableSynchronizationOfMissingVariables',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__disableSynchronizationOfMissingVariables}',
                        readOnly: '{actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'sharkEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__shark__DOT__enabled}',
                        readOnly: '{actions.view}'
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var formView = this.up('administration-content-setup-elements-workflow');
                            var vm = formView.getViewModel().getParent();
                            switch (newValue) {
                                case true:
                                    if (vm.get('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__provider') === "river" &&
                                        vm.get('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__river__DOT__enabled').toString() === "false"
                                    ) {
                                        formView.lookupReference('sharkDefault').setValue(true);
                                        formView.lookupReference('riverDefault').setValue(false);
                                        //vm.set('isSharkDefault', true);
                                        // vm.set('isRiverDefault', false);
                                        vm.set('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__provider', 'shark');
                                    }
                                    break;
                                case false:
                                    if (vm.get('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__river__DOT__enabled').toString() === "true") {
                                        formView.lookupReference('riverDefault').setValue(true);
                                        formView.lookupReference('sharkDefault').setValue(false);
                                        // vm.set('isSharkDefault', false);
                                        //vm.set('isRiverDefault', true);
                                        vm.set('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__provider', 'river');
                                    }
                                    break;
                            }
                        }
                    }
                }]
            }, {
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    style: {
                        paddingLeft: '15px'
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.default,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.default'
                    },
                    name: 'sharkDefault',
                    reference: 'sharkDefault',
                    bind: {
                        value: '{isSharkDefault}',
                        readOnly: '{actions.view}',
                        disabled: '{sharkDefaultFieldDisabled}'
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var formView = this.up('administration-content-setup-elements-workflow');
                            var vm = formView.getViewModel().getParent();
                            switch (newValue) {
                                case true:
                                    // vm.set('isSharkDefault', true);
                                    // vm.set('isRiverDefault', false);
                                    vm.set('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__provider', 'shark');
                                    formView.lookupReference('sharkDefault').setValue(true);
                                    formView.lookupReference('riverDefault').setValue(false);
                                    break;
                                case false:
                                    if (vm.get('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__river__DOT__enabled').toString() === "true") {
                                        vm.set('theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__provider', 'river');
                                        formView.lookupReference('riverDefault').setValue(true);
                                        formView.lookupReference('sharkDefault').setValue(false);
                                    }
                                    break;
                            }
                        }
                    }
                }]
            }]
        }]
    }]
});