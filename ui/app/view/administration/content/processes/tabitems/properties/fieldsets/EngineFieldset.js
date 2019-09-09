Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.EngineFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-enginefieldset',
    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        collapsible: true,
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.processes.strings.engine,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.processes.strings.engine'
        },
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 0.5,
            items: [{
                /********************* Category Lookup **********************/
                xtype: 'combobox',
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.fieldlabels.enginetype,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.fieldlabels.enginetype'
                },
                name: 'engine',
                allowBlank: false,
                hidden: true,
                editable: false,
                store: 'administration.processes.Engines',
                bind: {
                    value: '{theProcess.engine}',
                    hidden: '{actions.view}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.processes.fieldlabels.enginetype,
                localized:{
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.processes.fieldlabels.enginetype'
                },
                name: 'engine',
                hidden: true,
                bind: {
                    value: '{theProcess.engine}',
                    hidden: '{!actions.view}'
                },
                renderer: function (value) {
                    var enginesStore = Ext.getStore('administration.processes.Engines');
                    if (value) {
                        var engine = enginesStore.findRecord('value', value);
                        if(engine){
                            return engine.get('label');
                        }
                    }
                    return value;                    
                }
            }]
        }]
    }]
});