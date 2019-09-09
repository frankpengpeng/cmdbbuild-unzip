
Ext.define('CMDBuildUI.view.map.tab.cards.Layers', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.LayersController',
        'CMDBuildUI.view.map.tab.cards.LayersModel'
    ],
    alias: 'widget.map-tab-cards-layers',
    controller: 'map-tab-cards-layers',
    viewModel: {
        type: 'map-tab-cards-layers'
    },
    bind: {
        store: '{layerTree}'
    },

    cls: 'noicontree'

    /**
     * @event onmapzoomchanged
     * This event is fired when the mapZoom changes, the layerStore and external layer store changes (with the bind politics)
     * @param {Number} value the value of the actual zoom
     * @param {Ext.data.Store} layerStore the geoattributes store
     * @param externalLayerStore the store containing the external layers
     */
});
