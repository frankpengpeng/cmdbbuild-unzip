Ext.define('CMDBuildUI.view.map.MapModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-map',

    data: {

    },
    formulas: {
        toAddLayer: {
            bind: '{toAdd}',
            get: function (list) {
                if (list == null) return;
                else if (list.length > 0) {
                    this.getView().fireEvent('toaddlayer', list);
                } else {
                    var map = this.getView().getOlMap();
                    var extent = map.getView().calculateExtent(map.getSize());
                    this.getView().up('management-content').getViewModel().set('bbox', [extent[0], extent[1], extent[2], extent[3]]);
                }

            }
        },
        toRemoveLayer: {
            bind: '{toRemove}',
            get: function (list) {
                if (list.length > 0) {
                    this.getView().fireEvent('toremovelayer', list);
                }
            }
        },
        navigationTreeCheckChange: {
            bind: {
                checkListNT: '{checkNavigationTree}'
            },
            get: function (checkListNT) {
                this.getView().fireEvent('featurebynavigationtreechanged', checkListNT);
            }
        },
        overlaySelection: {
            bind: {
                notFound: '{selectedFeatureNotFound}'
            },
            get: function (data) { //TODO: put on View
                var map = CMDBuildUI.map.util.Util.getOlMap();
                var controller = this.getView().getController();
                var el;

                if (data != null && data.notFound != null) {
                    var feature = data.notFound[0];
                    var position = CMDBuildUI.map.util.Util._getCoordinate(feature);
                    var ovLay = map.getOverlayById('overlayId');

                    if (ovLay == null) {
                        el = controller.generateEl();
                        ovLay = new ol.Overlay({
                            id: 'overlayId',
                            position: position,
                            element: el,
                            stopEvent: false
                        });
                        map.addOverlay(ovLay);

                    } else {
                        ovLay.setPosition(position);
                    }
                } else {
                    map.removeOverlay(map.getOverlayById('overlayId'));
                    el = document.getElementById('overlayId');

                    if (el != null) {
                        el.remove();
                    }
                }
            }
        }
    }
});