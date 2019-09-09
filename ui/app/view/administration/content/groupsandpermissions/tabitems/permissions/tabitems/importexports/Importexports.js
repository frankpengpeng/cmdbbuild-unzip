
Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.importexports.Importexports',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.importexports.ImportexportsController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.importexports.ImportexportsModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-importexports-importexports',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-importexports-importexports',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-importexports-importexports'
    },
    layout: 'border',
    autoScroll: 'y',
    reference: 'ietemplate-tab',
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar',
        region: 'north'
    }, {
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        region: 'center',
        viewModel: {}
    }]
});
