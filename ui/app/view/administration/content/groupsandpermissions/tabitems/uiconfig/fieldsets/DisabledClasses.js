Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.fieldsets.DisabledClasses', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledclasses',
    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        ui: 'administration-formpagination',        
        title: CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.managementdisabledtabs,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.managementdisabledtabs'
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
                name: '_rp_card_tab_detail_access',
                uncheckedValue: true,
                inputValue: false,
                bind: {
                    value: '{theGroup._rp_card_tab_detail_access}'
                }
            }, {
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.note,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.note'
                },
                name: '_rp_card_tab_note_access',
                uncheckedValue: true,
                inputValue: false,
                bind: {
                    value: '{theGroup._rp_card_tab_note_access}'
                }
            }, {
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations'
                },
                name: '_rp_card_tab_relation_access',
                uncheckedValue: true,
                inputValue: false,
                bind: {
                    value: '{theGroup._rp_card_tab_relation_access}'
                }
            }, {
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.history,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.history'
                },
                name: '_rp_card_tab_history_access',
                uncheckedValue: true,
                inputValue: false,
                bind: {
                    value: '{theGroup._rp_card_tab_history_access}'
                }
            }, {
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email'
                },
                name: '_rp_card_tab_email_access',
                uncheckedValue: true,
                inputValue: false,
                bind: {
                    value: '{theGroup._rp_card_tab_email_access}'
                }
            }, {
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.attachments,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.attachments'
                },
                name: '_rp_card_tab_attachment_access',
                uncheckedValue: true,
                inputValue: false,
                bind: {
                    value: '{theGroup._rp_card_tab_attachment_access}'
                }
            }]
        }]
    }]
});