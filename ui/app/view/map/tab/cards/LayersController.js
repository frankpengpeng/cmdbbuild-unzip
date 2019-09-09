Ext.define('CMDBuildUI.view.map.tab.cards.LayersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-layers',
    control: {
        '#': {
            checkchange: 'onCheckChange',
            onmapzoomchanged: 'onMapZoomChanged'
        }
    },
    /**
     * This functon is called when the zoom changes or the store geoattributes changes. launched by bind
     * @param {Number} value the value of the actual zoom
     * @param {Ext.data.Store} layerStore the geoattributes store
     * @param externalLayerStore the store containing the external layers
     */
    onMapZoomChanged: function (value, layerStore, externalLayerStore) {
        function filterFun(item) {
            return value >= item.get('zoomMin') && value <= item.get('zoomMax') ? item : null;
        }

        function filterFunTwo(item) {
            return value >= item.get('zoomMin') && value <= item.get('zoomMax') ? item : null;
        }

        if (!layerStore.isLoaded()) { //this case appends when layerStore is not loaded
            layerStore.load({
                callback: function () {/* this.sequence(filterFun,layerStore) */
                    if (externalLayerStore && !externalLayerStore.isLoaded()) {
                        externalLayerStore.load({
                            callback: this.sequence(filterFun, layerStore, filterFunTwo, externalLayerStore)
                        });
                    } else {
                        this.sequence(filterFun, layerStore, filterFunTwo, externalLayerStore);
                    }
                },
                scope: this
            });

        } else if (externalLayerStore && !externalLayerStore.isLoaded()) {    //this case appends when layerStore is loaded but externalLayerStore is not
            externalLayerStore.load({
                callback: function () {
                    this.sequence(filterFun, layerStore, filterFunTwo, externalLayerStore)
                }, scope: this
            });
        } else {    //this case appends when all the stores are loaded
            this.sequence(filterFun, layerStore, filterFunTwo, externalLayerStore);
        }
    },
    /**
     * This event handles the chack change on map -> Tab -> layers
     * @param {Ext.data.Model} node the node changed
     * @param {Boolean} checked the value of the checked node
     */
    onCheckChange: function (node, checked) {
        var checkLayers = this.getViewModel().get('checkLayers');

        this.recursiveChangeCheck(node, checked, checkLayers);

        Ext.GlobalEvents.fireEventArgs("recursivechechcontrol");
        /* var id = node.get('id');

        if(checked == false){
            checkLayers[id] = false;
        }else{
            delete(checkLayers[id]);
        } */
    },
    privates: {
        /**
         * This function handle the correct calls for the tree check problem
         * @param {Ext.data.Record} node (Ext.data.Model) the node to set as checked
         * @param {Boolean} checked the ckecked value
         * @param {Object} checkLayers 
         */
        recursiveChangeCheck: function (node, checked, checkLayers) {

            this.downRecursiveChangeCheck(node, checked, checkLayers);
            this.upNonRecursiveChangeCheck(node, checked, checkLayers);
            this.checkListUpdate(node, checked, checkLayers);
        },
        /**
         * this function sets recursively as checked the child nodes of node 
         * @param {Ext.data.Record} node (Ext.data.Model) the node to set as checked
         * @param {Boolean} checked the ckecked value
         * @param {Object} checkLayers 
         */
        downRecursiveChangeCheck: function (node, checked, checkLayers) {
            var id = node.get('id');

            node.set('checked', checked);
            var childNodes = node.childNodes;

            if (node.isLeaf()) {
                if (checked == false) {
                    checkLayers[id] = {  //fase di inserimento in checkLayers
                        checked: false,
                        type: node.get('type')
                    };
                } else {
                    delete checkLayers[id];  //fase di rimozione da checkLayers
                }
            }
            for (var i = 0; i < childNodes.length; i++) {
                this.downRecursiveChangeCheck(childNodes[i], checked, checkLayers);
            }
        },
        /**
         * this function iterates up the parent chain until find a ture check or the root
         * @param {Ext.data.Record} node (Ext.data.Model) the node to set as checked
         * @param {Boolean} checked the ckecked value
         * @param {Object} checkLayers
         */
        upNonRecursiveChangeCheck: function (node, checked, checkLayers) {

            var tmpNode = node.parentNode;
            while (tmpNode != null && !tmpNode.get('checked')) {
                tmpNode.set('checked', true);
                tmpNode = tmpNode.parentNode;
            }
        },
        /**
         * This function is responsable to mantain in order the ckeckLayers in order to have a correct data Structure
         * @param node the clicked node in tab-layers
         * @param checked the value of the check 
         * @param checkLayers the checkLayers object in the viewModel
         */
        checkListUpdate: function (node, checked, checkLayers) {
            var id = node.get('id');

            if (node.isLeaf()) return;

            for (var el in checkLayers) {
                var layer = checkLayers[el];

                if (this.hasTypeDependency(id, layer.type)) {
                    if (checked == true) {
                        delete checkLayers[el];  //remove old ckeckLayer.id elements wich need to be displayed
                    }
                }
            }
        },
        /**
         * This function tells if thers a dependency of 2 given types
         * @param {String} first can be root, geoAttributesRoot, externalServicesRoot,externalLayersNode //TODO: add mapservices
         * @param {String} second can be shape or vector
        */
        hasTypeDependency: function (first, second) {
            if (first == 'root') {
                return true;
            } else if ((first == 'externalServicesRoot' || first == 'externalLayersNode') && second == 'shape') {
                return true;
            } else if (first == 'geoAttributesRoot' && second == 'geometry') {
                return true;
            } else {
                return false;
            }
        },
        /**
         * this method gives in a compact form the layers who need to be removed and the layer to be mantained
         * @param {CMDBuildUI.model.map.GeoAttribute[]} layerRange the range of the store after applying the filterFun
         * @param {CMDBuildUI.model.map.GeoExternalLayer[]} externalLayerRange the range of store externalLayer after applying the filterFun
         * after the function ends
         * viewModel variable toRemove will contain all the layers wich need to be remove from the map
         * viewModel variable toAdd will contain all the layer we need to create and add to the map
        */
        updateGeoAttributeNodeInStore: function (layerRange, externalLayerRange) {
            var vm = this.getViewModel();
            var store = vm.get('layerTree');
            var range = layerRange.concat(externalLayerRange);
            var rangeCopy = [];

            for (i = 0; i < range.length; i++) {
                rangeCopy.push(range[i]);
            }

            var oldRange = vm.get('oldRange');
            vm.set('oldRange', rangeCopy);

            this.calculateToRemoveAndToAdd(range, oldRange);
            if (oldRange.length != 0) {
                oldRange.forEach(function (element) {
                    store.getNodeById(element.get('_id')).remove();
                });
                this.getViewModel().getParent().getParent().set('toRemove', oldRange);
            }
            var me = this;
            if (range.length != 0) {
                range.forEach(function (element) {
                    var nodeName = this.getTreeNodeName(element);

                    node = {
                        type: element.data.type,
                        text: element.get('name') + '(' + element.get('owner_type') + ')',
                        id: element.get('_id'),
                        leaf: true,
                        checked: me.verifyChecks(element.get('_id'), nodeName)
                    };
                    this.appendChildInTree(node, nodeName);
                }, this);
            }
            //need to stay outside th if statment
            this.getViewModel().getParent().getParent().set('toAdd', range);
        },
        /**
         * This function insers in the view store a node as child of specificated node
         * @param node the node to insert in the view store
         * @param nodeName the id of the name in the vies store
         */
        appendChildInTree: function (node, nodeName) {
            var nodeParent = this.getViewModel().get('layerTree').getNodeById(nodeName);
            nodeParent.appendChild(node);
        },
        /**
         * This function performs the insiemistic operation range/oldRange and oldRange/range to see differences between the 2 stacks
         * @param range the actual range
         * @param oldRange the previous range
         * MODIFY range and oldRange. in each array stays only elements wich are not in common range = range/oldRange oldRange = oldRange/range
         * so in range remain only the elements wich were not in the olRange (new elements added)
         * and in oldRange remain the elements wich aren't now in range (the ones to delete)
         */
        calculateToRemoveAndToAdd: function (range, oldRange) { //TODO: Improve the complexity of this function

            for (var i = 0; i < range.length; i++) {
                var x = range[i].get('_id');
                for (var j = 0; j < oldRange.length; j++) {
                    if (oldRange[j].get('_id') == x) {
                        oldRange.splice(j, 1);
                        range.splice(i, 1);
                        i--;
                        break;
                    }
                }
            }


        },
        /**
         * This function sets the correct check state to the layers
         * @param id The id to verify
         * @param nodeName the id of the parent node
         **/
        verifyChecks: function (id, nodeName) {
            var checkLayers = this.getViewModel().get('checkLayers');
            var nodeParent = this.getViewModel().get('layerTree').getNodeById(nodeName);
            if (checkLayers[id] && checkLayers[id].checked == false || nodeParent.get('checked') == false) {
                return false;
            }
            return true;

        },
        /**
         * This function operates only to set the filter on the layer store to know if they are visible fot the actual livel of zoom
         * @param filterFun the ilter function to apply on layerStore
         * @param layerStore the GisLayerStore
         * @param filterFunTwo the second filter function to apply to externalLayerStore
         * @param externalLayerStore the store containing informations about the layers on geoserver
         */
        sequence: function (filterFun, layerStore, filterFunTwo, externalLayerStore) {
            if (externalLayerStore) {
                layerStore.getFilters().add(filterFun);
                externalLayerStore.getFilters().add(filterFunTwo);

                this.updateGeoAttributeNodeInStore(layerStore.getRange(), externalLayerStore.getRange());

                externalLayerStore.getFilters().remove(filterFunTwo);
                layerStore.getFilters().remove(filterFun);
            } else {
                layerStore.getFilters().add(filterFun);

                this.updateGeoAttributeNodeInStore(layerStore.getRange(), []);

                layerStore.getFilters().remove(filterFun);
            }

        },
        /**
         * This function returns the correct id of the root in layer tree
         * @param node
         */
        getTreeNodeName: function (node) {
            switch (node.get('type').toLowerCase()) {
                case 'shape':
                    return 'externalLayersNode';
                case 'geometry':
                    return 'geoAttributesRoot';
            }
        }
    }

});
