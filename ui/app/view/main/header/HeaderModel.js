Ext.define('CMDBuildUI.view.main.header.HeaderModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.main-header-header',

    data: {
        companylogoinfo: {
            hidden: true,
            url: null
        }
    },

    formulas: {
        updatecompanylogodata: {
            bind: {
                logoid: '{companylogoid}'
            },
            get: function(data) {
                if (data.logoid) {
                    var me = this;
                    me.set("companylogoinfo.url", Ext.String.format("{0}/resources/company_logo/download", CMDBuildUI.util.Config.baseUrl));
                    Ext.asap(function() {
                        me.set("companylogoinfo.hidden", false);
                    });
                }
            }
        },
        isAuthenticated: {
            bind: '{theSession.username}',
            get: function (username) {
                if (username) {
                    return true;
                }
                return false;
            }
        },
        isAdministrator: {
            bind: {
                privileges: '{theSession.rolePrivileges}',
                isAdministrationModule: '{isAdministrationModule}'
            },
            get: function (data) {
                return data.privileges && data.privileges.admin_access && !data.isAdministrationModule;
            }
        }
    }

});
