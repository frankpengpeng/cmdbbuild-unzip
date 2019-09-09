
Ext.define('CMDBuildUI.view.classes.cards.grid.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.classes.cards.grid.GridController',
        'CMDBuildUI.view.classes.cards.grid.GridModel',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget',
        'CMDBuildUI.view.classes.cards.card.View',
        'CMDBuildUI.util.helper.SessionHelper'
    ],

    mixins: [
        'CMDBuildUI.mixins.grids.Grid'
    ],

    alias: 'widget.classes-cards-grid-grid',
    controller: 'classes-cards-grid-grid',
    viewModel: {
        type: 'classes-cards-grid-grid'
    },

    config: {
        /**
         * @cfg {Boolean} maingrid
         * 
         * Set to true when the grid is added in main content.
         */
        maingrid: false,

        objectTypeName: null,
        allowFilter: true,
        showAddButton: true,

        /**
         * @cfg {Numeric} [selectedId]
         * Selected card id.
         */
        selectedId: null
    },
    
    publish: [
        'selectedId'
    ],
    
    //preserveScrollOnRefresh:true,
    bind: {
        store: '{cards}',
        selectedId: '{selectedId}',
        selection: '{selection}'
    },
    
    forceFit: true,
    loadMask: true,

    selModel : {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    viewConfig: {
        markDirty: false
    },

    plugins: [
        'gridfilters', {
            pluginId: 'forminrowwidget',
            ptype: 'forminrowwidget',
            id: 'forminrowwidget',
            expandOnDblClick: true,
            removeWidgetOnCollapse: true,
            widget: {
                xtype: 'classes-cards-card-view',
                viewModel: {} // do not remove otherwise the viewmodel will not be initialized
            }
        }
    ],
    autoEl: {
        'data-testid': 'cards-grid-grid'
    },

    /**
     * Method callend when selected id changes.
     */
    updateSelectedId: function (newvalue, oldvalue) {
        this.fireEvent("selectedidchanged", this, newvalue, oldvalue);
    },

    /**
     * Return true if the grid has been added in main container.
     * @return {Boolean}
     */
    isMainGrid: function () {
        return this.maingrid;
    }
});
