Ext.define('CMDBuildUI.view.relations.list.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.relations.list.GridController',
        'CMDBuildUI.view.relations.list.GridModel'
    ],

    alias: 'widget.relations-list-grid',
    controller: 'relations-list-grid',
    reference: 'relations-list-grid',
    viewModel: {
        type: 'relations-list-grid'
    },

    ui: 'relationslist',

    forceFit: true,
    loadMask: true,

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    columns: [{
        text: CMDBuildUI.locales.Locales.relations.type,
        dataIndex: '_destinationType',
        align: 'left',
        renderer: function (value) {
            if (value) {
                return CMDBuildUI.util.helper.ModelHelper.getObjectDescription(value);
            }
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.type'
        }
    }, {
        text: CMDBuildUI.locales.Locales.relations.code,
        dataIndex: '_destinationCode',
        align: 'left',
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.code'
        }
    }, {
        text: CMDBuildUI.locales.Locales.relations.description,
        dataIndex: '_destinationDescription',
        align: 'left',
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.description'
        }
    }, {
        text: CMDBuildUI.locales.Locales.relations.attributes,
        dataIndex: '_relationAttributes',
        align: 'left',
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.attributes'
        }
    }, {
        xtype: 'actioncolumn',
        minWidth: 104, // width property not works. Use minWidth.
        items: [{
            iconCls: 'relations-grid-action x-fa fa-external-link',
            tooltip: CMDBuildUI.locales.Locales.relations.opencard,
            handler: function (grid, rowIndex, colIndex) {
                var record = grid.getStore().getAt(rowIndex);
                grid.fireEvent("actionopencard", grid, record, rowIndex, colIndex);
            },
            localized: {
                toolitp: 'CMDBuildUI.locales.Locales.relations.opencard'
            }
        }, {
            iconCls: 'relations-grid-action x-fa fa-pencil',
            tooltip: CMDBuildUI.locales.Locales.relations.editrelation,
            handler: function (grid, rowIndex, colIndex) {
                var record = grid.getStore().getAt(rowIndex);
                grid.fireEvent("actioneditrelation", grid, record, rowIndex, colIndex);
            },
            isDisabled: function (view, rowIndex, colIndex, item, record) {
                return !record.get('_can_update') || record.get("_destinationIsProcess");
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.relations.editrelation'
            }
        }, {
            iconCls: 'relations-grid-action x-fa fa-trash',
            tooltip: CMDBuildUI.locales.Locales.relations.deleterelation,
            handler: function (grid, rowIndex, colIndex) {
                var record = grid.getStore().getAt(rowIndex);
                grid.fireEvent("actiondeleterelation", grid, record, rowIndex, colIndex);
            },
            isDisabled: function (view, rowIndex, colIndex, item, record) {
                return !record.get('_can_delete') || record.get("_destinationIsProcess");
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.relations.deleterelation'
            }
        }, {
            iconCls: 'relations-grid-action x-fa fa-pencil-square-o',
            tooltip: CMDBuildUI.locales.Locales.relations.editcard,
            handler: function (grid, rowIndex, colIndex) {
                var record = grid.getStore().getAt(rowIndex);
                grid.fireEvent("actioneditcard", grid, record, rowIndex, colIndex);
            },
            isDisabled: function (view, rowIndex, colIndex, item, record) {
                var titem = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(record.get("_destinationType"));
                return !(view.ownerGrid.getViewModel().get("basepermissions.edit") && titem.get(CMDBuildUI.model.base.Base.permissions.edit)) || record.get("_destinationIsProcess");
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.relations.editcard'
            }
        }]
    }],

    bind: {
        store: '{allRelations}'
    },

    initComponent: function () {
        /**
         * Get group header template
         */
        var vm = this.getViewModel();
        var domains = vm.get("domains");
        var objectTypeName = vm.get("objectTypeName");
        var headerTpl = Ext.create('Ext.XTemplate',
            '<div>{children:this.formatName} ({rows:this.getTotalRows})</div>', {
                formatName: function(children) {
                    if (children.length) {
                        var child = children[0];
                        var domain = domains.getById(child.get("_type"));
                        if (domain) {
                            return child.get("_is_direct") ? domain.getTranslatedDescriptionDirect() : domain.getTranslatedDescriptionInverse();
                        }
                    }
                },  
                getTotalRows: function (rows) {
                    return rows.length;
                }
            });
        Ext.apply(this, {
            features: [{
                ftype: 'customgrouping',
                // groupHeaderTpl: '{name}',
                groupHeaderTpl: headerTpl,
                depthToIndent: 50,
                enableGroupingMenu: false,
                enableNoGroups: false,
                startCollapsed: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.relationlimit)
            }]
        });

        this.callParent(arguments);
    }
});