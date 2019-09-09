Ext.define('CMDBuildUI.model.domains.Clone', {
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
            if (record.get("ignore")) {
                v = CMDBuildUI.model.base.Filter.cloneFilters.ignore;
            } else if (record.get("migrates")) {
                v = CMDBuildUI.model.base.Filter.cloneFilters.migrates;
            } else if (record.get("clone")) {
                v = CMDBuildUI.model.base.Filter.cloneFilters.clone;
            }
            return v;
        }
    }, {
        name: 'ignore',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.model.base.Filter.cloneFilters.ignore;
        }
    }, {
        name: 'migrates',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.model.base.Filter.cloneFilters.migrates;
        }
    }, {
        name: 'clone',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.model.base.Filter.cloneFilters.clone;
        }
    }, {
        name: 'cards',
        type: 'auto'
    }]
});
