
Ext.define('CMDBuildUI.view.administration.content.lookuptypes.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.lookuptypes.TopbarController',
        'CMDBuildUI.view.administration.content.lookuptypes.TopbarModel'
    ],

    alias: 'widget.administration-content-lookuptypes-topbar',
    controller: 'administration-content-lookuptypes-topbar',
    viewModel: {
        type: 'administration-content-lookuptypes-topbar'
    },
    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.lookuptypes.toolbar.addClassBtn.text,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.lookuptypes.toolbar.addClassBtn.text'
        },
        ui:'administration-action-small',
        reference: 'addlookuptype',
        itemId: 'addlookuptype',
        iconCls: 'x-fa fa-plus',
        autoEl: {
            'data-testid': 'administration-class-toolbar-addLookupTypeBtn'
        }
    }, {
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText:  CMDBuildUI.locales.Locales.administration.lookuptypes.toolbar.searchTextInput.emptyText,
        localized: {
            emptyText:  'CMDBuildUI.locales.Locales.administration.lookuptypes.toolbar.searchTextInput.emptyText'
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
                    'data-testid': 'administration-lookuptypes-toolbar-form-search-trigger'
                }
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear',
                autoEl: {
                    'data-testid': 'administration-lookuptypes-toolbar-form-clear-trigger'
                }
            }
        },
        autoEl: {
            'data-testid': 'administration-lookuptypes-toolbar-search-form'
        }
    },{
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            html: '{lookupLabel}: <b data-testid="administration-lookuptypes-toolbar-className">{theLookupType.name}</b>'
        }
    }]
});
