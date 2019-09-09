
Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.reports.Reports',{
    extend: 'Ext.panel.Panel',
    
    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.reports.ReportsController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.reports.ReportsModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-reports-reports',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-reports-reports',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-reports-reports'
    },
    layout: 'border',
    autoScroll: 'y',
    reference: 'reports-tab',
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar',
        region: 'north'
    }, {
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        region: 'center',
        viewModel: {}
    }]
});
