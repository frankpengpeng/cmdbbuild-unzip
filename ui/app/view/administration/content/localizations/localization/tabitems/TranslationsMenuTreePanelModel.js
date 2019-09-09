Ext.define('CMDBuildUI.view.administration.content.localizations.localization.tabitems.TranslationsMenuTreePanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-localizations-localization-tabitems-translationsmenutreepanel',
    data: {
        name: 'CMDBuildUI'
    },

    formulas: {},

    stores: {
        menus: {
            type: 'store',
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0,
            proxy: {
                url: Ext.String.format(
                    '{0}/menu/',
                    CMDBuildUI.util.Config.baseUrl
                ),
                type: 'baseproxy'
            },
            listeners: {
                load: 'onStoreLoad'
            }
        },
        completeTranslationsStore: {
            type: 'store',
            proxy: {
                type: 'baseproxy'
            },
            autoDestroy: true
        }

    }
});