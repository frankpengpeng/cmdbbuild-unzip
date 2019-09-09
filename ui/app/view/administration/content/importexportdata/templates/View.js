
(function () {

    Ext.define('CMDBuildUI.view.administration.content.importexportdata.templates.View', {
        extend: 'Ext.panel.Panel',
        
        alias: 'widget.administration-content-importexportdata-templates-view',

        requires: [
            'CMDBuildUI.view.administration.content.importexportdata.templates.ViewController',
            'CMDBuildUI.view.administration.content.importexportdata.templates.ViewModel'
        ],

        controller: 'administration-content-importexportdata-templates-view',
        viewModel: {
            type: 'administration-content-importexportdata-templates-view'
        },
        itemId: 'administration-content-importexportdata-templates',
        loadMask: true,
        defaults: {
            textAlign: 'left',
            scrollable: true
        },
        layout: 'border',
        items: [
            { xtype: 'administration-content-importexportdata-templates-topbar', region: 'north' },
            { xtype: 'administration-content-importexportdata-templates-grid', region: 'center', bind: {hidden:'{isGridHidden}'} }
        ],

        listeners: {
            afterlayout: function (panel) {
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        },

        initComponent: function () {
            var vm = this.getViewModel();
            vm.getParent().set('title', 'Import/Export templates');
            this.callParent(arguments);
        }
    });
})();