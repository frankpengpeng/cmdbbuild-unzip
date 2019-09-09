Ext.define('CMDBuildUI.view.management.ContentModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.management-content',

    data: {
        activeView: 'grid-list',
        activeMapTabPanel: 0,
        bbox: null,
        actualZoom: null
    }
    
});
