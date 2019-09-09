Ext.define('CMDBuildUI.util.helper.Configurations', {
    singleton: true,

    /**
     * @cfg {CMDBuildUI.model.Configuration} config
     * @private
     */
    _config: null,

    /**
     * @argument {Boolean} force
     * @return {Ext.promise.Promise} 
     */
    loadPublicConfs: function (force) {
        var deferred = new Ext.Deferred();

        if (!this.hasConfig() || force) {
            var me = this;
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getPublicConfigurationUrl(),
                callback: function (opts, success, response) {
                    if (response.responseText) {
                        var data = Ext.JSON.decode(response.responseText);
                        me.updateConfig(data.data);
                    }
                    CMDBuildUI.util.helper.SessionHelper.updateInstanceName(me.get(CMDBuildUI.model.Configuration.common.instancename));
                    CMDBuildUI.util.helper.SessionHelper.updateCompanyLogoId(me.get(CMDBuildUI.model.Configuration.common.companylogo));
                    CMDBuildUI.util.Ajax.updateAjaxTimeout();
                    deferred.resolve();
                }
            });
        } else {
            deferred.resolve();
        }
        return deferred.promise;
    },

    /**
     * @return {Ext.promise.Promise} 
     */
    loadSystemConfs: function () {
        var deferred = new Ext.Deferred();

        var me = this;
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getSystemConfigurationUrl(),
            callback: function (opts, success, response) {
                if (response.responseText) {
                    var data = Ext.JSON.decode(response.responseText);
                    me.updateConfig(data.data);
                }

                if (me.get(CMDBuildUI.model.Configuration.bim.enabled)) {
                    Ext.Ajax.request({
                        url: 'resources/js/viewer/BIMsurferCMDBuild.js',
                        callback: function (options, success, response) {
                            if (success && response.responseText) {
                                eval(response.responseText);
                            }
                        }
                    });
                }
                deferred.resolve();
            }
        });

        return deferred.promise;
    },

    /**
     * Return configuration
     * 
     * @param {String} configuration
     * @return {Object}
     */
    get: function (configuration) {
        return this.getConfigObject().get(configuration);
    },


    privates: {
        /**
         * @return {Boolean}
         */
        hasConfig: function () {
            return this._config !== null;
        },

        /**
         * @return {CMDBuildUI.model.Configuration}
         */
        getConfigObject: function () {
            if (!this.hasConfig()) {
                this._config = Ext.create("CMDBuildUI.model.Configuration");
            }
            return this._config;
        },

        /**
         * @param {Object} newdata
         */
        updateConfig: function (newdata) {
            if (!Ext.Object.isEmpty(newdata)) {
                var conf = this.getConfigObject();
                for (var key in newdata) {
                    conf.set(key, newdata[key]);
                }
            }
        }
    }
});