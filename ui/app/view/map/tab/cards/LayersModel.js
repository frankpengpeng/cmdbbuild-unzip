Ext.define('CMDBuildUI.view.map.tab.cards.LayersModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-tab-cards-layers',
    data: {
        oldRange: []
    },
    formulas: {
        geoAttributesByZoom: {
            bind: {
                zoomDisabled: '{zoomDisabled}',
                actualZoom: '{actualZoom}',
                geoAttributes: '{geoattributes}',
                externalLayerStore: '{externalLayerStore}'
            },
            get: function (d) {
                if (d.actualZoom != null && d.geoAttributes != null && !d.zoomDisabled) {
                    this.getView().fireEvent('onmapzoomchanged', d.actualZoom, d.geoAttributes, d.externalLayerStore);
                }
            }
        }
    },

    stores: {
        layerTree: {
            type: 'map-layers',
            root: {
                text: CMDBuildUI.locales.Locales.gis.root,
                id: 'root',
                expanded: true,
                checked: true,
                children: [{
                    id: 'geoAttributesRoot',
                    text: CMDBuildUI.locales.Locales.gis.geographicalAttributes,
                    expanded: true,
                    checked: true,
                    leaf: false,
                    children: []
                }, {
                    text: CMDBuildUI.locales.Locales.gis.externalServices, //TODO:
                    id: 'externalServicesRoot',
                    expanded: true,
                    checked: true,
                    leaf: false,
                    children: [{
                        id: 'externalLayersNode',
                        text: CMDBuildUI.locales.Locales.gis.geoserverLayers,
                        expanded: true,
                        checked: true,
                        leaf: false
                    }/* , {
                        id: 'mapservices',
                        text: CMDBuildUI.locales.Locales.gis.mapServices,
                        expanded: true,
                        checked: true,
                        leaf: false
                    } */]

                }]
            }
        }
    }
});
