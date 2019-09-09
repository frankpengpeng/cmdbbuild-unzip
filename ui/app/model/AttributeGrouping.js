Ext.define('CMDBuildUI.model.AttributeGrouping', {
    extend: 'Ext.data.Model',
    
    fields: [{
        name: 'name',
        type: 'string',
        defaultValue: ''
    },{
        name: 'description',
        type: 'string',
        defaultValue: ''
    }, {
        name: 'index',
        type: 'number',
        defaultValue: null
    }],
    proxy: {
        type: 'memory'
    }
});
