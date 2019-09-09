Ext.define('CMDBuildUI.model.map.GeoLayers', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        types: {
            shape: {
                label: 'SHAPE',
                value: 'shape'
            },
            worldimage: {
                label: 'WORLD IMAGE',
                value: 'worldimage'
            },
            geotiff: {
                label: 'GEOTIFF',
                value: 'geotiff'
            }
        }
    },

    fields: [{
            name: 'geoserver_name',
            type: 'string',
            critical: true
        },
        {
            name: 'description',
            type: 'string',
            critical: true
        },
        {
            name: 'index',
            type: 'integer',
            critical: true
        }, {
            name: 'name',
            type: 'string',
            critical: true
        }, {
            name: 'owner_id',
            type: 'string',
            critical: true
        }, {
            name: 'owner_type',
            type: 'string',
            critical: true
        }, {
            name: 'type',
            type: 'string',
            critical: true
        }, {
            name: 'visibility',
            type: 'auto', // array,
            defaultValue: [],
            critical: true
        }, {
            name: 'zoomDef',
            type: 'integer',
            critical: true,
            defaultValue: 13
        }, {
            name: 'zoomMax',
            type: 'integer',
            critical: true,
            defaultValue: 25
        }, {
            name: 'zoomMin',
            type: 'integer',
            critical: true,
            defaultValue: 1
        },
        {
            name: 'active',
            type: 'bool',
            critical: true,
            defaultValue: true
        }
    ],
    proxy: {
        type: 'baseproxy'
    },

    /**
     * Return a clean clone of a geoattribute.
     * 
     * @return {CMDBuildUI.model.Attribute} the fresh cloned attribute
     */
    clone: function () {
        var newGeoAttribute = this.copy();
        newGeoAttribute.set('_id', undefined);
        newGeoAttribute.set('name', '');
        newGeoAttribute.set('description', '');
        newGeoAttribute.crudState = "C";
        newGeoAttribute.phantom = true;
        delete newGeoAttribute.crudStateWas;
        delete newGeoAttribute.previousValues;
        delete newGeoAttribute.modified;
        return newGeoAttribute;
    }

});