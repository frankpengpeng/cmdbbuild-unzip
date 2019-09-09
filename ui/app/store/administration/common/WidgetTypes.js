Ext.define('CMDBuildUI.store.administration.common.WidgetTypes', {
    extend: 'CMDBuildUI.store.Base',
    requires: ['CMDBuildUI.model.base.ComboItem'],
    model: 'CMDBuildUI.model.base.ComboItem',

    alias: 'store.common-widgettypes',
    fields: ['value', 'label'],
    data: [{
        'value': 'calendar',
        'label': CMDBuildUI.locales.Locales.administration.classes.texts.calendar // Calendar
    }, {
        'value': 'createModifyCard',
        'label': CMDBuildUI.locales.Locales.administration.classes.texts.createmodifycard // Create / Modify Card'
    }, {
        'value': 'createReport',
        'label': CMDBuildUI.locales.Locales.administration.classes.texts.createreport // Create Report
    }, {
        'value': 'workflow',
        'label': CMDBuildUI.locales.Locales.administration.classes.texts.startworkflow // Start workflow'
    }],
    sorters: ['label']

});