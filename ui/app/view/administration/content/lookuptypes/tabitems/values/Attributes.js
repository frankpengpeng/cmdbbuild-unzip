
Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.Attributes',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.AttributesController',
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.AttributesModel'
    ],
    
    alias: 'widget.administration-content-lookuptypes-tabitems-values-attributes',
    controller: 'administration-content-lookuptypes-tabitems-values-attributes',
    viewModel: {
        type: 'administration-content-lookuptypes-tabitems-values-attributes'
    },
    layout: 'fit',
    ui: 'administration-tabandtools',
    config: {
        objectTypeName: null
    },
    autoEl: {
        'data-testid': 'administration-content-lookuptypes-tabitems-values-attributes'
    },
    items:[{xtype: 'administration-content-lookuptypes-tabitems-values-grid-grid'}]
    
});
