Ext.define('CMDBuildUI.view.administration.content.lookuptypes.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabpanel',

    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.lookuptypes.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        //view.up('administration-content').getViewModel().set('title', 'Lookups');
        var vm = this.getViewModel();
        var currentTabIndex = 0;
        this.addTab(view, "lookupList", [{
            xtype: 'administration-content-lookuptypes-tabitems-type-properties',
            objectTypeName: vm.get("lookupTypeName"),
            objectId: vm.get("objectId"),
            autoScroll: true
        }], 0, {
                disabled: '{disabledTabs.list}'
            });
        this.addTab(view, "values", [{
            xtype: 'administration-content-lookuptypes-tabitems-values-attributes',
            objectTypeName: vm.get("lookupTypeName"),
            objectId: vm.get("objectId")
        }], 1, {
                disabled: '{disabledTabs.values}'
            });

        // vm.set("activeTab", currentTabIndex);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.lookuptypes', this, view, newtab, oldtab, eOpts);
    },


    onItemCreated: function (record, eOpts) {
        // TODO: reload menu tree store
    },

    /**
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {Object} eOpts
     */
    onItemUpdated: function (record, eOpts) {
        Ext.ComponentQuery.query('classes-cards-grid-grid')[0].fireEventArgs('reload', [record, 'update']);
       // this.redirectTo('classes/' + record.getRecordType() + '/cards/' + record.getRecordId(), true);
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
    },

    privates: {
        addTab: function (view, name, items, index, bind) {
            return view.add({
                xtype: "panel",
                items: items,
                reference: name,
                layout: 'fit',

                autoScroll: true,
                padding: 0,
                tabConfig: {
                    ui: 'administration-tab-item',
                    tabIndex: index,
                    title: (CMDBuildUI.locales.Locales.administration.lookuptypes[name]) ? CMDBuildUI.locales.Locales.administration.lookuptypes[name].title : Ext.util.Format.capitalize(name).replace(/([a-z])([A-Z])/g, '$1 $2'),
                    tooltip: (CMDBuildUI.locales.Locales.administration.lookuptypes[name]) ? CMDBuildUI.locales.Locales.administration.lookuptypes[name].title : Ext.util.Format.capitalize(name).replace(/([a-z])([A-Z])/g, '$1 $2'),
                    autoEl: {
                        'data-testid': Ext.String.format('administration-lookuptypes-tab-', name)
                    }
                },
                bind: bind
            });
        }
    }
});