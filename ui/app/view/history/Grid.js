Ext.define('CMDBuildUI.view.history.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.history.GridModel'
    ],

    alias: 'widget.history-grid',
    viewModel: {
        type: 'history-grid'
    },

    plugins: [{
        ptype: 'rowwidget',
        expandOnDblClick: true,
        widget: {
            xtype: 'history-item',
            viewModel: {} // do not remove otherwise the viewmodel will not be initialized
        }
    }],

    config: {
        className: null,
        allowFilter: false,
        showAddButton: false
    },

    forceFit: true,
    loadMask: true,
    sortableColumns: false,

    columns: [{
        text: CMDBuildUI.locales.Locales.history.begindate,
        dataIndex: '_beginDate',
        align: 'left',
        menuDisabled: true,
        renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
            return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
        },
        hidden: false,
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.begindate'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.enddate,
        dataIndex: '_endDate',
        align: 'left',
        menuDisabled: true,
        renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
            return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
        },
        hidden: false,
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.enddate'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.user,
        dataIndex: '_user',
        align: 'left',
        hidden: false,
        menuDisabled: true,
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.user'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.activityname,
        dataIndex: '_activity_description',
        align: 'left',
        hidden: true,
        menuDisabled: true,
        bind: {
            hidden: '{!isProcess}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.activityname'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.activityperformer,
        dataIndex: '_activity_performer',
        align: 'left',
        hidden: true,
        menuDisabled: true,
        bind: {
            hidden: '{!isProcess}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.activityperformer'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.processstatus,
        dataIndex: '_status_description',
        align: 'left',
        hidden: true,
        menuDisabled: true,
        bind: {
            hidden: '{!isProcess}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.processstatus'
        }
    }],

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    bind: {
        store: '{objects}'
    }
});
