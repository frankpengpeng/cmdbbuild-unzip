Ext.define('CMDBuildUI.view.management.navigation.TreeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-navigation-tree',

    listen: {
        global: {
            objecttypechanged: 'onObjectTypeNameChanged'
        }
    },

    control: {
        '#': {
            selectionchange: "onSelectionChange"
        }
    },

    /**
     * Item click listener.
     * 
     * @param {Ext.event.Event} event    The Ext.event.Event encapsulating the DOM event.
     * @param {HTMLElement} element      The target of the event.
     * @param {Object} eOpts             The options object passed to Ext.util.Observable.addListener.
     */
    onItemClick: function (event, element, eOpts) {
        var view = Ext.get(element).component;
        if (view) {
            var node = view.getNode();
            var url;
            var menutype = node.get("menutype");
            if (menutype !== CMDBuildUI.model.menu.MenuItem.types.folder) {
                switch (menutype) {
                    case CMDBuildUI.model.menu.MenuItem.types.klass:
                        url = 'classes/' + node.get("objecttypename") + '/cards';
                        break;
                    case CMDBuildUI.model.menu.MenuItem.types.process:
                        url = 'processes/' + node.get("objecttypename") + '/instances';
                        break;
                    case CMDBuildUI.model.menu.MenuItem.types.custompage:
                        url = 'custompages/' + node.get("objecttypename");
                        break;
                    case CMDBuildUI.model.menu.MenuItem.types.report:
                        url = 'reports/' + node.get("objecttypename");
                        break;
                    case CMDBuildUI.model.menu.MenuItem.types.reportpdf:
                        url = 'reports/' + node.get("objecttypename") + '/pdf';
                        break;
                    case CMDBuildUI.model.menu.MenuItem.types.reportcsv:
                        url = 'reports/' + node.get("objecttypename") + '/csv';
                        break;
                    case CMDBuildUI.model.menu.MenuItem.types.view:
                        url = 'views/' + node.get("objecttypename") + '/items';
                        break;
                    default:
                        Ext.Msg.alert('Warning', 'Menu type not implemented!');
                }
            }

            if (url !== undefined) {
                this.redirectTo(url);
            }
        }
    },

    /**
     * Item double click listener.
     * 
     * @param {Ext.event.Event} event    The Ext.event.Event encapsulating the DOM event.
     * @param {HTMLElement} element      The target of the event.
     * @param {Object} eOpts             The options object passed to Ext.util.Observable.addListener.
     */
    onItemDblClick: function (event, element, eOpts) {
        var view = Ext.get(element).component;
        if (!(view && view.getExpandable())) {
            event.stopEvent();
            return false;
        }
        if (!view.isExpanded()) {
            view.expand();
        } else {
            view.collapse();
        }
    },

    /**
     * Update navigation selection
     * @param {String} newobjecttypename
     */
    onObjectTypeNameChanged: function (newobjecttypename) {
        var vm = this.getViewModel();
        var selected = vm.get("selected");
        if (!selected || selected.get("objecttypename") !== newobjecttypename) {
            vm.bind({
                bindTo: '{menuItems}'
            }, function(store) {
                var newselected = store.findNode("objecttypename", newobjecttypename);
                vm.set("selected", newselected);
            });
        }
    },

    /**
    * @param {Ext.list.Tree} view
    * @param {Ext.data.TreeModel} record
    * @param {Object} eOpts
    */
    onSelectionChange: function (view, record, eOpts) {
        // expand nodes
        var node = view.getItem(record);
        if (node) {  // workaround for #622
            this.expandNodeHierarchy(node);
        }
    },

    privates: {
        expandNodeHierarchy: function (node) {
            node.expand();
            if (node.getParentItem()) {
                this.expandNodeHierarchy(node.getParentItem());
            }
        }
    }

});
