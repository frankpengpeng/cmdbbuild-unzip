Ext.define('CMDBuildUI.proxy.BaseProxy', {
    extend: "Ext.data.proxy.Rest",
    alias: 'proxy.baseproxy',

    statics: {
        filter: {
            query: '_query',
            ecql: '_ecql'
        }
    },
    timeout: CMDBuildUI.util.Config.ajaxTimeout || 15000,
    reader: {
        type: 'json',
        rootProperty: 'data',
        metaProperty: 'meta',
        totalProperty: 'meta.total'
    },

    getBaseUrl: function () {
        return CMDBuildUI.util.Config.baseUrl;
    },

    // @override
    getUrl: function () {
        var url = this.callParent(arguments);
        if (url && url.indexOf("http://") === -1 && url.indexOf("https://") === -1) {
            // initialize url
            url = this.getBaseUrl() + url;
        }
        return url;
    },

    /**
     * Encodes the array of {@link Ext.util.Filter} objects into a string to be sent in the request url. By default,
     * this simply JSON-encodes the filter data
     * @param {Ext.util.Filter[]} filters The array of {@link Ext.util.Filter Filter} objects
     * @return {String} The encoded filters
     * @override
     */
    encodeFilters: function (filters) {
        var out = {};
        var attr_filters = [];

        Ext.Array.each(filters, function (filter, index) {
            // fulltext query filter
            if (filter.getProperty() === "filter") {
                out = filter.getValue();
            } else if (filter.getProperty() === CMDBuildUI.proxy.BaseProxy.filter.query) {
                out.query = filter.getValue();
            } else if (filter.getProperty() === CMDBuildUI.proxy.BaseProxy.filter.ecql) {
                out.ecql = filter.getValue();
            } else if (filter.getProperty()) {
                // attributes filter
                switch (filter.getOperator()) {
                    case 'in':
                        attr_filters.push({
                            simple: {
                                attribute: filter.getProperty(),
                                operator: 'in',
                                value: filter.getValue()
                            }
                        });
                        break;
                    case 'lt':
                        attr_filters.push({
                            simple: {
                                attribute: filter.getProperty(),
                                operator: 'less',
                                value: [filter.getValue()]
                            }
                        });
                        break;
                    case 'gt':
                        attr_filters.push({
                            simple: {
                                attribute: filter.getProperty(),
                                operator: 'greater',
                                value: [filter.getValue()]
                            }
                        });
                        break;
                    case 'eq':
                    case '==':
                        attr_filters.push({
                            simple: {
                                attribute: filter.getProperty(),
                                operator: 'equal',
                                value: [filter.getValue()]
                            }
                        });
                        break;
                    case 'like':
                        attr_filters.push({
                            simple: {
                                attribute: filter.getProperty(),
                                operator: 'contain',
                                value: [filter.getValue()]
                            }
                        });
                        break;
                }
            }
        });

        if (attr_filters.length === 1) {
            out.attribute = attr_filters[0];
        } else if (attr_filters.length > 1) {
            out.attribute = {
                and: attr_filters
            };
        }

        return this.applyEncoding(out);
    }
});
