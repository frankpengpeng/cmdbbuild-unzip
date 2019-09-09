Ext.define('CMDBuildUI.view.map.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-container',

    data: {
        checkLayers: {},
        checkNavigationTree: {},
        toRemove: [],
        toAdd: null,
        bboxExpanded: [0, 0, 0, 0],
        zoomDisabled: false,

        // {ol.Feature} selected feature
        selectedFeature: null,

        //{Ext.data.Model} CMDBuildUI.model.map.GeoElement
        selectedFeatureNotFound: null,

        //geoAttributes of selected row
        // geoDataRow: undefined,
        // stores config
        geoattributesStoreConfig: {
            autoload: false
        },
        externalLayerStore: null


    },

    formulas: {
        /**
         * Update stores configuration after objectTypeName updates
         */
        onUpdateObjectTypeName: {
            bind: '{objectTypeName}',
            get: function (objectTypeName) {
                if (objectTypeName) {
                    // set geoattributes store config
                    this.set("geoattributesStoreConfig.proxyurl", CMDBuildUI.util.api.Classes.getGeoAttributes(objectTypeName));
                    this.set("geoattributesStoreConfig.autoload", true);
                }
            }
        },

        /**
         * Update externalLayerStore data based on map.ExternalLayerExtends store data.
         */
        extentDataExternalLayerStore: {
            bind: {
                externalLayerStoreCount: '{externalLayerStore.totalCount}'
            },
            get: function (data) {
                if (data.externalLayerStoreCount) {
                    var records = Ext.getStore("map.ExternalLayerExtends").getRange();
                    var externalLayerStore = this.get("externalLayerStore");

                    for (var i = 0; i < records.length; i++) {
                        var name = records[i].get('Name');
                        var lrecord = externalLayerStore.findRecord('name', name);
                        if (lrecord != null) {
                            lrecord.set('extent', [records[i].get('minx'), records[i].get('miny'), records[i].get('maxx'), records[i].get('maxy')]);
                            lrecord.set('CRS', records[i].get('CRS'));
                        }
                    }
                }
            }
        },

        bboxChanges: {
            bind: {
                bbox: '{bbox}'
            },
            get: function (data) {
                if (data.bbox != null) {
                    this.getView().fireEvent('bboxchanged', data.bbox);
                }
            }
        },

        legendTabHidden: {
            bind: {
                thematism: '{theThematism}'
            }, get: function (data) {
                if (data.thematism){
                    return false;
                }
                return true;
            }
        }
    }
});
