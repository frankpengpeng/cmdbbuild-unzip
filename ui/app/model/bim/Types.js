Ext.define('CMDBuildUI.model.bim.Types', {
    extend: 'CMDBuildUI.model.base.Base',
    alias: 'store.bim.Types',
    fields: [{
        name: 'name', type: 'string'
    }, {
        name: 'qt', type: 'int'
    }, {
        name: 'clicks', type: 'int'
    }]
})