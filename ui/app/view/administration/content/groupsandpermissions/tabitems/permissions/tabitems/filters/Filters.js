
Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.filters.Filters',{
    extend: 'Ext.panel.Panel',
    
    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.filters.FiltersController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.filters.FiltersModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-filters-filters',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-filters-filters',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-filters-filters'
    },
    layout: 'border',
    autoScroll: 'y',
    reference: 'filters-tab',
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar',
        region: 'north'
    }, {
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        region: 'center',
        viewModel: {

        },
        bind: {
            store: '{objectTypeGrantsStore}'
        }
    }]
});
