Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.EditModel',
        'Ext.layout.*'
    ],
    autoDestroy: true,
    alias: 'widget.administration-content-lookuptypes-tabitems-values-card-viewinrow',
    controller: 'administration-content-lookuptypes-tabitems-values-card-viewinrow',
    viewModel: {
        type: 'view-administration-content-lookuptypes-tabitems-values-card-edit'
    },
    cls: 'administration',
    ui: 'administration-tabandtools',
    config: {
        objectTypeName: null,
        objectId: null,
        shownInPopup: false
    },
    items: [{
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.code,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.code'
                },
                name: 'code',
                bind: {
                    value: '{theValue.code}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description',
                    labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                },
                name: 'description',
                bind: {
                    value: '{theValue.description}'
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.lookuptypes.strings.textcolor,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.lookuptypes.strings.textcolor'
                },
                name: 'textColor',
                bind: {
                    value: '<i class="fa fa-square fa-2x" style="color:{theValue.text_color}"></i>'
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.lookuptypes.strings.parentdescription,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.lookuptypes.strings.parentdescription'
                },
                name: 'parent_description',
                bind: {
                    value: '{parentDescription}'
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 1,
                xtype: 'displayfield',
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
                readOnly: true,
                bind: {
                    value: '{theValue.active}'
                }
            }]
        }]
    }, {
        xtype: "fieldset",
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.common.labels.icon,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
        },
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icontype,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icontype'
                },
                name: 'iconType',
                bind: {
                    value: '{theValue.icon_type}'
                },
                renderer: function (value) {
                    if (value) {
                        switch (value) {
                            case 'none':
                                return CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none;
                            case 'font':
                                return CMDBuildUI.locales.Locales.administration.common.labels.icon;
                            case 'image':
                                return CMDBuildUI.locales.Locales.administration.common.strings.image;
                        }
                    }
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

            items: [{
                columnWidth: 0.5,

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
                    maxHeight: '64px',
                    maxWidth: '64px',
                    minHeight: '32px'
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
                padding: '0 0 0 0',
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
                },
                layout: 'column',
                items: [{
                    xtype: 'image',
                    autoEl: 'div',
                    alt: CMDBuildUI.locales.Locales.administration.common.labels.iconpreview,
                    localized: {
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
                padding: '0 0 0 0',
                columnWidth: 0.5,
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.iconcolor,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.iconcolor'
                },
                layout: 'column',

                items: [{
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
    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true,
        view: true,
        delete: true,
        activeToggle: true
    }, 'lookupvalue', 'theValue')
});