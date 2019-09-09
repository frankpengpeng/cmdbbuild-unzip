Ext.define('CMDBuildUI.util.api.Functions', {
    singleton: true,

    /**
     * Get function by name
     * @param {String} functionName
     * @return {String}
     */
    getFunctionByNameUrl: function(functionName) {
        return Ext.String.format(
            "/functions/{0}",
            functionName
        );
    },

    /**
     * Get function by name
     * @param {String} functionName
     * @return {String}
     */
    getFunctionOutputsByNameUrl: function(functionName) {
        return Ext.String.format(
            "/functions/{0}/outputs",
            functionName
        );
    }

});