Ext.define('CMDBuildUI.model.views.View', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        types: {
            sql: 'SQL',
            filter: 'FILTER'
        }
    },

    fields: [{
        name: 'name',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'description',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'filter',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'sourceClassName',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'sourceFunction',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'type',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: true
    }],

    idProperty: '_id',

    proxy: {
        url: '/views/',
        type: 'baseproxy'
    },

    /**
     * Get translated description
     * @return {String}
     */
    getTranslatedDescription: function () {
        return this.get("_description_translation") || this.get("description");
    },

    /**
     * Get object for menu
     * @return {String}
     */
    getObjectTypeForMenu: function () {
        return this.get('name');
    }
});
