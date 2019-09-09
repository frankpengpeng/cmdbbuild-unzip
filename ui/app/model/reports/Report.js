Ext.define('CMDBuildUI.model.reports.Report', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        extensions: {
            csv: 'csv',
            odt: 'odt',
            pdf: 'pdf',
            rtf: 'rtf'
        }
    },

    fields: [{
        name: 'code',
        type: 'string',
        critical: true
    }, {
        name: 'description',
        type: 'string',
        critical: true
    },{
        name: 'active',
        type: 'boolean',
        critical: true,
        defaultValue: true
    }],

    proxy: {
        url: '/reports/',
        type: 'baseproxy'
    },

    hasMany: [{
        name: 'attributes',
        model: 'CMDBuildUI.model.Attribute'
    }],

    /**
     * Get translated description
     * @return {String}
     */
    getTranslatedDescription: function () {
        return this.get("_description_translation") || this.get("description");
    },

    /**
     * Get object for menu
     * @return {String}
     */
    getObjectTypeForMenu: function () {
        return this.get('code');
    },

    /**
     * Load attributes relation
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as paramenters attributes store and a boolean field.
     */
    getAttributes: function(force) {
        var deferred = new Ext.Deferred();
        var attributes = this.attributes();

        if (!attributes.isLoaded() || force) {
            // set attributes url
            attributes.getProxy().setUrl(CMDBuildUI.util.api.Reports.getReportAttributesUrlByReportId(this.getId()));
            // load store
            attributes.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(attributes, true);
                    }
                }
            });
        } else {
            // return promise
            deferred.resolve(attributes, false);
        }
        return deferred.promise;
    }

});
