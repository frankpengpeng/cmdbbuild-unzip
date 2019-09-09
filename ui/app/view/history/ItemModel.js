Ext.define('CMDBuildUI.view.history.ItemModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.history-item',

    formulas: {
        title: function (get) {
            return null; // return null to hide header
        }
    }

});
