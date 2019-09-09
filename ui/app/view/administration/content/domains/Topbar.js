
Ext.define('CMDBuildUI.view.administration.content.domains.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.domains.TopbarController'
    ],

    alias: 'widget.administration-content-domains-topbar',
    controller: 'administration-content-domains-topbar',
    viewModel: {},

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.domains.toolbar.addBtn.text,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.toolbar.addBtn.text'
        },
        ui:'administration-action-small',
        reference: 'adddomain',
        itemId: 'adddomain',
        iconCls: 'x-fa fa-plus',
        autoEl: {
            'data-testid': 'administration-domain-toolbar-addDomainBtn'
        }
    // }, {
    //      /**
    //      * This button is actually not implemented
    //      */            
    //     xtype: 'button',
    //     text:  CMDBuildUI.locales.Locales.administration.lookuptypes.toolbar.printSchemaBtn.text,
    //     ui:'administration-action-small',
    //     reference: 'printdomain',
    //     itemId: 'printdomain',
    //     iconCls: 'x-fa fa-print',
    //     bind: {
    //         disabled: '{printButtonDisabled}',
    //         hidden: '{printButtonHidden}'
    //     },
    //     autoEl: {
    //         'data-testid': 'administration-domain-toolbar-printDomainBtn'
    //     }
    }, {
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText:  CMDBuildUI.locales.Locales.administration.domains.toolbar.searchTextInput.emptyText,
        localized: {
            emptyText:  'CMDBuildUI.locales.Locales.administration.domains.toolbar.searchTextInput.emptyText'
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
                    'data-testid': 'administration-domain-toolbar-form-search-trigger'
                }
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear',
                autoEl: {
                    'data-testid': 'administration-domain-toolbar-form-clear-trigger'
                }
            }
        },
        autoEl: {
            'data-testid': 'administration-domain-toolbar-search-form'
        }
    },{
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            html:  '{domainLabel}: <b data-testid="administration-domain-toolbar-domainName">{theDomain.name}</b>'
        }
    }]
});
