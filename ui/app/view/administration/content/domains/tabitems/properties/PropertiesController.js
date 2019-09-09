Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.PropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-tabitems-properties-properties',


    require: [
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },

        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.getViewModel();
        if (view.config._rowContext && view.config._rowContext.record) {
            var viewInRow = view.down('#domain-generaldatafieldset');
            viewInRow.setTitle(null);
            viewInRow.setStyle('pading-top: 0;border-width: 0 !important;margin-bottom: 10px!important;');
            vm.linkTo('theDomain', {
                type: 'CMDBuildUI.model.domains.Domain',
                id: view.config._rowContext.record.get('_id')
            });
        } else {
            if (!vm.get('actions.add')) {
                vm.linkTo('theDomain', {
                    type: 'CMDBuildUI.model.domains.Domain',
                    id: vm.get('objectTypeName')
                });
            } else {
                vm.linkTo('theDomain', {
                    type: 'CMDBuildUI.model.domains.Domain',
                    create: true
                });
            }
        }
    },

    onEditBtnClick: function (button) {
        if (!this.getView()._rowContext) {
            this.getViewModel().getParent().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        } else {
            var view = this.getView();
            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            container.removeAll();
            var theDomain = view.getViewModel().get('theDomain');
            container.add({
                xtype: 'administration-content-domains-tabitems-properties-properties',
                viewModel: {
                    data: {
                        theDomain: theDomain,
                        title: Ext.String.format('{0} - {1}',
                            CMDBuildUI.locales.Locales.administration.localizations.domain,
                            theDomain.get('name')),
                        grid: view.config._rowContext.ownerGrid,
                        actions: {
                            view: false,
                            edit: true,
                            add: false
                        },
                        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                        objectTypeName: theDomain.get('name')
                    }
                }
            });
        }

    },

    onDeleteBtnClick: function (button) {
        var me = this;

        var vm = this.getViewModel();
        Ext.Msg.confirm(
            "Delete domain", // TODO: translate
            "Are you sure you want to delete this domain?", // TODO: translate
            function (action) {
                if (action === "yes") {
                    button.setDisabled(true);
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
                    var theDomain = vm.get('theDomain');
                    CMDBuildUI.util.Ajax.setActionId('delete-domain');

                    theDomain.erase({
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDomainsUrl();
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
            });
    },
    /**
        * @param {Ext.button.Button} button
        * @param {Event} e
        * @param {Object} eOpts
        */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var view = this.getView();
        var vm = view.getViewModel();
        var theDomain = vm.get('theDomain');
        theDomain.set('active', !theDomain.get('active'));
        Ext.apply(theDomain.data, theDomain.getAssociatedData());
        theDomain.save({
            success: function (record, operation) {
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            },
            failure: function (record, reason) {
                record.reject();
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        button.setDisabled(true);

        if (!vm.get('theDomain').isValid()) {
            var validatorResult = vm.get('theDomain').validate();
            var errors = validatorResult.items;
            for (var i = 0; i < errors.length; i++) {
                // console.log('Key :' + errors[i].field + ' , Message :' + errors[i].msg);
            }
        } else {
            var theDomain = vm.get('theDomain');
            delete theDomain.data.system;
            //
            // save the domain
            theDomain.save({
                success: function (record, operation) {
                    var nextUrl = Ext.String.format('administration/domains/{0}', record.get('_id'));
                    var cardView = me.getView().up('administration-detailswindow');
                    me.saveLocales(Ext.copy(vm), record);
                    if (cardView) {

                        vm.get('grid').setStore(Ext.create('Ext.data.Store', {
                            model: 'CMDBuildUI.model.domains.Domain',
                            alias: 'store.classdomain-store',
                            proxy: {
                                type: 'baseproxy',
                                url: Ext.String.format('/classes/{0}/domains', vm.get('grid').getViewModel().get('objectTypeName')),
                                extraParams: {
                                    ext: true,
                                    detailed: true
                                }
                            },
                            autoLoad: true,
                            autoDestroy: true
                        }).load(function () {
                            var plugin = vm.get('grid').getPlugin('administration-forminrowwidget');
                            plugin.view.fireEventArgs('itemupdated', [vm.get('grid'), record, me]);
                            button.up('#CMDBuildAdministrationDetailsWindow').fireEvent("closed");
                        }));

                    } else {
                        if (vm.get('actions.edit')) {
                            var treestore = Ext.getCmp('administrationNavigationTree').getStore();
                            var selected = treestore.findNode("href", nextUrl);
                            selected.set('text', record.get('description'));

                            if (vm.get('grid')) {
                                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                                vm.set('actions.view', true);
                                vm.set('actions.add', false);
                                vm.set('actions.edit', false);
                                var store = vm.get('grid').getStore();
                                store.load({
                                    callback: function () {
                                        var gridView = vm.get('grid').getView();
                                        gridView.refresh();

                                        var index = vm.get('grid').getStore().findExact("_id", record.getId());
                                        var storeItem = vm.get('grid').getStore().getAt(index);

                                        vm.get('grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [vm.get('grid'), storeItem, index]);
                                        vm.get('grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [vm.get('grid'), storeItem, index]);
                                        button.up('#CMDBuildAdministrationDetailsWindow').fireEvent("closed");
                                    }
                                });
                            } else {
                                me.getViewModel().getParent().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                            }
                        } else {
                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                                function () {
                                    var treeComponent = Ext.getCmp('administrationNavigationTree');
                                    var treeComponentStore = treeComponent.getStore();
                                    var selected = treeComponentStore.findNode("href", nextUrl);

                                    treeComponent.setSelection(selected);
                                });
                            me.getViewModel().getParent().set('actionManager', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                            me.redirectTo(nextUrl, true);
                        }
                    }
                },
                callback: function (record, reason) {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var detailsWindow = button.up('#CMDBuildAdministrationDetailsWindow');
        var vm;
        if (detailsWindow) {
            vm = button.lookupViewModel();
            vm.get('theDomain').reject();
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            vm.set('actions.view', true);
            vm.set('actions.edit', false);
            vm.set('actions.add', false);
            detailsWindow.fireEvent("closed");
        } else {
            if (this.getViewModel().get('actions.edit')) {
                this.redirectTo(Ext.String.format('administration/domains/{0}', this.getViewModel().get('theDomain._id')), true);
            } else if (this.getViewModel().get('actions.add')) {
                var store = Ext.getStore('administration.MenuAdministration');
                vm = Ext.getCmp('administrationNavigationTree').getViewModel();
                var currentNode = store.findNode("objecttype", CMDBuildUI.model.administration.MenuItem.types.domain);
                vm.set('selected', currentNode);
                this.redirectTo('administration/domains_empty', true);
            }
        }
    },

    privates: {
        saveLocales: function (vm, record) {
            if (vm.get('actions.add')) {
                var translations = [
                    'theDomainDescriptionTranslation',
                    'theDirectDescriptionTranslation',
                    'theInverseDescriptionTranslation',
                    'theMasterDetailTranslation'];
                var keyFunction = [
                    'getLocaleKeyOfDomainDescription',
                    'getLocaleKeyOfDomainDirectDescription',
                    'getLocaleKeyOfDomainInverseDescription',
                    'getLocaleKeyOfDomainMasterDetail'
                ];
                Ext.Array.forEach(translations, function (item, index) {
                    if (vm.get(item)) {
                        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper[keyFunction[index]](record.get('name'));
                        vm.get(item).crudState = 'U';
                        vm.get(item).crudStateWas = 'U';
                        vm.get(item).phantom = false;
                        vm.get(item).set('_id', translationCode);
                        vm.get(item).save({
                            success: function (translations, operation) {
                                CMDBuildUI.util.Logger.log(item + " localization was saved", CMDBuildUI.util.Logger.levels.debug);
                            }
                        });
                    }
                });
            }
        }
    }
});