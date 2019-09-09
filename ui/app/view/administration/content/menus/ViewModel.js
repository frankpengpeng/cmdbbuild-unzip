Ext.define('CMDBuildUI.view.administration.content.menus.ViewModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'CMDBuildUI.util.api.Groups',
        'CMDBuildUI.util.MenuStoreBuilder'
    ],
    alias: 'viewmodel.administration-content-menus-view',

    data: {
        title: CMDBuildUI.locales.Locales.administration.menus.singular,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.menus.singular'
        },
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        actions: {
            edit: false,
            add: false,
            view: true
        },
        newFolderName: '',
        canAddNewFolder: false,
        unusedRoles: [],
        autoLoadRolesStore: null
    },

    formulas: {
        theMenuName: {
            bind: '{theMenu.name}',
            get: function (theMenuName) {
                if (theMenuName === '_default') {
                    return CMDBuildUI.locales.Locales.administration.common.strings.default; // '*Default*';
                }
                return theMenuName;
            }
        },
        unusedRolesDataManager: {
            bind: '{theMenu}',
            get: function (theMenu) {
                var me = this;
                CMDBuildUI.util.Stores.loadGroupsStore().then(
                    function (roles) {
                        roles = CMDBuildUI.util.administration.helper.SortHelper.sort(roles, 'description');
                        
                        CMDBuildUI.util.Stores.loadAdministrationMenusStore().then(
                            function (menus) {
                                var defaultMenu = Ext.Array.findBy(menus, function (menu) {
                                    return menu.get('group') === '_default';
                                });

                                if (!defaultMenu) {
                                    var unusedRoles = me.get('unusedRoles');
                                    unusedRoles.push({
                                        value: '_default',
                                        label: CMDBuildUI.locales.Locales.administration.common.strings.default
                                    });
                                    me.set('unusedRoles', unusedRoles);
                                }

                                Ext.Array.forEach(roles, function (role) {
                                    var menuForRole = Ext.Array.findBy(menus, function (menu) {
                                        return menu.get('group') === role.get('name');
                                    });
                                    if (!menuForRole) {
                                        var unusedRoles = me.get('unusedRoles');
                                        unusedRoles.push({
                                            value: role.get('name'),
                                            label: role.get('description')
                                        });
                                        me.set('unusedRoles', unusedRoles);
                                    }
                                });
                                me.set('autoLoadRolesStore', true);
                            }
                        );
                    },
                    function () {

                    }
                );
            }
        }
    },

    stores: {

        rolesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{unusedRoles}',
            autoLoad: '{autoLoadRolesStore}',
            proxy: {
                type: 'memory'
            }
        }
    },

    setCurrentAction: function (action) {
        this.set('actions.edit', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.set('actions.add', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
        this.set('actions.view', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        this.set('action', action);
    },

    setExistingMenus: function (value) {
        this.set('existingMenu', value);
    }
});