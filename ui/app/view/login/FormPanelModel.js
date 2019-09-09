Ext.define('CMDBuildUI.view.login.FormPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.login-formpanel',

    data: {
        loggedIn: false,
        password: null,
        showErrorMessage: false,
        hiddenfields: {},
        disabledfields: {},
        lengths: {
            groups: 0,
            tenants: 0
        }
    },
    formulas: {
        language: {
            get: function (get) {
                var lang = CMDBuildUI.util.helper.SessionHelper.getLanguage();
                if (!lang) {
                    lang = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.defaultlanguage);
                }
                return lang;
            },
            set: function (value) {
                CMDBuildUI.util.helper.SessionHelper.setLanguage(value);
            }
        },
        updateFieldsVisibility: {
            bind: {
                sessionid: '{theSession._id}',
                multiTenantActivationPrivileges: '{theSession.multiTenantActivationPrivileges}',
                groups: '{lengths.groups}',
                tenants: '{lengths.tenants}',
                activeTenants: '{theSession.activeTenants}'
            },
            get: function (data) {
                var loggedin = data.sessionid !== CMDBuildUI.model.users.Session.temporary_id;
                if (loggedin) {
                    this.set("disabledfields.username", true);
                    this.set("disabledfields.password", true);
                    this.set("hiddenfields.role", data.groups > 1 ? false : true);
                    this.set("hiddenfields.tenants", data.tenants > 1 && data.multiTenantActivationPrivileges == 'any' ? false : true);
                    this.set("hiddenfields.tenantsone", data.tenants > 1 && data.multiTenantActivationPrivileges == 'one' ? false : true);
                    this.set("hiddenfields.cancelbtn", false);
                } else {
                    this.set("disabledfields.username", false);
                    this.set("disabledfields.password", false);
                    this.set("hiddenfields.role", true);
                    this.set("hiddenfields.tenants", true);
                    this.set("hiddenfields.tenantsone", true);
                    this.set("hiddenfields.cancelbtn", true);
                }
                this.set("hiddenfields.language", !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.uselanguageprompt));

            }
        },

        groupsData: {
            bind: {
                groups: '{theSession.availableRolesExtendedData}'
            },
            get: function (data) {
                var groups = [];
                Ext.Array.each(data.groups, function (item, index) {
                    groups.push({
                        value: item.code,
                        label: item.description
                    });
                });
                this.set("lengths.groups", groups.length);
                return groups;
            }
        },

        tenantsData: {
            bind: {
                tenants: '{theSession.availableTenantsExtendedData}'
            },
            get: function (data) {
                var tenants = [];
                Ext.Array.each(data.tenants, function (item, index) {
                    tenants.push({
                        value: item.code,
                        label: item.description
                    });
                });
                this.set("lengths.tenants", tenants.length);
                return tenants;
            }
        }
    },

    links: {
        theSession: {
            type: 'CMDBuildUI.model.users.Session',
            create: {
                _id: CMDBuildUI.model.users.Session.temporary_id
            }
        }
    },

    stores: {
        languages: {
            model: 'CMDBuildUI.model.Language',
            sorters: 'description',
            autoLoad: true,
            autoDestroy: true
        },
        groups: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            autoDestroy: true,
            data: '{groupsData}'
        },
        tenants: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            autoDestroy: true,
            data: '{tenantsData}'
        }
    }

});