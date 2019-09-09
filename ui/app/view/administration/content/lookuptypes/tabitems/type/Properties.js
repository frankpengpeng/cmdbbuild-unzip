Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.type.Properties', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.type.PropertiesController'
    ],

    alias: 'widget.administration-content-lookuptypes-tabitems-type-properties',
    controller: 'administration-content-lookuptypes-tabitems-type-properties',
    autoScroll: false,
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    layout: 'border',
    items: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({delete: true}, 'lookuptype')
    }, {
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: [{
            xtype: 'administration-content-lookuptypes-tabitems-type-fieldsets-generaldatafieldset'
        }]
    }],
    
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