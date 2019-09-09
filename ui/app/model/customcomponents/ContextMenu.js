
Ext.define('CMDBuildUI.model.customcomponents.ContextMenu', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'name',
        type: 'string',
        critical: true
    }, {
        name: 'description',
        type: 'string',
        critical: true
    }, {
        name: 'alias',
        type: 'string',
        critical: true
    }, {
        name: 'componentId',
        type: 'string',
        critical: true
    },{
        name: 'active',
        type: 'boolean',
        critical: true,
        defaultValue: true
    }
],

    proxy: {
        url: '/components/contextmenu',
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
