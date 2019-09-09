Ext.define('CMDBuildUI.view.management.navigation.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.management-navigation-container',

    formulas: {
        navTitle: function() {
            return CMDBuildUI.locales.Locales.main.navigation;
        }
    }

});
