Ext.define('CMDBuildUI.view.administration.content.gis.geoserverslayers.card.ViewEditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gis-geoserverslayers-card-viewedit',
    data: {
        name: 'CMDBuildUI'
    },

    formulas: {
        getAllClassesProcesses: {
            get: function () {
                var data = [];

                var types = {
                    classes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.classes,
                        childrens: Ext.getStore('classes.Classes').getData().getRange()
                    },
                    processes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.processes,
                        childrens: Ext.getStore('processes.Processes').getData().getRange()
                    }
                };
                Object.keys(types).forEach(function (type, typeIndex) {
                    types[type].childrens.forEach(function (value, index) {
                        var item = {
                            value: value.get('_id'),
                            label: value.get('description')
                        };
                        data.push(item);
                    });
                });
                return data;
            }
        },
        getAssociatedCards: {
            bind: '{theLayer.owner_type}',
            get: function (associatedClass) {
                if (associatedClass) {
                   var url = CMDBuildUI.util.api.Classes.getCardsUrl(associatedClass);
                    this.set('storeProxyUrl', url);
                    this.set('storeAutoLoad', true);
                    if (this.getStore('getAssociatedCardsStore')) {
                        this.getStore('getAssociatedCardsStore').reload();
                    }
                }
            }
        }
    },
    stores: {
        getAllClassesProcessesStore: {
            data: '{getAllClassesProcesses}',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true
        },
        getAssociatedCardsStore: {
            autoDestroy: true,
            type: 'classes',
            proxy: {
                type: 'baseproxy',
                url: '{storeProxyUrl}'
            },
            //pageSize: 0,
            autoLoad: '{storeAutoLoad}'
        },
        typeStore: {
            proxy: {
                type: 'memory'
            },
            data: [{
                label: CMDBuildUI.model.map.GeoLayers.types.shape.label,
                value: CMDBuildUI.model.map.GeoLayers.types.shape.value
            }, {
                label: CMDBuildUI.model.map.GeoLayers.types.worldimage.label,
                value: CMDBuildUI.model.map.GeoLayers.types.worldimage.value
            }, {
                label: CMDBuildUI.model.map.GeoLayers.types.geotiff.label,
                value: CMDBuildUI.model.map.GeoLayers.types.geotiff.value
            }],
            sorters: ['label']
        }
    }

});