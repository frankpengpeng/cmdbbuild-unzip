Ext.define('CMDBuildUI.view.administration.content.views.card.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-views-card-form',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#editFilterBtn': {
            click: 'onEditFilterBtn'
        },
        '#removeFilterBtn': {
            click: 'onRemoveFilterBtn'
        }

    },
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        var type = vm.get('viewType');
        if (!vm.get('theViewFilter')) {
            if (!type) {

                CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', 'administration/views_empty/false/FILTER', this);
                return;
            }
            vm.linkTo("theViewFilter", {
                type: 'CMDBuildUI.model.views.View',
                create: {
                    type: type || 'FILTER'
                }
            });
        }

        var theViewFilter = vm.get('theViewFilter');
        switch (vm.get('theViewFilter.type')) {
            case 'FILTER':
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(view.down('[name="sourceClassName"]'), false, this.getView());
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(view.down('[name="sourceFunction"]'), true, this.getView());
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(view.down('[name="filter_input"]'), false, this.getView());
                break;
            case 'SQL':
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(view.down('[name="sourceClassName"]'), true, this.getView());
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(view.down('[name="sourceFunction"]'), false, this.getView());
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(view.down('[name="filter_input"]'), true, this.getView());
                break;
            default:
                break;
        }
    },

    onAddBtnClick: function (button, event, eOpts) {
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', 'administration/views_empty/true/' + button.lookupViewModel().get('viewType'), this);
    },


    onEditBtnClick: function (button, event, eOpts) {

        var view = this.getView();
        var vm = view.getViewModel();

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
        var vm = me.getViewModel();
        var form = me.getView();

        if (form.isValid()) {
            var theViewFilter = vm.get('theViewFilter');
            theViewFilter.save({
                success: function (record, operation) {                    
                    var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getViewUrl(record.get('_id'));
                    if (vm.get('actions.edit')) {
                        CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, record.get('description'), me);
                        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    } else {
                        var theTranslation = me.getViewModel().get('theTranslation');
                        if (theTranslation) {
                            var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfViewDescription(record.get('_id'));
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
                failure: function (reason) {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                },
                callback: function () {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                }
            });
        } else {

            if (button.el.dom) {
                button.setDisabled(false);
            }
        }
    },


    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var errorrs = CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(this.getView().form);

        if (this.getViewModel().get('actions.edit')) {
            vm.get("theViewFilter").reject();
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        } else if (this.getViewModel().get('actions.add')) {
            var newHref = 'administration/views_empty/' + this.getViewModel().get('theViewFilter.type');
            var store = Ext.getStore('administration.MenuAdministration');
            var vmNav = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode("href", newHref);
            vmNav.set('selected', currentNode);
            this.redirectTo(newHref, true);
        }


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
        var recordFilter = record.get('filter').length ? JSON.parse(record.get('filter')) : {};


        var popuTitle = 'Filter for view: {0}';

        var type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(record.get("sourceClassName"));


        popuTitle = Ext.String.format(
            popuTitle,
            record.get('description'));

        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            target: record.get("sourceClassName"),
            configuration: recordFilter

        });

        var viewmodel = {
            data: {
                objectType: type,
                objectTypeName: record.get("sourceClassName"),
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
                    var filter = {};
                    var attribute = button.up('tabpanel').down('administration-filters-attributes-panel').getAttributesData();
                    if (attribute) {
                        filter.attribute = attribute;
                    }
                    var relations = button.up('tabpanel').down('administration-filters-relations-panel').getRelationsData();
                    if (relations.length) {
                        filter.relation = relations;
                    }

                    var jsonFilter = JSON.stringify(filter);
                    if (Ext.Object.isEmpty(jsonFilter)) {
                        jsonFilter = '';
                    }
                    record.set('filter', jsonFilter);
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
        this.getViewModel().set('theViewFilter.filter', '');
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
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === "yes") {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
                    var theViewFilter = vm.get('theViewFilter');

                    CMDBuildUI.util.Ajax.setActionId('delete-view');
                    theViewFilter.erase({
                        success: function (record, operation) {
                            var nextUrl = 'administration/views_empty/false' + theViewFilter.get('type');
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
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theViewFilter = vm.get('theViewFilter');
        theViewFilter.set('active', !theViewFilter.get('active'));
        theViewFilter.save();
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
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfViewDescription(vm.get('action') === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit ? vm.get('theViewFilter').get('_id') : '..');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theTranslation', vm);
    },
    privates: {
        /**
         * 
         * @param {CMDBuildUI.model.base.Filter} filter The filter to edit.
         */
        getAttributesFilterTab: function (viewmodel, record) {
            var me = this;


            var filterPanel = {
                xtype: 'administration-filters-attributes-panel',
                title: CMDBuildUI.locales.Locales.administration.attributes.attributes,
                localized:{
                    title: 'CMDBuildUI.locales.Locales.administration.attributes.attributes'
                },
                viewModel: viewmodel
            };
            return filterPanel;
        },
        getRelationsFilterTab: function (viewmodel, record) {
            var me = this;
            var filterPanel = {
                xtype: 'administration-filters-relations-panel',
                title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations,
                localized:{
                    title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations'
                },
                viewModel: viewmodel
            };
            return filterPanel;
        }
    }
});