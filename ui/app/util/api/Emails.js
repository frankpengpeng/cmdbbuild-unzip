Ext.define('CMDBuildUI.util.api.Emails', {
    singleton: true,

    /**
     * 
     * @return {String}
     */
    getTemplatesUrl: function() {
        return '/email/templates/';
    },

    /**
     * 
     * @return {String}
     */
    getAccountsUrl: function() {
        return '/email/accounts/';
    },

    /**
     * 
     * @param {String} className 
     * @param {Number} cardId 
     * @return {String}
     */
    getCardEmailsUrl: function(className, cardId) {
        return Ext.String.format(
            '/classes/{0}/cards/{1}/emails',
            className,
            cardId
        );
    },

    /**
     * 
     * @param {String} processName 
     * @param {Number} instanceId
     * @return {String}
     */
    getProcessInstanceEmailsUrl: function(processName, instanceId) {
        return Ext.String.format(
            '/processes/{0}/instances/{1}/emails',
            processName,
            instanceId
        );
    }
});