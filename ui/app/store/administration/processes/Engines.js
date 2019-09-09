Ext.define('CMDBuildUI.store.administration.processes.Engines', {
    extend: 'Ext.data.Store',  

    requires: ['CMDBuildUI.model.base.ComboItem'],
    
    model: 'CMDBuildUI.model.base.ComboItem',
    alias: 'store.processes-Engines',
    
    autoLoad: true,
    fields: ['value', 'label'],
    proxy: {
        type: 'memory'
    },
    data: [{
            'value': 'river',
            'label': 'Tecnoteca River'// TODO: translate
        }, {
            'value': 'shark',
            'label': 'Enhydra Shark' // TODO: translate
        } 
    ],

    pageSize: 0 // disable pagination

});