Ext.define('CMDBuildUI.view.administration.ContentModel', {
    requires: ['CMDBuildUI.locales.Locales'],
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content',
    data: {
        title: CMDBuildUI.locales.Locales.administration.title,
        activeTabs: {
            classes: 0,
            processes: 0

        }
    }

});
