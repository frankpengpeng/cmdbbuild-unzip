
Ext.define('CMDBuildUI.view.administration.content.classes.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.classes.TopbarController'
    ],

    alias: 'widget.administration-content-classes-topbar',
    controller: 'administration-content-classes-topbar',
   
    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.classes.toolbar.addClassBtn.text,
        localized: {
            text : 'CMDBuildUI.locales.Locales.administration.classes.toolbar.addClassBtn.text'
        },
        ui:'administration-action-small',
        reference: 'addclass',
        itemId: 'addclass',
        iconCls: 'x-fa fa-plus',
        autoEl: {
            'data-testid': 'administration-class-toolbar-addClassBtn'
        }
    }, {
        xtype: 'button',
        text:  CMDBuildUI.locales.Locales.administration.classes.toolbar.printSchemaBtn.text,
        localized: {
            text:  'CMDBuildUI.locales.Locales.administration.classes.toolbar.printSchemaBtn.text'
        },
        ui:'administration-action-small',
        reference: 'printschema',
        itemId: 'printschema',
        iconCls: 'x-fa fa-print',
        bind: {
            disabled: '{printButtonDisabled}',
            hidden: '{printButtonHidden}'
        },
        autoEl: {
            'data-testid': 'administration-class-toolbar-printSchemaBtn'
        }
    }, {
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText:  CMDBuildUI.locales.Locales.administration.classes.toolbar.searchTextInput.emptyText,
        localized: {
            emptyText:  'CMDBuildUI.locales.Locales.administration.classes.toolbar.searchTextInput.emptyText'
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
                    'data-testid': 'administration-class-toolbar-form-search-trigger'
                }
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear',
                autoEl: {
                    'data-testid': 'administration-class-toolbar-form-clear-trigger'
                }
            }
        },
        autoEl: {
            'data-testid': 'administration-class-toolbar-search-form'
        }
    },{
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            html:  '{classLabel}: <b data-testid="administration-class-toolbar-className">{theObject.name}</b>'
        }
    }]
});
