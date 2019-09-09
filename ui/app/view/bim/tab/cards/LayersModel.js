Ext.define('CMDBuildUI.view.bim.tab.cards.LayersModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.bim-tab-cards-layers',
    data: {
        name: 'CMDBuildUI'
    },
    stores: {
        bimIfcLayerStore: {
            model: 'CMDBuildUI.model.bim.Types',
            data: '{ifcLayers}',
            sorters: [{
                property: 'name',
                direction: 'ASC' // or 'ASC'
            }]
        }
    }

});
