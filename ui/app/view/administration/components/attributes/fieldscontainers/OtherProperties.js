Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.OtherProperties', {
    extend: 'Ext.form.Panel',

    alias: 'widget.administration-components-attributes-fieldscontainers-otherproperties',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    
    items: [{
        hidden: true,
        bind: {
            hidden: '{!theAttribute}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.help,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.help'
                }, 
                name: 'cm_help',
                readOnly: true,
                bind: {
                    value: '{theAttribute.help}'
                },
                resizable: {
                    handles : "s"
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showif,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showif'
                }, 
                name: 'cm_showIf',
                readOnly: true,
                bind: {
                    value: '{theAttribute.showIf}'
                },
                resizable: {
                    handles : "s"
                }
            }]
        }]
    }, {
        hidden: true,
        bind: {
            hidden: '{!theAttribute}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.validationrules,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.validationrules'
                }, 
                name: 'cm_validadationRules',
                readOnly: true,
                bind: {
                    value: '{theAttribute.validationRules}'
                },
                resizable: {
                    handles : "s"
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.autovalue,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.autovalue'
                },
                readOnly: true,
                name: 'autoValue',
                bind: {
                    value: '{theAttribute.autoValue}'
                },
                resizable: {
                    handles : "s"
                }
            }]
        }]
    }, {

        hidden: true,
        bind: {
            hidden: '{!theAttribute}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'textarea',
                hidden: true, // TODO: activate on 3.x milestone
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.actionpostvalidation,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.actionpostvalidation'
                },
                name: 'actionsPostValidation',
                bind: {
                    value: '{theAttribute.actionPostValidation}'
                }
            }]
        }]
    }]
});