/**
 * This class is the view model for the Main view of the application.
 */
Ext.define('CMDBuildUI.view.main.MainModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.main',

    data: {
        name: 'CMDBuildUI',
        isAdministrationModule: null
    }
});
