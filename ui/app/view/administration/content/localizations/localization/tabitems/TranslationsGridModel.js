Ext.define('CMDBuildUI.view.administration.content.localizations.localization.tabitems.TranslationsGridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-localizations-localization-tabitems-translationsgrid',
    data: {
        allRows: []
    },
    formulas: {},

    stores: {
        completeTranslationsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            autoLoad: true,
            autoDestroy: true,
            sorters: ['element'],
            proxy: {
                type: 'memory'
            },
            data: '{allRows}',
            filters: []
        }
    }

});