Ext.define('CMDBuildUI.util.api.DomainTrees', {
    singleton: true,

    /**
     * Get class geo attributes
     * 
     * @param {String} className
     * @return {String} The url for api resources
     */
    getDomainTrees: function() {
        return "/domainTrees";
    },

    getGisDomainTree: function(){
        return "/domainTrees/gisnavigation";
    }

});