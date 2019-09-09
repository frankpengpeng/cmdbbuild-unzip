Ext.define('CMDBuildUI.view.administration.content.gis.geoserverslayers.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gis-geoserverslayers-topbar',

    control: {
        '#addlayer': {
            click: 'onNewLayerBtnClick'
        }
    },

    onNewLayerBtnClick: function () {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-gis-geoserverslayers-card-viewedit',
            viewModel: {
                links: {
                    theLayer: {
                        type: 'CMDBuildUI.model.map.GeoLayers',
                        create: true
                    }
                },
                data: {
                    actions: {
                        edit: false,
                        view: false,
                        add: true
                    }
                }
            }
        });
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onKeyUp: function (field, event) {
        // get vm value
        var vm = this.getViewModel();
        var searchTerm = vm.getData().search.value;
        var store = vm.get('layersStore');
        store.clearFilter();
        if (searchTerm) {
            CMDBuildUI.util.administration.helper.GridHelper.searchMoreFields(store, searchTerm);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // clear store filter

        var store = vm.get('layersStore');
        store.clearFilter();

        // reset input
        field.reset();
    }

});