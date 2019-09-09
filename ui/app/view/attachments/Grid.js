Ext.define('CMDBuildUI.view.attachments.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.attachments.GridController',
        'CMDBuildUI.view.attachments.GridModel'
    ],

    alias: 'widget.attachments-grid',
    controller: 'attachments-grid',
    viewModel: {
        type: 'attachments-grid'
    },

    forceFit: true,
    loadMask: true,

    columns: [{
        text: CMDBuildUI.locales.Locales.attachments.filename,
        dataIndex: 'name',
        align: 'left',
        hidden: false,
        localized: {
            text: 'CMDBuildUI.locales.Locales.attachments.filename'
        }
    }, {
        text: CMDBuildUI.locales.Locales.attachments.description,
        dataIndex: 'description',
        align: 'left',
        hidden: false,
        localized: {
            text: 'CMDBuildUI.locales.Locales.attachments.description'
        }
    }, {
        text: CMDBuildUI.locales.Locales.attachments.version,
        dataIndex: 'version',
        align: 'left',
        hidden: false,
        localized: {
            text: 'CMDBuildUI.locales.Locales.attachments.version'
        }
    }, {
        text: CMDBuildUI.locales.Locales.attachments.creationdate,
        dataIndex: 'created',
        align: 'left',
        hidden: false,
        renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
            return Ext.util.Format.date(value, CMDBuildUI.locales.Locales.common.dates.datetime);
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.attachments.creationdate'
        }
    }, {
        text: CMDBuildUI.locales.Locales.attachments.modificationdate,
        dataIndex: 'modified',
        align: 'left',
        hidden: true,
        renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
            return Ext.util.Format.date(value, CMDBuildUI.locales.Locales.common.dates.datetime);
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.attachments.modificationdate'
        }
    }, {
        text: CMDBuildUI.locales.Locales.attachments.category,
        dataIndex: 'category',
        align: 'left',
        hidden: true,
        localized: {
            text: 'CMDBuildUI.locales.Locales.attachments.category'
        }
    }, {
        text: CMDBuildUI.locales.Locales.attachments.author,
        dataIndex: 'author',
        align: 'left',
        hidden: true,
        localized: {
            text: 'CMDBuildUI.locales.Locales.attachments.author'
        }
    }, {
        xtype: 'actioncolumn',
        minWidth: 104, // width property not works. Use minWidth.
        items: [{
            iconCls: 'attachments-grid-action x-fa fa-download',
            getTip: function () {
                return CMDBuildUI.locales.Locales.attachments.download;
            },
            isDisabled: function(view) {
                return view.lookupViewModel().get("disableActions.download");
            },
            handler: function (grid, rowIndex, colIndex) {
                var record = grid.getStore().getAt(rowIndex);
                grid.fireEvent("actiondownload", grid, record, rowIndex, colIndex);
            }
        }, {
            iconCls: 'attachments-grid-action x-fa fa-pencil',
            getTip: function () {
                return CMDBuildUI.locales.Locales.attachments.editattachment;
            },
            isDisabled: function(view) {
                return view.lookupViewModel().get("disableActions.edit");
            },
            handler: function (grid, rowIndex, colIndex) {
                var record = grid.getStore().getAt(rowIndex);
                grid.fireEvent("actionedit", grid, record, rowIndex, colIndex);
            }
        }, {
            iconCls: 'attachments-grid-action x-fa fa-trash',
            getTip: function () {
                return CMDBuildUI.locales.Locales.attachments.deleteattachment;
            },
            isDisabled: function(view) {
                return view.lookupViewModel().get("disableActions.delete");
            },
            handler: function (grid, rowIndex, colIndex) {
                var record = grid.getStore().getAt(rowIndex);
                grid.fireEvent("actiondelete", grid, record, rowIndex, colIndex);
            }
        }, {
            iconCls: 'attachments-grid-action x-fa fa-history',
            getTip: function () {
                return CMDBuildUI.locales.Locales.attachments.viewhistory;
            },
            isDisabled: function(view) {
                return view.lookupViewModel().get("disableActions.viewHistory");
            },
            handler: function (grid, rowIndex, colIndex) {
                var record = grid.getStore().getAt(rowIndex);
                grid.fireEvent("actionhistory", grid, record, rowIndex, colIndex);
            }
        }]
    }],

    features: [{
        ftype: 'grouping',
        groupHeaderTpl: '{name}',
        depthToIndent: 50
    }],

    bind: {
        store: '{attachments}'
    }
});