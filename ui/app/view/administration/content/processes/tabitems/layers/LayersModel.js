Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.layers.LayersModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-processes-tabitems-layers-layers',
    data: {
        name: 'CMDBuildUI',
        actions: {
            edit: false,
            view: true
        },
        storedata: {
            url: null,
            autoLoad: false
        }
    },
    formulas: {
        layersStoreProxy: {
            bind: '{objectTypeName}',
            get: function (objectTypeName) {
                if(objectTypeName){
                    this.set('storedata.url', Ext.String.format('/processes/{0}/geoattributes', objectTypeName));
                    this.set('storedata.autoLoad', true);
                }
            }
        }
    },
    stores: {
        layersStore: {
            model: 'CMDBuildUI.model.map.GeoAttribute',
            proxy: {
                type: 'baseproxy',
                url: '{storedata.url}',
                extraParams: {
                    visibles: true
                }
            },
            pageSize: 0,
            autoLoad: '{storedata.autoLoad}',
            autoDestroy: true
        }
    }
});