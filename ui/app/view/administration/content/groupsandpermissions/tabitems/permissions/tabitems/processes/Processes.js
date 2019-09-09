Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.processes.Processes', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.processes.ProcessesController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.processes.ProcessesModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-processes-processes',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-processes-processes',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-processes-processes'
    },
    layout: 'border',
    autoScroll: 'y',
    reference: 'processes-tab',
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar',
        region: 'north'
    }, {
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        region: 'center',
        viewModel: {}
    }]
});