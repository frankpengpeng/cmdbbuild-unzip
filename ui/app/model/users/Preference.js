(function () {
    var statics = {
        startingpage: "cm_ui_startingClass",
        processWidgetAlwaysEnabled: "cm_ui_processWidgetAlwaysEnabled",
        thousandsSeparator: 'cm_ui_thousandsSeparator',
        decimalsSeparator: 'cm_ui_decimalsSeparator',
        dateFormat: 'cm_ui_dateFormat',
        timeFormat: 'cm_ui_timeFormat',
        timezone: 'cm_ui_timezone',
        language: 'language',
        preferredOfficeSuite: 'cm_ui_preferredOfficeSuite'
    };

    Ext.define('CMDBuildUI.model.users.Preference', {
        extend: 'Ext.data.Model',

        statics: statics,

        fields: [{
            name: statics.startingpage,
            type: 'string'
        }, {
            name: statics.processWidgetAlwaysEnabled,
            type: 'string'
        }, {
            name: statics.thousandsSeparator,
            type: 'string',
            defaultValue: null
        }, {
            name: statics.decimalsSeparator,
            type: 'string',
            defaultValue: null
        }, {
            name: statics.dateFormat,
            type: 'string',
            defaultValue: null
        }, {
            name: statics.timeFormat,
            type: 'string',
            defaultValue: null
        }, {
            name: statics.timezone,
            type: 'string',
            defaultValue: null
        }, {
            name: statics.preferredOfficeSuite,
            type: 'string'
        }],

        proxy: {
            url: '/sessions/current/',
            type: 'baseproxy'
        }
    });
})();