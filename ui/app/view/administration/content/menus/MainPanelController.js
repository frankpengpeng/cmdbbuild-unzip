Ext.define('CMDBuildUI.view.administration.content.menus.MainPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-menus-mainpanel',

    control: {

        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        }
    },

    /**
    * @param {Ext.button.Button} button
    * @param {Event} e
    * @param {Object} eOpts
    */
    onAddFolderBtnClick: function (button, e, eOpts) {
        var index;
        var vm = this.getViewModel();
        var newFolderName = vm.get('newFolderName');
        if (!newFolderName) {
            CMDBuildUI.util.Notifier.showMessage(
                CMDBuildUI.locales.Locales.administration.menus.strings.emptyfoldername, {
                    ui: 'administration',
                    icon: CMDBuildUI.util.Notifier.icons.error
                }
            );
            return false;
        }
        var destination = this.lookupReference('menuTreeViewDestination');
        var destinationStore = destination.getStore();
        var selectedDestinationNode = destination.getSelectionModel().getSelected();
        if (selectedDestinationNode.length > 0) {
            index = selectedDestinationNode.items[0].childNodes.length;
        }
        var nodeModel = Ext.create('CMDBuildUI.model.menu.MenuItem', {
            objectdescription: newFolderName,
            objectDescription: newFolderName,
            menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
            menuType: CMDBuildUI.model.menu.MenuItem.types.folder,
            expanded: true,
            children: [],
            index: index
        });
        if (selectedDestinationNode.length === 0) {
            destinationStore.getRoot().appendChild(nodeModel);
        } else {
            if (selectedDestinationNode.items[0].get('leaf')) {
                this.getParentNode(selectedDestinationNode.items[0]).appendChild(nodeModel);
            } else {
                selectedDestinationNode.items[0].appendChild(nodeModel);
            }
        }

        vm.set('newFolderName', '');
    },

    /**
    * @param {Ext.button.Button} button
    * @param {Event} e
    * @param {Object} eOpts
    */
    onRemoveItemBtnClick: function (button, e, eOpts) {
        var destination = button.up('administration-content-menus-mainpanel').down('#treepaneldestination');
        var store = destination.getStore();
        var selectedDestinationNode = destination.getSelectionModel().getSelection()[0];
        if (!selectedDestinationNode.get('root')) {
            selectedDestinationNode.remove();
            store.sync();
        }
        var origin = button.up('administration-content-menus-mainpanel').down('#treepanelorigin');
        origin.getController().generateMenu(origin);
        origin.expandAll();
    },

    /**
    * @param {Ext.button.Button} button
    * @param {Event} e
    * @param {Object} eOpts
    */
    onSaveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var me = this;
        button.setDisabled(true);

        var store = this.getView().down('administration-content-menus-treepanels-destinationpanel').getStore();
        var root = store.getRoot();
        var rootData = root.childNodes;
        var vm = me.getView().getViewModel();
        var theMenu = vm.get('theMenu');
        var menu = (!theMenu.isModel) ? Ext.create('CMDBuildUI.model.menu.Menu', theMenu) : theMenu;

        var group = menu.get('group');

        if (group.length > 0) {

            var rootRaw = {
                group: group,
                children: [],
                menuType: rootData.menutype || 'root',
                objectDescription: rootData.objectdescription || 'ROOT',
                _id: menu.getId(),
                objectTypeName: rootData.objecttype
            };

            root.childNodes.forEach(function (item, index) {
                rootRaw.children.push({
                    children: me.getRawTree(item.childNodes),
                    menuType: item.get('menutype'),
                    objectDescription: item.get('objectdescription'),
                    _id: item.get('_id'),
                    objectTypeName: item.get('objecttype')
                });
            });
            if (vm.get('actions.add')) {
                CMDBuildUI.util.administration.helper.AjaxHelper.createMenuForGroup(rootRaw).then(
                    function (response) {
                        vm.getParent().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheMenuUrl(response._id);
                        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                            function () {
                                CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                            });
                    },
                    function (e) {
                        CMDBuildUI.util.Logger.log('Menu create error', 'error');
                    });
            } else {
                CMDBuildUI.util.administration.helper.AjaxHelper.updateMenuForGroup(menu.getId(), rootRaw).then(
                    function (response) {
                        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheMenuUrl(response._id);
                        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                    },
                    function (e) {
                        CMDBuildUI.util.Logger.log('Menu update error', 'error');
                    });
            }
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        if (vm.get('actions.edit')) {
            this.redirectTo(CMDBuildUI.util.administration.helper.ApiHelper.client.getTheMenuUrl(vm.get('theMenu._id')), true);
        } else if (vm.get('actions.add')) {
            var store = Ext.getStore('administration.MenuAdministration');
            vm = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode("objecttype", CMDBuildUI.model.administration.MenuItem.types.menu);
            vm.set('selected', currentNode);
            this.redirectTo(CMDBuildUI.util.administration.helper.ApiHelper.client.getTheMenuUrl(), true);
        }
    },

    privates: {


        getParentNode: function (node) {
            if (node.get('leaf')) {
                return this.getParentNode(node.parentNode);
            }
            return node;
        },


        getRawTree: function (childNodes) {
            var me = this;
            var output = [];
            childNodes.forEach(function (item, index) {
                var _item = {
                    children: [],
                    menuType: item.get('menutype'),
                    objectDescription: item.get('objectdescription'),
                    _id: item.get('_id'),
                    objectTypeName: item.get('objecttype')
                };

                if (item.hasChildNodes()) {
                    _item.children = me.getRawTree(item.childNodes);
                }
                output.push(_item);
            });
            return output;
        }

    }
});