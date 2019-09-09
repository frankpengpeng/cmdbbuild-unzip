Ext.define('CMDBuildUI.view.map.tab.cards.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-card',
    listen: {
        component: {
            '#': {
                'beforerender': 'onBeforeRender'
            }
        }

    },

    /**
      * @param {CMDBuildUI.view.classes.cards.Grid} view
      * @param {Object} eOpts
      */
    onBeforeRender: function (view, eOpts) {
        view.mon(CMDBuildUI.map.util.Util.getMapGridContainerView(), 'selectedchangeevent', this.onSelectedChange, this);
        CMDBuildUI.map.util.Util.getGeoAttributesStore().addListener('load', this.geoattributesGridInit, this, { single: true });
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
     * @param {Ext.data.Model} records the records rapresenting the geovalues of the selected card
     */
    onSelectedChange: function (selected, records) {
        console.log('Handle the selectionChangeEvent from the Card');

        var geoattributesGrid = this.getView().lookupReference('map-geoattributes-grid');
        var gridStore = geoattributesGrid.getStore();

        if (gridStore.isLoaded()) { //FIXME:not sure if best check
            this.modifyActionColumn(records);
        } else {
            gridStore.addListener('datachanged', function (store, eOpts) {
                this.modifyActionColumn(records);
            }, this, {
                    single: true
                });
        }
    },

    /**
     * @param {[Ext.data.Model]} geoValues CMDBuildUI.model.map.GeoElement
     */
    modifyActionColumn: function (geoValues) {
        var geoattributesGrid = this.getView().lookupReference('map-geoattributes-grid');
        var gridStore = geoattributesGrid.getStore();

        gridStore.each(function (gridRecord) {
            var layerName = gridRecord.get('name');
            var layerOwnerType = gridRecord.get('_owner_type');

            try {

                var record = Ext.Array.findBy(geoValues, function (geoValue, index) {
                    if (geoValue.get('_owner_type') == layerOwnerType && geoValue.get('_attr') == layerName) {
                        return true;
                    }
                });
            } catch (e) {
                console.log('Has been catch');
            }

            if (record) {
                gridRecord.set('add', true);
                gridRecord.set('edit', false);
                gridRecord.set('remove', false);
                gridRecord.set('view', false);
                gridRecord.set('_id', record.get('_id'));
                gridRecord.set('coordinates', CMDBuildUI.map.util.Util._getCoordinate(record));
            } else {
                gridRecord.set('add', false);
                gridRecord.set('edit', true);
                gridRecord.set('remove', true);
                gridRecord.set('view', true);
                gridRecord.set('_id', null);
                gridRecord.set('coordinates', null);
            }
        }, this);
    },

    /**
     * @param {Ext.data.Store} geoattributesStore the store containing the geoAttribute
     * @param {[Ext.data.Model]} records
     * @param {Boolean} successful
     * @param {Ext.data.operation.Read} operation
     * @param {Object} eOpts
     */
    geoattributesGridInit: function (geoattributesStore, records, successful, operation, eOpts) {
        if (successful) {
            // var view = this.getView().down('map-tab-cards-card').lookupReference('map-geoattributes-grid');
            var store = this.getView().lookupReference('map-geoattributes-grid').getStore();

            var objectTypeName = this.getView().getViewModel().get('objectTypeName');
            var els = [];
            records.forEach(function (record) {
                if (record.get('owner_type') == objectTypeName) {
                    els.push([
                        record.get('name'),
                        record.get('owner_type'),
                        record.get('zoomDef'),
                        record.get('zoomMin'),
                        record.get('zoomMax'),
                        record.get('type'),
                        record.get('subtype'),
                        record.get('_id')
                    ]);
                }
            }, this);
            store.setData(els);
        } else {
            CMDBuildUI.util.Notifier.showErrorMessage('Somthing wrong on loading #geoattributes store');
        }
    }
});
