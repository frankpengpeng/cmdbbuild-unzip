Ext.define('CMDBuildUI.view.administration.content.gis.geoserverslayers.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.geoserverslayers.ViewController',
        'CMDBuildUI.view.administration.content.gis.geoserverslayers.ViewModel'
    ],

    alias: 'widget.administration-content-gis-geoserverslayers-view',
    controller: 'administration-content-gis-geoserverslayers-view',
    viewModel: {
        type: 'administration-content-gis-geoserverslayers-view'
    },

    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    items: [{
            xtype: 'administration-content-gis-geoserverslayers-topbar',
            region: 'north'
        },
        {
            xtype: 'administration-content-gis-geoserverslayers-grid',
            region: 'center'
        }
    ],

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.gis.geoserverlayers);
        this.callParent(arguments);
    }
});