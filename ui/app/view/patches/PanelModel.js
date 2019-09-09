Ext.define('CMDBuildUI.view.patches.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.patches-panel',

    stores: {
        patches: {
            model: 'CMDBuildUI.model.base.Patch',
            autoLoad: true,
            autoDestroy: true,
            sorters: ['name'],
            pageSize: 0
        }
    }

});
