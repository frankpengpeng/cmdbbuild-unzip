Ext.define('CMDBuildUI.model.lookups.Lookup', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        /**
         * Load lookup values for given Lookup Type
         * @param {String} type
         * @param {Number|String} id
         */
        getLookupValueById: function (type, id) {
            var lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(type);
            return lt.values().getById(id);
        }
    },

    fields: [{
        name: '_type',
        type: 'string',
        critical: true
    }, {
        name: 'code',
        type: 'string',
        critical: true
    }, {
        name: 'description',
        type: 'string',
        critical: true
    }, {
        name: '_description_translation',
        type: 'string',
        persist: false
    }, {
        name: 'text',
        type: 'string',
        calculate: function (data) {
            return data._description_translation || data.description;
        }
    }, {
        name: 'number',
        type: 'integer',
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true,
        critical: true
    }, {
        name: 'default',
        type: 'boolean',
        critical: true
    }, {
        name: 'parent_id',
        type: 'integer',
        critical: true
    }, {
        name: 'parent_description',
        type: 'string',
        critical: true
    }, {
        name: 'parent_type',
        type: 'string',
        critical: true
    }, {
        name: 'note',
        type: 'string',
        critical: true
    }, {
        name: 'icon_type',
        type: 'string',
        defaultValue: 'none',
        critical: true
    }, {
        name: 'icon_image',
        type: 'string',
        critical: true
    }, {
        name: 'icon_font',
        type: 'string',
        critical: true
    }, {
        name: 'icon_color',
        type: 'string',
        critical: true
    }, {
        name: 'text_color',
        type: 'string',
        critical: true
    }, {
        name: 'index',
        type: 'number',
        critical: true
    }],

    proxy: {
        type: 'baseproxy'
    },

    /**
     * Get translated description
     * @return {String}
     */
    getTranslatedDescription: function () {
        return this.get("_description_translation") || this.get("description");
    },

    /**
     * @return {HTML}
     */
    getFormattedDescription: function () {
        var output = "";

        // get font icon
        if (this.get("icon_type") == "font" && this.get("icon_font")) {
            output += '<span class="' + this.get("icon_font") + '" ';
            if (this.get("icon_color")) {
                output += 'style="color: ' + this.get("icon_color") + ';" ';
            }
            output += '></span> ';
        }

        // get font image
        if (this.get("icon_type") == "image" && this.get("icon_image")) {
            output += Ext.String.format(
                '<img src="{0}" style="max-width: 16px; max-height: 16px; vertical-align: text-bottom;" /> ',
                this.get("icon_image")
            );
        }

        // value description
        var txt = this.get("_description_translation") || this.get("description");
        if (this.get("text_color")) {
            output += Ext.String.format(
                '<span style="color: {0};">{1}</span>',
                this.get("text_color"),
                txt
            );
        } else {
            output += txt;
        }
        return output;
    }
});
