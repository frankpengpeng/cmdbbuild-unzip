Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.AttributesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabitems-values-attributes',
    control: {
        "#": {
            itemcreated: "onItemCreated"
        }
    },

    onItemCreated: function (record, eOpts) {
        // TODO: reload menu tree store
    }
});