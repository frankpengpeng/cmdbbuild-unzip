
Ext.define('CMDBuildUI.view.administration.components.attributesfilters.Row', {
    extend: 'CMDBuildUI.view.filters.attributes.Row',

    requires: [
        'CMDBuildUI.view.administration.components.attributesfilters.RowController',
        'CMDBuildUI.view.administration.components.attributesfilters.RowModel'
    ],

    alias: 'widget.administration-filters-attributes-row',
    controller: 'administration-filters-attributes-row',
    viewModel: {
        type: 'administration-filters-attributes-row'
    }
});
