Ext.define('CMDBuildUI.view.administration.content.users.card.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.view-administration-content-users-card-create',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#saveAndAddBtn': {
            click: 'onSaveAndAddBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.tabitems.users.card.EditController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        // set vm varibles
        if (!vm.get('theUser') || !vm.get('theUser').phantom) {
            vm.linkTo("theUser", {
                type: 'CMDBuildUI.model.users.User',
                create: true
            });
        }


    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var vm = this.getViewModel();
        var form = this.getView();

        if (form.isValid()) {
            var theUser = vm.getData().theUser;
            theUser.getProxy().setUrl('/users');
            delete theUser.data.inherited;
            delete theUser.data.writable;
            delete theUser.data.hidden;
            Ext.Array.forEach(theUser.get('userGroups'), function (element, index) {
                if (theUser.data.userGroups[index] && theUser.data.userGroups[index].isModel) {
                    theUser.data.userGroups[index] = theUser.data.userGroups[index].getData();
                }
                delete theUser.data.userGroups[index].id;
            });
            Ext.Array.forEach(theUser.get('userTenants'), function (element, index) {
                if (theUser.data.userGroups[index] && theUser.data.userGroups[index].isModel) {
                    theUser.data.userGroups[index] = theUser.data.userGroups[index].getData();
                }
                delete theUser.data.userTenants[index].id;
            });
            theUser.save({
                success: function (record, operation) {
                    Ext.GlobalEvents.fireEventArgs("usercreated", [record]);
                    var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
                    container.fireEvent('closed');
                }
            });
        } else {
            var w = Ext.create('Ext.window.Toast', {
                ui: 'administration',
                html: CMDBuildUI.locales.Locales.administration.common.messages.correctformerrors,
                title: CMDBuildUI.locales.Locales.administration.common.messages.error,
                iconCls: 'x-fa fa-check-circle',
                align: 'br'
            });
            w.show();
        }
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndAddBtnClick: function (button, e, eOpts) {
        this.onSaveBtnClick();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-users-card-create',
            viewModel: {
                links: {
                    theUser: {
                        type: 'CMDBuildUI.model.users.User',
                        create: true
                    }
                }
            }
        });
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getViewModel().get("theUser").reject();
        this.getView().up().fireEvent("closed");
    },



    onTenantStoreLoad: function (store, items, operation, success) {
        var vm = this.getViewModel();
        var theUser = vm.get('theUser');
        var tenantsData = [];
        if (!theUser.get('userTenants')) {
            theUser.set('userTenants', []);
        }
        
        items.forEach(function (tenant) {

            var exist = theUser.get('userTenants').find(function (userTenant) {
                return userTenant._id === tenant.get('_id');
            });

            var _tenant = {
                description: tenant.get('description'),
                _id: tenant.get('_id'),
                name: tenant.get('name') || tenant.get('code'),
                active: (exist) ? true : false
            };
            tenantsData.push(_tenant);
        });
        vm.set('tenantsData', tenantsData);
        vm.set('theUsers.userTenants', tenantsData);
    }
});