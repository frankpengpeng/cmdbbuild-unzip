Ext.define('CMDBuildUI.map.util.Util', {
    singleton: true,
    _mapContainer: null,

    _init: function () {
        //The container of the grid and the map
        this._mapGridContainer = Ext.ComponentQuery.query('classes-cards-grid-container')[0];
        this._vm_mapGridContainer = this._mapGridContainer.getViewModel();

        this._mapContainer = Ext.ComponentQuery.query('map-container')[0];
        this._vm_mapContainer = this._mapContainer.getViewModel();

        //navigation container
        this._navigationContainer = Ext.ComponentQuery.query('management-content')[0];
        this._vm_naviagtionContainer = this._navigationContainer.getViewModel();

        //geoattributes store
        this._vm_mapContainer_geoattributes = this._vm_mapContainer.get('geoattributes');

        this._mapView = this.getMapContainer().down('map-map');
        this._vm_mapViewModel = this._mapView.getViewModel();
        this._mapView.on('olmapcreated', function (olMap) {
            this._olMap = olMap;
        }, this);

        this._mapContainer.on('destroy', function () {
            this._reset();
        }, this);
        
        //gisNavigationTree
        
        var allNavTrees = Ext.getStore('navigationtrees.NavigationTrees');
        this._gisNavigationTree = allNavTrees.findRecord('_id', 'gisnavigation');

        this._tabPanel = this.getMapContainer().down('map-tab-tabpanel')
    },

    _reset: function () {
        this._mapGridContainer = null;
        this._vm_mapGridContainer = null;

        this._navigationContainer = null;
        this._vm_naviagtionContainer = null;

        //xtype map-container
        this._mapContainer = null;
        this._vm_mapContainer = null;

        //xtype: map-map
        this._mapView = null;
        this._vm_mapViewModel = null;
        this._olMap = null;

        //map-tab-cards-navigationtree
        this._gisNavigationTree = null;

        //geoattributes store
        this._vm_mapContainer_geoattributes = null;

        //
        this._selectionGeovalue = [];

        //
        this._tabPanel = null;
    },

    getNavigationContainer: function () {
        return this._navigationContainer;
    },

    /**
     * Ther should be another util that gives me the navigation container
     */
    getNavigationContainerViewModel: function () {
        if (!this._vm_naviagtionContainer) {
            this._navigationContainer = Ext.ComponentQuery.query('management-content')[0];
            this._vm_naviagtionContainer = this._navigationContainer.getViewModel();
        }
        return this._vm_naviagtionContainer;
    },

    getMapGridContainerViewModel: function () {
        return this._vm_mapGridContainer
    },

    getMapGridContainerView: function () {
        return this._mapGridContainer;
    },

    /**
     * @returns {Ext.panel.Panel} The father panel containing the map
     */
    getMapContainer: function () {
        return this._mapContainer;
    },

    getMapContainerViewModel: function () {
        return this._vm_mapContainer;
    },

    getMapMapView: function () {
        return this._mapView;
    },

    getMapMapViewModel: function () {
        return this._vm_mapViewModel;
    },

    getMapTabPanel: function () {
        return this._tabPanel;
    },

    getLegendPanel: function () {
        return this.getMapTabPanel().down('#map-legend')
    },

    getBbox: function () {
        return this._mapContainer.lookupViewModel('management-content').get('bbox');
    },
    /**
     * @returns the openlayerMap
     */
    getOlMap: function () {
        return this._olMap;
    },
    //TODO: make omgenity in the arguments. make all ext.data.model for the setter
    /**
     * @returns {Number} The value of the zoom
     */
    getMapZoom: function () {
        return this._olMap.getView().getZoom();
    },

    /**
     * @param {Number} zoomValue
     */
    setMapZoom: function (zoomValue) {
        this._olMap.getView().setZoom(zoomValue);
    },

    /**
     * @param {Ext.data.Model} geoElement  CMDBuildUI.model.map.GeoElement
     */
    setMapCenter: function (geoElement) {
        var coordinates = this._getCoordinate(geoElement);
        this._setMapCenter(coordinates);
    },

    /**
     * @param {[number]} coordinates
     */
    _setMapCenter: function (coordinates) {
        this.getOlMap().getView().setCenter(coordinates);
    },

    /**
     * @param {Ext.data.Model} geoValue model: CMDBuildUI.model.map.GeoElement
     * @returns {[Number]} reppresents the x and y coordinate;
     */
    _getCoordinate: function (geoValue) {
        if (!geoValue) return;
        var type = geoValue.get('_type').toUpperCase();

        switch (type) {
            case 'POINT':
                return [
                    geoValue.get('x'),
                    geoValue.get('y')
                ];
            case 'LINESTRING':
                return [
                    geoValue.get('points')[0].x,
                    geoValue.get('points')[0].y
                ];
            case 'POLYGON':
                return [
                    geoValue.get('points')[0].x,
                    geoValue.get('points')[0].y
                ];
        }
    },

    /**
     * @returns {Ext.data.Store} the "geoAttribute store"
     */
    getGeoAttributesStore: function () {
        return this._vm_mapContainer_geoattributes
    },

    /**
     * @param {String} id the id of the geoAttribute
     * @param {String} ownerType the ownerType of the geoAttribute
     * @returns {[Ext.data.Model]} model: CMDBuildUI.model.map.GeoAttribute
     */
    getGeoAttribute: function (id, ownerType) {
        var geoAttributeStore = this.getGeoAttributesStore();
        var geoAttribute = geoAttributeStore.queryRecordsBy(function (record) {
            if (record.get('name') === id && record.get('owner_type') === ownerType) return true;
            return false;
        });

        return geoAttribute;
    },

    /**
     * @param {String} id
     * @param {String} type
     * @param {Function} callback
     * @returns {[Ext.data.Model]} the geovalue related to that id and type. Empty array if doesn't have
     */
    getGeoValues: function (id, type, callback, scope) {
        scope = scope || this;

        Ext.create('Ext.data.Store', {
            model: 'CMDBuildUI.model.map.GeoElement',
            proxy: {
                url: Ext.String.format('/classes/{0}/cards/{1}/geovalues', type, id),
                type: 'baseproxy'
            }
        }).load({
            scope: this,
            callback: function (records, operation, success) {
                if (records != null && records.length != 0) {
                    this._setSelectionGeovalue(records);
                } else {
                    this._setSelectionGeovalue(null);
                }

                callback.call(scope, records, operation, success);
            }
        });

    },

    /**
     * @param {String} id
     * @param {String} type
     * @param {Object} conf
     * 
     * Sets the viewModel variable composed
     */
    setSelection: function (id, type, conf) {
        var navigationContainer = this.getNavigationContainerViewModel();

        conf = conf || {};
        Ext.applyIf(conf, {
            center: true
        });

        var selected = Ext.apply({}, {
            id: id,
            type: type,
            conf: conf
        });

        this._selectionGeovalue = null;
        navigationContainer.set('selected', null);
        navigationContainer.set('selected', selected);

        var url;
        if (id) {
            url = Ext.String.format('classes/{0}/cards/{1}', type, id);
        } else {
            url = Ext.String.format('classes/{0}/cards', type)
        }
        CMDBuildUI.util.Utilities.redirectTo(url, true);

    },

    /**
     * @returns {Object} The viewModel variable selelected
     */
    getSelection: function () {
        return this.getMapGridContainerViewModel().get('selected');
    },

    /**
     * @returns {[Ext.data.Model]}
     */
    getSelectionGeoValue: function () {
        return this._selectionGeovalue
    },

    /**
     * @param {[Ext.data.Model]} selecionGeovalue CMDBuildUI.model.map.GeoElement
     */
    _setSelectionGeovalue: function (selectedGeovalue) {
        this._selectionGeovalue = selectedGeovalue;
    },

    /**
     * @returns Ext.data.Model CMDBuildUI.model.navigationTrees.DomainTree
     */
    getGisNavigationTree: function () {
        return this._gisNavigationTree;
    },

    /**
     * 
     * @param {Ext.data.thematism.Thematism} thematism 
     * @param {Boolean} calltype tells if the apply comes from an apply or saveandApply event
     */
    applyThematism: function (thematism, calltype) {
        this.getMapContainerViewModel().set('theThematism', null);

        var name = thematism.get('attribute');
        var owner = thematism.get('owner');

        var geoAttribute = this.getGeoAttribute(name, owner)[0];

        if (!geoAttribute) {
            console.error(Ext.String.format("GeoAttribute not found. name: {0}, ownerClass: {1}", name, owner));
        } else {
            //setting the thematism will change the style in the creation process of the thematism
            geoAttribute.set('hasThematism', thematism);
        }

        var olLayer = this.getMapMapView().getController().getOlLayer(owner, name);

        if (olLayer) {
            //change the style of the olLayer.
            thematism.calculateResults(function () {
                this.getMapMapView().getController().setThematism(olLayer, geoAttribute);
            }, this, calltype);
        }

        //set the viewModel variable for enabeling the legend
        this.getMapContainerViewModel().set('theThematism', thematism);

        //call the update function of the component in the tab panel.
        //That function is not applied in automatic
        this.getLegendPanel().down('thematisms-thematism-rules').getViewModel().setrules();
    },

    /**
     * 
     */
    clearThematism: function () {
        var mapContinerVM = CMDBuildUI.map.util.Util.getMapContainerViewModel();
        var thematism = mapContinerVM.get('theThematism');

        if (!thematism) return;

        var name = thematism.get('attribute');
        var owner = thematism.get('owner');
        var geoAttribute = this.getGeoAttribute(name, owner)[0];

        if (!geoAttribute) {
            console.error(Ext.String.format("GeoAttribute not found. name: {0}, ownerClass: {1}", name, owner));
        } else {
            //setting the thematism will change the style in the creation process of the thematism
            geoAttribute.set('hasThematism', null);
        }

        var olLayer = this.getMapMapView().getController().getOlLayer(owner, name);
        if (olLayer) {
            // change the styleof the layer
            this.getMapMapView().getController().calculateStyle(olLayer, geoAttribute);
        }

        mapContinerVM.set('theThematism', null);
    }
});