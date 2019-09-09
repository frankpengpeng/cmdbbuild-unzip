
Ext.define('CMDBuildUI.view.administration.components.attributesfilters.Panel', {
    extend: 'CMDBuildUI.view.filters.attributes.Panel',
    requires: [
        'CMDBuildUI.view.filters.attributes.Panel',
        'CMDBuildUI.view.administration.components.attributesfilters.PanelController',
        'CMDBuildUI.view.administration.components.attributesfilters.PanelModel'
    ],

    alias: 'widget.administration-filters-attributes-panel',
    controller: 'administration-filters-attributes-panel',
    viewModel: {
        type: 'administration-filters-attributes-panel'
    },
    config: {
        allowInputParameter: false
    }
});
