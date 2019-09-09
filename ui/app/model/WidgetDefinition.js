Ext.define('CMDBuildUI.model.WidgetDefinition', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: '_type',
        type: 'string'
    }, {
        name: '_active',
        type: 'boolean',
        defaultValue: true
    }, {
        name: '_label',
        type: 'string'
    }, {
        name: '_output',
        type: 'string'
    }, {
        name: '_required',
        type: 'boolean'
    }, {
        name: '_config',
        type: 'string'
    }, {
        name: '_alwaysenabled',
        type: 'boolean'
    }],

    proxy:{
        type:'memory'
    }
});
