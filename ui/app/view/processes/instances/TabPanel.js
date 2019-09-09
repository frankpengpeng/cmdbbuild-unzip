
Ext.define('CMDBuildUI.view.processes.instances.TabPanel',{
    extend: 'Ext.tab.Panel',

    requires: [
        'CMDBuildUI.view.processes.instances.TabPanelController',
        'CMDBuildUI.view.processes.instances.TabPanelModel'
    ],

    mixins: [
        'CMDBuildUI.mixins.DetailsTabPanel'
    ],

    alias: 'widget.processes-instances-tabpanel',
    controller: 'processes-instances-tabpanel',
    viewModel: {
        type: 'processes-instances-tabpanel'
    },

    ui: 'management',
    border: false,
    tabPosition: 'left',
    tabRotation: 0,

    defaults: {
        textAlign: 'left',
        bodyPadding: 10,
        scrollable: true,
        border: false
    },
    layout: 'fit'
});
