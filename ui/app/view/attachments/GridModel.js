Ext.define('CMDBuildUI.view.attachments.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.attachments-grid',

    formulas: {
        updateTranslations: function() {
            this.set("translations.filename", CMDBuildUI.locales.Locales.attachments.filename);
            this.set("translations.description", CMDBuildUI.locales.Locales.attachments.description);
            this.set("translations.version", CMDBuildUI.locales.Locales.attachments.version);
            this.set("translations.creationdate", CMDBuildUI.locales.Locales.attachments.creationdate);
            this.set("translations.modificationdate", CMDBuildUI.locales.Locales.attachments.modificationdate);
            this.set("translations.category", CMDBuildUI.locales.Locales.attachments.category);
            this.set("translations.author", CMDBuildUI.locales.Locales.attachments.author);
        }
    }

});
