Ext.define('CMDBuildUI.view.administration.content.menus.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-menus-view',

    control: {
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#editBtn':{
            click: 'onEditBtnClick'
        }
    },

    setCopyButton: function (view) {
        var me = this;
        me.getViewModel().bind('{theMenu}', function (theMenu) {
            var copyFromButton = view.up('components-administration-toolbars-formtoolbar').down('#copyFrom');
            copyFromButton.menu.removeAll();
            var menuAdministration = Ext.getStore('administration.MenuAdministration');
            var menu = menuAdministration.findNode("objecttype", 'menu');
            if (menu && menu.childNodes) {
                Ext.Array.forEach(menu.childNodes, function (element) {
                    if (element.get('text') !== me.getViewModel().get('theMenu.name')) {
                        copyFromButton.menu.add({
                            text: element.get('text'),
                            iconCls: 'x-fa fa-users',
                            listeners: {
                                click: function () {
                                    me.cloneFrom(element, view);
                                }
                            }
                        });
                    }

                });
            }
        });
    },
    cloneFrom: function (menu, view, currentGrantsStore) {
        var vm = view.up("administration-content-menu-view").getViewModel();
        CMDBuildUI.util.administration.helper.AjaxHelper.getMenuForGroup(menu.get('objecttype')).then(
            function (response) {
                vm.set('theMenu.children', response.children);
            },
            function (e) {
                CMDBuildUI.util.Logger.log("Error on get menu for group", CMDBuildUI.util.Logger.levels.error);
            }
        );

    },
    /**
     * 
     * @param {*} button 
     * @param {*} event 
     * @param {*} eOpts 
     */
    onEditBtnClick: function (button, event, eOpts) {
        button.lookupViewModel().setCurrentAction(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        var me = this;
        var theMenu = this.getViewModel().get('theMenu');
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.menus.strings.delete,
            CMDBuildUI.locales.Locales.administration.menus.strings.areyousuredeleteitem,
            function (action) {
                if (action === "yes") {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);

                    CMDBuildUI.util.Ajax.setActionId('delete-menu');
                    theMenu.erase({
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheMenuUrl();
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', Ext.util.History.getToken(), nextUrl, me);
                        },
                        callback: function (record, reason) {
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                        }
                    });
                }
            }, this);
    }
});