Ext.define('CMDBuildUI.view.main.header.UserMenuController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.main-header-usermenu',

    control: {
        '#': {
            show: 'onAfterRender'
        },
        '#preferences': {
            click: 'onPreferencesClick'
        },
        '#changepassword': {
            click: 'onChangePasswordClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.main.header.UserMenu} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        var session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession();
        var menu = [];
        // Add user info
        menu.push({
            text: session.get("userDescription"),
            iconCls: 'x-fa fa-user',
            disabled: true
        }, {
            xtype: 'menuseparator'
        });

        var addseparator = false;
        // add change roles action
        if (session && session.get("availableRolesExtendedData").length > 1) {
            menu.push({
                text: CMDBuildUI.locales.Locales.main.changegroup,
                iconCls: 'x-fa fa-users',
                menu: this.getGroupMenu(session)
            });
            addseparator = true;
        }
        // add change tenant action
        if (session && session.get("availableTenantsExtendedData").length > 1) {
            menu.push({
                text: CMDBuildUI.locales.Locales.main.changetenant,
                iconCls: 'x-fa fa-list',
                reference: 'changetenant',
                itemId: 'changetenant',
                menu: this.getTenantsMenu(session)
            });
            addseparator = true;
        }
        // add separator
        if (addseparator) {
            menu.push({
                xtype: 'menuseparator'
            });
        }
        // add preferences action
        menu.push({
            text: CMDBuildUI.locales.Locales.main.userpreferences,
            iconCls: 'x-fa fa-pencil-square-o',
            reference: 'preferences',
            itemId: 'preferences'
        });
        // add change password
        menu.push({
            text: CMDBuildUI.locales.Locales.main.changepassword,
            iconCls: 'x-fa fa-key',
            reference: 'changepassword',
            itemId: 'changepassword'
        });
        // add administration module action
        menu.push({
            text: CMDBuildUI.locales.Locales.main.administrationmodule,
            iconCls: 'x-fa fa-cog',
            reference: 'administration',
            itemId: 'administration',
            hidden: true,
            bind: {
                hidden: '{!isAdministrator}'
            }
        });
        // add data management module action
        menu.push({
            text: CMDBuildUI.locales.Locales.main.managementmodule,
            iconCls: 'x-fa fa-table',
            reference: 'management',
            itemId: 'management',
            hidden: true,
            bind: {
                hidden: '{!isAdministrationModule}'
            }

        });
        // add preferences action
        menu.push({
            text: CMDBuildUI.locales.Locales.main.logout,
            iconCls: 'x-fa fa-sign-out',
            reference: 'logout',
            itemId: 'logout'
        });
        view.setMenu(menu);
    },

    /**
     * creates a menu with the available roles
     * @param {CMDBuildUI.model.users.Session} session
     */
    getGroupMenu: function (session) {
        var currentRole = session.get('role');
        var roles = session.get('availableRolesExtendedData');
        var multigroup = session.get("multigroup");

        var group = [];
        roles.forEach(function (element) {
            group.push({
                text: element.description,
                reference: element.code,
                itemId: element.code,
                disabled: multigroup || element.code === currentRole,
                iconCls: 'x-fa fa-users',
                handler: 'changeGroupClick'
            });
        });

        return group;
    },

    /**
     * 
     * @param {CMDBuildUI.model.users.Session} session 
     */
    getTenantsMenu: function (session) {
        var me = this;
        this.activetenants = Ext.Array.clone(session.get('activeTenants')).sort();
        var availabletenants = session.get('availableTenantsExtendedData');

        var menuitems = [];
        var ignoretenants = session.get("ignoreTenants");

        // add ignore tenants option
        if (session.get("canIgnoreTenants")) {
            menuitems.push({
                xtype: 'menucheckitem',
                text: CMDBuildUI.locales.Locales.main.ignoretenants,
                checked: ignoretenants,
                listeners: {
                    checkchange: function (checkitem, checked, eOpts) {
                        var text;
                        if (checked) {
                            text = CMDBuildUI.locales.Locales.main.confirmenabletenant;
                        } else {
                            text = CMDBuildUI.locales.Locales.main.confirmdisabletenant;
                        }
                        Ext.Msg.confirm(
                            CMDBuildUI.locales.Locales.notifier.attention,
                            text,
                            function (btnText) {
                                if (btnText === "yes") {
                                    session.set("ignoreTenants", checked);
                                    session.save({
                                        success: function () {
                                            window.location.reload();
                                        }
                                    });
                                }
                            }, this);
                    }
                }
            });
            // add separator
            menuitems.push({
                xtype: 'menuseparator'
            });
        }
        availabletenants.forEach(function (tenant) {
            menuitems.push({
                xtype: 'menucheckitem',
                text: tenant.description,
                checked: Ext.Array.contains(me.activetenants, tenant.code),
                startvalue: Ext.Array.contains(me.activetenants, tenant.code),
                disabled: ignoretenants,
                listeners: {
                    checkchange: function (checkitem, checked, eOpts) {
                        if (checked) {
                            Ext.Array.include(me.activetenants, tenant.code);
                        } else {
                            Ext.Array.remove(me.activetenants, tenant.code);
                        }
                    }
                }
            });
        });
        return {
            items: menuitems,
            listeners: {
                hide: 'onTenantMenuHide'
            }
        };
    },

    /**
     * @param {Ext.menu.Item} item
     * @param {Event} e
     * @param {Object} eOpts
     */
    changeGroupClick: function (item, e, eOpts) {
        var theSession = this.getViewModel().get('theSession');
        var role = item.reference;
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.main.confirmchangegroup,
            function (btnText) {
                if (btnText === "yes") {
                    theSession.set('role', role);
                    theSession.save({
                        success: function () {
                            theSession.commit();
                            window.location.reload();
                        }
                    });
                }
            }, this);
    },

    /**
     * 
     * @param {Ext.panel.Panel} panel 
     * @param {Object} eOpts 
     */
    onTenantMenuHide: function (panel, eOpts) {
        var me = this;
        var session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession();
        if (!Ext.Array.equals(session.get("activeTenants").sort(), this.activetenants.sort())) {
            Ext.Msg.confirm(
                CMDBuildUI.locales.Locales.notifier.attention,
                CMDBuildUI.locales.Locales.main.confirmchangetenants,
                function (btnText) {
                    if (btnText === "yes") {
                        CMDBuildUI.util.helper.SessionHelper.updateActiveTenants(this.activetenants);
                        session.save({
                            success: function () {
                                window.location.reload();
                            }
                        });
                    } else {
                        this.activetenants = Ext.Array.clone(session.get("activeTenants"));
                        var items = me.getView().getMenu().items.getByKey("changetenant").getMenu().getRefItems();
                        items.forEach(function (m) {
                            m.setChecked ? m.setChecked(m.startvalue) : null;
                        });
                    }
                }, this);
        }
    },

    onChangePasswordClick: function (item, e, eOpts) {

        var title = CMDBuildUI.locales.Locales.main.changepassword;
        var config = {
            xtype: 'main-header-changepassword',
            viewModel: {}
        };
        CMDBuildUI.util.Utilities.openPopup('popup-change-password', title, config, null, {
            width: '450px',
            height: '350px'
        });
    },

    /**
     * @param {Ext.menu.Item} item
     * @param {Event} e
     * @param {Object} eOpts
     */
    onPreferencesClick: function (item, e, eOpts) {
        CMDBuildUI.util.Utilities.openPopup('UserPreferences', CMDBuildUI.locales.Locales.main.userpreferences, {
            xtype: 'main-header-preferences'
        });
    }
});