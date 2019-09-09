Ext.define('CMDBuildUI.view.management.navigation.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-navigation-container',

    control: {
        '#': {
            afterrender: 'onAfterRender',
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * @param {CMDBuildUI.view.management.navigation.Container} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        var store = Ext.getStore('menu.Menu');

        // select menu item
        var startingMenuItem = CMDBuildUI.util.helper.UserPreferences.getPreferences().get(CMDBuildUI.model.users.Preference.startingpage);

        // get startin node
        var startingNode;
        if (startingMenuItem) {
            startingNode = store.findNode("objecttypename", startingMenuItem);
        }
        if (!startingNode) {
            startingNode = this.getFirstSelectableMenuItem(store.getRootNode().childNodes);
        }

        if (!startingNode) {
            CMDBuildUI.util.Navigation.addIntoManagemenetContainer('panel', {
                title: '&nbsp;',
                layout: 'fit'
            });
            return;
        }

        var url;
        switch (startingNode.get("menutype")) {
            case CMDBuildUI.model.menu.MenuItem.types.klass:
                url = Ext.String.format("classes/{0}/cards", startingNode.get("objecttypename"));
                break;
            case CMDBuildUI.model.menu.MenuItem.types.process:
                url = Ext.String.format("processes/{0}/instances", startingNode.get("objecttypename"));
                break;
            case CMDBuildUI.model.menu.MenuItem.types.report:
                url = Ext.String.format("reports/{0}", startingNode.get("objecttypename"));
                break;
            case CMDBuildUI.model.menu.MenuItem.types.reportpdf:
                url = Ext.String.format("reports/{0}/pdf", startingNode.get("objecttypename"));
                break;
            case CMDBuildUI.model.menu.MenuItem.types.reportcsv:
                url = Ext.String.format("reports/{0}/csv", startingNode.get("objecttypename"));
                break;
            case CMDBuildUI.model.menu.MenuItem.types.dashboard:
                url = Ext.String.format("dashboards/{0}", startingNode.get("objecttypename"));
                break;
            case CMDBuildUI.model.menu.MenuItem.types.custompage:
                url = Ext.String.format('custompages/{0}', startingNode.get("objecttypename"));
                break;
            case CMDBuildUI.model.menu.MenuItem.types.view:
                url = Ext.String.format('views/{0}/items', startingNode.get("objecttypename"));
                break;
        }
        this.redirectTo(url);
    },

    /**
     * @param {CMDBuildUI.view.management.navigation.Container} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var store = Ext.getStore('menu.Menu');

        // add menu
        view.add({
            xtype: 'management-navigation-tree',
            viewModel: {
                stores: {
                    menuItems: store
                }
            }
        });
    },

    privates: {
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
