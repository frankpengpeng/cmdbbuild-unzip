Ext.define('CMDBuildUI.view.attachments.history.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.attachments.history.GridController',
        'CMDBuildUI.view.attachments.history.GridModel'
    ],

    alias: 'widget.attachments-history-grid',
    controller: 'attachments-history-grid',
    viewModel: {
        type: 'attachments-history-grid'
    },

    forceFit: true,
    loadMask: true,

    columns: [{
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
            hidden: false,
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                return Ext.util.Format.date(value, CMDBuildUI.locales.Locales.common.dates.datetime);
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.modificationdate'
            }
        }, {
            text: CMDBuildUI.locales.Locales.attachments.author,
            dataIndex: 'author',
            align: 'left',
            hidden: false,
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.author'
            }
        }, {
            text: CMDBuildUI.locales.Locales.attachments.version,
            dataIndex: 'version',
            align: 'left',
            hidden: false,
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.version'
            }
        },
        {
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
            text: CMDBuildUI.locales.Locales.attachments.category,
            dataIndex: 'category',
            align: 'left',
            hidden: true,
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.category'
            }
        }, {
            xtype: 'actioncolumn',
            minWidth: 30, // width property not works. Use minWidth.
            items: [{
                iconCls: 'attachments-grid-action x-fa fa-download',
                getTip: function () {
                    return CMDBuildUI.locales.Locales.attachments.download;
                },
                handler: function (grid, rowIndex, colIndex) {
                    var record = grid.getStore().getAt(rowIndex);
                    grid.fireEvent("actiondownload", grid, record, rowIndex, colIndex);
                }
            }]
        }
    ],

    bind: {
        store: '{attachmentshistory}'
    }

});