Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.String', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-stringfields',
    config: {
        theAttribute: null,
        actions: {}
    },
    items: [{
        // add
        xtype: 'container',
        bind: {
            hidden: '{!actions.add}'
        },
        items: [{
            layout: 'column',

            items: [{
                columnWidth: 0.5,
                xtype: 'numberfield',
                step: 1,
                minValue: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength'
                },
                name: 'maxLength',
                bind: {
                    value: '{theAttribute.maxLength}',
                    hidden: '{theAttribute.inherited}'
                }
            }]
        }]
    }, {
        // edit
        xtype: 'container',
        bind: {
            hidden: '{!actions.edit}'
        },
        items: [{
            layout: 'column',

            items: [{
                columnWidth: 0.5,
                xtype: 'numberfield',
                step: 1,
                minValue: 1,
                disabled: true,
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength'
                },
                name: 'maxLength',
                bind: {
                    value: '{theAttribute.maxLength}',
                    disabled: '{theAttribute.inherited}'
                }
            }]
        }]
    }, {
        // view
        xtype: 'container',
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            layout: 'column',

            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                step: 1,
                minValue: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength'
                },
                name: 'maxLength',
                bind: {
                    value: '{theAttribute.maxLength}'
                }
            }]
        }]
    }]
});