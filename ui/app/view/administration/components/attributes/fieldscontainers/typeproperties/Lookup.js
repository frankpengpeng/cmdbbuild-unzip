Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Lookup', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-lookupfields',

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
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookup,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookup'
                },
                name: 'lookup',
                clearFilterOnBlur: false,
                anyMatch: true,
                autoSelect: true,
                forceSelection: true,
                displayField: 'name',
                valueField: '_id',
                bind: {
                    value: '{theAttribute.lookupType}',
                    store: '{lookupStore}',
                    disabled: '{actions.edit}',
                    hidden: '{actions.view}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter'
                },
                name: 'filter',
                bind: {
                    value: "{theAttribute.filter}",
                    readOnly: '{actions.view}'
                },
                labelToolIconCls: 'fa-list',
                labelToolIconQtip: 'Add metadata',
                labelToolIconClick: 'onEditMetadataClickBtn'
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique'
                },
                name: 'preselectIfUnique',
                bind: {
                    value: '{theAttribute.preselectIfUnique}',
                    readOnly: '{actions.view}'
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
                // ADD / EDIT
                columnWidth: 0.5,
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookup,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookup'
                },
                name: 'lookup',
                clearFilterOnBlur: false,
                anyMatch: true,
                autoSelect: true,
                forceSelection: true,
                displayField: 'name',
                valueField: '_id',
                bind: {
                    value: '{theAttribute.lookupType}',
                    store: '{lookupStore}',
                    disabled: '{actions.edit}',
                    hidden: '{actions.view}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter'
                },
                name: 'filter',
                bind: {
                    value: "{theAttribute.filter}"
                },
                labelToolIconCls: 'fa-list',
                labelToolIconQtip: 'Edit metadata',
                labelToolIconClick: 'onEditMetadataClickBtn'
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique'
                },
                name: 'preselectIfUnique',
                bind: {
                    value: '{theAttribute.preselectIfUnique}'
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
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookup,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookup'
                },
                bind: {
                    value: '{theAttribute.lookupType}',
                    hidden: '{!actions.view}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textarea',
                itemId: 'attribute-filterField',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter'
                },
                name: 'filter',
                readOnly: true,
                bind: {
                    value: "{theAttribute.filter}"
                },

                labelToolIconCls: 'fa-list',
                labelToolIconQtip: 'Show metadata',
                labelToolIconClick: 'onViewMetadataClick'
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique'
                },
                name: 'preselectIfUnique',
                bind: {
                    value: '{theAttribute.preselectIfUnique}',
                    readOnly: '{actions.view}'
                }
            }]
        }]
    }]
});