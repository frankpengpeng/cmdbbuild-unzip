
Ext.define('CMDBuildUI.view.administration.content.setup.RelationChart',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.RelationChartController',
        'CMDBuildUI.view.administration.content.setup.RelationChartModel'
    ],

    controller: 'administration-content-setup-relationchart',
    viewModel: {
        type: 'administration-content-setup-relationchart'
    },

    html: 'Hello, World!!'
});
