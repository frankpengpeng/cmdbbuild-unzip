Ext.define('CMDBuildUI.view.administration.content.localizations.exports.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-exports-view',
    control: {
        '#cancelBtn': {
            click: 'onCancelButtonClick'
        },
        '#exportBtn': {
            click: 'onExportButtonClick'
        }
    },

    onCancelButtonClick: function (button, e, eopts) {
        this.getView().up().close();
    },

    onExportButtonClick: function (button, e, eopts) {
        var sectionCombobox = this.lookup('sectionCombobox').getValue();
        var activelanguagesgrid = this.lookup('activelanguagesgrid');
        var formatCombobox = this.lookup('formatCombobox').getValue();
        var separatorCombobox = this.lookup('separatorCombobox').getValue();
        var activeOnly = this.lookup('activeOnly').getValue();
        var activeLanguages = activelanguagesgrid.getSelection();
        var languages = [];
        activeLanguages.forEach(function (activeLanguage) {
            var code = activeLanguage.get('code');
            languages.push(code);
        });
        
        Ext.Ajax.request({
            url: Ext.String.format('{0}/translations/download?section={1}&languages={2}&format=CSV&separator={3}&activeonly={4}', 
            CMDBuildUI.util.Config.baseUrl, 
            sectionCombobox,
            languages,
            separatorCombobox,
            activeOnly
            ),
            method: 'GET',
            jsonData: {}
        });

        this.getView().up().close();
    }
});