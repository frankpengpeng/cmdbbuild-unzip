
Ext.define('CMDBuildUI.view.classes.cards.relations.Panel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.classes.cards.relations.PanelController',
        'CMDBuildUI.view.classes.cards.relations.PanelModel'
    ],

    alias: 'widget.classes-cards-relations-panel',
    controller: 'classes-cards-relations-panel',
    viewModel: {
        type: 'classes-cards-relations-panel'
    },
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
                text: CMDBuildUI.locales.Locales.filters.domain,
                dataIndex: 'description',
                align: 'left',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.filters.domain'
                }
            },
            {
                text: CMDBuildUI.locales.Locales.filters.actions,
                localized: {
                    text: CMDBuildUI.locales.Locales.filters.actions
                },
                columns: [{
                    xtype: 'checkcolumn',
                    text: CMDBuildUI.locales.Locales.filters.ignore,
                    dataIndex: 'ignore',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.filters.ignore'
                    },
                    listeners: {
                        checkchange: function (column, rowindex, ckecked, record) {
                            var mode;
                            if (ckecked) {
                                mode = CMDBuildUI.model.base.Filter.cloneFilters.ignore;
                            }
                            record.set("mode", mode);
                        }
                    }
                }, {
                    xtype: 'checkcolumn',
                    text: CMDBuildUI.locales.Locales.filters.migrate,
                    dataIndex: 'migrates',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.filters.migrate'
                    },
                    listeners: {
                        checkchange: function (column, rowindex, ckecked, record) {
                            var mode;
                            if (ckecked) {
                                mode = CMDBuildUI.model.base.Filter.cloneFilters.migrates;
                            }
                            record.set("mode", mode);
                        }
                    }
                }, {
                    xtype: 'actioncolumn',
                    text: CMDBuildUI.locales.Locales.filters.clone,
                    dataIndex: 'clone',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.filters.clone'
                    },
                    items: [{
                        isDisabled: function (view, rowindex, colinndex, item, record) {
                            this.disabled = record.get('isDisabled');
                        },
                        listeners: {
                            checkchange: function (column, rowindex, ckecked, record) {
                                var mode;
                                if (ckecked) {
                                    mode = CMDBuildUI.model.base.Filter.cloneFilters.clone;
                                }
                                record.set("mode", mode);
                            }
                        }
                    }]
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
    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        formBind: true, //only enabled once the form is valid
        //disabled: true,
        reference: 'savebtn',
        itemId: 'savebtn',
        ui: 'management-action-small',
        autoEl: {
            'data-testid': 'card-create-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        reference: 'cancelbtn',
        itemId: 'cancelbtn',
        ui: 'secondary-action-small',
        autoEl: {
            'data-testid': 'selection-popup-card-create-cancel'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }],
    localized: {
        title: 'CMDBuildUI.locales.Locales.filters.relations'
    }

});
