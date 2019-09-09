Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.fieldsets.DisabledProcesses', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledprocesses',
    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.disabledmanagementprocesstabs,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.disabledmanagementprocesstabs'
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
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.detail,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.detail'
                },
                name: '_rp_flow_tab_detail_access',
                bind: {
                    value: '{theGroup._rp_flow_tab_detail_access}'
                }
            }, {
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.note,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.note'
                },
                name: '_rp_flow_tab_note_access',
                bind: {
                    value: '{theGroup._rp_flow_tab_note_access}'
                }
            }, {
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations'
                },
                name: '_rp_flow_tab_relation_access',
                bind: {
                    value: '{theGroup._rp_flow_tab_relation_access}'
                }
            }, {
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.history,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.history'
                },
                name: '_rp_flow_tab_history_access',
                bind: {
                    value: '{theGroup._rp_flow_tab_history_access}'
                }
            }, {
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email'
                },
                name: '_rp_flow_tab_email_access',
                bind: {
                    value: '{theGroup._rp_flow_tab_email_access}'
                }
            }, {
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.attachments,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.attachments'
                },
                name: '_rp_flow_tab_attachment_access',
                bind: {
                    value: '{theGroup._rp_flow_tab_attachment_access}'
                }
            }]
        }]
    }]
});