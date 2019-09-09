Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.fieldscontainers.specificproperties.Line', {
    extend: 'Ext.panel.Panel',
    requires: ['CMDBuildUI.util.helper.FieldsHelper'],
    alias: 'widget.administration-processes-geoattributes-specificproperties-line',
    layout: 'fit',
    config: {

    },
    viewModel: {

    },
    items: [{
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
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokedashstyle,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokedashstyle'
                },
                name: 'strokeDashstyle',
                bind: {
                    value: '{theGeoAttribute.style.strokeDashstyle}'
                },
                renderer: function (value) {
                    if (value) {
                        return value.toUpperCase();
                    }
                    return value;
                }
            }, {
                columnWidth: 0.5,
                xtype: 'fieldcontainer',

                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor'
                },
                layout: 'column',
                padding: '0 15 0 15',
                items: [{
                    xtype: 'image',
                    autoEl: 'div',
                    alt: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor,
                    localized: {
                        alt: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor'
                    },
                    columnWidth: 0.1,
                    cls: 'fa-2x x-fa fa-square',
                    style: {
                        lineHeight: '32px'
                    },
                    bind: {
                        style: {
                            color: '{theGeoAttribute.style.strokeColor}'
                        }
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokeopacity,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokeopacity'
                },
                name: 'strokeDashstyle',
                bind: {
                    value: '{theGeoAttribute.style.strokeOpacity}'
                },
                renderer: function (value) {
                    return (value * 100) + '%';
                }
            }, {
                columnWidth: 0.5,
                xtype: 'displayfield',
                padding: '0 15 0 15',
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokewidth,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokewidth'
                },
                name: 'strokeWidth',
                bind: {
                    value: '{theGeoAttribute.style.strokeWidth}'
                }
            }]
        }]
    }, {
        // edit
        xtype: 'container',
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
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokedashstyle,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokedashstyle'
                },
                name: 'strokeDashstyle',
                bind: {
                    value: '{theGeoAttribute.style.strokeDashstyle}',
                    store: '{strokeDashStyleStore}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'fieldcontainer',

                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor'
                },
                layout: 'column',
                padding: '0 15 0 0',
                items: [{
                    columnWidth: 0.8,
                    xtype: 'cmdbuild-colorpicker',
                    bind: {
                        value: '{theGeoAttribute.style.strokeColor}'
                    },
                    triggers: {
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: function (field) {
                                field._ownerRecord.set('strokeColor', null);
                                field.reset();
                            }
                        }
                    }
                }, {
                    xtype: 'image',
                    autoEl: 'div',
                    columnWidth: 0.2,
                    alt: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor,
                    localized: {
                        alt: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor'
                    },
                    cls: 'fa-2x x-fa fa-square',
                    style: {
                        lineHeight: '32px'
                    },
                    bind: {
                        style: {
                            color: '{theGeoAttribute.style.strokeColor}'
                        }
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [
                CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                    columnWidth: 0.5,
                    padding: '0 15 0 0',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokeopacity,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokeopacity'
                    },
                    name: 'strokeOpacity',
                    increment: 0.01,
                    minValue: 0,
                    maxValue: 1,
                    multiplier: 100,
                    inputDecimalPrecision: 0,
                    sliderDecimalPrecision: 2,
                    showPercentage: true,
                    bind: {
                        value: '{theGeoAttribute.style.strokeOpacity}'
                    }
                }), {
                    columnWidth: 0.5,
                    xtype: 'numberfield',
                    minValue: 0,
                    step: 1,
                    decimalPrecision: 0,

                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokewidth,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokewidth'
                    },
                    name: 'strokeWidth',
                    bind: {
                        value: '{theGeoAttribute.style.strokeWidth}'
                    }
                }
            ]
        }]
    }]
});