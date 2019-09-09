Ext.define('CMDBuildUI.view.administration.content.localizations.imports.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-imports-view',
    control: {
        '#cancelBtn': {
            click: 'onCancelButtonClick'
        },
        '#importBtn': {
            click: 'onImportButtonClick'
        }
    },

    onCancelButtonClick: function (button, e, eopts) {
        this.getView().up().close();
    },

    onImportButtonClick: function (button, e, eopts) {
        this.getView().up().close();
    }
});