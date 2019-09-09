Ext.define('CMDBuildUI.view.classes.cards.grid.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.classes.cards.grid.ContainerController',
        'CMDBuildUI.view.classes.cards.grid.ContainerModel'
    ],

    mixins: [
        'CMDBuildUI.mixins.grids.ContextMenuMixin',
        'CMDBuildUI.mixins.grids.AddButtonMixin'
    ],

    alias: 'widget.classes-cards-grid-container',
    controller: 'classes-cards-grid-container',
    viewModel: {
        type: 'classes-cards-grid-container'
    },

    config: {
        /**
         * @cfg {Boolean} maingrid
         * 
         * Set to true when the grid is added in main content.
         */
        maingrid: false,

        /**
         * @cfg {String} objectTypeName
         * Class name.
         */
        objectTypeName: null,

        /**
         * @cfg {Object} filter
         * Advanced filter definition.
         */
        filter: null
    },

    autoEl: {
        'data-testid': 'cards-card-view-container'
    },

    layout: 'fit',
    typeicon: CMDBuildUI.model.menu.MenuItem.icons.klass,

    bind: {
        title: '{title}'
    },

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.classes.cards.addcard,
        reference: 'addcard',
        itemId: 'addcard',
        iconCls: 'x-fa fa-plus',
        ui: 'management-action',
        autoEl: {
            'data-testid': 'classes-cards-grid-container-addbtn'
        },
        bind: {
            text: '{addbtn.text}',
            hidden: '{btnHide}'
        }
    }, {
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText: CMDBuildUI.locales.Locales.common.actions.searchtext,
        reference: 'searchtext',
        itemId: 'searchtext',
        cls: 'management-input',
        autoEl: {
            'data-testid': 'classes-cards-grid-container-searchtext'
        },
        bind: {
            value: '{search.value}'
        },
        listeners: {
            specialkey: 'onSearchSpecialKey'
        },
        triggers: {
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                handler: 'onSearchSubmit'
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear'
            }
        },
        localized: {
            emptyText: "CMDBuildUI.locales.Locales.common.actions.searchtext"
        }
    }, {
        xtype: 'filters-launcher',
        storeName: 'cards',
        reference: 'filterslauncher'
    }, {
        xtype: 'button',
        itemId: 'refreshBtn',
        reference: 'refreshBtn',
        iconCls: 'x-fa fa-refresh',
        ui: 'management-action',
        tooltip: CMDBuildUI.locales.Locales.common.actions.refresh,
        autoEl: {
            'data-testid': 'classes-cards-grid-container-refreshbtn'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.common.actions.refresh'
        }
    }, {
        xtype: 'thematisms-launcher',
        bind: {
            hidden: '{!btnHide}'
        }
    }, {
        xtype: 'button',
        itemId: 'contextMenuBtn',
        reference: 'contextMenuBtn',
        iconCls: 'x-fa fa-bars',
        ui: 'management-action',
        tooltip: CMDBuildUI.locales.Locales.common.grid.opencontextualmenu,
        arrowVisible: false,
        autoEl: {
            'data-testid': 'classes-cards-grid-container-contextmenubtn'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.common.grid.opencontextualmenu'
        },
        bind: {
            hidden: '{btnHide}'
        }
    }, {
        xtype: 'button',
        reference: 'showMapListButton',
        itemId: 'showMapListButton',
        iconCls: 'x-fa fa-globe',
        hidden: true,
        ui: 'management-action',
        bind: {
            text: '{btnMapText}',
            iconCls: '{btnIconCls}',
            hidden: '{btnMapHidden}'
        },
        listeners: {
            click: 'onShowMapListButtonClick'
        },
        autoEl: {
            'data-testid': 'classes-cards-grid-container-togglemapbtn'
        }
    }, CMDBuildUI.util.helper.GridHelper.getPrintButtonConfig({
        bind: {
            hidden: '{btnHide}'
        }
    }), {
        xtype: 'tbfill'
    }, CMDBuildUI.util.helper.GridHelper.getBufferedGridCounterConfig("cards")],

    /**
     * Return true if the grid has been added in main container.
     * @return {Boolean}
     */
    isMainGrid: function () {
        return this.maingrid;
    },

    /**
     * Returns the grid on which apply context menu actions.
     * 
     * @override
     * @return {Ext.gid.Panel}
     */
    getContextMenuGrid: function () {
        return this.lookupReference(this.referenceGridId);
    },

    privates: {
        /**
         * @property referenceGridId
         */
        referenceGridId: 'classes-cards-grid-grid-view',

        /**
         * @property referenceMapId
         */
        referenceMapId: 'map-container-view'
    }
});