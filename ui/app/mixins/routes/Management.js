Ext.define('CMDBuildUI.mixins.routes.Management', {
    mixinId: 'managementroutes-mixin',

    mixins: [
        'CMDBuildUI.mixins.routes.management.Classes',
        'CMDBuildUI.mixins.routes.management.Processes',
        'CMDBuildUI.mixins.routes.management.Reports',
        'CMDBuildUI.mixins.routes.management.Views'
    ],

    /******************* PATCHES ********************/
    showPatches: function () {
        CMDBuildUI.util.Navigation.addIntoMainContainer('patches-panel');
    },
    /**
     *  redirect to administration and refresh the window
     */
    goToManagement: function () {
        this.redirectTo('', true);
        window.location.reload();
    },
    /******************* LOGIN ********************/
    /**
     * Show login form
     */
    onBeforeShowLogin: function (action) {
        var me = this;
        // redirect to patch manager if needed
        CMDBuildUI.util.Utilities.checkBootStatus().then(function () {
            // close details window
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow();

            Ext.Promise.all([
                CMDBuildUI.util.helper.Configurations.loadPublicConfs()
            ]).then(function () {
                CMDBuildUI.util.helper.SessionHelper.checkSessionValidity().then(function (token) {
                    action.stop();
                    var url = CMDBuildUI.util.helper.SessionHelper.getStartingUrl();
                    if (url && Ext.String.startsWith(url, "administration")) {
                        me.redirectTo('administration');
                    } else {
                        me.redirectTo('management');
                    }
                }).otherwise(function (err) {
                    action.resume();
                });
            });
            // status is OK
        }).otherwise(function () {
            // need patchs
            action.stop();
            me.redirectTo('patches');
        });
    },
    showLogin: function () {
        if (!CMDBuildUI.util.helper.SessionHelper.logging) {
            CMDBuildUI.util.helper.SessionHelper.logging = true;
            CMDBuildUI.util.Navigation.addIntoMainContainer('login-container');
        }
    },

    /******************* LOGOUT ********************/
    /**
     * Do logout
     */
    doLogout: function () {
        this.getViewModel().get("theSession").set("username", null); // used to refresh session status
        // set action id
        CMDBuildUI.util.Ajax.setActionId('logout');
        // delete session
        this.getViewModel().get("theSession").erase({
            success: function (record, operation) {
                // blank session token
                CMDBuildUI.util.helper.SessionHelper.setToken(null);
                if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.redirectonlogout)) {
                    window.location.replace(CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.redirectonlogout));
                } else {
                    CMDBuildUI.util.Utilities.redirectTo("login", true);
                }
            }
        });
    },

    /******************* MANAGEMENT ********************/
    /**
     * Show management page
     */
    onBeforeShowManagement: function (action) {
        var me = this;
        this.getViewModel().set('isAdministrationModule', false);
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
        CMDBuildUI.util.Navigation.clearCurrentContext();
        // remove all from main container
        var container = CMDBuildUI.util.Navigation.getMainContainer(true);
        // add load mask
        var loadmask = new Ext.LoadMask({
            target: container
        });
        loadmask.show();

        CMDBuildUI.util.helper.SessionHelper.checkSessionValidity().then(function (token) {

            Ext.Promise.all([
                CMDBuildUI.util.helper.Configurations.loadSystemConfs()
            ]).then(function () {
                CMDBuildUI.util.Ajax.setActionId(null);
                Ext.Promise.all([
                    CMDBuildUI.util.Stores.loadClassesStore(),
                    CMDBuildUI.util.Stores.loadProcessesStore(),
                    CMDBuildUI.util.Stores.loadReportsStore(),
                    CMDBuildUI.util.Stores.loadDashboardsStore(),
                    CMDBuildUI.util.Stores.loadViewsStore(),
                    CMDBuildUI.util.Stores.loadCustomPagesStore(),
                    CMDBuildUI.util.Stores.loadMenuStore(),
                    CMDBuildUI.util.Stores.loadLookupTypesStore(),
                    CMDBuildUI.util.Stores.loadDomainsStore(),
                    CMDBuildUI.util.Stores.loadNavigationTreesStore(),
                    CMDBuildUI.util.Stores.loadGroupsStore(),
                    CMDBuildUI.util.helper.UserPreferences.load()
                ]).then(function () {
                    // laod mask
                    CMDBuildUI.util.MenuStoreBuilder.initialize();
                    Ext.getBody().removeCls('administration');
                    Ext.getBody().addCls('management');
                    // resume action
                    action.resume();
                    // destroy load mask
                    loadmask.destroy();
                });
            });
        }, function (err) {
            action.stop();
            // redirect to login
            me.redirectTo('login', true);
            // destroy load mask
            loadmask.destroy();
        });
    },
    showManagement: function () {
        CMDBuildUI.util.Navigation.addIntoMainContainer('management-maincontainer');
        this.redirectToStartingUrl();
    },


    /******************* CUSTOM PAGES ********************/
    /**
     * Before show custom page
     * 
     * @param {String} pageName
     * @param {Object} action
     */
    onBeforeShowCustomPage: function (pageName, action) {
        var page = Ext.getStore("custompages.CustomPages").findRecord("name", pageName);
        if (page) {
            Ext.require(page.get("componentId"), function () {
                action.resume();
            });
        } else {
            action.stop();
        }
    },
    /**
     * Show custom page
     * 
     * @param {String} pageName
     */
    showCustomPage: function (pageName) {
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
        var page = Ext.getStore("custompages.CustomPages").findRecord("name", pageName);
        CMDBuildUI.util.Navigation.addIntoManagemenetContainer('panel', {
            title: page.getTranslatedDescription(),
            layout: 'fit',
            items: [{
                xtype: page.get("alias").replace("widget.", "")
            }]
        });

        // fire global event objecttypechanged
        Ext.GlobalEvents.fireEventArgs("objecttypechanged", [pageName]);

        // update current context
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage,
            pageName
        );
    },

    privates: {
        redirectToStartingUrl: function () {
            var startingurl = CMDBuildUI.util.helper.SessionHelper.getStartingUrl();
            if (startingurl && startingurl !== "management" && startingurl !== "administration") {
                this.redirectTo(CMDBuildUI.util.helper.SessionHelper.getStartingUrl(), true);
                CMDBuildUI.util.helper.SessionHelper.clearStartingUrl();
            } else if (startingurl) {
                CMDBuildUI.util.helper.SessionHelper.clearStartingUrl();
            }
        }
    }
});