
Ext.define('CMDBuildUI.view.filters.relations.Panel', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.filters.relations.PanelController',
        'CMDBuildUI.view.filters.relations.PanelModel'
    ],

    alias: 'widget.filters-relations-panel',
    controller: 'filters-relations-panel',
    viewModel: {
        type: 'filters-relations-panel'
    },

    title: CMDBuildUI.locales.Locales.filters.relations,
    layout: 'border',

    items: [{
        xtype: 'panel',
        region: 'center',
        scrollable: true,
        flex: 1,
        items: [{
            xtype: "grid",
            forceFit: true,
            reference: 'domainsgrid',
            itemId: 'domainsgrid',
            sortableColumns: false,
            enableColumnHide: false,
            viewModel: {},
            columns: [{
                text: CMDBuildUI.locales.Locales.filters.description,
                dataIndex: 'description',
                align: 'left',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.filters.description'
                }
            }, {
                text: CMDBuildUI.locales.Locales.filters.type,
                dataIndex: 'destinationDescription',
                align: 'left',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.filters.type'
                }
            }, {
                text: 'CMDBuildUI.locales.Locales.filters.relations',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.filters.relations'
                },
                columns: [{
                    xtype: 'checkcolumn',
                    text: CMDBuildUI.locales.Locales.filters.noone,
                    dataIndex: 'noone',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.filters.noone'
                    },
                    listeners: {
                        checkchange: function (column, rowindex, ckecked, record) {
                            var mode;
                            if (ckecked) {
                                mode = CMDBuildUI.model.base.Filter.relationstypes.noone;
                            }
                            record.set("mode", mode);
                        }
                    }
                }, {
                    xtype: 'checkcolumn',
                    text: CMDBuildUI.locales.Locales.filters.any,
                    dataIndex: 'any',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.filters.any'
                    },
                    listeners: {
                        checkchange: function (column, rowindex, ckecked, record) {
                            var mode;
                            if (ckecked) {
                                mode = CMDBuildUI.model.base.Filter.relationstypes.any;
                            }
                            record.set("mode", mode);
                        }
                    }
                }, {
                    xtype: 'checkcolumn',
                    text: CMDBuildUI.locales.Locales.filters.fromselection,
                    dataIndex: 'oneof',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.filters.fromselection'
                    },
                    listeners: {
                        checkchange: function (column, rowindex, ckecked, record) {
                            var mode;
                            if (ckecked) {
                                mode = CMDBuildUI.model.base.Filter.relationstypes.oneof;
                            }
                            record.set("mode", mode);
                        }
                    }
                }]
            }],

            bind: {
                store: '{relations}'
            }
        }]
    }, {
        xtype: 'panel',
        layout: 'card',
        hidden: true,
        flex: 1,
        region: 'south',
        reference: 'oneofgridcontainer',
        scrollable: true,
        resizable: true
    }],

    localized: {
        title: 'CMDBuildUI.locales.Locales.filters.relations'
    },

    /**
     * @return {Object|Object[]}
     */
    getRelationsData: function () {
        var me = this;
        var vm = me.getViewModel();
        var results = [];
        var data = vm.get("relations") ? vm.get("relations").getRange() : [];
        data.forEach(function (d) {
            if (d.get("mode")) {
                var r = {
                    destination: d.get("destination"),
                    direction: d.get("direction"),
                    domain: d.get("domain"),
                    source: vm.get("objectTypeName"),
                    type: d.get("mode")
                };
                if (r.type === CMDBuildUI.model.base.Filter.relationstypes.oneof) {
                    r.cards = d.get("cards") || [];
                }
                results.push(r);
            }
        });
        return results;
    }
});
