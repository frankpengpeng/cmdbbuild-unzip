Ext.define('CMDBuildUI.view.administration.content.processes.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabpanel',

    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.processes.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function () {
        var view = this.getView();
        var vm = this.getViewModel();
        Ext.resumeLayouts();
        var currentTabIndex = view.up('administration-content').getViewModel().get('activeTabs.processes') || 0;
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;

        tabPanelHelper.addTab(view, "properties", CMDBuildUI.locales.Locales.administration.classes.properties.title, [{
            xtype: 'administration-content-processes-tabitems-properties-properties',
            objectTypeName: vm.get("objectTypeName"),
            objectId: vm.get("objectId"),

            autoScroll: true
        }], 0, { disabled: '{disabledTabs.properties}' });

        tabPanelHelper.addTab(view, "attributes", CMDBuildUI.locales.Locales.administration.attributes.attributes, [{
            xtype: 'administration-content-processes-tabitems-attributes-attributes',
            objectTypeName: vm.get("objectTypeName"),
            objectId: vm.get("objectId")
        }], 1, { disabled: '{disabledTabs.attributes}' });

        tabPanelHelper.addTab(view, "domains", CMDBuildUI.locales.Locales.administration.navigation.domains, [{
            xtype: 'administration-content-processes-tabitems-domains-domains'
        }], 2, { disabled: '{disabledTabs.domains}' });
        tabPanelHelper.addTab(view, "tasks", 'Tasks',[{
            xtype: 'administration-content-tasks-view',
            type:'workflow',
            workflowClassName: vm.get("objectTypeName")            
        }], 3, { disabled: '{disabledTabs.tasks}' });
        tabPanelHelper.addTab(view, "layers", CMDBuildUI.locales.Locales.administration.classes.strings.levels, [{
            xtype: 'administration-content-processes-tabitems-layers-layers'
        }], 4, { disabled: '{disabledTabs.layers}' });

        tabPanelHelper.addTab(view, "geo-attributes", CMDBuildUI.locales.Locales.administration.classes.strings.geaoattributes, [{
            xtype: 'administration-content-processes-tabitems-geoattributes-geoattributes'
        }], 5, { disabled: '{disabledTabs.geoattributes}' });

    },

    /**
     * @param {CMDBuildUI.view.administration.content.processes.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.processes', this, view, newtab, oldtab, eOpts);
    },


    onItemCreated: function (record, eOpts) {
        // TODO: reload menu tree store
    },

    /**
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {Object} eOpts
     */
    onItemUpdated: function (record, eOpts) {

        Ext.ComponentQuery.query('processes-cards-grid-grid')[0].fireEventArgs('reload', [record, 'update']);
        this.redirectTo('processes/' + record.getRecordType() + '/cards/' + record.getRecordId(), true);
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
