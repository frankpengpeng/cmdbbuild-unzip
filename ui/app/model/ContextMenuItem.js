Ext.define('CMDBuildUI.model.ContextMenuItem', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        types: {
            custom: 'custom',
            component: 'component',
            separator: 'separator'
        },
        visibilities: {
            all: 'all',
            many: 'many',
            one: 'one'
        }
    },

    fields: [{
        name: 'label',
        type: 'string',
        defaultValue: ""
    }, {
        name: 'type',
        type: 'string',
        defaultValue: "custom" //"custom"/"component"/"separator",
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true
    }, {
        name: 'visibility',
        type: 'string',
        defaultValue: 'all'  //"one"/"many"/"all",
    }, {
        //only if type==component
        name: 'componentId',
        type: 'string'
    }, {
        //only if type==custom
        name: 'script',
        type: 'string'
    }, {
        //only if type==component
        name: 'config',
        type: 'string'
    }, {
        name: 'separator',
        type: 'boolean',
        calculate: function (data) {
            return data.type === 'separator';
        }
    }, {
        name: '_isComponent',
        type: 'boolean',
        defaultValue: false,
        calculate: function (data) {
            return (data.type == 'component') ? true : false;
        }
    }],
    proxy: {
        type: 'memory'
    }
});
