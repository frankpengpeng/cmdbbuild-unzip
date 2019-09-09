Ext.define("CMDBuildUI.util.ecql.Resolver", {
    singleton: true,

    /**
     * Resolve eCQL
     * 
     * @param {Object} filter eCQL configuration.
     * @param {String} filter.id eCQL id.
     * @param {Object} filter.bindings eCQL bindings definition.
     * @param {String[]} filter.bindings.client List of binding client attributes.
     * @param {String[]} filter.bindings.server List of binding server attributes.
     * @param {Ext.data.Model} target The target model instance.
     * @return {Obejct} 
     * The eCQL filter.
     * {
     *  id: "ecqlId",
     *  context: "{client:{some:value},server:{wath:ever}}"
     * }
     */
    resolve: function (filter, target) {
        if (filter) {
            var context = {
                client: this.resolveClientVariables(filter.bindings.client, target),
                server: this.resolveServerVariables(filter.bindings.server, target)
            };

            return {
                id: filter.id,
                context: Ext.JSON.encode(context)
            };
        }
    },

    /**
     * Resolve client variables.
     * 
     * @param {String[]} variables List of binding client attributes.
     * @param {Ext.data.Model} target The target model instance.
     * @return {Object}
     */
    resolveClientVariables: function(variables, target) {
        if (variables && variables.length) {
            var data = target.getData();
            return this.getValuesFromData(data, variables);
        }
        return {};
    },

    /**
     * Resolve server variables.
     * 
     * @param {String[]} variables List of binding server attributes.
     * @param {Ext.data.Model} target The target model instance.
     * @return {Object}
     */
    resolveServerVariables: function(variables, target) {
        // TODO: get original data
        if (variables && variables.length) {
            var data = target.getData();
            return this.getValuesFromData(data, variables);
        }
        return {};
    },

    /**
     * Get bindings.
     * 
     * @param {Object} filter eCQL configuration.
     * @param {Object} filter.bindings eCQL bindings definition.
     * @param {String[]} filter.bindings.client List of binding client attributes.
     * @return {Object} An object containing all keys
     */
    getViewModelBindings:function (filter, linkname) {
        var bindings = {};
        for (var i = 0; i < filter.bindings.client.length; i++) {
            var attr = filter.bindings.client[i];
            var sattr = attr.split(".");
            if (sattr.length === 1) {
                bindings[sattr[0]] = Ext.String.format("{{0}.{1}}", linkname, sattr[0]);
            } else if (sattr.length === 2 && sattr[1] === "Id") {
                bindings[sattr[0]] = Ext.String.format("{{0}.{1}}", linkname, sattr[0]);
            }
        }
        return bindings;
    },

    privates: {
        /**
         * @private
         * @param {Object} data
         * @param {String[]} keys
         * @return {Object}
         */
        getValuesFromData: function (data, keys) {
            var values = {};

            for (var i = 0; i < keys.length; i++) {
                var attr = keys[i];
                var sattr = attr.split(".");
                var val;
                if (sattr.length === 1) {
                    if (sattr[0] === "Id") {
                        sattr[0] = "_id";
                    }
                    val = data[sattr[0]];
                } else if (sattr.length === 2 && sattr[1] === "Id") {
                    val = data[sattr[0]] !== 0 ? data[sattr[0]] : null;
                }
                values[attr] = Ext.isEmpty(val) ? null : val;
            }

            return values;
        }
    }
});