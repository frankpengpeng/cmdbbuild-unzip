
Ext.define('CMDBuildUI.view.relations.list.Container',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.relations.list.ContainerController',
        'CMDBuildUI.view.relations.list.ContainerModel'
    ],

    alias: 'widget.relations-list-container',
    controller: 'relations-list-container',
    viewModel: {
        type: 'relations-list-container'
    },

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.relations.addrelations,
        reference: 'addrelationbtn',
        itemId: 'addrelationbtn',
        iconCls: 'x-fa fa-plus',
        ui: 'management-action-small',
        disabled: true,
        bind: {
            disabled: '{addbtn.disabled}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.addrelations'
        },
        autoEl: {
            'data-testid': 'relations-list-container-addbtn'
        }
    }, {
        xtype: 'button',
        iconCls: 'cmdbuildicon-relgraph',
        ui: 'management-action-small',
        itemId: 'openrelgraphbtn',
        hidded: true,
        tooltip: CMDBuildUI.locales.Locales.relationGraph.openRelationGraph,
        autoEl: {
            'data-testid': 'cards-card-view-bimBtn'
        },
        bind: {
            hidden: '{hiddenbtns.relgraph}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.relationGraph.openRelationGraph'
        }
    }]

});
