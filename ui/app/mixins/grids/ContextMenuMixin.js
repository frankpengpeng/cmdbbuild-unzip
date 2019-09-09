Ext.define('CMDBuildUI.mixins.grids.ContextMenuMixin', {
    mixinId: 'grids-context-mixin',

    /**
     * Returns the grid on which apply context menu actions.
     * 
     * @return {Ext.gid.Panel}
     */
    getContextMenuGrid: Ext.emptyFn,

    /**
     * Initialize context menu.
     * 
     * @param {Ext.button.Button} button
     */
    initContextMenu: function (button) {
        var vm = this.lookupViewModel();
        var me = this;

        vm.set("contextmenu.multiselection.enabled", false);
        vm.set("contextmenu.multiselection.text", CMDBuildUI.locales.Locales.common.grid.enamblemultiselection);
        vm.set("contextmenu.multiselection.icon", 'x-fa fa-square-o');
        // get model object
        var objectTypeName;
        if (this.getObjectTypeName && this.getObjectTypeName()) {
            objectTypeName = this.getObjectTypeName();
        } else {
            objectTypeName = vm.get("objectTypeName");
        }
        var modelItem = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
            objectTypeName,
            vm.get("objectType")
        );

        // get items
        var menu = [];
        var menuitems = modelItem.contextMenuItems().getRange();
        menuitems.forEach(function (item) {
            if (item.get("active")) {
                var bind;
                switch (item.get("visibility")) {
                    case CMDBuildUI.model.ContextMenuItem.visibilities.one:
                        bind = { disabled: '{contextmenu.disabledvone}' };
                        break;
                    case CMDBuildUI.model.ContextMenuItem.visibilities.many:
                        bind = { disabled: '{contextmenu.disabledvmany}' };
                        break;
                }

                var executeContextMenuScript;
                if (item.get("type") === CMDBuildUI.model.ContextMenuItem.types.custom) {
                    /* jshint ignore:start */
                    var jsfn = Ext.String.format(
                        'executeContextMenuScript = function(records, api) {{0}}',
                        item.get("script")
                    );
                    try {
                        eval(jsfn);
                    } catch (e) {
                        CMDBuildUI.util.Logger.log(
                            "Error on context menu function.",
                            CMDBuildUI.util.Logger.levels.error,
                            null,
                            e
                        );
                        executeContextMenuScript = Ext.emptyFn;
                    }
                    /* jshint ignore:end */
                }

                function handler() {
                    var grid = me.getContextMenuGrid();
                    var selection = grid.getSelection();
                    var api = Ext.apply({ _grid: grid }, CMDBuildUI.util.api.Client.getApiForContextMenu());
                    switch (item.get("type")) {
                        case CMDBuildUI.model.ContextMenuItem.types.custom:
                            try {
                                executeContextMenuScript(selection, api);
                            } catch (e) {
                                CMDBuildUI.util.Logger.log(
                                    "Error on context menu script.",
                                    CMDBuildUI.util.Logger.levels.error,
                                    null,
                                    e
                                );
                            }
                            break;
                        case CMDBuildUI.model.ContextMenuItem.types.component:
                            var store = Ext.StoreManager.get("customcomponents.ContextMenus");
                            if (!store.isLoaded()) {
                                store.load({
                                    callback: function () {
                                        me.openCustomComponent(store, item, selection, grid);
                                    }
                                });
                            } else {
                                me.openCustomComponent(store, item, selection, grid);
                            }
                            break;
                    }

                }

                switch (item.get("type")) {
                    case CMDBuildUI.model.ContextMenuItem.types.separator:
                        menu.push({
                            xtype: 'menuseparator'
                        });
                        break;
                    case CMDBuildUI.model.ContextMenuItem.types.custom:
                    case CMDBuildUI.model.ContextMenuItem.types.component:
                        menu.push({
                            iconCls: 'x-fa fa-angle-double-right',
                            text: item.get("label"),
                            handler: handler,
                            bind: bind
                        });
                        break;
                }
            }
        });

        // add separator if menu is not empty
        if (menu.length) {
            menu.push({
                xtype: 'menuseparator'
            });
        }

        // add enable/disable multi-selection
        menu.push({
            iconCls: 'x-fa fa-square-o',
            text: CMDBuildUI.locales.Locales.common.grid.enamblemultiselection,
            handler: function (menuitem, eOpts) {
                me.onMultiselectionChange(menuitem, eOpts);
            },
            bind: {
                text: '{contextmenu.multiselection.text}',
                iconCls: '{contextmenu.multiselection.icon}'
            }
        });

        // add import/export actions
        if (modelItem.getImportExportTemplates) {
            modelItem.getImportExportTemplates().then(function (templates) {
                if (templates.getTotalCount()) {
                    var importtpls = [], exporttpls = [];
                    templates.getRange().forEach(function (tpl) {
                        switch (tpl.get("type")) {
                            case CMDBuildUI.model.importexports.Template.types.import:
                                importtpls.push(tpl);
                                break;
                            case CMDBuildUI.model.importexports.Template.types.export:
                                exporttpls.push(tpl);
                                break;
                            case CMDBuildUI.model.importexports.Template.types.importexport:
                                importtpls.push(tpl);
                                exporttpls.push(tpl);
                                break;
                        }
                    });
                    var btnmenu = button.getMenu();
                    btnmenu.add({
                        xtype: 'menuseparator'
                    });
                    if (importtpls.length) {
                        btnmenu.add({
                            iconCls: 'x-fa fa-upload',
                            text: CMDBuildUI.locales.Locales.common.grid.import,
                            handler: function (menuitem, eOpts) {
                                me.openImportPopup(modelItem, importtpls);
                            }
                        });
                    }
                    if (exporttpls.length) {
                        btnmenu.add({
                            iconCls: 'x-fa fa-download',
                            text: CMDBuildUI.locales.Locales.common.grid.export,
                            handler: function (menuitem, eOpts) {
                                me.openExportPopup(modelItem, exporttpls);
                            }
                        });
                    }
                }
            });
        }

        // create menu
        button.setMenu({
            xtype: 'menu',
            items: menu,
            listeners: {
                show: function () {
                    me.onContextMenuShow();
                }
            }
        });
    },

    onContextMenuShow: function () {
        var grid = this.getContextMenuGrid();

        // break context menu init if grid is empty
        if (!grid) {
            CMDBuildUI.util.Logger.log(
                Ext.String.format("getContextMenuGrid not implemented for {0}.", this.getId()),
                CMDBuildUI.util.Logger.levels.warn
            );
            return;
        }

        var vm = this.getViewModel();
        var selected = grid.getSelection().length;
        if (selected) {
            vm.set("contextmenu.disabledvone", selected > 1);
            vm.set("contextmenu.disabledvmany", false);
        } else {
            vm.set("contextmenu.disabledvone", true);
            vm.set("contextmenu.disabledvmany", true);
        }
    },

    /**
     * 
     * @param {Ext.menu.Item} menuitem 
     * @param {Object} eOpts 
     */
    onMultiselectionChange: function (menuitem, eOpts) {
        var me = this;
        var vm = menuitem.lookupViewModel();
        var grid = this.getContextMenuGrid();
        grid.setSelection(null);

        if (grid.isMultiSelectionEnabled()) {
            // set action variables
            vm.set("contextmenu.multiselection.enabled", false);
            vm.set("contextmenu.multiselection.text", CMDBuildUI.locales.Locales.common.grid.enamblemultiselection);
            vm.set("contextmenu.multiselection.icon", 'x-fa fa-square-o');

            grid.getSelectionModel().setSelectionMode("SINGLE");
            grid.getSelectionModel().excludeToggleOnColumn = null;
            grid.selModel.column.hide();
        } else {
            // set action variables
            vm.set("contextmenu.multiselection.enabled", true);
            vm.set("contextmenu.multiselection.text", CMDBuildUI.locales.Locales.common.grid.disablemultiselection);
            vm.set("contextmenu.multiselection.icon", 'x-fa fa-check-square-o');

            grid.getSelectionModel().setSelectionMode("MULTI");
            grid.getSelectionModel().excludeToggleOnColumn = 1;
            grid.selModel.column.show();
        }
    },

    /**
     * 
     * @param {CMDBuildUI.model.classes.Class} item 
     * @param {CMDBuildUI.model.importexports.Template[]} templates 
     */
    openImportPopup: function (item, templates) {
        var grid = this.getContextMenuGrid();
        var popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.common.grid.import, {
                xtype: "importexport-import",
                templates: templates,
                object: item,
                closePopup: function () {
                    popup.close();
                },
                refreshGrid: function () {
                    grid.getStore().load();
                }
            }
        );
    },

    /**
     * 
     * @param {CMDBuildUI.model.classes.Class} item 
     * @param {CMDBuildUI.model.importexports.Template[]} templates 
     */
    openExportPopup: function (item, templates) {
        var popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.common.grid.export, {
                xtype: "importexport-export",
                templates: templates,
                object: item,
                closePopup: function () {
                    popup.close();
                }
            }
        );
    },

    privates: {
        /**
         * 
         * @param {CMDBuildUI.store.customcomponents.ContextMenus} store 
         * @param {CMDBuildUI.model.ContextMenuItem} item 
         * @param {CMDBuildUI.model.classes.Card[]|CMDBuildUI.model.processes.Instance[]} selection 
         */
        openCustomComponent: function(store, item, selection, grid) {
            var ccm = store.findRecord("name", item.get("componentId"));
            if (ccm) {
                Ext.require(ccm.get("componentId"), function () {
                    var popup;
                    // create widget configuration
                    var config = {
                        xtype: ccm.get("alias").replace("widget.", ""),
                        selection: selection,
                        ownerGrid: grid,
                        listeners: {
                            /**
                             * Custom event to close popup directly from widget
                             */
                            popupclose: function (eOpts) {
                                popup.close();
                            }
                        }
                    };

                    // custom panel listeners
                    var listeners = {
                        /**
                         * @param {Ext.panel.Panel} panel
                         * @param {Object} eOpts
                         */
                        beforeclose: function (panel, eOpts) {
                            panel.removeAll(true);
                        }
                    };
                    // open popup
                    popup = CMDBuildUI.util.Utilities.openPopup(
                        null,
                        item.get("label"),
                        config,
                        listeners
                    );
                });
            }
        }
    }
});