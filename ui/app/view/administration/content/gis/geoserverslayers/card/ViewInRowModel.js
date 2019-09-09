Ext.define('CMDBuildUI.view.administration.content.gis.geoserverslayers.card.ViewInRowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gis-geoserverslayers-card-viewinrow',
    data: {
        name: 'CMDBuildUI'
    },

    formulas: {
        updateCardDescription: {
            bind: '{theLayer.owner_id}',
            get: function (card) {
                var vm = this;
                var theLayer = vm.get('theLayer');
                var id = card;
                var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                var c = CMDBuildUI.util.helper.ModelHelper.getModel(type, theLayer.get('owner_type')).then(
                    function (c) {
                        c.load(id, {
                            success: function (record) {
                                vm.set('cardDescription', record.get('Description'));
                            }
                        });
                    }
                );
            }
        }
    }
});