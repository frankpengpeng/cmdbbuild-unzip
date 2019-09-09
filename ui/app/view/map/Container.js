
Ext.define('CMDBuildUI.view.map.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.map.ContainerController',
        'CMDBuildUI.view.map.ContainerModel'
    ],

    alias: 'widget.map-container',
    controller: 'map-container',
    viewModel: {
        type: 'map-container'
    },

    layout: 'border',

    padding: '0',

    items: [
        {
            xtype: 'map-map',
            region: 'center',
            split: true,
            reference: 'map'
        }, {
            xtype: 'map-tab-tabpanel',
            title: CMDBuildUI.locales.Locales.gis.cardsMenu,
            localized: {
                title: 'CMDBuildUI.locales.Locales.gis.cardsMenu'
            },
            region: 'east',
            split: true,
            reference: 'tab-panel',
            width: '30%',
            collapsible: true,
            layout: 'container'
        }
    ]

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

    /**
     * @event navtreeload
     * @param {[Ext.data.model]} navTreeRecords model: CMDBuildUI.model.map.navigation.NavigationTree
     */

    /**
     * @event geoelementsload
     * @param {[Ext.data.model]} geoElemetsRecords model:CMDBuildUI.model.map.GeoElement
     */

    /**
    * @event layerlistchanged
    * this function is fired after all the layers are added on map 
    * @param {Object} layerList the list of the layer on the olMap either visible or not (enabled or disabled by the tab-layer checkBox)
    */

    /**
     * @event bboxchanged
     * This event is fired when the bbox of the map changes
     * @param bbox the map bbox
     */
});
