Ext.define('CMDBuildUI.view.administration.content.classes.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabpanel',
    requires: [
        'CMDBuildUI.util.administration.helper.TabPanelHelper'
    ],

    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();       
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;

        tabPanelHelper.addTab(view,
            "properties",
            CMDBuildUI.locales.Locales.administration.classes.properties.title,
            [{
                xtype: 'administration-content-classes-tabitems-properties-properties',
                objectTypeName: vm.get("objectTypeName"),
                objectId: vm.get("objectId"),
                autoScroll: true
            }],
            0, {
                disabled: '{disabledTabs.properties}'
            });

        tabPanelHelper.addTab(view, "attributes", CMDBuildUI.locales.Locales.administration.attributes.attributes, [{
            xtype: 'administration-content-classes-tabitems-attributes-attributes',
            objectTypeName: vm.get("objectTypeName"),
            objectId: vm.get("objectId")
        }], 1, {
                disabled: '{disabledTabs.attributes}'
            });

        tabPanelHelper.addTab(view, "domains", CMDBuildUI.locales.Locales.administration.navigation.domains, [{
            xtype: 'administration-content-classes-tabitems-domains-domains'
        }], 2, {
                disabled: '{disabledTabs.domains}'
            });
        tabPanelHelper.addTab(view, "import_export", 'Import / Export', [{
            xtype: 'administration-content-importexportdata-templates-view',
            viewModel: {
                data: {
                    targetName: vm.get("objectTypeName")
                }
            }
        }], 3, {
                disabled: '{disabledTabs.import_export}'
            });

        tabPanelHelper.addTab(view, "layers", CMDBuildUI.locales.Locales.administration.classes.strings.levels, [{
            xtype: 'administration-content-classes-tabitems-layers-layers'
        }], 4, {
                disabled: '{disabledTabs.layers}'
            });

        tabPanelHelper.addTab(view, "geo-attributes", CMDBuildUI.locales.Locales.administration.classes.strings.geaoattributes, [{
            xtype: 'administration-content-classes-tabitems-geoattributes-geoattributes'
        }], 5, {
                disabled: '{disabledTabs.geoattributes}'
            });

    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        if (newtab && newtab.reference === 'import_export' && this.getView().lookupViewModel().get('isSimpleClass')) {
            this.getView().lookupViewModel().set('activeTab', 0);
            return;
        }
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.classes', this, view, newtab, oldtab, eOpts);
    },


    onItemCreated: function (record, eOpts) {
        
    },

    /**
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {Object} eOpts
     */
    onItemUpdated: function (record, eOpts) {

        Ext.ComponentQuery.query('classes-cards-grid-grid')[0].fireEventArgs('reload', [record, 'update']);
        this.redirectTo('classes/' + record.getRecordType() + '/cards/' + record.getRecordId(), true);
    },

    /**
     * @param {Object} eOpts
     */
    onCancelCreation: function (eOpts) {

        var detailsWindow = Ext.getCmp('CMDBuildManagementDetailsWindow');
        detailsWindow.fireEvent('closed');
    },

    /**
     * @param {Object} eOpts
     */
    onCancelUpdating: function (eOpts) {

        var detailsWindow = Ext.getCmp('CMDBuildManagementDetailsWindow');
        detailsWindow.fireEvent('closed');
    }
});