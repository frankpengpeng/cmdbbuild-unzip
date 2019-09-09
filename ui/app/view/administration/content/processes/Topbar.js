
Ext.define('CMDBuildUI.view.administration.content.processes.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.processes.TopbarController'
    ],

    alias: 'widget.administration-content-processes-topbar',
    controller: 'administration-content-processes-topbar',

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.processes.toolbar.addProcessBtn.text,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.processes.toolbar.addProcessBtn.text'
        },
        ui: 'administration-action-small',
        reference: 'addprocess',
        itemId: 'addprocess',
        iconCls: 'x-fa fa-plus',
        autoEl: {
            'data-testid': 'administration-process-toolbar-addProcessBtn'
        }
    }, {
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.processes.toolbar.printSchemaBtn.text,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.processes.toolbar.printSchemaBtn.text'
        },
        ui: 'administration-action-small',
        reference: 'printschema',
        itemId: 'printschema',
        iconCls: 'x-fa fa-print',
        bind: {
            disabled: '{printButtonDisabled}',
            hidden: '{printButtonHidden}'
        },
        autoEl: {
            'data-testid': 'administration-process-toolbar-printSchemaBtn'
        }
    }, {
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText: CMDBuildUI.locales.Locales.administration.processes.toolbar.searchTextInput.emptyText,
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.administration.processes.toolbar.searchTextInput.emptyText'
        },
        cls: 'administration-input',
        reference: 'searchtext',
        itemId: 'searchtext',
        bind: {
            value: '{search.value}',
            hidden: '{!canFilter}'
        },
        listeners: {
            specialkey: 'onSearchSpecialKey'
        },
        triggers: {
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                handler: 'onSearchSubmit',
                autoEl: {
                    'data-testid': 'administration-process-toolbar-form-search-trigger'
                }
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear',
                autoEl: {
                    'data-testid': 'administration-process-toolbar-form-clear-trigger'
                }
            }
        },
        autoEl: {
            'data-testid': 'administration-process-toolbar-search-form'
        }
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            html: '{precessLabel}: <b data-testid="administration-process-toolbar-processName">{theProcess.name}</b>'
        }
    }]
});
