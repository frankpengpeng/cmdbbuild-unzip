// locazation: ok

Ext.define('CMDBuildUI.view.administration.processes.tabitems.geoattributes.card.fieldscontainers.GeneralProperties', {
    extend: 'Ext.form.Panel',

    alias: 'widget.administration-processes-tabitems-geoattributes-card-fieldscontainers-generalproperties',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [{
        // view        

        hidden: true,
        bind: {
            hidden: '{!actions.view}'
        },


        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                },
                name: 'name',
                bind: {
                    value: '{theGeoAttribute.name}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                },
                name: 'description',
                bind: {
                    value: '{theGeoAttribute.description}'
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom'
                },
                name: 'zoomMin',
                bind: {
                    value: '{theGeoAttribute.zoomMin}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom'
                },
                name: 'zoomMax',
                bind: {
                    value: '{theGeoAttribute.zoomMax}'
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom'
                },
                name: 'zoomDef',

                bind: {
                    value: '{theGeoAttribute.zoomDef}'
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                },
                name: 'active',
                readOnly: true,
                bind: {
                    value: '{theGeoAttribute.active}'
                }
            }]
        }]
    }, {
        // edit        

        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },

        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                },
                allowBlank: false,
                name: 'name',
                bind: {
                    value: '{theGeoAttribute.name}'
                },
                listeners: {
                    beforerender: function () {
                        // disable input field if is not ADD mode
                        this.setDisabled(!this.lookupViewModel().get('actions.add'));
                    }
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                allowBlank: false,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                },
                name: 'description',
                bind: {
                    value: '{theGeoAttribute.description}'
                }
            }]
        }, {
            layout: 'column',
            items: [
                /**
                 * 
                 * Currently OpensStreetMap support 0 - 19 zoom levels
                 * @link {https://wiki.openstreetmap.org/wiki/Zoom_levels}
                 * 
                 */
                CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                    columnWidth: 0.5,
                    padding: '0 15 0 0',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom,
                    name: 'zoomMin',
                    increment: 1,
                    minValue: 0,
                    maxValue: 19,
                    // multiplier: 1, 
                    inputDecimalPrecision: 0,
                    sliderDecimalPrecision: 0,
                    bind: {
                        value: '{theGeoAttribute.zoomMin}'
                    }
                }),

                /**
                 * 
                 * Currently OpensStreetMap support 0 - 19 zoom levels
                 * @link {https://wiki.openstreetmap.org/wiki/Zoom_levels}
                 * 
                 */
                CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom,
                    name: 'zoomMax',
                    increment: 1,
                    minValue: 0,
                    maxValue: 19,
                    inputDecimalPrecision: 0,
                    sliderDecimalPrecision: 0,
                    bind: {
                        value: '{theGeoAttribute.zoomMax}'
                    }
                })
            ]
        }, {
            layout: 'column',
            items: [
                /**
                 * 
                 * Currently OpensStreetMap support 0 - 19 zoom levels
                 * @link {https://wiki.openstreetmap.org/wiki/Zoom_levels}
                 * 
                 */
                CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                    columnWidth: 0.5,
                    name: 'zoomDef',
                    padding: '0 15 0 0',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom,
                    increment: 1,
                    minValue: 0,
                    maxValue: 19,
                    inputDecimalPrecision: 0,
                    sliderDecimalPrecision: 0,
                    bind: {
                        value: '{theGeoAttribute.zoomDef}'
                    }
                })
            ]
        }]
    }]
});