Ext.define('CMDBuildUI.view.filters.LauncherController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.filters-launcher',

    control: {
        '#mainbtn': {
            beforerender: 'onMainBtnBeforeRender'
        },
        '#clearfiltertool': {
            click: 'onClearFilterToolClick'
        },
        '#filterdesc': {
            click: 'onOpenMenuClick'
        }
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onMainBtnBeforeRender: function (button, eOpts) {
        var vm = this.getViewModel();
        var me = this;

        function initMenu(filters) {
            if (filters && filters.length) {
                button.on("click", me.onOpenMenuClick, me);
            } else {
                button.on("click", me.onAddNewFilterClick, me);
            }
        }

        vm.bind({
            bindTo: {
                objectTypeName: '{objectTypeName}',
                objectType: '{objectType}'
            }
        }, function (data) {
            if (data.objectTypeName && data.objectType) {
                var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objectTypeName, data.objectType);
                vm.set("item", item);
                item.getFilters().then(function (filters) {
                    initMenu(filters.getRange());
                });
            }
        });
    },

    /**
     * Open a popup to create a new filter
     */
    onAddNewFilterClick: function () {
        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            target: this.getViewModel().get("objectTypeName"),
            configuration: {}
        });
        this.editFilter(filter);
    },

    onOpenMenuClick: function () {
        var view = this.getView();
        if (!view.isMenuExpanded) {
            view.getMenu().getViewModel().setStores({
                filters: this.getViewModel().get("item").filters()
            });
            view.expandMenu();
        }
    },

    /**
     * 
     * @param {CMDBuildUI.model.base.Filter} filter The filter to edit.
     */
    editFilter: function (filter) {
        var me = this;
        var vm = this.getViewModel();

        var viewmodel = {
            data: {
                objectType: vm.get("objectType"),
                objectTypeName: vm.get("objectTypeName"),
                theFilter: filter
            }
        };

        // popup definition
        var popup = CMDBuildUI.util.Utilities.openPopup(null, filter.get("description"), {
            xtype: 'filters-panel',
            viewModel: viewmodel,
            listeners: {
                /**
                 * 
                 * @param {CMDBuildUI.view.filters.Panel} panel 
                 * @param {CMDBuildUI.model.base.Filter} filter 
                 * @param {Object} eOpts 
                 */
                applyfilter: function (panel, filter, eOpts) {
                    me.onApplyFilter(filter);
                    popup.close();
                },
                /**
                 * 
                 * @param {CMDBuildUI.view.filters.Panel} panel 
                 * @param {CMDBuildUI.model.base.Filter} filter 
                 * @param {Object} eOpts 
                 */
                saveandapplyfilter: function (panel, filter, eOpts) {
                    me.onSaveAndApplyFilter(filter);
                    popup.close();
                },
                /**
                 * Custom event to close popup directly from popup
                 * @param {Object} eOpts 
                 */
                popupclose: function (eOpts) {
                    popup.close();
                }
            }
        });
    },

    /**
     * 
     * @param {CMDBuildUI.model.base.Filter} filter The filter to delete.
     */
    deleteFilter: function (filter) {
        if (this.getViewModel().get("appliedfilter.id") === filter.getId()) {
            this.onClearFilterToolClick();
        }
        filter.erase();
    },

    /**
     * 
     * @param {CMDBuildUI.model.base.Filter} filter 
     */
    onApplyFilter: function (filter) {
        var me = this;

        me.applyFilter(filter);

        var button = me.lookup("mainbtn");
        button.un("click", me.onAddNewFilterClick, me);
        button.on("click", me.onOpenMenuClick, me);
    },

    /**
     * 
     * @param {CMDBuildUI.model.base.Filter} filter 
     */
    onSaveAndApplyFilter: function (filter) {
        this.onApplyFilter(filter);
        filter.save();
    },

    /**
     * 
     * @param {Ext.panel.Tool} tool 
     * @param {Ext.event.Event} e 
     * @param {CMDBuildUI.view.filters.Launcher} owner 
     * @param {Object} eOpts 
     */
    onClearFilterToolClick: function (tool, e, owner, eOpts) {
        this.getView().clearFilter();
    },

    /**
     * 
     * @param {Ext.view.Table} grid 
     * @param {HTMLElement} td 
     * @param {Number} cellIndex 
     * @param {CMDBuildUI.model.base.Filter} record 
     * @param {HTMLElement} tr 
     * @param {Number} rowIndex 
     * @param {Ext.event.Event} e 
     * @param {Object} eOpts 
     */
    onFiltersGridCellClick: function (grid, td, cellIndex, record, tr, rowIndex, e, eOpts) {
        if (cellIndex === 0) {
            this.applyFilter(record);
            this.getView().collapseMenu();
        }
    },

    privates: {
        /**
         * 
         * @param {Object[]} runtimeattrs 
         * @return {Ext.form.Field[]}
         */
        getFormForRuntimeAttributes: function (runtimeattrs) {
            var fields = [];
            var vm = this.getViewModel();
            var modelName = CMDBuildUI.util.helper.ModelHelper.getModelName(vm.get("objectType"), vm.get("objectTypeName"));
            var model = Ext.ClassManager.get(modelName);
            runtimeattrs.forEach(function (a) {
                var field = model.getField(a.attribute);
                var editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(
                    field
                );

                editor.fieldLabel = Ext.String.format("{0} - {1}", field.attributeconf.description_localized, CMDBuildUI.model.base.Filter.getOperatorDescription(a.operator));
                editor._tempid = a._tempid;

                var container = {
                    xtype: 'fieldcontainer',
                    layout: 'anchor',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    items: [editor]
                };

                if (a.operator === CMDBuildUI.model.base.Filter.operators.between) {
                    container.items.push(Ext.applyIf({
                        fieldLabel: '',
                        _tempid: a._tempid + '-v2'
                    }, editor));
                }
                fields.push(container);
            });
            return fields;
        },

        /**
         * 
         * @param {CMDBuild.model.base.Filter} filter
         */
        applyFilter: function (filter) {
            var me = this,
                view = me.getView();
            var vm = this.getViewModel();

            function applyFilter() {
                // apply filter
                var store = me.getViewModel().get(view.getStoreName());
                store.getAdvancedFilter().clearAdvancedFilter();
                store.getAdvancedFilter().applyAdvancedFilter(filter.get("configuration"));
                store.load();


                var filters = vm.get("item").filters();
                var filterposition = filters.findRecord("_id", filter.getId());
                if (!filterposition || filterposition === -1) {
                    filters.add(filter);
                }

                // update selected filter data
                vm.set("appliedfilter.id", filter.getId());
                vm.set("appliedfilter.description", filter.get("description"));
            }

            // check runtime attributes
            var runtimeattrs = [];

            function checkRuntime(v) {
                if (v.parameterType === CMDBuildUI.model.base.Filter.parametersypes.runtime) {
                    v._tempid = Ext.String.format("{0}-{1}", v.attribute, Ext.String.leftPad(Ext.Number.randomInt(0, 9999), 4, '0'));
                    runtimeattrs.push(v);
                }
            }

            CMDBuildUI.view.filters.Launcher.analyzeAttributeRecursive(filter.get("configuration").attribute, checkRuntime);
            if (runtimeattrs.length > 0) {
                var popup;
                var form = {
                    xtype: 'form',
                    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                    scrollable: true,
                    items: this.getFormForRuntimeAttributes(runtimeattrs),
                    listeners: {
                        beforedestroy: function (form) {
                            form.removeAll(true);
                        }
                    },
                    buttons: [{
                        text: CMDBuildUI.locales.Locales.common.actions.apply,
                        ui: 'management-action-small',
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.common.actions.apply'
                        },
                        handler: function (button, e) {
                            var fields = {};
                            var form = button.up("form");
                            form.getForm().getFields().getRange().forEach(function (f) {
                                fields[f._tempid] = f;
                            });

                            function updateRuntimeValues(f) {

                                f.value = [];
                                var v = fields[f._tempid].getValue();
                                if (v !== undefined) {
                                    f.value.push(v);
                                }
                                if (f.operator === CMDBuildUI.model.base.Filter.operators.between && fields[f._tempid + '-v2'].getValue()) {
                                    f.value.push(fields[f._tempid + '-v2'].getValue());
                                }
                                delete f._tempid;
                            }
                            CMDBuildUI.view.filters.Launcher.analyzeAttributeRecursive(filter.get("configuration").attribute, updateRuntimeValues);
                            applyFilter();
                            popup.destroy();
                        }
                    }, {
                        text: CMDBuildUI.locales.Locales.common.actions.cancel,
                        ui: 'secondary-action-small',
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                        },
                        handler: function (button, e) {
                            popup.destroy();
                        }
                    }]
                };
                popup = CMDBuildUI.util.Utilities.openPopup(null, '', form, {}, {
                    width: '40%',
                    height: '40%'
                });
            } else {
                applyFilter();
            }
        }
    }

});