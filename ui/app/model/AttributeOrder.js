Ext.define('CMDBuildUI.model.AttributeOrder', {
    extend: 'Ext.data.Model',
    
    fields: [{
        name: 'attribute',
        type: 'string',
        defaultValue: ''
    }, {
        name: 'direction',
        type: 'string',
        defaultValue: ''
    }],
    proxy: {
        type: 'memory'
    }
});
