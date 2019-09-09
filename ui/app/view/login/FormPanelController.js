Ext.define('CMDBuildUI.view.login.FormPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.login-formpanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        'textfield': {
            specialkey: 'onSpecialKey'
        },
        '#loginbtn': {
            click: 'onLoginBtnClick'
        },
        '#logoutbtn': {
            click: 'onLogoutBtnClick'
        }
    },

    onBeforeRender: function (view, eOpts) {
        var currentsession = CMDBuildUI.util.helper.SessionHelper.getCurrentSession();
        if (currentsession && currentsession.crudState !== "D") {
            var vm = view.lookupViewModel();
            // set session in viewmodel
            vm.set("theSession", currentsession);
            // hide password
            vm.set("hiddenfields.password", true);
            // add binding fo tenants field
            vm.bind({
                bindTo: '{tenants}'
            }, function () {
                view.lookupReference('activeTenantsField').setBind({
                    value: '{theSession.activeTenants}'
                });
            });
            vm.set('loggedIn', true);
        }
    },

    /**
     * @param {Ext.form.field.Text} textfield
     * @param {Event} e The click event
     */
    onSpecialKey: function (textfield, e) {
        if (e.getKey() == e.ENTER) {
            this.onLoginBtnClick(this.lookupReference('loginbtn'));
        }
    },
    /**
     * @param {Ext.button.Button} btn
     * @param {Event} e The click event
     */
    onLoginBtnClick: function (btn, e) {
        var me = this;
        var vm = this.getViewModel();
        var form = this.getView();

        if (form.isValid()) {
            btn.mask();
            vm.get("theSession").set("password", vm.get("password"));
            // set action id
            CMDBuildUI.util.Ajax.setActionId('login');
            // save session
            if (typeof vm.get('theSession').get('activeTenants') == 'string') {
                vm.get('theSession').set('activeTenants', [vm.get('theSession').get('activeTenants')]);
            }
            vm.get("theSession").save({
                failure: function (record, operation) {
                    if (record.getId() === CMDBuildUI.model.users.Session.temporary_id) {
                        if (operation.getError() && operation.getError().status == 401) {
                            CMDBuildUI.util.Notifier.showErrorMessage(CMDBuildUI.locales.Locales.errors.autherror);
                        }
                    }
                },
                success: function (record, operation) {
                    CMDBuildUI.util.helper.SessionHelper.setToken(record.getId());
                    CMDBuildUI.util.helper.SessionHelper.logging = false;

                    // add binding fo tenants field

                    form.lookupReference('activeTenantsField').setBind({
                        value: '{theSession.activeTenants}'
                    });

                    if (record.get("role") && (!record.get('availableTenants') || record.get('activeTenants'))) {
                        // show logged in messages
                        CMDBuildUI.util.Notifier.showMessage(
                            Ext.String.format(CMDBuildUI.locales.Locales.login.welcome, record.get('userDescription')), {
                                title: CMDBuildUI.locales.Locales.login.loggedin,
                                icon: 'fa-check-circle'
                            });

                        // redirect to management
                        me.redirectTo('management');
                        return;
                    }

                    vm.set('loggedIn', true);
                },
                callback: function (record, operation, success) {
                    btn.unmask();
                    // hide error message
                    vm.set('showErrorMessage', false);
                }
            });
        } else {
            vm.set('showErrorMessage', true);
        }
    },

    /**
     * @param {Ext.button.Button} btn
     * @param {Event} e The click event
     */
    onLogoutBtnClick: function (btn, e) {
        var vm = this.getViewModel();
        // set action id
        CMDBuildUI.util.Ajax.setActionId('logout');
        // erase session
        vm.getData().theSession.erase({
            success: function (record, operation) {
                // blank session token
                CMDBuildUI.util.helper.SessionHelper.setToken(null);
                CMDBuildUI.util.Utilities.redirectTo("login", true);
            }
        });
    }
});