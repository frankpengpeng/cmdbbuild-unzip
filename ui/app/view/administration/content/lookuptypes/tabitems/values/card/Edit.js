Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.Edit', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-lookuptypes-tabitems-values-card-edit',

    requires: [
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.EditController',
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.EditModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],
    controller: 'view-administration-content-lookuptypes-tabitems-values-card-edit',
    viewModel: {
        type: 'view-administration-content-lookuptypes-tabitems-values-card-edit'
    },
    bubbleEvents: [
        'itemupdated',
        'cancelupdating'
    ],
    modelValidation: true,
    config: {
        theValue: null
    },
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
                name: 'code',
                disabled: true,
                bind: {
                    value: '{theValue.code}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textfield',
                bind: {
                    value: '{theValue.description}'
                },
                allowBlank: false,
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description',
                    labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
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
                    disabled: '{!theValue.parent_type}',
                    store: '{parentLookupsStore}',
                    listeners: {
                        change: function (combobox, newValue, oldValue) {
                            var vm = this.lookupViewModel();
                            var parentStore = vm.getStore('parentLookupsStore').getData();
                            var parentValue = parentStore.filterBy('_id', newValue);
                            if (parentValue.items.length) {
                                vm.set('theValue.parent_description', parentValue.items[0].get('description'));
                            }
                        }
                    }
                }
            }, {
                columnWidth: 0.5,
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.lookuptypes.strings.textcolor,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.lookuptypes.strings.textcolor'
                },
                layout: 'column',
                msgTarget: 'qtip',
                items: [{
                    columnWidth: 0.8,
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
                    alt: CMDBuildUI.locales.Locales.administration.common.labels.colorpreview,
                    localized:{
                        alt: 'CMDBuildUI.locales.Locales.administration.common.labels.colorpreview'
                    },
                    columnWidth: 0.2,
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
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.note,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.note'
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
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
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
        collapsible: true,
        title: "Icon", // TODO: translate
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
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
                hidden: '{!iconTypeIsImage}'
            },
            items: [{
                columnWidth: 0.5,
                xtype: 'filefield',
                itemId: 'lookupValueImage',
                ui: 'administration-action-small',
                name: 'iconImage',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.strings.image,
                buttonText: CMDBuildUI.locales.Locales.administration.common.strings.selectimage,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.strings.image',
                    buttonText: 'CMDBuildUI.locales.Locales.administration.common.strings.selectimage'
                },
                labelCls: Ext.baseCSSPrefix + 'form-item-label-default',
                reference: 'iconImage',
                accept: 'image/png',
                listeners: {
                    change: 'onFileChange' // no scope given here    
                },
                bind: {
                    hidden: '{!actions.add}'
                }
            }]
        }, {
            layout: 'column',
            liquidLayout: true,
            userCls: 'img-container',

            border: false,
            bind: {
                hidden: '{!iconTypeIsImage}'
            },
            style: {
                borderWidth: 0
            },
            items: [{
                xtype: 'image',
                reference: 'lookupValueImage',
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
                    borderWidth: 0,
                    maxWidth: '64px',
                    masHeight: '64px'
                },
                liquidLayout: true,
                border: false
            }]
        }, {
            layout: 'column',
            bind: {
                hidden: '{!iconTypeIsFont}'
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
                    columnWidth: 0.8,
                    xtype: 'cmdbuild-fapicker',
                    itemId: 'lookupValueIconFont',
                    allowBlank: false,
                    msgTarget: 'qtip',
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
                    localized: {
                        alt: 'CMDBuildUI.locales.Locales.administration.common.labels.iconpreview'
                    },
                    columnWidth: 0.2,
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
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.iconcolor'
                },
                layout: 'column',
                items: [{
                    columnWidth: 0.8,
                    xtype: 'cmdbuild-colorpicker',
                    itemId: 'lookupValueIconColor',
                    msgTarget: 'qtip',
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
                    localized: {
                        alt: 'CMDBuildUI.locales.Locales.administration.common.labels.colorpreview'
                    },
                    columnWidth: 0.2,
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