Ext.define('CMDBuildUI.model.thematisms.LegendModel', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'value',
        type: 'string'
    }, {
        name: 'viewValue',
        type: 'string'
    },{
        name: 'color',
        type: 'string'
    }, {
        name: 'count',
        type: 'number',
        mapping: '_count'
    }]
});