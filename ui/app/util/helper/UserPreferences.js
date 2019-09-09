Ext.define('CMDBuildUI.util.helper.UserPreferences', {
    singleton: true,

    /**
     * Load user preferences
     * 
     * @return {Ext.promise.Promise} Resolve method has as argument an 
     *      instance of {CMDBuildUI.store.users.Preferences}. 
     *      Reject method has as argument a {String} containing error message.
     */
    load: function () {
        var me = this;
        // create deferred instance
        var deferred = new Ext.Deferred();

        // load preferences
        CMDBuildUI.model.users.Preference.load('preferences', {
            callback: function (record, operation, success) {
                if (success) {
                    me._preferences = record;
                    deferred.resolve(record);
                } else {
                    deferred.reject(operation);
                }
            }
        });

        // returns promise
        return deferred.promise;
    },

    /**
     * @return {CMDBuildUI.store.users.Preferences} Returns null if has not preferences.
     */
    getPreferences: function () {
        return this._preferences;
    },
    
    /**
     * Get user preference
     * @param {String} property 
     * @return {String|Number|Boolean|Object}
     */
    get: function(property) {
        return this._preferences.get(property);
    },

    /**
     * Get thousands separator character
     * @return {String}
     */
    getThousandsSeparator: function () {
        if (!this.formats.thousandsSeparator) {
            this.formats.thousandsSeparator = this.get(CMDBuildUI.model.users.Preference.thousandsSeparator) ||
                CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.thousandsSeparator);
        }
        return this.formats.thousandsSeparator;
    },

    /**
     * Get decimals separator character
     * @return {String}
     */
    getDecimalsSeparator: function () {
        if (!this.formats.decimalsSeparator) {
            this.formats.decimalsSeparator = this.get(CMDBuildUI.model.users.Preference.decimalsSeparator) ||
                CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.decimalsSeparator);
        }
        return this.formats.decimalsSeparator;
    },
    
    /**
     * Get dates format
     * @return {String}
     */
    getDateFormat: function() {
        if (!this.formats.date) {
            this.formats.date = this.get(CMDBuildUI.model.users.Preference.dateFormat) ||
                CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.dateFormat) ||
                CMDBuildUI.locales.Locales.common.dates.date;
        }
        return this.formats.date;
    },
    
    /**
     * Get dates format
     * @return {String}
     */
    getTimeWithSecondsFormat: function() {
        if (!this.formats.timeWithSeconds) {
            this.formats.timeWithSeconds = this.get(CMDBuildUI.model.users.Preference.timeFormat) ||
                CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.timeFormat) ||
                CMDBuildUI.locales.Locales.common.dates.time;
        }
        return this.formats.timeWithSeconds;
    },
    
    /**
     * Get dates format
     * @return {String}
     */
    getTimeWithoutSecondsFormat: function() {
        if (!this.formats.timeWithoutSeconds) {
            this.formats.timeWithoutSeconds = this.getTimeWithSecondsFormat().replace(":s", "");
        }
        return this.formats.timeWithoutSeconds;
    },
    
    /**
     * Get dates format
     * @return {String}
     */
    getTimestampWithSecondsFormat: function() {
        if (!this.formats.timestampWithSeconds) {
            this.formats.timestampWithSeconds = this.getDateFormat() + " " + this.getTimeWithSecondsFormat();
        }
        return this.formats.timestampWithSeconds;
    },
    
    /**
     * Get dates format
     * @return {String}
     */
    getTimestampWithoutSecondsFormat: function() {
        if (!this.formats.timestampWithoutSeconds) {
            this.formats.timestampWithoutSeconds = this.getDateFormat() + " " + this.getTimeWithoutSecondsFormat();
        }
        return this.formats.timestampWithoutSeconds;
    },

    privates: {
        /**
         * @property {CMDBuildUI.model.users.Preference} _preferences
         * Object containing user preferences
         */
        _preferences: null,

        /**
         * An object containing all formats.
         */
        formats: {
            thousandsSeparator: null,
            decimalsSeparator: null,
            date: null,
            timeWithSeconds: null,
            timeWithoutSeconds: null,
            timestampWithSeconds: null,
            timestampWithoutSeconds: null
        }
    }
});