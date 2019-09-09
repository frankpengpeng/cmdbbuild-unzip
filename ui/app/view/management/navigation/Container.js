
Ext.define('CMDBuildUI.view.management.navigation.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.management.navigation.ContainerController',
        'CMDBuildUI.view.management.navigation.ContainerModel',

        'CMDBuildUI.view.management.navigation.Tree'
    ],

    xtype: 'management-navigation-container',
    controller: 'management-navigation-container',
    viewModel: {
        type: 'management-navigation-container'
    },

    title: CMDBuildUI.locales.Locales.main.navigation,
    layout: 'container',
    width: 250,
    scrollable: true,
    cls: Ext.baseCSSPrefix + 'panel-bold-header',

    bind: {
        title: '{navTitle}'
    },

    autoEl: {
        'data-testid': 'management-navigation-container'
    }
});
