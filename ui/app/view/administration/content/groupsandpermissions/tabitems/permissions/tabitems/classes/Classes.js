Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.Classes', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.ClassesController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.ClassesModel'
    ],

    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-classes-classes',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-classes-classes',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-classes-classes'
    },
    reference: 'classes-tab',
    
    layout: 'border',
    autoScroll: 'y',
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar',
        region: 'north'
    }, {
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        itemId: 'administration-classes-permissions',
        region: 'center',
        viewModel: {
        }
    }]
});