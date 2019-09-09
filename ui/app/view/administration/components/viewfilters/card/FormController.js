Ext.define('CMDBuildUI.view.administration.components.viewfilters.card.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-viewfilters-card-form',

    control: {
        '#': {
            afterrender: 'onAfterRender'
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
        '#editFilterBtn': {
            click: 'onEditFilterBtn'
        },
        '#removeFilterBtn': {
            click: 'onRemoveFilterBtn'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }
    },
    onAfterRender: function (view) {
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.searchfilters);
    },

    /**
     * On add searchfilters button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, e, eOpts) {
        this.redirectTo(CMDBuildUI.util.administration.helper.ApiHelper.client.getTheViewFilterUrl(undefined, true));
    },

    onEditBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },
    onDeleteBtnClick: function (button, event, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        var theViewFilter = vm.get('theViewFilter');
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-serachfilter');
                    theViewFilter.erase({
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheViewFilterUrl();
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', CMDBuildUI.util.administration.helper.ApiHelper.client.getTheViewFilterUrl(record.get('name')));
                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                        }
                    });
                }
            }, me);
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        var me = this;
        var vm = me.getViewModel();
        var form = me.getView();
        if (form.isValid()) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
            var theViewFilter = vm.get('theViewFilter');
            var groups = vm.getStore('rolesStore') ? vm.getStore('rolesStore').getRange() : [];
            var defaultFor = [];
            Ext.Array.forEach(groups, function (item) {
                if (item.get('active')) {
                    defaultFor.push({
                        _id: item.get('_id')
                    });
                }
            });
            theViewFilter.save({
                success: function (record, operation) {
                    Ext.GlobalEvents.fireEventArgs("itemcreated", [record]);
                    CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                    CMDBuildUI.util.administration.helper.AjaxHelper.setGroupsForFilter(theViewFilter.getId(), defaultFor).then(
                        function (response) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheViewFilterUrl(theViewFilter.get('name'));
                            if (vm.get('actions.edit')) {
                                var newDescription = record.get('description');
                                CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, newDescription, me);
                                CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                            } else {
                                var theTranslation = me.getViewModel().get('theTranslation');
                                if (theTranslation) {
                                    var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfSearchFiltreDescription(record.get('_id'));
                                    theTranslation.set('_id', translationCode);
                                    theTranslation.crudState = 'U';
                                    theTranslation.crudStateWas = 'U';
                                    theTranslation.phantom = false;
                                    theTranslation.save({
                                        success: function (localeRecord, operation) {
                                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                                                function () {
                                                    if (button.el && button.el.dom) {
                                                        button.setDisabled(false);
                                                    }
                                                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                                                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                                                });
                                        }
                                    });
                                } else {
                                    CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                                        function () {
                                            if (button.el && button.el.dom) {
                                                button.setDisabled(false);
                                            }
                                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                                        });
                                }
                            }
                        },
                        function (error) {
                            CMDBuildUI.util.Logger.log(error, CMDBuildUI.util.Logger.levels.error);
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                        }
                    );
                },
                failure: function (reason) {
                    if (button.el && button.el.dom) {
                        button.setDisabled(false);
                    }
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);

                }
            });
        } else {

            button.setDisabled(false);
        }
    },


    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {

        var vm = this.getViewModel();
        vm.get("theViewFilter").reject();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        button.up().fireEvent("closed");
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditFilterBtn: function (button, e, eOpts) {
        var me = this;

        var vm = me.getViewModel();
        var record = vm.get('theViewFilter');
        var actions = vm.get('actions');
        var recordFilter = record.get('configuration').length ? JSON.parse(record.get('configuration')) : {};


        var popuTitle = actions.view ?
            CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.viewfilters :
            CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.editfilters;

        var type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(record.get("target"));


        popuTitle = Ext.String.format(
            popuTitle,
            type,
            record.get('description'));

        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            target: record.get("target"),
            configuration: recordFilter,
            shared: true

        });
        var viewmodel = {
            data: {
                objectType: type,
                objectTypeName: record.get("target"),
                theFilter: filter,
                actions: Ext.copy(actions)
            }
        };
        var attrbutePanel = this.getAttributesFilterTab(viewmodel, record);
        var relationsPanel = this.getRelationsFilterTab(viewmodel, record);
        var listeners = {
            /**
             * 
             * @param {CMDBuildUI.view.filters.Panel} panel 
             * @param {CMDBuildUI.model.base.Filter} filter 
             * @param {Object} eOpts 
             */
            applyfilter: function (panel, filter, eOpts) {
                me.onApplyFilter(filter);
                me.popup.close();
            },
            /**
             * 
             * @param {CMDBuildUI.view.filters.Panel} panel 
             * @param {CMDBuildUI.model.base.Filter} filter 
             * @param {Object} eOpts 
             */
            saveandapplyfilter: function (panel, filter, eOpts) {
                me.onSaveAndApplyFilter(filter);
                me.popup.close();
            },
            /**
             * Custom event to close popup directly from popup
             * @param {Object} eOpts 
             */
            popupclose: function (eOpts) {
                me.popup.close();
            }
        };
        var dockedItems = [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: me.getViewModel().get('actions.view'),
            items: CMDBuildUI.util.administration.helper.FormHelper.getOkCloseButtons({
                handler: function (button) {
                    var value = JSON.stringify({
                        attribute: button.up('tabpanel').down('administration-filters-attributes-panel').getAttributesData(),
                        relation: button.up('tabpanel').down('administration-filters-relations-panel').getRelationsData()
                    });
                    if (Ext.Object.isEmpty(value)) {
                        value = '';
                    }
                    record.set('configuration', value);
                    me.popup.close();
                }
            }, {
                    handler: function () {
                        me.popup.close();
                    }
                })
        }];
        var content = {
            xtype: 'tabpanel',
            cls: 'administration',
            ui: 'administration-tabandtools',
            items: [attrbutePanel, relationsPanel],
            dockedItems: dockedItems,
            listeners: listeners
        };



        me.popup = CMDBuildUI.util.Utilities.openPopup(
            'filterpopup',
            popuTitle,
            content, {}, {
                ui: 'administration-actionpanel',
                viewModel: {
                    data: {
                        index: '0',
                        grid: {},
                        record: record,
                        canedit: true
                    }
                }
            }
        );
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRemoveFilterBtn: function (button, e, eOpts) {
        button.lookupViewModel().get('theViewFilter').set('configuration', '');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theViewFilter = vm.get('theViewFilter');
        theViewFilter.set('active', !theViewFilter.get('active'));

        theViewFilter.save({
            success: function (record, operation) {

            },
            failure: function (record, reason) {
                record.reject();
            }
        });

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
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfSearchFiltreDescription(vm.get('action') === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit ? vm.get('theViewFilter').get('_id') : '..');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theTranslation', vm);
    },
    privates: {
        /**
         * 
         * 
         */
        getAttributesFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-filters-attributes-panel',
                title: CMDBuildUI.locales.Locales.administration.attributes.attributes, // Attributes
                viewModel: viewmodel
            };
            return filterPanel;
        },

        getRelationsFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-filters-relations-panel',
                title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations, // Relations
                viewModel: viewmodel
            };
            return filterPanel;
        }
    }
});