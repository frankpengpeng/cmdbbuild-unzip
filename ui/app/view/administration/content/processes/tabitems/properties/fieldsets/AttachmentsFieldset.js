Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.AttachmentsFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-attachmentsfieldset',
    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        collapsible: true,
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.processes.strings.processattachments,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.processes.strings.processattachments'
        },
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 0.5,
            items: [{
                /********************* Category Lookup **********************/
                xtype: 'combobox',
                typeAhead: true,
                queryMode: 'local',
                displayField: 'name',
                valueField: '_id',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.categorylookup,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.categorylookup'
                },
                name: 'attachmentTypeLookup',
                hidden: true,
                bind: {
                    store: '{attachmentTypeLookupStore}',
                    value: '{theProcess.attachmentTypeLookup}',
                    hidden: '{actions.view}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.categorylookup,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.categorylookup'
                },
                name: 'attachmentTypeLookup',
                hidden: true,
                bind: {
                    value: '{theProcess.attachmentTypeLookup}',
                    hidden: '{!actions.view}'
                }
            }]
        }, {
            cmdbuildtype: 'column',
            columnWidth: 0.5,
            style: {
                paddingLeft: '15px'
            },
            items: [{
                /********************* Description Mode **********************/
                xtype: 'combobox',
                typeAhead: true,
                queryMode: 'local',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.descriptionmode,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.descriptionmode'
                },
                name: 'attachmentDescriptionMode',
                displayField: 'label',
                valueField: 'value',
                hidden: true,
                bind: {
                    value: '{theProcess.attachmentDescriptionMode}',
                    hidden: '{actions.view}',
                    store: '{attachmentDescriptionModeStore}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.descriptionmode,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.descriptionmode'
                },
                name: 'attachmentDescriptionMode',
                hidden: true,
                bind: {
                    value: '{theProcess.attachmentDescriptionMode}',
                    hidden: '{!actions.view}'
                },
                renderer: CMDBuildUI.util.administration.helper.RendererHelper.getAttachmentDescriptionMode
            }]
        }]
    }]
});