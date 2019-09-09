Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.AttachmentsFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-attachmentsfieldset',
    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        collapsible: true,
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.ClassAttachments, // Class Attachments
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.ClassAttachments'
        },
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 0.5,
            items: [{
                /********************* Category Lookup **********************/
                // create / edit
                xtype: 'combobox',
                typeAhead: true,
                queryMode: 'local',
                displayField: 'name',
                valueField: '_id',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.categorylookup, // Category Lookup
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.categorylookup'
                },
                name: 'attachmentTypeLookup',
                hidden: true,
                bind: {
                    store: '{attachmentTypeLookupStore}',
                    value: '{theObject.attachmentTypeLookup}',
                    hidden: '{actions.view}'
                }
            }, {
                // view
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.categorylookup, // Category Lookup
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.categorylookup'
                },
                name: 'attachmentTypeLookup',
                hidden: true,
                bind: {
                    value: '{theObject.attachmentTypeLookup}',
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
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.descriptionmode, // Category Lookup
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.descriptionmode'
                },
                name: 'attachmentDescriptionMode',
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                hidden: true,
                bind: {
                    value: '{theObject.attachmentDescriptionMode}',
                    hidden: '{actions.view}',
                    store: '{attachmentDescriptionModeStore}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.descriptionmode, // Category Lookup
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.descriptionmode'
                },
                name: 'attachmentDescriptionMode',
                hidden: true,
                bind: {
                    value: '{theObject.attachmentDescriptionMode}',
                    hidden: '{!actions.view}'
                },
                renderer: CMDBuildUI.util.administration.helper.RendererHelper.getAttachmentDescriptionMode
            }]
        }, {
            columnWidth: 0.5,
            items: [{
                /********************* Inline notes **********************/
                // create / edit / view
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.attachmentsinline,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.attachmentsinline'
                },
                name: 'attachmentsInline',
                hidden: true,
                bind: {
                    value: '{theObject.attachmentsInline}',
                    readOnly: '{actions.view}',
                    hidden: '{!theObject}'
                }
            }]
        }, {
            columnWidth: 0.5,
            style: {
                paddingLeft: '15px'
            },
            items: [{
                /********************* Closed inline notes **********************/
                // create / edit / view
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.attachmentsinlineclosed,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.attachmentsinlineclosed'
                },
                name: 'attachmentsInlineClosed',
                hidden: true,
                bind: {
                    value: '{theObject.attachmentsInlineClosed}',
                    readOnly: '{actions.view}',
                    hidden: '{!theObject}',
                    disabled: '{checkboxAttachmentsInlineClosed.disabled}'
                }
            }]
        }]
    }]
});