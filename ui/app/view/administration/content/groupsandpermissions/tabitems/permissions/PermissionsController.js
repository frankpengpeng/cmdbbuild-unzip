Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsController', {

    requires: [
        'CMDBuildUI.util.administration.helper.TabPanelHelper'
    ],

    mixins: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsMixin'
    ],

    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-permissions-permissions',
    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChage'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.groupsandpermissions.TabPanel} view
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();

        var currentSubTabIndex = this.getView().up('administration-content').getViewModel().get('activeTabs.permissions') || 0;
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(view, "classes", CMDBuildUI.locales.Locales.administration.navigation.classes, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-classes-classes',
            autoScroll: true
        }], 0, {}, {
                objectType: CMDBuildUI.model.menu.MenuItem.types.klass
            });

        tabPanelHelper.addTab(view, "processes", CMDBuildUI.locales.Locales.administration.navigation.processes, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-processes-processes'
        }], 1, {}, {
                objectType: 'processclass'
            });

        tabPanelHelper.addTab(view, "views", CMDBuildUI.locales.Locales.administration.navigation.views, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-views-views'
        }], 2, {}, {
                objectType: CMDBuildUI.model.menu.MenuItem.types.view
            });

        tabPanelHelper.addTab(view, "searchFilters", CMDBuildUI.locales.Locales.administration.navigation.searchfilters, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-filters-filters'
        }], 3, {}, {
                objectType: CMDBuildUI.model.menu.MenuItem.types.searchfilter
            });

        tabPanelHelper.addTab(view, "dashboards", CMDBuildUI.locales.Locales.administration.navigation.dashboards, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-dashboards-dashboards'
        }], 4, {}, {
                objectType: CMDBuildUI.model.menu.MenuItem.types.dashboard
            });

        tabPanelHelper.addTab(view, "reports", CMDBuildUI.locales.Locales.administration.navigation.reports, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-reports-reports'
        }], 5, {}, {
                objectType: CMDBuildUI.model.menu.MenuItem.types.report
            });

        tabPanelHelper.addTab(view, "custompages", CMDBuildUI.locales.Locales.administration.navigation.custompages, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-custompages-custompages'
        }], 6, {}, {
                objectType: CMDBuildUI.model.menu.MenuItem.types.custompage
            });

        tabPanelHelper.addTab(view, "ietemplate", CMDBuildUI.locales.Locales.administration.navigation.importexports, [{
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-importexports-importexports'
        }], 7, {}, {
                objectType: CMDBuildUI.model.administration.MenuItem.types.importexport
            });

        vm.set("activeTab", currentSubTabIndex);
        view.setActiveTab(currentSubTabIndex);

    },

    /**
     * @param {CMDBuildUI.view.administration.content.groupsandpermissions.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        vm.set('objectType', newtab.reference);
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.permissions', this, view, newtab, oldtab, eOpts);


        var grantsStore = Ext.getStore('groups.Grants');
        var proxyUrl = Ext.String.format('/roles/{0}/grants', vm.get('theGroup._id'));
        var chainedStore = vm.getStore('grantsChainedStore');
        me.setCopyButton(newtab, chainedStore);

        grantsStore.getProxy().setUrl(proxyUrl);
        grantsStore.load();
        chainedStore.clearFilter();
        chainedStore.addFilter([function (rec) {
            switch (newtab.config.objectType) {
                case 'class':
                    return rec.get('objectType') === newtab.config.objectType && !rec.get('_is_process');
                case 'processclass':
                    return rec.get('objectType') === 'class' && rec.get('_is_process');
                default:
                    return rec.get('objectType') === newtab.config.objectType;
            }

        }]);
        chainedStore.config = {
            relatedStore: newtab.config.relatedStore,
            objectType: newtab.config.objectType,
            roleId: vm.get('theGroup._id')
        };

        newtab.down('grid').setStore(chainedStore);


    },
    setCopyButton: function (view, currentGrantsStore) {
        var me = this;
        var copyFromButton = Ext.ComponentQuery.query('#' + view.id + ' #copyFrom')[0];
        copyFromButton.menu.removeAll();

        Ext.getStore('groups.Groups').load({
            callback: function (items) {
                Ext.Array.forEach(items, function (element, index) {
                    if (element.get('active') && !element.get('_rp_data_all_write')) {
                        copyFromButton.menu.add({
                            text: element.get('description'),
                            iconCls: 'x-fa fa-users',
                            listeners: {
                                click: function () {
                                    me.cloneFrom(element, view, currentGrantsStore);
                                }
                            }
                        });
                    }
                });
            }
        });
    },
    cloneFrom: function (group, view, currentGrantsStore) {

        var grantsStore = Ext.create('Ext.data.Store', {
            extend: 'CMDBuildUI.store.Base',
            requires: [
                'CMDBuildUI.store.Base',
                'CMDBuildUI.model.users.Grant'
            ],
            model: 'CMDBuildUI.model.users.Grant',
            pageSize: 0,
            autoLoad: false,
            autoDestroy: true
        });

        var proxyUrl = Ext.String.format('/roles/{0}/grants', group.get('_id'));
        grantsStore.getProxy().type = 'baseproxy';
        grantsStore.getModel().getProxy().setUrl(proxyUrl);

        grantsStore.load({
            callback: function (items) {
                var grantsToRemove = currentGrantsStore.query('objectType', view.config.objectType);
                currentGrantsStore.remove(grantsToRemove.items);
                currentGrantsStore.add(items.filter(function (item, index) {
                    return item.get('objectType') === view.config.objectType;
                }));
            }
        });

    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel('administration-content-groupsandpermissions-view');
        vm.set('actions.view', false);
        vm.set('actions.edit', true);
        vm.set('actions.add', false);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var me = this;
        var view = this.getView().down('grid');
        var vm = this.getViewModel();
        var store = Ext.getStore('groups.Grants');
        var data = store.getData().items;
        var jsonData = [];

        view.getView().mask();

        Ext.Array.forEach(data, function (element) {
            if (element.get('mode') !== 'n') {
                jsonData.push(element.getData());
            }
        });

        var grid = button.up('administration-content-groupsandpermissions-tabitems-permissions-permissions').down('grid');

        Ext.Ajax.request({
            url: Ext.String.format(
                '{0}/roles/{1}/grants/_ANY',
                CMDBuildUI.util.Config.baseUrl,
                vm.get('theGroup._id')
            ),
            method: 'PUT',
            jsonData: jsonData,

            callback: function () {
                store.load();
                view.getView().unmask();
                me.toggleEnablePermissionsTabs();
                me.toggleEnableTabs();
                var columns = grid.getColumnManager().getColumns();
                Ext.Array.forEach(columns, function (item) {
                    if (item.xtype === 'checkcolumn') {
                        item.setHeaderCheckbox(false);
                        item.sortable = true;
                    }
                });
                
                vm.set('actions.view', true);
                vm.set('actions.edit', false);
                vm.set('actions.add', false);
                button.setDisabled(false);
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = this.getView().getViewModel();        
        me.getView().down('grid').getStore().source.load({
            callback: function () {
                vm.set('actions.view', true);
                vm.set('actions.edit', false);
                vm.set('actions.add', false);
                me.toggleEnablePermissionsTabs();
                me.toggleEnableTabs();                
                me.getView().activeTab.down('grid').getView().refresh();
            }
        });
    }
});