
Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.ProcessParamentersFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-processparametersfieldset',
    viewModel: {},
    bind: {
        hidden: '{actions.add}'
    },
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.title,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.title'
        },
        ui: 'administration-formpagination',
        collapsible: true,
        items: [{
            columnWidth: 0.5,
            items: [
                {
                /********************* description **********************/
                // create / edit
                xtype: 'combo',  
                typeAhead:true,
                queryMode: 'local',
                reference: 'flowStatusAttr',
                displayField: 'description',
                valueField: '_id',
                name: 'flowStatusAttr',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.flowStatusAttr.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.flowStatusAttr.label'
                },
                hidden: true,
                bind: {
                    store: '{allLookupAttributesStore}',
                    value: '{theProcess.flowStatusAttr}',
                    hidden: '{actions.view}'
                }
            }, {
                // view
                xtype: 'displayfield',
                name: 'flowStatusAttr',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.flowStatusAttr.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.flowStatusAttr.label'
                },
                hidden: true,
                bind: {
                    value: '{theProcess.flowStatusAttr}', // TODO: this work when #619 is fixed
                    hidden: '{!actions.view}'
                }
            }, {
                /********************* Default Filter **********************/
                // create / edit
                xtype: 'combobox',
                editable: false,
                reference: 'defaultFilter',
                displayField: 'name',
                valueField: '_id',
                name: 'defaultFilter',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.defaultFilter.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.defaultFilter.label'
                },
                hidden: true,
                bind: {
                    store: '{defaultFilterStore}',
                    value: '{theProcess.defaultFilter}',
                    hidden: '{actions.view}'
                }

            }, {
                // view
                xtype: 'displayfield',
                name: 'defaultFilter',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.defaultFilter.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.defaultFilter.label'
                },
                hidden: true,
                bind: {
                    value: '{theProcess.defaultFilter}', // TODO: this work when #619 is fixed
                    hidden: '{!actions.view}'
                }
            }]
        }, {
            columnWidth: 0.5,
            style: {
                paddingLeft: '15px'
            },
            items: [{
                /********************* messageAttr **********************/
                // create / edit
                xtype: 'combobox',
                typeAhead:true,
                queryMode: 'local',
                reference: 'messageAttr',
                displayField: 'description',
                valueField: '_id',
                name: 'messageAttr',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.messageAttr.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.messageAttr.label'
                },
                hidden: true,
                bind: {
                    store: '{allAttributesStore}',
                    value: '{theProcess.messageAttr}',
                    hidden: '{actions.view}'
                }
            }, {
                // view
                xtype: 'displayfield',
                name: 'messageAttr',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.messageAttr.label,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.processParameter.inputs.messageAttr.label'
                },
                hidden: true,
                bind: {
                    value: '{theProcess.messageAttr}',  // TODO: this work when #619 #621 is fixed
                    hidden: '{!actions.view}'
                }
            }]
        }]
    }]
});
