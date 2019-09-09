Ext.define('CMDBuildUI.util.bim.Viewer', {
    singleton: true,

    /**
     * @param {Number} objectId represents the objectId
     */
    select: function (objectId) {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        minimalInstance.bimServerViewer.viewer.setSelectionState([objectId], true, true);

        var viewObject = minimalInstance.bimServerViewer.viewer.getViewObject(objectId);
        if (viewObject != null) {
            Ext.GlobalEvents.fireEventArgs('highlitedifcobject', [viewObject]);
        }
    },

    /**
     * This function changes the visibility of ifcElements base on information in this.transparentLayers
     */
    changeTransparence: function () {
        /*  if (!this.globalCurrentOid) { //LOOK: this is not useful now
             this.globalCurrentOid = IfcTree.getRoot().ifcObject.object.oid;
         } 
        */
        var root = CMDBuildUI.util.bim.IfcTree.getIfcRoot();
        for (var i = 0; i < root.length; i++) {
            this.GlobalCurrentOid = root[i].ifcObject.object.oid;

            //array of oid filled during the recursive visit'
            var show = [];
            var semiHide = [];
            var hide = [];

            this.recursiveVisit(root[i], show, semiHide, hide);
        }

        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var viewer = minimalInstance.bimServerViewer.viewer;

        viewer.resetVisibility(show);
        viewer.setVisibility(hide, false);


        try {
            //should be done in a better way
            if (this.transparentLayers[viewer.getSelected()[0].type] == 2) {
                viewer.selectedElements.clear();
                //TODO: trigger selection null so the card tab will be disabled
            }
        } catch (e) {

        }
    },

    /**
     * @param {Object} node the node we are operating on
     * @param {[Number]} show array of oids tha must be shown
     * @param {[Number]} semiHide arry of oids that must have the alpha value halved
     * @param {[Number]} hide array of oids that mus be hide
     */
    recursiveVisit: function (node, show, semiHide, hide) {
        var currentOid = node.ifcObject.object.oid;
        var ifcType = node.ifcObject.object._t;

        var mode = this.transparentLayers[ifcType];
        switch (true) {
            case (mode == 0 || mode == null):
                show.push(currentOid);
                break;
            case (mode == 1):
                semiHide.push(currentOid);
                break;
            case (mode == 2):
                hide.push(currentOid);
                break;
        }

        for (var i = 0; i < node.children.length; i++) {
            this.recursiveVisit(node.children[i], show, semiHide, hide);
        }
    },

    /**
     *  updates the value for the layer with full opacity
     * @param {String} layername the ifc layer name 
    */
    showLayer: function (layerName) {
        delete this.transparentLayers[layerName];
    },

    /**
     *  updates the value for the layer with half transparence
     * @param {String} layername the ifc layer name 
    */
    semiHideLayer: function (layerName) {
        this.transparentLayers[layerName] = 1;
    },

    /**
     *  updates the value for the layer not visible
     * @param {String} layername the ifc layer name 
    */
    hideLayer: function (layerName) {
        this.transparentLayers[layerName] = 2;
    },

    /**
     * 
     */
    defaultView: function () {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var camera = minimalInstance.bimServerViewer.viewer.camera;

        camera.target = Float32Array.from([0, 0, 0]);
        camera.center = Float32Array.from([0, 0, 0]);
        camera.eye = Float32Array.from([65000, 70000,10000])
    },

    /**
     * 
     */
    sideView: function () {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var camera = minimalInstance.bimServerViewer.viewer.camera;

        camera.target = Float32Array.from([0, 0, 0]);
        camera.center = Float32Array.from([0, 0, 0]);
        camera.eye = Float32Array.from([100000, 0, 0]);
    },

    /**
     * 
     */
    topView: function () {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var camera = minimalInstance.bimServerViewer.viewer.camera;

        camera.target = Float32Array.from([0, 0, 0]);
        camera.center = Float32Array.from([0, 0, 0]);
        camera.eye = Float32Array.from([0, 0.1, 100000]);

    },

    /**
     * 
     */
    frontView: function () {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var camera = minimalInstance.bimServerViewer.viewer.camera;

        camera.target = Float32Array.from([0, 0, 0]);
        camera.center = Float32Array.from([0, 0, 0]);
        camera.eye = Float32Array.from([0, 100000, 0]);
    },

    // /**
    //     * @param {string} IfcType describes the type of the ifc Object
    //     * @return {int} a value between 0 and 1 representing the alpha (trasparency) of the IfcObject. if not found return the defaultValue 
    //     */
    // getAlphaValueFromIfcType: function (IfcType) {
    //     if (IfcType) {
    //         var type = BIMSURFER.Constants.materials[IfcType];
    //         if (type) {
    //             return type.a;
    //         }
    //     }
    //     return null;
    // },

    show: function (poid, type, observer) {
        this._init();

        var email = 'admin@bimserver.com'//CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.user);
        var password = 'admin'//CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.password);

        //The value is instanciated when downloading the modules
        var minimal = CMDBuildUI.util.bimsurfer.util.getMinimal();
        var minimalInstance = new minimal({
            address: window.cmdbuildConfig.bimserverBaseUrl,
            username: email,
            password: password,
            poid: poid
        });

        CMDBuildUI.util.bimsurfer.util.setMinimalInstance(minimalInstance);
        minimalInstance.start(function () {
            //adds click interaction
            this._serviceOperation(minimalInstance);
        }, this);

        var restoreRoot = true;
        minimalInstance.getTree2(function (project, model) {
            CMDBuildUI.util.bim.IfcTree._init(project, restoreRoot);
            restoreRoot = false;
            Ext.GlobalEvents.fireEventArgs("ifctreeready", null);

            //Sets the layer opacity set in the _init function
            this.changeTransparence();
        }, this);

    },

    /**
     * This function makes extra operation on the instance
     * -Add another listener on clickUp on the canvas
     * @param {Minimal} minimalInstance
     */
    _serviceOperation: function (minimalInstance) {
        try {
            //add mouseUp event
            var canvas = minimalInstance.bimServerViewer.canvas;
            var camera = minimalInstance.bimServerViewer.viewer.cameraControl;

            //This function is very similat to the one in the bimview cameracontrol module
            canvas.addEventListener("mouseup", function (e) {

                var dt = e.timeStamp - camera.mouseDownTime;
                switch (e.which) {
                    case 1:
                        if (dt < 500. && camera.closeEnoughCanvas(camera.mouseDownPos, camera.mousePos)) {
                            var viewObject = camera.viewer.pick({
                                canvasPos: camera.mousePos,
                                shiftKey: e.shiftKey
                            });

                            if (viewObject && viewObject.object) {
                                CMDBuildUI.util.bim.Viewer.select(viewObject.object.objectId);
                            }

                        }
                        break;
                }
            });
        } catch (e) { }
    },

    /**
     * @param {String} cameraType: orthographic or perspective
     */
    setCamera: function (cameraType) {
        var minimalInstance = CMDBuildUI.util.bimsurfer.util.getMinimalInstance();
        var viewerCamera = minimalInstance.bimServerViewer.viewer.camera;

        switch (cameraType) {
            case 'orthographic':
                viewerCamera._projection = viewerCamera.orthographic
                break;
            case 'perspective':
                viewerCamera._projection = viewerCamera.perspective;
                break;
        }

        minimalInstance.bimServerViewer.viewer.updateViewport()
    },

    /**
     * This function restores the transparancy 
     */
    _init: function () {
        this.transparentLayers = {
            'IfcSpace': 2
        }

        /**
         * FIXME: view casese for more than one project
         */
        CMDBuildUI.util.bimsurfer.util.init();
    },

    privates: {
        /**
         * Set the default values sono layers. see this.reset() function
         * 
        */
        transparentLayers: {}
    }
});