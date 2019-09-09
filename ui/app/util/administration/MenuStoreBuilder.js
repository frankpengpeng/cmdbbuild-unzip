Ext.define("CMDBuildUI.util.administration.MenuStoreBuilder", {
    singleton: true,
    mixins: ['Ext.mixin.Observable'],
    requires: ['CMDBuildUI.util.administration.helper.ApiHelper'],
    initialized: false,
    initialize: function (callback, ignoreInitialized) {
        Ext.getStore('classes.PrototypeClasses').load();
        var me = this;
        CMDBuildUI.util.helper.Configurations.loadSystemConfs().then(
            function () {
                Ext.Promise.all([
                    CMDBuildUI.util.Stores.loadClassesStore(),
                    CMDBuildUI.util.Stores.loadProcessesStore(),
                    CMDBuildUI.util.Stores.loadDomainsStore(),
                    CMDBuildUI.util.Stores.loadLookupTypesStore(),
                    CMDBuildUI.util.Stores.loadViewsStore(),
                    CMDBuildUI.util.Stores.loadSearchfiltersStore(),
                    CMDBuildUI.util.Stores.loadDashboardsStore(),
                    CMDBuildUI.util.Stores.loadCustomPagesStore(),
                    CMDBuildUI.util.Stores.loadCustomContextMenuStore(),
                    CMDBuildUI.util.Stores.loadReportsStore(),
                    CMDBuildUI.util.Stores.loadAdministrationMenusStore(),
                    CMDBuildUI.util.Stores.loadNavigationTreesStore(),
                    CMDBuildUI.util.Stores.loadGroupsStore(),
                    CMDBuildUI.util.helper.UserPreferences.load()
                ]).then(function (stores) {
                    if (ignoreInitialized && me.initialized) {
                        return;
                    } else {
                        me.onReloadAdminstrationMenu(stores, callback);
                    }
                });
            }
        );

    },

    /**
     * 
     */
    onReloadAdminstrationMenu: function (storesItems, callback) {
        var me = this;
        var store = Ext.getStore('administration.MenuAdministration');
        var childrens = [];
        me.initialized = true;


        Ext.Promise.all([
            me.appendClassesAdminMenu(storesItems[0]).then(function (classes) {
                childrens[0] = classes;
            }, function () {
                Ext.Msg.alert('Error', 'Menu Classes NOT LOADED!');
            }),
            me.appendProcessesAdminMenu(storesItems[1]).then(function (processes) {
                childrens[1] = processes;
            }, function () {
                Ext.Msg.alert('Error', 'Menu Processes NOT LOADED!');
            }),
            me.appendDomainsAdminMenu(storesItems[2]).then(function (domains) {
                childrens[2] = domains;
            }, function () {
                Ext.Msg.alert('Error', 'Menu Domains NOT LOADED!');
            }),
            me.appendLookupTypesAdminMenu(storesItems[3]).then(function (lookupTypes) {
                childrens[3] = lookupTypes;
            }, function () {
                Ext.Msg.alert('Error', 'Menu LookTypes NOT LOADED!');
            }),
            me.appendViewsAdminMenu(storesItems[4]).then(function (views) {
                childrens[4] = views;
            }, function () {
                Ext.Msg.alert('Error', 'Menu Views NOT LOADED!');
            }),
            me.appendSearchFiltersAdminMenu(storesItems[5]).then(function (searchFilter) {
                childrens[5] = searchFilter;
            }, function () {
                Ext.Msg.alert('Error', 'Menu Search filter NOT LOADED!');
            }),
            me.appendDashboardsAdminMenu(storesItems[6]).then(function (dashboard) {
                childrens[6] = dashboard;
            }, function () {
                Ext.Msg.alert('Error', 'Menu Dashboard NOT LOADED!');
            }),
            me.appendCustomPagesAdminMenu(storesItems[7]).then(function (custompage) {
                childrens[7] = custompage;
            }, function () {
                Ext.Msg.alert('Error', 'Menu Dashboard NOT LOADED!');
            }),
            me.appendCustomComponentsAdminMenu(storesItems[8]).then(function (customcomponent) {
                childrens[8] = customcomponent;
            }, function () {
                Ext.Msg.alert('Error', 'Menu Dashboard NOT LOADED!');
            }),
            me.appendReportsAdminMenu(storesItems[9]).then(function (reports) {
                childrens[9] = reports;
            }, function () {
                Ext.Msg.alert('Error', 'Menu Report NOT LOADED!');
            }),
            me.appendMenuAdminMenu(storesItems[10], storesItems[12]).then(function (menu) {                
                childrens[10] = menu;
            }, function () {
                Ext.Msg.alert('Error', 'Menu Menu NOT LOADED!');
            }),
            me.appendNavigationTreeAdminMenu(storesItems[11]).then(function (navigationTree) {
                childrens[11] = navigationTree;
            }, function () {
                Ext.Msg.alert('Error', 'Menu Navigation tree NOT LOADED!');
            }),
            me.appendGroupsAndPermissionsAdminMenu(storesItems[12]).then(function (roleAndPermission) {
                childrens[12] = roleAndPermission;
            }, function () {
                Ext.Msg.alert('Error', 'Menu role and permission NOT LOADED!');
            }),
            me.appendUsersAdminMenu().then(function (users) {
                childrens[13] = users;
            }, function () {
                Ext.Msg.alert('Error', 'Menu role and permission NOT LOADED!');
            }),
            me.appendTasksAdminMenu().then(function (tasks) {
                childrens[14] = tasks;
            }, function () {
                Ext.Msg.alert('Error', 'Menu tasks NOT LOADED!');
            }),
            me.appendEmailsAdminMenu().then(function (emails) {
                childrens[15] = emails;
            }, function () {
                Ext.Msg.alert('Error', 'Menu emails NOT LOADED!');
            }),
            me.appendImportExportMenu().then(function (settings) {
                childrens[16] = settings;
            }, function () {
                Ext.Msg.alert('Error', 'Menu settings NOT LOADED!');
            }),
            me.appendGisAdminMenu().then(function (gis) {
                childrens[17] = gis;
            }, function () {
                Ext.Msg.alert('Error', 'Menu gis NOT LOADED!');
            }),
            me.appendBimAdminMenu().then(function (bim) {
                childrens[18] = bim;
            }, function () {
                Ext.Msg.alert('Error', 'Menu bim NOT LOADED!');
            }),
            me.appendLanguagesMenu().then(function (languages) {
                childrens[19] = languages;
            }, function () {
                Ext.Msg.alert('Error', 'Menu languages NOT LOADED!');
            }),

            me.appendSettingsMenu().then(function (settings) {
                childrens[20] = settings;
            }, function () {
                Ext.Msg.alert('Error', 'Menu settings NOT LOADED!');
            })
        ]).then(function () {
            store.beginUpdate();
            store.removeAll();
            store.setRoot({
                expanded: true
            });
            store.getRoot().appendChild(childrens);
            store.endUpdate();
            me.onMenuStoreReady(store, callback);
        });
    },

    /**
     * 0 - Create classes tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendClassesAdminMenu: function (items) {
        var deferred = new Ext.Deferred();

        var simples = {
            menutype: 'folder',
            objecttype: 'Simples',
            text: CMDBuildUI.locales.Locales.administration.navigation.simples,
            href: 'administration/classes_empty',
            leaf: false,
            index: 1,
            children: this.getRecordsAsSubmenu(items.filter(function (rec, id) {
                return rec.get('type') === 'simple';
            }).sort(this.sortByText), CMDBuildUI.model.menu.MenuItem.types.klass, '')
        };

        var standard = {
            menutype: 'folder',
            objecttype: 'Standard',
            text: CMDBuildUI.locales.Locales.administration.navigation.standard,
            href: 'administration/classes_empty',
            leaf: false,
            index: 0,
            children: this.getRecordsAsSubmenu(items.filter(function (rec, id) {
                return rec.get('prototype') === true || rec.get('parent').length > 0;
            }).sort(this.sortByDescription), CMDBuildUI.model.menu.MenuItem.types.klass, 'Class')
        };

        //TODO: check configuration
        var classesMenu = {
            menutype: 'folder',
            objecttype: 'Class',
            index: 0,
            href: 'administration/classes_empty',
            text: CMDBuildUI.locales.Locales.administration.navigation.classes,
            leaf: false,
            children: [standard, simples]
        };
        deferred.resolve(classesMenu);

        return deferred.promise;
    },
    /**
     * 1 - Create Process tree
     */
    appendProcessesAdminMenu: function (items) {
        var deferred = new Ext.Deferred();
        var children;
        if (items) {
            children = this.getRecordsAsSubmenu(items.filter(function (rec, id) {
                return rec.get('prototype') === true || rec.get('parent').length > 0;
            }).sort(this.sortByDescription), CMDBuildUI.model.menu.MenuItem.types.process, 'Activity');
        }

        var processesMenu = {
            menutype: 'folder',
            objecttype: CMDBuildUI.model.administration.MenuItem.types.process,
            text: CMDBuildUI.locales.Locales.administration.navigation.processes,
            leaf: !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled) && !children,
            index: 1,
            disabled: !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled),
            href: 'administration/processes_empty'
        };

        processesMenu.children = children;
        deferred.resolve(processesMenu);

        return deferred.promise;
    },

    /**
     * 2 - Create new folder for domains in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendDomainsAdminMenu: function (items) {
        var deferred = new Ext.Deferred();
        var domainsMenu = {
            menutype: 'folder',
            href: 'administration/domains_empty',
            index: 2,
            objecttype: 'domain',
            text: CMDBuildUI.locales.Locales.administration.navigation.domains,
            leaf: false,
            children: this.getRecordsAsSubmenu(items, CMDBuildUI.model.administration.MenuItem.types.domain, '')
        };

        // append menu item to the store if has children
        deferred.resolve(domainsMenu);
        return deferred.promise;
    },

    /**
     * 3 - Create LookType tree 
     */
    appendLookupTypesAdminMenu: function (items) {
        var deferred = new Ext.Deferred();

        function setAsPrototype(item) {
            for (var i in items) {
                if (items[i].get('name') === item) {
                    items[i].set('prototype', true);
                }
            }
        }

        function threeGenerator(items) {

            for (var item in items) {
                if (items[item].get('parent').length) {
                    setAsPrototype(items[item].get('parent'));
                }
            }
            return items;
        }
        items = threeGenerator(items);
        var lokupTypesMenu = {
            menutype: 'folder',
            index: 3,
            objecttype: 'lookuptype',
            text: CMDBuildUI.locales.Locales.administration.navigation.lookuptypes,
            leaf: false,
            href: 'administration/lookup_types_empty',
            children: this.getRecordsAsSubmenu(items, CMDBuildUI.model.administration.MenuItem.types.lookuptype, '')
        };

        // append menu item to the store if has children
        deferred.resolve(lokupTypesMenu);
        return deferred.promise;
    },

    /**
     * 4 - Create new folder for domains in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendViewsAdminMenu: function (items) {
        var deferred = new Ext.Deferred();
        var filter = {
            menutype: 'folder',
            objecttype: CMDBuildUI.model.administration.MenuItem.types.view,
            text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromfilter,
            href: 'administration/views_empty/false/FILTER',
            leaf: false,
            index: 1,
            children: this.getRecordsAsSubmenu(items.filter(function (item) {
                return item.get('type') === 'FILTER';
            }).sort(this.sortByDescription), CMDBuildUI.model.administration.MenuItem.types.view, '')
        };

        var sql = {
            menutype: 'folder',
            objecttype: CMDBuildUI.model.administration.MenuItem.types.view,
            text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromsql,
            href: 'administration/views_empty/false/SQL',
            leaf: false,
            index: 1,
            children: this.getRecordsAsSubmenu(items.filter(function (item) {
                return item.get('type') === 'SQL';
            }).sort(this.sortByDescription), CMDBuildUI.model.administration.MenuItem.types.view, '')
        };

        var classesMenu = {
            menutype: 'folder',
            objecttype: CMDBuildUI.model.administration.MenuItem.types.view,
            index: 0,
            text: CMDBuildUI.locales.Locales.administration.navigation.views,
            href: 'administration/views_empty/false',
            leaf: false,
            children: [filter, sql]
        };
        deferred.resolve(classesMenu);
        return deferred.promise;
    },

    /**
     * 5 - Create new folder for search filter in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendSearchFiltersAdminMenu: function (items) {
        var deferred = new Ext.Deferred();

        var searchfiltersMenu = {
            menutype: 'folder',
            index: 5,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.searchfilter,
            text: CMDBuildUI.locales.Locales.administration.navigation.searchfilters,
            href: 'administration/searchfilters_empty/false',
            leaf: false,
            children: this.getRecordsAsSubmenu(items.sort(this.sortByDescription).sort(this.sortByDescription), CMDBuildUI.model.administration.MenuItem.types.searchfilter, '')
        };

        deferred.resolve(searchfiltersMenu);

        return deferred.promise;
    },
    /**
     * 6 - Create new folder for search filter in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendDashboardsAdminMenu: function () {
        var deferred = new Ext.Deferred();

        setTimeout(function () {
            var viewsMenu = {
                menutype: 'folder',
                index: 6,
                objecttype: 'dashboards',
                text: CMDBuildUI.locales.Locales.administration.navigation.dashboards,

                leaf: false,
                children: []
            };

            deferred.resolve(viewsMenu);
        });

        return deferred.promise;
    },

    /**
     * 7 - Create new folder for custom pages in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendCustomPagesAdminMenu: function (items) {
        var deferred = new Ext.Deferred();

        var reportsMenu = {
            menutype: 'folder',
            index: 7,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.custompage,
            text: CMDBuildUI.locales.Locales.administration.navigation.custompages,
            leaf: false,
            href: 'administration/custompages_empty',
            children: this.getRecordsAsSubmenu(items.sort(this.sortByDescription), CMDBuildUI.model.administration.MenuItem.types.custompage, '')
        };

        // append menu item to the store if has children
        deferred.resolve(reportsMenu);

        return deferred.promise;
    },

    /**
     * 8 - Create new folder for custom components in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendCustomComponentsAdminMenu: function (contextMenus) {
        var deferred = new Ext.Deferred();
        var contextMenuFolder = {
            menutype: 'folder',
            index: 1,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.contextmenu,
            text: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.contextMenus.title,
            leaf: false,
            href: 'administration/customcomponents_empty/contextmenu/false',
            children: this.getRecordsAsSubmenu(contextMenus.sort(this.sortByDescription), CMDBuildUI.model.administration.MenuItem.types.contextmenu, '')
        };
        var customcomponentMenu = {
            menutype: 'folder',
            index: 8,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.customcomponent,
            text: CMDBuildUI.locales.Locales.administration.navigation.customcomponents,
            leaf: false,
            href: 'administration/customcomponents_empty',
            children: [contextMenuFolder]
        };

        // append menu item to the store if has children
        deferred.resolve(customcomponentMenu);

        return deferred.promise;
    },

    /**
     * 9 - Create new folder for search filter in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendReportsAdminMenu: function (items) {
        var deferred = new Ext.Deferred();

        var reportsMenu = {
            menutype: 'folder',
            index: 9,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.report,
            text: CMDBuildUI.locales.Locales.administration.navigation.reports,
            leaf: false,
            href: 'administration/reports_empty',
            children: this.getRecordsAsSubmenu(items.sort(this.sortByDescription), CMDBuildUI.model.administration.MenuItem.types.report, '')
        };

        // append menu item to the store if has children
        deferred.resolve(reportsMenu);

        return deferred.promise;
    },
    /**
     * 10 - Create Menu tree
     */

    appendMenuAdminMenu: function (items, groups) {
        var deferred = new Ext.Deferred();
        items.forEach(function (item, index) {
            if (item.get('description') === '_default') {
                items[index].data.description = CMDBuildUI.locales.Locales.administration.common.strings.default;
            } else {
                var group = Ext.Array.findBy(groups, function(group){ 
                    return group.get('name') === item.get('group');
                });                
                items[index].data.description = group.get('description');
            }
        });

        var domainsMenu = {
            menutype: 'folder',
            index: 10,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.menu,
            text: CMDBuildUI.locales.Locales.administration.navigation.menus,
            href: CMDBuildUI.util.administration.helper.ApiHelper.client.getTheMenuUrl(),
            leaf: false,
            children: this.getRecordsAsSubmenu(items.sort(this.sortByDescription), CMDBuildUI.model.administration.MenuItem.types.menu, '')
        };

        // append menu item to the store if has children
        deferred.resolve(domainsMenu);

        return deferred.promise;
    },

    /**
     * 11 - Create new folder for navigation tree in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendNavigationTreeAdminMenu: function (items) {

        var deferred = new Ext.Deferred();

        Ext.Array.forEach(items, function (item) {
            item.set('name', item.get('_id'));
            if (!item.get('description')) {
                item.set('description', item.get('_id'));
            }
        });
        items = items.filter(function (item) {
            return item.get('_id') !== 'gisnavigation' && item.get('_id') !== 'bimnavigation';
        });

        items.sort(this.sortByDescription);
        var menu = {
            menutype: 'folder',
            index: 11,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.navigationtree,
            text: CMDBuildUI.locales.Locales.administration.navigation.navigationtrees,
            href: 'administration/navigationtrees_empty/false',
            leaf: false,
            children: this.getRecordsAsSubmenu(items.sort(this.sortByDescription), CMDBuildUI.model.administration.MenuItem.types.navigationtree, '')
        };

        // append menu item to the store if has children
        deferred.resolve(menu);

        return deferred.promise;
    },


    /**
     * 12 - Create new folder for navigation tree in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendGroupsAndPermissionsAdminMenu: function (items) {
        var deferred = new Ext.Deferred();

        var groupsMenu = {
            menutype: 'folder',
            index: 12,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.groupsandpermissions,
            text: CMDBuildUI.locales.Locales.administration.navigation.groupsandpermissions,
            href: 'administration/groupsandpermissions_empty',
            leaf: false,
            children: this.getRecordsAsSubmenu(items.sort(this.sortByDescription), CMDBuildUI.model.administration.MenuItem.types.groupsandpermissions, '')
        };
        deferred.resolve(groupsMenu);

        return deferred.promise;
    },

    /**
     * 13 - Create new folder for users in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendUsersAdminMenu: function () {
        var deferred = new Ext.Deferred();

        var userItem = {
            menutype: 'user',
            index: 0,
            objecttype: 'user',
            objectDescription: CMDBuildUI.locales.Locales.administration.navigation.users,
            leaf: true,
            iconCls: 'x-fa fa-user',
            href: 'administration/users'
        };
        setTimeout(function () {
            var viewsMenu = {
                menutype: 'folder',
                index: 13,
                objecttype: 'user',
                text: CMDBuildUI.locales.Locales.administration.navigation.users,
                leaf: false,
                children: [userItem],
                href: 'administration/users_empty'
            };
            deferred.resolve(viewsMenu);
        });

        return deferred.promise;
    },

    /**
     * 14 - Create new folder for users in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendTasksAdminMenu: function () {
        var deferred = new Ext.Deferred();

        setTimeout(function () {
            var viewsMenu = {
                menutype: 'folder',
                index: 14,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                text: CMDBuildUI.locales.Locales.administration.navigation.taskmanager,
                leaf: false,
                href: 'administration/tasks/reademails_empty',
                children: [{
                    menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    index: 1,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    text: CMDBuildUI.locales.Locales.administration.tasks.texts.reademails,
                    leaf: true,
                    href: 'administration/tasks/emailService'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    index: 2,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.importexport,
                    leaf: true,
                    href: 'administration/tasks/import_export'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    index: 3,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    text: CMDBuildUI.locales.Locales.administration.localizations.process,
                    leaf: true,
                    href: 'administration/tasks/workflow'
                }
                    //  {
                    //     menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    //     index: 2,
                    //     objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    //     text: CMDBuildUI.locales.Locales.administration.tasks.texts.sendemails,
                    //     leaf: true,
                    //     href: 'administration/tasks/sendamails'
                    // }, {
                    //     menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    //     index: 3,
                    //     objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    //     text: CMDBuildUI.locales.Locales.administration.tasks.texts.syncronousevents,
                    //     leaf: true,
                    //     href: 'administration/tasks/syncevents',
                    //     disabled: true
                    // }, {
                    //     menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    //     index: 4,
                    //     objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    //     text: CMDBuildUI.locales.Locales.administration.tasks.texts.asyncronousevents,
                    //     leaf: true,
                    //     href: 'administration/tasks/asyncevents',
                    //     disabled: true
                    // }, {
                    //     menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    //     index: 5,
                    //     objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    //     text: CMDBuildUI.locales.Locales.administration.tasks.texts.startprocesses,
                    //     leaf: true,
                    //     href: 'administration/tasks/startprocesses',
                    //     disabled: true
                    // }, {
                    //     menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    //     index: 6,
                    //     objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    //     text: CMDBuildUI.locales.Locales.administration.tasks.texts.wizardconnectors,
                    //     leaf: true,
                    //     href: 'administration/tasks/wizardconnectors',
                    //     disabled: true
                    // }
                ]
            };

            deferred.resolve(viewsMenu);
        });

        return deferred.promise;
    },

    /**
     * 15 - Create new folder for eamils in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendEmailsAdminMenu: function () {
        var deferred = new Ext.Deferred();

        setTimeout(function () {
            var viewsMenu = {
                menutype: 'folder',
                index: 15,
                objecttype: 'email',
                text: CMDBuildUI.locales.Locales.administration.navigation.email,
                leaf: false,
                href: 'administration/email_empty',
                children: [{
                    menutype: CMDBuildUI.model.administration.MenuItem.types.email,
                    index: 1,
                    objecttype: 'email',
                    text: CMDBuildUI.locales.Locales.administration.emails.accounts,
                    leaf: true,
                    href: 'administration/email/accounts'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.email,
                    index: 2,
                    objecttype: 'email',
                    text: CMDBuildUI.locales.Locales.administration.emails.templates,
                    leaf: true,
                    href: 'administration/email/templates'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.email,
                    index: 3,
                    objecttype: 'email',
                    text: CMDBuildUI.locales.Locales.administration.emails.queue,
                    leaf: true,
                    href: 'administration/email/queue'
                }]
            };

            deferred.resolve(viewsMenu);
        });

        return deferred.promise;
    },

    /**
     * 16 - Create new folder for gis in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendGisAdminMenu: function () {
        var deferred = new Ext.Deferred();

        setTimeout(function () {
            var viewsMenu = {
                menutype: 'folder',
                index: 16,
                objecttype: 'gis',
                text: CMDBuildUI.locales.Locales.administration.navigation.gis,
                leaf: false,
                href: 'administration/gis_empty',
                children: [{
                    menutype: CMDBuildUI.model.administration.MenuItem.types.gis,
                    index: 1,
                    objecttype: 'gis',
                    text: CMDBuildUI.locales.Locales.administration.gis.manageicons,
                    leaf: true,
                    href: 'administration/gis/manageicons'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.gis,
                    index: 2,
                    objecttype: 'gis',
                    text: CMDBuildUI.locales.Locales.administration.gis.externalservices,
                    leaf: true,
                    href: 'administration/gis/externalservices'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.gis,
                    index: 3,
                    objecttype: 'gis',
                    text: CMDBuildUI.locales.Locales.administration.gis.layersorder,
                    leaf: true,
                    href: 'administration/gis/layersorder'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.gis,
                    index: 4,
                    objecttype: 'gis',
                    text: CMDBuildUI.locales.Locales.administration.gis.geoserverlayers,
                    leaf: true,
                    href: 'administration/gis/geoserverslayers'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.gis,
                    index: 5,
                    objecttype: 'gis',
                    text: CMDBuildUI.locales.Locales.administration.navigation.gisnavigation,
                    leaf: true,
                    href: 'administration/gis/gisnavigation'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.gis,
                    index: 6,
                    objecttype: 'gis',
                    text: CMDBuildUI.locales.Locales.administration.gis.thematism,
                    leaf: true,
                    href: 'administration/gis/thematism'
                }]
            };

            deferred.resolve(viewsMenu);
        });

        return deferred.promise;
    },

    /**
     * 17 - Create new folder for bim in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendBimAdminMenu: function () {
        var deferred = new Ext.Deferred();

        setTimeout(function () {
            var viewsMenu = {
                menutype: 'folder',
                index: 17,
                objecttype: 'bim',
                text: CMDBuildUI.locales.Locales.administration.navigation.bim,
                leaf: false,
                href: 'administration/bim_empty',
                children: [{
                    menutype: CMDBuildUI.model.administration.MenuItem.types.bim,
                    index: 1,
                    objecttype: 'bim',
                    text: CMDBuildUI.locales.Locales.administration.bim.projects,
                    leaf: true,
                    href: 'administration/bim/projects'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.bim,
                    index: 2,
                    objecttype: 'bim',
                    text: CMDBuildUI.locales.Locales.administration.navigation.layers,
                    leaf: true,
                    href: 'administration/bim/layers'
                }
                ]
            };

            deferred.resolve(viewsMenu);
        });

        return deferred.promise;
    },

    /**
     * 18 - Create new folder for language in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendLanguagesMenu: function () {
        var deferred = new Ext.Deferred();

        setTimeout(function () {
            var viewsMenu = {
                menutype: 'folder',
                index: 18,
                objecttype: 'localization',
                text: CMDBuildUI.locales.Locales.administration.navigation.languages,
                leaf: false,
                href: 'administration/localization_empty',
                children: [{
                    menutype: CMDBuildUI.model.administration.MenuItem.types.localization,
                    index: 1,
                    objecttype: 'localization',
                    text: CMDBuildUI.locales.Locales.administration.localizations.configuration,
                    leaf: true,
                    iconCls: 'x-fa fa-globe',
                    href: 'administration/localizations/configuration'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.localization,
                    index: 2,
                    objecttype: 'localization',
                    text: CMDBuildUI.locales.Locales.administration.localizations.localization,
                    leaf: true,
                    iconCls: 'x-fa fa-globe',
                    href: 'administration/localizations/localization'
                }
                ]
            };

            deferred.resolve(viewsMenu);
        });

        return deferred.promise;
    },
    appendImportExportMenu: function () {
        var deferred = new Ext.Deferred();
        setTimeout(function () {
            var menu = {
                menutype: 'folder',
                index: 19,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.importexport,
                text: CMDBuildUI.locales.Locales.administration.navigation.importexports,
                leaf: false,
                href: 'administration/importexport_empty',
                children: []
            };

            menu.children.push({
                menutype: CMDBuildUI.model.administration.MenuItem.types.importexport,
                index: 1,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.importexports,
                text: CMDBuildUI.locales.Locales.administration.importexport.texts.templates,
                leaf: true,
                iconCls: 'x-fa fa-list',
                href: 'administration/importexport/templates'
            });
            deferred.resolve(menu);
        });

        return deferred.promise;
    },

    /**
     * 20 - Create new folder for configuration in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem} 
     */
    appendSettingsMenu: function () {
        var deferred = new Ext.Deferred();

        setTimeout(function () {
            var viewsMenu = {
                menutype: 'folder',
                index: 20,
                objecttype: 'setup',
                text: CMDBuildUI.locales.Locales.administration.navigation.systemconfig,
                leaf: false,
                href: 'administration/setup_empty',
                children: [{
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 1,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.generaloptions,
                    leaf: true,
                    iconCls: 'x-fa fa-wrench',
                    href: 'administration/setup/generaloptions'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 2,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.multitenant,
                    leaf: true,
                    iconCls: 'x-fa fa-wrench',
                    href: 'administration/setup/multitenant'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 3,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.workflow,
                    leaf: true,
                    iconCls: 'x-fa fa-wrench',
                    href: 'administration/setup/workflow'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 4,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.dms,
                    leaf: true,
                    iconCls: 'x-fa fa-wrench',
                    href: 'administration/setup/documentmanagementsystem'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 5,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.gis,
                    leaf: true,
                    iconCls: 'x-fa fa-wrench',
                    href: 'administration/setup/gis'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 6,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.bim,
                    leaf: true,
                    iconCls: 'x-fa fa-wrench',
                    href: 'administration/setup/bim'
                }, {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 7,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.servermanagement,
                    leaf: true,
                    iconCls: 'x-fa fa-wrench',
                    href: 'administration/setup/servermanagement'
                }]
            };

            viewsMenu.children.push();
            deferred.resolve(viewsMenu);
        });

        return deferred.promise;
    },
    selectAndRedirectToRecordBy: function (key, value, controller) {
        var navTree = Ext.getCmp('administrationNavigationTree');
        var store = navTree.getStore();
        var record = store.findNode(key, value);
        var vm = navTree.getViewModel();
        controller.redirectTo(value, true);
        vm.set('selected', record);
    },
    changeRecordBy: function (key, value, newDescription, controller) {
        var navTree = Ext.getCmp('administrationNavigationTree');
        var store = navTree.getStore();
        var record = store.findNode(key, value);
        record.set('text', newDescription);
        var sorted = record.parentNode.childNodes.sort(this.sortByText);
        sorted.forEach(function (item, index) {
            item.set('index', index);
        });
        record.parentNode.childNodes = sorted;
        record.parentNode.data.children = sorted;
        store.sort('index', 'ASC');
    },
    removeRecordBy: function (key, value, nextUrl, controller) {
        var navTree = Ext.getCmp('administrationNavigationTree');
        var store = navTree.getStore();
        var record = store.findNode(key, value);
        if (record) {
            record.remove();
        }
        if (controller && nextUrl) {
            controller.redirectTo(nextUrl, true);
        }
        var currentNode = store.findNode("href", Ext.History.getToken());

        if (!currentNode) {
            currentNode = this.getFirstSelectableMenuItem(store.getRootNode().childNodes);
        }
        var vm = navTree.getViewModel();
        vm.set('selected', currentNode);
    },
    onMenuStoreReady: function (store, callback) {
        var navTree;
        if (!store) {
            navTree = Ext.getCmp('administrationNavigationTree');
            store = navTree.getStore();
        }
        var currentNode = store.findNode("href", Ext.History.getToken());

        if (!currentNode) {
            currentNode = this.getFirstSelectableMenuItem(store.getRootNode().childNodes);
        }
        if (navTree && currentNode) {
            var vm = navTree.getViewModel();
            vm.set('selected', currentNode);
        }
        if (typeof callback === 'function') {
            callback();
        }
        // }
    },

    /**
     * Return the json definition for records as tree.
     * @param {Ext.data.Model[]} records The plain list of items.
     * @param {String} menutype The menu type to use for these records.
     * @param {String} parentmenu The name of the parent item.
     * 
     * @returns {Object[]} A list of CMDBuild.model.MenuItem definitions.
     */
    getRecordsAsSubmenu: function (records, menutype, parentname) {
        var output = [];
        var me = this;

        var frecords = Ext.Array.filter(records, function (item) {
            return item.getData().hasOwnProperty('parent') && item.getData().parent === parentname;
        });

        switch (menutype) {
            case CMDBuildUI.model.administration.MenuItem.types.domain:
            case CMDBuildUI.model.administration.MenuItem.types.menu:
            case CMDBuildUI.model.administration.MenuItem.types.report:
            case CMDBuildUI.model.administration.MenuItem.types.groupsandpermissions:
            case CMDBuildUI.model.administration.MenuItem.types.custompage:
            case CMDBuildUI.model.administration.MenuItem.types.contextmenu:
            case CMDBuildUI.model.administration.MenuItem.types.navigationtree:
            case CMDBuildUI.model.administration.MenuItem.types.searchfilter:
                frecords = records;
                break;
            case CMDBuildUI.model.administration.MenuItem.types.view:
                frecords = records;
                break;
            default:
                break;
        }

        for (var i = 0; i < frecords.length; i++) {

            var record = frecords[i].getData();
            var menuitem = {
                menutype: menutype,
                index: i,
                objecttype: decodeURI(record.name),
                text: record.description,
                leaf: true
            };

            switch (menutype) {
                case CMDBuildUI.model.administration.MenuItem.types.klass:
                    menuitem.href = 'administration/classes/' + menuitem.objecttype;
                    if (record.prototype) {
                        menuitem.leaf = false;
                        menuitem.children = me.getRecordsAsSubmenu(records, menutype, record.name);
                    }
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.lookuptype:
                    menuitem.iconCls = 'x-fa fa-table';
                    if (record.prototype) {
                        menuitem.leaf = false;
                        menuitem.children = me.getRecordsAsSubmenu(records, menutype, record.name);
                    }
                    menuitem.objecttype = CMDBuildUI.util.Utilities.stringToHex(menuitem.objecttype);
                    menuitem.href = 'administration/lookup_types/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.domain:
                    menuitem.iconCls = 'x-fa fa-table';
                    menuitem.href = 'administration/domains/' + menuitem.objecttype;
                    if (record.prototype) {
                        menuitem.leaf = false;
                        menuitem.children = me.getRecordsAsSubmenu(records, menutype, record.name);
                    }
                    break;

                case CMDBuildUI.model.administration.MenuItem.types.groupsandpermissions:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = 'x-fa fa-users';
                    menuitem.href = 'administration/groupsandpermissions/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.menu:
                    menuitem.objecttype = record._id; // TODO: use getRecordId() not work!!
                    menuitem.iconCls = 'x-fa fa-users';
                    menuitem.href = 'administration/menus/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.process:
                    menuitem.objecttype = record._id; // TODO: use getRecordId() not work!!
                    menuitem.iconCls = 'x-fa fa-cog';
                    menuitem.href = 'administration/processes/' + menuitem.objecttype;
                    if (record.prototype) {
                        menuitem.leaf = false;
                        menuitem.iconCls = 'x-fa fa-cogs';
                        menuitem.children = me.getRecordsAsSubmenu(records, menutype, record.name);
                    }
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.report:
                    menuitem.objecttype = record._id; // TODO: use getRecordId() not work!!
                    menuitem.iconCls = 'x-fa fa-files-o';
                    menuitem.href = 'administration/reports/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.custompage:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = 'x-fa fa-code';
                    menuitem.href = 'administration/custompages/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.contextmenu:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = 'x-fa fa-code';
                    menuitem.href = 'administration/customcomponents/contextmenu/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.navigationtree:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = 'x-fa fa-sitemap';
                    menuitem.href = 'administration/navigationtrees/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.searchfilter:
                    menuitem.objecttype = record.name;
                    menuitem.iconCls = 'x-fa fa-binoculars';
                    menuitem.href = 'administration/searchfilters/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.view:
                    menuitem.objecttype = record.name;
                    menuitem.iconCls = 'x-fa fa-list-alt';
                    menuitem.href = 'administration/views/' + menuitem.objecttype;
                    break;

            }

            output.push(menuitem);
        }
        return output;
    },

    /**
     * Return the json definition for records as tree.
     * @param {Ext.data.Model[]} records The plain list of items.
     * @param {String} menutype The menu type to use for these records.
     * 
     * @returns {Object[]} A list of CMDBuild.model.MenuItem definitions.
     */
    getRecordsAsList: function (records, menutype) {
        var output = [];

        for (var i = 0; i < records.length; i++) {
            var record = records[i].getData();
            var menuitem = {
                menutype: menutype,
                index: i,
                objectid: record._id,
                text: record.description,
                leaf: true
            };
            output.push(menuitem);
        }
        return output;
    },

    privates: {
        /**
         * @private
         */
        sortByDescription: function (a, b) {
            if ((a && a.get('description')) && (b && b.get('description'))) {
                var nameA = a.get('description').toUpperCase(); // ignore upper and lowercase
                var nameB = b.get('description').toUpperCase(); // ignore upper and lowercase
                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }
                // if descriptions are same
                return 0;
            }
        },

        /**
         * @private
         */
        sortByText: function (a, b) {
            if (a.get('text') && b.get('text')) {
                var nameA = a.get('text').toUpperCase(); // ignore upper and lowercase
                var nameB = b.get('text').toUpperCase(); // ignore upper and lowercase
                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }
                // if descriptions are same
                return 0;
            }
        },
        /**
         * @param {CMDBuildUI.model.menu.MenuItem[]} items
         * @return {CMDBuildUI.model.menu.MenuItem} First selectable menu item
         */
        getFirstSelectableMenuItem: function (items) {
            var item;
            var i = 0;
            while (!item && i < items.length) {
                var node = items[i];
                if (node.get("menutype") !== CMDBuildUI.model.menu.MenuItem.types.folder) {
                    item = node;
                } else {
                    item = this.getFirstSelectableMenuItem(node.childNodes);
                }
                i++;
            }
            return item;
        }
    }
});