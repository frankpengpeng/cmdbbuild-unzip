
Ext.define('CMDBuildUI.view.filters.attributes.Panel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.filters.attributes.PanelController',
        'CMDBuildUI.view.filters.attributes.PanelModel'
    ],

    alias: 'widget.filters-attributes-panel',
    controller: 'filters-attributes-panel',
    viewModel: {
        type: 'filters-attributes-panel'
    },

    title: CMDBuildUI.locales.Locales.filters.attributes,

    localized: {
        title: 'CMDBuildUI.locales.Locales.filters.attributes'
    },

    layout: 'border',

    config: {
        /**
         * @cfg {Boolean} allowInputParameter
         */
        allowInputParameter: true
    },

    items: [{
        region: 'center',
        xtype: 'container',
        scrollable: true,
        reference: 'attributescontainer',
        itemId: 'attributescontainer'
    }, {
        xtype: 'panel',
        region: 'north',
        reference: 'addattrfiltercontainer',
        layout: 'fit',
        cls: 'panel-with-gray-background'
    }],

    /**
     * @return {Object}
     */
    getAttributesData: function () {
        var me = this;
        var attributes = [];
        this._fieldsetsreferences.forEach(function (fieldsetid) {
            var rows = me.lookup(fieldsetid).items;
            var attr;
            if (rows.length === 1) {
                attr = {
                    simple: rows.items[0].getRowData()
                };
            } else if (rows.length > 1) {
                attr = {
                    or: []
                };
                rows.items.forEach(function (row) {
                    attr.or.push({
                        simple: row.getRowData()
                    });
                });
            }
            if (attr) {
                attributes.push(attr);
            }
        });
        if (attributes.length === 1) {
            return attributes[0];
        } else if (attributes.length > 1) {
            return {
                and: attributes
            };
        }
    },

    privates: {
        _fieldsetsreferences: []
    }
});
