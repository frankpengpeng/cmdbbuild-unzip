Ext.define('CMDBuildUI.view.widgets.presetfromcard.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-presetfromcard-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#applybtn' : {
            click: 'onApplyBtnClick'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.presetfromcard.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        var widget = vm.get("theWidget");
        var typeinfo = CMDBuildUI.view.widgets.linkcards.Panel.getTypeInfo(widget);
        var objectTypeName = typeinfo.objectTypeName, objectType = typeinfo.objectType;

        if (objectType) {
            vm.set("objectType", objectType);
            vm.set("objectTypeName", objectTypeName);
        } else {
            Ext.asap(function () {
                CMDBuildUI.util.Notifier.showErrorMessage(Ext.String.format(CMDBuildUI.locales.Locales.errors.classnotfound, objectTypeName));
            });
            return;
        }

        // get the model for objtect type name
        CMDBuildUI.util.helper.ModelHelper.getModel(
            objectType,
            objectTypeName
        ).then(
            function (model) {
                // set model variable into view model
                vm.set("model", model);

                // get columns from model
                var columns = CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                    allowFilter: false,
                    addTypeColumn: CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType).get("prototype")
                });

                // add grid
                view.add({
                    xtype: 'grid',
                    columns: columns,
                    forceFit: true,
                    loadMask: true,
                    itemId: 'grid',
                    reference: 'grid',
                    selModel: {
                        selType: 'checkboxmodel',
                        showHeaderCheckbox: false,
                        checkOnly: true,
                        mode: "SINGLE",
                        allowDeselect: true,
                        excludeToggleOnColumn: 1
                    },
                    bind: {
                        store: '{gridrows}',
                        selection: '{selection}'
                    },
                    plugins: [{
                        pluginId: 'forminrowwidget',
                        ptype: 'forminrowwidget',
                        id: 'forminrowwidget',
                        expandOnDblClick: true,
                        removeWidgetOnCollapse: true,
                        widget: {
                            xtype: 'classes-cards-card-view',
                            viewModel: {}, // do not remove otherwise the viewmodel will not be initialized
                            tabpaneltools: []
                        }
                    }]
                });
            });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onApplyBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        var selection = vm.get("selection");
        var attrsmapping = vm.get("theWidget").get("AttributeMapping");
        var target = vm.get("theTarget");
        attrsmapping.split(",").forEach(function(attr) {
            var lattr = attr.split("=");
            if (lattr.length === 2) {
                target.set(lattr[0], selection.get(lattr[1]));
            }
        });
        this.getView().fireEvent("popupclose");
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloseBtnClick: function (button, e, eOpts) {
        this.getView().fireEvent("popupclose");
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // get value
        var searchTerm = vm.get("search.value");
        if (searchTerm) {
            // add filter
            var store = vm.get("gridrows");
            store.getAdvancedFilter().addQueryFilter(searchTerm);
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
        var store = vm.get("gridrows");
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
    }

});
