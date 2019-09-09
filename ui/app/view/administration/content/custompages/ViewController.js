Ext.define('CMDBuildUI.view.administration.content.custompages.ViewController', {
    extend: 'Ext.app.ViewController',
    requires: ['CMDBuildUI.util.administration.File'],
    alias: 'controller.administration-content-custompages-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
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
        },
        '#downloadBtn': {
            click: 'onDownloadBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },


    /**
     * Before render
     * @param {CMDBuildUI.view.administration.content.custompages.View} view
     */
    onBeforeRender: function (view) {
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.custompages.plural);
        this.setFilefieldProperties();
    },

    /**
     * On add custompage button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, e, eOpts) {
        this.redirectTo('administration/custompages_empty/true');
        this.getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edd);
    },

    /**
     * On delete custompage button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this;
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    button.setDisabled(true);
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
                    CMDBuildUI.util.Ajax.setActionId('delete-custompage');
                    me.getViewModel().get('theCustompage').erase({
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomPageUrl();
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
    },

    /**
     * On download custompage button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDownloadBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        var url = Ext.String.format('{0}/custompages/{1}/file?extension=zip', CMDBuildUI.util.Config.baseUrl, vm.get('theCustompage._id'));
        window.open(url, '_blank');
    },

    /**
     * On edit custompage button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.setFilefieldProperties();
    },

    /**
     * On cancel button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        if (vm.get('actions.add')) {

            this.redirectTo('administration/custompages_empty', true);
            var store = Ext.getStore('administration.MenuAdministration');
            var vmNavigation = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode("objecttype", CMDBuildUI.model.administration.MenuItem.types.custompage);
            vmNavigation.set('selected', currentNode);
        } else {
            vm.get('theCustompage').reject();
            vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        }
    },

    // /**
    //  * On disable custompage button click {
    //  * @param {Ext.button.Button} button
    //  * @param {Event} e
    //  * @param {Object} eOpts
    //  */
    // onDisableCustompageBtnClick: function (button, e, eOpts) {
    //     var vm = this.getView().getViewModel();
    //     vm.set('theCustompage.active', false);
    //     this.save(vm);
    // },

    // /**
    //  * On enable custompage button click
    //  * @param {Ext.button.Button} button
    //  * @param {Event} e
    //  * @param {Object} eOpts
    //  */
    // onEnableCustompageBtnClick: function (button, e, eOpts) {
    //     var vm = this.getView().getViewModel();
    //     vm.set('theCustompage.active', true);
    //     this.save(vm);
    // },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theCustompage = vm.get('theCustompage');
        theCustompage.set('active', !theCustompage.get('active'));

        theCustompage.save({
            success: function (record, operation) {

            },
            failure: function (record, reason) {
                record.reject();
            }
        });

    },

    /**
     * On save button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var me = this;
        var form = this.getView().getForm();
        var vm = this.getView().getViewModel();

        if (form.isValid()) {

            var afterSave = function (record) {
                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomPageUrl(record.get('_id'));
                if (vm.get('actions.edit')) {
                    var newDescription = record.get('description');
                    CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, newDescription, me);
                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                } else {
                    CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                        function () {
                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                            if (button.el && button.el.dom) {
                                button.setDisabled(false);
                            }
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                        });
                }
            };

            me.save(vm,
                function (res) {
                    var record = res;
                    if (!record.isModel) {
                        record = CMDBuildUI.model.custompages.CustomPage.create(record.data);
                    }
                    if (vm.get('theTranslation')) {
                        var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfCustomPageDescription(record.get('_id'));
                        vm.get('theTranslation').crudState = 'U';
                        vm.get('theTranslation').crudStateWas = 'U';
                        vm.get('theTranslation').phantom = false;
                        vm.get('theTranslation').set('_id', key);
                        vm.get('theTranslation').save({
                            success: function (translation, operation) {
                                afterSave(record);
                            }
                        });
                    } else {
                        afterSave(record);
                    }
                },
                function (reason) {
                    button.setDisabled(false);
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                }
            );
        } else {
            button.setDisabled(false);
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    /**
     * On translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (event, button, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfCustomPageDescription(!isNaN(vm.get('theCustompage').get('_id')) ? vm.get('theCustompage').get('_id') : '..');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theTranslation', vm);
    },

    /**
     * privates
     */
    privates: {
        /**
         * @private
         */
        setFilefieldProperties: function () {
            var view = this.getView();
            var vm = this.getViewModel();
            if (vm.get('actions.edit')) {
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(view.down('[name="fileCustompage"]'), true, this.getView());
            } else if (vm.get('actions.add')) {                
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(view.down('[name="fileCustompage"]'), false, this.getView());
            }
        },
        /**
         * @private
         * @param {CMDBuildUI.view.administration.content.custompages.ViewModel} vm 
         * @param {Function} successCb 
         * @param {Function} errorCb 
         */
        save: function (vm, successCb, errorCb) {
            var view = this.getView();
            CMDBuildUI.util.Ajax.setActionId('custompage.upload');
            // define method
            var method = vm.get("actions.add") ? "POST" : "PUT";
            var input =this.getView().down('[name="fileCustompage"]').extractFileInput();
            // init formData
            var formData = new FormData();

            // append attachment json data
            var jsonData = Ext.encode(vm.get("theCustompage").getData());
            var fieldName = 'data';
            try {
                formData.append(fieldName, new Blob([jsonData], {
                    type: "application/json"
                }));
            } catch (err) {
                // TODO: sostituire con logger
                CMDBuildUI.util.Logger.log("Unable to create attachment Blob FormData entry with type 'application/json', fallback to 'text/plain': " + err, CMDBuildUI.util.Logger.levels.error);

                // metadata as 'text/plain' (format compatible with older webviews)
                formData.append(fieldName, jsonData);
            }

            // get url
            var custompageUrl = Ext.String.format('{0}/custompages', CMDBuildUI.util.Config.baseUrl);
            var url = vm.get('actions.add') ? custompageUrl : Ext.String.format('{0}/{1}', custompageUrl, vm.get('theCustompage._id'));
            // upload 
            if (input.files.length) {
                CMDBuildUI.util.Ajax.initRequestException();
                CMDBuildUI.util.administration.File.upload(method, formData, input, url, {
                    success: function (response) {
                        if (Ext.isFunction(successCb)) {
                            Ext.callback(successCb, null, [response]);
                        }
                    },
                    failure: function (error) {
                        if (Ext.isFunction(errorCb)) {
                            Ext.callback(errorCb, null, [error]);
                        }
                    }
                });
            } else {
                // file is empty, use default put 
                vm.get('theCustompage').save({
                    success: function (record, operation) {
                        if (Ext.isFunction(successCb)) {
                            Ext.callback(successCb, null, [record]);
                        }
                    },
                    failure: function (record, reason) {
                        if (Ext.isFunction(errorCb)) {
                            Ext.callback(errorCb, null, [reason]);
                        }

                    }
                });
            }

        }
    }
});