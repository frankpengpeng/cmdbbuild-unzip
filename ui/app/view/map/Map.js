Ext.define('CMDBuildUI.view.map.Map', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.map.MapController',
        'CMDBuildUI.view.map.MapModel'
    ],
    alias: 'widget.map-map',
    controller: 'map-map',
    viewModel: {
        type: 'map-map'
    },

    config: {
        olMap: null,
        divMapId: null,
        layerList: {}
    },

    publish: ['olMap'],

    /**
     * @event toaddlayer
     * Fired when a layer need to be added on tab -> layers
     * @param {object[]} list
     */

    /**
    * @event toremovelayer
    * fired when a later in tab -> layers need to be removed
    * @param {object[]} list the list of record representing the layers to remove
    */

    /**
     * @event featurebynavigationtreechanged
     * This function is fired when the features on map are manipulated from navigationTree 
     * and need manipulation
     * @param {object} checkListNT keeps information about the features to hide or show
     */

    /**
     * @event olmapcreated
     * This event is fired after is set the olMap in the view
     * @param {ol.Map} olMap the openlayer map
     */

    /**
     * Add the listener once olMap is set   TODO: Could allow more listeners
     */
    updateOlMap: function (newValue, oldValue) {
        newValue.on('moveend', function (a, b, c, d) {
            var controller = this.getController();
            if (controller) {
                controller.onMoveEnd(a, b, c, d);
            }
        }, this);
    }
});
