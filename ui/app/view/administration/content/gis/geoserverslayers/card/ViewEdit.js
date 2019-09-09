Ext.define('CMDBuildUI.view.administration.content.gis.geoserverslayers.card.ViewEdit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.geoserverslayers.card.ViewEditController',
        'CMDBuildUI.view.administration.content.gis.geoserverslayers.card.ViewEditModel'
    ],

    alias: 'widget.administration-content-gis-geoserverslayers-card-viewedit',
    controller: 'administration-content-gis-geoserverslayers-card-viewedit',
    viewModel: {
        type: 'administration-content-gis-geoserverslayers-card-viewedit'
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    cls: 'administration tab-hidden',
    ui: 'administration-tabandtools',


    items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            region: 'north',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true
            }, 'geoserver layers'),
            hidden: true,
            bind: {
                hidden: '{!actions.view}'
            }
        },
        {
            ui: 'administration-formpagination',
            xtype: 'container',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            items: [{
                ui: 'administration-formpagination',
                xtype: "fieldset",
                layout: 'column',
                collapsible: true,
                title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
                },
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput({
                        name: {
                            allowBlank: false,
                            bind: {
                                value: '{theLayer.name}'
                            }
                        }
                    }),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                        description: {
                            allowBlank: false,
                            bind: {
                                value: '{theLayer.description}'
                            }
                        }
                    }),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getTypeInput({
                        type: {
                            columnWidth: 0.5,
                            allowBlank: false,
                            bind: {
                                value: '{theLayer.type}',
                                store: '{typeStore}'
                            }
                        }
                    }),

                    {
                        xtype: 'filefield',                        
                        columnWidth: 0.5,
                        padding: '0 15 0 0',
                        name: 'file',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.gis.file,
                        allowBlank: false,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.file'
                        },                        
                        buttonConfig: {
                            ui: 'administration-secondary-action-small'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{actions.view}'
                        }

                    },

                    CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                        columnWidth: 0.5,
                        padding: '0 15 0 0',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.gis.defaultzoom,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.defaultzoom'
                        },
                        name: 'zoomDef',
                        increment: 1,
                        minValue: 0,
                        maxValue: 25,
                        // multiplier: 1, 
                        inputDecimalPrecision: 0,
                        sliderDecimalPrecision: 0,
                        bind: {
                            value: '{theLayer.zoomDef}'
                        }
                    }),

                    CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                        columnWidth: 0.5,
                        padding: '0 15 0 0',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.gis.minimumzoom,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.minimumzoom'
                        },
                        name: 'zoomMin',
                        increment: 1,
                        minValue: 0,
                        maxValue: 25,
                        // multiplier: 1, 
                        inputDecimalPrecision: 0,
                        sliderDecimalPrecision: 0,
                        bind: {
                            value: '{theLayer.zoomMin}'
                        }
                    }),

                    CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField({
                        columnWidth: 0.5,
                        padding: '0 15 0 0',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.gis.maximumzoom,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.maximumzoom'
                        },
                        name: 'zoomMax',
                        increment: 1,
                        minValue: 0,
                        maxValue: 25,
                        // multiplier: 1, 
                        inputDecimalPrecision: 0,
                        sliderDecimalPrecision: 0,
                        bind: {
                            value: '{theLayer.zoomMax}'
                        }
                    }),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                        active: {
                            disabledCls: '',
                            bind: {
                                value: '{theLayer.active}'
                            }
                        }
                    })
                ]
            }, {
                ui: 'administration-formpagination',
                xtype: "fieldset",
                layout: 'column',
                collapsible: true,
                title: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.gis.associatedcard'
                },
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getAssociatedClass({
                        associatedClass: {
                            allowBlank: false,
                            forceSelection: true,                            
                            bind: {
                                value: '{theLayer.owner_type}',
                                store: '{getAllClassesProcessesStore}'
                            }
                        }

                    }),
                    {
                        xtype: 'fieldcontainer',
                        layout: 'column',
                        columnWidth: 0.5,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.associatedcard'
                        },
                        items: [{
                            columnWidth: 1,
                            hidden: true,
                            xtype: 'displayfield',
                            bind: {
                                hidden: '{!actions.view}',
                                value: '{theLayer.cardDescription}'
                            }
                        }, {
                            columnWidth: 1,
                            hidden: true,
                            xtype: 'referencecombofield',
                            reference: 'owner_type',
                            allowBlank: false,
                            displayField: 'Description',
                            valueField: '_id',
                            metadata: '{theLayer.owner_type}',
                            bind: {
                                hidden: '{actions.view}',
                                value: '{theLayer.owner_id}',
                                store: '{getAssociatedCardsStore}'
                            }
                        }]
                    }
                ]
            }]
        }
    ],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }]
});