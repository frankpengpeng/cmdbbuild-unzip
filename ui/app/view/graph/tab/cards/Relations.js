
Ext.define('CMDBuildUI.view.graph.tab.cards.Relations', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.graph.tab.cards.RelationsController',
        'CMDBuildUI.view.graph.tab.cards.RelationsModel',
        'Ext.grid.feature.Grouping'
    ],
    alias: 'widget.graph-tab-cards-relations',
    controller: 'graph-tab-cards-relations',
    viewModel: {
        type: 'graph-tab-cards-relations'
    },
    layout: 'fit',
    disableSelection: true,
    ui: 'relationslist',
    columns: [{
        text: CMDBuildUI.locales.Locales.relationGraph.class,
        localized: {
            text: 'CMDBuildUI.locales.Locales.relationGraph.class'
        },
        dataIndex: 'destTypeDescription',
        align: 'left',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.relations.code,
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.code'
        },
        dataIndex: '_destinationCode',
        align: 'left',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.relations.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.description'
        },
        dataIndex: '_destinationDescription',
        align: 'left',
        flex: 1
    }],

    bind: {
        store: '{edgesRelationStore}'
    },

    initComponent: function () {
        /**
         * Get group header template
         */
        var vm = this.getViewModel();
        var domains = Ext.getStore('domains.Domains');

        var headerTpl = Ext.create('Ext.XTemplate',
            '<div>{name:this.formatName} ({rows:this.getTotalRows}) {name:this.compoundName}</div>', {
                formatName: function (name) {
                    var selectedType;
                    try {
                        selectedType = vm.get('selectedNode')[0].type;
                    } catch (err) {
                        selectedType = null;
                    }
                    if (selectedType.includes('compound') || name.includes('compound_')) {
                        selectedType = selectedType.replace('compound_', '');
                        name = name.replace('compound_', '');
                    }
                    var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(selectedType);
                    var objectHierarchy = object.getHierarchy();
                    var domain = domains.getById(name);
                    if (domain && Ext.Array.contains(objectHierarchy, domain.get("source"))) {
                        return domain.getTranslatedDescriptionDirect();
                    } else if (domain) {
                        return domain.getTranslatedDescriptionInverse();
                    }
                },
                getTotalRows: function (rows) {
                    return rows[0].nodes().getRange().length || rows.length;
                },
                compoundName: function (name) {
                    if (name.includes('compound_')) {
                        return 'Compound';
                    }
                    return;
                }
            });
        Ext.apply(this, {
            features: [{
                ftype: 'grouping',
                // groupHeaderTpl: '{name}',
                groupHeaderTpl: headerTpl,
                depthToIndent: 50,
                enableGroupingMenu: false,
                enableNoGroups: false
            }]
        });

        this.callParent(arguments);
    }
});
