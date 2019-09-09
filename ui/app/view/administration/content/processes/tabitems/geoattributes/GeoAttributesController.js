Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.GeoAttributesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabitems-geoattributes-geoattributes',
    control: {
        '#': {           
            deselect: 'onDeselect',
            select: 'onSelect'
            //,
            //rowdblclick: 'onRowDblclick'
        },
        '#addattribute': {
            click: 'onAddAttributeClickBtn'
        }
    },

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onDeselect: function (row, record, index, eOpts) {

    },

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onSelect: function (row, record, index, eOpts) {
        this.view.setSelection(record);
    },

    onAddAttributeClickBtn: function (button, event, eOpts) {
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
                        create: true
                    }
                },

                data: {
                    actions: {
                        view: false,
                        edit: true,
                        add: true
                    },
                    grid: this.getView().up()
                }
            }
        });
    }
});