Ext.define('CMDBuildUI.view.map.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-container',
    listen: {
        component: {
            '#': {
                afterrender: 'onAfterRender',
                beforerender: 'onBeforeRender',
                bboxchanged: 'onBboxChanged',
                layerlistchanged: 'onLayerListChanged'
            }
        },
        store: {
            '#cards': {
                load: 'onStoreLoad'
            }
        }
    },

    onBeforeRender: function (container, eOpts) {
        //geoattributes
        this.setGeoAttributesStore();

        //externalLayerStore
        var geoserverEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.geoserverEnabled);
        if (geoserverEnabled == true) {
            this.setExternalLayerStore();
        }

        //Bim store
        var bimStore = Ext.getStore("bim.Projects");
        var bimEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.enabled);
        if (bimEnabled) {
            bimStore.load({
                callback: function () {

                }
            });
        }
        //-------------------------------
        CMDBuildUI.map.util.Util._init();
        // ------------------------------

        container.mon(CMDBuildUI.map.util.Util.getMapGridContainerView(), 'selectedchangeevent', this.onSelectedChange, this);

    },

    /**
     * 
     */
    onAfterRender: function (view) {
        var store = Ext.getStore("map.ExternalLayerExtends");
        var geoserverEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.geoserverEnabled);
        if (geoserverEnabled == true && !store.isLoaded()) {
            store.load({
                callback: function () {
                    this.getViewModel().set('ExternalLayerExtendsLoaded', true);
                },
                scope: this
            });
        }

        //The first selection is get by the url
        var object = this._tokenizeUrl();
        CMDBuildUI.map.util.Util.setSelection(
            object.id,
            object.type);

        if (!object.id && !view.lookupViewModel().get('bbox')) {
            var geoattrsstore = CMDBuildUI.map.util.Util.getGeoAttributesStore();
            geoattrsstore.on("load", function(s, records, successful, operation) {
                s.filter("owner_type", object.type);
                if (s.getRange().length) {
                    var geoattr = s.getRange()[0];
                    Ext.Ajax.request({
                        url: Ext.String.format('{0}/classes/_ANY/cards/_ANY/geovalues/center', CMDBuildUI.util.Config.baseUrl),
                        method: 'GET',
                        params: {
                            attribute: geoattr.getId()
                        },
                        scope: this
                    }).then(function(response) {
                        var responseText = JSON.parse(response.responseText);
                        if (responseText && responseText.found) {
                            var data = responseText.data;
                            CMDBuildUI.map.util.Util.setMapZoom(geoattr.get("zoomDef"));
                            CMDBuildUI.map.util.Util._setMapCenter([data.x, data.y]);
                        }
                    });
                }
            }, this, {
                single: true
            });
        }
    },

    /**
    * selectedchangeevent handler
    * This is specialized in reading the view Configurations
    * @param {Object} selected 
    * {
    *  type: { String }
    *  id: { String }
    *  conf: {
    *      center: true || false,
    *      zoom: true || false
    *      }
    *  }
    * @param {Ext.data.Model} records Model: CMDBuildUI.model.map.GeoElement 
    * the records representing the geovalues of the selected card
    */
    onSelectedChange: function (selected, records) {
        console.log('Handle the selectionChangeEvent from the MapGlobalContainer');
        if (!records) return;

        var center = selected.conf.center;
        var zoom = selected.conf.zoom;

        if (center) {
            CMDBuildUI.map.util.Util.setMapCenter(records[0]);
        }

        if (zoom) {
            //Looks for geoElements visibles with the current zoom level
            for (var i = 0; i < records.length; i++) {
                var record = records[i];
                var geoAttribute = CMDBuildUI.map.util.Util.getGeoAttribute(record.get('_attr'), record.get('_owner_type'))[0];
                if (geoAttribute) {
                    var zoomMin = geoAttribute.get('zoomMin');
                    var zoomMax = geoAttribute.get('zoomMax');
                    var zoom = CMDBuildUI.map.util.Util.getMapZoom();
                    if (zoom >= zoomMin && zoom <= zoomMax) {
                        //set the center with the visible one geoElement
                        if (center) {
                            CMDBuildUI.map.util.Util.setMapCenter(records[i]);
                        }
                        return;
                    }
                }
            }
            //Default behavour, takes the first geoAttibute
            var record = records[0];
            var geoAttribute = CMDBuildUI.map.util.Util.getGeoAttribute(record.get('_attr'), record.get('_owner_type'))[0];
            if (center) {
                CMDBuildUI.map.util.Util.setMapCenter(records[0]);
            }
            if (geoAttribute) {
                var zoomMin = geoAttribute.get('zoomMin');
                var zoomMax = geoAttribute.get('zoomMax');
                var zoomDef = geoAttribute.get('zoomDef');

                var zoom = CMDBuildUI.map.util.Util.getMapZoom();

                if (zoom < zoomMin || zoom > zoomMax) {
                    CMDBuildUI.map.util.Util.setMapZoom(zoomDef);
                }

            }
        }

    },

    /**
     * @param {[number]} bbox 
     */
    onBboxChanged: function (bbox) {
        this.ajaxCaller();
    },

    /**
     * @param {Object} layerList
     */
    onLayerListChanged: function (layerList) {
        this.ajaxCaller()
    },

    /**
     * 
     */
    onStoreLoad: function () {
        this.ajaxCaller(
            function () {
                var layerListCurrentClass = [];
                this._filterLayers(null, layerListCurrentClass);
                this.clearFeatures(layerListCurrentClass);
            }, this);
    },

    /**
     * This function gets the reference of 2 array and fills them
     * @param {[String]} layerlist 
     * @param {[String]} layerListCurrentClass
     */
    _filterLayers: function (layerList, layerListCurrentClass) {
        layerList = layerList || [];
        layerListCurrentClass = layerListCurrentClass || [];

        var allLayers = CMDBuildUI.map.util.Util.getMapMapView().getLayerList();
        var objectTypeName = this.getViewModel().get('objectTypeName');

        for (var els in allLayers) {
            var el = allLayers[els];
            if (el.type === 'VECTOR') {
                var owner_type = el.get('owner_type');
                if (owner_type == objectTypeName) {
                    layerListCurrentClass.push(els);
                } else {
                    layerList.push(els);
                }
            }
        }
    },

    /**
     * 
     */
    ajaxCaller: function (callback, scope) {
        var layerList = [];
        var layerListCurrentClass = [];

        this._filterLayers(layerList, layerListCurrentClass);

        if (layerList.length == 0 && layerListCurrentClass.length == 0) {
            return;
        }

        var bbox = CMDBuildUI.map.util.Util.getBbox();
        if (!bbox) {
            return;
        }

        var filter = Ext.getStore('cards').getAdvancedFilter();
        if (filter.isEmpty()) {
            this.ajaxCall(layerList.concat(layerListCurrentClass), bbox, null, callback, scope);
        } else {

            if (layerList.length != 0) {
                this.ajaxCall(layerList, bbox, null, callback, scope);
            }

            if (layerListCurrentClass.length != 0) {
                var encodedFilter = filter.encode();
                this.ajaxCall(layerListCurrentClass, bbox, encodedFilter, callback, scope);
            }
        }
    },

    /**
     * This function calls the server for geoElements with 'attach_nav_tree' = true
     * Fires 2 events
     * @param {[String]} layerList the id of the geoAttribute elements
     * @param {[Number]} bbox represents the bbox in ol.Bbox formta
     * @param {String} encodedFilter  //TODO: check type
     * @param {Function} callback This callback is called immediately on the succes function
     * @param {Object} scope
     * NOTE: in this function passes first all geoElements. can make here some filters;
     */
    ajaxCall: function (layerList, bbox, encodedFilter, callback, scope) {
        this.handleLoadmaskShow();
        Ext.Ajax.request({
            url: Ext.String.format('{0}/classes/_ANY/cards/_ANY/geovalues', CMDBuildUI.util.Config.baseUrl),
            method: 'GET',
            scope: this,
            params: {
                attribute: layerList,
                area: bbox.toString(),
                attach_nav_tree: CMDBuildUI.map.util.Util.getGisNavigationTree() ? true : false,
                filter: encodedFilter || null
            },
            success: function (response) {
                if (callback) {
                    callback.call(scope || this);
                }

                var responseText = JSON.parse(response.responseText);

                var geoElements = responseText.data;
                var geoElemetsRecords = [];
                geoElements.forEach(function (geoElement) {
                    geoElemetsRecords.push(Ext.create('CMDBuildUI.model.map.GeoElement', geoElement));
                }, this);
                // geoElemetsRecords = this.filterGeoElements(geoElemetsRecords);
                CMDBuildUI.map.util.Util.getMapContainer().fireEvent('geoelementsload', geoElemetsRecords);

                if (CMDBuildUI.map.util.Util.getGisNavigationTree()) {
                    var navTreeDatas = responseText.meta.nav_tree_items;
                    var navTreeRecords = [];
                    navTreeDatas.forEach(function (navTreeData) {
                        navTreeData['_type'] = navTreeData['type']; //because the mapping function doesn't work in the model
                        navTreeRecords.push(Ext.create('CMDBuildUI.model.map.navigation.NavigationTree', navTreeData));
                    }, this);
                    CMDBuildUI.map.util.Util.getMapContainer().fireEvent('navtreeload', navTreeRecords);
                }

                this.handleLoadmaskDelete();
            },
            error: function (response) {
            }
        });
    },

    /**
     * 
     */
    filterGeoElements: function (geoElemetsRecords) {
        var st = CMDBuildUI.map.util.Util.getMapGridContainerViewModel().getStore('cards');
        var objectTypeName = this.getViewModel().get('objectTypeName')
        var filteredGeoElements = [];

        if (st.getAdvancedFilter().isEmpty()) return geoElemetsRecords;

        geoElemetsRecords.forEach(function (geoElemetsRecord) {
            if (geoElemetsRecord.get('_owner_type') == objectTypeName && st.find('_id', geoElemetsRecord.get('_owner_id')) == -1) {
                return;
            }

            filteredGeoElements.push(geoElemetsRecord);
        }, this);

        return filteredGeoElements;
    },

    /**
     * @param {[String]} layerList the layers on wich clear the sources
     */
    clearFeatures: function (layerList) {
        var allLayerList = CMDBuildUI.map.util.Util.getMapMapView().getLayerList();

        for (var i = 0; i < layerList.length; i++) {
            try {
                var layerId = layerList[i];
                var olLayer = allLayerList[layerId];
                olLayer.getSource().clear();
            } catch (e) {
                console.log("Can't delete source of " + layerId);
            }
        }

        try {
            var mapMapController = CMDBuildUI.map.util.Util.getMapMapView().getController();
            var geoValues = CMDBuildUI.map.util.Util.getSelectionGeoValue();
            mapMapController.udateNotFoundSelectedFeature([geoValues[0]]);
        } catch (e) { }
    },

    /**
     * Configure the geoattributes store
     */
    setGeoAttributesStore: function () {
        var objectTypeName = this.getViewModel().get('objectTypeName');

        var geoattributes = Ext.create('Ext.data.Store', {
            storeId: 'geoattributes',
            model: 'CMDBuildUI.model.map.GeoAttribute',
            proxy: {
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Classes.getGeoAttributes(objectTypeName),
                extraParams: {
                    visible: true
                }
            },
            autoLoad: true
        });

        this.getViewModel().set('geoattributes', geoattributes);
    },

    /**
     * configure the externalLayerStore
     */
    setExternalLayerStore: function () {
        var externalLayerStore = Ext.create('Ext.data.Store', {
            model: 'CMDBuildUI.model.map.GeoExternalLayer',
            proxy: {
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Classes.getExternalGeoAttributes(this.getViewModel().get('objectTypeName')),
                extraParams: {
                    visible: true
                }
            },
            autoload: true
        });
        this.getViewModel().set('externalLayerStore', externalLayerStore);
    },

    handleLoadmaskShow: function () {

        if (!this._loadmask) {
            this._loadmask = new Ext.LoadMask({
                target: CMDBuildUI.map.util.Util.getMapMapView()
            });
            this._loadingLayers = 0;
            this._loadmask.show();
        }

        this._loadingLayers = ++this._loadingLayers;
    },

    handleLoadmaskDelete: function () {
        this._loadingLayers = --this._loadingLayers;
        if (this._loadingLayers == 0) {
            this._loadmask.destroy();
            delete this._loadmask;
        }
    },

    privates: {
        /**
         * @returns {Object}
         */
        _tokenizeUrl: function () {
            var url = window.location.href;
            url = url.substring(url.indexOf('classes/'));
            var tokenized = url.split('/');

            return {
                type: tokenized[1],
                id: tokenized[3]
            };
            
        },

        _loadingMayers: 0,
        _loadmask: null
    }
});
