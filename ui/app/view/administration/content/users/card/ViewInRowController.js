Ext.define('CMDBuildUI.view.administration.content.users.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    requires: ['CMDBuildUI.util.administration.helper.ConfigHelper'],
    alias: 'controller.administration-content-users-card-viewinrow',
    listen: {
        global: {
            selecteduser: 'onUserUpdated',
            userupdated: 'onUserUpdated'
        }
    },
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            itemupdated: 'onUserUpdated',
            tabchange: 'onTabChage',
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        }
    },

    onAfterRender: function (view) {
        CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(function (config) {

            var multitenantConfig = config.filter(function (item) {
                return 'org__DOT__cmdbuild__DOT__multitenant__DOT__mode' === item._key;
            })[0];

            var isMultitenantActive = multitenantConfig.hasValue && multitenantConfig.value !== 'DISABLED';
            if (!isMultitenantActive) {
                view.child('#tenants').tab.hide();
            } else {
                view.child('#tenants').tab.show();
            }

        });
    },
    /**
     * @param {CMDBuildUI.view.administration.content.classes.tabitems.users.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        this.linkUser(view, vm);
    },

    onUserUpdated: function (v, record) {
        new Ext.util.DelayedTask(function () { }).delay(
            150,
            function (v, record) {
                var vm = this.getViewModel();
                var view = this.getView();
                this.linkUser(view, vm);
            },
            this,
            arguments);
    },

    onEditBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        container.add({
            xtype: 'administration-content-users-card-edit',
            viewModel: {
                data: {
                    theUser: view.getViewModel().get('selected'),
                    title: CMDBuildUI.locales.Locales.administration.navigation.users + ' - ' + view.getViewModel().get('theUser').get('name'),
                    grid: this.getView().up()
                }
            }
        });
    },

    onDeleteBtnClick: function () {
        var me = this;
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-user');
                    me.getViewModel().getData().theUser.erase({
                        success: function (record, operation) {
                            Ext.ComponentQuery.query('administration-content-users-grid')[0].fireEventArgs('reload', [record, 'delete']);
                        }
                    });
                }
            }, this);
    },


    onViewBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-users-card-view',

            viewModel: {
                data: {
                    theUser: view.getViewModel().get('selected'),
                    title: CMDBuildUI.locales.Locales.administration.navigation.users + ' - ' + view.getViewModel().get('theUser').get('username'),
                    grid: this.getView().up()
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onActiveToggleBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theUser = vm.get('theUser');
        theUser.set('active', !theUser.get('active'));
        // TODO: is best if use proxy writer
        Ext.Array.forEach(theUser.get('userGroups'), function (element, index) {
            if (theUser.data.userGroups[index].isModel) {
                theUser.data.userGroups[index] = theUser.data.userGroups[index].getData();
            }
            delete theUser.data.userGroups[index].id;
        });
        Ext.Array.forEach(theUser.get('userTenants'), function (element, index) {
            if (theUser.data.userGroups[index].isModel) {
                theUser.data.userGroups[index] = theUser.data.userGroups[index].getData();
            }
            delete theUser.data.userTenants[index].id;
        });
        theUser.save({
            success: function (record, operation) {
                // view.getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, storeItem, index]);
                view.up('administration-content-users-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [view.up('administration-content-users-grid'), record, this]);

            }
        });

    },

    onCloneBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var newUser = vm.get('theUser').clone();

        container.removeAll();
        container.add({
            xtype: 'administration-content-users-card-create',

            viewModel: {
                data: {
                    theUser: newUser,
                    title: CMDBuildUI.locales.Locales.administration.navigation.users + ' - ' + vm.get('theUser').get('username'),
                    grid: this.getView().up()
                }
            }
        });
    },

    linkUser: function (view, vm) {
        if(view){
            var grid = view.up(),
            record = grid.getSelection()[0];

        vm.set("theUser", record);
        }
        
    },

    /**
     * 
     * @param {Ext.tab.Panel} tabPanel 
     * @param {Ext.Component} newCard 
     * @param {Ext.Component} oldCard 
     * @param {Object} eOpts 
     */
    onTabChage: function (tabPanel, newCard, oldCard, eOpts) {
        // if(newCard.reference === 'groupsTab'){
        //     var dlength = newCard.up('administration-content-users-grid').getViewModel().get('selected.userGroups').length;
        //     var ml = newCard.down().down().items.items[2].getHeight() + 40 + (dlength * 20);
        //     newCard.down().down().items.items[2].setHeight(ml);
        //     tabPanel.updateLayout();

        // }
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