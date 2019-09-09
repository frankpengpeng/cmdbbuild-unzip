Ext.define('CMDBuildUI.view.administration.content.localizations.localization.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-localization-tabpanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChange'
        }
    },

    onBeforeRender: function () {
        var view = this.getView();
        var vm = this.getViewModel();
        var defaulttab = 0;
        var i = 0;
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(view, "classes", CMDBuildUI.locales.Locales.administration.classes.title, [{
            xtype: 'administration-content-localizations-localization-tabitems-translationsgrid',
            autoScroll: true,
            viewModel: {
                storeList: ['classes.Classes'],
                tabName: 'class'
            }
        }], i++, {});
        var wfEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled);
        if (wfEnabled) {
            tabPanelHelper.addTab(view, "processes", CMDBuildUI.locales.Locales.administration.processes.title, [{
                xtype: 'administration-content-localizations-localization-tabitems-translationsgrid',
                autoScroll: true,
                viewModel: {
                    storeList: ['processes.Processes'],
                    tabName: 'process'
                }
            }], i++, {});
        }

        tabPanelHelper.addTab(view, "domains", CMDBuildUI.locales.Locales.administration.domains.pluralTitle, [{
            xtype: 'administration-content-localizations-localization-tabitems-translationsgrid',
            autoScroll: true,
            viewModel: {
                storeList: ['domains.Domains'],
                tabName: 'domain'
            }
        }], i++, {});
        tabPanelHelper.addTab(view, "views", CMDBuildUI.locales.Locales.administration.navigation.views, [{
            xtype: 'administration-content-localizations-localization-tabitems-translationsgrid',
            autoScroll: true,
            viewModel: {
                storeList: ['views.Views'],
                tabName: CMDBuildUI.util.administration.helper.FormHelper.formActions.view
            }
        }], i++, {});
        tabPanelHelper.addTab(view, "searchfilters", CMDBuildUI.locales.Locales.administration.navigation.searchfilters, [{
            xtype: 'administration-content-localizations-localization-tabitems-translationsgrid',
            autoScroll: true,
            viewModel: {
                storeList: [],
                tabName: 'searchfilters'
            }
        }], i++, {});
        tabPanelHelper.addTab(view, "lookups", CMDBuildUI.locales.Locales.administration.lookuptypes.title, [{
            xtype: 'administration-content-localizations-localization-tabitems-translationsgrid',
            autoScroll: true,
            viewModel: {
                storeList: ['lookups.LookupTypes'],
                tabName: 'lookup'
            }
        }], i++, {});
        tabPanelHelper.addTab(view, "reports", CMDBuildUI.locales.Locales.administration.navigation.reports, [{
            xtype: 'administration-content-localizations-localization-tabitems-translationsgrid',
            autoScroll: true,
            viewModel: {
                storeList: ['reports.Reports'],
                tabName: 'report'
            }
        }], i++, {});
        tabPanelHelper.addTab(view, "dashboards", CMDBuildUI.locales.Locales.administration.navigation.dashboards, [{
            xtype: 'administration-content-localizations-localization-tabitems-translationsgrid',
            autoScroll: true,
            viewModel: {
                storeList: [],
                tabName: 'dashboard'
            }
        }], i++, {});
        tabPanelHelper.addTab(view, "menu", 'Menu', [{
            xtype: 'administration-content-localizations-localization-tabitems-translationsmenutreepanel',
            autoScroll: true,
            viewModel: {
                storeList: [],
                tabName: 'menu'
            }
        }], i++, {});
        vm.set('activeTab', defaulttab); //this.getView().up('administration-content').getViewModel().get('activeTabs.localizations') || 0);
    },

    onEditBtnClick: function (button, e, eOpts) {
        var grid = this.getView().getActiveTab().down();
        var vm = this.getViewModel();
        vm.set('actions.view', false);
        vm.set('actions.edit', true);
        vm.toggleEnableTabs(vm.get('activeTab'));
        grid.getColumns().forEach(function (column) {
            if (!column.locked) {
                column.setEditor(true);
            }
        });
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.event.Event} event
     */
    onKeyUp: function (field, event) {
        var vm = this.getViewModel();
        var searchTerm = '';
        if (vm.getData().search) {
            searchTerm = vm.getData().search.value;
        }
        var store = this.getView().getActiveTab().down('grid').getViewModel().get('completeTranslationsStore');
        store.clearFilter();
        if (searchTerm) {
            CMDBuildUI.util.administration.helper.GridHelper.searchMoreFields(store, searchTerm);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        // clear store filter
        var store = this.getView().getActiveTab().down('grid').getViewModel().get('completeTranslationsStore');
        if (store) {
            store.clearFilter();
        }
        // reset input
        field.reset();
    },

    /**
     * @param {CMDBuildUI.view.administration.content.groupsandpermissions.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChange: function (view, newtab, oldtab, eOpts) {

        var field = this.getView().lookupReference('localizationsearchtext');
        if (field.getValue()) {
            this.onSearchClear(field);
        }
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.localizations', this, view, newtab, oldtab, eOpts);
        if (newtab.viewModelKey == 'menu') {
            this.getViewModel().set('canFilter', false);
        } else {
            this.getViewModel().set('canFilter', true);
        }
    },

    onstoreLoaded: function (store, records) {

        var vm = this.getViewModel();
        var translationsStore = vm.getStore('translations');
        var languagesStore = vm.getStore('languages');
        if (languagesStore.isLoaded() && translationsStore.isLoaded()) {

            var languageRecords = languagesStore.getRange();
            var grid = this.getView().getActiveTab().down('grid');
            var columns = [{
                text:CMDBuildUI.locales.Locales.administration.localizations.element,
                dataIndex: 'element',
                align: 'left',
                locked: true
            }, {
                text: CMDBuildUI.locales.Locales.administration.localizations.type,
                dataIndex: 'type',
                align: 'left',
                locked: true
            }, {
                text: CMDBuildUI.locales.Locales.administration.localizations.defaulttranslation,
                dataIndex: 'defaulttranslation',
                align: 'left',
                locked: true

            }];
            languageRecords.forEach(function (record) {
                var lang = record.get('description');
                var code = record.get('code');
                var flag = '<img width="20px" style="vertical-align:middle;margin-right:5px" src="resources/images/flags/' + code + '.png" />';
                columns.push({
                    text: flag + lang,
                    dataIndex: code,
                    align: 'left',
                    locked: false
                });

            });
            grid.reconfigure(null, columns);

        }
    }
});