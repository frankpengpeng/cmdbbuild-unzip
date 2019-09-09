Ext.define('CMDBuildUI.view.administration.content.localizations.localization.tabitems.TranslationsGrid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.localizations.localization.tabitems.TranslationsGridController',
        'CMDBuildUI.view.administration.content.localizations.localization.tabitems.TranslationsGridModel',
        'Ext.grid.filters.Filters'
    ],

    alias: 'widget.administration-content-localizations-localization-tabitems-translationsgrid',
    controller: 'administration-content-localizations-localization-tabitems-translationsgrid',
    viewModel: {
        type: 'administration-content-localizations-localization-tabitems-translationsgrid'
    },

    viewConfig: {
        markDirty: false
    },
    config: {
        allowFilter: true
    },

    //layout: 'hbox',
    forceFit: true,

    bind: {
        store: '{completeTranslationsStore}'
    },
    plugins: {
        pluginId: 'cellediting',
        ptype: 'cellediting',
        clicksToEdit: 1,
        listeners: {
            edit: 'editedCell'
        }

    },

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
                xtype: 'component',
                flex: 1
            },
            {
                text: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.saveBtn,
                ui: 'administration-action-small',
                listeners: {
                    click: 'onSaveBtnClick'
                }
            },
            {
                text: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.cancelBtn,
                ui: 'administration-secondary-action-small',
                listeners: {
                    click: 'onCancelBtnClick'
                }
            }
        ]
    }],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        this.callParent(arguments);
    }


});