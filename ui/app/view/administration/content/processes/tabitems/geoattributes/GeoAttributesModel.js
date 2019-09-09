Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.GeoAttributesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-processes-tabitems-geoattributes-geoattributes',
    data: {
        name: 'CMDBuildUI',
        actions: {
            edit: false,
            view: true,
            add: false
        },
        storedata: {
            url: null,
            autoLoad: false
        }
    },
    formulas: {
        geoattributesStoreProxy: {
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
        geoattributesStore: {
                model: 'CMDBuildUI.model.map.GeoAttribute',
                proxy: {
                    type: 'baseproxy',
                    url: '{storedata.url}'
                },
                pageSize: 0,
                autoLoad: '{storedata.autoLoad}',
                autoDestroy: true
            
        }
    }

});
