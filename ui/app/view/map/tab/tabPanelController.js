//TODO: move privates in separte file
Ext.define('CMDBuildUI.view.map.tab.tabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-tabpanel',

    control: {
        '#': {
            afterrender: 'onAfterRender',
            tabchange: 'onTabChange',
            addbtnclick: 'onAddButtonClick',
            modifybtnclick: 'onModifyButtonClick',
            removebtnclick: 'onRemoveButtonclick',
            viewbtnclick: 'onViewButtonClickHandler',
            beforerender: 'onBeforeRender'

        },

        '#map-legend': {
            disable: 'onLegendDisable',
            enable: 'onLegendEnable'
        }
    },

    /**
     * fired by it's view Model
     * @event selectedchangeevent
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
        console.log('Handle the selectionChangeEvent from the tabPabel');
        var view = this.getView().down('#cancelButton');
        if (view) {
            this.onCancelButtonClick(view);
        }

        /**
         * makes visible the card tab
         */
        var items = this.getView().getTabBar().items.items;
        var cardTabView = items[items.length - 2]; //get the card tab
        if (cardTabView.isDisabled()) {
            cardTabView.setDisabled(false);
        }

        this.getViewModel().set('objectTypeName', selected.type);
        this.getViewModel().set('objectId', selected.id);
    },

    /**
     * mantains active the tab wich was active before
     * @param _this
     * @param eOpts
     */
    onAfterRender: function (_this, eOpts) {
        /**
         * adds the tree tab
         */

        /**
       * Temporary fixup
       */
        var allNavTrees = Ext.getStore('navigationtrees.NavigationTrees');
        var i = allNavTrees.find('_id', 'gisnavigation');

        if (i !== -1) {
            var vm = this.getView().lookupViewModel('map-container');
            vm.set('theTree', allNavTrees.getAt(i));

            _this.insert(0, [{
                title: CMDBuildUI.locales.Locales.gis.tree,
                xtype: 'map-tab-cards-navigationtree',
                layout: 'fit',
                reference: 'map-tab-cards-navigationtree',
                localized: {
                    title: 'CMDBuildUI.locales.Locales.gis.tree'
                }
            }]);
            _this.setActiveTab(0);
        }
    },

    /**
     * Here we set the disabled Tab CARD
     */
    onBeforeRender: function (_this, eOpts) {
        _this.mon(CMDBuildUI.map.util.Util.getMapGridContainerView(), 'selectedchangeevent', this.onSelectedChange, this);
    },

    reset: function () {
        this.configRestore();
    },
    /**
     * @param tabPanel, 
     * @param newCard, 
     * @param oldCard, 
     * @param eOpts
     */
    onTabChange: function (tabPanel, newCard, oldCard, eOpts) {
        var n = tabPanel.items.findIndex('id', newCard.id);
        this.getViewModel().getParent().getParent().getParent().set('activeMapTabPanel', n);
    },

    /**
     * 
     * @param {Ext.Componend} legendTab 
     * @param {eOpts} eOpts 
     */
    onLegendDisable: function (legendTab, eOpts) {
        //if the active tab get's disabled change the active one
        if (this.getView().getActiveTab().getId() == legendTab.getId()) {
            this.getView().setActiveTab(0);
        }
    },

    /**
     * 
     * @param {Ext.Component} legendTab 
     * @param {eOpts} eOpts 
     */
    onLegendEnable: function (legendTab, eOpts) {
        this.getView().setActiveTab(legendTab);
    },

    // ------------------------------------------------------------------------------------//
    // ---------- handle the modification of arguments ----------//

    onAddButtonClick: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {
        if (!this._addingMode[record.get('name')]) {

            this.addSaveCancelButton();
            this.mapPrepare(record);
            this.addDrawInteraction(record);
            this.pointermoveMapUnassign();

            this.initBuffer(record, 'Start');
            this.bufferOperation('add', {
                record: record
            });

            this._addingMode[record.get('name')] = true;
        } else {
            console.log('you are still adding this layer. Press save or cancel');
        }

    },
    onModifyButtonClick: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {

        if (this._editMode == true) {
            console.log('You are yet in editMode');
        } else {
            this.findOlMap();
            this.createTmpOlLayer(record.get('layer_id'));
            this.initBuffer(record, 'Start');
            this.bufferOperation('edit', {
                record: record
            });

            this.moveFeature({ record: record });
            this.pointermoveMapUnassign();

            //this.onViewButtonClick.call(this, actioncolumn, rowIndex, colIndex, item, event, record, row);

            this.addSaveCancelButton();
            this.setSaveCancelButtons([false, false]); //TODO: set after the mofify end


            this._editMode = true;
        }
    },
    onRemoveButtonclick: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {

        this.findOlMap();
        this.initBuffer(record, 'Start');

        if (this.isRemovable(record)) {

            this.disableZoomControl();
            this.bufferOperation('remove', {
                record: record
            });
            this.addSaveCancelButton();
            this.setSaveCancelButtons([false, false]);

        } else {
            CMDBuildUI.util.Notifier.showWarningMessage(
                'Go to ' + record.get('zoomDef') + 'zoom livel for being able to remove this feature'
            );
        }


        console.log(arguments);
    },
    onViewButtonClickHandler: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {
        //FUTURE: Decidere se portare al livello di zoom predefinito e adattarsi in base al livello di zoom in cui ci si trova attualmente
        this.onViewButtonClick(record.get('zoomMin'), record.get('zoomMax'), record.get('zoomDef'), this.getCenter(record));
    },

    /**
     * @param {string} zoomMin
     * @param {string} zoomMax
     * @param {String} zoomDef
     * @param {[Number]} center
     */
    onViewButtonClick: function (zoomMin, zoomMax, zoomDef, center) {
        this.findOlMap();
        var mapView = this._map.getView();
        var zoom = mapView.getZoom();
        if (zoom < zoomMin || zoom > zoomMax) {
            mapView.setZoom(zoomDef);
        }
        mapView.setCenter(center);
    },

    /**
     * 
     */
    mapviewEdit: function (args) {
        this.findOlMap();
        var mapView = this._map.getView();

        var zoom = args.zoom;
        var center = args.center;

        if (zoom) mapView.setZoom(zoom);
        if (center) mapView.setCenter(center);
    },

    /**
     * This function is executed by the tre functions who can make appear the 2 buttons: modify,add and remove. 
     * The function checks a global variable to dicide if the 
     * buttons need to be addoed or not to the view. That variable is set again once save or cancel is clicked
     */
    addSaveCancelButton: function () {
        var present = this._saveCancelBtnPresent;

        if (present == false) {
            this._saveCancelBtnPresent = true;
            var buttonCmp = this.buttonCreation();
            var view = this.getView().down('map-tab-cards-card').lookupReference('map-geoattributes-grid').up().up();
            view.insert(1, buttonCmp);
        }
        return;
    },

    /**
     * this function removes from the view the 2 buttons
     * @param  {} view
     */
    removeSaveCancelButtons: function (view) {
        this._saveCancelBtnPresent = false;
        view = view.up();
        this.getView().down('map-tab-cards-card').lookupReference('map-geoattributes-grid').up().up().remove(view);
    },

    /**
     * this functions sets the disable value for the save and cancel button
     * @param {[Boolean]} values values[0] is for saveButton; values[1] is for cancelButton
     * if the values[i] = null will not be set a new value
     */
    setSaveCancelButtons: function (values) {
        var view = this.getView().down('map-tab-cards-card');
        var save = view.lookupReference('saveButton');
        var cancel = view.lookupReference('cancelButton');

        if (values[0] != null) {
            save.setDisabled(values[0]);
        }

        if (values[1] != null) {
            cancel.setDisabled(values[1]);
        }
    },

    /**
     * @param  {} view
     * @param  {} eOpts
     */
    onSaveButtonClick: function (view, eOpts) {
        /**
         * other operations for saveButton are located in the callback of the prepareAjax function
         */
        this.removeSaveCancelButtons(view);
        this.prepareAjaxCall();
        this.enableZoomControl();
        this.enableSelectInteraction();
        this.removeTmpOlLayer();
    },

    /**
     * @param  {} view
     * @param  {} eOpts
     */
    onCancelButtonClick: function (view, eOpts) {
        this.removeSaveCancelButtons(view);
        this.restoreActioncolumnsState();
        this.enableZoomControl();
        this.enableSelectInteraction();
        this.removeModifyInteraction();
        this.removeDrawInteraction();
        this.removeTmpOlLayer();
        this.reloadSource();
        this.configRestore();
        this.triggerSelectedRow();
        this.pointermoveMapAssign();
    },

    triggerSelectedRow: function () {
        var selection = CMDBuildUI.map.util.Util.getSelection();
        CMDBuildUI.map.util.Util.setSelection(selection.id, selection.type)
    },

    /**
     * @param {Object} config
     */
    disableAllActionColumns: function (conf) {
        var view = this.getView().down('map-tab-cards-card').lookupReference('map-geoattributes-grid');
        var records = view.getStore().getRange();

        records.forEach(function (record) {
            this.saveActioncolumnsState(record);
            this.setActioncolumnState({
                record: record,
                values: [true]
            });
        }, this);
    },

    /**
     * This function stores in an object the value of the action for the given record
     * @param {Object} arg
     * @param {Object} arg.record the involved record
     */
    saveActioncolumnsState: function (arg) {
        var record = arg.record;
        if (record) {
            var name = record.get('name');
            this._actioncolumnsState[name] = {
                add: record.get('add'),
                edit: record.get('edit'),
                remove: record.get('remove'),
                view: record.get('remove')
            };
        }
    },

    /**
     * This function saves the state of all the actioncolumns of each actioncolumn
     */
    saveAllActionColumnsState: function () {
        var view = this.getView().down('map-tab-cards-card').lookupReference('map-geoattributes-grid');
        var records = view.getStore().getRange();

        records.forEach(function (record) {
            var name = record.get('name');
            if (!this._actioncolumnsState[name]) {
                this.saveActioncolumnsState({
                    record: record
                });
            }
        }, this);
    },

    /**
     * this function restores the the actioncolumns values saved in the _actioncolumnsState
     */
    restoreActioncolumnsState: function () {
        var view = this.getView().down('map-tab-cards-card').lookupReference('map-geoattributes-grid');
        var range = view.getStore().getRange();

        range.forEach(function (record) {
            var name = record.get('name');
            var state = this._actioncolumnsState[name];
            if (state != null) {
                this.setActioncolumnState({
                    record: record,
                    values: [
                        state.add,
                        state.edit,
                        state.remove,
                        state.view
                    ]
                });
            }
        }, this);
    },
    /**
     * this function set the action column state of the given record
     * record[i] will set the i-th action column
     * if record has length 1, to all the actioncolumns will be given the same value
     * @param  {Object} args
     * @param {Recold} args.record The interested record
     * @param  {[boolean]} args.values the argument of isDisabled() method
    */
    setActioncolumnState: function (args) {

        var record = args.record;
        if (!record) CMDBuildUI.util.Error.showInfoMessage("Record shouldn't be null");

        var values = args.values;
        if (!values) values = this.get('_actioncolumnState');

        var a, b, c, d;
        if (values.length == 1) {
            a = b = c = d = 0;
        } else {
            a = 0; b = 1; c = 2; d = 3;
        }
        record.set('add', values[a]);
        record.set('edit', values[b]);
        record.set('remove', values[c]);
        record.set('view', values[d]);

    },

    configRestore: function () {
        this._buffer = {};
        this._lastDFD = null;
        this._usedLayer = {};
        this._actioncolumnsState = {};
        this._editMode = false;
        this._addingMode = {};
    },

    /**
     * this function changes the state of the map
     * @param  {Object} record
     */
    mapPrepare: function (record) {
        this.disableZoomControl();  //in CMDBuild
        if (!this._map) {
            this.findOlMap();
        }

        this.disableSelectInteraction();

        if (!this._olTmpLayer) {
            this.createTmpOlLayer(record.get('layer_id'));
        }
    },

    /**
     * this function removes the actual source of the vectorLayers and force to refresh the data form the server
     */
    reloadSource: function () {
        var view = this.getView().down('map-tab-cards-card').lookupReference('map-geoattributes-grid');
        var records = view.getStore().getRange();

        records.forEach(function (record) {
            var id = record.get('layer_id');
            var olLayer = this.findOlLayer(id);
            if (olLayer) {
                var olSource = olLayer.getSource();
                olSource.clear();
                olSource.refresh();
            }
        }, this);
    },

    /**
     * sets the array coordinates on the pending attribute
     * @param {[Ol.Feature]}
     * @param {DFDNode} dfdNode the dfdNode in wich save the feature. If not specified, it will be saved in the .currentNode of the DFD
     */
    saveFeature: function (arg, dfdNode) { //TODO: modify this function
        var feature = arg.feature;
        var coordinates = feature.getGeometry().getCoordinates();
        var DFD = this._lastDFD;

        if (!dfdNode) {
            DFD.setNodeKey('currentNode', 'params', this.paramsManipulation(
                DFD.get('_type'), coordinates)
            );
            DFD.setNodeKey('currentNode', 'feature', feature);
        } else {
            dfdNode.set('params', this.paramsManipulation(
                DFD.get('_type'), coordinates)
            );
            dfdNode.set('feature', feature);
        }
        return;
    },

    /**
     * This function removes from the map the feature with id saved in the record on the olLayer
     * if 
     * @param {Object} args.record information about the clicked record 
     */
    removeFeature: function (args) {

        var record = args.record;
        var DFD = this._lastDFD;
        var feature = DFD.getNodeKey('Add', 'feature');
        var id = record.get('_id');
        var source;

        if (feature) {
            this._olTmpLayer.getSource().removeFeature(feature);
        } else {
            var layer = this.findOlLayer(record);
            if (layer) {
                source = layer.getSource();
                if (id) {
                    feature = source.getFeatureById(id);
                }
            }
            if (feature) {
                source.removeFeature(feature);
                this.findSelectInteraction();
                this._selectInteraction.getFeatures().clear();
            }
        }

    },

    /**
  * this functions moves a feature from his original layer to the ol_tmpLayer
  */
    moveFeature: function (args) {

        var record = args.record;
        var layer = this.findOlLayer(args.record);

        var feature_found = function (feature, layer) {
            var clone = feature.clone();

            if (layer === this._olTmpLayer) {
                this.addModifyInteraction({
                    feature: clone,
                    record: record
                });
            } else {
                this._olTmpLayer.getSource().addFeature(clone);
                layer.getSource().removeFeature(feature);
                feature.setStyle(new ol.style.Style({}));
                this.addModifyInteraction({
                    feature: clone,
                    record: record
                });
            }
        };

        var listener_source = function (args) {
            this.disableZoomControl();
            var feature = args.feature;
            if (feature.get('data')._id == record.get('_id')) {
                feature_found.call(this, feature, layer);

                layer.getSource().un('addfeature', listener_source, this);
            }
        };

        var listener = function () { //TODO: vedere quali sono gli argomenti passati
            layer = this.findOlLayer(record);

            if (layer) {
                layer.getSource().on('addfeature', listener_source, this);
                this._map.getLayers().un('propertychange', listener, this);
            }
        };

        if (!layer) {
            this._map.getLayers().on('propertychange', listener, this);
            this.mapviewEdit({
                zoom: record.get('zoomDef'),
                center: this.getCenter(record)
            });
        }
        else {
            this.disableZoomControl();
            //feature = this._lastDFD.getNodeKey('Add', 'feature'); //IMPROVE: i think this part of code can be deleted. No more feature are foun in the add 
            // if (feature) {
            //     layer = this._olTmpLayer;
            //     feature_found.call(this, feature, layer);
            // } else {
            feature = layer.getSource().getFeatureById(record.get('_id'));
            if (!feature) {
                this.mapviewEdit({
                    center: this.getCenter(record.record)
                });
                layer.getSource().on('addfeature', listener_source, this);
            } else {
                feature_found.call(this, feature, layer);
                // }
            }

        }
        // this.findOlMap();
        // var mapView = this._map.getView();
        // mapView.setZoom(record.get('zoomDef'));
        // var center = this.getCenter(record);
        // mapView.setCenter(center);

    },

    /**
     * Tells if a determinated feature il visible on layer now and rady for manipulation
     * @param record
     */
    isRemovable: function (record) {
        var DFD = this._lastDFD;
        var feature = DFD.getNodeKey('Add', 'feature');
        var id = record.get('_id');

        if (feature) {
            return true;
        } else {
            var layer = this.findOlLayer(record);
            if (layer) {
                var source = layer.getSource();
                if (id) {
                    feature = source.getFeatureById(id);
                }
            }
            if (feature) {
                return true;
            } else {
                return false;
            }
        }
    },

    /**
     * this function gets the center of the feature
     *  @param {Object} record information about the clicked record
     * @return {[Number,Number]} the coordinate format
     */
    getCenter: function (record) {
        var name = record.get('name');
        var feature = null;
        var coordinates = null;

        if (this._buffer[name]) {
            var DFD = this._buffer[name].DFD;
            feature = DFD.getNode('Add').get('feature');
        }

        if (feature) {
            coordinates = feature.getGeometry().getCoordinates();
            return this.getFirstCoordinates(coordinates, record.get('subtype'));
        } else {
            return record.get('coordinates');
        }

    },

    /**
     * @param {[Number] || [[Numbers]]} an array of coordinates
     * @param {String} type the type of the geometry
     */
    getFirstCoordinates: function (coordinates, type) {
        switch (type) {
            case 'POINT':
                return coordinates;
            case 'LINESTRING':
                return coordinates[0];
            case 'POLYGON':
                return coordinates[0];
        }
    },

    /**
     * This function returns the ol layer of the clicked layer
     * @param {Object || String} recordOrString
     */
    findOlLayer: function (recordOrString) {
        var olLayer;
        if (typeof recordOrString != 'string') {
            recordOrString = recordOrString.get('layer_id');
        }
        this._map.getLayers().getArray().forEach(function (layer) { //TODO: change with a wile loop
            var id = layer.get('_id');
            if (id == recordOrString) {
                olLayer = layer;
            }
        }, this);
        return olLayer;
    },
    /**
     * This function saves the openLayers library in an auxiliary value
     */
    findOlMap: function () {
        if (!this._map) {
            var mapView = this.getViewModel().getParent().getView().lookupReference('map');
            this._map = mapView.getOlMap();
        }
    },

    /**
     * disables the functionanlity of the zoom
     */
    disableZoomControl: function () {
        var vm = this.getViewModel();
        vm.getParent().set('zoomDisabled', true);
    },

    /**
     * enables the functionanlity of the zoom 
     */
    enableZoomControl: function () {
        var vm = this.getViewModel();
        vm.getParent().set('zoomDisabled', false);
    },

    /**
     * this function saves in a local variable a pointer to the select interaction
     */
    findSelectInteraction: function () {
        this._map.getInteractions().getArray().forEach(function (intr) {
            if (intr instanceof ol.interaction.Select) {
                this._selectInteraction = intr;
            }
        }, this);
    },
    /**
     * this function disables the select interaction and saves that interaction in a variable
     */
    disableSelectInteraction: function () {
        if (this._selectInteraction) {
            this._selectInteraction.setActive(false);
        } else {
            this.findSelectInteraction();
            this.disableSelectInteraction();
        }
    },
    /**
     * enables the ol.selectiInteraction
     */
    enableSelectInteraction: function () {
        if (this._selectInteraction) {
            this._selectInteraction.setActive(true);
            this._selectInteraction = null;
        }
    },

    /**
     * this function creates the temporary openlayer layer
     * @param {String} layer_id the id of the layer interested by the draw or modify interaction
     */
    createTmpOlLayer: function (layer_id) {
        var tmpVectorSource = new ol.source.Vector();
        var style;
        this._map.getLayers().forEach(function (layer) {
            if (layer.get('_id') == layer_id) {
                style = layer.getStyle();
            }
        }, this);

        this._olTmpLayer = new ol.layer.Vector({ //save _olTmpLayer
            source: tmpVectorSource,
            style: new ol.style.Style({
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

            })
            // style: style //HACK: change ModifyInteraction Style
        });
        this._olTmpLayer.setZIndex(5555); //a big zIndex
        this._map.addLayer(this._olTmpLayer);
    },

    /**
     * this function removes the new temporary olLayer 
     */
    removeTmpOlLayer: function () {
        if (this._olTmpLayer) {
            this._map.removeLayer(this._olTmpLayer);
            this._olTmpLayer = null;
        }
    },


    /**
     * @param  {object} record 
     */
    addDrawInteraction: function (record) {

        var listener = function (args) {

            this.bufferOperation('end', {
                record: record,
                feature: args.feature
            });

            //this.bufferAdd(args.feature); //TODO: imprve here
            this.setSaveCancelButtons([false, null]);
        };
        var style;
        this._map.getLayers().forEach(function (layer) {
            if (layer.get('_id') == record.get('layer_id')) {
                style = layer.getStyle();
            }
        }, this);

        if (!this._drawInteraction) {
            this._drawInteraction = new ol.interaction.Draw({
                source: this._olTmpLayer.getSource(),
                type: this.stringFormatter(record.get('subtype'))
                // style: style //HACK: change drawInteraction style
            });

            this._drawInteraction.on('drawend', listener, this);

            this._map.addInteraction(this._drawInteraction);
        } else {
            this._drawInteraction.setActive(true);
        }

    },

    /**
     * this function removes the draw interaction from the map if esists
     */
    removeDrawInteraction: function () {
        if (this._drawInteraction) {
            this._map.removeInteraction(this._drawInteraction);
            this._drawInteraction = null;
        }
    },

    /**
     * adds the ol.interaction.Modify interaction to the mapp 
     * @param {ol.Feature} args.feature the feature to set as editable
     */
    addModifyInteraction: function (args) {
        var listener = function (e) {
            //var DFD = this._lastDFD;
            //DFD.setNodeKey('AddingEditing', 'feature', e.features.getArray()[0]);
            this.saveFeature({
                'feature': e.features.getArray()[0]
            });

        };

        var olTmpStyle = this._olTmpLayer.getStyle();
        if (!this._modifyInteraction) {
            this._modifyInteraction = new ol.interaction.Modify({
                features: new ol.Collection([args.feature]),
                style: olTmpStyle
            });
            this._modifyInteraction.on('modifyend', listener, this);
            this._map.addInteraction(this._modifyInteraction);
        }
    },

    /**
     * Removes the modify interaction from the map and from the variables
     */
    removeModifyInteraction: function () {
        if (this._modifyInteraction) {
            this._map.removeInteraction(this._modifyInteraction);
            this._modifyInteraction = null;
        }
    },

    /**
     * removes the pointermove event listener 
     */
    pointermoveMapUnassign: function () {
        this.getViewModel().getParent().getView().lookupReference('map').getController().pointermoveMapUnassign();
    },

    /**
     * adds the pointermove event handler
     */
    pointermoveMapAssign: function () {
        this.getViewModel().getParent().getView().lookupReference('map').getController().pointermoveMapAssign();
    },
    /**
     * this function initialises the buffer
     * @param {Object} record informations about the actioncolumn clicked
     * @param {String} action the action taken
     */
    initBuffer: function (record, action) {
        var name = record.get('name');
        var token = record.get('_id') == null ? true : false;
        var layer_id = record.get('layer_id');

        if (!this._buffer[name]) {
            var DFD = this.DFDCreator(token, action);
            DFD.set('_type', record.get('subtype'));
            this._buffer[name] = {
                layer_id: record.get('layer_id'),
                DFD: DFD
            };
        }

        this._lastDFD = this._buffer[name].DFD;

        if (!this._usedLayer[layer_id]) { //TODO: check why this is here
            this._usedLayer[layer_id] = true;
        }
    },

    /**
     * this function makes the operation transaction in the DFD
     * @param {String} operation
     * @param {Object} arg informations about the clicked row
     */
    bufferOperation: function (operation, arg) {
        var DFD = this._lastDFD;    //assert: the last dfd must be setted on button clicks
        DFD.transaction(operation, arg);
    },

    /**
     * this function creates and initializates a DFD structure
     * @param {Boolean} token nedded to attach information to the remove node;
     * @param {String} action sets the starting node in the DFD;
     * @return {DFD} the created DFD
     */

    DFDCreator: function (token, action) {
        var startingNode = new this.DFDNode('Start', this);
        startingNode.addOnExit(this.saveAllActionColumnsState, this);

        var addNode = new this.DFDNode('Add', this);
        addNode.set('_actioncolumnState', [false, false, false, false]);
        addNode.addOnEnter(this.restoreActioncolumnsState, this);
        addNode.addOnEnter(this.setActioncolumnState);//HACK: Change the activation or disativation of the icons

        //addNode.addOnExit(this.removeDrawInteraction, this);

        var removeNode = new this.DFDNode('Remove', this);
        removeNode.set('_actioncolumnState', [false, true, true, true]);
        removeNode.addOnEnter(this.setActioncolumnState);
        removeNode.addOnEnter(this.removeFeature, this);

        var editing = new this.DFDNode('Editing', this);
        editing.set('_actioncolumnState', [true, true, true, true]);
        //editing.addOnEnter(this.moveFeature, this);
        editing.addOnEnter(this.disableAllActionColumns, this);
        editing.addOnEnter(this.setActioncolumnState);
        editing.addOnExit(this.removeModifyInteraction, this);

        var addingDrawing = new this.DFDNode('AddingDrawing', this);
        addingDrawing.set('_actioncolumnState', [true, true, true, true]);
        addingDrawing.addOnEnter(this.disableAllActionColumns, this);
        addingDrawing.addOnEnter(this.setActioncolumnState);
        addingDrawing.addOnExit(function (args) {
            this.saveFeature(args, addingEditing);
        }, this);
        addingDrawing.addOnExit(this.removeDrawInteraction, this);

        var addingEditing = new this.DFDNode('AddingEditing', this);
        addingEditing.set('_actioncolumnState', [true, true, true, true]);
        addingEditing.addOnEnter(this.addModifyInteraction, this);
        addingEditing.addOnExit(this.removeModifyInteraction, this); //LOOK:the on exit is never executed because thers no exit from this state
        //addNode.addOnEnter(this.saveFeature, this);

        var endNode = new this.DFDNode('EndNode', this);

        startingNode.addTransaction([['remove', 'Remove'], ['edit', 'Editing'], ['add', 'AddingDrawing']]);
        addNode.addTransaction([['remove', 'Remove'], ['edit', 'Editing']]);
        removeNode.addTransaction([['add', 'AddingDrawing']]);
        editing.addTransaction([['end', 'Add']]);
        addingDrawing.addTransaction([['end', 'AddingEditing']]);
        //FUTURE: add here transaction for the addingEditing node addingEditing.addTransaction([]);



        if (token == true) {
            removeNode.set('notSavable', true); //used as control in the prepareAjax function
        }

        var DFD = new this.DFD(action);
        DFD.initDFD([startingNode, addNode, removeNode, editing, addingDrawing, addingEditing, endNode]);

        return DFD;
    },

    /**
     * This functions returns 
     * @param {String} type the type of the points
     * @param {[Object]} coordinates array of coordinates
     */
    paramsManipulation: function (type, coordinates) {
        var obj;

        if (type == 'POINT') { //if those are coordinates of a point
            obj = {
                '_type': type,
                'x': coordinates[0],
                'y': coordinates[1]
            };
        } else { //if are coordinates of a linestring or a polygon 
            if (type == 'POLYGON') {
                coordinates = coordinates[0]; //polygon case
            }
            var points = [];
            coordinates.forEach(function (pnt) {
                points.push({
                    'x': pnt[0],
                    'y': pnt[1]
                });
            }, this);

            obj = {
                '_type': type,
                'points': points
            };
        }
        return obj; //TODO: modify
    },

    prepareAjaxCall: function () {
        var i = 0;
        var selected = CMDBuildUI.map.util.Util.getSelection();
        var cardId = selected.id;
        var className = selected.type;
        for (var el in this._buffer) {
            if (this._buffer.hasOwnProperty(el)) {

                var DFD = this._buffer[el].DFD;
                var attributeId = el;
                var params = DFD.getNodeKey('currentNode', 'params');
                var els = el;

                /**
                 * This if statement can be removed. only for debug
                 */
                // if (DFD.getNodeKey('currentNode', 'label') != 'Remove' || DFD.getNodeKey('currentNode', 'label') != 'AddingEditing') {
                //     console.error('handle more cases');
                // }

                if (DFD.getNodeKey('currentNode', 'label') != 'Remove' || !DFD.getNodeKey('currentNode', 'notSavable')) {

                    i++;
                    this.ajaxRequest(className, cardId, attributeId, params, function () {
                        delete this._buffer[els];
                        i--;
                        if (i == 0) {
                            this.reloadSource();
                            this.removeTmpOlLayer();
                            this.removeDrawInteraction();
                            this.removeModifyInteraction();
                            this.triggerSelectedRow();
                            this.configRestore();
                            this.pointermoveMapAssign();
                        }
                    }, this);
                }
            }
        }
    },

    /**
     * This function makes the ajak call for the geovalues elements manipulation
     * @param  {} className 
     * @param  {} cardId
     * @param  {} attributeId
     * @param  {} params
     */
    ajaxRequest: function (className, cardId, attributeId, params, callback, callbackScope) {
        var method = params ? "PUT" : "DELETE";

        Ext.Ajax.request(
            {
                url: Ext.String.format(
                    '{0}/classes/{1}/cards/{2}/geovalues/{3}',
                    CMDBuildUI.util.Config.baseUrl,
                    className,
                    cardId,
                    attributeId
                ),
                method: method,
                jsonData: params,
                success: function (response) {
                    console.log('Point Added Correctly in the database');
                    callback.call(callbackScope);
                },
                error: function (response) {
                    console.log('ERROR', response);
                },
                scope: this
            }
        );
    },

    privates: {
        // ---------- VARIABLES ---------- //
        /**
         * Variable stores the information about the presence of the 2 buttons in the view
         */
        _saveCancelBtnPresent: false,

        /**
         * tells if we are in the edit mode
         */
        _editMode: false,

        /**
         * This object saves information about the layers being added, avoid error on pressing 
         * multiple times the add actioncolumn
         */
        _addingMode: {},

        /**
         * This variable stores the informations about state of actioncolumns
         */
        _actioncolumnsState: {},

        //--------MAP VARIABLES --------//

        /**
         * stores the value of the select interaction
         */
        _selectInteraction: null,

        /**
         * Saves the ol Map
         */
        _map: null,

        /**
         * stores the temporary olLayer
         */
        _olTmpLayer: null,

        /**
         * saves the ol.interaction.Draw
         */
        _drawInteraction: null,

        /**
         * the ol.interaction.Modify
         */
        _modifyInteraction: null,
        // --------End MAP VARIABLES --------// 


        /**
         * this Object contain the data structure for saving informatins in the buffer
         * NOTE: only one object at a time can have the pending status
         */
        _buffer: {

        },

        /**
         * This variable stores the id of the used layers. helpful for the refresh layer TODO:save the olLayer
         */
        _usedLayer: {},

        /**
         * This function sets the last used DFD
         */
        _lastDFD: null,
        // -------- VARIABLES END -------- //

        /**
         * This function creates the extjs button components
         */
        buttonCreation: function () {
            var me = this;
            return Ext.create('Ext.container.Container', {
                reference: 'sava-cancel-buttons',

                layout: {
                    type: 'column'
                },
                items: [{
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.save'
                    },
                    id: 'saveButton',
                    ui: 'management-action',
                    reference: 'saveButton',
                    disabled: true,
                    listeners: {
                        click: function (view, eOpts) {
                            me.onSaveButtonClick(view, eOpts);
                        }
                    },
                    margin: '0px 5px 0px 0px'
                }, {
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.common.actions.cancel,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                    },
                    ui: 'secondary-action',
                    id: 'cancelButton',
                    reference: 'cancelButton',
                    disabled: false,
                    listeners: {
                        click: function (view, eOpts) {
                            me.onCancelButtonClick(view, eOpts);
                        }
                    }
                }]
            });
        }
    },

    /*
     * this function modifyes a string mantaining the same words but setting uppercase the first latter and lowercase the remaining
     * @param {String} inp the input string
     * @return {String} the modified string
     */
    stringFormatter: function (inp) {
        if (inp.toLowerCase() == 'linestring') {
            return 'LineString';
        } else if (inp.toLowerCase() == 'point') {
            return 'Point';
        } else if (inp.toLowerCase() == 'polygon') {
            return 'Polygon';
        }
        /* var c0 = inp.charAt(0);
        var ca = inp.slice(1);
        return c0.toUpperCase() + ca.toLowerCase(); */
    },

    /**
     * DATA STRUCTURE FOR DFD
     */


    /**
     * This object simulates a node of a DFD. It has his label and his transaction table;
     * @param {String} label the label of the node
     * @param {Object} globalScope the globalScope
     */
    DFDNode: function (label, globalScope) {
        this.transactionTabel = {};
        this.keys = {
            label: label
        };

        var _this = this;
        /**
         * the subject in the observer pattern for enter event
         */
        this.onEnter = new globalScope.Subject({ scope: _this });

        /**
         * The subject in the observer pattern for exit event
         */
        this.onExit = new globalScope.Subject({ scope: _this });

        /**
         * This functions adds a transaction information to this node
         * @param {[[Strins,String]]} nodes contains informations of type: comand -> nextNodeLabel; nodes[i].lenght = 2;
         */
        this.addTransaction = function (nodes) {
            nodes.forEach(function (node) {
                this.transactionTabel[node[0]] = node[1];
            }, this);
        };

        /**
         * This functions returns the label of the next node after appying the comand 
         * @param {String} comand 
         * @returns {String} the label of the next node applying the comand
         */
        this.transaction = function (comand) {
            return this.transactionTabel[comand];
        };

        /**
         * Set the content of the node
         * @param {String} key the key label
         * @param {*} value the key value
         */
        this.set = function (key, value) {
            this.keys[key] = value;
        };

        /**
         * get the key with the passed label
         * @param {*} key 
         * @returns {*} the value of key 
         */
        this.get = function (key) {
            return this.keys[key];
        };

        /**
         * @returns an array of setted keys
         */
        this.getKeys = function () {
            var keys = [];
            for (var k in this.keys) {
                keys.push(k);
            }

            return keys;
        };

        /**
         * register functions onEnter
         * @param {Function} fn the function to call
         * @param {Object} scope the this parameter
         */

        this.addOnEnter = function (fn, scope) {
            this.onEnter.subscribe({
                obs: new globalScope.Observer({ fn: fn })
            }, scope);
        };

        /**
         * register functions onEnter
         * @param {Function} fn the function to call
         * @param {Object} scope the this parameter
         */
        this.addOnExit = function (fn, scope) {
            this.onExit.subscribe({
                obs: new globalScope.Observer({ fn: fn })
            }, scope);
        };
    },

    /**
     * @param {String} startNode
     */
    DFD: function (startNode) {
        this.nodes = {};
        this.keys = {
            initialized: false
        };
        this.currentNode = startNode;

        /**
         * this function must be executed before using the DFD
         * @param {[Object]} nodes 
         */
        this.initDFD = function (nodes) {
            if (!this.keys.initialized) {
                nodes.forEach(function (node) {
                    this.nodes[node.get('label')] = node;
                }, this);

                this.currentNode = this.getNode(this.currentNode);
                this.keys.initialized = true;
            } else {
                console.error("object jet initialized");
            }
        };

        /**
         * This functino applys the coman from the current node
            * @param {String} comand
            * @param {Object} arg The arguments passed for the transaction functions;
            */
        this.transaction = function (comand, arg) {
            if (!arg) arg = {};

            var nextNodeLabel = this.currentNode.transaction(comand);
            var currentNodeLabel = this.currentNode.get('label');

            arg.node = nextNodeLabel;

            this.currentNode.onExit.notify(arg);
            this.currentNode = this.getNode(nextNodeLabel);

            arg.node = currentNodeLabel;
            this.currentNode.onEnter.notify(arg);
        };

        /**
         * 
         * @param {String} label
         * @returns {Node} the node with Node.label = label
         */
        this.getNode = function (label) {
            return this.nodes[label];
        };

        /**
         * this functions gives the key of the node with node.label.label
         * @param {String} label the label of the node 
         * @param {String} key the interested key
         * @returns {*} the key of the interested node
         */
        this.getNodeKey = function (label, key) {
            var DFDNode;
            if (label == 'currentNode') {
                DFDNode = this.currentNode;
            } else {
                DFDNode = this.getNode(label);
            }
            return DFDNode.get(key);
        };

        /**
         * This function sets the key value of a DFDNode of the DFD
         * 
         * @param {*} label the node identifier
         * @param {*} key the key label
         * @param {*} value the value of the key
         */
        this.setNodeKey = function (label, key, value) {
            var DFDNode;
            if (label == 'currentNode') {
                DFDNode = this.currentNode;
            } else {
                DFDNode = this.getNode(label);
            }

            DFDNode.set(key, value);
        };

        /**
         * Sets a key --> value relation in th DFD
         * @param {*} key 
         * @param {*} value 
         */
        this.set = function (key, value) {
            this.keys[key] = value;
        };

        /**
         * Access to the saved keys of the DFD
         * @param {String} key 
         * @returns {*} the value of the key
         */
        this.get = function (key) {
            return this.keys[key];
        };

        /**
         * List all the saved keys
         * @returns {[String]} The list of all the keys saved;
         */
        this.getKeys = function () {
            var tmp = [];
            for (var k in keys) {
                tmp.push(k);
            }

            return tmp;
        };

    },

    /**
     * The subscriver
     */
    Subject: function (arg) { //TODO: add unsubscribe method
        this.observers = [];

        /**
         * 
         * @param {Object} conf the observer to register on the subject 
         * @param {Observer} conf.obs
         * @param {Object} scope the scope in wich the observer will be executed
         */
        this.subscribe = function (conf, scope) {
            if (!scope) scope = this._scope;
            if (!conf.node) conf.node = 'all';

            this.observers.push({
                obs: conf.obs,
                node: conf.node,
                scope: scope
            });
        };

        /**
         * This function fires all the observers with the specificated argument
         * @param {Object} arg contains the arguments  
         */
        this.notify = function (arg) {
            var node = arg.node;
            delete arg.node;

            this.observers.forEach(function (el) {
                if (el.node === 'all' || el.node === node) {

                    el.obs.fire(el.scope, arg);
                }
            });
        };

        this._scope = arg.scope ? arg.scope : this;
    },

    /**
     * the observer
     */
    Observer: function (conf) {
        this.fn = conf.fn;

        /**
         * @param {Object} arg Contains arguments needed to functions
         */
        this.fire = function (scope, arg) {
            this.fn.call(scope, arg); //TODO: pass scope method
        };
    }
});