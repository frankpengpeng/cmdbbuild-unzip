Ext.define('CMDBuildUI.view.fields.reference.SelectionPopupController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-reference-selectionpopup',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            rowdblclick: 'onRowDblClick'
        },
        '#addcardbtn': {
            beforerender: 'onAddCardBtnBeforeRender'
        },
        '#searchtextinput': {
            beforerender: 'onSearchTextInputBeforeRender',
            specialkey: 'onSearchSpecialKey'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.fields.reference.SelectionPopup} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        CMDBuildUI.util.helper.ModelHelper.getModel(
            vm.get("objectType"),
            vm.get("objectTypeName")
        ).then(function (model) {
            if (vm.get("searchvalue")) {
                view.lookupReference("searchtextinput").focus();
            }
            // model name
            var modelname = CMDBuildUI.util.helper.ModelHelper.getModelName(
                vm.get("objectType"),
                vm.get("objectTypeName")
            );
            vm.set("storeinfo.modelname", modelname);

            // reconfigure table
            view.reconfigure(null, CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                allowFilter: true,
                addTypeColumn: CMDBuildUI.util.helper.ModelHelper.getObjectFromName(vm.get("objectTypeName"), vm.get("objectType")).get("prototype")
            }));

            // set autoload to true
            vm.set("storeinfo.autoload", true);
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddCardBtnBeforeRender: function (button, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        this.getView().updateAddButton(
            button,
            function(item, event, eOpts) {
                me.onAddCardBtnClick(item, event, eOpts);
            },
            vm.get("objectTypeName"),
            vm.get("objectType")
        );
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onAddCardBtnClick: function (item, event, eOpts) {
        var vm = this.getViewModel();
        var title = Ext.String.format(
            "{0} {1}",
            CMDBuildUI.locales.Locales.classes.cards.addcard,
            vm.get("objectTypeDescription")
        );
        var popup = CMDBuildUI.util.Utilities.openPopup(null, title, {
            xtype: 'classes-cards-card-create',
            fireGlobalEventsAfterSave: false,
            viewModel: {
                data: {
                    objectTypeName: item.objectTypeName
                }
            },
            buttons: [{
                text: CMDBuildUI.locales.Locales.common.actions.save,
                formBind: true, //only enabled once the form is valid
                disabled: true,
                ui: 'management-action-small',
                autoEl: {
                    'data-testid': 'selection-popup-card-create-save'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                },
                handler: function (button, e) {
                    var form = button.up("form");
                    if (form.isValid()) {
                        var object = form.getViewModel().get("theObject");
                        object.save({
                            success: function (record, operation) {
                                vm.get("records").getFilters().add({
                                    property: 'positionOf',
                                    value: record.getId()
                                });
                                vm.set("selection", record);
                                popup.destroy(true);
                            }
                        });
                    }
                }
            }, {
                text: CMDBuildUI.locales.Locales.common.actions.cancel,
                ui: 'secondary-action-small',
                autoEl: {
                    'data-testid': 'selection-popup-card-create-cancel'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                },
                handler: function (button, e) {
                    popup.destroy(true);
                }
            }]
        });
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function () {
        var vm = this.getViewModel();
        if (vm.get("searchvalue")) {
            // add filter
            var store = vm.get("records");
            store.getAdvancedFilter().addQueryFilter(vm.get("searchvalue"));
            store.load();
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // clear store filter
        var store = vm.get("records");
        store.getAdvancedFilter().clearQueryFilter();
        store.load();
        // reset input
        field.reset();
    },

    /**
    * @param {Ext.form.field.Base} field
    * @param {Ext.event.Event} event
    */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    onSearchTextInputBeforeRender: function(field, eOpts) {
        var view = this.getView();
        field.lookupViewModel().set("searchvalue", view.getDefaultSearchFilter());
    },

    /**
     * 
     * @param {CMDBuildUI.view.fields.reference.SelectionPopup} view 
     * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} record 
     * @param {HTMLElement} element 
     * @param {Number} rowindex 
     * @param {Ext.event.Event} e 
     * @param {Object} eOpts 
     */
    onRowDblClick: function(view, record, element, rowindex, e, eOpts) {
        this.onSaveBtnClick();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = this.getViewModel();
        var selection;
        if (view.getSelection() && view.getSelection().length && vm.get("records").getById(view.getSelection()[0].getId())) {
            selection = view.getSelection();
        }
        view.setValueOnParentCombo(selection);
        view.closePopup();
    }
});
