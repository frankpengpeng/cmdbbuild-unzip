Ext.define('CMDBuildUI.view.administration.content.users.card.View', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.users.card.ViewController',
        'CMDBuildUI.view.administration.content.users.card.EditModel',
        'Ext.layout.*'
    ],

    alias: 'widget.administration-content-users-card-view',
    controller: 'administration-content-users-card-view',
    viewModel: {
        type: 'view-administration-content-users-card-edit'
    },

    config: {
        objectTypeName: null,
        objectId: null,
        shownInPopup: false
    },

    scrollable: true,
    cls: 'administration tab-hidden',
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true, // #editBtn set true for show the button
        view: false, // #viewBtn set true for show the button
        clone: true, // #cloneBtn set true for show the button
        'delete': false, // #deleteBtn set true for show the button
        activeToggle: true, // #enableBtn and #disableBtn set true for show the buttons
        download: false // #downloadBtn set true for show the buttons
    },

        /* testId */
        'users',

        /* viewModel object needed only for activeTogle */
        'theUser',

        /* add custom tools[] on the left of the bar */
        [],

        /* add custom tools[] before #editBtn*/
        [],

        /* add custom tools[] after at the end of the bar*/
        []
    ),

    items: [{
        xtype: 'container',
        items: [{
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            xtype: "fieldset",
            collapsible: true,
            ui: 'administration-formpagination',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            items: [{
                layout: 'column',
                defaults: {
                    columnWidth: 0.5
                },
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.username,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.username'
                    },
                    name: 'username',
                    bind: {
                        value: '{theUser.username}'
                    }
                }, {
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.texts.description,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.texts.description'
                    },
                    name: 'description',
                    bind: {
                        value: '{theUser.description}'
                    }
                }]
            }, {
                layout: 'column',
                defaults: {
                    columnWidth: 0.5
                },
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.navigation.email,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.navigation.email'
                    },
                    name: 'email',
                    bind: {
                        value: '{theUser.email}'
                    }
                }]
            }, {
                layout: 'column',
                defaults: {
                    columnWidth: 0.5
                },
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.language,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.language'
                    },
                    labelAlign: 'top',
                    name: 'language',
                    readOnly: true,
                    bind: {
                        value: '{theUser.language}'
                    }
                }, {
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.initialpage,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.initialpage'
                    },
                    name: 'initialPage',
                    readOnly: true,
                    bind: {
                        value: '{theUser.initialPage}'
                    }
                }]
            }, {
                layout: 'column',
                defaults: {
                    columnWidth: 0.5
                },
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.service,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.service'
                    },
                    name: 'service',
                    readOnly: true,
                    bind: {
                        value: '{theUser.service}'
                    }
                }, {
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.privileged,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.privileged'
                    },
                    name: 'privileged',
                    readOnly: true,
                    bind: {
                        value: '{theUser.privileged}'
                    }
                }]
            }, {
                layout: 'column',
                defaults: {
                    columnWidth: 0.5
                },
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'active',
                    readOnly: true,
                    bind: {
                        value: '{theUser.active}'
                    }
                }]
            }]
        }, {
            title: CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups'
            },
            xtype: "fieldset",
            collapsible: true,
            ui: 'administration-formpagination',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            items: [{
                layout: 'column',
                defaults: {
                    columnWidth: 0.5
                },
                items: [{
                    xtype: 'checkbox',
                    labelAlign: 'top',
                    flex: '0.5',
                    padding: '0 15 0 15',
                    layout: 'anchor',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multigroup,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multigroup'
                    },
                    readOnly: true,
                    bind: {
                        value: '{theUser.multiGroup}'
                    }
                }]
            }, {
                layout: 'column',
                defaults: {
                    columnWidth: 0.5
                },
                items: [{
                    xtype: 'displayfield',
                    labelAlign: 'top',
                    flex: '0.5',
                    padding: '0 15 0 15',
                    layout: 'anchor',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup'
                    },
                    bind: {
                        value: '{theUser.defaultUserGroup}'
                    },
                    // get the user description from groupStore
                    renderer: function (value) {

                        var group = this.lookupViewModel().getStore('groups').getById(value);
                        return (group && group.isModel) ? group.get('description') : value;
                    }
                }]
            }, {
                layout: 'column',
                defaults: {
                    columnWidth: 0.5
                },
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups'
                    },
                    bind: {
                        value: '{groupsHtml}'
                    }
                }]
            }]
        }, {
            title: CMDBuildUI.locales.Locales.administration.users.fieldLabels.tenants,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.tenants'
            },
            xtype: "fieldset",
            ui: 'administration-formpagination',
            collapsible: true,
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            bind: {
                hidden: '{!isMultitenantActive}'
            },
            items: [
                /**
                 * Currently not supperted
                 */
                //     {
                //     layout: 'column',
                //     defaults: {
                //         columnWidth: 0.5
                //     },
                //     items: [{
                //         xtype: 'checkbox',
                //         labelAlign: 'top',
                //         flex: '0.5',
                //         padding: '0 15 0 15',
                //         layout: 'anchor',
                //         fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenant,      
                //         localized: {
                //             fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenant'
                //         },
                //         readOnly: true,
                //         bind: {
                //             value: '{theUser.multiTenant}'
                //         }
                //     }]
                // }, {
                //     layout: 'column',
                //     defaults: {
                //         columnWidth: 0.5
                //     },
                //     items: [{
                //         xtype: 'displayfield',
                //         labelAlign: 'top',
                //         flex: '0.5',
                //         padding: '0 15 0 15',
                //         layout: 'anchor',
                //         fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaulttenant,      
                //         localized: {
                //             fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaulttenant'
                //         },
                //         bind: {
                //             value: '{theUser.defaultUserTenant}'
                //         }
                //     }]
                // },
                {
                    layout: 'column',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [{
                        xtype: 'displayfield',
                        labelAlign: 'top',
                        flex: '0.5',
                        padding: '0 15 0 15',
                        layout: 'anchor',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenantactivationprivileges,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenantactivationprivileges'
                        },
                        displayField: 'label',
                        valueField: 'vale',
                        bind: {
                            value: '{theUser.multiTenantActivationPrivileges}'
                        },
                        renderer: function(value){
                            var vm = this.lookupViewModel();
                            var store = vm.getStore('multiTenantActivationPrivilegesStore');
                            
                            if(store){
                                var record = store.findRecord('value', value);
                                if(record){
                                    return record.get('label');
                                }
                            }
                            return value;
                        }
                    }]
                },
                {
                    layout: 'column',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [{
                        xtype: 'displayfield',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.tenant,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.tenant'
                        },
                        bind: {
                            value: '{tenantsHtml}'
                        }
                    }]
                }
            ]
        }]
    }]
});