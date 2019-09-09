Ext.define('CMDBuildUI.model.Dashboard', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'name',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'charts',
        type: 'auto'
    }, {
        name: 'columns',
        type: 'auto'
    }, {
        name: 'groups',
        type: 'auto'
    }],

    proxy: {
        url: '/dashboards/',
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
