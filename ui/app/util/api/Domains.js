Ext.define('CMDBuildUI.util.api.Domains', {
    singleton: true,

    /**
     * Get class attributes
     * 
     * @param {String} className
     * @return {String} The url for api resourcess
     */
    getAttributes: function (domainName) {
        return Ext.String.format(
            '/domains/{0}/attributes',
            domainName
        );
    }
});