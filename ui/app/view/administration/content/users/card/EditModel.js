Ext.define('CMDBuildUI.view.administration.content.users.card.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.view-administration-content-users-card-edit',
    data: {
        theUser: null,
        rolesData: [],
        tenantsData: [],
        isMultitenantActive: false,
        tenantModeIsDbFunction: false,
        tenantModeIsClass: false
    },

    formulas: {
        isClone: {
            bind: '{theUser}',
            get: function (theUser) {
                return theUser.phantom || false;
            }
        },
        getAllPages: {
            bind: '{theUser}',
            get: function (theUser) {
                var data = [];
                var types = {
                    classes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.classes,
                        childrens: Ext.getStore('classes.Classes').getData().getRange()
                    },
                    processes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.processes,
                        childrens: Ext.getStore('processes.Processes').getData().getRange()
                    },
                    dashboards: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.dasboard,
                        childrens: Ext.getStore('Dashboards').getData().getRange()
                    },
                    custompages: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.custompages,
                        childrens: Ext.getStore('custompages.CustomPages').getData().getRange()
                    },
                    views: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.views,
                        childrens: Ext.getStore('views.Views').getData().getRange()
                    }
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
        },
        groupsHtml: {
            bind: '{theUser.userGroups}',
            get: function (groups) {

                if (groups && groups.length) {
                    var groupsHtml = '<ul>';
                    groups.forEach(function (group) {
                        groupsHtml += '<li>' + group.description + '</li>';
                    });
                    groupsHtml += '</ul>';
                    return groupsHtml;
                }
                return '<em>' + CMDBuildUI.locales.Locales.administration.users.fieldLabels.nodata + '</em>';
            }
        },
        tenantsHtml: {
            bind: '{theUser.userTenants}',
            get: function (tenants) {
                if (tenants && tenants.length) {
                    var tenantsHtml = '<ul>';
                    tenants.forEach(function (tenant) {
                        tenantsHtml += '<li>' + tenant.description + '</li>';
                    });
                    return tenantsHtml += '</ul>';

                }
                return '<em>' + CMDBuildUI.locales.Locales.administration.users.fieldLabels.nodata + '</em>';
            }
        },

        panelTitle: {
            bind: '{theUser.username}',
            get: function (username) {
                var title = Ext.String.format(
                    '{0} - {1}',
                    CMDBuildUI.locales.Locales.administration.users.fieldLabels.user,
                    username
                );
                this.getParent().set('title', title);
            }
        },

        getRolesData: {
            bind: '{theUser}',
            get: function (theUser) {
                if (theUser) {
                    var me = this,
                        store = me.getStore('groups'),
                        items = store.getRange(),
                        roles = [];

                    if (!theUser.get('userGroups')) {
                        theUser.set('userGroups', []);
                    }
                    items.forEach(function (role) {
                        var exist = theUser.get('userGroups').find(function (userGroup) {
                            return userGroup._id === role.get('_id');
                        });

                        me.getStore('rolesStore').add({
                            description: role.get('description'),
                            _id: role.get('_id'),
                            name: role.get('name'),
                            active: (exist) ? true : false
                        });

                    });


                    me.set('theUser.groupsLength', roles.length);
                }

            }
        },

        getTenantsData: {
            bind: '{theUser}',
            get: function (theUser) {
                var me = this;
                if (theUser) {
                    CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(function (config) {

                        var multitenantConfig = config.filter(function (item) {
                            return 'org__DOT__cmdbuild__DOT__multitenant__DOT__mode' === item._key;
                        })[0];
                        var isMultitenantActive = multitenantConfig.hasValue && multitenantConfig.value !== 'DISABLED';
                        me.set('isMultitenantActive', isMultitenantActive);
                        me.set('tenantModeIsDbFunction', isMultitenantActive && multitenantConfig.value === 'DB_CONFIG');
                        me.set('tenantModeIsClass', isMultitenantActive && multitenantConfig.value === 'CMDBUILD_CLASS');

                        if (isMultitenantActive) {
                            me.set('tenantstoreload', true);
                            // var store = Ext.create('Ext.data.Store', {
                            //     proxy: {
                            //         type: 'baseproxy',
                            //         url: '/tenants'
                            //     }
                            // });
                            // debugger;

                            // store.load({

                            //     callback: function (items, operation, success) {
                            //         var tenantsData = [];
                            //         if (!theUser.get('userTenants')) {
                            //             theUser.set('userTenants', []);
                            //         }

                            //         items.forEach(function (tenant) {

                            //             var exist = theUser.get('userTenants').find(function (userTenant) {
                            //                 return userTenant._id === tenant.get('_id');
                            //             });

                            //             var _tenant = {
                            //                 description: tenant.get('description'),
                            //                 _id: tenant.get('_id'),
                            //                 name: tenant.get('name') || tenant.get('code'),
                            //                 active: (exist) ? true : false
                            //             };
                            //             tenantsData.push(_tenant);
                            //         });
                            //         this.set('tenantsData', tenantsData);
                            //         this.set('theUsers.userTenants', tenantsData);
                            //     },
                            //     scope: me
                            // });
                        }
                    });
                }
            }
        },

        userGroups: {
            bind: {
                userGroups: '{theUser.userGroups}',
                groupsLength: '{theUser.groupsLength}'
            },

            get: function (data) {
                if (data.userGroups) {
                    return data.userGroups;
                }
            },
            set: function (value) {
                this.set('theUser.userGroups', value);
                this.set('theUser.groupsLength', this.get('theUser.userGroups').length);
            }
        },
        userTenants: {
            bind: {
                userTenants: '{theUser.userTenants}'
            },

            get: function (data) {
                if (data.userTenants) {
                    return data.userTenants;
                }
            },
            set: function (value) {
                // var activeItems = value.filter(function (item) {
                //     return item.active === true;
                // });
                // this.set('theUser.userTenants', activeItems);
                // this.set('theUser.tenantsLength', activeItems.length);
            }
        }
    },

    stores: {
        tenantStore: {


            proxy: {
                type: 'baseproxy',
                url: '/tenants'
            },
            params: {
                limit: 0
            },
            listeners: {
                load: 'onTenantStoreLoad'
            },
            autoDestroy: true,
            autoLoad: '{tenantstoreload}'
        },


        multiTenantActivationPrivilegesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: [{
                value: 'any',
                label: CMDBuildUI.locales.Locales.administration.common.actions.yes
            }, {
                value: 'one',
                label: CMDBuildUI.locales.Locales.administration.common.actions.no
            }]
        },
        groups: {
            type: 'chained',
            source: 'groups.Groups',
            autoLoad: true,
            autoDestroy: true
        },
        rolesStore: {
            model: 'CMDBuildUI.model.users.UserGroup',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true
        },
        activeUserRolesStore: {
            source: '{rolesStore}',
            filters: [function (item) {
                return item.get('active') === true;
            }],
            proxy: {
                type: 'memory'
            },
            sorters: 'description',
            autoLoad: true,
            autoDestroy: true
        },
        userGroupsStore: {
            proxy: {
                type: 'baseproxy',
                url: '{userGroupProxyUrl}'
            },
            sorters: 'description',
            autoLoad: true,
            autoDestroy: true
        },
        tenantsStore: {
            data: '{tenantsData}',
            proxy: {
                type: 'memory'
            },
            fields: ['active',
                'description',
                'name',
                '_id'
            ],

            autoLoad: true,
            autoDestroy: true
        },
        userTenantsStore: {
            proxy: {
                type: 'baseproxy',
                url: '{userTenantProxyUrl}'
            },
            sorters: 'description',
            autoLoad: true,
            autoDestroy: true
        },
        languagesStore: {
            model: 'CMDBuildUI.model.Language',
            proxy: {
                type: 'baseproxy',
                url: '/languages',
                extraParams: {
                    active: true
                }
            },
            sorters: 'description',
            autoLoad: true,
            autoDestroy: true
        },
        getAllPagesStore: {
            data: '{getAllPages}',
            autoDestroy: true
        },

        getSelectedTenants: {
            data: '{userTenants}',
            proxy: {
                type: 'memory'
            },
            sorters: 'description',
            autoLoad: true,
            autoDestroy: true,
            filters: [function (item) {
                return item.get('active') === true;
            }]
        }
    }
});