Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.fieldsets.DisabledUtility', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledutility',
    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.disabledutilitymenu,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.disabledutilitymenu'
        },
        collapsible: true,
        items: [{
            xtype: 'checkboxgroup',
            columns: 1,
            vertical: true,
            bind: {
                readOnly: '{actions.view}'
            },
            items: [{
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.massiveeditingcards,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.massiveeditingcards'
                },
                name: '_rp_bulkupdate_access',
                bind: {
                    value: '{theGroup._rp_bulkupdate_access}'
                }
            }]
        }]
    }]
});