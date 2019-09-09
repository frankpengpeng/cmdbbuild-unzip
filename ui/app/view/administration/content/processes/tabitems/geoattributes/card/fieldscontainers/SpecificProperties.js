// locazation: ok

Ext.define('CMDBuildUI.view.administration.processes.tabitems.geoattributes.card.fieldscontainers.SpecificProperties', {
    extend: 'Ext.form.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.fieldscontainers.specificproperties.Point'
    ],
    alias: 'widget.administration-processes-tabitems-geoattributes-card-fieldscontainers-specificproperties',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [{
        // view        
        xtype: 'container',
        hidden: true,
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.type,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.type'
                },
                name: 'name',
                bind: {
                    value: '{theGeoAttribute.subtype}'
                }
            }]
        }, {
            xtype: 'administration-geoattributes-specificproperties-point'
        }]
    }, {
        // edit        
        xtype: 'container',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
                layout: 'column',
                items: [{
                    columnWidth: 0.5,
                    xtype: 'combobox',
                    clearFilterOnBlur: true,
                    queryMode: 'local',
                    displayField: 'key',
                    valueField: 'value',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.type,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.type'
                    },
                    name: 'subtype',
                    bind: {
                        store: '{subtypesStore}',
                        value: '{theGeoAttribute.subtype}'
                    },
                    listeners: {
                        beforerender: function () {
                            this.setDisabled(!this.up('administration-content-processes-tabitems-geoattributes-card-edit').lookupViewModel().get('actions.add'));
                        }
                    }
                }]
            },
            {
                xtype: 'administration-geoattributes-specificproperties-line',
                bind: {
                    hidden: '{!type.isLine}'
                }
            },
            {
                xtype: 'administration-geoattributes-specificproperties-point',
                bind: {
                    hidden: '{!type.isPoint}'
                }
            }, {
                xtype: 'administration-geoattributes-specificproperties-polygon',
                bind: {
                    hidden: '{!type.isPolygon}'
                }
            }
        ]
    }]
});