// locazation: ok

Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.GeneralProperties', {
    extend: 'Ext.form.Panel',

    alias: 'widget.administration-components-attributes-fieldscontainers-generalproperties',

    // fieldDefaults: {
    //     labelAlign: 'top',
    //     xtype: 'fieldcontainer',
    //     flex: '0.5',
    //     layout: 'anchor'
    // },
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    items: [{
        layout: 'column',
        items: [{
            // view / view in row
            columnWidth: 0.5,
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.name,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.name'
            },
            name: 'name',
            bind: {
                value: '{theAttribute.name}',
                hidden: '{!actions.view}'
            }
        }, {
            // view / viewInRow
            columnWidth: 0.5,
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.description,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.description'
            },
            name: 'description',
            bind: {
                value: '{theAttribute.description}',
                hidden: '{!actions.view}'
            }
        }]
    }, {
        layout: 'column',
        items: [{
            itemId: 'groupfield',
            columnWidth: 0.5,
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group'
            },
            hidden: true,
            bind: {
                hidden: '{isGroupHidden}',
                value: '{theAttribute._group_description}'
            }
        }, {
            columnWidth: 0.5,
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mode,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mode'
            },
            name: 'mode',
            bind: {
                value: '{theAttribute.mode}'
            },
            renderer: CMDBuildUI.util.administration.helper.RendererHelper.getAttributeMode
        }]
    }, {
        layout: 'column',
        items: [{
            columnWidth: 0.5,
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showingrid,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showingrid'
            },
            name: 'showInGrid',
            readOnly: true,
            bind: {
                value: '{theAttribute.showInGrid}'
            }
        }, {
            columnWidth: 0.5,
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid'
            },
            name: 'showInReducedGrid',
            readOnly: true,
            bind: {
                value: '{theAttribute.showInReducedGrid}'
            }
        }]
    }, {
        layout: 'column',
        items: [{
            columnWidth: 0.5,
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unique,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unique'
            },
            name: 'unique',
            readOnly: true,
            bind: {
                value: '{theAttribute.unique}'
            }
        }, {
            columnWidth: 0.5,
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mandatory,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mandatory'
            },
            name: 'mandatory',
            readOnly: true,
            bind: {
                value: '{theAttribute.mandatory}'
            }
        }]
    }, {
        layout: 'column',
        items: [{
            columnWidth: 0.5,
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.active,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.active'
            },
            name: 'active',
            readOnly: true,
            bind: {
                value: '{theAttribute.active}'
            }
        }]
    }]
});