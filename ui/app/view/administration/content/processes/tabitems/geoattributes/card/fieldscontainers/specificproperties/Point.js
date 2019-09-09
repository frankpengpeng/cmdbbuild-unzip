Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.fieldscontainers.specificproperties.Point', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-processes-geoattributes-specificproperties-point',
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
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillopacity,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillopacity'
                },
                name: 'fillOpacity',
                bind: {
                    value: '{theGeoAttribute.style.fillOpacity}'
                },
                renderer: function (value) {
                    return (value * 100) + '%';
                }

            }, {
                columnWidth: 0.5,
                xtype: 'fieldcontainer',

                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor'
                },
                layout: 'column',
                padding: '0 15 0 15',
                items: [{
                    xtype: 'image',
                    autoEl: 'div',
                    alt: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor,
                    localized: {
                        alt: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor'
                    },
                    columnWidth: 0.1,
                    cls: 'fa-2x x-fa fa-square',
                    style: {
                        lineHeight: '32px'
                    },
                    bind: {
                        style: {
                            color: '{theGeoAttribute.style.fillColor}'
                        }
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.pointradius,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.pointradius'
                },
                name: 'pointRadius',
                bind: {
                    value: '{theGeoAttribute.style.pointRadius}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'fieldcontainer',                
                padding: '0 0 0 15',
                layout: 'column',

                items: [{
                    xtype: 'image',
                    columnWidth: 0.2,
                    height: 32,
                    width: 32,
                    alt: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                    tooltip: CMDBuildUI.locales.Locales.administration.common.strings.currenticon,
                    localized: {
                        alt: 'CMDBuildUI.locales.Locales.administration.common.labels.icon',
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.strings.currenticon'
                    },
                    floated: true,
                    style: 'margin-top: 30px',
                    reference: 'currentIconPreview',
                    config: {
                        theValue: null
                    },
                    bind: {
                        src: '{theGeoAttribute.style._iconPath}'                        
                    }
                }]
            }]
        }, {
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
            items: [

                CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                    columnWidth: 0.5,
                    padding: '0 15 0 0',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillopacity,
                    name: 'fillOpacity',
                    increment: 0.01,
                    minValue: 0,
                    maxValue: 1,
                    multiplier: 100,
                    inputDecimalPrecision: 0,
                    sliderDecimalPrecision: 2,
                    showPercentage: true,
                    bind: {
                        value: '{theGeoAttribute.style.fillOpacity}'
                    }
                }),
                CMDBuildUI.util.helper.FieldsHelper.getColorpickerField({
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor',
                        alt: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor'
                    },

                    alt: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor,
                    bind: {
                        value: '{theGeoAttribute.style.fillColor}'
                    }
                })
            ]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'numberfield',
                minValue: 0,
                step: 1,
                decimalPrecision: 0,
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.pointradius,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.pointradius'
                },
                name: 'pointRadius',
                bind: {
                    value: '{theGeoAttribute.style.pointRadius}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
                },
                layout: 'column',
                items: [{
                    columnWidth: 1,
                    xtype: 'filefield',
                    reference: 'iconFile',
                    listeners: {
                        change: 'onFileChange' // no scope given here    
                    },

                    emptyText: CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile,
                    localized:{
                        emptyText: 'CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile'
                    },

                    accept: '.png',
                    buttonConfig: {
                        ui: 'administration-secondary-action-small'
                    }
                }, {
                    xtype: 'image',
                    
                    height: 32,
                    width: 32,
                    alt: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                    tooltip: CMDBuildUI.locales.Locales.administration.common.strings.currenticon,
                    localized: {
                        alt: 'CMDBuildUI.locales.Locales.administration.common.labels.icon',
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.strings.currenticon'
                    },
                    itemId: 'geoAttributeIconPreview',
                    config: {
                        theValue: null
                    },
                    viewModel: {
                        data: {
                            theGeoAttribute: null,
                            vmKey: 'theGeoAttribute'
                        }
                    },
                    bind: {
                        src: '{theGeoAttribute.style._iconPath}'
                    }
                }]
            }]
        }, {
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
                },
                CMDBuildUI.util.helper.FieldsHelper.getColorpickerField({
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor,
                    alt: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor',
                        alt: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor'
                    },
                    bind: {
                        value: '{theGeoAttribute.style.strokeColor}'
                    }
                })
            ]
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