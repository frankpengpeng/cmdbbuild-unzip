Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsMixin', {
    mixinId: 'administration-permissions-tab-mixin',

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchChange: function (field, trigger, eOpts) {
        // get value
        var searchTerm = field.value;
        var filterCollection = this.getView().down('grid').getStore().getFilters();
        if (searchTerm) {
            filterCollection.add([{
                id: 'objectTypeNameFilter',
                property: '_object_description',
                operator: 'like',
                value: searchTerm
            }]);
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * Reset search field
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        // clear store filter
        var filterCollection = this.getView().down('grid').getStore().getFilters();
        filterCollection.removeByKey('objectTypeNameFilter');

        // reset input
        field.reset();
    },

    /**
     * Function for enable/disable sub tabs of "Permission" permission tab panel
     * @param {Number} index 
     */
    toggleEnablePermissionsTabs: function (index) {
        var vm = Ext.getCmp('CMDBuildAdministrationPermissions').getViewModel();
        vm.toggleEnableTabs(index);
    },

    /**
     * Function for enable/disable tabs of 
     * "CMDBuildUI.view.administration.content.groupsandpermissions.View" tab 
     * panel
     * @param {Number} index 
     */
    toggleEnableTabs: function (index) {
        var vm = Ext.getCmp('CMDBuildAdministrationContentGroupView').getViewModel();
        vm.toggleEnableTabs(index);
    },

    /**
     * 
     *  The filter to edit.
     */
    onActionFiltersClick: function (grid, rowIndex, colIndex, button, event, record) {
        var me = this;
        var popup;

        var actions = me.getViewModel().get('actions');
        var recordFilter = record.get('filter').length ? JSON.parse(record.get('filter')) : {};
        var popuTitle = actions.view ?
            CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.viewfilters :
            CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.editfilters;

        var type = record.get('_is_process') ? CMDBuildUI.locales.Locales.administration.processes.texts.process : CMDBuildUI.locales.Locales.administration.classes.texts.class;

        popuTitle = Ext.String.format(
            popuTitle,
            type,
            record.get('_object_description'));

        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            target: record.get("objectTypeName"),
            configuration: recordFilter

        });

        var viewmodel = {
            data: {
                objectType: record.get("objectType"),
                objectTypeName: record.get("objectTypeName"),
                theFilter: filter,
                attributesPrivileges: record.get("attributePrivileges"),
                actions: Ext.copy(actions)
            }
        };

        var listeners = {
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
        };

        var dockedItems = [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: me.getViewModel().get('actions.view'),
            items: CMDBuildUI.util.administration.helper.FormHelper.getOkCloseButtons({
                handler: function (button) {
                    var attributespanel = popup.down('administration-filters-attributes-panel');
                    var functionspanel = popup.down('administration-components-functionfilters-panel');
                    var columnprivilegespanel = popup.down('administration-components-columnsprivileges-panel');
                    // set filter
                    var value = Ext.JSON.encode({
                        attribute: attributespanel.getAttributesData(),
                        functions: functionspanel.getFunctionData()
                    });

                    if (Ext.Object.isEmpty(Ext.JSON.decode(value,true))) {
                        value = null;
                    }
                    record.set('filter', value);
                    // set row privileges
                    var attributePrivileges = {};
                    var attributes = columnprivilegespanel.lookupViewModel().get("attributes");
                    
                    if (attributes && attributes.getRange().length) {
                        attributes.getRange().forEach(function (row) {
                            attributePrivileges[row.get("name")] = row.get("mode");
                        });
                        record.set('attributePrivileges', attributePrivileges);
                    }
                    popup.close();
                }
            }, {
                    handler: function () {
                        popup.close();
                    }
                })
        }];

        var tabpanel = {
            xtype: 'tabpanel',
            layout: 'fit',
            ui: 'administration-tabandtools',
            viewModel: viewmodel,
            dockedItems: dockedItems,
            items: [],
            listeners: {
                beforerender: function (view) {
                    var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
                    tabPanelHelper.addTab(view, "rowsprivileges", CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.rowsprivileges, [{
                        xtype: 'administration-components-rowsprivileges-tabpanel',
                        autoScroll: true
                    }], 0, {});
                    tabPanelHelper.addTab(view, "columnsprivileges", CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.columnsprivileges, [{
                        xtype: 'administration-components-columnsprivileges-panel',
                        autoScroll: true
                    }], 1, {});
                    view.setActiveTab(0);
                }
            }
        };
        // var content = ;


        popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            popuTitle,
            tabpanel,
            listeners, {
                ui: 'administration-actionpanel',
                viewModel: {
                    data: {
                        index: rowIndex,
                        grid: grid,
                        record: record,
                        canedit: false
                    }
                }
            }
        );

        if (me.getViewModel().get('actions.view')) {
            popup.down('administration-filters-attributes-panel').down('panel').hide();
        }
    },

    /**
     * On remove filter action click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRemoveFilterActionClick: function (grid, rowIndex, colIndex, button, event, record) {
        record.set('filter', '');
        record.set('attributePrivileges', {});        
        if (record.previousValues && (record.previousValues.filter || record.previousValues.attributePrivileges)) {
            delete record.previousValues.filter;
            delete record.previousValues.attributePrivileges;
            delete record.modified.filter;
            delete record.modified.attributePrivileges;
        }
        record.crudState = record.crudStateWas = Ext.Object.getSize(record.modified) ? 'U' : 'R';
    }
});