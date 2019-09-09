Ext.define('CMDBuildUI.view.administration.content.users.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    requires: [
        'CMDBuildUI.view.administration.content.users.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.users.card.ViewInRowModel',
        'Ext.layout.*'
    ],
    autoDestroy: true,
    alias: 'widget.administration-content-users-card-viewinrow',
    controller: 'administration-content-users-card-viewinrow',
    viewModel: {
        type: 'view-administration-content-users-card-edit'
    },


    cls: 'administration',
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    config: {
        objectTypeName: null,
        objectId: null,
        shownInPopup: false,
        theUser: null
    },
    bind: {
        theUser: '{theUser}',
        hidden: '{!theUser.username}'
    },

    items: [{
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        xtype: "fieldset",
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        ui: 'administration-formpagination',
        items: [{
            xtype: 'fieldcontainer',
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
        }]
    }, {
        title: CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups'
        },
        xtype: "fieldset",
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        ui: 'administration-formpagination',
        items: [{
            xtype: 'fieldcontainer',
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'checkbox',
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
                fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup'
                },
                bind: {
                    value: '{theUser.defaultUserGroup}'
                },
                // get the user description from groupStore
                renderer: function (value) {
                    var group = Ext.getStore('groups.Groups').getById(value);
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
        xtype: "fieldset",
        ui: 'administration-formpagination',
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        title: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenant,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenant'
        },
        hidden: true,
        itemId: 'tenants',
        items: [{
            xtype: "fieldcontainer",
            bind: {
                hidden: '{!isMultitenantActive}'
            },
            items: [
                /**
                 * currently not supported by server
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
                        valueField: 'value',
                        bind: {
                            value: '{theUser.multiTenantActivationPrivileges}'
                        },
                        renderer: function (value) {                            
                            if(value){
                                var vm = this.lookupViewModel();
                                var store = vm.getStore('multiTenantActivationPrivilegesStore');
                                if (store) {
                                    var record = store.findRecord('value', value);
                                    if (record) {
                                        return record.get('label');
                                    }
                                }
                            }
                            return value;
                        }
                    }]
                }, {
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
                        name: 'username',
                        bind: {
                            value: '{tenantsHtml}'
                        }
                    }]

                }
            ]
        }]

    }],
    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true, // #editBtn set true for show the button
        view: true, // #viewBtn set true for show the button
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
    )
});