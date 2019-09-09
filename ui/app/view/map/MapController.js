Ext.define('CMDBuildUI.view.map.MapController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-map',

    listen: {
        global: {
            recursivechechcontrol: 'onCheckChangeLayers'
        }
    },
    control: {
        '#': {
            afterrender: "onAfterRender",
            beforerender: 'onBeforeRender',
            resize: "onResize",
            toaddlayer: 'addLayerGis',
            toremovelayer: 'removeLayerGis',
            featurebynavigationtreechanged: 'removeFeaturesBynavigationTree'
        }
    },

    /**
     * @param view the view
     * @param eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.mon(CMDBuildUI.map.util.Util.getMapGridContainerView(), 'selectedchangeevent', this.onSelectedChange, this);
        view.mon(CMDBuildUI.map.util.Util.getMapContainer(), 'geoelementsload', this.onGeoelementsLoad, this);
    },

    /**
     * @param {[Ext.data.model]} geoElemetsRecords model:CMDBuildUI.model.map.GeoElement
     */
    onGeoelementsLoad: function (geoElemetsRecords) {
        var vmP = CMDBuildUI.map.util.Util.getMapContainerViewModel();
        if (vmP.get('zoomDisabled') == true) return;

        var list = this.getViewModel().get('checkNavigationTree');
        var bimProjectStore = Ext.getStore("bim.Projects");

        geoElemetsRecords.forEach(function (geoElemetsRecord) {
            var layerName = geoElemetsRecord.get('_attr');
            var layerType = geoElemetsRecord.get('_owner_type');
            var olLayer = this.getOlLayer(layerType, layerName);
            if (!olLayer) return;

            var featureToInsert;
            var vectorSource = olLayer.getSource();
            var type = geoElemetsRecord.get('_type');

            switch (type) {
                case 'point':
                    var point = new ol.geom.Point(
                        ol.proj.transform([geoElemetsRecord.get('x'), geoElemetsRecord.get('y')], 'EPSG:3857', 'EPSG:3857')
                    );
                    var feature = new ol.Feature({
                        type: 'Point',
                        geometry: point,
                        data: geoElemetsRecord.getData()
                    });
                    featureToInsert = feature;
                    this.setBimProperty(feature, bimProjectStore);
                    break;
                case 'polygon':
                    var points = geoElemetsRecord.get('points');
                    var coords = [];
                    for (var i = 0; i < points.length; i++) {
                        coords.push(ol.proj.transform([points[i].x, points[i].y], 'EPSG:3857', 'EPSG:3857'));
                    }
                    var featurePoly = new ol.Feature({
                        type: 'Polygon',
                        geometry: new ol.geom.Polygon([coords]),
                        data: geoElemetsRecord.getData()
                    });
                    featureToInsert = featurePoly;
                    break;
                case 'linestring':
                    var pointsL = geoElemetsRecord.get('points');
                    var coordsL = [];
                    for (var k = 0; k < pointsL.length; k++) {
                        coordsL.push(ol.proj.transform([pointsL[k].x, pointsL[k].y], 'EPSG:3857', 'EPSG:3857'));
                    }
                    var featureLine = new ol.Feature({
                        type: 'LineString',
                        geometry: new ol.geom.LineString(coordsL),
                        data: geoElemetsRecord.getData()
                    });
                    featureToInsert = featureLine;
                    break;
            }
            featureToInsert.setId(geoElemetsRecord.get('_id'));
            this.insertFeature(vectorSource, featureToInsert, list)
        }, this);

        if (this.getViewModel().get('selectedFeatureNotFound')) {
            this.featureAlghorithm(CMDBuildUI.map.util.Util.getSelectionGeoValue());
        }
    },

    /**
     * fired by it's view Model
     * @param {Object} selected 
     * {
     *  type: { String }
     *  id: { String }
     *  conf: {
     *      center: true || false,
     *      zoom: true || false
     *  
     *      }
     *  }
     * @param {Ext.data.Model} records CMDBuildUI.model.map.GeoElement the records rapresenting the geovalues of the selected card
     */
    onSelectedChange: function (selected, records) {
        console.log('Handle the selectionChangeEvent from the Map');
        this.featureAlghorithm(records);
    },

    onResize: function (extCmp, width, height) {
        var view = this.getView();
        var map = view.getOlMap();
        map.setSize([width, height]);
    },
    /**
     * @param view
     * @param eOpts
     */
    onAfterRender: function (view, eOpts) {
        // sets and generates the html div id
        view.setDivMapId("map-" + Ext.id());
        view.setHtml(
            Ext.String.format(
                '<div id="{0}"></div>',
                view.getDivMapId()
            )
        );
        var zoom = this.getViewModel().get('actualZoom');
        var extent = this.getViewModel().get('bbox');
        var center = function () {
            if (extent != null) {
                return function () {
                    var X = extent[0] + (extent[2] - extent[0]) / 2;
                    var Y = extent[1] + (extent[3] - extent[1]) / 2;
                    return [X, Y];
                }();
            } else {
                var initialLon = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.initialLon);
                var initialLat = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.initialLat);

                return ol.proj.fromLonLat([initialLon, initialLat]);
            }
        }();//Note, this anonimous function is called immediately

        var viewConfig = {
            projection: 'EPSG:3857',
            zoom: zoom || CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.initialZoom),
            center: center || ol.proj.fromLonLat(),
            maxZoom: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.maxZoom),
            minZoom: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.minZoom)
        };

        // Configure map components
        var olView = new ol.View(viewConfig);
        var baseLayer = new ol.layer.Tile({
            source: new ol.source.OSM()
        });
        var controls = this.createMapControls();
        // add map to container
        // TODO: Resolve the probleme on the opening of map, if i resize the window (of chrome) all works otherwise is wrong 
        view.setOlMap(new ol.Map({
            controls: controls,
            target: view.getDivMapId(),
            view: olView,
            layers: [
                baseLayer
            ]
        }));
        this.createControls();
        view.fireEvent('olmapcreated', view.getOlMap());
    },

    /**
     * @returns {[ol.control.Control]}
     */
    createMapControls: function () {
        var me = this;
        return [
            new ol.control.Zoom(),
            new ol.control.ScaleLine(),
            new ol.control.MousePosition({
                projection: 'EPSG:4326',
                coordinateFormat: function (coord) { //Template for the mousePostiton controller
                    var template = Ext.String.format('{0}: {1} {2}: {3}',
                        CMDBuildUI.locales.Locales.gis.zoom,
                        CMDBuildUI.map.util.Util.getMapZoom(),
                        CMDBuildUI.locales.Locales.gis.position,
                        '{x} {y}'
                    );
                    return ol.coordinate.format(coord, template, 2);
                }
            })

        ];
    },

    /**
     * @param {Object} a -contains information about the event fired
     */
    onMoveEnd: function (a) {
        var map = a.map;
        var mapView = map.getView();
        var extent = mapView.calculateExtent(map.getSize());
        var zoom = CMDBuildUI.map.util.Util.getMapZoom()

        if (zoom != this.getViewModel().get('actualZoom')) {
            this.getView().up('management-content').getViewModel().set('actualZoom', zoom);
        }
        CMDBuildUI.util.Navigation.getManagementContainer();
        this.getView().up('management-content').getViewModel().set('bbox', [extent[0], extent[1], extent[2], extent[3]]);
    },

    /**
     * 
     */
    onCheckChangeLayers: function () {
        var vm = this.getViewModel();
        var checkLayers = vm.get('checkLayers');
        var layerList = this.getView().getLayerList();

        var bool = (vm.get('selectedFeature') != null) ? true : false;
        var bool2 = false;


        for (var el in layerList) {
            if (checkLayers[el] && checkLayers[el].checked == false) {
                layerList[el].setVisible(false);
                bool ? this.interactionSelectionVisibility(layerList[el]) : null;   //handle the case in wich the selectedFeature is on a layer wich is now not visible and mus operate on that feature.
            } else {
                layerList[el].setVisible(true);
                bool2 = true;
            }
        }

        var ft = vm.get('selectedFeatureNotFound'); //Handles the case in wich the selectedFeature is on a layer wich is now visible and must operate to make that feature visible
        if (bool2 && ft != null) {
            this.featureAlghorithm(CMDBuildUI.map.util.Util.getSelectionGeoValue());
            // this.findFeatureFromRow(vm.get('selectedFeatureNotFound'), false);
        }
    },

    /**
     * remove Layers from the map, provide to delate the selected feature too from the map
     * @param {Array[Objects]} list the list of record representing the layers to remove
     */
    removeLayerGis: function (list) {
        var layerList = this.getView().getLayerList();
        var map = this.getView().getOlMap();
        var vm = this.getViewModel();

        var selectedFeature = vm.get('selectedFeature');

        list.forEach(function (el) {
            var _id = el.get('_id');

            if (this.featureBelongsLayer(selectedFeature, el)) {
                this.udateNotFoundSelectedFeature(CMDBuildUI.map.util.Util.getSelectionGeoValue());
            }

            var layer = layerList[_id];
            map.removeLayer(layer);

            delete (layerList[_id]);
        }, this);
    },

    /**
     * Puts new layers on map
     * @param list  the list of layer. Has information about style
     */
    addLayerGis: function (list) {
        var me = this;
        list.forEach(function (el) {
            var layer;
            var _id = el.get('_id');

            switch ((el.get('type')).toLowerCase()) {
                case 'geometry':
                    var layer = me.buildGisLayer(el);
                    break;
                case 'shape':
                    var layer = this.buildGeoLayer(el);
                    break;
                default:
                    CMDBuildUI.util.Logger.log('Geometry not found', 'error');
                    break;
            }

            CMDBuildUI.map.util.Util.getOlMap().addLayer(layer);
            this.saveLayer(_id, layer);
            this.handleLayerVisibility(_id, layer);
        }, this);

        var suspendedFeatures = this.getViewModel().get('selectedFeatureNotFound');
        if (suspendedFeatures != null) {
            this.featureAlghorithm(CMDBuildUI.map.util.Util.getSelectionGeoValue());
        }

        var mapContainer = CMDBuildUI.map.util.Util.getMapContainer();
        mapContainer.fireEvent('layerlistchanged', this.getView().getLayerList());
    },

    /**
     * This function creates an hash table wich helps to find ol.layers by '_id'
     * @param id The id of the cmdbuild layer
     * @param gisLayer The ol.layer to save
     */
    saveLayer: function (id, gisLayer) {
        var layerList = this.getView().getLayerList();
        layerList[id] = gisLayer;
    },

    /**
     * This function adds the layer to the map and checks if it must be visible or not loocking at checkLayers
     * @param {String} id
     * @param {ol.layer} layer TODO: the layer
     */
    handleLayerVisibility: function (id, gisLayer) {
        var checkLayers = this.getViewModel().get('checkLayers');
        var parentTreeLayer = this.getParentLayerTree(gisLayer.get('type'), 1);
        var layerTreeStore = this.getViewModel().getParent().getView().lookupReference('tab-panel').lookupReference('map-tab-cards-layer').items.items[0].getStore();
        var node = layerTreeStore.findNode('id', parentTreeLayer);
        var check = node.get('checked');

        if (checkLayers[id] && checkLayers[id].checked == false || !check) {
            gisLayer.setVisible(false);
        } else {
            gisLayer.setVisible(true);
        }
    },

    /**
     * this function creates the layer using the geoserver WMS services
     * @param el the information about the node to add
     */
    buildGeoLayer: function (el) {

        if (!el.get('extent')) {
            CMDBuildUI.util.Logger.log(el.get('name') + " should have a specificated little extent to improve performance " + el.get('extent'), 'warn');
        }
        var geoLayer = new ol.layer.Tile({
            extent: el.get('extent')/* (el.get('extent')) ? (ol.proj.transformExtent(el.get('extent'),'EPSG:4326','EPSG:3857')) : undefined */,
            source: new ol.source.TileWMS({
                url: Ext.String.format('{0}/wms/', CMDBuildUI.util.Config.geoserverBaseUrl),
                params: {
                    'LAYERS': el.get('geoserver_name'),
                    'TILED': true
                },
                loader: function () {
                    return false;
                },
                serverType: 'geoserver'
            })
        });
        geoLayer.setZIndex(el.get('index'));
        geoLayer.set('type', el.data.type);
        return geoLayer;
    },

    /**
     * Constructs the gis layer taking care of all the aspects of the layer
     * @param {Ext.data.Model} layer Is a record. Describes the layer
     * @returns the ol.Layer type
     */
    buildGisLayer: function (layer) {
        var me = this;
        var vectorSource = new ol.source.Vector({
            strategy: ol.loadingstrategy.bbox
        });

        var olLayer = new ol.layer.Vector({
            source: vectorSource
        });
        olLayer.setZIndex(layer.get('index'));
        var thematism = layer.get('hasThematism');
        if (thematism != null) {
            thematism.calculateResults(function (data) {
                this.setThematism(olLayer, layer);
            }, this, thematism.get('tryRules'));
        } else {
            this.calculateStyle(olLayer, layer);
        }

        olLayer.set('_attr', layer.get('description'));
        olLayer.set('owner_type', layer.get('owner_type'));
        olLayer.set('_id', layer.get('_id'));
        olLayer.set('type', layer.get('type'));
        return olLayer;
    },

    /**
     * This function creates the click controls for the features
     */
    createControls: function () {
        var me = this;
        layerList = this.getView().getLayerList();
        objectType = this.getViewModel().get('objectTypeName');
        this.select = new ol.interaction.Select({
            /* toggleCondition: function(mbe){
                return (mbe.type == 'singleclick');
            }, */
            addCondition: function (mbe) {
                return (mbe.type == 'singleclick');
            },
            style: function (feature) {
                return me.findStyle.call(me, feature);
            },
            layers: function (layer) {
                var _owner_type = layer.get('owner_type');
                if (_owner_type == objectType) { //HACK: change here to make cliccable all points on map
                    return true;
                }
                return false;
            }
        });
        var me = this;
        var selectedFeaturesClick = this.select.getFeatures();

        this.select.on('select', function (event) {
            if (event.selected.length != 0) {
                selectedFeaturesClick.clear();
                selectedFeaturesClick.push(event.selected[0]);
                me.udateFoundSelectedFeature(event.selected[0]);

                var geoValue = Ext.create("CMDBuildUI.model.map.GeoElement", event.selected[0].get('data'));
                CMDBuildUI.map.util.Util.setSelection(geoValue.get('_owner_id').toString(), geoValue.get('_owner_type'));
            }
        });
        var map = this.getView().getOlMap();
        map.addInteraction(this.select);

        this.addBimInteraction();
    },

    /**
     * 
     * @param {ol.Vector} olLayer 
     * @param {Object} layer
     */
    calculateStyle: function (olLayer, layer) { //TODO: handle icons in style TODO: handle dash Styles
        var me = this;
        var type = layer.get('subtype')
        var style = layer._style;
        var icon = layer.get('_icon');

        var fillColor = this.hexToRgbA(style.get('fillColor'));
        if (fillColor != null) fillColor.push(style.get('fillOpacity'));

        var strokeWidth = style.get('strokeWidth');
        var strokeColor = this.hexToRgbA(style.get('strokeColor'));

        if (strokeColor != null) strokeColor.push(style.get('strokeOpacity'));

        var pointRadius = style.get('pointRadius');

        switch (type) {
            case 'POINT': {
                var image;
                var noIconPointStyle = function () {
                    olLayer.setStyle(function (feature) {
                        var basicStyle = [new ol.style.Style({ //point without bin and no icon
                            image: new ol.style.Circle({
                                fill: new ol.style.Fill({
                                    color: fillColor
                                }),
                                stroke: new ol.style.Stroke({
                                    width: strokeWidth,
                                    color: strokeColor
                                }),
                                radius: pointRadius

                            })
                        })];

                        // if (feature != null && feature.get('data') != null && feature.get('data').hasBim && feature.get('data').bimActive == true) {

                        //     basicStyle = me.getBimStyle('point-noIcon', { //point with bim and no icon
                        //         fillColor: fillColor,
                        //         strokeColor: strokeColor,
                        //         strokeWidth: strokeWidth,
                        //         pointRadius: pointRadius
                        //     });
                        // }
                        return basicStyle;
                    });
                };
                if (icon == null || icon == "") {  //point icon handler
                    noIconPointStyle();
                } else {
                    var img = new Image();
                    var url = Ext.String.format('{0}/uploads/{1}/download', CMDBuildUI.util.Config.baseUrl, icon);

                    img.onerror = function () {
                        noIconPointStyle();
                    };

                    img.onload = function () {

                        var width = img.width;
                        var height = img.height;
                        var coeff;

                        if (width > height) {
                            coeff = width / height;
                            width = pointRadius * 2;
                            height = width / coeff;
                        } else {
                            coeff = height / width;
                            height = pointRadius * 2;
                            width = height / coeff;
                        }

                        var canvas = document.createElement('canvas');
                        canvas.width = width;
                        canvas.height = height;
                        canvas.getContext('2d').drawImage(img, 0, 0, canvas.width, canvas.height);

                        olLayer.setStyle(function (feature) { //set the stile for the icon images
                            var point_icon = [new ol.style.Style({
                                image: new ol.style.Icon({
                                    img: canvas,
                                    imgSize: [width, height]
                                })
                            })];

                            // if (feature != null && feature.get('data') != null && feature.get('data').hasBim && feature.get('data').bimActive == true) {
                            //     point_icon = me.getBimStyle("point-icon", {
                            //         img: canvas,
                            //         imgSize: [width, height]
                            //     });
                            // }
                            return point_icon;
                        });

                    };

                    img.src = url;
                }
                break;
            }
            case 'POLYGON': {
                olLayer.setStyle(new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: strokeColor,
                        width: strokeWidth,
                        lineDash: undefined
                    }),
                    fill: new ol.style.Fill({
                        color: fillColor
                    })
                }));
                break;
            }
            case 'LINESTRING': {
                olLayer.setStyle(new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: strokeColor
                    })
                }));
                break;
            }
        }
    },

    /**
     * 
     * @param {ol.VectorTile} olLayer 
     * @param {Object} layer
     * @param {} img The img to use if exists
     */
    setThematism: function (olLayer, layer, img) {
        var me = this;
        var style = layer.getStyle();
        // var type = layer.get('subtype');

        var fillColor = this.hexToRgbA(style.get('fillColor'));
        if (fillColor != null) fillColor.push(style.get('fillOpacity'));

        var strokeWidth = style.get('strokeWidth');
        var strokeColor = this.hexToRgbA(style.get('strokeColor'));

        if (strokeColor != null) strokeColor.push(style.get('strokeOpacity'));

        var pointRadius = style.get('pointRadius');
        var thematism = layer.get('hasThematism');

        var results = thematism.get('result');
        var me = this;

        //Load the icon
        var icon = layer.get('_icon');
        if (!img && img !== false && icon) {
            //set the thematism with no icon as default
            me.setThematism(olLayer, layer, false);

            var img = new Image();
            var url = Ext.String.format('{0}/uploads/{1}/download', CMDBuildUI.util.Config.baseUrl, icon);

            img.onload = function () {
                me.setThematism(olLayer, layer, img);
            };
            img.src = url;
            return;
        }

        if (img) {
            //Images operations
            var width = img.width;
            var height = img.height;
            var coeff;

            if (width > height) {
                coeff = width / height;
                width = pointRadius * 2;
                height = width / coeff;
            } else {
                coeff = height / width;
                height = pointRadius * 2;
                width = height / coeff;
            }

            var canvas = document.createElement('canvas');
            canvas.width = width;
            canvas.height = height;
            canvas.getContext('2d').drawImage(img, 0, 0, canvas.width, canvas.height);
            //--images Operations
        }

        olLayer.setStyle(function (feature) {
            if (!feature) return null;
            var ownerId = feature.get('data')._owner_id;

            for (var i = 0; i < results.length; i++) {
                if (results[i]._id == ownerId) {
                    switch (layer.get('subtype').toLowerCase()) {
                        case 'point':
                            noIconStyle = function () {
                                return new ol.style.Style({
                                    image: new ol.style.Circle({
                                        fill: new ol.style.Fill({
                                            color: me.hexToRgbA(results[i].style.color)
                                        }),
                                        stroke: new ol.style.Stroke({
                                            width: strokeWidth,
                                            color: strokeColor
                                        }),
                                        radius: img ? pointRadius / 2 : pointRadius

                                    })
                                });
                            }

                            // var icon = layer.get('_icon');
                            if (!img) {
                                return noIconStyle();
                            } else {
                                var point_icon = [
                                    new ol.style.Style({
                                        image: new ol.style.Icon({
                                            img: canvas,
                                            imgSize: [width, height]
                                        })
                                    }),
                                    noIconStyle()
                                ];
                                return point_icon;
                            }
                            break;
                        case 'polygon':
                            return new ol.style.Style({
                                stroke: new ol.style.Stroke({
                                    color: strokeColor,
                                    width: strokeWidth,
                                    lineDash: undefined
                                }),
                                fill: new ol.style.Fill({
                                    color: results[i].style.color
                                })
                            });
                            break;
                        case 'linestring':
                            return new ol.style.Style({
                                stroke: new ol.style.Stroke({
                                    color: results[i].style.color
                                })
                            })
                            break;
                    }

                }
            }
        });
    },

    /**
     * This function creates a style function for the bim elements
     * @param data the object describing style of the feature
     * @returns {[ol.style]} an array containing style
     */
    getBimStyle: function (type, data) {
        //var style = feature.getStyle();
        switch (type) {
            case 'point-noIcon': {
                return [new ol.style.Style({
                    image: new ol.style.RegularShape({
                        fill: new ol.style.Fill({
                            color: data.fillColor
                        }),
                        stroke: new ol.style.Stroke({
                            width: data.strokeWidth,
                            color: data.strokeColor
                        }),
                        radius: data.pointRadius,
                        points: 4,
                        angle: Math.PI / 4
                    })
                })];
            }
            case 'point-icon': {
                // data.img.style.border = "2px double red"; //HACK: chenge style of icon with bim
                return [
                    new ol.style.Style({
                        image: new ol.style.Icon({
                            img: data.img,
                            imgSize: data.imgSize
                        })
                    }),
                    new ol.style.Style({
                        image: new ol.style.RegularShape({
                            stroke: new ol.style.Stroke({
                                width: 2,
                                color: 'red'
                            }),
                            radius: Math.hypot(data.imgSize[0], data.imgSize[1]) / 2 + 1,
                            points: 4,
                            angle: Math.PI / 4
                        })
                    })
                ];
            }
        }

    },

    /**
     * This function is used to create the style of the current selected feature by adding an extra 
     * style
     * @param {ol.feature.Feature} feature
     * @returns {ol.style.Style[]} the merged styles
     */
    findStyle: function (feature) {
        /**
         * Note: is not handled the case in wich the feature have is own style
         */
        var me = this;
        var layerList = me.getView().getLayerList();
        var owner_type = feature.get('data')._owner_type;
        var layerStyle;

        for (var el in layerList) {
            var layer = layerList[el];
            if (layer.get('owner_type') == owner_type) {
                layerStyle = layer.getStyleFunction()(feature);
                break;
            }
        }
        var selectionStyle = new ol.style.Style({
            image: new ol.style.Circle({
                fill: new ol.style.Fill({
                    color: 'rgba(255,165,0,1)'//'orange',//[0, 255, 0, 0.5],
                }),
                stroke: new ol.style.Stroke({
                    width: 1,
                    color: 'white'
                }),
                radius: 10

            }),
            fill: new ol.style.Fill({
                color: 'rgba(255,165,0,0.5)'//[255, 165, 0, 0.7], //'orange'
            }),
            stroke: new ol.style.Stroke({
                color: 'white',
                width: 2
            })

        });
        var result = [];

        switch (feature.get('type')) {
            case 'Point':
                if (layerStyle.length) { //handle the case in wich the the layer have an array of styles
                    layerStyle.forEach(function (el) {
                        result.push(el);
                    }, me);

                    result.push(selectionStyle);
                } else {  // handle the case in wich the layers have only one style, an ol.style.Style
                    result = [layerStyle, selectionStyle];
                }
                break;
            case 'Polygon':
                var result = [selectionStyle];
                break;
        }
        return result;

    },

    /**
     * This feature handle the process to add a a feature in the vectorSource with the correct style
     * @param {ol.VectorSource} vectorSource the openlayer vector source
     * @param {ol.Feature} feature the openlayyer feature 
     * @param checkNavigationTree the checkNavigationTree contained in the modelView
     */
    insertFeature: function (vectorSource, feature, checkNavigationTree) {
        if (!this.isVisibleFeature(feature, checkNavigationTree)) {
            this.setFeatureDisplay(feature, false);
        }
        vectorSource.addFeature(feature);
    },

    /**
     * @param {ol.Feature} feature
     * @param {Boolean} bool
     */
    setFeatureDisplay: function (feature, bool) {
        switch (bool) {
            case true:
                feature.setStyle(null) //restores the layers style
                break;
            case false:
                feature.setStyle([
                    new ol.style.Style({
                        visibility: 'hidden'
                    })
                ]);
                break;
            default:
                console.error('This shouldn \'t happend');
        }
    },
    /**
     * This function tells if a feature is visible or not
     * @param {ol.Feature} feature the theature
     * @param checkNavigationTree the checkNavigationTree contained in the modelView
     * MODIFY this function modifies the checkNavigationTree 
     */
    isVisibleFeature: function (feature, checkNavigationTree) {
        if (!checkNavigationTree) {
            checkNavigationTree = this.getViewModel().get('checkNavigationTree');
        }

        var type = feature.get('data')._owner_type;

        if (checkNavigationTree[type] != null) {
            var id = feature.get('data')._owner_id;
            if (checkNavigationTree[type][id] != null) {
                if (checkNavigationTree[type][id].visible == false) {
                    return false;
                } else {
                    delete checkNavigationTree[type][id];
                }
            }
        }
        return true;
    },

    /**
     * This function removes from the ol map the features individuated by the data in the list
     * @param {Object} list the checkNavigationTree contained in the modelView
     * @param {Object} list.checkListNT the actual Object where data are listed
     */
    removeFeaturesBynavigationTree: function (list) { //TODO: add the overlay functionality to save the selected point
        list = list.checkListNT;
        var selectedFeatureNotfound = this.getViewModel().get('selectedFeatureNotFound');
        if (selectedFeatureNotfound) {
            selectedFeatureNotfound = selectedFeatureNotfound[0];
            var ownerId = selectedFeatureNotfound.get('_owner_id');
            var ownerType = selectedFeatureNotfound.get('_owner_type');

            try {
                if (list[ownerType][ownerId].visible == true) {
                    var name = selectedFeatureNotfound.get('_attr');
                    var layer = this.getOlLayer(ownerType, name);
                    var source = layer.getSource();
                    var featureId = selectedFeatureNotfound.get('_id');
                    var olFeature = source.getFeatureById(featureId);

                    if (olFeature && layer.getVisible()) {
                        this.udateFoundSelectedFeature(olFeature);
                    }
                }
            } catch (e) { }
        };

        var selectedFeature = this.getViewModel().get('selectedFeature');
        if (selectedFeature) {
            var data = selectedFeature.get('data');
            var ownerId = data._owner_id;
            var ownerType = data._owner_type

            try {
                if (list[ownerType][ownerId].visible == false) {
                    this.udateNotFoundSelectedFeature(CMDBuildUI.map.util.Util.getSelectionGeoValue());
                }
            } catch (e) { };

        }

        var map = CMDBuildUI.map.util.Util.getOlMap();
        var layers = map.getLayers().getArray();
        var tmpList = {};

        layers.forEach(function (el) {
            var type = el.get('owner_type');
            if (list[type] != null) {
                tmpList[type] = {};
                this.manageFeatures(el, list[type], tmpList[type]);
            }
        }, this);

        this.updateCheckListNT(list, tmpList);
    },

    /**
     * This method change the visibility of the fature listed in ObjFeatures on the olLayer
     * assert the olLayer is the owner of those features
     * @param olLayer the openlayer layer
     * @param {Object} ObjFeatures contains description of the features
     * @param {boolean} ObjFeatures.visible Tells if the object must be visible or hidden 
     * @param {object} tmpList Saves all the cards being made visible again (same structure as checkListNT)
     */
    manageFeatures: function (olLayer, ObjFeatures, tmpList) {
        var olFeat = olLayer.getSource().getFeatures();

        olFeat.forEach(function (el) {
            var id = el.get('data')._owner_id;
            if (ObjFeatures[id] != null) {
                var tmp = ObjFeatures[id];
                if (tmp.visible == true) {
                    this.setFeatureDisplay(el, true);
                    tmpList[id] = ObjFeatures[id];
                } else {
                    this.setFeatureDisplay(el, false);
                }
            }
        }, this);
    },

    /**
     * this function removes the elements in list that appear in tmpList
     * NB: list and tmpList have the same structure. Elements in 'tmpList' must appear in 'list'
     * @param list the first list
     * @param tmpList the elements to remove from lis
     * 
     */
    updateCheckListNT: function (list, tmpList) {
        for (var type in tmpList) {
            for (var id in tmpList[type]) {
                delete list[type][id];
            }
        }
    },

    /**
     * this function handles the visibility of the interactionSelection
     * @param layer the ol.Layer
     */
    interactionSelectionVisibility: function (layer) {
        var vm = this.getViewModel();
        var feature = vm.get('selectedFeature');
        if (this.featureBelongsLayer(feature, layer)) {
            this.udateNotFoundSelectedFeature(CMDBuildUI.map.util.Util.getSelectionGeoValue());
        }
    },

    /**
     * Clear features selection
     * @param {CMDBuildUI.view.map.Map} view
     */
    clearFeaturesSelection: function (view) {
        this.getMapSelectInteraction().getFeatures().clear();
    },

    /**
     * This function sets hover and styling for the features with bim
     * Add's an event handler for the map pointer move
     */
    addBimInteraction: function () {
        var bimEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.enabled);
        if (!bimEnabled) {
            return;
        }

        this.addBimPopup();
        this.pointermoveMapAssign();

    },

    /**
     * Binds a function to pointermove event
     */
    pointermoveMapAssign: function () {
        var map = this.getView().getOlMap();
        map.on('pointermove', this.pointerMoveFunction, this);
    },

    /**
     * 
     */
    pointermoveMapUnassign: function () {
        var map = this.getView().getOlMap();
        map.un('pointermove', this.pointerMoveFunction, this);
    },
    /**
     * This is the function fired on 'pointermove' ol event
     * Is used to kwno when open the popup for the bim elements
     * @param event
     */
    pointerMoveFunction: function (event) {
        var me = this;
        var map = this.getView().getOlMap();
        var features = map.getFeaturesAtPixel(event.pixel);

        if (features) {
            var feature = features[0];
            var featureId = feature.id_;

            if (feature.get('data')._type == 'point') { //HACK: enable popup only for point type

                if (feature.get('data').hasBim && feature.get('data').bimActive && featureId != this._popupBim.lastFeatureId && !me._popupBimEvents.popupHover) {
                    this._popupBim.lastFeatureId = featureId;
                    this._popupBim.lastProjectId = feature.get('data').projectId;
                    this._popupBimEvents.featureHover = true;

                    var position = feature.getGeometry().getCoordinates();
                    this._popupBim.overlay.setPosition(position);
                }
            }
        }
        else {
            this._popupBimEvents.featureHover = false;
        }

        if (this._popupBimEvents.featureHover == false && this._popupBimEvents.popupHover == false) {
            this._popupBim.overlay.setPosition(undefined);
            this._popupBim.lastFeatureId = null;
        }
    },

    /**
     * creates the DOM element,the ol.Overlay element and adds it to the map
     */
    addBimPopup: function () {
        var me = this;
        /**
         * Creates the DOM element
         */

        var extEl = new Ext.button.Button({
            text: CMDBuildUI.locales.Locales.bim.showBimCard,
            localized: {
                text: 'CMDBuildUI.locales.Locales.bim.showBimCard'
            },
            renderTo: Ext.getBody(),
            handler: function () {
                CMDBuildUI.util.Utilities.openPopup('bimPopup', CMDBuildUI.locales.Locales.bim.bimViewer, { //FUTURE: create a configuration for passing the poid and ifctype
                    xtype: 'bim-container',
                    projectId: me._popupBim.lastProjectId
                });
            }
        });

        var element = extEl.getEl().dom;

        /**
         * Add controls to the dom elemnt
         */

        element.onmouseenter = function (mouseEvt) {
            me._popupBimEvents.popupHover = true;
        };

        element.onmouseleave = function (mouseEvt) {
            me._popupBimEvents.popupHover = false;

            if (me._popupBimEvents.featureHover == false && me._popupBimEvents.popupHover == false) {
                me._popupBim.overlay.setPosition(undefined);
                me._popupBim.lastFeatureId = null;
            }
        };


        /**
         * creates ol.Overlay
         */
        this._popupBim = {
            overlay: new ol.Overlay({
                element: element,
                id: 'bimMapPopup',
                offset: [0, -25],
                stopEvent: false
            }),
            element: element
        };
        this._popupBim.overlay.setPosition(undefined);

        var map = this.getView().getOlMap(); // this can be passed as argument of the function avoiding calling the view
        map.addOverlay(this._popupBim.overlay);
    },


    /**
     * @param  {ol.Feature} feature the interested feature 
     * @param  {Ext.data.Store} bimProjectStore the bim.Projects store. NOTE: is passed as argument to avoid calling Ext.getStore('bim.Projects') for each function call
     */
    setBimProperty: function (feature, bimProjectStore) {
        var me = this;
        var bimEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.enabled);

        if (!bimEnabled) {
            return;
        }

        /**
         * this function sets some values in the feature if ther is a match in the bim.Projects store 
         * @param _feature the feature being analized
         */
        setFeatureBim = function (_feature) {
            var _owner_id = _feature.get('data')._owner_id;
            var _owner_class = _feature.get('data')._owner_type;
            //vedere nei range dello store se trovo una corrispondenza tra owner id e owner type. se si assegno has bim alla feature
            var range = bimProjectStore.getRange();
            var bool = false;
            var i;

            for (i = 0; i < range.length && !bool; i++) {
                if (range[i].get('ownerClass') == _owner_class && range[i].get('ownerCard') == _owner_id) {
                    bool = true;
                }
            }


            if (bool) { //HACK: chose here the extra bim values to add to the feature 
                i--; //must do, set the i to the correct value. Because the for loop increases import {  } from "module";

                var data = _feature.get('data');
                data.hasBim = true;
                data.bimActive = range[i].get('active');
                data.projectId = range[i].get('projectId');

                //me.setBimStyle(_feature);
            }

        };

        if (bimProjectStore.isLoaded()) { //handles the case in wich the store is already loaded
            setFeatureBim(feature);
        } else {    //handle the case in wich the store is not yet loaded and used the callback function
            bimProjectStore.load({
                callback: setFeatureBim(feature),
                scope: this
            });
        }
    },
    privates: {

        /**
         * {object} object containing the ol.Overlay and the htmlElement used fo it
         */
        _popupBim: {
            /**
             * saves the information about the last feature wich opened the popup 
             */
            lastFeatureId: null,
            /**
             * Contains the html element used as popup
             */
            element: null,
            /**
             * contains the ol.Overlay object
             */
            overlay: null,
            /**
             * The id of the last project clicked
             */
            lastProjectId: null
        },

        _popupBimEvents: {
            popupHover: null,
            featureHover: null
        },

        /**
         * this functon sets a feature as selected. The feature is found in the list
         * @param {ol.Feature} feature the feature
         */
        makeSelection: function (feature) {
            var selectInteraction = this.getMapSelectInteraction();
            selectInteraction.getFeatures().clear();
            selectInteraction.getFeatures().push(feature);
        },

        /**
         * This function sets some data specified in the viewModel and modify it
         * @param {Object[]} obj contains (key - value) pairs
         */
        manualParametersBinding: function (obj) { //TODO: modify this function
            var vm = this.getViewModel().getParent();
            obj.forEach(function (element) {
                var key = element.key;
                var value = element.value;
                vm.set(key, value);
            });
        },


        /**
         * this function modifyes the input array of for numbers in a string format
         * NOTE: this format is used to make proxy calls
         * @param {number[]} bbox an array of 4 numbers
         * @returns {String} a string representing the array
         */
        stringfyBbox: function (bbox) {
            string = bbox[0];
            for (var i = 1; i < bbox.length; i++) {
                string = string + ',' + bbox[i];
            }
            return string;
        },

        /**
         * @returns the select interaction of the map
         */
        getMapSelectInteraction: function () {
            var map = this.getView().getOlMap();
            var interactionArray = map.getInteractions().getArray();
            var i = interactionArray.length - 1;

            while (i >= 0) {
                if (interactionArray[i] instanceof ol.interaction.Select) {
                    return interactionArray[i];
                }
                i--;
            }
        },

        /**
        * This data tells if a feature belong to a layer
        * @param {[Ext.data.Model] || ol.Feature} feateure the feature saved in the view model as selectedFeature
        * @param {ol.layer} layer the layer
        * @return true if belongs, false otherwose
        */
        featureBelongsLayer: function (feature, layer) {
            var owner = layer.get('owner_type');
            var name = layer.get('name') ? layer.get('name') : layer.get('_attr');

            if (feature instanceof ol.Feature) {
                var featureData = feature.get('data');
                if (feature != null && featureData._owner_type == owner && featureData._attr == name) { //i can use the layer.getSource().getfeatureById(feature._id) 
                    return true;
                }
                return false;
            } else {
                if (feature != null && feature.get('_attr') == name && feature.get('_owner_type') == owner) {
                    return true;
                }
                return false;
            }
        },

        /**
         * this function generates a DOM element
         * @return the dom element
         */
        generateEl: function () {
            /**Create the dom element with this style */
            var el = document.createElement('div');
            el.id = 'overlayId';
            el.style.width = '10px';
            el.style.height = '10px';
            el.style.borderRadius = '100%';
            el.style.backgroundColor = 'orange';
            el.style.marginLeft = '-50%';
            el.style.marginTop = '-50%';

            return el;
        },

        /**
         * This function changes the format of color so it can be readable from openlayer
         * @param {String} hex the input hexadecimal
         * @return an array rappresenting the hex color
         */
        hexToRgbA: function (hex) {
            if (hex == null) return;
            var c;
            if (/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)) {
                c = hex.substring(1).split('');
                if (c.length == 3) {
                    c = [c[0], c[0], c[1], c[1], c[2], c[2]];
                }
                c = '0x' + c.join('');
                return [(c >> 16) & 255, (c >> 8) & 255, c & 255]; // to set the transparency value add the alpha parameter to the returned ones
            }
            throw new Error('Bad Hex');
        },

        /**
         * Update found selected feature
         * @param {[Ext.data.Model] || ol.Feature} feature 
         */
        udateFoundSelectedFeature: function (feature) {
            this.getViewModel().getParent().set('selectedFeature', feature);
            this.getViewModel().getParent().set('selectedFeatureNotFound', null);
            this.makeSelection(feature);
        },

        /**
         * Update not found selected feature
         * @param {[Ext.data.Model]} feature model:CMDBuildUI.model.map.GeoElement
         */
        udateNotFoundSelectedFeature: function (feature) {
            this.getViewModel().getParent().set('selectedFeatureNotFound', feature);
            this.getViewModel().getParent().set('selectedFeature', null);
            this.clearFeaturesSelection();
        },

        /**
         * @param {[Ext.data.Model]} features model: CMDBuildUI.model.map.GeoElement
         */
        featureAlghorithm: function (features) {
            if (features && features.length != 0) {
                var feature = features[0];
                var owner_type = feature.get('_owner_type');
                var _attr = feature.get('_attr');
                var _id = feature.get('_id');

                var layer = this.getOlLayer(owner_type, _attr);

                if (layer) {
                    delete layer.getSource().listeners_.addfeature; //FIXME: study this case;
                    var olFeature = this.getFeatureFromLayer(owner_type, _attr, _id);
                    if (olFeature) {
                        var checknavigation = this.getViewModel().get('checkNavigationTree');
                        if (this.isVisibleFeature(olFeature, checknavigation) & layer.getVisible()) {
                            this.udateFoundSelectedFeature(olFeature);
                        } else {
                            this.udateNotFoundSelectedFeature([feature]);
                        }
                    } else {
                        this.udateNotFoundSelectedFeature([feature]);
                        layer.getSource().on('addfeature', function (featureEvt) {
                            if (featureEvt.feature.getId() == _id) {
                                delete featureEvt.target.listeners_.addfeature;

                                var olFeature = featureEvt.feature;
                                var checknavigation = this.getViewModel().get('checkNavigationTree');
                                if (this.isVisibleFeature(olFeature, checknavigation) & layer.getVisible()) {
                                    this.udateFoundSelectedFeature(olFeature);
                                }
                                return;
                            }
                        }, this);
                    }
                } else {
                    this.udateNotFoundSelectedFeature([feature]);
                }
            } else {
                this.udateNotFoundSelectedFeature(null);
            }
        },

        /**
         * This function shearces the layer identified by it's owner type and name. if name (_attr) is "_ANY" returns all the layers that belongs to that owner type.
         * The "_ANY" can take more time to execute task
         * @param {String} owner_type
         * @param {String} attr
         * @returns {[ol.Layer] || ol.layer} returns the array of layer if is added (with "_ANY"); the single olLayer when univoque; null otherwise
         */
        getOlLayer: function (owner_type, _attr) {
            var layerList = this.getView().getLayerList();

            switch (_attr) {
                case '_ANY':
                    var returned = [];
                    for (el in layerList) {
                        var layer = layerList[el];
                        if (layer.get('owner_type') == owner_type) {
                            returned.push(layer);
                        }
                    }
                    if (returned.length != 0) return returned;
                    return null;
                    break;
                default:
                    for (el in layerList) {
                        var layer = layerList[el];
                        if (layer.get('owner_type') == owner_type && layer.get('_attr') == _attr) {
                            return layer;
                        }
                    }
                    return false;
                    break;
            }
        },

        /**
         * //TODO: make the variant that passes directly the ol.Layer
         * This function tells if the ol.Feature specified by featureId is in the ol.Layer
         * @param {String} owner_type 
         * @param {String} _attr 
         * @param {String} featureId 
         * @returns {ol.Feature} the feature if found. null otherwise
         */
        getFeatureFromLayer: function (owner_type, _attr, featureId) {
            var olLayer = this.getOlLayer(owner_type, _attr);
            try {
                return olLayer.getSource().getFeatureById(featureId);
            } catch (e) {
                console.error(e);
                return null;
            }
        }
    },

    /**
     * This function gets the name of parent nodes based on type
     * @param {string} type geometry or shpe
     * @param {number} depth how many parents to return, if null gives the first one
     */
    getParentLayerTree: function (type, depth) {
        switch (type) {
            case 'geometry':
                return 'geoAttributesRoot';
            case 'SHAPE':
                return 'externalLayersNode';
            default:
                console.error('SHAPE NAME IS NOT CORRECT!');
                return null;
        }
    }
});
