Ext.define('CMDBuildUI.view.administration.content.gis.geoserverslayers.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gis-geoserverslayers-view',
    data: {
        storeAutoLoad: false,
        storeProxyUrl: ''
    },
    formulas: {

        updateStoreVariables: {
            get: function (data) {
                this.set(
                    "storeProxyUrl",
                    Ext.String.format(
                        '{0}/classes/_ANY/cards/_ANY/geolayers',
                        CMDBuildUI.util.Config.baseUrl
                    )
                );
                // set auto load
                this.set("storeAutoLoad", true);
            }
        }
    },

    stores: {
        layersStore: {
            model: 'CMDBuildUI.model.map.GeoLayers',
            proxy: {
                type: 'baseproxy',
                url: '{storeProxyUrl}'
            },
            pageSize: 0,
            autoLoad: '{storeAutoLoad}',
            autoDestroy: true
        }
    }

});