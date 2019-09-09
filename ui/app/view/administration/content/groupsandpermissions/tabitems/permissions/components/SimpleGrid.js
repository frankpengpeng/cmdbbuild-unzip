Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.SimpleGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
    viewModel: {},

    width: '100%',
    layout: 'fit',

    viewConfig: {
        markDirty: false
    },

    sealedColumns: false,
    sortableColumns: true,
    enableColumnHide: false,
    enableColumnMove: false,
    enableColumnResize: false,
    menuDisabled: true,

    columns: [{
        flex: 9,
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.description'
        },
        dataIndex: '_object_description',
        align: 'left',
        renderer: function (value, metaData, record, rowIndex, colIndex, store) {
            if (!value.length) {
                return record.get('objectTypeName');
            }
            return value;
        }
    }, {
        flex: 1,
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none'
        },
        dataIndex: 'modeTypeNone',
        align: 'center',
        xtype: 'checkcolumn',
        injectCheckbox: false,
        headerCheckbox: false,
        disabled: true,
        hideable: false,
        hidden: true,
        sortable: true,
        menuDisabled: true,
        disabledCls: '', // or don't add this config if you want the field to look disabled,
        bind: {
            hidden: '{hiddenColumns.modeTypeNone}',
            disabled: '{!actions.edit}',
            headerCheckbox: '{actions.edit}'
        },
        listeners: {
            headercheckchange: function (columnHeader, checked, e, eOpts) {
                this.getView().getStore().each(function (record) {
                    record.changeMode('-');
                });
            },
            checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                record.changeMode('-');
            }
        }
    }, {
        flex: 1,
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.allow,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.allow'
        },
        dataIndex: 'modeTypeAllow',
        align: 'center',
        xtype: 'checkcolumn',
        injectCheckbox: false,
        headerCheckbox: false,
        disabled: true,
        hideable: false,
        hidden: true,
        sortable: true,
        menuDisabled: true,
        disabledCls: '', // or don't add this config if you want the field to look disabled,
        bind: {
            hidden: '{hiddenColumns.modeTypeAllow}',
            disabled: '{!actions.edit}',
            headerCheckbox: '{actions.edit}'
        },
        listeners: {
            headercheckchange: function (columnHeader, checked, e, eOpts) {
                this.getView().getStore().each(function (record) {
                    record.changeMode('r');
                });
            },
            checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                record.changeMode('r');
            }
        }
    }, {
        flex: 1,
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read'
        },
        dataIndex: 'modeTypeRead',
        align: 'center',
        xtype: 'checkcolumn',
        headerCheckbox: false,
        disabled: true,
        hideable: false,
        hidden: true,
        menuDisabled: true,
        sortable: true,
        disabledCls: '', // or don't add this config if you want the field to look disabled
        bind: {
            hidden: '{hiddenColumns.modeTypeRead}',
            disabled: '{!actions.edit}',
            headerCheckbox: '{actions.edit}'
        },
        listeners: {
            headercheckchange: function (columnHeader, checked, e, eOpts) {
                this.getView().getStore().each(function (record) {
                    record.changeMode('r');
                });
            },
            checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                record.changeMode('r');
            }
        }
    }, {
        flex: 1,
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default'
        },
        dataIndex: 'modeTypeDefault',
        align: 'center',
        xtype: 'checkcolumn',
        headerCheckbox: false,
        disabled: true,
        hideable: false,
        hidden: true,
        menuDisabled: true,
        sortable: true,
        disabledCls: '', // or don't add this config if you want the field to look disabled
        bind: {
            hidden: '{hiddenColumns.modeTypeDefault}',
            disabled: '{!actions.edit}',
            headerCheckbox: '{actions.edit}'
        },
        listeners: {
            headercheckchange: function (columnHeader, checked, e, eOpts) {
                this.getView().getStore().each(function (record) {
                    record.changeMode('w');
                });
            },
            checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                record.changeMode('w');
            }
        }
    }, {
        flex: 1,
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.defaultread,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.defaultread'
        },
        dataIndex: 'modeTypeDefaultRead',
        align: 'center',
        xtype: 'checkcolumn',
        headerCheckbox: false,
        disabled: true,
        hideable: false,
        hidden: true,
        menuDisabled: true,
        sortable: true,
        disabledCls: '', // or don't add this config if you want the field to look disabled
        bind: {
            hidden: '{hiddenColumns.modeTypeDefaultRead}',
            disabled: '{!actions.edit}',
            headerCheckbox: '{actions.edit}'
        },
        listeners: {
            headercheckchange: function (columnHeader, checked, e, eOpts) {
                this.getView().getStore().each(function (record) {
                    record.changeMode('r');
                });
            },
            checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                record.changeMode('r');
            }
        }
    }, {
        flex: 1,
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.write,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.write'
        },
        dataIndex: 'modeTypeWrite',
        align: 'center',
        xtype: 'checkcolumn',
        headerCheckbox: false,
        disabled: true,
        hideable: false,
        hidden: true,
        menuDisabled: true,
        sortable: true,
        disabledCls: '', // or don't add this config if you want the field to look disabled
        bind: {
            hidden: '{hiddenColumns.modeTypeWrite}',
            disabled: '{!actions.edit}',
            headerCheckbox: '{actions.edit}'
        },
        listeners: {
            headercheckchange: function (columnHeader, checked, e, eOpts) {
                this.getView().getStore().each(function (record) {
                    record.changeMode('w');
                });
            },
            checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                record.changeMode('w');
            }
        }
    }, {
        xtype: 'actioncolumn',
        minWidth: 80,
        maxWidth: 80,
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.filters,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.filters'
        },
        hideable: false,
        disabled: true,
        hidden: true,
        border: 0,
        align: 'center',
        bind: {
            hidden: '{hiddenColumns.actionFilter}'
        },
        items: [{
            viewModel: {},
            bind: {
                disabled: '{!actions.edit}'
            },
            iconCls: 'cmdbuildicon-filter',
            tooltip: CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters'
            },
            getClass: function (v, meta, row, rowIndex, colIndex, store) {
                return 'cmdbuildicon-filter margin-right5';
            },
            handler: 'onActionFiltersClick',
            autoEl: {
                'data-testid': 'administration-permissions-grid-row-filter'
            },
            isDisabled: function (view, rowIndex, colIndex, item, record) {        
                if (view.up().getViewModel().get('actions.view') && (!record.get('filter') && Ext.Object.isEmpty(record.get('attributePrivileges')))) {
                    return true;
                }
                return false;
            }
        }, {
            viewModel: {},
            iconCls: 'cmdbuildicon-filter-remove',
            tooltip: CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.removefilters,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.removefilters'
            },
            isDisabled: function (view, rowIndex, colIndex, item, record) {
                if (view.up().getViewModel().get('actions.view') || (!record.get('filter') && Ext.Object.isEmpty(record.get('attributePrivileges')))) {
                    return true;
                }
                return false;
            },
            getClass: function (v, meta, row, rowIndex, colIndex, store) {
                return 'cmdbuildicon-filter-remove margin-right5';
            },
            handler: 'onRemoveFilterActionClick',
            autoEl: {
                'data-testid': 'administration-permissions-grid-row-filter'
            }
        }]

    }, {
        xtype: 'actioncolumn',
        minWidth: 80,
        maxWidth: 80,
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.actions,
        localized:{
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.actions'
        },
        hideable: false,
        disabled: true,
        hidden: true,
        border: 0,
        align: 'center',
        bind: {
            hidden: '{hiddenColumns.actionActionDisabled}'
        },
        items: [{
            viewModel: {},
            bind: {
                disabled: '{!actions.edit}'
            },
            iconCls: 'cmdbuildicon-list',
            tooltip: CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.disabledactions,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.disabledactions'
            },
            getClass: function (v, meta, row, rowIndex, colIndex, store) {
                return 'cmdbuildicon-list margin-right5';
            },
            handler: 'onDisabledActionClick',
            autoEl: {
                'data-testid': 'administration-permissions-grid-row-list'
            },
            isDisabled: function (view, rowIndex, colIndex, button, record) {
                var fields = ['_card_create_disabled', '_card_update_disabled', '_card_delete_disabled', '_card_clone_disabled', '_card_realtion_disabled', '_card_print_disabled'];
                var disabled = true;
                Ext.Array.forEach(fields, function (field) {
                    if (record.get(field)) {
                        disabled = false;
                    }
                });
                return !view.up().getViewModel().get('actions.edit') && disabled;
            }

        }, {
            viewModel: {},
            bind: {
                disabled: '{!actions.edit}'
            },
            iconCls: 'cmdbuildicon-list-alt-remove',
            tooltip: CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.removedisabledactions,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.removedisabledactions'
            },
            getClass: function (v, meta, row, rowIndex, colIndex, store) {
                return 'cmdbuildicon-list-alt-remove margin-left5';
            },
            isDisabled: function (view, rowIndex, colIndex, button, record) {
                var fields = ['_card_create_disabled', '_card_update_disabled', '_card_delete_disabled', '_card_clone_disabled', '_card_realtion_disabled', '_card_print_disabled'];
                var disabled = true;
                Ext.Array.forEach(fields, function (field) {
                    if (view.up().getViewModel().get('actions.edit') && record.get(field)) {
                        disabled = false;
                    }
                });
                return disabled;
            },
            handler: 'onRemoveDisabledActionClick',
            autoEl: {
                'data-testid': 'administration-permissions-grid-row-list'
            }
        }]

    }]
});