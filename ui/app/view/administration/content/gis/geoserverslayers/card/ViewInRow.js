Ext.define('CMDBuildUI.view.administration.content.gis.geoserverslayers.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.geoserverslayers.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.gis.geoserverslayers.card.ViewInRowModel'
    ],

    alias: 'widget.administration-content-gis-geoserverslayers-card-viewinrow',
    controller: 'administration-content-gis-geoserverslayers-card-viewinrow',
    viewModel: {
        type: 'administration-content-gis-geoserverslayers-card-viewinrow'
    },

    cls: 'administration',
    ui: 'administration-tabandtools',
    items: [{
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

        layout: 'column',
        defaults: {
            columnWidth: 0.5
        },
        items: [{
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                },
                name: 'name',
                align: 'left',
                bind: {
                    value: '{theLayer.name}'
                }
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                },
                name: 'geoerver_name',
                align: 'left',
                bind: {
                    value: '{theLayer.description}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.type,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.type'
                },
                name: 'type',
                align: 'left',
                bind: {
                    value: '{theLayer.type}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom'
                },
                name: 'zoomMin',
                align: 'left',
                bind: {
                    value: '{theLayer.zoomMin}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom'
                },
                name: 'zoomMax',
                align: 'left',
                bind: {
                    value: '{theLayer.zoomMax}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom'
                },
                name: 'zoomDef',
                align: 'left',
                bind: {
                    value: '{theLayer.zoomDef}'
                }
            },
            {
                xtype: 'checkbox',
                disabled: true,
                disabledCls: '',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                },
                name: 'active',
                bind: {
                    value: '{theLayer.active}'
                }
            }
        ]

    }, {
        title: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.gis.associatedcard'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

        layout: 'column',
        defaults: {
            columnWidth: 0.5
        },
        items: [{
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedclass,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.associatedclass'
                },
                name: 'owner_type',
                align: 'left',
                bind: {
                    value: '{theLayer.owner_type}'
                }
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.associatedcard'
                },
                name: 'associatedCard',
                align: 'left',
                bind: {
                    value: '{theLayer.owner_id}'
                },
                renderer: function () {
                    return this.lookupViewModel().get('cardDescription');
                }
            }
        ]
    }],
    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true,
        view: true,
        delete: true,
        clone: true,
        activeToggle: true
    }, 'gislayer', 'theLayer')
});