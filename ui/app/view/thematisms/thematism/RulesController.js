Ext.define('CMDBuildUI.view.thematisms.thematism.RulesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.thematisms-thematism-rules',
    listen: {
        component: {
            '#': {
                beforerender: 'onBeforeRender'
            },
            'grid': {
                cellclick: 'onCellClick'
            }
        }
    },

    onBeforeRender: function () {
        var view = this.getView();

        if (view.config.needListener) {
            view.lookupReference('calculaterules').show();
        }
    },

    onCellClick: function (grid, td, cellIndex, record, tr, rowIndex, e, eOpts) {
        var view = this.getView();

        if (view.config.needListener) {
            view.expandColorPicker(tr)
            view.setColorPickerRecord(record);
        }
    }
});
