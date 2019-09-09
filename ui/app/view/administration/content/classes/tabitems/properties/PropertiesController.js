Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.PropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabitems-properties-properties',

    require: [
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],

    control: {
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {        
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        var me = this;
        var vm = me.getViewModel();

        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.classes.strings.deleteclass, // Delete class
            CMDBuildUI.locales.Locales.administration.classes.strings.deleteclassquest, // Are you sure you want to delete this class?
            function (action) {
                if (action === "yes") {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
                    var theObject = vm.get('theObject');
                    var tmpGetAssociated = theObject.getAssociatedData;
                    theObject.getAssociatedData = function () {
                        return false;
                    };

                    CMDBuildUI.util.Ajax.setActionId('delete-class');
                    theObject.erase({
                        error: function (error) {
                            theObject.getAssociatedData = tmpGetAssociated;
                        },
                        success: function (record, operation) {
                            var response = operation.getResponse();
                            var w = Ext.create('Ext.window.Toast', {
                                // Ex: "INFO: drop cascades to table "AssetTmpl_history""
                                html: Ext.JSON.decode(response.responseText).message || 'Class successfully deleted.', // TODO: translate
                                title: CMDBuildUI.locales.Locales.administration.common.messages.success,
                                localized:{
                                    title: 'CMDBuildUI.locales.Locales.administration.common.messages.success'
                                },
                                iconCls: 'x-fa fa-check-circle"',
                                align: 'br',
                                autoClose: true,
                                maxWidth: 300,
                                monitorResize: true,
                                closable: true
                            });
                            w.show();
                            var nextUrl = 'administration/classes_empty';
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
            }, this
        );
    },

    /**
     * @param {Ext.menu.Item} menuItem
     * @param {Event} e
     * @param {Object} eOpts
     */
    onPrintMenuItemClick: function (menuItem) {
        var url,
            objectTypeName = this.getView().lookupViewModel().get('theObject').get('name');
        switch (menuItem.fileType) {
            case 'PDF':
                url = CMDBuildUI.util.api.Classes.getClassReport('PDF', objectTypeName);
                break;
            case 'ODT':
                url = CMDBuildUI.util.api.Classes.getClassReport('ODT', objectTypeName);
                break;
            default:
                Ext.Msg.alert('Warning', 'File type of report not implemented!');
        }
        window.open(url, '_blank');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDisableBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var vm = this.getViewModel();
        var theObject = vm.get('theObject');
        Ext.apply(theObject.data, theObject.getAssociatedData());
        theObject.set('active', false);
        theObject.save({
            success: function (record, operation) {
                vm.getParent().linkTo('theObject', {
                    type: 'CMDBuildUI.model.classes.Class',
                    id: vm.get('objectTypeName')
                });
                theObject.commit();
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            },
            callback: function (record, reason) {
                if (button.el.dom) {
                    button.setDisabled(false);
                }
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEnableBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var vm = this.getViewModel();
        var theObject = vm.get('theObject');
        Ext.apply(theObject.data, theObject.getAssociatedData());
        theObject.set('active', true);
        theObject.save({
            success: function (record, operation) {
                var w = Ext.create('Ext.window.Toast', {
                    title: CMDBuildUI.locales.Locales.administration.common.messages.success, // Success
                    html: CMDBuildUI.locales.Locales.administration.classes.strings.classactivated, // Class activated correctly.
                    iconCls: 'x-fa fa-check-circle',
                    align: 'br'
                });
                w.show();
                vm.getParent().linkTo('theObject', {
                    type: 'CMDBuildUI.model.classes.Class',
                    id: vm.get('objectTypeName')
                });
                theObject.commit();
                button.setDisabled(false);
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            },
            callback: function (record, reason) {
                if (button.el.dom) {
                    button.setDisabled(false);
                }
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
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var me = this;
        button.setDisabled(true);
        var vm = this.getViewModel();
        if (vm.get('theObject').isValid()) {

            var theObject = vm.get('theObject');
            delete theObject.data.system;
            Ext.apply(theObject.data, theObject.getAssociatedData());
            // delete all id / _id in associatedData
            theObject.data.formTriggers = [];

            vm.get('formTriggersStore').getData().items.forEach(function (record, index) {
                theObject.data.formTriggers.push(record.getData());
            });

            vm.get('attributeGroupsStore').getData().items.forEach(function (record, index) {
                theObject.data.attributeGroups.push(record.getData());
            });

            theObject.data.contextMenuItems.forEach(function (record, index) {
                delete theObject.data.contextMenuItems[index].id;
                delete theObject.data.contextMenuItems[index]._id;
            });
            theObject.data.widgets.forEach(function (record, index) {
                delete theObject.data.widgets[index].id;
                delete theObject.data.widgets[index]._id;
            });
            // upload the icon             
            me.uploadIcon(vm, theObject, button).then(
                function (theObject) {
                    // save the class
                    theObject.save({
                        success: function (record, operation) {
                            var key = Ext.String.format('class.{0}.description', record.get('name'));
                            me.saveTranslation(key, vm).then(
                                function(){
                                    me.reloadClassesStoreAfterSave(record, button);
                                }
                            );
                        }
                    });
                },
                function (error) {
                    CMDBuildUI.util.Logger.log("Upload icon error...", CMDBuildUI.util.Logger.levels.error);
                    CMDBuildUI.util.Logger.log(error, CMDBuildUI.util.Logger.levels.error);
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
            this.redirectTo(Ext.String.format('administration/classes/{0}', this.getViewModel().get('theObject._id')), true);
        } else if (this.getViewModel().get('actions.add')) {
            var store = Ext.getStore('administration.MenuAdministration');
            var vm = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode("objecttype", CMDBuildUI.model.administration.MenuItem.types.klass);
            vm.set('selected', currentNode);
            this.redirectTo('administration/classes_empty', true);
        }
    },

    /**
     * On translate button click (button, e, eOpts) {
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onTranslateClick: function (button, e, eOpts) {
        var me = this;
        var vm = me.getViewModel();        
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassDescription(vm.get('theObject').get('description'));
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theTranslation', vm);
    },

    privates: {
        reloadClassesStoreAfterSave: function (record, button) {
            
            var me = this;
            var vm = this.getViewModel();
            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getClassUrl(record.get('name'));
            if (vm.get('action') === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                    function () {
                        if (button.el.dom) {
                            button.setDisabled(false);
                        }
                        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
                        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                    });
            } else {
                CMDBuildUI.util.Stores.loadClassesStore().then(function () {
                    vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, record.get('description'), me);
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
                });
            }
        },
        /**
         * @private
         * @param {CMDBuildUI.view.administration.content.processes.ViewModel} vm 
         * @param {Function} successCb 
         * @param {Function} errorCb 
         */
        uploadIcon: function (vm, record, button) {
            var deferred = new Ext.Deferred();            
            CMDBuildUI.util.Ajax.setActionId('class.icon.upload');
            // define method
            var method = "POST";
            var input = this.lookupReference('iconFile').extractFileInput();

            if (!input.files.length) {
                deferred.resolve(record);
            } else {
                // init formData
                var formData = new FormData();
                // get url
                var url = Ext.String.format('{0}/uploads?overwrite_existing=true&path=images/classicons/{1}.png', CMDBuildUI.util.Config.baseUrl, vm.get('theObject.name'));
                // upload 
                CMDBuildUI.util.administration.File.upload(method, formData, input, url, {
                    success: function (response) {
                        if (response && response.data) {
                            record.set('_icon', response.data._id);
                            deferred.resolve(record);
                        }
                    },
                    failure: function (error) {
                        deferred.reject(error);
                    }
                });
            }
            return deferred.promise;
        },

        saveTranslation: function(key, vm){
            var deferred = new Ext.Deferred();            
            if (vm.get('actions.add')) {
                // save the translation 
                if (vm.get('theTranslation')) {
                    vm.get('theTranslation').crudState = 'U';
                    vm.get('theTranslation').crudStateWas = 'U';
                    vm.get('theTranslation').phantom = false;
                    vm.get('theTranslation').set('_id', key);
                    vm.get('theTranslation').save({
                        success: function (translations, operation) {
                            deferred.resolve();
                        }
                    });
                } else {
                    deferred.resolve();
                }
            }else{
                deferred.resolve();
            }
            return deferred.promise;
        }
    }

});