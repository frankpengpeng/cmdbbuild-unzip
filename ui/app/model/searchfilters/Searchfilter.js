Ext.define('CMDBuildUI.model.searchfilters.Searchfilter', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
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
        name: 'configuration',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'target',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'defaultGroups',
        type: 'auto',
        persist: true,
        critical: true
    }, {
        name: 'target',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'shared',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: true
    }, {
        name: 'active',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: true
    }],

    proxy: {
        type: 'baseproxy',
        url: '/classes/_ANY/filters',
        pageSize: 0
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
