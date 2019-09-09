
Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.ValididationFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-valididationfieldset',

    items: [{
        xtype: 'fieldset',
        collapsible: true,
        collapsed: true,
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.validation.title,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.validation.title'
        },
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 0.5,
            items: [
                {
                xtype: 'aceeditortextarea',
                allowBlank: true,
                vmObjectName: 'theObject',
                inputField: 'validationRule',
                options:{
                    readOnly: true
                },
                bind: {
                    hidden:'{!actions.view}',
                    value: '{theObject.validationRule}',
                    readOnly: true,
                    options:{ readOnly:'{actions.view}' }
                   
                },
                viewModel: {},
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.validation.inputs.validationRule.label,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.validation.inputs.validationRule.label'
                },
                name: 'validationRule',
                width: '95%'
            },{
                xtype: 'aceeditortextarea',
                allowBlank: true,
                vmObjectName: 'theObject',
                inputField: 'validationRule',
                options:{
                    readOnly: false
                },
                bind: {
                    hidden:'{actions.view}',
                    value: '{theObject.validationRule}',
                    config: { readOnly: '{actions.view}' }
                },
                viewModel: {},
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.validation.inputs.validationRule.label,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.validation.inputs.validationRule.label'
                },
                name: 'validationRule',
                width: '95%'
            }
        ]
        }]
    }]
});
