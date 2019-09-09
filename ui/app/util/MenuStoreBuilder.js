Ext.define("CMDBuildUI.util.MenuStoreBuilder", {
    singleton: true,

    allItems: {},

    initialize: function (callback) {
        var store = Ext.getStore('menu.Menu');
        this.allItems = {};
        var children = [];

        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");

        if (privileges.class_access) {
            var classes = this.getClassesMenu();
            if (classes) {
                children.push(classes);
            }
        }

        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled) && privileges.process_access) {
            var processes = this.getProcessesMenu();
            if (processes) {
                children.push(processes);
            }
        }

        if (privileges.report_access) {
            var reports = this.getReportsMenu();
            if (reports) {
                children.push(reports);
            }
        }

        if (privileges.dashboard_access) {
            var dashboards = this.getDashboardsMenu();
            if (dashboards) {
                children.push(dashboards);
            }
        }

        if (privileges.dataview_access) {
            var views = this.getViewsMenu();
            if (views) {
                children.push(views);
            }
        }

        if (privileges.custompages_access) {
            var custompages = this.getCustomPagesMenu();
            if (custompages) {
                children.push(custompages);
            }
        }

        var allItems = {
            menutype: 'folder',
            index: store.getData().length,
            objectdescription: 'All items',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.allitems,
            leaf: false,
            children: children
        };
        this.allItems = allItems;

        if (children.length && store.getRoot()) {
            store.getRoot().appendChild(allItems);
        }
    },

    /**
     * classes list menu
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getClassesMenu: function () {
        var classesMenu;
        var store = Ext.getStore('classes.Classes');
        store.filter({
            property: "active",
            value: 'true',
            exactMatch: true
        });
        // sort by translated description
        store.sort("_description_translation", "ASC");
        var items = store.getRange();
        store.clearFilter();
        // create standard classes menu
        var standard = {
            menutype: 'folder',
            objecttype: 'Class',
            objectdescription: 'Standard',
            objectdescription_translation: CMDBuildUI.locales.Locales.classes.standard,
            leaf: false,
            children: this.getRecordsAsSubmenu(items, CMDBuildUI.model.menu.MenuItem.types.klass, 'Class')
        };

        store.filter("type", CMDBuildUI.model.classes.Class.classtypes.simple);
        var simpleclasses = store.getRange();
        store.clearFilter();

        // create simple classes menu
        if (simpleclasses.length) {
            var chidren = [];
            var simple = {
                menutype: 'folder',
                objecttype: 'Class',
                objectdescription: 'Simple',
                objectdescription_translation: CMDBuildUI.locales.Locales.classes.simple,
                leaf: false,
                children: this.getRecordsAsList(simpleclasses, CMDBuildUI.model.menu.MenuItem.types.klass)
            };

            // append standard classes tree
            if (!Ext.isEmpty(standard.children)) {
                chidren.push(standard);
            }
            // append simple classes tree
            if (!Ext.isEmpty(simple.children)) {
                chidren.push(simple);
            }

            // create class menu
            classesMenu = {
                menutype: 'folder',
                objecttype: 'Class',
                objectdescription: 'Classes',
                objectdescription_translation: CMDBuildUI.locales.Locales.menu.classes,
                leaf: false,
                children: chidren
            };
        } else {
            // create class menu
            classesMenu = Ext.apply(standard, {
                objectdescription: 'Classes',
                objectdescription_translation: CMDBuildUI.locales.Locales.menu.classes
            });
        }

        // append menu item to the store if has children
        if (!Ext.isEmpty(classesMenu.children)) {
            return classesMenu;
        }

        return;
    },

    /**
     * process list menu
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getProcessesMenu: function (navStore, callback) {
        var store = Ext.getStore('processes.Processes');
        store.filter({
            property: "active",
            value: 'true',
            exactMatch: true
        });
        // sort by translated description
        store.sort("_description_translation", "ASC");
        var items = store.getRange();
        store.clearFilter();
        var processesMenu = {
            menutype: 'folder',
            objecttype: 'Activity',
            objectdescription: 'Processes',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.processes,
            leaf: false,
            children: this.getRecordsAsSubmenu(items, CMDBuildUI.model.menu.MenuItem.types.process, 'Activity')
        };
        // append menu item to the store if has children
        if (!Ext.isEmpty(processesMenu.children)) {
            return processesMenu;
        }

        return;
    },

    /**
     * report list menu
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getReportsMenu: function (navStore, callback) {
        var store = Ext.getStore('reports.Reports');
        store.filter({
            property: "active",
            value: 'true',
            exactMatch: true
        });
        // sort by translated description
        store.sort("_description_translation", "ASC");
        var items = store.getRange();
        store.clearFilter();
        var reportsMenu = {
            menutype: 'folder',
            objectdescription: 'Reports',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.reports,
            leaf: false,
            children: this.getRecordsAsList(items, CMDBuildUI.model.menu.MenuItem.types.report)
        };
        // append menu item to the store if has children
        if (!Ext.isEmpty(reportsMenu.children)) {
            return reportsMenu;
        }

        return;
    },

    /**
     * dashboard list menu
     * @param {CMDBuildUI.store.menu.Menu} navStore
     * @param {function} callback
     */
    getDashboardsMenu: function (navStore, callback) {
        var store = Ext.getStore('Dashboards');
        store.filter({
            property: "active",
            value: 'true',
            exactMatch: true
        });
        // sort by translated description
        store.sort("_description_translation", "ASC");
        var items = store.getRange();
        store.clearFilter();
        var dashboardMenu = {
            menutype: 'folder',
            objectdescription: 'Dashboards',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.dashboards,
            leaf: false,
            children: this.getRecordsAsList(items, CMDBuildUI.model.menu.MenuItem.types.dashboard)
        };
        // append menu item to the store if has children
        if (!Ext.isEmpty(dashboardMenu.children)) {
            return dashboardMenu;
        }

        return;
    },

    /**
     * views list menu
     * @param {CMDBuildUI.store.menu.Menu} navStore
     * @param {function} callback
     */
    getViewsMenu: function (navStore, callback) {
        var store = Ext.getStore('views.Views');
        store.filter({
            property: "active",
            value: 'true',
            exactMatch: true
        });
        // sort by translated description
        store.sort("_description_translation", "ASC");
        var items = store.getRange();
        store.clearFilter();
        var viewMenu = {
            menutype: 'folder',
            objectdescription: 'Views',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.views,
            leaf: false,
            children: this.getRecordsAsList(items, CMDBuildUI.model.menu.MenuItem.types.view)
        };
        // append menu item to the store if has children
        if (!Ext.isEmpty(viewMenu.children)) {
            return viewMenu;
        }

        return;
    },

    /**
     * custompage list menu
     * @param {CMDBuildUI.store.menu.Menu} navStore
     * @param {function} callback
     */
    getCustomPagesMenu: function (navStore, callback) {
        var store = Ext.getStore('custompages.CustomPages');
        store.filter({
            property: "active",
            value: 'true',
            exactMatch: true
        });
        // sort by translated description
        store.sort("_description_translation", "ASC");
        var items = store.getRange();
        store.clearFilter();
        var custompageMenu = {
            menutype: 'folder',
            objectdescription: 'Custom pages',
            objectdescription_translation: CMDBuildUI.locales.Locales.menu.custompages,
            leaf: false,
            children: this.getRecordsAsList(items, CMDBuildUI.model.menu.MenuItem.types.custompage)
        };
        // append menu item to the store if has children
        if (!Ext.isEmpty(custompageMenu.children)) {
            return custompageMenu;
        }

        return;
    },

    onMenuStoreReady: function (callback) {
        Ext.callback(callback);
    },

    /**
     * Return the json definition for records as tree.
     * @param {Ext.data.Model[]} records The plain list of items.
     * @param {String} menutype The menu type to use for these records.
     * @param {String} parentmenu The name of the parent item.
     * 
     * @return {Object[]} A list of CMDBuild.model.MenuItem definitions.
     */
    getRecordsAsSubmenu: function (records, menutype, parentname) {
        var output = [];
        var me = this;

        var frecords = Ext.Array.filter(records, function (item) {
            return item.getData().parent && item.getData().parent === parentname;
        });

        frecords.forEach(function (record, i) {
            var menuitem = {
                menutype: menutype,
                index: i,
                objecttypename: record.getObjectTypeForMenu(),
                objectdescription: record.getTranslatedDescription(),
                leaf: true
            };
            if (record.get("prototype")) {
                menuitem.leaf = false;
                menuitem.children = me.getRecordsAsSubmenu(records, menutype, record.get("name"));
            }
            output.push(menuitem);
        });
        return output;
    },

    /**
     * Return the json definition for records as tree.
     * @param {Ext.data.Model[]} records The plain list of items.
     * @param {String} menutype The menu type to use for these records.
     * 
     * @return {Object[]} A list of CMDBuild.model.MenuItem definitions.
     */
    getRecordsAsList: function (records, menutype) {
        var output = [];

        records.forEach(function (record, i) {
            var menuitem = {
                menutype: menutype,
                index: i,
                objecttypename: record.getObjectTypeForMenu(),
                objectdescription: record.getTranslatedDescription(),
                leaf: true
            };
            output.push(menuitem);
        });
        return output;
    },

    /**
     * all classes list menu
     * @return {CMDBuildUI.model.menu.MenuItem}
     */
    getAllItemsMenu: function () {
        this.initialize();
        this.allItems.expanded = true;
        return this.allItems;
    }
});