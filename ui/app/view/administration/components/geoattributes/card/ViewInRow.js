Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.components.geoattributes.card.ViewInRowController'
    ],
    alias: 'widget.administration-components-geoattributes-card-viewinrow',
    controller: 'administration-components-geoattributes-card-viewinrow',
    viewModel: {
        type: 'administration-components-geoattributes-card-form'
    },
    config: {
        theGeoAttribute: null,
        subtype: null,
        objectTypeName: null,
        objectType: null
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

        items: [{
            xtype: 'administration-components-geoattributes-card-fieldscontainers-generalproperties',
            bind: {
                actions: '{actions}'
            }
        }]
    }, {
        title: CMDBuildUI.locales.Locales.administration.geoattributes.strings.specificproperty,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.geoattributes.strings.specificproperty'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',
        items: [{
            xtype: 'administration-components-geoattributes-card-fieldscontainers-typeproperties',
            bind: {
                actions: {
                    edit: false,
                    view: true,
                    add: false
                }
            }
        }]
    }],
    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true,
        view: true,
        delete: true,
        clone: true,
        activeToggle: true
    }, 'geoattribute', 'theGeoAttribute')
});