Ext.define("CMDBuildUI.util.Stores", {
    singleton: true,

    loaded: {
        classes: false,
        processes: false,
        reports: false,
        dashboards: false,
        views: false,
        searchfilters: false,
        custompages: false,
        customcomponents: false,
        menu: false,
        administrationmenus: false,
        lookuptypes: false,
        domains: false,
        navtree: false,
        groups: false,
        emailaccounts: false,
        emailtemplates: false
    },

    /**
     * Load classes store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadClassesStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('classes.Classes');
        // load classes store
        store.load({
            params: {
                detailed: true // load full data
            },
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.classes = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Classes store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load processes store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadProcessesStore: function () {
        var workflowEnabled = CMDBuildUI.util.helper.Configurations.get('cm_system_workflow_enabled');
        var deferred = new Ext.Deferred();

        var store = Ext.getStore('processes.Processes');
        if (workflowEnabled) {
            // load processes store
            store.load({
                params: {
                    detailed: true // load full data
                },
                callback: function (records, operation, success) {
                    if (success) {
                        CMDBuildUI.util.Stores.loaded.processes = true;
                        deferred.resolve(records);
                    } else {
                        CMDBuildUI.util.Logger.log(
                            "Error loading Processes store",
                            CMDBuildUI.util.Logger.levels.error
                        );
                        deferred.resolve([]);
                    }
                }
            });
            return deferred.promise;
        } else {
            deferred.resolve([]);
        }
    },

    /**
     * Load reprts store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadReportsStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('reports.Reports');
        // load reports store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.reports = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Reports store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load dashboards store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadDashboardsStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('Dashboards');
        // load reports store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.dashboards = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Dashboards store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load views store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadViewsStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('views.Views');
        // load reports store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.views = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Views store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load searchfilters store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadSearchfiltersStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('searchfilters.Searchfilters');
        // load serachfilters store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.searchfilters = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Searchfilters store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load custompages store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadCustomPagesStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('custompages.CustomPages');
        // load reports store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.custompages = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading CustomPages store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },
    /**
      * Load customcomponents store.
      * 
      * @return {Ext.promise.Promise}
      */
    loadCustomContextMenuStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('customcomponents.ContextMenus');
        // load CustomComponents store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.customcomponents = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading CustomComponents store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },
    /**
     * Load menu store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadMenuStore: function () {
        var deferred = new Ext.Deferred();

        // load classes store
        var store = Ext.getStore('menu.Menu');
        store.setRoot({
            expanded: false
        });
        store.load(function (records, operation, success) {
            if (success) {
                CMDBuildUI.util.Stores.loaded.menu = true;
                deferred.resolve(records);
            } else {
                CMDBuildUI.util.Logger.log(
                    "Error loading Menu items store",
                    CMDBuildUI.util.Logger.levels.error
                );
                deferred.resolve([]);
            }
        });

        return deferred.promise;
    },

    /**
     * Load menu store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadAdministrationMenusStore: function () {
        var deferred = new Ext.Deferred();

        // load classes store
        var store = Ext.create('Ext.data.Store', {
            model: 'CMDBuildUI.model.menu.Menu',

            pageSize: 0, // disable pagination

            sorters: [
                'group'
            ]
        });

        store.load(function (records, operation, success) {
            if (success) {
                CMDBuildUI.util.Stores.loaded.administrationmenus = true;
                deferred.resolve(records);
            } else {
                CMDBuildUI.util.Logger.log(
                    "Error loading administration Menu items store",
                    CMDBuildUI.util.Logger.levels.error
                );
                deferred.resolve([]);
            }
        });

        return deferred.promise;
    },

    /**
     * Load lookup types store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadLookupTypesStore: function () {
        var deferred = new Ext.Deferred();

        // load classes store
        var store = Ext.getStore('lookups.LookupTypes');
        // load reports store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.lookuptypes = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading LookupTypes store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load domains store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadDomainsStore: function () {
        var deferred = new Ext.Deferred();

        // load classes store
        var store = Ext.getStore('domains.Domains');
        // load reports store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.domains = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Domains store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load navigation trees
     */
    loadNavigationTreesStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('navigationtrees.NavigationTrees');
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.navtree = true;
                    /// Temporary until server makes services
                    for (var i = 0; i < records.length; i++) {
                        if (records[i].get('_id') === 'gisnavigation') {
                            CMDBuildUI.model.navigationTrees.DomainTree.load('gisnavigation', {
                                callback: function (record, operation, success) {
                                    if (success) {
                                        store.add([record]);
                                    }
                                }
                            });
                        }
                    }
                    /// Temporary until server makes services
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading NavTree store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load groups store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadGroupsStore: function () {
        var deferred = new Ext.Deferred();
        var store = Ext.getStore('groups.Groups');
        // load groups store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.groups = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading Groups store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Load flow statuses store.
     * 
     * @return {Ext.promise.Promise}
     */
    loadFlowStatuses: function () {
        // load classes store
        var type = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(CMDBuildUI.model.processes.Process.flowstatus.lookuptype);
        return type.getLookupValues();
    },

    /**
    * Load email templates store.
    * 
    * @return {Ext.promise.Promise}
    */
    loadEmailAccountsStore: function () {

        var deferred = new Ext.Deferred();
        var store = Ext.getStore('emails.Accounts');
        // load groups store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.emailaccounts = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading email Accounts store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
    * Load email templates store.
    * 
    * @return {Ext.promise.Promise}
    */
    loadEmailTemplatesStore: function () {

        var deferred = new Ext.Deferred();
        var store = Ext.getStore('emails.Templates');
        // load groups store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.emailtemplates = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        "Error loading email Templates store",
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    }


});