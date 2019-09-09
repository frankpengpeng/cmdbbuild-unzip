Ext.define('CMDBuildUI.util.api.Common', {
    singleton: true,

    /**
     * Get boot status url
     * @return {String}
     */
    getBootStatusUrl: function () {
        return '/boot/status';
    },

    /**
     * Get boot status url
     * @return {String}
     */
    getApplyPatchesUrl: function () {
        return '/boot/patches/apply';
    },

    /**
     * Get configuration url
     * @return {String}
     */
    getPublicConfigurationUrl: function () {
        return '/configuration/public';
    },

    /**
     * Get configuration url
     * @return {String}
     */
    getSystemConfigurationUrl: function () {
        return '/configuration/system';
    },

    /**
     * Get filters url
     * 
     * @param {String} type 
     * @param {String} typename 
     */
    getFiltersUrl: function (type, typename) {
        var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(typename, type);
        return Ext.String.format("{0}{1}/filters", item.getProxy().getUrl(), item.getId());
    }
});