Ext.define('CMDBuildUI.view.administration.components.relationsfilters.PanelController', {
    extend: 'CMDBuildUI.view.filters.relations.PanelController',
    alias: 'controller.administration-filters-relations-panel',

    control: {
        '#domainsgrid': {
            // oneofselected: 'onOneOfSelected'
            rowclick: 'onDomainsGridRowClick'
        }
    },


    onDomainsGridRowClick: function (grid, record, element, rowIndex, e, eOpts) {
        var me = this;
        var container = me.lookup("oneofgridcontainer");
        if (record.get("mode") === CMDBuildUI.model.base.Filter.relationstypes.oneof) {
            var gridid = Ext.String.format("grid{0}{1}", record.get("domain"), record.get("direction"));
            var activeitem = me.lookup(gridid);
            if (!activeitem) {
                var firstload = true;
                CMDBuildUI.util.helper.ModelHelper.getModel(
                    record.get("destinationIsProcess") ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                    record.get("destination")
                ).then(function (model) {
                    // get columns from model
                    var columns = CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                        allowFilter: false
                    });

                    // add grid
                    container.add({
                        xtype: 'grid',
                        columns: columns,
                        forceFit: true,
                        loadMask: true,
                        itemId: gridid,
                        reference: gridid,
                        selModel: {
                            selType: 'checkboxmodel',
                            mode: 'SIMPLE'
                        },
                        store: {
                            type: record.get("destinationIsProcess") ? 'processes-instances' : 'classes-cards',
                            model: model.getName(),
                            autoLoad: true,
                            autoDestroy: true,
                            listeners: {
                                load: function (store, records) {
                                    var selected = [];
                                    if (record.get("cards") && !Ext.isEmpty(record.get("cards"))) {
                                        record.get("cards").forEach(function (s) {
                                            selected.push(store.getById(s.id));
                                        });
                                    }
                                    me.lookup(gridid).setSelection(selected);
                                }
                            }
                        },
                        listeners: {
                            selectionchange: function (g, selected, eOpts) {
                                var sel = [];
                                selected.forEach(function (s) {
                                    sel.push({
                                        className: s.get("_type"),
                                        id: s.getId()
                                    });
                                });
                                record.set("cards", sel);
                            }
                        }
                    });
                    container.setActiveItem(gridid);
                });
            } else {
                container.setActiveItem(gridid);
            }
            container.show();
        } else {
            container.hide();
        }
    },

    onOneOfSelected: function (grid, record) {
        var container = this.lookup("oneofgridcontainer");
        // container.show();
        container.add({
            xtype: 'grid',
            columns: [{
                text: 'aaaa'
            }, {
                text: 'vvvv'
            }]
        });
    }
});
