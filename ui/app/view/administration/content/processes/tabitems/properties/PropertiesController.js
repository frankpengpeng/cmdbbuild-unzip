Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.PropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabitems-properties-properties',


    require: [
        'CMDBuildUI.util.Utilities',
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.up('#CMDBuildAdministrationContentProcessView').getViewModel();
        var versionBtn = view.down('#versionBtn');
        var objectTypeName = vm.get('objectTypeName');


        if (objectTypeName && versionBtn) {
            Ext.create('Ext.data.Store', {
                model: 'CMDBuildUI.model.processes.ProcessVersion',
                proxy: {
                    type: 'baseproxy',
                    url: '/processes/' + objectTypeName + '/versions',
                    reader: {
                        type: 'json'
                    }
                },
                autoLoad: true,
                autoDestroy: true,
                sorters: [{
                    // Sort by version, in descending order
                    sorterFn: function (record1, record2) {
                        var version1 = record1.get('version'),
                            version2 = record2.get('version');
                        return version1 > version2 ? 1 : (version1 === version2) ? 0 : -1;
                    },
                    direction: 'DESC'
                }],
                listeners: {
                    load: function (store, records, success, operation, opts) {
                        var items = [];
                        Ext.Array.forEach(records, function (record) {
                            var item = {
                                text: record.get('version'),
                                planId: record.get('planId'),
                                listeners: {
                                    click: 'onVersionMenuItemClick'
                                },
                                cls: 'menu-item-nospace',
                                autoEl: {
                                    'data-testid': 'administration-processes-properties-tool-version-' + record.get('version')
                                }
                            };

                            items.push(item);
                            versionBtn.menu.add(item);
                        });

                        var template = {
                            text: CMDBuildUI.locales.Locales.administration.processes.strings.template,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.processes.strings.template'
                            },
                            planId: 'template',
                            listeners: {
                                click: 'onTemplateMenuItemClick'
                            },
                            cls: 'menu-item-nospace',
                            autoEl: {
                                'data-testid': 'administration-processes-properties-tool-version-template'
                            }
                        };

                        if (versionBtn.menu) {
                            versionBtn.menu.add(template);
                        }
                    }
                }
            });

        }
    },

    onEditBtnClick: function (button) {

        this.getView().up('administration-content-processes-view').getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    onDeleteBtnClick: function (button) {
        var me = this,
            vm = this.getViewModel();

        Ext.Msg.confirm(
            "Delete process", // TODO: translate
            "Are you sure you want to delete this process?", // TODO: translate
            function (action) {
                if (action === 'yes') {
                    var theProcess = vm.get('theProcess');
                    var tmpGetAssociated = theProcess.getAssociatedData;
                    theProcess.getAssociatedData = function () {
                        return false;
                    };

                    CMDBuildUI.util.Ajax.setActionId('delete-process');

                    theProcess.erase({
                        error: function () {
                            theProcess.getAssociatedData = tmpGetAssociated;
                        },
                        success: function (record, operation) {
                            var nextUrl = 'administration/processes_empty';
                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                                function () {
                                    var treestore = Ext.getCmp('administrationNavigationTree');
                                    var selected = treestore.getStore().findNode("href", nextUrl);
                                    treestore.setSelection(selected);
                                });

                            me.redirectTo(nextUrl, true);
                        }
                    });
                }
            }
        );
    },
    onTemplateMenuItemClick: function (menuItem) {
        var url,
            objectTypeName = this.getView().up('#CMDBuildAdministrationContentProcessView').getViewModel().get('objectTypeName');

        url = CMDBuildUI.util.api.Processes.getTemplateFileUrl(objectTypeName);
        window.open(url, '_blank');

    },

    onVersionMenuItemClick: function (menuItem) {
        var url,
            objectTypeName = this.getView().up('#CMDBuildAdministrationContentProcessView').getViewModel().get('objectTypeName');

        url = CMDBuildUI.util.api.Processes.getVersionFileUrl(objectTypeName, menuItem.planId);
        window.open(url, '_blank');
    },

    onDisableBtnClick: function () {
        var vm = this.getViewModel();
        var theProcess = vm.get('theProcess');
        Ext.apply(theProcess.data, theProcess.getAssociatedData());
        theProcess.set('active', false);
        theProcess.save({
            success: function (record, operation) {
                var w = Ext.create('Ext.window.Toast', {
                    title: CMDBuildUI.locales.Locales.administration.common.messages.success,
                    html: CMDBuildUI.locales.Locales.administration.processes.texts.processdeactivated,
                    iconCls: 'x-fa fa-check-circle',
                    align: 'br'
                });
                w.show();
                vm.getParent().linkTo('theProcess', {
                    type: 'CMDBuildUI.model.processes.Process',
                    id: vm.get('objectTypeName')
                });
                theProcess.commit();
            }
        });
    },

    onEnableBtnClick: function () {
        var vm = this.getViewModel();
        var theProcess = vm.get('theProcess');
        Ext.apply(theProcess.data, theProcess.getAssociatedData());
        theProcess.set('active', true);
        theProcess.save({
            success: function (record, operation) {
                var w = Ext.create('Ext.window.Toast', {
                    title: CMDBuildUI.locales.Locales.administration.common.messages.success,
                    html: CMDBuildUI.locales.Locales.administration.processes.texts.processactivated,
                    iconCls: 'x-fa fa-check-circle',
                    align: 'br'
                });
                w.show();
                vm.getParent().linkTo('theProcess', {
                    type: 'CMDBuildUI.model.processes.Process',
                    id: vm.get('objectTypeName')
                });
                theProcess.commit();
            }
        });
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
        var vm = this.getViewModel();
        if (!vm.get('theProcess').isValid()) {
            var validatorResult = vm.get('theProcess').validate();
            var errors = validatorResult.items;
            for (var i = 0; i < errors.length; i++) {
                // console.log('Key :' + errors[i].field + ' , Message :' + errors[i].msg);
            }
        } else {
            var theProcess = vm.get('theProcess');
            delete theProcess.data.system;
            Ext.apply(theProcess.data, theProcess.getAssociatedData());

            vm.get('attributeGroupsStore').getData().items.forEach(function (record, index) {
                theProcess.data.attributeGroups.push(record.getData());
            });

            theProcess.save({
                success: function (record, operation) {
                    var objectTypeName = record.getId();
                    me.uploadXpdlAndIcon(
                        vm,
                        function () {
                            vm.getView().up('administration-content-processes-view').getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                            if (vm.get('theTranslation')) {
                                var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfProcessDescription(record.get('name'));
                                vm.get('theTranslation').crudState = 'U';
                                vm.get('theTranslation').crudStateWas = 'U';
                                vm.get('theTranslation').phantom = false;
                                vm.get('theTranslation').set('_id', translationCode);
                                vm.get('theTranslation').save({
                                    success: function (translations, operation) {
                                        me.reloadProcessStoreAfterSave(record);
                                        button.setDisabled(false);
                                        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
                                    }
                                });
                            } else {
                                me.reloadProcessStoreAfterSave(record);
                                button.setDisabled(false);
                                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
                            }
                        },
                        function () {
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                        }
                    );
                },
                failure: function (reason) {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
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
        if (this.getViewModel().get('actions.edit')) {
            this.redirectTo(Ext.String.format('administration/processes/{0}', this.getViewModel().get('theProcess._id')), true);
        } else if (this.getViewModel().get('actions.add')) {
            var store = Ext.getStore('administration.MenuAdministration');
            var vm = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode("objecttype", CMDBuildUI.model.administration.MenuItem.types.process);
            vm.set('selected', currentNode);
            this.redirectTo('administration/processes_empty', true);
        }
    },

    /**
     * On translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theProcess = vm.get('theProcess');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfProcessDescription(vm.get('actions.edit') ? theProcess.get('name') : '.');
        var popup = CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, CMDBuildUI.util.administration.helper.FormHelper.formActions.edit, 'theTranslation', vm);
        popup.setPagePosition(event.getX() - 450, event.getY() - 100);
    },

    privates: {
        /**
         * @private
         * @param {CMDBuildUI.view.administration.content.processes.ViewModel} vm 
         * @param {Function} successCb 
         * @param {Function} errorCb 
         */
        uploadXpdlAndIcon: function (vm, successCb, errorCb) {
            CMDBuildUI.util.Ajax.setActionId('process.xpdl.upload');
            // define method
            var me = this,
                method = "POST",
                input = me.lookupReference('xpdlFile').extractFileInput();
            if (!input.files.length) {

                me.uploadIcon(vm, successCb, errorCb);
                if (Ext.isFunction(successCb)) {
                    Ext.callback(successCb, null, []);
                }
            } else {
                // init formData
                var formData = new FormData();
                // get url
                var url = Ext.String.format('{0}/processes/{1}/versions', CMDBuildUI.util.Config.baseUrl, vm.get('theProcess._id'));
                // upload 
                CMDBuildUI.util.administration.File.upload(method, formData, input, url, {
                    success: function (response) {

                        if (Ext.isFunction(successCb)) {
                            Ext.callback(successCb, null, [response]);
                        }
                    },
                    failure: function (error) {

                        CMDBuildUI.util.Notifier.showErrorMessage();
                        if (Ext.isFunction(errorCb)) {
                            Ext.callback(errorCb, null, [error]);
                        }
                    }
                });
            }
        },
        /**
         * @private
         * @param {CMDBuildUI.view.administration.content.processes.ViewModel} vm 
         * @param {Function} successCb 
         * @param {Function} errorCb 
         */
        uploadIcon: function (vm, successCb, errorCb) {
            CMDBuildUI.util.Ajax.setActionId('process.icon.upload');
            // define method
            var method = "POST";
            var input = this.lookupReference('iconFile').extractFileInput();

            if (!input.files.length) {
                if (Ext.isFunction(successCb)) {
                    Ext.callback(successCb, null, []);
                }
            } else {
                // init formData
                var formData = new FormData();
                // get url
                var url = Ext.String.format('{0}/uploads?overwrite_existing=true&path=images/classicons/{1}.png', CMDBuildUI.util.Config.baseUrl, vm.get('theProcess._id'));
                // upload 
                CMDBuildUI.util.administration.File.upload(method, formData, input, url, {
                    success: function (response) {
                        if (response && response.data) {
                            vm.get('theProcess').set('_icon', response.data._id);
                            Ext.apply(vm.get('theProcess').data, vm.get('theProcess').getAssociatedData());

                            vm.get('theProcess').save({
                                success: function (record, operation) {
                                    if (Ext.isFunction(successCb)) {
                                        Ext.callback(successCb, null, [response]);
                                    }
                                }
                            });
                        }
                    },
                    failure: function (error) {
                        if (Ext.isFunction(errorCb)) {
                            Ext.callback(errorCb, null, [error]);
                        }
                    }
                });
            }
        },
        reloadProcessStoreAfterSave: function (record) {
            var me = this;
            var vm = this.getViewModel();

            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(record.get('name'));
            if (vm.get('action') === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
                CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                    function () {
                        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                    });
            } else {
                CMDBuildUI.util.Stores.loadProcessesStore().then(function () {
                    CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, record.get('description'), me);
                });
            }
        }
    }

});