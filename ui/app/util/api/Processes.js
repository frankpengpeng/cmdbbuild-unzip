Ext.define('CMDBuildUI.util.api.Processes', {
    singleton: true,

    /**
     * Get all instances activities url
     * 
     * @param {String} processName
     * @return {String} The url for api resources
     */
    getAllInstancesActivitiesUrl: function (processName) {
        return Ext.String.format(
            "/processes/{0}/instance_activities",
            processName
        );
    },

    /**
     * Get the url for process start activities
     * 
     * @param {String} processName
     * @param {Numeric} instanceId
     * @return {String} The url for api resources
     */
    getStartActivitiesUrl: function (processName) {
        return Ext.String.format(
            "/processes/{0}/start_activities",
            processName
        );
    },

    /**
     * Get the url for process instances
     * 
     * @param {String} processName
     * @return {String} The url for api resources
     */
    getInstancesUrl: function (processName) {
        return Ext.String.format(
            "/processes/{0}/instances/{1}",
            processName
        );
    },

    /**
     * Get the url for available activities for an instance
     * 
     * @param {String} processName
     * @param {Numeric} instanceId
     * @return {String} The url for api resources
     */
    getInstanceActivitiesUrl: function (processName, instanceId) {
        return Ext.String.format(
            "/processes/{0}/instances/{1}/activities",
            processName,
            instanceId
        );
    },

    /**
     * Get process template
     * @param {String} processName
     */
    getTemplateFileUrl: function (processName) {
        var uri = Ext.String.format(
            '{0}/processes/{1}/template',
            CMDBuildUI.util.Config.baseUrl,
            processName);
        return encodeURI(uri);

    },

    /**
     * Get process version file
     * @param {String} processName
     * @param {String} versionId
     */
    getVersionFileUrl: function (processName, versionId) {
        var uri = Ext.String.format(
            '{0}/processes/{1}/versions/{2}/file',
            CMDBuildUI.util.Config.baseUrl,
            processName,
            versionId);
        return encodeURI(uri);

    },

    /**
     * 
     * @param {String} processName 
     * @param {Number} instanceId 
     */
    getProcessInstanceRelations: function (processName, instanceId) {
        return Ext.String.format("/processes/{0}/instances/{1}/relations", processName, instanceId);
    },

    /**
     * 
     * @param {String} processName
     * @param {String} extension
     */
    getPrintInstancesUrl: function (processName, extension) {
        return Ext.String.format(
            "{0}/processes/{1}/print/{1}.{2}",
            CMDBuildUI.util.Config.baseUrl,
            processName,
            extension
        );
    },

    /**
     * Get process domains
     * 
     * @param {String} processName
     * @return {String} The url for api resourcess
     */
    getDomains: function (processName) {
        return Ext.String.format(
            "/processes/{0}/domains",
            processName
        );
    }
});