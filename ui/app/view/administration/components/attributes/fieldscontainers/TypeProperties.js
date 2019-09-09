Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.TypeProperties', {
    extend: 'Ext.form.Panel',

    alias: 'widget.administration-components-attributes-fieldscontainers-typeproperties',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [{
        layout: 'column',
        items: [{
            columnWidth: 0.5,
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.type,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.type'
            }, 
            name: 'type',
            bind: {
                value: '{theAttribute.type}'
            },
            renderer: CMDBuildUI.util.administration.helper.RendererHelper.getAttributeType
        }]
    }, {
        // If type is date
        bind: {
            hidden: '{!types.isDate}'
        },
        hidden: true,
        xtype: 'administration-attribute-datefields'
    }, {
        // If type is datetime
        bind: {
            hidden: '{!types.isDatetime}'
        },
        hidden: true,
        xtype: 'administration-attribute-datetimefields'
    }, {
        // If type is decimal
        bind: {
            hidden: '{!types.isDecimal}'
        },
        hidden: true,
        xtype: 'administration-attribute-decimalfields'
    }, {
        // If type is double
        bind: {
            hidden: '{!types.isDouble}'
        },
        hidden: true,
        xtype: 'administration-attribute-doublefields'
    }, {
        // If type is foreignKey
        bind: {
            hidden: '{!types.isForeignkey}'
        },
        hidden: true,
        xtype: 'administration-attribute-foreignkeyfields'
    }, {
        // If type is integer
        bind: {
            hidden: '{!types.isInteger}'
        },
        hidden: true,
        xtype: 'administration-attribute-integerfields'
    }, {
        // If type is ip address
        bind: {
            hidden: '{!types.isIpAddress}'
        },
        hidden: true,
        xtype: 'administration-attribute-ipaddressfields'
    }, {
        // If type is lookup
        bind: {
            hidden: '{!types.isLookup}'
        },
        hidden: true,
        xtype: 'administration-attribute-lookupfields'
    }, {
        // If type is reference
        bind: {
            hidden: '{!types.isReference}'
        },
        hidden: true,
        xtype: 'administration-attribute-referencefields'
    }, {
        // If type is string
        bind: {
            hidden: '{!types.isString}',
            theAttribute: '{theAttribute}'
        },
        hidden: true,
        xtype: 'administration-attribute-stringfields'
    }, {
        // If type is text
        bind: {
            hidden: '{!types.isText}'
        },
        hidden: true,
        xtype: 'administration-attribute-textfields'
    }, {
        // If type is time
        bind: {
            hidden: '{!types.isTime}'
        },
        hidden: true,
        xtype: 'administration-attribute-timefields'
    }, {
        // If type is timestamp
        bind: {
            hidden: '{!types.isTimestamp}'
        },
        hidden: true,
        xtype: 'administration-attribute-timestampfields'
    }]
});