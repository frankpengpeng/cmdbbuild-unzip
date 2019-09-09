Ext.define('CMDBuildUI.util.api.Views', {
    singleton: true,

    /**
     * 
     * @param {String} viewName
     * @param {String} extension
     */
    getPrintItemsUrl: function (viewName, extension) {
        return Ext.String.format(
            "{0}/views/{1}/print/{1}.{2}",
            CMDBuildUI.util.Config.baseUrl,
            viewName,
            extension
        );
    }
});