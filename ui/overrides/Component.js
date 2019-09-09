Ext.define('Overrides.Component', {
    override: 'Ext.Component',

    config: {
        /**
         * @cfg {Object} localized
         * An object containing localizations for each localized property.
         * Only string values starting with `CMDBuildUI.locales.Locales` are allowed.
         */
        localized: null
    },

    initComponent: function () {
        var me = this,
            localized = me.getLocalized(),
            value;
        if (Ext.isObject(localized) && !Ext.Object.isEmpty(localized)) {
            for (var prop in localized) {
                value = localized[prop];
                if (Ext.isString(value) && Ext.String.startsWith(value, "CMDBuildUI.locales.Locales")) {
                    /* jshint ignore:start */
                    try {
                        me[prop] = eval(value);
                    } catch(e) {
                        me[prop] = value;
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format("Label {0} not found", value),
                            CMDBuildUI.util.Logger.levels.error
                        )
                    }
                    /* jshint ignore:end */
                }
            }
        }
        me.callParent(arguments);
    }
});