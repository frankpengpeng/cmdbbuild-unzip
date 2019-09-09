
Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.custompages.CustomPages',{
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.custompages.CustomPagesController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.custompages.CustomPagesModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-custompages-custompages',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-custompages-custompages',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-custompages-custompages'
    },
    layout: 'border',
    autoScroll: 'y',
    reference: 'custompages-tab',
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar',
        region: 'north'
    }, {
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        region: 'center',
        viewModel: {}
    }]
});
