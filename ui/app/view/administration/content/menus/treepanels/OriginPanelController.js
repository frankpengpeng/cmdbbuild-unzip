Ext.define('CMDBuildUI.view.administration.content.menus.treepanels.OriginPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-menus-treepanels-originpanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {

        var me = this,
            storesArray = [
                'classes.Classes',
                'reports.Reports',
                'views.Views',
                'Dashboards',
                'custompages.CustomPages'
            ];
        var wfEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled);
        if (wfEnabled) {
            storesArray.push('processes.Processes');
        }
        
        Ext.Array.forEach(storesArray, function (storeId) {
            //For checking store is created or not
            //if store is not created we can create dyanamically using passing storeId/alias
            var store = Ext.getStore(storeId);
            if (Ext.isDefined(store) === false) {
                store = Ext.create(storeId);
            }
            store.load({
                callback: function () {
                    //On every store call back we can remove data from storesArray or maintain a veribale for checking.
                    Ext.Array.remove(storesArray, this.storeId);
                    if (storesArray.length === 0) {                        
                        me.generateMenu(view);
                    }
                }
            });
        });

    },
    /** 
     * @param {*} view 
     */
    generateMenu: function (view) {

        var classesItems = Ext.getStore('classes.Classes').getData().getRange();
        var processesItems = Ext.getStore('processes.Processes').getData().getRange();
        var reportsItems = Ext.getStore('reports.Reports').getData().getRange();
        var viewsItems = Ext.getStore('views.Views').getData().getRange();
        var dashboardsItems = Ext.getStore('Dashboards').getData().getRange();
        var custompagesItems = Ext.getStore('custompages.CustomPages').getData().getRange();

        var store = view.getStore();
        if(!store){
            return;
        }
        var root = store.getRootNode();
        root.removeAll();
        var i = 0;
        root.appendChild({
            menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
            index: i++,
            objectdescription: CMDBuildUI.locales.Locales.administration.navigation.classes, // Classes
            leaf: false,
            allowDrag: false,
            expanded: false,
            children: this.generateChildren(classesItems, CMDBuildUI.model.menu.MenuItem.types.klass)
        });
        var wfEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled);
        if (wfEnabled) {
            root.appendChild({
                menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
                index: i++,
                objectdescription: CMDBuildUI.locales.Locales.administration.navigation.processes, // Processes
                leaf: false,
                allowDrag: false,
                expanded: false,
                children: this.generateChildren(processesItems, CMDBuildUI.model.menu.MenuItem.types.process)
            });
        }
        
        root.appendChild({
            menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
            index: i++,
            objectdescription: CMDBuildUI.locales.Locales.administration.navigation.reports, // Reports
            leaf: false,
            allowDrag: false,
            expanded: false,
            children: this.generateChildren(reportsItems, CMDBuildUI.model.menu.MenuItem.types.report)
        });
        root.appendChild({
            menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
            index: i++,
            objectdescription: CMDBuildUI.locales.Locales.administration.navigation.views, // Views
            leaf: false,
            allowDrag: false,
            expanded: false,
            children: this.generateChildren(viewsItems, CMDBuildUI.model.menu.MenuItem.types.view)
        });

        root.appendChild({
            menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
            index: i++,
            objectdescription: CMDBuildUI.locales.Locales.administration.navigation.dashboards, // Dashboards
            leaf: false,
            allowDrag: false,
            expanded: false,
            children: this.generateChildren(dashboardsItems, CMDBuildUI.model.menu.MenuItem.types.dashboard)
        });

        root.appendChild({
            menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
            index: i++,
            objectdescription: CMDBuildUI.locales.Locales.administration.navigation.custompages, // Custom pages
            leaf: false,
            allowDrag: false,
            expanded: false,
            children: this.generateChildren(custompagesItems, CMDBuildUI.model.menu.MenuItem.types.custompage)
        });

    },
    privates: {

        /**
         * 
         * @private
         */
        generateChildren: function (items, type) {
            var me = this;
            var _items = [];
            var destination = this.getView().up('administration-content-menus-mainpanel').down('#treepaneldestination');
            items.forEach(function (element, index) {
                var isAlredyInMenu = destination.getStore().getData().findBy(function (item) {
                    return item.get('objecttype') === element.get('_id') && item.get('menutype') === type;
                });
                if (!isAlredyInMenu) {
                    switch (type) {
                        case CMDBuildUI.model.menu.MenuItem.types.klass:
                            _items.push(me.generateClassChildren(type, element, index, CMDBuildUI.locales.Locales.administration.localizations.class));
                            break;
                        case CMDBuildUI.model.menu.MenuItem.types.process:
                            _items.push(me.generateClassChildren(type, element, index, CMDBuildUI.locales.Locales.administration.localizations.process));
                            break;
                        case CMDBuildUI.model.menu.MenuItem.types.custompage:
                            _items.push(me.generateCustompageChildren(type, element, index, CMDBuildUI.locales.Locales.administration.localizations.custompage));
                            break;
                        case CMDBuildUI.model.menu.MenuItem.types.report:
                        case CMDBuildUI.model.menu.MenuItem.types.reportcsv:
                        case CMDBuildUI.model.menu.MenuItem.types.reportodt:
                        case CMDBuildUI.model.menu.MenuItem.types.reportpdf:
                        case CMDBuildUI.model.menu.MenuItem.types.reportrtf:
                            _items.push(me.generateReportChildren(CMDBuildUI.model.menu.MenuItem.types.reportpdf, element, index, Ext.String.format('{0} {1}', CMDBuildUI.locales.Locales.administration.localizations.report, CMDBuildUI.locales.Locales.administration.localizations.pdf)));
                            _items.push(me.generateReportChildren(CMDBuildUI.model.menu.MenuItem.types.reportcsv, element, index, Ext.String.format('{0} {1}', CMDBuildUI.locales.Locales.administration.localizations.report, CMDBuildUI.locales.Locales.administration.localizations.csv)));
                            break;

                        case CMDBuildUI.model.menu.MenuItem.types.dashboard:
                            _items.push(me.generateClassChildren(type, element, index, CMDBuildUI.locales.Locales.administration.localizations.dashboard));
                            break;

                        case CMDBuildUI.model.menu.MenuItem.types.view:
                            _items.push(me.generateClassChildren(type, element, index, CMDBuildUI.locales.Locales.administration.localizations.view));
                            break;
                        default:
                            _items.push(me.generateClassChildren(type, element, index));
                            break;
                    }
                }
            });
            return _items;
        },
        generateClassChildren: function (type, element, index, qtip) {
            // ok
            var uuid = CMDBuildUI.util.Utilities.generateUUID();
            var leaf = {
                _id: uuid,
                id: uuid,
                menutype: type,
                index: index,
                objecttype: element.get('_id'),
                text: element.get('description'),
                objectdescription: element.get('description'),
                objectDescription: element.get('description'),
                leaf: true,
                qtip: qtip
            };
            leaf = Ext.create('CMDBuildUI.model.menu.MenuItem', leaf);
            if (element.get('prototype')) {
                switch (type) {
                    case CMDBuildUI.model.menu.MenuItem.types.klass:
                        leaf.data.iconCls = CMDBuildUI.model.menu.MenuItem.icons.klassparent;
                        break;
                    case CMDBuildUI.model.menu.MenuItem.types.process:
                        leaf.data.iconCls = CMDBuildUI.model.menu.MenuItem.icons.processparent;
                        break;
                    default:
                        break;
                }
            }


            return leaf;
        },
        generateCustompageChildren: function (type, element, index, qtip) {
            // ok
            var uuid = CMDBuildUI.util.Utilities.generateUUID();
            var leaf = {
                _id: uuid,
                id: uuid,
                menutype: type,
                index: index,
                objecttype: element.get('name'),
                objectid: element.get('_id'),
                text: element.get('description'),
                objectdescription: element.get('description'),
                objectDescription: element.get('description'),
                leaf: true,
                qtip: qtip
            };
            leaf = Ext.create('CMDBuildUI.model.menu.MenuItem', leaf);
            return leaf;
        },
        generateReportChildren: function (type, element, index, qtip) {

            var uuid = CMDBuildUI.util.Utilities.generateUUID();
            var leaf = {
                _id: uuid,
                id: uuid,
                menutype: type,
                index: index,
                objecttype: element.get('code'),
                objectid: element.get('_id'),
                text: element.get('description'),
                objectdescription: element.get('description'),
                objectDescription: element.get('description'),
                leaf: true,
                qtip: qtip
            };

            leaf = Ext.create('CMDBuildUI.model.menu.MenuItem', leaf);
            return leaf;
        }
    }
});