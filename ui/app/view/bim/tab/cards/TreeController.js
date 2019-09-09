Ext.define('CMDBuildUI.view.bim.tab.cards.TreeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-tab-cards-tree',
    listen: {
        global: {
            ifctreeready: 'onIfcTreeReady',
            highlitedifcobject: 'onHighlitedIfcObject'
        },
        component: {
            '#': {
                'beforeselect': function (grid, record, index, eOpts) {
                    return false;
                },
                'beforerender': 'onBeforeRender'
            },
            'bim-tab-cards-tree #singleIfcOpacity': {
                click: 'singleIfcOpacityHandler'
            }
        }
    },

    /**
     * @param {Ext.Component} treePanel
     * @param {Object} eOptsi
     */
    onBeforeRender: function (treePanel, eOpts) {
        treePanel.getView().getStore().setSorters([{
            property: 'text',
            direction: 'ASC'
        }])
    },

    init: function () {
        this.ifcLayers = [];
        this._tmpLayers = [];
        this._tmpOids = [];
    },

    /**
     * This function is executed when the ifctreeready is fired
     */
    onIfcTreeReady: function () {
        console.log('OnIfcTreeReady');
        var ifcRoot = CMDBuildUI.util.bim.IfcTree.getIfcRoot();
        var ifcTreeCreation = this.ifcTreeCreationRecursive(ifcRoot[ifcRoot.length - 1], {});
        this.getView().getRootNode().appendChild(ifcTreeCreation);
        this.openDepth(this.getView().getRootNode(), this.DEPTH);

        this.getViewModel().getParent().set('ifcLayers', this.computeObject());
    },
    /**
     * this function takes an object and removes the reference they had and creates an array with those values
     * @example 
     * the input:
     * {
     *  'name' : 'Ugo',
     *  'age' : '27',
     *  'color' : [125,255,17]
     * }
     * the output: 
     * [Ugo,27,[125,255,17]]
     * @returns {[*]} look the example  
     */
    computeObject: function () {
        //removes the unwanted types
        delete this._tmpLayers.IfcProject;

        var ifcLayers = [];
        for (var ifcName in this._tmpLayers) {
            if (this._tmpLayers.hasOwnProperty(ifcName)) {

                // sets some default values to action column
                var mode = CMDBuildUI.util.bim.Viewer.transparentLayers[ifcName];
                if (mode != null) {
                    this._tmpLayers[ifcName].clicks = mode;
                } else {
                    this._tmpLayers[ifcName].clicks = 0;
                }

                ifcLayers.push(this._tmpLayers[ifcName]);
            }
        }

        return ifcLayers;
    },


    /**
     * recursive function for generating from the raw ifcTree the EXTJS root tree with childs
     * The function also makes operatins for the layer tab
     * @param {Object} node the current analized node 
     */
    ifcTreeCreationRecursive: function (node) {

        /**
          * manage the modification of this._tmpLayers for another file (Layers.js)
        */
        var name = node.ifcObject.object._t;
        var oid = node.ifcObject.oid;

        if (!this._tmpOids[oid]) {
            this._tmpOids[oid] = true;
            if (!this._tmpLayers[name]) {
                this._tmpLayers[name] = {
                    name: name.replace('Ifc', ""),
                    qt: 1
                };
            } else {
                this._tmpLayers[name].qt++;
            }


            
            /**
             * manage the creation of the ifcTree
            */
            var text;
            if (node.ifcObject.object._t == 'IfcProject') {
                text = node.ifcObject.object.LongName || 'IfcProject';
            } else {
                text = (node.ifcObject.object._t || "").replace("Ifc", "") + " " + (node.ifcObject.object.Name || "");
            }
            var tmpNode = {
                text: text,
                children: [],
                oid: node.ifcObject.oid,
                globalId: node.ifcObject.object.GlobalId,
                leaf: true,
                clicks: 0
            };
            node.children.forEach(function (childNode) {
                tmpNode.leaf = false;

                var childTemp = this.ifcTreeCreationRecursive(childNode);
                childTemp ? tmpNode.children.push(childTemp) : null;

            }, this);

            return tmpNode;
        }
        return null;
    },

    /**
     * This functions expands the tree from selected ifcObject in canvas to the root
     * @param {Object} highlited The higlited object in the canvas
     */
    onHighlitedIfcObject: function (highlited) {
        var objectId = highlited.objectId

        var storeRoot = this.getView().getStore().getRoot();
        var node = this.recursiveFound(storeRoot, objectId);

        if (node == null) {
            console.log("Attenction, node not found");
        }
        this.getView().getSelectionModel().select(node, false, true);

        var tmpNode = node;
        while (tmpNode != null) {
            tmpNode.expand();
            tmpNode = tmpNode.parentNode;
        }
        this.getView().ensureVisible(node.getPath());
    },

    /**
     * This function gets a node and looks in hi children in or to find the one with a specific value
     * @param {} node The node to inspect
     * @param {} value the value of the node we are looking for
     * @returns the node with node.value = value
     */
    recursiveFound: function (node, value) {
        if (node.data.oid == value) {
            return node;
        } else {
            for (var i = 0; i < node.childNodes.length; i++) {
                var tmpChild = this.recursiveFound(node.childNodes[i], value);
                if (tmpChild) {
                    return tmpChild;
                }
            }
        }

        return null;
    },

    /**
     * 
     */
    singleIfcOpacityHandler: function (v, rowIndex, colIndex, item, e, record, row) {
        var clicks;
        record.get('clicks') == 0 ? clicks = 2 : clicks = 0;
        record.set('clicks', clicks);

        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var viewer = minimalInstance.bimServerViewer.viewer;
        var oid = record.get('oid');

        switch (clicks) {
            case 0:
                viewer.setVisibility([oid], true);
                break;
            case 2:
                viewer.setVisibility([oid], false);
                break;
        }

        viewer.selectedElements.clear();
    },

    privates: {
        /**
         * This array contains the ifcLayers found wile exlplorying the tree 
         */
        _tmpLayers: [],


        _tmpOids: [],

        /**
         * 
         */
        DEPTH: 3,

        /**
         * This function expands all the nodes wit a depth value <= depth
         * @param {object} node the node we start;
         * @param {depth} depth the value of depth to reach from that node;
         */
        openDepth: function (node, depth) {
            var nodeDepth = node.getDepth();
            if (nodeDepth <= depth) {
                node.expand();

                var childNodes = node.childNodes;
                for (var i = 0; i < childNodes.length; i++) {
                    this.openDepth(childNodes[i], depth);
                }
            }


            // this.recursiveOpenDepth(node,0,depth);
        }
    }
});
