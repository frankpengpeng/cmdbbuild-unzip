
Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.views.Views',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.views.ViewsController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.views.ViewsModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-views-views',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-views-views',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-views-views'
    },
    layout: 'border',
    autoScroll: 'y',
    reference: 'views-tab',
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar',
        region: 'north'
    }, {
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        region: 'center',
        viewModel: {}
    }]
});
