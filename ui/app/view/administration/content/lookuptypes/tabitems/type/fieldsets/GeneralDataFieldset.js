
Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.type.fieldsets.GeneralDataFieldset',{
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-lookuptypes-tabitems-type-fieldsets-generaldatafieldset',

    viewModel: {
      
    },
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 0.5,
            items: [
                /********************* Name **********************/
                {
                    // create / edit
                    xtype: 'textfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.lookuptypes.type.form.fieldsets.generalData.inputs.name.label,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.lookuptypes.type.form.fieldsets.generalData.inputs.name.label'
                    },
                    name: 'name',
                    hidden: true,
                    bind: {
                        value: '{theLookupType.name}',
                        hidden: '{actions.view}'
                    }
                },
                {
                    // view
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.lookuptypes.type.form.fieldsets.generalData.inputs.name.label,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.lookuptypes.type.form.fieldsets.generalData.inputs.name.label'
                    },
                    name: 'name',
                    hidden: true,
                    bind: {
                        value: '{theLookupType.name}',
                        hidden: '{!actions.view}'
                    }
                },
                /********************* Name **********************/
                {
                    // create / edit
                    xtype: 'combobox',
                    editable: false,
                    reference: 'parentField',
                    displayField: 'name',
                    valueField: 'name',
                    name: 'parent',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.lookuptypes.type.form.fieldsets.generalData.inputs.parent.label,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.lookuptypes.type.form.fieldsets.generalData.inputs.parent.label'
                    },
                    hidden: true,
                    bind: {
                        store:{
                            model: "CMDBuildUI.model.lookups.LookupType",
                            proxy: '{allLookupTypeProxy}',
                
                            pageSize: 0, // disable pagination
                            fields: ['id', 'name'],
                            autoLoad: true,
                            sorters: [
                                'name'
                            ]
                        },
                        value: '{theLookupType.parent}',
                        hidden: '{actions.view}',
                        disabled: '{actions.edit}'
                    }
                }, {
                    // view
                    xtype: 'displayfield',
                    name: 'parent',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.lookuptypes.type.form.fieldsets.generalData.inputs.parent.label,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.lookuptypes.type.form.fieldsets.generalData.inputs.parent.label'
                    },
                    hidden: true,
                    bind: {
                        value: '{theLookupType.parent}',
                        //hidden: '{hideParentDisplayfield}',
                        hidden: '{!actions.view}'
                    }
                }
            ]
        }]
    }]
});
