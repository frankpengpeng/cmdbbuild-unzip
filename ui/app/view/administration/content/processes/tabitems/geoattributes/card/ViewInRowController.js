Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabitems-geoattributes-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.getViewModel();
        if(view && view._rowContext){
            vm.set('theGeoAttribute', view._rowContext.record);
        }
    },

    onEditBtnClick: function (button, event, eOpts) {

        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        
        CMDBuildUI.model.map.GeoAttribute.setProxy({
            url: Ext.String.format('/{0}/{1}/geoattributes', 'processes', vm.get('objectTypeName')),
            type: 'baseproxy'
        });

        container.add({
            xtype: 'administration-content-processes-tabitems-geoattributes-card-edit',
            viewModel: {
                links: {
                    theGeoAttribute: {
                        type: 'CMDBuildUI.model.map.GeoAttribute',
                        id: vm.get('theGeoAttribute.name')
                    }
                },
               
                data: {
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    },
                    grid: this.getView().up()
                }
            }
        });

    }

});