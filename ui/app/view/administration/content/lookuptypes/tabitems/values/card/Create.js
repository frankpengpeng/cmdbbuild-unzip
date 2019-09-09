Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.Create', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.CreateController',
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.EditModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],

    alias: 'widget.administration-content-lookuptypes-tabitems-values-card-create',
    controller: 'view-administration-content-lookuptypes-tabitems-values-card-create',
    viewModel: {
        type: 'view-administration-content-lookuptypes-tabitems-values-card-edit'
    },


    autoScroll: true,

    bubbleEvents: [
        'itemcreated',
        'cancelcreation'
    ],

    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,
    ui: 'administration-formpagination',
    items: [{
        ui: 'administration-formpagination',        
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        collapsible: true,
        xtype: "fieldset",
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.code,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.code'
                },
                allowBlank: false,
                name: 'code',
                bind: {
                    value: '{theValue.code}'
                },
                listeners: {
                    change:  function (input, newVal, oldVal) {
                        CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(input, newVal, oldVal,'[name="description"]');
                    }
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textfield',
                name: 'description',
                bind: {
                    value: '{theValue.description}'
                },
                allowBlank: false,
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                },
                labelToolIconCls: 'fa-flag',
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                labelToolIconClick: 'onTranslateClick'
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.lookuptypes.strings.parentdescription,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.lookuptypes.strings.parentdescription'
                },
                name: 'parentDescription',
                allowBlank: true,
                displayField: 'description',
                valueField: '_id',
                bind: {
                    value: '{theValue.parent_id}',
                    store: '{parentLookupsStore}',
                    disabled: '{isParentDescriptionDisabled}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.lookuptypes.strings.textcolor,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.lookuptypes.strings.textcolor'
                },
                layout: 'column',

                items: [{
                    columnWidth: 0.9,
                    xtype: 'cmdbuild-colorpicker',
                    bind: {
                        value: '{theValue.text_color}'
                    },
                    triggers: {
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: function (field) {
                                field._ownerRecord.set('text_color', null);
                                field.reset();
                            }
                        }
                    }
                }, {
                    xtype: 'image',
                    autoEl: 'div',
                    alt: CMDBuildUI.locales.Locales.administration.lookuptypes.strings.colorpreview,
                    localized:{
                        alt: 'CMDBuildUI.locales.Locales.administration.lookuptypes.strings.colorpreview'
                    },
                    columnWidth: 0.1,
                    cls: 'fa-2x x-fa fa-square',
                    style: {
                        lineHeight: '32px'
                    },
                    bind: {
                        style: {
                            color: '{theValue.text_color}'
                        }
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 1,
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.note,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.note'
                },
                name: 'note',
                bind: {
                    value: '{theValue.note}'
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.lookuptypes.type.form.fieldsets.generalData.inputs.active.label,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.lookuptypes.type.form.fieldsets.generalData.inputs.active.label'
                },
                name: 'active',
                bind: {
                    value: '{theValue.active}'
                }
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        title: CMDBuildUI.locales.Locales.administration.common.labels.icon,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
        },
        collapsible: true,
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.type,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
                },
                reference: 'iconType',
                name: 'iconType',
                allowBlank: true,
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                bind: {
                    value: '{theValue.icon_type}',
                    store: {
                        model: 'CMDBuildUI.model.base.ComboItem',
                        fields: ['value', 'label'],
                        autoLoad: true,
                        proxy: {
                            type: 'memory'
                        },
                        data: [{
                            'value': 'none',
                            'label': 'None' // TODO: translate
                        }, {
                            'value': 'font',
                            'label': 'Font' // TODO: translate
                        }, {
                            'value': 'image',
                            'label': 'Image' // TODO: translate
                        }]
                    }
                }
            }]
        }, {
            layout: 'column',
            bind: {
                hidden: '{valueIconType.isFontOrNone}'
            },
            items: [{
                columnWidth: 0.5,
                itemId: 'lookupValueImage',
                xtype: 'filefield',
                ui: 'administration-action-small',
                name: 'iconImage',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.strings.image,
                buttonText: CMDBuildUI.locales.Locales.selectimage,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.strings.image',
                    buttonText: 'CMDBuildUI.locales.Locales.administration.common.strings.selectimage'
                },
                labelCls: Ext.baseCSSPrefix + 'form-item-label-default',
                reference: 'iconImage',
                allowBlank: false,
                accept: 'image/png',
                validator: 'isInputFileRequired',
                listeners: {
                    change: 'onFileChange' // no scope given here    
                }
            }]
        }, {
            layout: 'column',
            liquidLayout: true,
            userCls: 'img-container',
            // width: 32,
            // height: 32,
            border: false,
            bind: {
                hidden: '{valueIconType.isFontOrNone}'
            },
            style: {
                borderWidth: 0
            },
            items: [{
                xtype: 'image',
                alt: CMDBuildUI.locales.Locales.administration.common.strings.iconimage,
                localized:{
                    alt: 'CMDBuildUI.locales.Locales.administration.common.strings.iconimage'
                },
                src: null,
                bind: {
                    src: '{theValue.icon_image}'
                },
                style: {
                    marginTop: '5px',
                    borderWidth: 0
                },
                liquidLayout: true,
                border: false,
                width: 256,
                heigth: 256
            }]
        }, {
            layout: 'column',
            bind: {
                hidden: '{valueIconType.isImageOrNone}'
            },
            items: [{
                columnWidth: 0.5,
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
                },
                layout: 'column',
                items: [{
                    columnWidth: 0.9,
                    xtype: 'cmdbuild-fapicker',
                    itemId: 'lookupValueIconFont',
                    allowBlank: false,
                    bind: {
                        value: '{theValue.icon_font}'
                    },
                    triggers: {
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: function (field) {
                                field._ownerRecord.set('icon_font', null);
                                field.reset();
                            }
                        }
                    }
                }, {
                    xtype: 'image',
                    autoEl: 'div',
                    alt: CMDBuildUI.locales.Locales.administration.common.labels.iconpreview,
                    localized:{
                        alt: 'CMDBuildUI.locales.Locales.administration.common.labels.iconpreview'
                    },
                    columnWidth: 0.1,
                    cls: 'fa-2x',
                    style: {
                        lineHeight: '32px'
                    },
                    bind: {
                        userCls: '{theValue.icon_font}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.iconcolor,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.iconcolor'
                },
                layout: 'column',

                items: [{
                    columnWidth: 0.9,
                    xtype: 'cmdbuild-colorpicker',
                    itemId: 'lookupValueIconColor',
                    value: '#30373D',
                    bind: {
                        value: '{theValue.icon_color}'
                    },
                    triggers: {
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: function (field) {
                                field._ownerRecord.set('icon_color', null);
                                field.reset();
                            }
                        }
                    }
                }, {
                    xtype: 'image',
                    autoEl: 'div',
                    alt: CMDBuildUI.locales.Locales.administration.common.labels.colorpreview,
                    localized:{
                        alt: 'CMDBuildUI.locales.Locales.administration.common.labels.colorpreview'
                    },
                    columnWidth: 0.1,
                    cls: 'fa-2x x-fa fa-square',
                    style: {
                        lineHeight: '32px'
                    },
                    bind: {
                        style: {
                            color: '{theValue.icon_color}'
                        }
                    }
                }]
            }]
        }]
    }],

    buttons: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
});