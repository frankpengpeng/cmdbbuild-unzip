Ext.define('CMDBuildUI.model.domains.Domain', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        cardinalities: {
            onetoone: '1:1',
            onetomany: '1:N',
            manytoone: 'N:1',
            manytomany: 'N:N'
        }
    },

    fields: [{
        name: 'name',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'description',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'source',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'sourceProcess',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'destination',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'destinationProcess',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'cardinality',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'descriptionDirect',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'descriptionInverse',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: "indexDirect",
        type: "number",
        persist: true,
        critical: true
    }, {
        name: "indexInverse",
        type: "number",
        persist: true,
        critical: true
    }, {
        name: 'isMasterDetail',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'descriptionMasterDetail',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true,
        persist: true,
        critical: true
    }, {
        name: "disabledDestinationDescendants",
        type: "auto",
        defaultValue: [],
        persist: true,
        critical: true
    }, {
        name: "disabledSourceDescendants",
        type: 'auto',
        defaultValue: [],
        persist: true,
        critical: true
    }, {
        name: "inline",
        type: 'boolean',
        defaultValue: false,
        persist: true,
        critical: true
    }, {
        name: "defaultClosed",
        type: 'boolean',
        defaultValue: false,
        persist: true,
        critical: true
    }],

    proxy: {
        url: '/domains/',
        type: 'baseproxy',
        extraParams: {
            ext: true
        }
    },

    hasMany: [{
        name: 'attributes',
        model: 'CMDBuildUI.model.Attribute'
    }],

    /**
     * Get get the description of source class or process.
     * 
     * @return {String}
     */
    getSourceDescription: function () {
        var store = Ext.getStore(this.get('sourceProcess') ? 'processes.Processes' : 'classes.Classes');
        if (store && store.isLoaded) {
            var record = store.findRecord('name', this.get('source'), 0, false, true, true);
            return record.get('description');
        }
        return;
    },

    /**
     * Get get the description of destination class or process.
     * 
     * @return {String}
     */
    getDestinationDescription: function () {
        var store = Ext.getStore(this.get('destinationProcess') ? 'processes.Processes' : 'classes.Classes');
        if (store && store.isLoaded) {
            var record = store.findRecord('name', this.get('destination'), 0, false, true, true);
            return record.get('description');
        }
        return;
    },


    /**
     * Get translation for Master/Detail description.
     * 
     * @return {String}
     */
    getTranslatedDescriptionMasterDetail: function () {
        return this.get("_descriptionMasterDetail_translation") || this.get("descriptionMasterDetail") ||
            this.get("_description_translation") || this.get("description");
    },

    /**
     * Get translation for direct description.
     * 
     * @return {String}
     */
    getTranslatedDescriptionDirect: function () {
        return this.get("_descriptionDirect_translation") || this.get("descriptionDirect");
    },

    /**
     * Get translation for inverse description.
     * 
     * @return {String}
     */
    getTranslatedDescriptionInverse: function () {
        return this.get("_descriptionInverse_translation") || this.get("descriptionInverse");
    },

    /**
     * Load attributes relation
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as parameters the attributes store and a boolean field.
     */
    getAttributes: function (force) {
        var deferred = new Ext.Deferred();
        var attributes = this.attributes();
        var domainName = this.get('name');

        if (!attributes.isLoaded() || force) {

            attributes.setProxy({
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Domains.getAttributes(domainName)
            });

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