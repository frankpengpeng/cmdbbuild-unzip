Ext.define('CMDBuildUI.util.api.Lookups', {
    singleton: true,

    /**
     * Get lookup values for give type
     * @param {String} type
     */
    getLookupValues: function(type) {
        return Ext.String.format(
            "/lookup_types/{0}/values/",
            CMDBuildUI.util.Utilities.stringToHex(type)
        );
    },
    getLookupTypes: function(){
        return "/lookup_types";
    },
     
    /**
     * Get class report for give type
     * @param {String} extension The file extension (PDF|ODT)
     * @param {String} className
     */
    getLookupTypeReport: function (extension, className) {
        //TODO: check rigth url #491
        var uri = Ext.String.format(
            '');

        return encodeURI(uri);
    }

});