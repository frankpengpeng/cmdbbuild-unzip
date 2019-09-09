Ext.define('CMDBuildUI.model.domains.Filter', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'domain',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'destination',
        type: 'string'
    }, {
        name: 'destinationDescription',
        type: 'string'
    }, {
        name: 'mode',
        type: 'string',
        serialize: function (v, record) {
            if (record.get("noone")) {
                v = CMDBuildUI.model.base.Filter.relationstypes.noone;
            } else if (record.get("any")) {
                v = CMDBuildUI.model.base.Filter.relationstypes.any;
            } else if (record.get("oneof")) {
                v = CMDBuildUI.model.base.Filter.relationstypes.oneof;
            }
            return v;
        }
    }, {
        name: 'noone',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.model.base.Filter.relationstypes.noone;
        }
    }, {
        name: 'any',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.model.base.Filter.relationstypes.any;
        }
    }, {
        name: 'oneof',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.model.base.Filter.relationstypes.oneof;
        }
    }, {
        name: 'cards',
        type: 'auto'
    }]
});
